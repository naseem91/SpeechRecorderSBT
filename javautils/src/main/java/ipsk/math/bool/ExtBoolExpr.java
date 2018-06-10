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

import ipsk.lang.reflect.NativeTypeWrapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * Boolean expression with extra operators intended for SQ/HQL querys. 
 * @author klausj
 */
public class ExtBoolExpr extends BoolExpr {

	// String operators
    public static final String STARTS_WITH = "(starts with)";

    public static final String ENDS_WITH = "(ends with)";

    public static String CONTAINS = "(contains)";
    
    // Object relationship operators  
    public static String EQUALS ="(equals)";
    public static String EQUALS_NOT="(equals not)";
    public static String MEMBER ="(has member)";
    public static String NOT_MEMBER="(has no member)";
    public static String BOUND ="(is bound)";
    public static String NOT_BOUND="(is not bound)";
    
    private boolean caseInSensitive=false;
     
    public ExtBoolExpr() {
        super();
        
        //operator = EQUAL;
    }

    public ExtBoolExpr(String col, Object val) {
        this();
        operand0 = col;
        operand1 = val;
    }
    public boolean isExpression(){
        String[] exprOps=getExprOperators();
        for(int i=0;i<exprOps.length;i++){
            if(exprOps[i].equals(operator))return true;
        }
        return false;
    }
    
    /**
     * Adds an empty expression.
     * This is intended for HTML forms, to allow the user to extend the condition.
     * @throws CloneNotSupportedException 
     *
     */
    public ExtBoolExpr addEmptyExpression() throws CloneNotSupportedException{
        ExtBoolExpr newConditon=new ExtBoolExpr();
        //ExtBoolExpr clone=(ExtBoolExpr)this.clone();
        newConditon.setOperator(ExtBoolExpr.AND);
        newConditon.setOperand0(this);
        newConditon.setOperand1(new ExtBoolExpr());
        return newConditon;
    }
    
    public static String[] getNumberExprOperators(){
     return BoolExpr.getExprOperators();
    }
    
    public static String[] getExprOperators(){
    	ArrayList<String> ops=new ArrayList<String>();
    	ops.addAll(Arrays.asList(BoolExpr.getExprOperators()));
    	String[] addOps=new String []{STARTS_WITH,ENDS_WITH,CONTAINS,EQUALS,EQUALS_NOT,MEMBER,NOT_MEMBER,BOUND,NOT_BOUND};
    	ops.addAll(Arrays.asList(addOps));
    	return (String[])ops.toArray(new String[0]);
    }
    
    public static String[] getExprOperatorsWithoutRelationshipOperators(){
    	ArrayList<String> ops=new ArrayList<String>();
    	ops.addAll(Arrays.asList(BoolExpr.getExprOperators()));
    	String[] addOps=new String []{STARTS_WITH,ENDS_WITH,CONTAINS};
    	ops.addAll(Arrays.asList(addOps));
    	return (String[])ops.toArray(new String[0]);
    }
    
    public boolean unaryOperator(){
    	return BOUND.equals(operator) || NOT_BOUND.equals(operator);
    }

  public static boolean isOperatorSupportingType(Object operator,Class<?> type){
	  Class<?> wrapType=NativeTypeWrapper.getWrapperClass(type);
	  if(wrapType.equals(Boolean.class)){
		  return (operator.equals(EQUAL) || operator.equals(NOT_EQUAL));
	  }else if(wrapType.isEnum()){
		  return (operator.equals(EQUAL) || operator.equals(NOT_EQUAL));
	  }else if(wrapType.equals(String.class)){
		  return(operator.equals(GREATER_EQUAL)|| operator.equals(GREATER_THEN) ||operator.equals(LESS_EQUAL) || operator.equals(LESS_THEN)||operator.equals(EQUAL) ||operator.equals(NOT_EQUAL) ||operator.equals(STARTS_WITH) || operator.equals(ENDS_WITH)|| operator.equals(CONTAINS));
		 
	  }else if(wrapType.equals(Character.class)){
		  return(operator.equals(GREATER_EQUAL)|| operator.equals(GREATER_THEN) ||operator.equals(LESS_EQUAL) || operator.equals(LESS_THEN)||operator.equals(EQUAL) ||operator.equals(NOT_EQUAL));
		 
	  }else if(Number.class.isAssignableFrom(wrapType)){
		  return (operator.equals(EQUAL) || operator.equals(NOT_EQUAL) ||  operator.equals(GREATER_EQUAL)|| operator.equals(GREATER_THEN) ||operator.equals(LESS_EQUAL) || operator.equals(LESS_THEN));
			  
	  }else if(Date.class.isAssignableFrom(wrapType)){
		  return (operator.equals(EQUAL) || operator.equals(NOT_EQUAL) ||  operator.equals(GREATER_EQUAL)|| operator.equals(GREATER_THEN) ||operator.equals(LESS_EQUAL) || operator.equals(LESS_THEN));	  
	  }else{
		  // related objects
		  return (operator.equals(MEMBER)|| operator.equals(NOT_MEMBER) || operator.equals(EQUALS) || operator.equals(EQUALS_NOT));
	  }
		
  }
  
  /**
   * Returns true if this a object relationship condition.
   * JPQL example: SELECT o FROM Organisation o WHERE :operand1 MEMBER OF o.persons
   * @return true if expression is a relationship between objects
   */
  public boolean isObjectRelationShip(){
	  if (operator != null && (operator.equals(EQUALS) ||
			  operator.equals(MEMBER) || operator.equals(EQUALS_NOT) || operator.equals(NOT_MEMBER) || operator.equals(BOUND) || operator.equals(NOT_BOUND))){
		  return true;
	  }else{
	  return false;
	  }
  }
  public Object clone() throws CloneNotSupportedException{
      ExtBoolExpr clone=new ExtBoolExpr();
      if (operand0!=null && operand0 instanceof ExtBoolExpr){
         
          clone.setOperand0(((ExtBoolExpr)operand0).clone());
      }else{
          clone.setOperand0(cloneType(operand0));
      }
      
      clone.setOperator(cloneType(operator));
      if (operand1!=null && operand1 instanceof ExtBoolExpr){
          
          clone.setOperand1(((ExtBoolExpr)operand1).clone());
      }else{
          clone.setOperand1(cloneType(operand1));
      }
      clone.setCaseInSensitive(caseInSensitive);
      return clone;
  }

public boolean isCaseInSensitive() {
	return caseInSensitive;
}

public void setCaseInSensitive(boolean caseInSensitive) {
	this.caseInSensitive = caseInSensitive;
}
 
}
