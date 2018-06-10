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
 * Date  : Jun 17, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.arr.clip.ui;

import ipsk.audio.actions.StartPlaybackAction;

import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.AudioSourceChangedEvent;
import ipsk.audio.arr.clip.events.SelectionChangedEvent;
import ipsk.audio.events.StartPlaybackActionEvent;
import ipsk.swing.JWideButton;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.sound.sampled.AudioSystem;
import javax.swing.Action;


/**
 * Fragmented button bar.
 * Fragments are the selection, and the rest of the clip to the left and right. 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class FragmentActionBarUI extends BasicAudioClipUI implements
		PropertyChangeListener, ActionListener {

	private JWideButton buttLeft;
	private Rectangle buttLeftBounds;

	private JWideButton selButt;
	private Rectangle selButtBounds;

	private JWideButton rightButton;
	private Rectangle rightButtonBounds;

	private StartPlaybackAction startPlaybackAction;
	int preferredHeight;
	private JWideButton buttAll;
	private Rectangle buttAllBounds;


	public FragmentActionBarUI() {
		super();
		setLayout(null);
		
		buttAll=new JWideButton("Play all");
		buttAll.addActionListener(this);
		buttLeft = new JWideButton("Play (left)");
		
		
		buttLeft.addActionListener(this);

		selButt = new JWideButton("Play selection");
		selButt.addActionListener(this);
		rightButton = new JWideButton("Play (right)");
		rightButton.addActionListener(this);
//		addComponentListener(this);

	}
	
	public String getName(){
		return "Play selection bar";
	}
	
	public boolean isPreferredFixedHeight(){
	    return true;
	}

	private void update() {
	    //System.out.println("Update");
		removeAll();
		if (viewSelection == null) {
		    buttAllBounds=new Rectangle(0, 0, getWidth(), preferredHeight);
            buttAll.setBounds(buttAllBounds);
			add(buttAll);
			preferredHeight=buttAll.getPreferredSize().height;
		} else {
		    
			int vsBegin = viewSelection.getXLeft();
			int vsEnd = viewSelection.getXRight();
			int vsLen = Math.abs(vsEnd - vsBegin);
			buttLeftBounds=new Rectangle(0, 0, vsBegin, preferredHeight);
            buttLeft.setBounds(buttLeftBounds);
			add(buttLeft);
			selButtBounds=new Rectangle(vsBegin, 0, vsLen, preferredHeight);
            selButt.setBounds(selButtBounds);
			add(selButt);
			rightButtonBounds=new Rectangle(vsEnd, 0, getWidth() - vsEnd, preferredHeight);
            rightButton.setBounds(rightButtonBounds);
			add(rightButton);
			preferredHeight = selButt.getPreferredSize().height;

		}
		
		setPreferredSize(new Dimension(getWidth(),preferredHeight));
		setMinimumSize(getPreferredSize());
		revalidate();
		repaint();

	}
	
	
//	public void paintChildren(Graphics g){
//		// Without overloading the buttons are painted the whole length
//		// Takes too much time for strong zoomed signals
//		// so I intersect the bounds of the buttons to the clipping area
//		Rectangle clipBounds=g.getClipBounds();
//		Rectangle clip=(Rectangle)clipBounds.clone();
//		// TODO not nice
//		// the buttons should overlap the clipping area a "little bit"
//		clip.width+=100;
//		clip.x-=50;
//		if (viewSelection == null) {
//		    
//		    //buttLeft.setBounds(buttLeftBounds.intersection(clip));
//		    Rectangle buttLeftRect=buttLeftBounds.intersection(clip);
//			if (!buttLeft.getBounds().equals(buttLeftRect)){
//			buttLeft.setBounds(buttLeftBounds.intersection(clip));
//			//revalidate();
//			}
//		} else {
//		    
//			int vsBegin = viewSelection.getXLeft();
//			int vsEnd = viewSelection.getXRight();
//			int vsLen = Math.abs(vsEnd - vsBegin);
//			
//			Rectangle buttLeftRect=buttLeftBounds.intersection(clip);
//			
//			if (!buttLeft.getBounds().equals(buttLeftRect)){
//			    System.out.println("Bounds:\n"+buttLeftBounds+"\n"+clip+"\n"+buttLeftRect+ "\n"+buttLeft.getBounds());
//			buttLeft.setBounds(buttLeftRect);
//			//revalidate();
//			}
//			Rectangle selButtRect=selButtBounds.intersection(clip);
//			if (!selButtRect.equals(selButt.getBounds())){
//			    //System.out.println("Bounds");
//			selButt.setBounds(selButtRect);
//			//revalidate();
//			}
//		Rectangle rightButtRect=rightButtonBounds.intersection(clip);
//		if (!rightButtRect.equals(rightButton.getBounds())){
//		    //System.out.println("Bounds");
//			rightButton.setBounds(rightButtRect);
//			//revalidate();
//		}
//			
//
//		}
//		//System.out.println("Repainting");
//		
//		super.paintChildren(g);
//		
//	}

	public void audioClipChanged(AudioClipChangedEvent event) {
		super.audioClipChanged(event);
		
		if (event instanceof AudioSourceChangedEvent || event instanceof SelectionChangedEvent){
		update();
		}
	}

	



	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		StartPlaybackActionEvent pae = null;
		if (src == selButt) {
			pae = new StartPlaybackActionEvent(this, selection.getLeft(),
					selection.getRight());
		} else if (src == buttLeft) {			
				pae = new StartPlaybackActionEvent(this, 0, selection
						.getLeft());			
		} else if (src == rightButton) {
			pae = new StartPlaybackActionEvent(this, selection.getRight(),
					AudioSystem.NOT_SPECIFIED);
		}else if (src == buttAll) {
			pae = new StartPlaybackActionEvent(this,0,AudioSystem.NOT_SPECIFIED);
		}
		if (pae != null)
			startPlaybackAction.actionPerformed(pae);
	}

	/**
	 * @param startPlaybackAction
	 */
	public void setStartPlaybackAction(StartPlaybackAction startPlaybackAction) {
		this.startPlaybackAction = startPlaybackAction;
		startPlaybackAction.addPropertyChangeListener(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		boolean enabled = ((Action) evt.getSource()).isEnabled();
		buttAll.setEnabled(enabled);
		buttLeft.setEnabled(enabled);
		selButt.setEnabled(enabled);
		rightButton.setEnabled(enabled);

	}

//    /* (non-Javadoc)
//     * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
//     */
//    public void componentHidden(ComponentEvent arg0) {
//       
//    }
//
//    /* (non-Javadoc)
//     * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
//     */
//    public void componentMoved(ComponentEvent arg0) {
//        
//        
//    }
//
//    /* (non-Javadoc)
//     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
//     */
//    public void componentResized(ComponentEvent arg0) {
//        super.componentResized(arg0);
//        update();
//        
//    }
//   
//    
//
//    /* (non-Javadoc)
//     * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
//     */
//    public void componentShown(ComponentEvent arg0) {
//       
//        
//    }

    public void doLayout(){
        super.doLayout();
        update();
    }
    
    
    public void updateUI(){
    	super.updateUI();
    	if(buttAll!=null){
    		// distribute update to all buttons
    		// some buttons may not have an ancestor
    	buttAll.updateUI();
    	buttLeft.updateUI();
    	selButt.updateUI();
    	rightButton.updateUI();
    	}
    	
    }

   
}
