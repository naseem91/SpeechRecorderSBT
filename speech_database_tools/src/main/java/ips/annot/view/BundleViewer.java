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
 * Created on 16.08.2013
 *
 * Project: SpeechDatabaseTools
 * Original author: draxler
 */
package ips.annot.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;

import javax.swing.JPanel;

import ips.annot.model.db.Bundle;
import ips.annot.model.db.EventItem;
import ips.annot.model.db.Item;
import ips.annot.model.db.Level;
import ips.annot.model.db.Link;
import ips.annot.model.db.SegmentItem;

public class BundleViewer extends JPanel {

    private final int MIN_WIDTH = 800;
    private final int MIN_HEIGHT = 600;
    
    private final int PREF_WIDTH = 1200;
    private final int PREF_HEIGHT = 800;
    
    private final int MAX_WIDTH = 3072;
    private final int MAX_HEIGHT = 2048;
    
    private Bundle bundle;
    
    private HashMap<Item, ItemViewer> itemViewerMap = new HashMap<Item, ItemViewer>();
    private HashMap<Level, Point> tierMap = new HashMap<Level, Point>();
    
    private int width = PREF_WIDTH;
    private int height = PREF_HEIGHT;
    
    public BundleViewer (Bundle bundle) {
        super();
        
        this.bundle = bundle;
        
        createGUI();
    }

    private void createGUI() {
        setLayout(null);
        setOpaque(true);
        setSize(width, height);
        
        int i = 0;
        int tierHeight = height / bundle.getLevels().size();
        
        for (Level tier : bundle.getLevels()) {

            Point tierOrigin = new Point(0, i * tierHeight);
            i = i + 1;
            tierMap.put(tier, tierOrigin);
            
            int sumViewerWidth = 0;
            
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
                
                itemViewerMap.put(item, viewer);
                sumViewerWidth = sumViewerWidth + viewer.getWidth();
            }
            
            int x_spacing = (width - sumViewerWidth) / tier.getItems().size();
            
            Point itemViewerLocation = new Point(tierOrigin.x + (x_spacing / 2), tierOrigin.y);
            
            for (Item item : tier.getItems()) {
                ItemViewer viewer = itemViewerMap.get(item);
                add(viewer);
                viewer.setLocation(itemViewerLocation);
                itemViewerLocation.x = itemViewerLocation.x + viewer.getWidth() + x_spacing;
            }
        }
        repaint();
    }
    
    
    
    /**
     * paintComponent() draws the links between item viewers.
     */
    
    public void paintComponent(Graphics g) {
        
        for (Link link : bundle.getLinksAsSet()) {
            
            Item fromItem = link.getFrom();
            Level fromTier = fromItem.getLevel();
            ItemViewer fromItemViewer = itemViewerMap.get(fromItem);
            
            Item toItem = link.getTo();
            Level toTier = toItem.getLevel();
            ItemViewer toItemViewer = itemViewerMap.get(toItem);
            
            if (fromItemViewer != null && toItemViewer != null) {
                g.setColor(Color.RED);
                
                Point fromPoint = null;
                Point toPoint = null;
                
                if (tierMap.get(fromTier).y < tierMap.get(toTier).y) {
                    // fromTier is above toTier
                    fromPoint = fromItemViewer.getBottomMid();
                    toPoint = toItemViewer.getTopMid();
                } else {
                    // fromTier is below toTier
                    fromPoint = fromItemViewer.getTopMid();
                    toPoint = toItemViewer.getBottomMid();
                }
                
                g.drawLine(fromPoint.x, fromPoint.y, toPoint.x, toPoint.y);
                
            } else {
//                System.err.println("ERROR: link contains null items or item viewers: " + link.getLabel());
            }
        }
    }
}
