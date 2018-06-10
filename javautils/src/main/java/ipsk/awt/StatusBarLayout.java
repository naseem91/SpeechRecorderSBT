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
import java.awt.LayoutManager2;
import java.util.Iterator;
import java.util.Vector;

/**
 * Layout for status bar at bottom of application window.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *  
 */
public class StatusBarLayout implements LayoutManager2 {

    private final static boolean DEBUG=false;
    
    public final static String ALIGN_LEFT="align left";
    public final static String ALIGN_RIGHT="align right";
    private Vector<Component> leftAligns;
    private Vector<Component> rightAligns;
	
	/**
	 *  
	 */
	public StatusBarLayout() {
		super();
		leftAligns=new Vector<Component>();
		rightAligns=new Vector<Component>();
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
			//Component[] cs = parent.getComponents();
			int pWidth=0;
			int pHeight=0;
			Iterator<Component> i=leftAligns.iterator();

			while(i.hasNext()) {
			    Component c=i.next();
				Dimension d = c.getPreferredSize();
				if (d.height>pHeight)pHeight=d.height;
				pWidth+=d.width;
			}
			i=rightAligns.iterator();

			while(i.hasNext()) {
			    Component c=i.next();
				Dimension d = c.getPreferredSize();
				if (d.height>pHeight)pHeight=d.height;
				pWidth+=d.width;
			}
	
			Dimension pD= new Dimension(pWidth + insets.left + insets.right,pHeight+insets.top+insets.bottom);
			if (DEBUG)System.out.println("Preferred: "+pD);
			return pD;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
	 */
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
	 */
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			Insets insets = parent.getInsets();
			//int height=parent.getSize().height-insets.top-insets.bottom;
			int width=parent.getSize().width-insets.left-insets.right;
			Iterator<Component> i=leftAligns.iterator();
			int xPos=insets.left;
			while(i.hasNext()) {
			    Component c=i.next();
//					int mHeight = cs[i].getMinimumSize().height;
//					if (DEBUG)System.out.println("Set bounds"+cs[i]+": "+layoutWidth+" "+ mHeight);
			    	Dimension d=c.getPreferredSize();
					c.setBounds(xPos, insets.top, d.width,d.height);
					xPos+=d.width;
				}
			i=rightAligns.iterator();
			xPos=width-insets.right;
			while(i.hasNext()) {
			    Component c=i.next();
//					int mHeight = cs[i].getMinimumSize().height;
//					if (DEBUG)System.out.println("Set bounds"+cs[i]+": "+layoutWidth+" "+ mHeight);
			    	Dimension d=c.getPreferredSize();
			    	xPos-=d.width;
					c.setBounds(xPos, insets.top, d.width,d.height);
				}
			
			
		}

	}

	


    /* (non-Javadoc)
     * @see java.awt.LayoutManager2#getLayoutAlignmentX(java.awt.Container)
     */
    public float getLayoutAlignmentX(Container arg0) {
        // TODO Auto-generated method stub
        return 0;
    }


    /* (non-Javadoc)
     * @see java.awt.LayoutManager2#getLayoutAlignmentY(java.awt.Container)
     */
    public float getLayoutAlignmentY(Container arg0) {
        // TODO Auto-generated method stub
        return 0;
    }


    /* (non-Javadoc)
     * @see java.awt.LayoutManager2#invalidateLayout(java.awt.Container)
     */
    public void invalidateLayout(Container arg0) {
        // TODO Auto-generated method stub
        
    }


    /* (non-Javadoc)
     * @see java.awt.LayoutManager2#maximumLayoutSize(java.awt.Container)
     */
    public Dimension maximumLayoutSize(Container parent) {
    	return preferredLayoutSize(parent);
    }


    /* (non-Javadoc)
     * @see java.awt.LayoutManager2#addLayoutComponent(java.awt.Component, java.lang.Object)
     */
    public void addLayoutComponent(Component arg0, Object arg1) {
        if (arg1!=null){
       if ( arg1==ALIGN_LEFT){
           leftAligns.add(arg0);
       }else if(arg1==ALIGN_RIGHT){
           rightAligns.add(arg0);
       }
        }
       
    }
}
