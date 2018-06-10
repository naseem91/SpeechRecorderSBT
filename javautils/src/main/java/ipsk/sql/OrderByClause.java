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
 * SQL/JPQL order by clause.
 * @author klausj
 *
 */
public class OrderByClause {

	private OrderBy[] order;

	
	public OrderByClause(OrderBy[] order){
		this.order=order;
	}
	
	public OrderBy[] getOrder() {
		return order;
	}

	public void setOrder(OrderBy[] order) {
		this.order = order;
	}
	
	public String toSQLString(){
		if (order==null || order.length==0) return "";
		String orderStr=" ORDER BY ";
		for(int i=0;i<order.length;i++){
			OrderBy orderBy=order[i];
			orderStr=orderStr.concat(orderBy.toSQLString());
			if (i<order.length-1){
				orderStr=orderStr.concat(", ");
			}
		}
		return orderStr;
	}
	
	public String toJPQLString(String jpqlSelectExpression){
		if (order==null || order.length==0) return "";
		String orderStr=" ORDER BY ";
		for(int i=0;i<order.length;i++){
			OrderBy orderBy=order[i];
			orderStr=orderStr.concat(orderBy.toJPQLString(jpqlSelectExpression));
			if (i<order.length-1){
				orderStr=orderStr.concat(", ");
			}
		}
		return orderStr;
	}
	
}
