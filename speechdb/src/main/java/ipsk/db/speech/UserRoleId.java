//    IPS Java Speech Database
//    (c) Copyright 2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Speech Database
//
//
//    IPS Java Speech Database is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Speech Database is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Speech Database.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.db.speech;


import java.util.Locale;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

/**
 * UserRoleId
 */
@Embeddable
public class UserRoleId implements java.io.Serializable {
	
	public enum RoleName {
		ADMIN,PROJECT_ADMIN,ANNOTATON_PROJECT_ADMIN,PROJECT_MEMBER,SUBJECT,ORGANISATION,PROJECT_ANNOTATOR;
		
		// JSP EL friendly getter
		public String getName(){
			return name();
		}
		public static RoleName parse(String roleName){
			return RoleName.valueOf(roleName.trim().toUpperCase(Locale.ENGLISH));
		}
	}
	// Fields    

	// Java EE Tutorial: must be public (or protected)
	public String login;
	public RoleName roleName;
	

	
	// Constructors

	/** default constructor */
	public UserRoleId() {
	}

	/** full constructor */
	public UserRoleId(String login, RoleName roleName) {
		this.login = login;
		this.roleName = roleName;
	}

	/**
	 * Constructor with single string.
	 * Required for frame work to create ID object using reflection.
	 *  Syntax: login,rolename
	 */
	public UserRoleId(String parseString){
		StringTokenizer st=new StringTokenizer(parseString,",");
		login=st.nextToken().trim();
		String roleNameStr=st.nextToken().trim();
		roleName=RoleName.parse(roleNameStr);
		
	}
	
	
	
	// Property accessors

	@Column(name = "login", length = 100)
	public String getLogin() {
		return this.login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	@Column(name = "role_name", length = 100)
	@Enumerated(EnumType.STRING)
	public RoleName getRoleName() {
		return this.roleName;
	}

	public void setRoleName(RoleName roleName) {
		this.roleName = roleName;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof UserRoleId))
			return false;
		UserRoleId castOther = (UserRoleId) other;

		return ((this.getLogin() == castOther.getLogin()) || (this.getLogin() != null
				&& castOther.getLogin() != null && this.getLogin().equals(
				castOther.getLogin())))
				&& ((this.getRoleName() == castOther.getRoleName()) || (this
						.getRoleName() != null
						&& castOther.getRoleName() != null && this
						.getRoleName().equals(castOther.getRoleName())));
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result
				+ (getLogin() == null ? 0 : this.getLogin().hashCode());
		result = 37 * result
				+ (getRoleName() == null ? 0 : this.getRoleName().hashCode());
		return result;
	}

	public String toString(){
		return login+", "+roleName;
	}
	
}
