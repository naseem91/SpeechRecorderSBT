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
 * Created on 08.03.2010
 *
 * Project: SpeechDatabaseBrowser
 * Original author: draxler
 */
package ips.annot.view;

import java.awt.Color;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JComponent;

import ips.annot.model.db.SegmentItem;


public class SegmentsViewer extends JComponent {

    private List<SegmentItem> intervalItems;
    
    public SegmentsViewer(List<SegmentItem> intervalItems) {
        super();
        this.intervalItems = intervalItems;
        
        setLayout(null);
        setOpaque(true);
        setSize(getPreferredSize().width,getPreferredSize().height);
    }
    

    //TODO: use total file length instead of speech segment - otherwise, segments will be shown on right edge only
    private long totalLength() {
//        long begin = segments.get(0).getBegin();
        long begin = 0l;
        SegmentItem lastItem = intervalItems.get(intervalItems.size() - 1);
        long end = lastItem.getSampleStart() + lastItem.getSampleDur();
        return end - begin;
    }
    
    public void paintComponent(Graphics g) {
        int w = getSize().width;
    
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getSize().width, getSize().height);
        g.setColor(Color.BLACK);

        int xStep = (int) (totalLength() / w);
        
        for (int i = 0; i < intervalItems.size(); i++) {
            SegmentItem ii = intervalItems.get(i);
            int xl = (int) (ii.getSampleStart() / xStep);
            int xr = (int) ((ii.getSampleStart() + ii.getSampleDur()) / xStep);
            String label = ii.getLabelText();
            
            // don't draw the first left boundary - it doesn't look nice
            if (i > 0) {
                g.drawLine(xl, 0, xl, getSize().height);
            }
            
            int midx = xl + ((xr - xl) / 2) - 3;
            int midy = getSize().height / 2;
            
            g.drawString(label, midx, midy + 6);
        }
    }

    public List<SegmentItem> getIntervalItems() {
        return intervalItems;
    }

    public void setIntervalItems(List<SegmentItem> intervalItems) {
        this.intervalItems = intervalItems;
    }
}
