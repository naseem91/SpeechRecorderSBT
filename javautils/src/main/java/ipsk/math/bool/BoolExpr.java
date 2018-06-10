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

package ipsk.math.bool;

import ipsk.util.ObjectNullComparator;

/**
 * Represents a boolean condition.
 * @author klausj
 */
public class BoolExpr implements Cloneable{

	
	public static final String GREATER_THEN = ">";
	public static final String GREATER_EQUAL = ">=";
	public static final String LESS_THEN = "<";
	public static final String LESS_EQUAL = "<=";
	

    public static String EQUAL = "=";

    public static String NOT_EQUAL = "<>";

    public static String AND = "AND";

    public static String OR = "OR";
    
    protected Object operand0;
    protected Object operator;
    protected Object operand1;
    public BoolExpr() {
        
    }
    /**
     * @return Returns the operand0.
     */
    public Object getOperand0() {
        return operand0;
    }
    /**
     * @param operand0 The operand0 to set.
     */
    public void setOperand0(Object operand0) {
        this.operand0 = operand0;
    }
    /**
     * @return Returns the operand1.
     */
    public Object getOperand1() {
        return operand1;
    }
    /**
     * @param operand1 The operand1 to set.
     */
    public void setOperand1(Object operand1) {
        this.operand1 = operand1;
    }
    /**
     * @return Returns the operator.
     */
    public Object getOperator() {
        return operator;
    }
    /**
     * @param operator The operator to set.
     */
    public void setOperator(Object operator) {
        this.operator = operator;
    }
    
    public boolean isExpression(){
        String[] exprOps=getExprOperators();
        for(int i=0;i<exprOps.length;i++){
            if(exprOps[i].equals(operator))return true;
        }
        return false;
    }
    
    public static String[] getExprOperators(){
    	
    	return new String []{BoolExpr.EQUAL,BoolExpr.NOT_EQUAL,BoolExpr.GREATER_THEN,BoolExpr.GREATER_EQUAL,BoolExpr.LESS_THEN,BoolExpr.LESS_EQUAL};
    }
    
    
    public boolean equals(Object o){
    	if (o==null || !( o instanceof BoolExpr)) return false;
    	BoolExpr be=(BoolExpr)o;
    	
    	return (ObjectNullComparator.areEqual(be.getOperand0(), operand0) && ObjectNullComparator.areEqual(be.getOperator(), operator) && ObjectNullComparator.areEqual(be.getOperand1(), operand1));
//    	if (!be.getOperand0().equals(operand0)) return false;
//    	if (!be.getOperand1().equals(operand1)) return false;
//    	if (!be.getOperator().equals(operator)) return false;
//    	return true;
    	
    }
    
    protected Object cloneType(Object o) throws CloneNotSupportedException{
        if(o==null)return null;
        if(o instanceof String){
            return new String((String)o);
        }else if(o instanceof Byte){
            return new Byte(((Byte)o).byteValue());
        }else if(o instanceof Short){
            return new Short(((Short)o).shortValue());
        }else if(o instanceof Integer){
            return new Integer(((Integer)o).intValue());
        }else if(o instanceof Long){
            return new Long(((Long)o).longValue());
        }else if(o instanceof Float){
            return new Float(((Float)o).floatValue());
        }else if(o instanceof Double){
            return new Double(((Double)o).doubleValue());
        }else throw new CloneNotSupportedException();
        
    }
    
    public Object clone() throws CloneNotSupportedException{
        BoolExpr clone=new BoolExpr();
        if (operand0!=null && operand0 instanceof BoolExpr){
           
            clone.setOperand0(((BoolExpr)operand0).clone());
        }else{
            clone.setOperand0(cloneType(operand0));
        }
        
        clone.setOperator(cloneType(operator));
        if (operand1!=null && operand1 instanceof BoolExpr){
            
            clone.setOperand1(((BoolExpr)operand1).clone());
        }else{
            clone.setOperand1(cloneType(operand1));
        }
        return clone;
    }
  

}
