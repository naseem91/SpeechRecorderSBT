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
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import ips.annot.model.db.LevelDefinition;

public class TierDefinitionViewer extends JPanel implements MouseListener, MouseMotionListener {

    private static int NEUTRAL = 0;
    private static int HILITE = 1;
    private static int DISABLED = 2;
    private static int SELECTED = 3;

    private LevelDefinition tierDefinition;
    private int state = NEUTRAL;
    private int oldState = state;
    
    private Point mousepress;
    private Point mouserelease;
//    private Point mouselocation;
    private Point location;
    private int width;
    private int height;
    
    public TierDefinitionViewer() {
        super();
        setState(NEUTRAL);
        createGUI();
    }

    private void createGUI() {
        width = 100;
        height = 20;
        setSize(width, height);
        setOpaque(true);
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    
    public LevelDefinition getTierDefinition() {
        return tierDefinition;
    }

    public void setTierDefinition(LevelDefinition tierDefinition) {
        this.tierDefinition = tierDefinition;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getOldState() {
        return oldState;
    }

    public void setOldState(int oldState) {
        this.oldState = oldState;
    }

    
    public Point getTopMid() {
        int x = getLocation().x + (getWidth() / 2);
        return new Point(x, getLocation().y);
    }
    
    public Point getBottomMid() {
        int x = getLocation().x + (getWidth() / 2);
        return new Point(x, getLocation().y + getHeight());
    }
    
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); 
//        System.out.println("TierDefinition.paintComponent()");

        Color bgColor = null;
        Color lineColor = null;
        Color textColor = null;
        if (getState() == NEUTRAL) {
            bgColor = ColorScheme.NEUTRAL_BG_COLOR;
            lineColor = ColorScheme.NEUTRAL_LINE_COLOR;
            textColor = ColorScheme.NEUTRAL_TEXT_COLOR;
        } else if (getState() == HILITE) {
            bgColor = ColorScheme.HILITE_BG_COLOR;
            lineColor = ColorScheme.HILITE_LINE_COLOR;
            textColor = ColorScheme.HILITE_TEXT_COLOR;
        } else if (getState() == SELECTED) {
            bgColor = ColorScheme.SELECTED_BG_COLOR;
            lineColor = ColorScheme.SELECTED_LINE_COLOR;
            textColor = ColorScheme.SELECTED_TEXT_COLOR;
        } else if (getState() == DISABLED) {
            bgColor = ColorScheme.DISABLED_BG_COLOR;
            lineColor = ColorScheme.DISABLED_LINE_COLOR;
            textColor = ColorScheme.DISABLED_TEXT_COLOR;
        }
        g.setColor(bgColor);
        g.fillRect(0,  0, width, height);
        g.setColor(lineColor);
        g.drawRect(0, 0, width - 1, height - 1);
        g.setColor(textColor);
        g.drawString(tierDefinition.getName(), 2, 15);
        
    }
    
    private void moveViewer(int x, int y) {
        int offset = 1;
        if (mousepress.x != x || mousepress.y != y) {
            repaint(location.x, location.y, width + offset, height + offset);
    //      System.out.println("MouseDragged: "+ mouselocation.getX() + ", " + mouselocation.getY());
            int translatex = x - mousepress.x;
            int translatey = y - mousepress.y;
            location.translate(translatex, translatey);
    
            setLocation(location);
            repaint(location.x, location.y, width + offset, height + offset);
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent arg0) {
//        System.out.println("MouseClicked(): " + location.x + ", " + location.y);
        if (getState() == NEUTRAL) {
            setState(HILITE);
        } else if (getState() == HILITE) {
            setState(NEUTRAL);
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {
        setOldState(state);
        setState(SELECTED);
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        setState(oldState);
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent event) {
        mousepress = event.getPoint();
        location = this.getLocation();
        moveViewer(mousepress.x, mousepress.y);
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        setState(oldState);
//        if (getState() == SELECTED) {
//            setState(NEUTRAL);
//        } else {
//            setState(SELECTED);
//        }
//        setOldState(state);
        repaint();
    }


    @Override
    public void mouseDragged(MouseEvent event) {
        moveViewer(event.getX(), event.getY());
    }


    @Override
    public void mouseMoved(MouseEvent arg0) {
        // TODO Auto-generated method stub
        
    }
}
