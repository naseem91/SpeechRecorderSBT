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


import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Import
 */
@Entity
@Table(name = "import")
public class Import implements java.io.Serializable {

	// Fields    

	private ImportId id;

	// Constructors

	/** default constructor */
	public Import() {
	}

	/** full constructor */
	public Import(ImportId id) {
		this.id = id;
	}

	// Property accessors
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "value", column = @Column(name = "value", nullable = true, length = 1000)),
			@AttributeOverride(name = "type", column = @Column(name = "type",  nullable = true, length = 100)),
			@AttributeOverride(name = "id", column = @Column(name = "id", length = 10)),
			@AttributeOverride(name = "description", column = @Column(name = "description", length = 100)) })
	public ImportId getId() {
		return this.id;
	}

	public void setId(ImportId id) {
		this.id = id;
	}

}
