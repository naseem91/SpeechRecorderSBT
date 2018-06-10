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

import java.util.HashSet;
import java.util.Set;

import ipsk.beans.PreferredDisplayOrder;
import ipsk.beans.Unit;
import ipsk.beans.dom.DOMAttributes;
import ipsk.beans.dom.DOMElements;
import ipsk.util.PluralResourceKey;
import ipsk.util.ResourceBundleName;
import ipsk.util.ResourceKey;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Audio format.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
@Entity
@Table(name = "audio_format", schema = "public")
@ResourceBundleName("ipsk.db.speech.PropertyNames")
@ResourceKey("audio.format")
@PluralResourceKey("audio.formats")
@PreferredDisplayOrder("name,encoding,sampleRate,channels,*,projects")
@DOMAttributes({})
@DOMElements({"name","encoding","channels","sampleRate","bigEndian","quantisation","frameSize"})
public class AudioFormat {

    private String name = null;
    private String encoding = "PCM_SIGNED";
    private int channels = 2;
    private double sampleRate = 44100;
    private boolean bigEndian = false;
    private int quantisation = 16;
    private int frameSize = 4;
    private Set<Project> projects = new HashSet<Project>(0);

    public AudioFormat() {
    }
    
    /**
     * set the name of the audi format. Something like 'CD quality'.
     * @param string
     */
    public void setName(String string) {
        name = string;
    }
    /**
    * Get name of audio format.
    */
    @Id
    @Column(name = "name", unique = true, nullable = false, updatable = false, length = 100)
    @ResourceKey("name")
    public String getName() {
        return name;
    }
    
    /**
     * Encoding name. JavaSound terms should be used. ('PCM_SIGNED')
     * @return encoding as string
     */
    @Column(name = "encoding", updatable = false, length = 100)
    @ResourceKey("encoding")
    public String getEncoding() {
        return encoding;
    }
    
    public void setEncoding(String encoding) {
        this.encoding=encoding;
    }

    /**
     * Number of channels.
     * @return channels
     */
    @Column(name = "channels", updatable = false)
    @ResourceKey("channels")
    public int getChannels() {
        return channels;
    }

    /**
     * Framesize in bytes of one audio frame.
     * For PCM encoding this is equal to the size of one sample multiplied by the number of channels.
     * @return frame size in bytes
     */
    @Column(name = "framesize", updatable = false)
    @ResourceKey("framesize")
    public int getFrameSize() {
        return frameSize;
    }

    /**
     * Get samplerate.
     * @return samplerate in Hz
     */
    @Column(name = "samplerate", updatable = false)
    @ResourceKey("samplerate")
    @Unit("Hz")
    public double getSampleRate() {
        return sampleRate;
    }

    /**
     * Get quantisation (sample size in bits), e.g. 16 for CD-DA Quality.
     * @return quantisation
     */
    @Column(name = "quantisation", updatable = false)
    @ResourceKey("quantisation")
    public int getQuantisation() {
        return this.quantisation;
    }
    
    @Column(name = "bigendian", updatable = false)
    @ResourceKey("bigendian")
    public boolean getBigEndian() {
        return bigEndian;
    }

    /**
     * Set to true if the byte order is big endian.
     * @param b
     */
    
    public void setBigEndian(boolean b) {
        bigEndian = b;
    }

    /**
     * Set number of channels.
     * @param i
     */
    public void setChannels(int i) {
        channels = i;
    }

    /**
     * Set frame size.
     * @param i
     */
    public void setFrameSize(int i) {
        frameSize = i;
    }

    
    /**
     * Set sample rate in Hz.
     * @param d
     */
    public void setSampleRate(double d) {
        sampleRate = d;
    }

    /**
     * Set quantisation in bits.
     * @param quantisation
     */
    public void setQuantisation(int quantisation) {
        this.quantisation = quantisation;
    }

    @OneToMany( fetch = FetchType.LAZY, mappedBy = "audioFormat")
    @ResourceKey("projects")
    public Set<Project> getProjects() {
        return this.projects;
    }
    
    public void setProjects(Set<Project> projects){
        this.projects=projects;
    }
    
    @Transient
    public String toString(){
        return name;
    }
    

}
