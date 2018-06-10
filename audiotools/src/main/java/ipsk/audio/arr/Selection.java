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
 * Date  : Jul 14, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.arr;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class Selection implements Cloneable {
    protected Marker startMarker;

    protected Marker endMarker;

    protected String name;
    
    protected int[] channels=null;

    public Selection() {
        this(new Marker(), new Marker(), null);
    }

    public Selection(Marker startMarker, Marker endMarker) {
        this(startMarker, endMarker, null);
    }

    public Selection(long start, long end) {
        this(new Marker(start), new Marker(end), null);
    }

    /**
     * Labeled selection.
     * @param startMarker start marker
     * @param endMarker end marker
     * @param name label
     */
    public Selection(Marker startMarker, Marker endMarker, String name) {
        this.startMarker = startMarker;
        this.endMarker = endMarker;
        this.name = name;
    }

    public synchronized boolean isInSelection(long pos) {
        return ((pos >= startMarker.position && pos <= endMarker.position) || (pos <= startMarker.position && pos >= endMarker.position));
    }

    public long getLeft() {
        return (startMarker.position <= endMarker.position) ? startMarker.position
                : endMarker.position;
    }

    public long getRight() {
        return (startMarker.position <= endMarker.position) ? endMarker.position
                : startMarker.position;
    }

    public long getLength() {
        return Math.abs(endMarker.position - startMarker.position) + 1;
    }

    public void setStart(long l) {
        startMarker.setPosition(l);
    }

    public void setEnd(long l) {
        endMarker.setPosition(l);
    }
    
    public long getStart() {
        return startMarker.getPosition();
    }

    public long getEnd() {
        return endMarker.getPosition();
    }
    public void limitTo(long startLimit, long endLimit) {
        if (startMarker.position <= endMarker.position) {
            if (startMarker.position < startLimit)
                startMarker.setPosition(startLimit);
            if (endMarker.position > endLimit)
                endMarker.setPosition(endLimit);
        } else {
            if (startMarker.position > endLimit)
                startMarker.setPosition(endLimit);
            if (endMarker.position < startLimit)
                endMarker.setPosition(startLimit);
        }

    }

    public Marker getEndMarker() {
        return endMarker;
    }

    public void setEndMarker(Marker endMarker) {
        this.endMarker = endMarker;
    }

    public Marker getStartMarker() {
        return startMarker;
    }

    public void setStartMarker(Marker startMarker) {
        this.startMarker = startMarker;
    }

    public Object clone() throws CloneNotSupportedException {

        Selection clone = null;
        if (name != null) {
            clone = new Selection((Marker) (startMarker.clone()),
                    (Marker) (endMarker.clone()), new String(name));
        } else {
            clone = new Selection((Marker) (startMarker.clone()),
                    (Marker) (endMarker.clone()));
        }
        return clone;
    }

    
    public boolean equals(Object o){
        if (o==null)return false;
        if (!(o instanceof Selection)) return false;
        Selection s=(Selection)o;
        
        return(((s.getName()==null && name==null) || s.getName().equals(name)) && s.getStartMarker().equals(startMarker) && s.getEndMarker().equals(endMarker));
       
    }
    
    public String toString(){
    	return new String("Selection: "+getStart()+"-"+getEnd());    	
    }
    
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int[] getChannels() {
        return channels;
    }
    public void setChannels(int[] channels) {
        this.channels = channels;
    }
}
