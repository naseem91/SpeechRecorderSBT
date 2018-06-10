//    IPS Speech database tools
// 	  (c) Copyright 2016
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Speech database tools
//
//
//    IPS Speech database tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Speech database tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Speech database tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Mar 19, 2010
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ips.annot.model;

import java.util.Vector;

import ips.annot.model.db.Bundle;
import ips.annot.model.event.BundleChangedEvent;
import ips.annot.model.event.BundleListener;
import ipsk.audio.AudioSource;
import ipsk.audio.arr.clip.AudioClip;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class AnnotatedAudioClip extends AudioClip {

    private Bundle bundle;
    private Vector<BundleListener> bundleListeners = new Vector<BundleListener>();

    public AnnotatedAudioClip() {
        super();
    }

    public AnnotatedAudioClip(AudioSource audioSource) {
        super(audioSource);
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
        fireBundleEvent(new BundleChangedEvent(this));
    }
    
    public void addBundleListener(BundleListener bundleListener){
        bundleListeners.add(bundleListener);
    }
    public void removeBundleListener(BundleListener bundleListener){
        bundleListeners.remove(bundleListener);
    }

    protected void fireBundleEvent(BundleChangedEvent event){
        for(BundleListener al:bundleListeners){
            al.bundleChanged(event);
        }
    }
}
