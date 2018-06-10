package ips.net.auth.jaas;

import java.security.Principal;

public class RolePrincipal implements Principal, java.io.Serializable {

	private String roleName;

	public RolePrincipal(String name) {
		if (name == null)
			throw new NullPointerException();

		this.roleName = name;
	}

	public String getName() {
		return roleName;
	}

	public String toString() {
		return ("JAAS role:  " + roleName);
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (this == o)
			return true;

		if (!(o instanceof RolePrincipal))
			return false;
		RolePrincipal that = (RolePrincipal) o;

		if (this.getName().equals(that.getName()))
			return true;
		return false;
	}

	public int hashCode() {
		return roleName.hashCode();
	}
}
