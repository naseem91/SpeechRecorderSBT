//    IPS Java Audio Tools
// 	  (c) Copyright 2015
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.audio.dsp;

import java.util.List;

/**
 * @author klausj
 *
 */
public class BufferLevelInfoArray extends BufferInfoArray<BufferLevelInfo> {

	private final static boolean DEBUG=false;
	/**
	 * 
	 */
	public BufferLevelInfoArray() {
		super();
	}

	/**
	 * @param capacity
	 */
	public BufferLevelInfoArray(int capacity) {
		super(capacity);
	}

	public LevelInfo[] intervalLevelInfos(long fromFramePosition,long toFramePosition){
		LevelInfo[] mergedInfo=null;
		if(DEBUG)System.out.println("Requested interval: "+fromFramePosition+" "+toFramePosition);
		List<BufferLevelInfo> blis=listBufferInfosTangentInterval(fromFramePosition, toFramePosition);
		for(BufferLevelInfo bli:blis){
			LevelInfo[] lis=bli.getLevelInfos();
			if(lis!=null){
				if(mergedInfo==null){
					mergedInfo=new LevelInfo[lis.length];
				}
				if(lis.length==mergedInfo.length){
					for(int i=0;i<lis.length;i++){
						LevelInfo li=lis[i];
						if(li!=null){
							mergedInfo[i]=li.merge(mergedInfo[i]);
						}
					}
				}
			}
		}
		if(DEBUG){
			if(mergedInfo!=null && mergedInfo.length>0){
				System.out.println("Merged: "+mergedInfo[0]);
			}
		}
		return mergedInfo;
	}
	
}
