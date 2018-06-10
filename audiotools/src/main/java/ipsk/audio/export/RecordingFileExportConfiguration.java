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

package ipsk.audio.export;

import ipsk.beans.PreferredDisplayOrder;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import java.text.DecimalFormat;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFileFormat.Type;
/**
 * Configuration parameters for export of recording file.
 * The class id currently only used by WikiSpeech.
 * @author klausj
 *
 */
@PreferredDisplayOrder("includeOriginalRecordingFiles,includePCMConvertedFiles,includePCMPickedChannelFiles")
@ResourceBundleName("ipsk.audio.Messages")
public class RecordingFileExportConfiguration {
	
	private static final Type DEFAULT_AUDIO_FILE_FORMAT_TYPE = AudioFileFormat.Type.WAVE;

	public static final String CHANNEL_NUMBERFORMAT="00";
	
	private boolean includeOriginalRecordingFiles=true;
	private boolean includePCMConvertedFiles=true;
	private boolean includePCMPickedChannelFiles=true;
	private int[] pickedChannels=null;
	private String channelNumberFormat=CHANNEL_NUMBERFORMAT;
	private AudioFileFormat.Type pcmAudioFileFormatType=DEFAULT_AUDIO_FILE_FORMAT_TYPE;
	
	@ResourceKey("export.includeOriginalRecordingFiles")
	public boolean isIncludeOriginalRecordingFiles() {
		return includeOriginalRecordingFiles;
	}
	public void setIncludeOriginalRecordingFiles(
			boolean includeOriginalRecordingFiles) {
		this.includeOriginalRecordingFiles = includeOriginalRecordingFiles;
	}
	@ResourceKey("export.includePCMConvertedFiles")
	public boolean isIncludePCMConvertedFiles() {
		return includePCMConvertedFiles;
	}
	public void setIncludePCMConvertedFiles(boolean includePCMConvertedFiles) {
		this.includePCMConvertedFiles = includePCMConvertedFiles;
	}
	@ResourceKey("export.includePCMPickedChannelFiles")
	public boolean isIncludePCMPickedChannelFiles() {
		return includePCMPickedChannelFiles;
	}
	public void setIncludePCMPickedChannelFiles(boolean includePCMPickedChannelFiles) {
		this.includePCMPickedChannelFiles = includePCMPickedChannelFiles;
	}
	public int[] getPickedChannels() {
		return pickedChannels;
	}
	public void setPickedChannels(int[] pickedChannels) {
		this.pickedChannels = pickedChannels;
	}
	public String getChannelNumberFormat() {
		return channelNumberFormat;
	}
	public void setChannelNumberFormat(String channelNumberFormat) {
		this.channelNumberFormat = channelNumberFormat;
	}
	public AudioFileFormat.Type getPcmAudioFileFormatType() {
		return pcmAudioFileFormatType;
	}
	public void setPcmAudioFileFormatType(
			AudioFileFormat.Type pcmAudioFileFormatType) {
		this.pcmAudioFileFormatType = pcmAudioFileFormatType;
	}
	
}
