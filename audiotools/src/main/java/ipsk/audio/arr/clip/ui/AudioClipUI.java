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
 * Created on Oct 7, 2005
 *
 */
package ipsk.audio.arr.clip.ui;

import ips.media.MediaLengthUnit;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.AudioClipListener;
import ipsk.awt.TickProvider;
import ipsk.swing.action.tree.ActionProvider;
import ipsk.util.LocalizableMessage;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.text.Format;

import javax.swing.JComponent;

/**
 * Audio clip UI plugins have to implement this interface.
 * @author klausj
 *
 */
public interface AudioClipUI extends AudioClipListener,ActionProvider{

    /**
     * Get name of plugin.
     * @return name
     */
	public String getName();
	
	/**
	 * Get localized name.
	 * @return localized name
	 */
	public LocalizableMessage getLocalizableName();

	
	/**
	 * Set media length units. (time or frames)
	 */
	public void setMediaLengthUnit(MediaLengthUnit mediaLengthUnit);
	
    /**
     * Set the time format to use.
     * For example framecount or media timne format ("00:00:00.000")
     * @param format
     */
	public void setTimeFormat(Format format);
	/**
	 * Set the audio sample.
	 * @param audiosample the audiosample or null to clear
	 */
	public void setAudioSample(AudioClip audiosample);
	
	/**
	 * Set the provider for time scale ticks.
	 * If there is a time scale in the audio sample GUI, it can provide 
	 * the time ticks for the other plugins to display vertical time scale lines.
	 * @param timeScaleTickProvider
	 */
	public void setTimeScaleTickProvider(TickProvider<Long> timeScaleTickProvider);
	
	/**
	 * Add an actionlistener to the plugin.
	 * @param containerUI
	 */
    public void addActionListener(ActionListener containerUI);
    /**
     * Remove an action listener.
     * @param containerUI
     */
    public void removeActionListener(ActionListener containerUI);
    public void close();
    
    
    public boolean isPreferredFixedHeight();
    
//    public Component getControlComponent();
    
    public boolean  hasControlDialog();
    /**
     * Show a dialog component for plugin controls. 
     */
    public void showJControlDialog(Component parentComponent);
    
//    public Component getComponent();
    
    //public JMenu[] getJMenus();
    
    public JComponent[] getYScales();

    public Component asComponent();
	
}
