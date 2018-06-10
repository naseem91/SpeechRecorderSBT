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
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import ips.annot.model.db.SegmentItem;

public class IntervalItemViewer extends ItemViewer {

    private final int X_SPACE = 5;
    private final int Y_SPACE = 2;
    
    private Font font = new Font("Sans-serif", Font.PLAIN, 12);
    private FontMetrics fontMetrics = getFontMetrics(font);
    
    private int lineHeight;
    private int height;
    private int width;
    private int labelWidth;
    private int beginWidth;
    private int endWidth;
    
    private float xFactor = 1.0f;
    private float yFactor = 1.0f;
    
    private SegmentItem intervalItem;
    
    public IntervalItemViewer(SegmentItem intervalItem) {
        super(intervalItem);
        this.intervalItem = intervalItem;
        createGUI();
    }
    
    private void createGUI() {
        setLayout(null);
        setOpaque(true);
        setBackground(Color.WHITE);

        lineHeight = fontMetrics.getHeight() + fontMetrics.getLeading() + fontMetrics.getDescent();
        
        width = (int) (computeWidth() * xFactor) + 1;
        height = Y_SPACE + (3 * lineHeight) + Y_SPACE;
        
        setSize(width, height);
    }
    
    public float getxFactor() {
        return xFactor;
    }

    public void setxFactor(float xFactor) {
        this.xFactor = xFactor;
    }

    public float getyFactor() {
        return yFactor;
    }

    public void setyFactor(float yFactor) {
        this.yFactor = yFactor;
    }

    private int computeWidth() {
        labelWidth = fontMetrics.stringWidth(intervalItem.getLabelText());
        beginWidth = fontMetrics.stringWidth("" + intervalItem.getSampleStart());
        endWidth = fontMetrics.stringWidth("" + intervalItem.getSampleDur());
                
        int contentWidth = 0;
        
        if (labelWidth < beginWidth) {
            if (beginWidth < endWidth) {
                contentWidth = endWidth; 
            } else {
                contentWidth = beginWidth;
            }
        } else {
            contentWidth = labelWidth;
        }
        return X_SPACE + contentWidth + X_SPACE;
    }
    
    public void paintComponent(Graphics g) {
        
        if (isMarked()) {
            g.setColor(Color.RED);
        } else if (isHighlighted()) {
            g.setColor(Color.YELLOW);
        } else {
            g.setColor(Color.WHITE);
        }
        g.fillRect(0, 0, width, height);
        g.setColor(Color.BLACK);
        g.drawString(intervalItem.getLabelText(), (int) ((width - labelWidth) / 2), lineHeight);
        g.drawString("" + intervalItem.getSampleStart(), width - beginWidth - 2, 2 * lineHeight);
        g.drawString("" + intervalItem.getSampleDur(), width - endWidth - 2, 3 * lineHeight);
        
        g.drawRect(0, 0, width - 1, height - 1);
    }
}
