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

import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;



/**
 * Group of selections.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class SelectionGroup {

    
    //public final static String MARKS="Marks";
  
    protected Vector<Selection> selections;
    protected String name;
    
    /**
     * 
     */
    public SelectionGroup() {
        this(null);
           
    }
    
    public SelectionGroup(String name){
        super();
        this.name=name;
        selections=new Vector<Selection>(); 
    }
    
    public Selection[] getSelectionsContainingPosition(long pos){
        Vector<Selection> matches=new Vector<Selection>();
        synchronized(selections){
            for(int i=0;i<selections.size();i++){
                Selection s =(Selection)selections.get(i);
                if (s.isInSelection(pos)) matches.add(s);
            }
        
        return (Selection[])matches.toArray(new Selection[0] );
        }
    }
    
    public Selection getLastSelectionContainingPosition(long pos){
        synchronized(selections){
            for(int i=selections.size()-1;i>=0;i--){
                Selection s =(Selection)selections.get(i);
                if (pos>=s.getLeft() && pos<s.getRight()) return(s);
            }
        
        return null;
        }
    }
    
    public static Selection[] getSelectionsContainingPosition(SelectionGroup[] sgs,long pos){
        Vector<Selection> matches=new Vector<Selection>();
        synchronized(sgs){
        for (int i=0;i<sgs.length;i++){
            matches.addAll(Arrays.asList(sgs[i].getSelectionsContainingPosition(pos)));
        }
        return (Selection[])matches.toArray(new Selection[0] );
        }
    }
    
    public static Selection getLastSelectionContainingPosition(SelectionGroup[] sgs,long pos){
      synchronized(sgs){
        for (int i=sgs.length-1;i>=0;i++){
            Selection[] ss=sgs[i].getSelections();
            for (int j=ss.length-1;j<=0;j--){
                if (ss[j].isInSelection(pos))return ss[j];
            }
            
        }
      
        
        return null;
      }
    }
    public boolean equals(Object o){
        if (o==null)return false;
        if (!(o instanceof SelectionGroup)) return false;
        SelectionGroup sg=(SelectionGroup)o;
        
        return (((sg.getName()==null && name==null) || sg.getName().equals(name)) && sg.getSelections().equals(selections)); 
       
    }
    
    
    
    
    public void add(int arg0, Selection arg1) {
        selections.add(arg0, arg1);
    }
    public boolean add(Selection arg0) {
        return selections.add(arg0);
    }
    public void clear() {
        selections.clear();
    }
    public boolean contains(Selection arg0) {
        return selections.contains(arg0);
    }
    public void copyInto(Selection[] arg0) {
        selections.copyInto(arg0);
    }
    public Object elementAt(int arg0) {
        return selections.elementAt(arg0);
    }
    public Enumeration<Selection> elements() {
        return selections.elements();
    }
    public Selection firstElement() {
        return (Selection)(selections.firstElement());
    }
    public Selection get(int arg0) {
        return (Selection)selections.get(arg0);
    }
    public int indexOf(Selection arg0) {
        return selections.indexOf(arg0);
    }
    public int indexOf(Selection arg0, int arg1) {
        return selections.indexOf(arg0, arg1);
    }
    public void insertElementAt(Selection arg0, int arg1) {
        selections.insertElementAt(arg0, arg1);
    }
    public boolean isEmpty() {
        return selections.isEmpty();
    }
    public Iterator<Selection> iterator() {
        return selections.iterator();
    }
    public Selection lastElement() {
        return (Selection)selections.lastElement();
    }
    public int lastIndexOf(Selection arg0) {
        return selections.lastIndexOf(arg0);
    }
    public int lastIndexOf(Selection arg0, int arg1) {
        return selections.lastIndexOf(arg0, arg1);
    }
    public ListIterator<Selection> listIterator() {
        return selections.listIterator();
    }
    public ListIterator<Selection> listIterator(int arg0) {
        return selections.listIterator(arg0);
    }
    public Selection remove(int arg0) {
        return (Selection)selections.remove(arg0);
    }
    public boolean remove(Selection arg0) {
        return selections.remove(arg0);
    }
    public boolean removeAll(Collection<Selection> arg0) {
        return selections.removeAll(arg0);
    }
    public void removeAllElements() {
        selections.removeAllElements();
    }
    public boolean removeElement(Selection arg0) {
        return selections.removeElement(arg0);
    }
    public void removeElementAt(int arg0) {
        selections.removeElementAt(arg0);
    }
    public boolean retainAll(Collection<Selection> arg0) {
        return selections.retainAll(arg0);
    }
    public Selection set(int arg0, Selection arg1) {
        return (Selection)selections.set(arg0, arg1);
    }
    public void setElementAt(Selection arg0, int arg1) {
        selections.setElementAt(arg0, arg1);
    }
    public int size() {
        return selections.size();
    }
    public List<Selection> subList(int arg0, int arg1) {
        return selections.subList(arg0, arg1);
    }
    
    public Selection[] getSelections() {
        return (Selection [])selections.toArray(new Selection[0]);
    }
    public String toString() {
        return selections.toString();
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
