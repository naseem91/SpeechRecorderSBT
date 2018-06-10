//    IPS Java Audio Tools
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Oct 17, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.audio.arr;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class Marker implements Cloneable{

    protected long position=0;
    protected String name=null;
    
    /**
     * 
     */
    public Marker() {
       super();
    }
    /**
     * 
     */
    public Marker(long position) {
      this.position=position;
    }
  
    /**
     * 
     */
    public Marker(String name,long position) {
      this.position=position;
    }
    
   
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public long getPosition() {
        return position;
    }
    public void setPosition(long position) {
        this.position = position;
    }
    
    public Object clone() throws CloneNotSupportedException{
       
        Marker clone=null;
        if (name==null){
            clone=new Marker(getPosition());
        }else{
        new Marker(new String(name),position);
        }
        return clone;
    }
    
    public boolean equals(Object o){
        if (o==null)return false;
        if (!(o instanceof Marker)) return false;
        Marker m=(Marker)o;
        
        return (((m.getName()==null && name==null) || m.getName().equals(name)) && m.getPosition()==position);
    }
    
    public String toString(){
    	StringBuffer sb=new StringBuffer();
    	if(name!=null){
    		sb.append(name+": ");
    	}
    	sb.append(position);
    	return sb.toString();
    }
    
    
}
