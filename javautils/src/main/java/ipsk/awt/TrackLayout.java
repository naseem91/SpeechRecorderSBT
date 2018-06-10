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

package ipsk.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;

/**
 * A layout for media track containers.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class TrackLayout implements LayoutManager {

    private final boolean DEBUG=false;
	private int width=0;
	private boolean useFixedWidth=true;

	public TrackLayout(){
		
	}
	
	/**
	 *  
	 */
	public TrackLayout(int width) {
		super();
		this.width = width;
	}

	public synchronized void setFixedWidth(int width){
		this.width=width;
	}
	
	public int getFixedWidth(){
		return width;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String,
	 *      java.awt.Component)
	 */
	public void addLayoutComponent(String name, Component comp) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
	 */
	public void removeLayoutComponent(Component comp) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
	 */
	public Dimension preferredLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			Component[] cs = parent.getComponents();
			int preferredHeight = 0;
			for (int i = 0; i < cs.length; i++) {
				Dimension d = cs[i].getPreferredSize();
				if (d != null) {

					if (d.height > 0)
						preferredHeight += d.height;
				}
			}
			preferredHeight += insets.top;
			preferredHeight += insets.bottom;
			int preferredWidth=0;
			if(useFixedWidth){
				preferredWidth=width;
			}else{
				preferredWidth=parent.getSize().width;
			}
			return new Dimension(preferredWidth + insets.left + insets.right, (int) preferredHeight);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
	 */
	public Dimension minimumLayoutSize(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			Component[] cs = parent.getComponents();
			int preferredHeight = 0;
			for (int i = 0; i < cs.length; i++) {
				Dimension d = cs[i].getMinimumSize();
				if (d != null) {

					if (d.height > 0)
						preferredHeight += d.height;
				}
			}
			preferredHeight += insets.top;
			preferredHeight += insets.bottom;
			
			return new Dimension( insets.left + insets.right, (int) preferredHeight);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
	 */
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			int y = 0;
			//int availableHeight = parent.getSize().height - insets.top - insets.bottom;
			int parentHeight = parent.getSize().height;
			int parentWidth=parent.getSize().width;
			int layoutWidth;
			if(useFixedWidth){
				layoutWidth=width;
			}else{
				layoutWidth=parentWidth-insets.left-insets.right;
			}
			int minimumLayoutHeight = minimumLayoutSize(parent).height;
			int preferredLayoutHeight = preferredLayoutSize(parent).height;
			if (minimumLayoutHeight >= parentHeight) {
//				 Set all to minium height
				Component[] cs = parent.getComponents();
				for (int i = 0; i < cs.length; i++) {
					int mHeight = cs[i].getMinimumSize().height;
					if (DEBUG)System.out.println("Set bounds"+cs[i]+": "+layoutWidth+" "+ mHeight);
					cs[i].setBounds(insets.left, y+insets.top, layoutWidth, mHeight);
					y += mHeight;
				}
			} else if (preferredLayoutHeight > parent.getSize().height) {
				// set minimum
				int distributableHeight = parentHeight - minimumLayoutHeight;
				Component[] cs = parent.getComponents();
				int componentsWhichLikeSpace = 0;
				for (int i = 0; i < cs.length; i++) {
					int pHeight = cs[i].getPreferredSize().height;
					int mHeight = cs[i].getMinimumSize().height;
					if (pHeight > mHeight) {
						componentsWhichLikeSpace++;
					}
				}
				int eachDistributableSpace = 0;
				if(componentsWhichLikeSpace>0){
				    eachDistributableSpace=distributableHeight / componentsWhichLikeSpace;
				}

				for (int i = 0; i < cs.length; i++) {
					int pHeight = cs[i].getPreferredSize().height;
					int mHeight = cs[i].getMinimumSize().height;
					int height;
					if (pHeight > mHeight) {
						height = mHeight + eachDistributableSpace;

					} else {
						height = mHeight;
					}
					if (DEBUG)System.out.println("Set bounds "+cs[i]+": "+layoutWidth+" "+ height);
					cs[i].setBounds(insets.left, y+insets.top, layoutWidth, height);
					y += height;
				}

			} else {
			     
			    int distributableHeight = parentHeight - preferredLayoutHeight;
				Component[] cs = parent.getComponents();
				int componentsWhichLikeSpace = 0;
				for (int i = 0; i < cs.length; i++) {
					int pHeight = cs[i].getPreferredSize().height;
					int mHeight = cs[i].getMinimumSize().height;
					if (pHeight > mHeight) {
						componentsWhichLikeSpace++;
					}
				}
				int eachDistributableSpace = 0;
				if(componentsWhichLikeSpace>0){
				    eachDistributableSpace=distributableHeight / componentsWhichLikeSpace;
				}

			
			    for (int i = 0; i < cs.length; i++) {
					int pHeight = cs[i].getPreferredSize().height;
					int mHeight = cs[i].getMinimumSize().height;
					int height;
					if (pHeight > mHeight) {
						height = pHeight + eachDistributableSpace;

					} else {
						height = pHeight;
					}
				
					if (DEBUG)System.out.println("Set bounds"+cs[i]+": "+width+" "+ height);
					cs[i].setBounds(insets.left, y+insets.top, width, height);
					y += height;
				}
			}
		}

	}

	/**
	 * @return Returns the useFixedWidth.
	 */
	public boolean isUseFixedWidth() {
		return useFixedWidth;
	}
	/**
	 * @param useFixedWidth The useFixedWidth to set.
	 */
	public void setUseFixedWidth(boolean useFixedWidth) {
		this.useFixedWidth = useFixedWidth;
	}
}
