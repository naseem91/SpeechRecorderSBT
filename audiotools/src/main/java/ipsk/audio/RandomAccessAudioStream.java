//    IPS Java Audio Tools
//    (c) Copyright 2009-2015
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

package ipsk.audio;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;

public class RandomAccessAudioStream {
	
	public static final boolean DEBUG=false;
	
	protected AudioSource audioSource;
	protected AudioInputStream audioInputStream=null;
    protected long framePos=0;
    protected int frameSize;
    
    public RandomAccessAudioStream(AudioSource audioSource){
    	this.audioSource=audioSource;
    	
    }
    
	public int readFrames(byte[] buf,int offset,int len) throws AudioSourceException{
		getStream();
		int readBytes;
		try {
			readBytes = audioInputStream.read(buf,offset*frameSize,len*frameSize);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AudioSourceException(e);
		}
		if (readBytes==-1){
			return readBytes;
		}
		if(readBytes % frameSize!=0){
			throw new AudioSourceException("Could not read multiple bytes of a frame");
		}
		int readFrames=readBytes/frameSize;
		framePos+=readFrames;
		return readFrames;
	}
	
	public int readFrames(long pos,byte[] buf,int offset,int len) throws AudioSourceException{
		setPosition(pos);
		return readFrames(buf,offset,len);
	}
	
	
	private AudioInputStream getStream() throws AudioSourceException{
		if (audioInputStream==null){
			framePos=0;
			audioInputStream=audioSource.getAudioInputStream();
			frameSize=audioInputStream.getFormat().getFrameSize();
		}
		return audioInputStream;
	}
	
	public long getPosition(){
		return framePos;
	}
	public void setPosition(long pos) throws AudioSourceException{
		getStream();
		if (framePos>pos){
			framePos=0;
			try {
				audioInputStream.close();
			} catch (IOException e) {
				throw new AudioSourceException(e);
			}
			if(DEBUG){
				System.out.println("Random access stream position mismatch. Closed stream.");
			}
			audioInputStream=audioSource.getAudioInputStream();
		}
		
		if(pos>framePos){
			long toSkip=frameSize*(pos-framePos);
			while(toSkip>0){
				try {
					toSkip-=audioInputStream.skip(toSkip);
				} catch (IOException e) {
					
					
					e.printStackTrace();
					throw new AudioSourceException(e);
				}
				//Thread.yield();
			}
			framePos=pos;
		}
	}
	public long skipFrames(long framesToSkip) throws AudioSourceException{
		getStream();
		frameSize=audioSource.getFormat().getFrameSize();
		long toSkip=framesToSkip*frameSize;
		long skipped;
			try {
				skipped=audioInputStream.skip(toSkip);
			} catch (IOException e) {
				
				e.printStackTrace();
				throw new AudioSourceException(e);
			}
			if (skipped % frameSize >0)throw new AudioSourceException("could not skip multile bytes of frame");
			long skippedFrames=skipped/frameSize;
			framePos+=skippedFrames;
		return skippedFrames;
	}

	public void close() throws AudioSourceException{
		if (audioInputStream!=null)
			try {
				audioInputStream.close();
			} catch (IOException e) {
				throw new AudioSourceException(e);
			}finally{
				audioInputStream=null;
			}
	}

    public long getFrameLength() throws AudioSourceException {
        return audioSource.getFrameLength();
    }
	
	
}
