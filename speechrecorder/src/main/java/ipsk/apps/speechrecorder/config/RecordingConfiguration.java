//    Speechrecorder
//    (c) Copyright 2009-2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Jun 2, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.config;

import ipsk.audio.capture.PrimaryRecordTarget;
import ipsk.beans.dom.DOMElements;


/**
 * Configuration concerning audio recording.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
@DOMElements({"url","format","channelAssignment","captureScope","primaryRecordTarget","overwrite","overwriteWarning","numLines","labelExtension","mode","seamlessAutorecording","preRecDelay","postRecDelay","progressToNextUnrecorded","resetPeakOnRecording"})
public class RecordingConfiguration {

	public static String MANUAL="manual";
	public static String AUTOPROGRESS="autoprogress";
	public static String AUTORECORDING="autorecording";

	public enum CaptureScope {SESSION,ITEM};

	private Format format;
	private int preRecDelay;
	private int postRecDelay;
	private boolean forcePostRecDelayPhase=false; 
	private CaptureScope captureScope=null;
	

	/**
	 * @return the captureScope
	 */
	public CaptureScope getCaptureScope() {
		return captureScope;
	}


	/**
	 * @param captureScope the captureScope to set
	 */
	public void setCaptureScope(CaptureScope lineOpenScope) {
		this.captureScope = lineOpenScope;
	}


	private PrimaryRecordTarget primaryRecordTarget=PrimaryRecordTarget.DIRECT;
//	private boolean useRawTempFile=false;
	private boolean overwrite;
	private boolean overwriteWarning=true;
	private String mode;
	private boolean progressToNextUnrecorded;
	//private boolean automaticRecording;
	private boolean resetPeakOnRecording;
	private int numLines;
	private String url;
	private String labelExtension;
	private boolean seamlessAutorecording=false;	
//	private ChannelRouting channelAssignment=new ChannelRouting();
	private ChannelRouting channelAssignment=null;
	
	public RecordingConfiguration() {
		super();
		format=new Format();
		overwrite=true;
		//automaticProgress=false;
		progressToNextUnrecorded=false;
		//automaticRecording=false;
		resetPeakOnRecording=true;
		preRecDelay=ipsk.db.speech.Recording.DEF_PRERECDELAY;
		postRecDelay=ipsk.db.speech.Recording.DEF_POSTRECDELAY;
		numLines=1;
		url=new String("file:RECS/");
		labelExtension=new String(".txt");
		mode=MANUAL;
		// default: If an upload cache is used the recordings are temporarily 
		// stored in files to avoid out of memory problems
		// TODo
		// default changed !!
		//cacheInFiles=true;
		
		// TEST channel routing
//		channelAssignment.setRouting(new int[]{1});
	}
	

	/**
	 * @return post recording delay in ms
	 */
	public int getPostRecDelay() {
		return postRecDelay;
	}

	/**
	 * @return pre-recording delay in ms
	 */
	public int getPreRecDelay() {
		return preRecDelay;
	}

	/**
	 * @param d post recording delay in ms
	 */
	public void setPostRecDelay(int d) {
		postRecDelay = d;
		
	}

	/**
	 * @param i pre-recording delay in ms
	 */
	public void setPreRecDelay(int i) {
		preRecDelay = i;
	}
	/**
	 * @return the forcePostRecDelayPhase
	 */
	public boolean isForcePostRecDelayPhase() {
		return forcePostRecDelayPhase;
	}


	/**
	 * @param forcePostRecDelayPhase the forcePostRecDelayPhase to set
	 */
	public void setForcePostRecDelayPhase(boolean forcePostRecDelayPhase) {
		this.forcePostRecDelayPhase = forcePostRecDelayPhase;
	}

	/**
	 * @return recording audio format
	 */
	public Format getFormat() {
		return format;
	}

	/**
	 * @param format recording audio format
	 */
	public void setFormat(Format format) {
		this.format = format;
	}

	/**
	 * @return overwrite existing files
	 */
	public boolean getOverwrite() {
		return overwrite;
	}

	/**
	 * @param b overwrite existing files
	 */
	public void setOverwrite(boolean b) {
		overwrite = b;
	}


	/**
	 * @return recording base directory URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url recording base directory URL
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	
	/**
	 * @return number of recording lines (not channels)
	 */
	public int getNumLines() {
		return numLines;
	}

	/**
	 * @param i number of recording lines (not channels)
	 */
	public void setNumLines(int i) {
		numLines = i;
	}

	
	public String getLabelExtension() {
		return labelExtension;
	}

	public void setLabelExtension(String string) {
		labelExtension = string;
	}


	/**
	 * @return true if peak hold display should be reset at start of each recording
	 */
	public boolean getResetPeakOnRecording() {
		return resetPeakOnRecording;
	}

	/**
	 * @param b true if peak hold display should be reset at start of each recording
	 */
	public void setResetPeakOnRecording(boolean b) {
		resetPeakOnRecording = b;
	}

	
	public boolean getProgressToNextUnrecorded() {
		return progressToNextUnrecorded;
	}

	
	public void setProgressToNextUnrecorded(boolean b) {
		progressToNextUnrecorded = b;
	}

	/**
	 * @return recording mode
	 */
	public String getMode() {
		return mode;
	}

	/**
	 * @param string recording mode
	 */
	public void setMode(String string) {
		mode = string;
	}


//    /**
//     * @param useRawTempFile the useRawTempFile to set
//     */
//    public void setUseRawTempFile(boolean useRawTempFile) {
//        this.useRawTempFile = useRawTempFile;
//    }
//
//
//    /**
//     * @return the useRawTempFile
//     */
//    public boolean isUseRawTempFile() {
//        return useRawTempFile;
//    }


    public PrimaryRecordTarget getPrimaryRecordTarget() {
        return primaryRecordTarget;
    }


    public void setPrimaryRecordTarget(PrimaryRecordTarget primaryRecordTarget) {
        this.primaryRecordTarget = primaryRecordTarget;
    }


    public boolean isOverwriteWarning() {
        return overwriteWarning;
    }


    public void setOverwriteWarning(boolean overwriteWarning) {
        this.overwriteWarning = overwriteWarning;
    }


    public boolean isSeamlessAutorecording() {
        return seamlessAutorecording;
    }


    public void setSeamlessAutorecording(boolean seamlessAutorecording) {
        this.seamlessAutorecording = seamlessAutorecording;
    }
    
    /**
	 * @return the channelAssignment
	 */
	public ChannelRouting getChannelAssignment() {
		return channelAssignment;
	}


	/**
	 * @param channelAssignment the channelAssignment to set
	 */
	public void setChannelAssignment(ChannelRouting channelAssignment) {
		this.channelAssignment = channelAssignment;
	}

//	/**
//	 * @return
//	 */
//	public boolean isCacheInFiles() {
//		return cacheInFiles;
//	}
//
//	/**
//	 * @param b
//	 */
//	public void setCacheInFiles(boolean b) {
//		cacheInFiles = b;
//	}

}
