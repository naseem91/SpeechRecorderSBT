//    IPS Java Audio Tools
// 	  (c) Copyright 2014
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

package ipsk.io;

/**
 * @author klausj
 *
 */
public class ChannelRouting {
	
	/**
	 * @param assignment
	 */
	public ChannelRouting(Integer[] assignment) {
		this(null,assignment);
	}
	
	/**
	 * @param srcChannels
	 * @param assignment
	 */
	public ChannelRouting(Integer srcChannels, Integer[] assignment) {
		super();
		if(srcChannels!=null){
			this.srcChannels = srcChannels;
		}
		this.assignment = assignment;
		if(assignment!=null){
			for(int trgCh=0;trgCh<assignment.length;trgCh++){
				int as=assignment[trgCh];
				if(srcChannels!= null){
					if(as>=srcChannels){
						throw new IllegalArgumentException("Target channel index "+trgCh+" cannot be assigned to source channel index "+as+". Index outside source channel count "+srcChannels);
					}
				}else{
					if(as>=this.srcChannels){
						this.srcChannels=as+1;
					}
				}
			}
		}
	}
	
	public ChannelRouting(boolean sourceOffset,int channelOffset, int channels) {
		super();
		if(sourceOffset){
			srcChannels=channelOffset+channels;
			assignment=new Integer[channels];
			for(int trgCh=0;trgCh<channels;trgCh++){
				assignment[trgCh]=channelOffset+trgCh;
			}
			
		}else{
			this.srcChannels = channels;
			int trgChannels=channelOffset+channels;
			assignment=new Integer[trgChannels];
			for(int trgCh=0;trgCh<trgChannels;trgCh++){
				if(trgCh<channelOffset){
					assignment[trgCh]=null;
				}else{
					assignment[trgCh]=trgCh-channelOffset;
				}
			}
		}

	}
	
	private int srcChannels=0;
	
	/**
	 * @return the trgChannels
	 */
	public int getTrgChannels() {
		if(assignment!=null){
			return assignment.length;
		}else{
			return srcChannels;
		}
	}

	/**
	 * @return the srcChannels
	 */
	public int getSrcChannels() {
		return srcChannels;
	}
	/**
	 * @return the assignment
	 */
	public Integer[] getAssignment() {
		return assignment;
	}
	private Integer[] assignment;
	
	

	
}
