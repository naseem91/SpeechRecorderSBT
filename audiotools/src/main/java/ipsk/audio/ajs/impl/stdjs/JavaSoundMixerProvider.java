//    IPS Java Audio Tools
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
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

package ipsk.audio.ajs.impl.stdjs;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.spi.MixerProvider;

/**
 * @author klausj
 *
 */
public class JavaSoundMixerProvider extends MixerProvider {

    /* (non-Javadoc)
     * @see javax.sound.sampled.spi.MixerProvider#getMixer(javax.sound.sampled.Mixer.Info)
     */
    @Override
    public Mixer getMixer(Info arg0) {
        return AudioSystem.getMixer(arg0);
    }

    /* (non-Javadoc)
     * @see javax.sound.sampled.spi.MixerProvider#getMixerInfo()
     */
    @Override
    public Info[] getMixerInfo() {
       return AudioSystem.getMixerInfo();
    }

}
