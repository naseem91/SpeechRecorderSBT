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
 * Created on 21.11.2013
 *
 * Project: SpeechDatabaseTools
 * Original author: draxler
 */
package ips.annot.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.JPanel;

import ips.annot.model.db.LevelDefinition;
import ips.annot.model.db.LinkDefinition;

public class LinkDefinitionViewer extends JPanel implements MouseMotionListener {

    private final static int HIERARCHICAL = 0;
    private final static int CIRCULAR = 1;
    
    private Set<LinkDefinition> linkDefinitions;
    private Map<LevelDefinition, TierDefinitionViewer> mapViewers = new HashMap<LevelDefinition, TierDefinitionViewer>();
    
    public LinkDefinitionViewer(Set<LinkDefinition> constraints) {
        super();
        setLinkDefinitions(constraints);
        createGUI();
    }
    
    public Dimension getPreferredSize() {
        return new Dimension(250,600);
    }
    
    public void addViewer(LevelDefinition definition) {
        if (! mapViewers.containsKey(definition)) {
            TierDefinitionViewer tdv = new TierDefinitionViewer();
            tdv.setTierDefinition(definition);
            tdv.addMouseMotionListener(this);
            mapViewers.put(definition, tdv);
        }
    }
    
    public Set<LinkDefinition> getConstraints() {
        return linkDefinitions;
    }

    public List<LinkDefinition> getRootConstraints() {
        List<LinkDefinition> rootConstraints = new Vector<LinkDefinition>();
        for (LinkDefinition c : getConstraints()) {
            if (c.getType() == LinkDefinition.INIT) {
                rootConstraints.add(c);
            }
        }
        return rootConstraints;
    }
    

    public void setLinkDefinitions(Set<LinkDefinition> linkDefinition) {
        this.linkDefinitions = linkDefinition;
    }

    public void rearrange(int order) {
        int x = 0; 
        int y = 0;
        if (order == CIRCULAR) {
            
        } else if (order == HIERARCHICAL) {
            
        }
    }

    /**
     * getAllPaths() returns all possible paths of TierDefinitions as specified 
     * by the set of constraints
     * 
     * getAllPaths() implements a breadth-first search through the constraints
     * graph
     * 
     * @param constraints
     * @return list of level definition paths 
     */
    private List<List<LevelDefinition>> getAllPaths(Set<LinkDefinition> constraints) {
        List<List<LevelDefinition>> paths = new Vector<List<LevelDefinition>>();

        return paths;
    }
    
    private void createGUI() {
        setLayout(null);
        setOpaque(true);
        setSize(250, 800);
        for (LinkDefinition c : linkDefinitions) {
            System.out.println("LinkDefinitionViewer(): " + c.toString());
            if (! c.getType().equals(LinkDefinition.INIT)) {
                addViewer(c.getSuperTier());
            }
            
            addViewer(c.getSubTier());
        }
        int y = 0;
        int x = 0;
        for (TierDefinitionViewer tdv : mapViewers.values()) {
            add(tdv);
            tdv.setLocation(new Point(x,y));
            System.out.println("LinkDefinitionViewer.createGUI(): " + x + ", " + y);
            y = y + 20;
            x = x + 10;
        }
        repaint();
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
//        System.out.println("ConstraintViewer.paintComponent()");
        for (LinkDefinition c : linkDefinitions) {
            if (! c.getType().equals(LinkDefinition.INIT)) {
                TierDefinitionViewer tdv1 = mapViewers.get(c.getSuperTier());
                TierDefinitionViewer tdv2 = mapViewers.get(c.getSubTier());
                Point p1 = tdv1.getLocation();
                Point p2 = tdv2.getLocation();
                if (c.getType().equals(LinkDefinition.MANY_TO_MANY)) {
                    g.setColor(Color.RED);
                } else {
                    g.setColor(Color.BLACK);
                }
                Point lp1;
                Point lp2;
                if (p1.y < p2.y) {
                    lp1 = tdv1.getBottomMid();
                    lp2 = tdv2.getTopMid();
                } else {
                    lp1 = tdv1.getTopMid();
                    lp2 = tdv2.getBottomMid();
                }
                g.drawLine(lp1.x, lp1.y, lp2.x, lp2.y);
            }
        }
    }


    @Override
    public void mouseDragged(MouseEvent event) {
        repaint();
//        System.out.println("MouseDragged: "+ event.getX() + ", " + event.getY() + ", " + event.getSource());
    }

    @Override
    public void mouseMoved(MouseEvent arg0) {
    }
}
