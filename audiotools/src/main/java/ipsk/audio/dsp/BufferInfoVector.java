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

import java.util.Vector;

public class BufferInfoVector {

	private Vector<BufferInfo> bufferInfos;

	public BufferInfoVector() {
		super();
		bufferInfos = new Vector<BufferInfo>();
	}

	public void add(BufferInfo bi) {
		bufferInfos.add(bi);
		//System.out.println(bufferInfos.size());
	}

	public boolean remove(BufferInfo bi) {
		return bufferInfos.remove(bi);
	}

	public void clear() {
		bufferInfos.clear();
	}

	public BufferInfo getBufferInfoAtFramePosition(long framePosition) {
		BufferInfo retVal = null;
		synchronized (bufferInfos) {
			int size = bufferInfos.size();
			for (int i = 0; i < size; i++) {
				BufferInfo bi = (BufferInfo) bufferInfos.get(i);
				long biFramePos = bi.getFramePosition();
				if (framePosition >= biFramePos
						&& framePosition < biFramePos + bi.getFrameLength())
					retVal = bi;
				break;
			}
		}
		return retVal;
	}

	public boolean isAvailable(long framePosition, int length) {
		long endPosition = framePosition + length;
		boolean retVal = false;
		synchronized (bufferInfos) {
			int size = bufferInfos.size();
			for (int i = 0; i < size; i++) {
				BufferInfo bi = (BufferInfo) bufferInfos.get(i);
				long biFramePos = bi.getFramePosition();
				//int len = bi.getFrameLength();
				if (framePosition >= biFramePos
						&& endPosition <= biFramePos + bi.getFrameLength())
					retVal = true;
				break;
			}
		}
		return retVal;
	}
}
