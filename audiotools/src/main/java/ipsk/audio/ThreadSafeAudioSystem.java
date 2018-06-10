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

package ipsk.audio;


import ipsk.audio.samples.SampleManager;


import ipsk.util.services.ServicesInspector;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.spi.AudioFileReader;
import javax.sound.sampled.spi.AudioFileWriter;
import javax.sound.sampled.spi.FormatConversionProvider;

/**
 * @author klausj
 *
 */
public class ThreadSafeAudioSystem{
	
	private static final boolean DEBUG=false;
	private static final int DEF_BUF_SIZE=2048;
	
	private static boolean enabled=true;
	private static String[] READER_TESTFILES={"onesample/one_sample.ogg","onesample/one_sample.flac","onesample/one_sample.mp3"};
	
	// WaveFloatFileReader is grey listed because it does not close the file handle on getAudioInputStream(File f)
	// which could end up in too many files open error of the application !!
	private static String[] PLUGIN_GREY_LIST={"com.sun.media.sound.WaveFloatFileReader"};
	
	private static ServicesInspector<AudioFileReader> audioFileReaderInspector;
	private static ServicesInspector<AudioFileWriter> audioFileWriterInspector;
	private static ServicesInspector<FormatConversionProvider> formatConversionProviderInspector;
	private static List<Class<? extends FormatConversionProvider>> formatConversionProviderList=null;
	private static List<Class<? extends AudioFileReader>> audioFileReaderClassList=null;
	private static List<Class<? extends AudioFileWriter>> audioFileWriterClassList=null;
//	private static List<Class<AudioFileReader>> audioFileReaderList;
	public static int NOT_SPECIFIED=AudioSystem.NOT_SPECIFIED;
	
//	private ClassLoader classLoader=null;
	
	public static synchronized void reload(){
		audioFileReaderInspector=new ServicesInspector<AudioFileReader>(AudioFileReader.class);
		try {
			List<Class<? extends AudioFileReader>>audioFileReaderClassListOrg = audioFileReaderInspector.getServiceImplementorClasses(true);
			List<Class<? extends AudioFileReader>>audioFileReaderClassNotGreyList=new ArrayList<Class<? extends AudioFileReader>>();
			audioFileReaderClassList=new ArrayList<Class<? extends AudioFileReader>>();
			
			for(Class<? extends AudioFileReader> afrCl:audioFileReaderClassListOrg){
				String afrClNm=afrCl.getName();
				// Move grey listed plugins to the beginning (for trial/error lookup to the end)
				for(String plgGreyNm:PLUGIN_GREY_LIST){
					if(plgGreyNm.equals(afrClNm)){
						audioFileReaderClassList.add(afrCl);
						
					}else{
						audioFileReaderClassNotGreyList.add(afrCl);
					}
				}
			}
			
			audioFileReaderClassList.addAll(audioFileReaderClassNotGreyList);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
//		for(Class<AudioFileReader> afrCl:audioFileReaderClassList){
//			if(DEBUG)System.out.println("AudioFileReader: "+afrCl.getName());
//			try {
//				AudioFileReader afr=afrCl.newInstance();
//
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			}
//
//		}

		formatConversionProviderInspector=new ServicesInspector<FormatConversionProvider>(FormatConversionProvider.class);
		try {
			formatConversionProviderList = formatConversionProviderInspector.getServiceImplementorClasses(true);

		} catch (IOException e) {
			
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		audioFileWriterInspector=new ServicesInspector<AudioFileWriter>(AudioFileWriter.class);
		try {
			audioFileWriterClassList = audioFileWriterInspector.getServiceImplementorClasses(true);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
//		for(Class<AudioFileWriter> afwCl:audioFileWriterClassList){
//			if(DEBUG)System.out.println("AudioFileWriter: "+afwCl.getName());
//			try {
//				AudioFileWriter afw=afwCl.newInstance();
//
//			} catch (InstantiationException e) {
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			}
//
//		}

	}
	
	
	
	
//	public synchronized static ThreadSafeAudioSystem getInstance() {
//		if(_instance==null){
//			_instance=new ThreadSafeAudioSystem();
//		}
//		return _instance;
//	}
//	
	
	public static AudioFileFormat.Type[] getAudioFileTypes(){
		return AudioSystem.getAudioFileTypes();
	}
	
	public static AudioFileFormat.Type[] getAudioFileReaderTypes(){
		Hashtable<String,AudioFileFormat.Type> types=new Hashtable<String,AudioFileFormat.Type>();
		AudioFileFormat.Type[] fcTypes=getAudioFileTypes();
		
		for(AudioFileFormat.Type fcType:fcTypes){
			if(DEBUG)System.out.println("Ext: "+fcType.getExtension());
			types.put(fcType.toString(),fcType);
		}
		// SND format is not in this list.
		types.put(AudioFileFormat.Type.SND.toString(), AudioFileFormat.Type.SND);
		types.put(AudioFileFormat.Type.AIFC.toString(), AudioFileFormat.Type.AIFC);
		for(String readerTestFile:READER_TESTFILES){
			URL testUrl=SampleManager.class.getResource(readerTestFile);
			try {
				AudioFileFormat testAff=getAudioFileFormat(testUrl);
				if(testAff!=null){
					AudioFileFormat.Type affT=testAff.getType();
					types.put(affT.toString(),affT);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedAudioFileException e) {
				continue;
			}
		}
		AudioFileFormat.Type[] rTypes=new AudioFileFormat.Type[types.size()];
		int i=0;
		for(Entry<String, Type> e:types.entrySet()){
			rTypes[i++]=e.getValue();
		}
		return rTypes;
		
		
	}
	
	public static boolean _write(AudioInputStream stream, Type fileType, File out) throws IOException {
		if(audioFileWriterClassList!=null){
			
//			for(Class<AudioFileWriter> afwClass:audioFileWriterClassList){
			for(int i=audioFileWriterClassList.size()-1;i>=0;i--){
				Class<? extends AudioFileWriter> afwClass=audioFileWriterClassList.get(i);
				try {
					AudioFileWriter afw=afwClass.newInstance();
//					if(afw.isFileTypeSupported(fileType){
						afw.write(stream, fileType, out);
						return true;
//					}
				} catch (InstantiationException e) {
					continue;
				} catch (IllegalAccessException e) {
					continue;
				} catch (IllegalArgumentException e) {
					continue;
				}
			}
			}
		return false;
	}

	public static void write(AudioInputStream stream, Type fileType, File out) throws IOException,IllegalArgumentException {
		if(enabled){
			boolean success=_write(stream, fileType, out);
			if(!success){
				reload();
				success=_write(stream, fileType, out);
			}
			if(!success){
				AudioSystem.write(stream, fileType, out);
				//throw new IllegalArgumentException();
			}
		}else{
			AudioSystem.write(stream, fileType, out);
		}
	}
	
	private static AudioInputStream _getAudioInputStream(URL url) throws IOException {
		if(audioFileReaderClassList!=null){
			// search beginning at the end to get "overloaded" providers first
//		for(Class<AudioFileReader> afrClass:audioFileReaderClassList){
			for(int i=audioFileReaderClassList.size()-1;i>=0;i--){
				Class<? extends AudioFileReader> afrClass=audioFileReaderClassList.get(i);
			try {
//				String pkgName=afrClass.getPackage().getName();
//				SecurityManager sm=System.getSecurityManager();
//				try{
//				sm.checkPackageAccess(pkgName);
//				}catch(SecurityException se){
//					if(DEBUG)System.err.println("Permission denied to load "+afrClass.getName());
//					continue;
//				}
				AudioFileReader afr=afrClass.newInstance();
				AudioInputStream ais=afr.getAudioInputStream(url);
				if(ais!=null){
					return ais;
				}
			} catch (InstantiationException e) {
//				if(DEBUG)e.printStackTrace();
				continue;
			} catch (IllegalAccessException e) {
//				if(DEBUG)e.printStackTrace();
				continue;
			} catch (UnsupportedAudioFileException e) {
				continue;
			}
		}
		}
	return null;
	}
	public static AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException,IOException {
		if(enabled){
		AudioInputStream ais=_getAudioInputStream(url);
		if(ais==null){
			reload();
			ais=_getAudioInputStream(url);
		}
		if(ais==null){
		throw new UnsupportedAudioFileException();
		}else{
			return ais;
		}
		}else{
			return AudioSystem.getAudioInputStream(url);
	}
	}
	
	private static AudioInputStream _getAudioInputStream(File file) throws IOException {
		if(audioFileReaderClassList!=null){
			
//		for(Class<AudioFileReader> afrClass:audioFileReaderClassList){
			for(int i=audioFileReaderClassList.size()-1;i>=0;i--){
				Class<? extends AudioFileReader> afrClass=audioFileReaderClassList.get(i);
//				if("com.sun.media.sound.WaveFloatFileReader".equals(afrClass.getName())){
//					continue;
//				}
			try {
				AudioFileReader afr=afrClass.newInstance();
				AudioInputStream ais=afr.getAudioInputStream(file);
				if(ais!=null){
					return ais;
				}
			} catch (InstantiationException e) {
				continue;
			} catch (IllegalAccessException e) {
				continue;
			} catch (UnsupportedAudioFileException e) {
				continue;
			}
		}
		}
	return null;
	}
	/**
	 * @see AudioSystem#getAudioInputStream(File) 
	 */
	public static AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {
		if(enabled){
		AudioInputStream ais=_getAudioInputStream(file);
		if(ais==null){
			reload();
			ais=_getAudioInputStream(file);
		}
		if(ais==null){
		throw new UnsupportedAudioFileException();
		}else{
			return ais;
		}
		}else{
			return AudioSystem.getAudioInputStream(file);
		}
	}


	private static AudioInputStream _getAudioInputStream(InputStream is) throws IOException {
		if(audioFileReaderClassList!=null){
//		for(Class<AudioFileReader> afrClass:audioFileReaderClassList){
			
			// accoridg to Java SE documentation the AudioFileReaders have to reset the (tested) stream
			// if they do not support the format

			// Bug in jflac-1.2 :  FLACAudioFileReader does not mark/reset the stream
			
			for(int i=audioFileReaderClassList.size()-1;i>=0;i--){
				Class<? extends AudioFileReader> afrClass=audioFileReaderClassList.get(i);
			try {
				AudioFileReader afr=afrClass.newInstance();
//				if(is.markSupported()){
//					is.mark(DEF_BUF_SIZE);
//				}
				AudioInputStream ais=afr.getAudioInputStream(is);
				if(ais!=null){
					return ais;
				}
//				if(is.markSupported()){
//					is.reset();
//				}
			} catch (InstantiationException e) {
//				if(is.markSupported()){
//					is.reset();
//				}
				continue;
			} catch (IllegalAccessException e) {
//				if(is.markSupported()){
//					is.reset();
//				}
				continue;
			} catch (UnsupportedAudioFileException e) {
//				if(is.markSupported()){
//					is.reset();
//				}
				continue;
			}
		}
		}
	return null;
	}
	/**
	 * @see AudioSystem#getAudioInputStream(InputStream)
	 */
	public static AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException {
		if(enabled){
			AudioInputStream ais=_getAudioInputStream(stream);
		
		if(ais==null){
			reload();
			ais=_getAudioInputStream(stream);
		}
		if(ais==null){
//			throw new UnsupportedAudioFileException();
			return AudioSystem.getAudioInputStream(stream);
		}else{
			return ais;
		}
	}else{
		return AudioSystem.getAudioInputStream(stream);
	}
	}


	private static AudioInputStream _getAudioInputStream(Encoding targetEncoding,
			AudioInputStream sourceStream) {
		
		AudioFormat srcFormat=sourceStream.getFormat();
		Encoding srcEncoding=srcFormat.getEncoding();
		if(srcEncoding.equals(targetEncoding)){
			return sourceStream;
		}
		if(formatConversionProviderList!=null){
//		for(Class<FormatConversionProvider> fcpClass:formatConversionProviderList){
			for(int i=formatConversionProviderList.size()-1;i>=0;i--){
				Class<? extends FormatConversionProvider> fcpClass=formatConversionProviderList.get(i);
			try {
				FormatConversionProvider fcp=fcpClass.newInstance();
				Encoding[] targetEncodings=fcp.getTargetEncodings(srcFormat);
				for(Encoding e:targetEncodings){
					if(e.equals(targetEncoding)){
						AudioInputStream ais=fcp.getAudioInputStream(targetEncoding, sourceStream);
						if(DEBUG)System.out.println("Using FormatConversionProvider: "+fcpClass.getName());
						return ais;
					}
				}
			} catch (InstantiationException e) {
				continue;
			} catch (IllegalAccessException e) {
				continue;
			}
		}
		}
		return null;
	}
	
	/**
  	 * @see AudioSystem#getAudioInputStream(AudioFormat.Encoding, AudioInputStream)
	 */
	public static AudioInputStream getAudioInputStream(Encoding targetEncoding,
			AudioInputStream sourceStream) {
		if(enabled){
		AudioInputStream ais=_getAudioInputStream(targetEncoding, sourceStream);
		if(ais==null){
			reload();
			ais=_getAudioInputStream(targetEncoding, sourceStream);
		}
		if(ais==null){
//			throw new IllegalArgumentException();
			return AudioSystem.getAudioInputStream(targetEncoding, sourceStream);
		}else{
			return ais;
		}
		}else{
			return AudioSystem.getAudioInputStream(targetEncoding, sourceStream);
		}
	}

	private static AudioFileFormat _getAudioFileFormat(URL url) throws IOException {
		if(audioFileReaderClassList!=null){
//			for(Class<AudioFileReader> afrClass:audioFileReaderClassList){
				for(int i=audioFileReaderClassList.size()-1;i>=0;i--){
					Class<? extends AudioFileReader> afrClass=audioFileReaderClassList.get(i);
				try {
					AudioFileReader afr=afrClass.newInstance();
					AudioFileFormat aff=afr.getAudioFileFormat(url);
					if(aff!=null){
						return aff;
					}
				} catch (InstantiationException e) {
					continue;
				} catch (IllegalAccessException e) {
					continue;
				} catch (UnsupportedAudioFileException e) {
					continue;
				}
			}
		}
		return null;
	}
	
	public static AudioFileFormat getAudioFileFormat(URL url) throws IOException, UnsupportedAudioFileException {
		if(enabled){
		AudioFileFormat aff=_getAudioFileFormat(url);
		if(aff==null){
			//not found, try reload
			reload();
			aff=_getAudioFileFormat(url);
		}
		if(aff==null){
//			throw new UnsupportedAudioFileException();
			return AudioSystem.getAudioFileFormat(url);
		}else{
			return aff;
		}
		}else{
			return AudioSystem.getAudioFileFormat(url);
	}
	}
	
	private static AudioFileFormat _getAudioFileFormat(File file) throws IOException {
		if(audioFileReaderClassList!=null){
//			for(Class<AudioFileReader> afrClass:audioFileReaderClassList){
				for(int i=audioFileReaderClassList.size()-1;i>=0;i--){
					Class<? extends AudioFileReader> afrClass=audioFileReaderClassList.get(i);
				try {
					AudioFileReader afr=afrClass.newInstance();
					AudioFileFormat aff=afr.getAudioFileFormat(file);
					if(aff!=null){
						return aff;
					}
				} catch (InstantiationException e) {
					continue;
				} catch (IllegalAccessException e) {
					continue;
				} catch (UnsupportedAudioFileException e) {
					continue;
				}
			}
		}
		return null;
	}
	
	public static AudioFileFormat getAudioFileFormat(File file) throws IOException, UnsupportedAudioFileException {
		if(enabled){
		AudioFileFormat aff=_getAudioFileFormat(file);
		if(aff==null){
			reload();
			aff=_getAudioFileFormat(file);
		}
		if(aff==null){
//			throw new UnsupportedAudioFileException();
			return AudioSystem.getAudioFileFormat(file);
		}else{
			return aff;
		}
		}else{
			return AudioSystem.getAudioFileFormat(file);
		}
	}




	public static boolean isEnabled() {
		return enabled;
	}




	public static void setEnabled(boolean e) {
		enabled = e;
	}


	

}
