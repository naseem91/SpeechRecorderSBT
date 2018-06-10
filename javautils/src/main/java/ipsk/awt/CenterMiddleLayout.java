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
 * A layout to center single child component horizontally and vertically.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class CenterMiddleLayout implements LayoutManager {

    private final boolean DEBUG=true;
    
    private boolean fallbackToMinSize;
	
    /**
     *  
     */
    public CenterMiddleLayout() {
        this(true);
    }
	
	/**
	 *  
	 */
	public CenterMiddleLayout(boolean fallbackToMinSize) {
	    super();
	    this.fallbackToMinSize=fallbackToMinSize;
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
			int preferredWidth=0;
			int preferredHeight = 0;
			for (int i = 0; i < cs.length; i++) {
				Dimension d = cs[i].getPreferredSize();
				if (d != null) {
				    if (d.width > preferredWidth)
                        preferredWidth = d.width;
					if (d.height > preferredHeight)
						preferredHeight = d.height;
				}
			}
			preferredWidth+=insets.left;
            preferredWidth+=insets.right;
			preferredHeight += insets.top;
			preferredHeight += insets.bottom;
			
			
			return new Dimension(preferredWidth, preferredHeight);
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
	        int minimumWidth=0;
	        int minimumHeight = 0;
	        for (int i = 0; i < cs.length; i++) {
	            Dimension d = cs[i].getMinimumSize();
	            if (d != null) {
	                if (d.width > minimumWidth)
	                    minimumWidth = d.width;
	                if (d.height > minimumHeight)
	                    minimumHeight = d.height;
	            }
	        }
	        minimumWidth+=insets.left;
	        minimumWidth+=insets.right;
	        minimumHeight += insets.top;
	        minimumHeight += insets.bottom;


	        return new Dimension(minimumWidth, minimumHeight);
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
	        int insetsX=insets.left+insets.right;
	        int insetsY=insets.top+insets.bottom;

	        int parentHeight = parent.getSize().height;
	        int parentWidth= parent.getSize().width;

	        Component[] cs = parent.getComponents();

	        for (int i = 0; i < cs.length; i++) {
	            Component c=cs[i];
	            Dimension pd=c.getPreferredSize();
	            int pWidth=pd.width;
	            int pHeight = pd.height;

	            Dimension md=c.getMinimumSize();
	            int mWidth=md.width;
	            int mHeight = md.height;

	            int cX=insets.left;
	            int cWidth=pWidth;
	            // try preferred width
	            if(pWidth+insetsX<=parentWidth){
	                // preferred width fits
	                cX=insets.left +( (parentWidth- insetsX-pWidth)/2);
	            }else if(fallbackToMinSize && mWidth+insetsX<=parentWidth){
	                cX=insets.left +( (parentWidth- insetsX-mWidth)/2);

	            }else{
	                cWidth=parentWidth-insetsX;
	            }

	            int cY=insets.top;
	            int cHeight=pHeight;
	            // try preferred height
	            if(pHeight+insetsY<=parentHeight){
	                // preferred height fits
	                cY=insets.top +( (parentHeight- insetsY-pHeight)/2);
	            }else if(fallbackToMinSize && mHeight+insetsX<=parentHeight){
	                cY=insets.top +( (parentHeight- insetsY-mHeight)/2);
	            }else{
	                cHeight=parentHeight-insetsY;
	            }
	            if(DEBUG){
	                System.out.println("Parent size: "+parentWidth+"x"+parentHeight+" insets: "+insets);
	                System.out.println("Component pref: "+pWidth+"x"+pHeight);
	                System.out.println("Component min: "+mWidth+"x"+mHeight);
	                System.out.println("Component bounds: "+cX+","+cY+" "+cWidth+"x"+cHeight);
	            }
	            c.setBounds(cX,cY,cWidth,cHeight);
	        }
	    }
	}
}
