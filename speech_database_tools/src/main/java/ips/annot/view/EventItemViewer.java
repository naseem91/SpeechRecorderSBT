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
 * Created on 19.08.2013
 *
 * Project: SpeechDatabaseTools
 * Original author: draxler
 */
package ips.annot.view;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import ips.annot.model.db.EventItem;

public class EventItemViewer extends ItemViewer {

    private final int MIN_WIDTH = 32;
    private final int MIN_HEIGHT = 16;
    private final int X_PADDING = 4;
    private final int Y_PADDING = 2;
    
    private Font font = new Font("Sans-serif", Font.PLAIN, 12);
    private FontMetrics fontMetrics = getFontMetrics(font);
    
    private int lineHeight;
    private int width = MIN_WIDTH;
    private int height = MIN_HEIGHT;
    
    private EventItem eventItem;
    
    public EventItemViewer(EventItem eventItem) {
        super(eventItem);
        this.eventItem = eventItem;
        createGUI();
    }
    
    private void createGUI() {
        setLayout(null);
        setOpaque(true);
        setBackground(Color.WHITE);

        width = fontMetrics.stringWidth(eventItem.getLabelText()) + (2 * X_PADDING);
        if (width < MIN_WIDTH) {
            width = MIN_WIDTH;
        }
        lineHeight = fontMetrics.getHeight() + fontMetrics.getLeading() + fontMetrics.getDescent();
        height = 2 * lineHeight + (2 * Y_PADDING);
        if (height < MIN_HEIGHT) {
            height = MIN_HEIGHT;
        }
        setSize(width, height);
    }
    
    
    public void paintComponent(Graphics g) {
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        
        g.setColor(Color.BLACK);
        g.drawString(eventItem.getLabelText(), 1, lineHeight);
        g.drawString("" + eventItem.getSamplepoint(), 1, 2 * lineHeight);
        
        g.drawRect(0, 0, width - 1, height - 1);
    }
}
