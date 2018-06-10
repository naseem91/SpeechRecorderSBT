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



import ipsk.util.ResourceKey;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * User role
 */
@Entity
@Table(name = "user_role")
public class UserRole implements java.io.Serializable {

	// Fields    

	private UserRoleId id;

	private Account account;

	// Constructors

	/** default constructor */
	public UserRole() {
	}

	/** minimal constructor */
	public UserRole(UserRoleId id) {
		this.id = id;
	}

	/** full constructor */
	public UserRole(UserRoleId id, Account account) {
		this.id = id;
		this.account = account;
	}

	// Property accessors
	@EmbeddedId
	//@AttributeOverrides( {
			//@AttributeOverride(name = "login", column = @Column(name = "login", length = 100)),
//			@AttributeOverride(name = "roleName", column = @Column(name = "role_name", length = 100)) })
	@ResourceKey("id")
	public UserRoleId getId() {
		return this.id;
	}

	public void setId(UserRoleId id) {
		this.id = id;
	}

	@ManyToOne( fetch = FetchType.LAZY)
	@JoinColumn(name = "login", insertable = false, updatable = false)
	@ResourceKey("account")
	public Account getAccount() {
		return this.account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

}
