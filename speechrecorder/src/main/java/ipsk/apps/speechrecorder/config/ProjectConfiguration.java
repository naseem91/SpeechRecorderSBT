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

import java.util.UUID;

import ipsk.audio.impl.j2audio2.J2AudioController3;
import ipsk.audio.impl.j2audio2.J2AudioController4;
import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMElements;


/**
 * Speechrecorder project configuration.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
@DOMElements({"name","uuid","description","directory","editable","audioControllerClass","recordingMixerName","playbackMixerName","promptPlaybackMixerName","recordingConfiguration","promptConfiguration","viewConfiguration","speakers","control","viewConfiguration","annotation","cacheConfiguration","loggingConfiguration"})
@DOMAttributes({"version"})
public class ProjectConfiguration {

	public static final String DEFAULT_VERSION= "1.9.2";
	private String name = "";
	private UUID uuid=null;
	private String version = DEFAULT_VERSION;
	private String description = "";
	
	//private Locale locale=new Locale();
	// local projects are editable, web projects should not be editable
	private boolean editable=true;
	private MixerName[] recordingMixerName=null;
	private MixerName[] playbackMixerName=null;
	private MixerName[] promptPlaybackMixerName=null;
	
	private String audioControllerClass = J2AudioController4.class.getName();
	private String directory = "";
	private SpeakersConfiguration speakers;
	private PromptConfiguration prompting;
	private RecordingConfiguration recording;
//	private Annotations annotations;
    private Control control;
//    private ViewConfiguration viewConfiguration;
    private Annotation annotation;
	private CacheConfiguration cacheConfiguration;
	private LoggingConfiguration loggingConfiguration;
	

	/**
	 * @return the annotation
	 */
	public Annotation getAnnotation() {
		return annotation;
	}

	/**
	 * @param annotation the annotation to set
	 */
	public void setAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	public ProjectConfiguration() {
		speakers = new SpeakersConfiguration();
		recording = new RecordingConfiguration();
		prompting = new PromptConfiguration();
//		viewConfiguration=new ViewConfiguration();
        control=new Control();
		cacheConfiguration=new CacheConfiguration();
		loggingConfiguration=new LoggingConfiguration();
		annotation=new Annotation();
	}

	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return version
	 */
	public String getVersion() {
		return version;
	}

	/**
	 * @param string name
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param i version
	 */
	public void setVersion(String i) {
		version = i;
	}

	public RecordingConfiguration getRecordingConfiguration() {
		return recording;
	}

	public void setRecordingConfiguration(RecordingConfiguration rec) {
		recording = rec;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param string description
	 */
	public void setDescription(String string) {
		description = string;
	}

	/**
	 * @return prompting configuration
	 */
	public PromptConfiguration getPromptConfiguration() {
		return prompting;
	}

	/**
	 * @param prompting prompting configuration
	 */
	public void setPromptConfiguration(PromptConfiguration prompting) {
		this.prompting = prompting;
	}

	/**
	 * @return speaker database configuration
	 */
	public SpeakersConfiguration getSpeakers() {
		return speakers;
	}

	/**
	 * @param speakers speaker database configuration
	 */
	public void setSpeakers(SpeakersConfiguration speakers) {
		this.speakers = speakers;
	}

	/**
	 * @return class name of audio controller implementation 
	 */
	public String getAudioControllerClass() {
		return audioControllerClass;
	}

	/**
	 * @param string class name of audio controller implementation
	 */
	public void setAudioControllerClass(String string) {
		audioControllerClass = string;
	}

	/**
	 * @return project directory
	 */
	public String getDirectory() {
		return directory;
	}

	/**
	 * @param string project directory
	 */
	public void setDirectory(String string) {
		directory = string;
	}

	/**
	 * @return true if project is editable by the user
	 */
	public boolean getEditable() {
		return editable;
	}

	/**
	 * @param b true if project is editable by the user
	 */
	public void setEditable(boolean b) {
		editable = b;
	}
	

//	/**
//	 * @return
//	 */
//	public Locale getLocale() {
//		return locale;
//	}
//
//	/**
//	 * @param locale
//	 */
//	public void setLocale(Locale locale) {
//		this.locale = locale;
//	}

	/**
     * Get list of configured playback mixer names (or regular expression patterns) for monitoring
     * @return array of playback device descriptors
     */
	@ipsk.beans.dom.DOMElement(name="playbackMixerName")
	public MixerName[] getPlaybackMixerName() {
		return playbackMixerName;
	}
	

	/**
	 * Get list of configured playback mixer names (or regular expression patterns) for audio prompts 
	 * @return the promptPlaybackMixerName
	 */
	@ipsk.beans.dom.DOMElement(name="promptPlaybackMixerName")
	public MixerName[] getPromptPlaybackMixerName() {
		return promptPlaybackMixerName;
	}


	/**
	 * Get list of configured recording mixer names (or regular expression patterns)
	 * @return array of recording device descriptors
	 */
	@ipsk.beans.dom.DOMElement(name="recordingMixerName")
	public MixerName[] getRecordingMixerName() {
		return recordingMixerName;
	}

	/**
	 * @param playbackMixerNames array of playback device descriptors
	 */
	public void setPlaybackMixerName(MixerName[] playbackMixerNames) {
		playbackMixerName = playbackMixerNames;
	}
	
	/**
	 * @param promptPlaybackMixerName the promptPlaybackMixerName to set
	 */
	public void setPromptPlaybackMixerName(MixerName[] promptPlaybackMixerName) {
		this.promptPlaybackMixerName = promptPlaybackMixerName;
	}


	/**
	 * @param recordingMixerNames array of recording device descriptors
	 */
	public void setRecordingMixerName(MixerName[] recordingMixerNames) {
		recordingMixerName = recordingMixerNames;
	}

	/**
	 * @return upload cache configuration
	 */
	public CacheConfiguration getCacheConfiguration() {
		return cacheConfiguration;
	}

	/**
	 * @param configuration upload cache configuration
	 */
	public void setCacheConfiguration(CacheConfiguration configuration) {
		cacheConfiguration = configuration;
	}

	/**
	 * @return logging configuration
	 */
	public LoggingConfiguration getLoggingConfiguration() {
		return loggingConfiguration;
	}

	/**
	 * @param configuration logging configuration
	 */
	public void setLoggingConfiguration(LoggingConfiguration configuration) {
		loggingConfiguration = configuration;
	}

    /**
     * @return keyboard control configuration
     */
    public Control getControl() {
        return control;
    }

    /**
     * @param transport keyboard control configuration
     */
    public void setControl(Control transport) {
        this.control = transport;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

   
}
