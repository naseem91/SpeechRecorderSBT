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

package ipsk.audio.sampled.spi.test;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

public class DecoderTest extends Thread{

	private File audioFile;
	public DecoderTest(File audioFile){
		super();
		this.audioFile=audioFile;
		
		
	}
	
	public void run(){
//		try {
//			Thread.sleep((long)(Math.random()*21));
//		} catch (InterruptedException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
		AudioFileFormat aff;
		try {
			//aff = AudioSystemWrapper.getAudioFileFormat(audioFile);
			aff = AudioSystem.getAudioFileFormat(audioFile);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		System.out.println(aff);
		AudioInputStream ais=null;
		try {
			
			ais=AudioSystem.getAudioInputStream(audioFile);
			//ais=AudioSystemWrapper.getAudioInputStream(audioFile);
			
			// the method getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, ais) seems to be thread safe
			AudioInputStream decAis=AudioSystem.getAudioInputStream(AudioFormat.Encoding.PCM_SIGNED, ais);
			File testFile=File.createTempFile("test", "wav");
			testFile.deleteOnExit();
			AudioSystem.write(decAis, AudioFileFormat.Type.WAVE, testFile);
			testFile.delete();
			System.out.println("Decoded successfully.");
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
}
