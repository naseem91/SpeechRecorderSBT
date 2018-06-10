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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import ips.annot.model.db.Item;

public class ItemViewer extends JPanel implements MouseListener {
    public static Color COL_NEUTRAL = Color.WHITE;
    public static Color COL_MARKED = Color.RED;
    public static Color COL_HIGHLIGHTED = Color.YELLOW;
    public static final String MARKED = "marked";
    public static final String HIGHLIGHTED = "highlighted";

    private final int X_PADDING = 4;
    private final int Y_PADDING = 2;
    private final int MIN_WIDTH = 32;
    private final int MIN_HEIGHT = 16;
    
    private Font font = new Font("Sans-serif", Font.PLAIN, 12);
    private FontMetrics fontMetrics = getFontMetrics(font);

    private Item item;
    private boolean marked = false;
    private boolean highlighted = false;
    private int width;
    private int height;
    
    public ItemViewer(Item item) {
        super();
        this.item = item;
        createGUI();
    }

    private void createGUI() {
        setLayout(null);
        setOpaque(true);
        setSize(getSize());
        addMouseListener(this);
        this.setBackground(COL_NEUTRAL);
    }

    public Item getItem() {
        return item;
    }
    
    private void toggleMarked() {
        if (isMarked()) {
            setMarked(false);
            setBackground(COL_NEUTRAL);
        } else {
            setMarked(true);
            setBackground(COL_MARKED);
        }
    }

    /**
     * returns the space needed to draw the item. If the space needed is smaller than the minimum space, then width or height
     * are set to the minimum values.
     */
    public Dimension getSize() {
        width = fontMetrics.stringWidth(item.getLabelText()) + (2 * X_PADDING);
        if (width < MIN_WIDTH) {
            width = MIN_WIDTH;
        }
        height = fontMetrics.getHeight() + (2 * Y_PADDING);
        if (height < MIN_HEIGHT) {
            height = MIN_HEIGHT;
        }
        
//        System.out.println(item.getTier().getName() + ".'" + item.getLabel() + "': (" + width + ", " + height + ")");
        return new Dimension(width, height);
    }

    public Point getTopMid () {
        int x = getLocation().x + (width / 2);
        int y = getLocation().y;
        return new Point(x, y);
    }
    
    public Point getBottomMid () {
        int x = getLocation().x + (width / 2);
        int y = getLocation().y + height;
        return new Point(x, y);
    }

    public Point getLeftMid() {
        int x = getLocation().x;
        int y = getLocation().y + (height / 2);
        return new Point(x, y);
    }
    
    public Point getRightMid() {
        int x = getLocation().x + width;
        int y = getLocation().y + (height / 2);
        return new Point(x, y);
    }
    
    
    public void paintComponent(Graphics g) {
        int h = fontMetrics.getHeight();
        int l = fontMetrics.getLeading();
        int d = fontMetrics.getDescent();
//        System.out.println(item.getTier().getName() + ".'" + item.getLabel() + "': [" + h + ", " + l + ", " + d + "]");
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.drawString(item.getLabelText(), X_PADDING, Y_PADDING + h - d - l);
        g.drawRect(0, 0, width - 1, height - 1);
    }

    
    private void setMarked(boolean marked) {
        this.marked = marked;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setHighlighted(boolean highlighted) {
        this.highlighted = highlighted;
    }

    public boolean isHighlighted() {
        return highlighted;
    }

    @Override
    public void mouseClicked(MouseEvent arg0) {}

    @Override
    public void mouseEntered(MouseEvent arg0) {
        if (!isMarked()) {
            setBackground(COL_HIGHLIGHTED);
        } else {
            setBackground(COL_MARKED);
        }
    }

    @Override
    public void mouseExited(MouseEvent arg0) {
        if (!isMarked()) {
            setBackground(COL_NEUTRAL);
        } else {
            setBackground(COL_MARKED);
        }
    }

    @Override
    public void mousePressed(MouseEvent arg0) {}

    @Override
    public void mouseReleased(MouseEvent arg0) {
        toggleMarked();
    }
}