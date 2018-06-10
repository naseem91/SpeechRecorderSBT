//    IPS Java Utils
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ips.net.auth.jaas;

import java.io.Console;
import java.io.IOException;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.TextInputCallback;
import javax.security.auth.callback.TextOutputCallback;
import javax.security.auth.callback.UnsupportedCallbackException;

/**
 * @author klausj
 * 
 */
public class ConsoleCallbackHandler implements CallbackHandler {

	/**
     * 
     */
	public ConsoleCallbackHandler() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.security.auth.callback.CallbackHandler#handle(javax.security.auth
	 * .callback.Callback[])
	 */
	public void handle(Callback[] callbacks) throws IOException,
			UnsupportedCallbackException {
		Console console = System.console();
		for (Callback cb : callbacks) {
			if (cb instanceof TextOutputCallback) {
				TextOutputCallback tocb = (TextOutputCallback) cb;
				// TODO distinguish message type
				String msg = tocb.getMessage();
				console.printf("%s\n", msg);
			} else if (cb instanceof TextInputCallback) {
				TextInputCallback ticb = (TextInputCallback) cb;
				String defaultText = ticb.getDefaultText();
				String prompt = ticb.getPrompt();
				if (prompt != null) {
					console.printf("%s", prompt);
				}
				String retInput = null;
				String input = console.readLine();

				if (input != null && "".equals(input)) {
					retInput = input;
				} else {
					retInput = defaultText;
				}
				ticb.setText(retInput);
			} else if (cb instanceof NameCallback) {

				// username
				NameCallback ncb = (NameCallback) cb;
				String prompt = ncb.getPrompt();
				if (prompt != null) {
					console.printf("%s", prompt);
				}

				String input = console.readLine();
				ncb.setName(input);

			} else if (cb instanceof PasswordCallback) {

				PasswordCallback pwcb = (PasswordCallback) cb;

				String prompt = pwcb.getPrompt();
				if (prompt != null) {
					console.printf("%s", prompt);
				}

				char[] passwInput = console.readPassword();
				pwcb.setPassword(passwInput);
			} else {
				throw new UnsupportedCallbackException(cb,
						"Cannot handle callback.");
			}
		}

	}

}
