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

package ipsk.sql;

/**
 * SQL order by term.
 * @author klausj
 *
 */
public class OrderBy {

	private String column;
	private boolean descending;
	
	public OrderBy(String column){
		this(column,false);
	}
	public OrderBy(String column,boolean descending){
		this.column=column;
		this.descending=descending;
	}
	public String getColumn() {
		return column;
	}
	public void setColumn(String column) {
		this.column = column;
	}
	public boolean isDescending() {
		return descending;
	}
	public void setDescending(boolean descending) {
		this.descending = descending;
	}
	/**
	 * Convert to SQL string.
	 * @return SQL string
	 */
	public String toSQLString(){
		String direction="";
		if(descending){
			direction=" DESC";
		}
		return column+direction;
	}
	
	/**
	 * Convert to JPQL string.
	 * @param jpqlSelectExpression the select expression e.g. "o" (SELECT object o FROM foo ORDER BY o.column)
	 * @return JPQL string
	 */
	public String toJPQLString(String jpqlSelectExpression){
		String direction="";
		if(descending){
			direction=" DESC";
		}
		return jpqlSelectExpression+"."+column+direction;
	}
}
