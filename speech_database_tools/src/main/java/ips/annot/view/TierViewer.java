//    IPS Speech database tools
// 	  (c) Copyright 2016
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Speech database tools
//
//
//    IPS Speech database tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Speech database tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Speech database tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Created on 20.08.2013
 *
 * Project: SpeechDatabaseTools
 * Original author: draxler
 */
package ips.annot.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JPanel;

import ips.annot.model.db.EventItem;
import ips.annot.model.db.Item;
import ips.annot.model.db.Level;
import ips.annot.model.db.SegmentItem;

public class TierViewer extends JPanel {

    private final static int MIN_WIDTH = 400;
    private final static int MIN_HEIGHT = 32;
    private final static int PREF_WIDTH = 1024;
    private final static int PREF_HEIGHT = 64;
    private final static int MAX_WIDTH = 3072;
    private final static int MAX_HEIGHT = 512;
    
    private final static int LEFT_PADDING = 4;
    private final static int RIGHT_PADDING = 4;
    private final static int TOP_PADDING = 4;
    private final static int BOTTOM_PADDING = 4;
    
    private int width = PREF_WIDTH;
    private int height = PREF_HEIGHT;
    private Vector<ItemViewer> itemViewers = new Vector<ItemViewer>();
    
    private Level tier;
    
    public TierViewer(Level tier) {
        super();
        this.tier = tier;
        createGUI();
    }
    
    public Vector<ItemViewer> getItemViewers() {
        return itemViewers;
    }
    
    public Dimension getMinimumSize() {
        return new Dimension(MIN_WIDTH, MIN_HEIGHT);
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(PREF_WIDTH, PREF_HEIGHT);
    }
    
    public Dimension getMaximumSize() {
        return new Dimension(MAX_WIDTH, MAX_HEIGHT);
    }
    

    private void createGUI() {
        setLayout(null);
        update();
    }
    
    public void update() {
        int widthSum = 0;
        
        // compute total sum of the widths of the tier items 
        // and get the maximum height of the tier items 
        int maxHeight = 0;
        
        for (Item item : tier.getItems()) {
            ItemViewer viewer = null;
            
            if (item instanceof SegmentItem) {
                viewer = new IntervalItemViewer((SegmentItem) item);
            } else if (item instanceof EventItem) {
                viewer = new EventItemViewer((EventItem) item);
            } else if (item instanceof Item) {
                viewer = new ItemViewer(item);
            } else {
                System.out.println("ERROR: other type of viewer: " + viewer.getClass().getName() + ", " + viewer.getHeight());
            }
            
            if (viewer.getHeight() > maxHeight) {
                maxHeight = viewer.getHeight();
            }
            
            itemViewers.add(viewer);
            widthSum = widthSum + LEFT_PADDING + viewer.getWidth();
        }
        
        width = widthSum + RIGHT_PADDING;
        height = TOP_PADDING + maxHeight + BOTTOM_PADDING;
        setSize(width, height);
        System.out.println(tier.getName() + ": (" + width + ", " + height + "), " + itemViewers.size() + " viewers.");
        
        // now draw the items; make sure the starting point is at least 0
        int x = (int) ((getSize().width - widthSum) / 2);
        if (x < 0) {
            x = 0;
        }

        for (JComponent viewer : itemViewers) {
            viewer.setLocation(x, TOP_PADDING);
            add(viewer);
            x = x + viewer.getWidth() + LEFT_PADDING;
        }
        repaint();
    }

    
    public Point getItemViewerLocation(Item item) {
        for (ItemViewer itemViewer : itemViewers) {
            if (itemViewer.getItem().equals(item)) {
                return itemViewer.getLocation();
            }
        }
        return null;
    }
    
    /**
     * getMidWidthPoint() returns the horizontal mid point of a component 
     * @param component
     * @return Point
     */
    private Point getMidWidthPoint(JComponent component) {
        Point p = component.getLocation();
        int midWidth = (int) (component.getWidth() / 2);
        return new Point(p.x + midWidth, p.y);
    }
    
    /**
     * getMidHeightPoint() returns the vertical mid point of a component 
     * @param component
     * @return Point
     */
    private Point getMidHeightPoint(JComponent component) {
        Point p = component.getLocation();
        int midHeight = (int) (component.getHeight() / 2);
        return new Point(p.x, p.y + midHeight);
    }
    
    public void paintComponent(Graphics g) {
        Point p0 = getMidHeightPoint(itemViewers.get(0));
        g.setColor(Color.BLACK);
        
        for (JComponent itemViewer : itemViewers) {
            Point p1 = getMidHeightPoint(itemViewer);
            
            g.drawLine(p0.x, p0.y, p1.x, p1.y);
            g.drawLine(p0.x, p0.y+1, p1.x, p1.y+1);
        }
    }
}
