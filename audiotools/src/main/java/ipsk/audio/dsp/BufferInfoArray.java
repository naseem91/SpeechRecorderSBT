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
 * Created on 24.08.2005
 * 
 */
package ipsk.audio.dsp;

import java.util.ArrayList;
import java.util.List;

public class BufferInfoArray<T extends BufferInfo> {

    private static final boolean DEBUG=false;
    
	private List<T> bufferInfos;

	private int length;

	private int position;

	private int lastMatch;

	public BufferInfoArray() {
		this(512);

	}

	public BufferInfoArray(int capacity) {
		length = capacity;
		bufferInfos = new ArrayList<T>(capacity);
		for(int i=0;i<length;i++){
			bufferInfos.add(null);
		}
		position=0;
		lastMatch=0;
	}

	public void add(T bi) {
//		bufferInfos[position++] = bi;
		bufferInfos.set(position++, bi);
		if (position == length)
			position = 0;

		//System.out.println("Added pos: "+bi.getFramePosition());
	}

	public void clear() {
		for(int i=0;i<length;i++){
			bufferInfos.set(i,null);
		}
		position = 0;
	}

	
	private boolean matches(T bi,long framePosition){
		long biFramePos = bi.getFramePosition();
		long biEnd=biFramePos + bi.getFrameLength();
		//System.out.println("LevelPos: "+biFramePos+" "+framePosition+" "+biEnd);
		return (framePosition >= biFramePos
				&& framePosition < biEnd);
	}
	
	private boolean tangents(T bi,long startFramePosition,long endFramePosition){
		// assume start<=end
		long biFramePos = bi.getFramePosition();
		long biEnd=biFramePos + bi.getFrameLength();
		if(biFramePos>=startFramePosition){
			if(biFramePos<=endFramePosition){
				// buffer starts in interval
				return(true);
			}
		}else{
			// buffer end is in interval
			// or buffer covers interval completely (biFramePos<start && biEnd >= endFramePosition )
			if(biEnd>=startFramePosition){
				return(true);
			}
		}
		return false;
	}
	
	private int bufferInfoIndexAtFramePosition(long framePosition) {

		// Lookup ring buffer from last match to end
		for (int i = lastMatch; i < length; i++) {
			T bi =  bufferInfos.get(i);
			if (bi == null)
				continue;
			if(matches(bi,framePosition)){
				lastMatch=i;
				return i;
			}
		}
		// If not found continue lookup ring buffer from start to last match
		for (int i = 0; i < lastMatch; i++) {
			T bi =  bufferInfos.get(i);
			if (bi == null)
				continue;
			if(matches(bi,framePosition)){
				lastMatch=i;
				return i;
			}
		}
		return -1;

	}
	
	
	public BufferInfo getBufferInfoAtFramePosition(long framePosition) {

		int idx=bufferInfoIndexAtFramePosition(framePosition);
		if(idx<0){
			return null;
		}else{
			return bufferInfos.get(idx);
		}
	}
	
	
	/**
	 * List all tangent buffers
	 * @param fromFramePosition start of interval
	 * @param toFramePosition stop of interval
	 * @return list of tangent buffer infos
	 */
	public List<T> listBufferInfosTangentInterval(long fromFramePosition,long toFramePosition){
		long startFp;
		long endFp; 
		List<T> biList=new ArrayList<T>();
		if(fromFramePosition<=toFramePosition){
			startFp=fromFramePosition;
			endFp=toFramePosition;
		}else{
			startFp=toFramePosition;
			endFp=fromFramePosition;
		}
		// Assume ordered sequence without gaps and intersections

		int startIdx=bufferInfoIndexAtFramePosition(startFp);
		if(startIdx>=0){
			// Lookup ring buffer from start index to end
			for (int i = startIdx; i < length; i++) {
				T bi = bufferInfos.get(i);
				if (bi == null)
					continue;
				if(tangents(bi, startFp,endFp)){
					lastMatch=i;
					biList.add(bi);
				}else{
					return biList;
				}
			}
			// If not found continue lookup ring buffer from start to last match
			for (int i = 0; i < startIdx; i++) {
				T bi =  bufferInfos.get(i);
				if (bi == null)
					continue;
				if(tangents(bi,startFp,endFp)){
					lastMatch=i;
					biList.add(bi);
				}else{
					return biList;
				}
			}
		}else{
		    if(DEBUG){
		        System.out.println(this);
		    }
		}
		return biList;
	}

	
	

	public synchronized boolean isAvailable(long framePosition, int length) {
		long endPosition = framePosition + length;

		for (int i = 0; i < this.length; i++) {
			T bi = bufferInfos.get(i);
			if (bi == null)
				continue;
			long biFramePos = bi.getFramePosition();
			//int len = bi.getFrameLength();
			if (framePosition >= biFramePos
					&& endPosition <= biFramePos + bi.getFrameLength())
				return true;
		}
		return false;
	}
	
	public String toString(){
	    StringBuffer sb=new StringBuffer("Buffer info array: idx pos: "+position+", length: "+length+", last match: "+lastMatch);
	    sb.append("\nwith buffer infos:\n");
	    for(BufferInfo bi:bufferInfos){
	        if(bi!=null){
	            sb.append(bi);
	            sb.append("\n");
	        }
	    }
	    return sb.toString();
	}
}
