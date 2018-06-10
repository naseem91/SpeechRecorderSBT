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
import java.util.Locale;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFormat.Encoding;

public class EncoderTest extends Thread{

	private File audioFile;
	private Encoding encoding;
	private AudioFileFormat.Type targetType;
	
	public EncoderTest(File audioFile,Encoding encoding,AudioFileFormat.Type targetType){
		super();
		this.audioFile=audioFile;
		this.encoding=encoding;
		this.targetType=targetType;
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
			
			
			String srcFilename=audioFile.getAbsolutePath();
			String extension =aff.getType().getExtension();
			String extUpperCase=extension.toUpperCase(Locale.ENGLISH);
			String extLowerCase=extension.toLowerCase(Locale.ENGLISH);
			String extPattern="";
			for(int i=0;i<extension.length();i++){
				extPattern=extPattern.concat("["+extLowerCase.substring(i,i)+extUpperCase.substring(i,i)+"]");
			}
			String regex=extPattern+"$"; 
			String trgFilename=srcFilename.replaceFirst(regex,targetType.getExtension());
			if(srcFilename.equals(trgFilename)){
				throw new IOException("Could not create target file name by replacing extension");
			}
			
			File testFile=new File(trgFilename);
			if(testFile.exists()){
				throw new IOException("Target file "+testFile+" already exists");
			}
			
			// OK start encoding
			ais=AudioSystem.getAudioInputStream(audioFile);
			AudioInputStream decAis=AudioSystem.getAudioInputStream(encoding, ais);
			AudioSystem.write(decAis, targetType, testFile);
			
		} catch (UnsupportedAudioFileException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}
	
}
