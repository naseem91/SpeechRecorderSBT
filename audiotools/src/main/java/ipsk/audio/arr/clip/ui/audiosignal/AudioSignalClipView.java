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

package ipsk.audio.arr.clip.ui.audiosignal;

import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.AudioSourceChangedEvent;
import ipsk.audio.arr.clip.ui.BasicAudioClipUI;
import ipsk.audio.arr.clip.ui.audiosignal.AudioSignalModelRenderer.RenderResult;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 * @author klausj
 *
 */
public class AudioSignalClipView extends BasicAudioClipUI implements AudioSignalModelRendererListener {
    private AudioSignalModelRenderer renderer;
    private AudioSignalPainter<JPanel> painter;
    private int preferredHeight = 60;
    private boolean useThread=true;
//    private boolean rendered;
    
    
//    public void clear() {    
//       clearScreen();
//    }
//
//    public void clearScreen() {     
////        setCursor();
//        repaint();
//    }
    
    public AudioSignalClipView(){
        super();
        painter=new AudioSignalPainter<JPanel>();
    }
    public void audioClipChanged(AudioClipChangedEvent event) {

        super.audioClipChanged(event);
       if (event instanceof AudioSourceChangedEvent) {
           
            close();
//            clear();
            repaint();
            AudioSource as = ((AudioSourceChangedEvent) event).getAudioSource();
            if (as != null){
                try {
                    if (renderer!=null){
                        renderer.close();
                    }
                    renderer = new AudioSignalModelRenderer(as,this);
                  
                } catch (AudioFormatNotSupportedException e) {
                    JOptionPane.showMessageDialog(this,
                            "Audio format not supported\n"
                                    + e.getLocalizedMessage(),
                            "Audio signal renderer",
                            JOptionPane.INFORMATION_MESSAGE);
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (AudioSourceException e) {
                    JOptionPane.showMessageDialog(this,
                            "Audio source error: \n" + e.getLocalizedMessage(),
                            "Audio signal renderer error",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            }
//            updateYScales();
        }
    }

    public void paintComponent(Graphics g){
        Graphics2D g2d=(Graphics2D)g;
        Rectangle clipBounds = g.getClipBounds();
        super.paintComponent(g2d);
        if (audioSample == null || audioSample.getAudioSource() == null || channels == 0 || pixelsPerFrame == 0.0 || renderer == null)
            return;
//        long frames=audioSample.getFrameLength();
        int width=getWidth();
        int height = getHeight();
//        int width=(int)(framesPerPixel/(double)frames);

//        imgHeight = (height - 2 * borderLength) / channels;
        
        int paintFrom = clipBounds.x - (int) pixelsPerFrame - 1;
        if (paintFrom < 0)
            paintFrom = 0;
        int paintTo = clipBounds.x + clipBounds.width + (int) pixelsPerFrame
                + 1;
 

        RenderResult rr = null;
        try {
            rr = renderer.render(paintFrom, paintTo, framesPerPixel,useThread,false);
        } catch (AudioSourceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        rendered=rr.rendered;
        if(rr!=null){
            painter.setRenderResult(rr);
            painter.paint(g2d, this, width, height);
        }
        
    }

    /* (non-Javadoc)
     * @see ipsk.audio.arr.clip.ui.audiosignal.AudioSignalModelRendererListener#update(ipsk.audio.arr.clip.ui.audiosignal.AudioSignalModelRendererEvent)
     */
    public void update(AudioSignalModelRendererEvent event) {
        Throwable renderException=event.getRenderException();
        if(renderException!=null){
            close();
            JOptionPane.showMessageDialog(this,
                    "Audio signla model renderer error: \n" +renderException.getLocalizedMessage(),
                    "Audio signal model renderer error",
                    JOptionPane.ERROR_MESSAGE);
        }else{
        repaint();
        }
    }
    
    public Dimension getMinimumSize() {
        return new Dimension(getWidth(), 3);
    }

    public Dimension getPreferredSize() {
//        if (imgHeightSet) {
//            return new Dimension(getWidth(), imgHeight);
//        }
        return new Dimension(getWidth(), preferredHeight);

    }
    
    public void close() {
        if(renderer!=null){
            renderer.close();
        }
    }
}
