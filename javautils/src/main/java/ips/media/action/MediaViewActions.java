//    IPS Java Utils
// 	  (c) Copyright 2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ips.media.action;

import ips.media.MediaLengthUnit;
import ips.media.MediaView;

import ipsk.swing.action.tree.ActionFolder;
import ipsk.swing.action.tree.ActionGroup;
import ipsk.swing.action.tree.RadioActionGroup;
import ipsk.swing.action.tree.RadioActionLeaf;
import ipsk.text.MediaTimeFormat;
import ipsk.text.TimeFormat;
import ipsk.util.LocalizableMessage;

import java.awt.event.ActionEvent;
import java.util.HashSet;

/**
 * @author klausj
 *
 */
public class MediaViewActions {
    
    private MediaView mediaView;
    
   
    public class MediaLengthUnitFramesAction extends RadioActionLeaf{
    
        public MediaLengthUnitFramesAction() {
            super(new LocalizableMessage("Media length in frames"));
        }

        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            mediaView.setMediaLengthUnit(MediaLengthUnit.FRAMES);
        }
    }
    private MediaLengthUnitFramesAction mediaLenFramesAction=new MediaLengthUnitFramesAction();
    
    public class MediaLengthUnitTimeAction extends RadioActionLeaf{

        public MediaLengthUnitTimeAction() {
            super(new LocalizableMessage("Media length in time"));
        }

        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            mediaView.setMediaLengthUnit(MediaLengthUnit.TIME);
        }
    }
    private MediaLengthUnitTimeAction mediaLenTimeAction=new MediaLengthUnitTimeAction();
   
    private RadioActionGroup mediaLenUnitGroup=new RadioActionGroup();
    
    public class TimeFormatSecondsMsAction extends RadioActionLeaf{
   
        public TimeFormatSecondsMsAction() {
            super(new LocalizableMessage("Seconds (00.000)"));
        }

        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            mediaView.setTimeFormat(TimeFormat.FIXED_SECONDS_MS_TIME_FORMAT);
        }
    }
    private TimeFormatSecondsMsAction timeFormatSecondsMsAction=new TimeFormatSecondsMsAction();
    
    public class MediaTimeFormatAction extends RadioActionLeaf{
       
        public MediaTimeFormatAction() {
            super(new LocalizableMessage("Mediatime (00:00:00.000)"));
        }

        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            mediaView.setTimeFormat(MediaTimeFormat.MEDIA_TIME_FORMAT);
        }
    }
    private MediaTimeFormatAction mediaTimeAction=new MediaTimeFormatAction();
   
    private RadioActionGroup timeFormatGroup=new RadioActionGroup();
    
    
    public MediaViewActions(MediaView mediaView){
        super();
        mediaLenUnitGroup.add(mediaLenFramesAction);
        mediaLenUnitGroup.add(mediaLenTimeAction);
        mediaLenFramesAction.setRadioActionGroup(mediaLenUnitGroup);
        mediaLenTimeAction.setRadioActionGroup(mediaLenUnitGroup);     
//        MediaLengthUnit mlu=asc.getMediaLengthUnit();
//        if(MediaLengthUnit.TIME.equals(mlu)){
//            mediaLenTimeAction.setSelected(true);
//        }   
        ActionGroup lengthUnitGroup=new ActionGroup("view.length_unit");
        ActionFolder uaf=new ActionFolder("view.units", new LocalizableMessage("Units"));
        lengthUnitGroup.add(uaf);
        
        ActionFolder tff=new ActionFolder("view.units.time", new LocalizableMessage("Time"));
        uaf.add(tff);
        timeFormatGroup.add(timeFormatSecondsMsAction);
        timeFormatGroup.add(mediaTimeAction);
        timeFormatSecondsMsAction.setRadioActionGroup(timeFormatGroup);
        mediaTimeAction.setRadioActionGroup(timeFormatGroup);
        
        tff.add(timeFormatSecondsMsAction);
        tff.add(mediaTimeAction);
        timeFormatSecondsMsAction.setSelected(true);
        
        uaf.add(mediaLenFramesAction);
        uaf.add(mediaLenTimeAction);
        setMediaView(mediaView);
    }


    public MediaView getMediaView() {
        return mediaView;
    }


    public void setMediaView(MediaView mediaView) {
        this.mediaView = mediaView;
      MediaLengthUnit mlu=mediaView.getMediaLengthUnit();
      if(MediaLengthUnit.TIME.equals(mlu)){
          mediaLenTimeAction.setSelected(true);
      }   
    }


    public MediaLengthUnitFramesAction getMediaLenFramesAction() {
        return mediaLenFramesAction;
    }


    public MediaLengthUnitTimeAction getMediaLenTimeAction() {
        return mediaLenTimeAction;
    }


    public TimeFormatSecondsMsAction getTimeFormatSecondsMsAction() {
        return timeFormatSecondsMsAction;
    }


    public MediaTimeFormatAction getMediaTimeAction() {
        return mediaTimeAction;
    }
    
}
