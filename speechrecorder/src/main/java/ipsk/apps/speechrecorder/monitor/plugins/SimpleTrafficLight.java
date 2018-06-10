//    Speechrecorder
//    (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.apps.speechrecorder.monitor.plugins;

import ipsk.apps.speechrecorder.monitor.StartStopSignal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JComponent;


public class SimpleTrafficLight extends JComponent implements StartStopSignal{

	private static final long serialVersionUID = 1L;
	private static final int LIGHT_WIDTH=60;
    private static final int LIGHT_PADDING=2;
    private static final int WIDTH = LIGHT_WIDTH+2*LIGHT_PADDING;
//    private static final int HEIGHT = 192;
    private static final int HEIGHT = (LIGHT_WIDTH+2*LIGHT_PADDING)*3;
	private static final int BORDER_WIDTH=2;

	private State status=State.OFF;
	private Dimension fixedSize=new Dimension(WIDTH+BORDER_WIDTH*2,HEIGHT+BORDER_WIDTH*2); 
	
	
	
	public SimpleTrafficLight() {
		super();
		
		setBorder(BorderFactory.createEmptyBorder(BORDER_WIDTH, BORDER_WIDTH,BORDER_WIDTH, BORDER_WIDTH));
	}

	
    public JComponent getComponent() {
       return this;
    }


    public void setStatus(State status) {
        this.status=status;
        repaint();
    }
    
   
    public void paintComponent(Graphics g){
        Graphics2D g2=(Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        int y=0;
        if(status.equals(State.IDLE) || status.equals(State.PRERECORDING)){
            g2.setColor(Color.RED);
        }else{
            g2.setColor(Color.GRAY);
        }
        g2.fillOval(LIGHT_PADDING,y+LIGHT_PADDING, LIGHT_WIDTH, LIGHT_WIDTH);
        y+=LIGHT_WIDTH+2*LIGHT_PADDING;
        if(status.equals(State.POSTRECORDING) || status.equals(State.PRERECORDING)){
            g2.setColor(Color.YELLOW);
        }else{
            g2.setColor(Color.GRAY);
        }
        g2.fillOval(LIGHT_PADDING, y+LIGHT_PADDING, LIGHT_WIDTH, LIGHT_WIDTH);
        y+=LIGHT_WIDTH+2*LIGHT_PADDING;
        if(status.equals(State.RECORDING)){
            g2.setColor(Color.GREEN);
        }else{
            g2.setColor(Color.GRAY);
        }
        g2.fillOval(LIGHT_PADDING, y+LIGHT_PADDING, LIGHT_WIDTH, LIGHT_WIDTH);
    }
    
    
    public Dimension getPreferredSize(){
        return fixedSize;
    }
   
}