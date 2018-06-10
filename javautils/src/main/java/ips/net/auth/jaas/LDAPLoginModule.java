package ips.net.auth.jaas;

import java.security.Principal;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.FailedLoginException;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * Authenticates to LDAP server and stores some inetOrgPerson (RFC 2798)
 * attributes to the user principal. Role principals are also added.
 * Configuration options: connectionURL: LDAP connection URL baseDN:
 * Distinguished name of base node accountsDN: DN part where accounts can be
 * found rolesDN: DN part where roles (groupOfNames) can be found
 * 
 * example JAAS config file:
 * 
 * JAASLDAPLoginTest { ips.net.auth.jaas.LDAPLoginModule Sufficient
 * connectionURL="ldaps://ldap.example.org:636" baseDN="dc=example,dc=org"
 * accountsDN="ou=People" rolesDN="ou=roles,ou=webapp" debug=true; };
 * 
 * @author klausj
 * 
 */
public class LDAPLoginModule implements LoginModule {
	private boolean debug = false;

	// initial state
	private Subject subject;
	private CallbackHandler callbackHandler;
	private Map sharedState;
	private Map options;

	// configurable option

	// the authentication status
	private boolean succeeded = false;
	private boolean commitSucceeded = false;

	// username and password
	private String username;
	private char[] password;

	private String baseDN;
	private String userDn;

	// principals for user and role
	private InetOrgPersonPrincipal userPrincipal;
	private RolePrincipal rolePrincipal;

	private DirContext context;

	public void initialize(Subject subject, CallbackHandler callbackHandler,
			Map sharedState, Map options) {

		this.subject = subject;
		this.callbackHandler = callbackHandler;
		this.sharedState = sharedState;
		this.options = options;

		// initialize any configured options
		debug = "true".equalsIgnoreCase((String) options.get("debug"));
		if (debug) {
			System.out.println(getClass().getName() + " Initialized");
		}
	}

	public boolean login() throws LoginException {

		// prompt for a user name and password
		if (callbackHandler == null) {
			throw new LoginException("No CallbackHandler available.");
		}

		Callback[] callbacks = new Callback[2];
		callbacks[0] = new NameCallback("Login: ");
		callbacks[1] = new PasswordCallback("Password: ", false);

		try {
			callbackHandler.handle(callbacks);
			username = ((NameCallback) callbacks[0]).getName();
			char[] tmpPassword = ((PasswordCallback) callbacks[1])
					.getPassword();
			if (tmpPassword == null) {
				// treat a NULL password as an empty password
				tmpPassword = new char[0];
			}
			password = new char[tmpPassword.length];
			System.arraycopy(tmpPassword, 0, password, 0, tmpPassword.length);
			((PasswordCallback) callbacks[1]).clearPassword();

		} catch (java.io.IOException ioe) {
			throw new LoginException(ioe.toString());
		} catch (UnsupportedCallbackException uce) {
			throw new LoginException("Error: Unsupported callback:"
					+ uce.getCallback());
		}

		// print debugging information
		if (debug) {
			System.out.println("[LDAPLoginModule] "
					+ "user entered user name: " + username);
			System.out
					.println("[LDAPLoginModule] " + "user entered a password");

		}
		baseDN = ((String) options.get("baseDN")).trim();
		String accountsDN = ((String) options.get("accountsDN")).trim();
		StringBuffer userDNBuffer = new StringBuffer("uid=");
		userDNBuffer.append(username);
		userDNBuffer.append(',');
		if (debug) {
			System.out.println("[LDAPLoginModule] accountsDN: " + accountsDN);
		}
		if (accountsDN != null && !"".equals(accountsDN)) {
			userDNBuffer.append(accountsDN);
			if (!accountsDN.endsWith(",")) {
				userDNBuffer.append(',');
			}
		}
		userDNBuffer.append(baseDN);

		// verify the username/password
		userDn = userDNBuffer.toString();

		String connectionURL = (String) options.get("connectionURL");
		if (debug) {
			System.out.println("[LDAPLoginModule] try to connect to "
					+ connectionURL + " as user " + userDn);
		}
		Hashtable<Object, Object> env = new Hashtable<Object, Object>();
		// Provider (Implementierung setzen)
		env.put(Context.PROVIDER_URL, connectionURL);
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.SECURITY_PRINCIPAL, userDn);
		env.put(Context.SECURITY_CREDENTIALS, password);
		// Kontext setzen (Directory-Kontext)
		try {
			context = new InitialDirContext(env);
			// if we could bind to LDAP user is successfully authenticated
			if (debug) {
				System.out
						.println("[LDAPLoginModule] successfully bound to LDAP.");
			}
			succeeded = true;
			return succeeded;
		} catch (NamingException e) {
			if (debug) {
				System.out.println("[LDAPLoginModule] Could not bind to LDAP.");
			}
			throw new FailedLoginException("Could not bind to LDAP");
		}

	}

	public boolean commit() throws LoginException {
		if (succeeded == false) {
			return false;
		} else {

			Set<Principal> principalSet = subject.getPrincipals();
			try {
				Attributes attrs = context.getAttributes(userDn);
				Attribute inetOrgPersonClassAttr = attrs.get("objectClass");
				NamingEnumeration<?> objClassesEnum = inetOrgPersonClassAttr
						.getAll();
				while (objClassesEnum.hasMore()) {
					Object objClassObj = objClassesEnum.next();

					if ("inetOrgPerson".equals(objClassObj)) {
						userPrincipal = new InetOrgPersonPrincipal(userDn,
								attrs);
						if (!principalSet.contains(userPrincipal)) {
							principalSet.add(userPrincipal);
							if (debug) {
								System.out
										.println("[LDAPLoginModule] added InetOrgPrincipal "
												+ userPrincipal.getName()
												+ " to Subject");
							}
						}
						if (debug) {
							System.out.println("Displayname: "
									+ userPrincipal.getAttrDisplayname());
							System.out.println("Given name: "
									+ userPrincipal.getAttrGivenname());
							System.out.println("Surname: "
									+ userPrincipal.getAttrSurname());
							System.out.println("E-Mail: "
									+ userPrincipal.getAttrMail());
						}
					}
					// TODO accept posixAccounts?
				}

			} catch (NamingException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				// no principals added
			}

			StringBuffer rolesNodeDNBuffer = new StringBuffer("");
			String rolesDN = ((String) options.get("rolesDN")).trim();

			if (debug) {
				System.out.println("[LDAPLoginModule] rolesDN option: "
						+ rolesDN);
			}
			if (rolesDN != null && !"".equals(rolesDN)) {
				rolesNodeDNBuffer.append(rolesDN);
				if (!rolesDN.endsWith(",")) {
					rolesNodeDNBuffer.append(',');
				}
			}
			rolesNodeDNBuffer.append(baseDN);
			String rolesNodeDN = rolesNodeDNBuffer.toString();
			Attribute objClassAttr = new BasicAttribute("objectClass",
					"groupOfNames");
			Attribute memberAttr = new BasicAttribute("member", userDn);
			Attributes attrs = new BasicAttributes();
			attrs.put(objClassAttr);
			attrs.put(memberAttr);
			try {
				NamingEnumeration<SearchResult> rolesSearchRes = context
						.search(rolesNodeDN, attrs);
				while (rolesSearchRes.hasMore()) {
					SearchResult sr = rolesSearchRes.next();
					// System.out.println("Sr: "+sr.getName());
					Attributes srAttrs = sr.getAttributes();
					Attribute cnattr = srAttrs.get("cn");
					Object roleObj = cnattr.get();
					if (roleObj instanceof String) {
						String role = (String) roleObj;
						rolePrincipal = new RolePrincipal(role);
						if (!principalSet.contains(rolePrincipal))
							principalSet.add(rolePrincipal);

						if (debug) {
							System.out
									.println("[LDAPLoginModule] added RolePrincipal "
											+ rolePrincipal.getName()
											+ " to Subject");
						}

					}
					// sr.getName()
				}
			} catch (NamingException e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
				// no principals added
			}

			// in any case, clean out state
			username = null;
			for (int i = 0; i < password.length; i++)
				password[i] = ' ';
			password = null;

			commitSucceeded = true;
			return true;
		}
	}

	public boolean abort() throws LoginException {
		if (succeeded == false) {
			return false;
		} else if (succeeded == true && commitSucceeded == false) {
			// login succeeded but overall authentication failed
			succeeded = false;
			username = null;
			if (password != null) {
				for (int i = 0; i < password.length; i++)
					password[i] = ' ';
				password = null;
			}
			userPrincipal = null;
		} else {
			// overall authentication succeeded and commit succeeded,
			// but someone else's commit failed
			logout();
		}
		return true;
	}

	public boolean logout() throws LoginException {

		subject.getPrincipals().remove(userPrincipal);
		subject.getPrincipals().remove(rolePrincipal);
		succeeded = false;
		succeeded = commitSucceeded;
		username = null;
		if (password != null) {
			for (int i = 0; i < password.length; i++)
				password[i] = ' ';
			password = null;
		}
		userPrincipal = null;
		rolePrincipal = null;
		return true;
	}
}
