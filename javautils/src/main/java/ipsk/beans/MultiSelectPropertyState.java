//    IPS Java Utils
// 	  (c) Copyright 2014
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

package ipsk.beans;

/**
 * @author klausj
 *
 */
public class MultiSelectPropertyState<T> {

    private T objectSet=null;
  
    private boolean equalOrNull=true;
    private boolean firstObj=true;
    /**
     * 
     */
    public MultiSelectPropertyState() {
        super();
    }
    
    public void nextNotAvail(){
    	equalOrNull=false;
    }
    public void next(T obj){
        if(!equalOrNull){
            return;
        }
        if(firstObj){
            objectSet=obj;
            firstObj=false;
        }else{
            if(objectSet==null){
                if(obj!=null){
                    objectSet=obj;
                    equalOrNull=false;
                }
            }else{
                if(!objectSet.equals(obj)){
                    equalOrNull=false;
                }
            }
        }
    }
    public boolean allEqual(){
        return(equalOrNull);
    }

    public T getObjectSet() {
        return objectSet;
    }

}
