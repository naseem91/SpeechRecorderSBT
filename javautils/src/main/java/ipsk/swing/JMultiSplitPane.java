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
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;

public class JMultiSplitPane extends JLayeredPane implements MouseListener,
		MouseMotionListener {

	public final static boolean DEBUG=false;
	
	public class Division{
		private Component component;
		private double weight;
		
		public Division(Component c){
			component=c;
		}

		public double getWeight() {
			return weight;
		}

		public void setWeight(double weight) {
			this.weight = weight;
		}

		public Component getComponent() {
			return component;
		}
	}
	private ArrayList<Division> divisions;

	private ArrayList<JSeparator> seperators;

	//private boolean customScaled = false;

	private Component draggedSeparator;

	public JMultiSplitPane() {
		super();
		setLayout(null);
		divisions = new ArrayList<Division>();
		seperators = new ArrayList<JSeparator>();
	}

	public synchronized Component add(Component c) {
		super.add(c);
		Division newDiv=new Division(c);
		divisions.add(newDiv);
		int cs=divisions.size();
		double dCs=(double)cs;
		double divWeight=1.0/dCs;
		double divider=dCs/(dCs-1.0);
		for(int i=0;i<cs-1;i++){
			// weight down existing elements
			Division d=divisions.get(i);
			d.setWeight(d.getWeight()/divider);
		}
		newDiv.setWeight(divWeight);
		if (divisions.size() > 1) {
			JSeparator sep = new JSeparator();
			super.add(sep);
			seperators.add(sep);
			setLayer((Component) sep, JLayeredPane.PALETTE_LAYER.intValue());
			sep.setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
			sep.addMouseListener(this);
			sep.addMouseMotionListener(this);
		}
		revalidate();
		return c;
	}
	
	public void removeAll(){
		super.removeAll();
		divisions.clear();
		seperators.clear();
	}
	
	public synchronized void remove(Component c) {
		// TODO not tested !!!
		Division divToRemove=null;
		int divs=divisions.size();
		for(int i=0;i<divs;i++){
			Division d=divisions.get(i);
			if(d.getComponent().equals(c)){
				divToRemove=d;
				int sepIndex=i-1;
				if(sepIndex<0)sepIndex=0;
				int seps=seperators.size();
				if(seps>sepIndex){
					JSeparator s=seperators.remove(sepIndex);
					s.removeMouseListener(this);
					s.removeMouseMotionListener(this);
					super.remove(s);
				}
				divisions.remove(divToRemove);
				super.remove(c);
				break;
			}
		}
		if (divToRemove!=null) {
			double totalDistributableWeight=divToRemove.getWeight();
			divs=divisions.size();
			double distributableWeight=totalDistributableWeight/(double)divs;
			for (int i = 0; i < divs; i++) {
				Division d=divisions.get(i);
				d.setWeight(d.getWeight()+distributableWeight);
			}
		}
		revalidate();
		repaint();
	}
	
	public void doLayout() {
		synchronized (getParent().getTreeLock()) {
			int y = 0;
			int width = getWidth();
			int height = getHeight();
			if(width<=0 || height<=0)return;
			int compsLen = divisions.size();
			
			// calc required space for separators
			int totalSepHeight = 0;
			for (int i = 0; i < compsLen-1; i++) {
                Division d=divisions.get(i);
                Component c = d.getComponent();
                JSeparator s=seperators.get(i);
                if (c.isVisible()){
                    totalSepHeight += s.getPreferredSize().height;
                }
			}
			
//			for (JSeparator s : seperators) {
//				totalSepHeight += s.getPreferredSize().height;
//			}
			int distributableHeight = height - totalSepHeight;
			// calculate visible factor
			double totalWeight=0;
			double visibleWeight=0;
			for (int i = 0; i < compsLen; i++) {
                Division d=divisions.get(i);
                Component c = d.getComponent();
                double weight=d.getWeight();
                totalWeight+=weight;
                if(c.isVisible()){
                    visibleWeight+=weight;
                }
			}
			double visibleWeightFactor=totalWeight/visibleWeight;
			for (int i = 0; i < compsLen; i++) {
				Division d=divisions.get(i);
				Component c = d.getComponent();
				boolean visible=c.isVisible();
				double weight=d.getWeight();
				// height+=c.getSize().height;
				int prefHeight = 0;
				if(visible){
				    prefHeight=(int) (weight* visibleWeightFactor* (double) distributableHeight);
				}
				if(prefHeight>distributableHeight){
					prefHeight=distributableHeight;
				}
				
				c.setBounds(0, y, width, prefHeight);
				c.doLayout();
				y += prefHeight;
				if (i < compsLen - 1) {
					JSeparator s = seperators.get(i);
					int sHeight = 0;
					if(visible){
					    sHeight=s.getPreferredSize().height;
					}
					s.setBounds(0, y, width, sHeight);
					y += sHeight;
				}
			}
		}
	}

//
//   Setting a preferred size causes the parent container scroll pane to re-validate 
//   in a loop if the size (height) is smaller then the preferred height.
	
//	public Dimension getPreferredSize() {
//		//return new Dimension(200, 100);
//		return new Dimension(200, 400);
//	}

	public void mouseClicked(MouseEvent arg0) {
		// nothing to do
	}

	public void mouseEntered(MouseEvent arg0) {
		setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));
	}

	public void mouseExited(MouseEvent arg0) {
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	}

	public void mousePressed(MouseEvent arg0) {
		draggedSeparator = null;
	}

	public void mouseReleased(MouseEvent arg0) {
		if (draggedSeparator != null) {
			//customScaled = true;
			// find separator in list
			for (int i = 0; i < seperators.size(); i++) {
				JSeparator s = seperators.get(i);
				if (s == draggedSeparator) {
					synchronized(divisions){

						Division dAbove=divisions.get(i);
						Component cAbove = dAbove.getComponent();
						double cAboveWeight=dAbove.getWeight();
						Rectangle cAboveBounds = cAbove.getBounds();
						int cAboveHeight=s.getY()- cAboveBounds.y;
						if(cAboveHeight<0)cAboveHeight=0;
						//cAbove.setBounds(0, cAboveBounds.y, cAboveBounds.width, cAboveHeight);

						Division dBelow=divisions.get(i + 1);
						Component cBelow = dBelow.getComponent();
						double cBelowWeight=dBelow.getWeight();
						Rectangle cBelowBounds = cBelow.getBounds();
						int y = s.getY() + s.getHeight();
						int cBelowHeight=(cBelowBounds.y + cBelowBounds.height) - y;
						if(cBelowHeight<0)cBelowHeight=0;
						//cBelow.setBounds(0, y + 1, cBelowBounds.width,cBelowHeight);

						double divisonsHeight=cAboveHeight+cBelowHeight;
						double weights=cAboveWeight+cBelowWeight;
						double newAboveWeight=weights*((double)cAboveHeight/(double)divisonsHeight);
						dAbove.setWeight(newAboveWeight);
						dBelow.setWeight(weights-newAboveWeight);

					}
				}
			}
		}
		draggedSeparator = null;
		revalidate();
	}

	public void mouseDragged(MouseEvent arg0) {

		int minY = 0;
		int maxY = 0;
		Component s = arg0.getComponent();
		for (int i = 0; i < seperators.size(); i++) {
			if (s == seperators.get(i)) {
				if (i == 0) {
					minY = 0;
				} else {
					JSeparator sAbove = seperators.get(i - 1);
					minY = sAbove.getY() + sAbove.getHeight();
				}
				if (i == seperators.size() - 1) {
					maxY = getHeight() - s.getHeight();
				} else {
					JSeparator sBelow = seperators.get(i + 1);
					maxY = sBelow.getY() - s.getHeight();
				}
				break;
			}
		}
		int reqY = s.getY() + arg0.getY();
		if (reqY < minY)
			reqY = minY;
		if (reqY > maxY)
			reqY = maxY;
		arg0.getComponent().setLocation(0, reqY);
		draggedSeparator = s;
		// revalidate();

	}

	public void mouseMoved(MouseEvent arg0) {
		// nothing to do
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagConstraints c=new GridBagConstraints();
		final Container content=f.getContentPane();
		content.setLayout(new GridBagLayout());
		c.gridx=0;
		c.gridy=0;
		c.gridwidth=3;
		c.weightx=2.0;
		c.weighty=2.0;
		c.fill=GridBagConstraints.BOTH;
		final JMultiSplitPane msp = new JMultiSplitPane();
		final JComponent p0 = new JPanel();
		p0.setBackground(Color.RED);
		p0.setPreferredSize(new Dimension(200, 30));
		//msp.add(p0);
		final JPanel p1 = new JPanel();
		p1.setPreferredSize(new Dimension(200, 60));
		p1.setBackground(Color.GREEN);
//		msp.add(p1);
		final JPanel p2 = new JPanel();
		p2.setPreferredSize(new Dimension(200, 60));
		p2.setBackground(Color.YELLOW);
//		msp.add(p2);
		content.add(msp,c);
		final JCheckBox cb0=new JCheckBox("0");
		cb0.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(cb0.isSelected()){
					msp.add(p0);
				}else{
					msp.remove(p0);
				}
			}
		});
		c.gridy++;
		c.gridwidth=1;
		c.weighty=1.0;
		c.fill=GridBagConstraints.NONE;
		content.add(cb0,c);
		final JCheckBox cb1=new JCheckBox("1");
		cb1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(cb1.isSelected()){
					msp.add(p1);
				}else{
					msp.remove(p1);
				}
			}
		});
		c.gridx++;
		content.add(cb1,c);
		final JCheckBox cb2=new JCheckBox("2");
		cb2.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent ae){
				if(cb2.isSelected()){
					msp.add(p2);
				}else{
					msp.remove(p2);
				}
			}
		});
		c.gridx++;
		content.add(cb2,c);
		f.pack();
		f.setVisible(true);
	}


}
