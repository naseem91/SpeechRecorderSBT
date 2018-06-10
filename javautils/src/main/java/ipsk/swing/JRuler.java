//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
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

package ipsk.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;



public class JRuler extends javax.swing.JPanel implements MouseMotionListener, MouseListener, ActionListener {
	
	public final static int NORTH=0;
	public final static int EAST=1;
	public final static int SOUTH=2;
	public final static int WEST=3;
	
	// JPopupMenu does not work with  JWindow
	private JFrame w;
	private MouseEvent mouseDragstartEvent;
	private JPopupMenu popup;
	private JMenu orientationMenu;
	private JMenuItem north;
	private JMenuItem east;
	private JMenuItem south;
	private JMenuItem west;
	private JMenuItem quit;
	private int orientation=NORTH;
	
	public JRuler(){
		super();
		setBackground(Color.ORANGE);
		quit=new JMenuItem("Close");
		quit.addActionListener(this);
		
		 popup = new JPopupMenu("Pop");
		 orientationMenu=new JMenu("Orientation");
		 north=new JMenuItem("North");
		 north.addActionListener(this);
		 south=new JMenuItem("South");
		 south.addActionListener(this);
		 west=new JMenuItem("West");
		 west.addActionListener(this);
		 east=new JMenuItem("East");
		 east.addActionListener(this);
		 orientationMenu.add(north);
		 orientationMenu.add(south);
		 orientationMenu.add(west);
		 orientationMenu.add(east);
		 popup.add(orientationMenu);
		 popup.add(quit);
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Dimension size=getSize();
		int length;
		if(orientation==NORTH || orientation==SOUTH){
			length=size.width;
		}else{
			length=size.height;
		}
		for (int i=0;i<length;i++){
			if(i % 5==0){
				if(orientation==NORTH){
			g.drawLine(i, 0,i , 3);
				}else if(orientation==SOUTH){
					g.drawLine(i, size.height-3,i , size.height);
				}else if(orientation==WEST){
					g.drawLine(0, i,3 ,i);
				}else if(orientation==EAST){
					g.drawLine(size.width-3, i,size.width ,i);
				}
			}
			if(i % 10 ==0){
				if(orientation==NORTH){
				g.drawLine(i, 0,i , 10);
				}else if(orientation==SOUTH){
					g.drawLine(i, size.height-10,i , size.height);
				}else if(orientation==WEST){
					g.drawLine(0, i,10 ,i);
				}else if(orientation==EAST){
					g.drawLine(size.width-10, i,size.width ,i);
				}
				
			}
			if(i % 50 ==0){
				if(orientation==NORTH){
					g.drawLine(i, 0,i , 20);
				g.drawString(Integer.toString(i), i+1, 20);
				}else if(orientation==SOUTH){
					g.drawLine(i, size.height-20,i , size.height);
					g.drawString(Integer.toString(i), i+1, size.height-10);
				}else if(orientation==WEST){
					g.drawLine(0,i,20,i);
					g.drawString(Integer.toString(i), 10, i-1);
				}else if(orientation==EAST){
					g.drawLine(size.width-20,i,size.width,i);
					g.drawString(Integer.toString(i), size.width-32, i-1);
				}
				
			}
		}
	}
	
	
	
	public Dimension getPreferredSize(){
		if(orientation==NORTH || orientation==SOUTH){
		return new Dimension(1000,50);
		}else{
			return new Dimension(50,800);
		}
	}
	
	
	
	private  void createAndShow(){
		
		w=new JFrame();
		w.setUndecorated(true);
		w.getContentPane().add(this);
		w.pack();
		w.setVisible(true);
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	public static void main(String[] args){
		
		Runnable r=new Runnable(){
			public void run(){
				JRuler jr=new JRuler();
				jr.createAndShow();
			}
		};
		SwingUtilities.invokeLater(r);
	}
	public void mouseDragged(MouseEvent e) {
		int x=w.getLocation().x;
		int y=w.getLocation().y;
		int deltax=e.getX()-mouseDragstartEvent.getX();
		int deltay=e.getY()-mouseDragstartEvent.getY();
		w.setLocation(x+deltax,y+deltay);
		w.toFront();
		w.setAlwaysOnTop(true);
		
	}
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	public void mousePressed(MouseEvent e) {
		
		mouseDragstartEvent=e;
		if (e.isPopupTrigger()) {
			//System.out.println("Popup");
            popup.show(e.getComponent(), e.getX(), e.getY());
        }
	}
	public void mouseReleased(MouseEvent e) {
		mouseDragstartEvent=null;
		if (e.isPopupTrigger()) {
			//System.out.println("Popup");
            popup.show(e.getComponent(), e.getX(),e.getY());
            
        }
	}

	public void actionPerformed(ActionEvent e) {
		Object src=e.getSource();
		if (src==quit){
			w.dispose();
			System.exit(0);
		}else if(src==north){
			setOrientation(NORTH);
		}else if(src==south){
			setOrientation(SOUTH);
		}else if(src==west){
			setOrientation(WEST);
		}else if(src==east){
			setOrientation(EAST);
		}
		
	}

	public int getOrientation() {
		return orientation;
	}

	public void setOrientation(int orientation) {
		this.orientation = orientation;
		revalidate();
		w.pack();
		repaint();
	}
}
