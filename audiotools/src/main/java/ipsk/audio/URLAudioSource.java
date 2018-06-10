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
 * Date  : Aug 6, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;

import javax.sound.sampled.AudioInputStream;

/**
 * An URL based audio source.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class URLAudioSource extends BasicAudioSource implements AudioSource {

    private URL url;


    /**
     * Create new audio source from URL.
     * 
     * @param url
     *            the audio file
     */
    public URLAudioSource(URL url) {
        this.url = url;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ipsk.audio.AudioSource#getAudioInputStream()
     */
    public AudioInputStream getAudioInputStream() throws AudioSourceException {
        AudioInputStream ais=null;
        // The URL audio input stream of JavaSound does not work well with
        // multiple channel and/or odd frame sizes
        // so I try to get an file based stream if the URL is file based
        // Klaus J. 
        
        try {
            if(url.getProtocol().equalsIgnoreCase("file")){
                
                String filePath=URLDecoder.decode(url.getPath(),"UTF-8");
                // TODO Better?
                // String filePath=url.toURI().getPath();
                ais=ThreadSafeAudioSystem.getAudioInputStream(new File(filePath));
            }else{
                ais=ThreadSafeAudioSystem.getAudioInputStream(url);
            }
        } catch (Exception e) {
            throw new AudioSourceException(e);
        }
        setAudioFormat(ais.getFormat());
        setFrameLength(ais.getFrameLength());
        return ais;
    }

    /**
     * Returns the audio source URL.
     * 
     * @return URL
     */
    public URL getUrl() {
        return url;
    }

    /**
     * Set a new audio source URL.
     * 
     * @param url
     *            new audio source
     */
    public void setUrl(URL url) {
        this.url = url;
    }
}
