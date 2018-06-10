//    IPS Java Speech Database
//    (c) Copyright 2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Speech Database
//
//
//    IPS Java Speech Database is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Speech Database is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Speech Database.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.db.speech;

import ipsk.beans.PreferredDisplayOrder;
import ipsk.util.PluralResourceKey;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * AudioDevice 
 */
@Entity
@Table(name = "audio_device", schema = "public")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("audio.device")
@PluralResourceKey("audio.devices")
@PreferredDisplayOrder("audioDeviceId,name,regex,isPlayback,*,projects")
public class AudioDevice implements java.io.Serializable {

	// Fields    
	public static final String PLAYBACK_PREFIX="Playback: ";
	public static final String RECORDING_PREFIX="Recording: ";
	public static final String REGEX_STR="(regex)";
	
	// Fields   
	
	private int audioDeviceId;

	

	private String name;

	private boolean playback;

//	private String api;
	
	private boolean regex;

//	private AudioDeviceId id;

	private String jextension;

	private Set<Project> projects = new HashSet<Project>(0);

	// Constructors

	/** default constructor */
	public AudioDevice() {
		super();
	}

//	/** minimal constructor */
//	public AudioDevice(AudioDeviceId id) {
//		this.id = id;
//	}
//
//	/** full constructor */
//	public AudioDevice(AudioDeviceId id, String jextension,
//			Set<Project> projects) {
//		this.id = id;
//		this.jextension = jextension;
//		this.projects = projects;
//	}
	
	

//	// Property accessors
//	@EmbeddedId
//	@ResourceKey("id")
//	public AudioDeviceId getId() {
//		return this.id;
//	}
//
//	public void setId(AudioDeviceId id) {
//		this.id = id;
//	}



	// Property accessors

	@Id
	@Column(name = "audio_device_id", unique = true, nullable = false, updatable = false)
	@GeneratedValue(generator="id_gen")
	@ResourceKey("id")
	public int getAudioDeviceId() {
		return audioDeviceId;
	}

	public void setAudioDeviceId(int audioDeviceId) {
		this.audioDeviceId = audioDeviceId;
	}
	
	@Column(name = "name", length = 500, updatable = false)
	@ResourceKey("name")
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "is_playback", nullable = false, updatable = false)
	@ResourceKey("playback")
	public boolean isPlayback() {
		return this.playback;
	}

	public void setPlayback(boolean playback) {
		this.playback = playback;
	}

//	@Column(name = "api",length = 100)
//	public String getApi() {
//		return this.api;
//	}
//
//	public void setApi(String api) {
//		this.api = api;
//	}
	
	@Column(name = "regex", nullable = false, updatable = false)
	@ResourceKey("regular_expression")
	public boolean isRegex() {
		return regex;
	}

	public void setRegex(boolean regex) {
		this.regex = regex;
	}
	
	@Column(name = "jextension", length = 20, updatable = false)
	@ResourceKey("audio.javasound.extension")
	public String getJextension() {
		return this.jextension;
	}

	public void setJextension(String jextension) {
		this.jextension = jextension;
	}

	@ManyToMany(fetch = FetchType.LAZY, mappedBy = "audioDevices")
	@ResourceKey("projects")
	public Set<Project> getProjects() {
		return this.projects;
	}

	public void setProjects(Set<Project> projects) {
		this.projects = projects;
	}
//	
//	public String toString(){
//		StringBuffer sb=new StringBuffer(getId().toString());
//		String jExt=getJextension();
//		if(jExt!=null){
//			sb.append(", Java-Ext: "+jExt);
//		}
//		return sb.toString();
//	}
	
	public String toString(){
		StringBuffer sb=new StringBuffer();
//		sb.append("ID: ");
//		sb.append(audioDeviceId);
		if(isPlayback()){
			sb.append("Playback: ");
		}else{
			sb.append("Recording: ");
		}
//		sb.append(api);
//		sb.append(", ");
		sb.append(name);
		if(regex){
			sb.append(",");
			sb.append(REGEX_STR);
		}
		String jExt=getJextension();
		if(jExt!=null && ! jExt.equals("")){
			sb.append(", Java-Ext: "+jExt);
		}
		return sb.toString();
	}


}
