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
 * Date  : Aug 9, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;



/**
 * Stores a chain of audio plugins. The chain begins with an AudioSource.
 * AudioPlugin's may be appended to the chain. The last AudioPlugin
 * implements the AudioSource of this class.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
public class PluginChain implements TransferableAudioSource {

	public static final DataFlavor DATA_FLAVOR=new DataFlavor(PluginChain.class, "Audio Plugin Chain");
	private AudioSource source;

	private Vector<AudioPlugin> plugins;
	
	private Long frameLengthObj=null;

	
	private static final DataFlavor[] dataFlavors = new DataFlavor[] {
			AudioSource.DATA_FLAVOR,
			 DATA_FLAVOR};

	private AudioFormat format;
	
	private boolean valid=true;

	/**
	 * Build a new plugin chain with the given audio source.
	 * 
	 * @param source
	 *            the audio source of the chain
	 */
	public PluginChain(AudioSource source) {
		this.source = source;

		plugins = new Vector<AudioPlugin>();

	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		PluginChain clone = new PluginChain(source);
		clone.plugins = (Vector<AudioPlugin>) plugins.clone();
		return clone;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.AudioSource#getAudioInputStream()
	 */
	public AudioInputStream getAudioInputStream() throws AudioSourceException {
		if(!valid){
			throw new AudioSourceException("Plugin chain data invalid !");
		}
		AudioInputStream ais = source.getAudioInputStream();
		for (int i = 0; i < plugins.size(); i++) {
			AudioPlugin ap = (AudioPlugin) plugins.get(i);

			try {
				ais = ap.getAudioInputStream(ais);
			} catch (AudioPluginException e) {
				throw new AudioSourceException(e);
			}
		}
		frameLengthObj=new Long(ais.getFrameLength());
        format=ais.getFormat();
		return ais;
	}

	private void getSourceFormat() {

		// try to get source format
		AudioInputStream ais = null;
		format = null;
		try {
			ais = source.getAudioInputStream();
			format = ais.getFormat();
		} catch (AudioSourceException e) {
			// a plugin chain could exist without a functional audio source, so
			// we
			// ctach this exception and set the audio format unknown
		} finally {
			if (ais != null)
				try {
					ais.close();
				} catch (IOException e1) {
					// O.K. No chance.
				}
		}
	}

	/**
	 * Add (append) new plugin. The plugin must be able to handle the current
	 * format.
	 * 
	 * @param newPlugin
	 *            plugin to append
	 * @throws AudioFormatNotSupportedException
	 *             if the plugin cannot handle the current format
	 */
	public synchronized void add(AudioPlugin newPlugin)
			throws AudioFormatNotSupportedException {
		if (format == null && plugins.size() == 0) {
			getSourceFormat();
		}
		if (format != null) {
			AudioFormat apFormat = newPlugin.getInputFormat();
			if (apFormat == null) {
				newPlugin.setInputFormat(format);
			} else {
				if (!apFormat.matches(format))
					throw new AudioFormatNotSupportedException(apFormat);
			}
		}
		format = newPlugin.getOutputFormat();
		plugins.add(newPlugin);
		frameLengthObj=null;
		return;
	}

	/**
	 * Get plugin at index i;
	 * 
	 * @param i
	 *            index
	 * @return plugin at i
	 */
	public AudioPlugin get(int i) {
		return (AudioPlugin) plugins.get(i);
	}


	
	public AudioPlugin removeLast(){
	    AudioPlugin lastPlugin=(AudioPlugin)plugins.remove(plugins.size() -1);
	    format = ((AudioPlugin)plugins.lastElement()).getOutputFormat();
		frameLengthObj=null;
	    return lastPlugin;
	}

	/**
	 * Get number of plugins in chain.
	 * 
	 * @return number of plugins
	 */
	public int size() {
		return plugins.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.datatransfer.Transferable#getTransferDataFlavors()
	 */
	public DataFlavor[] getTransferDataFlavors() {
		if(valid){
		return dataFlavors;
		}else{
			return new DataFlavor[0];
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.datatransfer.Transferable#isDataFlavorSupported(java.awt.datatransfer.DataFlavor)
	 */
	public boolean isDataFlavorSupported(DataFlavor arg0) {
		if(!valid)return false;
		for (int i = 0; i < dataFlavors.length; i++) {
			if (arg0.equals(dataFlavors[i])) {
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.datatransfer.Transferable#getTransferData(java.awt.datatransfer.DataFlavor)
	 */
	public Object getTransferData(DataFlavor arg0)
			throws UnsupportedFlavorException, IOException {
		if(!valid){
			throw new IOException("Transfer data is invalid !");
		}
		if (isDataFlavorSupported(arg0))
			return this;
		else
			return null;
	}

	/**
	 * Get audio format of last plugin of this chain.
	 * 
	 * @return audio format of this chain
	 * @throws AudioSourceException 
	 */
	public AudioFormat getFormat() throws AudioSourceException {
        if (format==null){
               AudioInputStream dummyStream=getAudioInputStream();
               try {
                dummyStream.close();
            } catch (IOException e) {
              throw new AudioSourceException(e);
            }
           }
        
		return format;
	}

    /* (non-Javadoc)
     * @see ipsk.audio.AudioSource#getFrameLength()
     */
    public long getFrameLength() throws AudioSourceException {
       if (frameLengthObj==null){
           AudioInputStream dummyStream=getAudioInputStream();
           try {
            dummyStream.close();
        } catch (IOException e) {
          throw new AudioSourceException(e);
        }
       }
       if(frameLengthObj==null)return ThreadSafeAudioSystem.NOT_SPECIFIED;
       return frameLengthObj.longValue();
    }
    
    /**
     * Returns used (involved) audio files.
     * @return array of used files.
     */
    public File[] getUsedAudioFiles(){
    	ArrayList<File> afList=new ArrayList<File>();
    	if(source instanceof FileAudioSource){
    		afList.add(((FileAudioSource)source).getFile());
    	}
    	for (AudioPlugin ap : plugins) {
			if (ap instanceof SourcePlugin) {
				AudioSource as = ((SourcePlugin) ap).getAudioSource();
				if (as instanceof FileAudioSource) {
					afList.add(((FileAudioSource) source).getFile());
				}
			}
		}
    	return afList.toArray(new File[0]);
    }

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
  
}
