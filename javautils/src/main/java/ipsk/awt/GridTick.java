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

package ipsk.awt;

/**
 * A tick of scale.
 * 
 * @author klausj
 *
 */
public class GridTick<T> extends Object{

//	public final static int MAJOR_TYPE=0;
//	public final static int MINOR_TYPE=1;

	public enum Type {MAJOR,MINOR}
	private T tickValue;
	private int position;
	private int nextPosition;
	private Type type=Type.MAJOR;
	
	public GridTick(int position){
		this(position,null);
	}
	public GridTick(int position,T tickvalue){
		super();
		this.position=position;
		this.tickValue=tickvalue;
	}
	
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public T getTickValue() {
		return tickValue;
	}
	public void setTickValue(T tickValue) {
		this.tickValue = tickValue;
	}
	public Type getType() {
		return type;
	}
	/**
	 * Set tick type.
	 * @param type
	 */
	public void setType(Type type) {
		this.type = type;
	}
	public int getNextPosition() {
		return nextPosition;
	}
	/**
	 * Set position of the next subsequent tick of same type
	 * @param nextPosition
	 */
	public void setNextPosition(int nextPosition) {
		this.nextPosition = nextPosition;
	}
	
}
