//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
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

package ipsk.net;

import java.net.Authenticator;
import java.net.PasswordAuthentication;


/**
 * Stores and provides username and password for network authentication.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class SimplePasswordAuthentication extends Authenticator {

	private String username;
	private char[] password;
	
	/**
	 * Creates new password authentication.
	 * @param username the username for authentication
	 * @param password the password for authentication
	 */
	public SimplePasswordAuthentication(String username,String password){
		this.username=username;
		this.password=password.toCharArray();
	}
	
	public SimplePasswordAuthentication(String username,char[] password){
        this.username=username;
        this.password=password;
    }

	/**
	 * Gets password authentication.
	 * @return PasswordAuthentication password authentication
	 */
	protected PasswordAuthentication getPasswordAuthentication() {
		  	return new PasswordAuthentication(username, password);
	}
		  

	/**
	 * Gets current password.
	 * @return curent password
	 */
	public String getPassword() {
		return new String(password);
	}
	
	public char[] getPasswordChars() {
        return password;
    }
	

	/**
	 * Gets current user name.
	 * @return user name
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets password.
	 * @param string password
	 */
	public void setPassword(String string) {
		password = string.toCharArray();
	}

	/**
	 * Sets user name.
	 * @param string user name
	 */
	public void setUsername(String string) {
		username = string;
	}

}
