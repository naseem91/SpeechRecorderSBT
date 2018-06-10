//    IPS Java Audio Tools
// 	  (c) Copyright 2016
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

package ipsk.audio.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * @author klausj
 *
 */
public class AudioStatus extends JComponent{

	public enum Status {OFF,CAPTURE,RECORDING,PLAYBACK};
	
	private Status status=Status.OFF;
	private Dimension size;
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
		repaint();
	}
	
	
	public AudioStatus(){
		super();
		size=new Dimension(DEFAULT_EDGE_LENGTH_UNITS*unit,DEFAULT_EDGE_LENGTH_UNITS*unit);
		setPreferredSize(size);
	}

	public int DEFAULT_UNIT_PIXELS=2;
	public int DEFAULT_EDGE_LENGTH_UNITS=10;
	
	public int unit=DEFAULT_UNIT_PIXELS;

	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2=(Graphics2D)g;
		
		if(Status.CAPTURE.equals(status)){
			// paint yellow pause symbol 
			g2.setColor(Color.YELLOW);
			g2.fillRect(unit,unit,3*unit,8*unit);
			g2.fillRect(6*unit,unit,3*unit,8*unit);
			g2.setColor(Color.BLACK);
			g2.drawRect(unit,unit,3*unit,8*unit);
			g2.drawRect(6*unit,unit,3*unit,8*unit);
		}else if(Status.RECORDING.equals(status)){
			// paint red recording symbol 
			g2.setColor(Color.RED);
			g2.fillRect(unit,unit,8*unit,8*unit);
			
			g2.setColor(Color.BLACK);
			g2.drawRect(unit,unit,8*unit,8*unit);
		}else if(Status.PLAYBACK.equals(status)){
			int border=unit;
			int edgeLength=unit*10;
			Polygon poly=new Polygon(new int[]{unit,edgeLength-border,border},new int[]{border,edgeLength/2,edgeLength-border},3);
			
			g2.setColor(Color.GREEN);
			g2.fillPolygon(poly);
			g2.setColor(Color.BLACK);
			g2.drawPolygon(poly);
		}
	}
	
	public Dimension getPreferredSize(){
		return size;
	}
	public Dimension getMinimumSize(){
		return size;
	}
	
	public static void main(String[] args){
        final AudioStatus as=new AudioStatus();
        Runnable sr=new Runnable() {
            
            @Override
            public void run() {
                JFrame f=new JFrame();
                f.getContentPane().add(as);
                f.pack();
                f.setVisible(true);
                
                
            }
        };
        SwingUtilities.invokeLater(sr);
        try {
        	Thread.sleep(2000);
			as.setStatus(AudioStatus.Status.CAPTURE);
			Thread.sleep(2000);
			as.setStatus(AudioStatus.Status.RECORDING);
	        Thread.sleep(2000);
	        as.setStatus(AudioStatus.Status.PLAYBACK);
	        Thread.sleep(2000);
	        as.setStatus(AudioStatus.Status.OFF);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
    }
	
	
}
