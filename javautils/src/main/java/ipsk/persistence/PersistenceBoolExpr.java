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

package ipsk.persistence;

/**
 * Represents the WHERE clause (conditional_expression) for an JPQL (Java EE 5 persistence query).
 * @author klausj
 *
 */
public class PersistenceBoolExpr {

	private String conditionalExpression;
	private Object[] queryVars;
	
	public PersistenceBoolExpr(String querystring,Object[] queryVars){
		this.conditionalExpression=querystring;
		this.queryVars=queryVars;
	}

	/**
	 * Get the conditional expression string (the WHERE clause).
	 * @return conditional expression
	 */
	public String getConditionalExpression() {
		return conditionalExpression;
	}
	/**
	 * Set the conditional expression string (the WHERE clause).
	 * @param queryString
	 */
	public void setConditionalExpression(String queryString) {
		this.conditionalExpression = queryString;
	}

	/**
	 * Get query variable objects.
	 * @return array of query variables
	 */
	public Object[] getQueryVars() {
		return queryVars;
	}

	public void setQueryVars(Object[] queryVars) {
		this.queryVars = queryVars;
	}
	
}
