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
 * Created on 25.08.2013
 *
 * Project: SpeechDatabaseTools
 * Original author: draxler
 */
package ips.annot.model.emu;

import java.util.HashMap;
import java.util.Vector;

import ips.annot.model.db.Bundle;
import ips.annot.model.db.EventItem;
import ips.annot.model.db.Item;
import ips.annot.model.db.Level;
import ips.annot.model.db.Link;
import ips.annot.model.db.SegmentItem;

public class EmuLabelFileConverter {
    
    public static final String EMU_HIERARCHY_HEADER = "**EMU hierarchical labels**";

    public void importSegments(Vector<String>lines, Bundle bundle, String tiername) {
        // read the first three lines and check whether the file is an emu label file
        if (! lines.get(0).matches("signal \\w+")) {
            return;
        } 
        if (! lines.get(1).matches("nfields [0-9]+")) {
            return;
        }
        if (! lines.get(2).matches("#")) {
            return;
        }
        // valid label file found
        
        // the fourth line contains a dummy element with the begin timepoint
        String [] fields = lines.get(3).split("\\s");
        long beginSegment = 0l;
        
        if (fields.length == 3 && fields[2].equals("H#")) {
            double beginTmp = Double.parseDouble(fields[0]) * 1000;
            beginSegment = Math.round(beginTmp);
        } else {
            return;
        }
        
        Level segmentTier = bundle.getTierByName(tiername);
        int index = 0;
        
        for (int i = 4; i < lines.size(); i++) {
            fields = lines.get(i).split("\\s+");
            if (fields.length == 3) {
                SegmentItem segment = new SegmentItem();
                segment.setLabel(tiername, fields[2]);
                segment.setLevel(segmentTier);
                
                double tmp = Double.parseDouble(fields[0]) * 1000;
                long endSegment = Math.round(tmp);
                
                segment.setSampleStart(beginSegment);
                segment.setSampleDur(endSegment - beginSegment - 1);
                
                // now replace the old item with the new segment with timepoints
                // not only in the tier, but also in the links
                Item oldItem = segmentTier.getItems().get(index);
                for (Link link : bundle.getLinksAsSet()) {
//                    System.out.println("Link: " + link.getFrom().getTier().getName() + "." + link.getFrom().getLabel() + " -> " + link.getTo().getTier().getName() + "." + link.getTo().getLabel());
                    if (link.getTo().equals(oldItem)) {
                        link.setTo(segment);
                    }
                }
                segmentTier.getItems().remove(index);
                segmentTier.getItems().add(index, segment);
                
                beginSegment = endSegment;
                index = index + 1;
            }
        }
    }

    
    
    public void importEvents(Vector<String>lines, Bundle bundle, String tiername) {
        // read the first three lines and check whether the file is an emu label file
        if (! lines.get(0).matches("signal \\w+")) {
            return;
        } 
        if (! lines.get(1).matches("nfields [0-9]+")) {
            return;
        }
        if (! lines.get(2).matches("#")) {
            return;
        }
        // valid label file found
        
        // the fourth line contains a dummy element with the begin timepoint
        String [] fields = lines.get(3).split("\\s");
        
        Level eventTier = bundle.getTierByName(tiername);
        int index = 0;
        
        for (int i = 3; i < lines.size(); i++) {
            fields = lines.get(i).split("\\s+");
            if (fields.length == 3) {
                EventItem event = new EventItem();
                event.setLabel(tiername, fields[2]);
                event.setLevel(eventTier);
                
                double tmp = Double.parseDouble(fields[0]) * 1000;
                long samplePoint = Math.round(tmp);
                
                event.setSamplepoint(samplePoint);
                
                // now replace the old item with the new event with timepoints
                // not only in the tier, but also in the links
                Item oldItem = eventTier.getItems().get(index);
                for (Link link : bundle.getLinksAsSet()) {
//                    System.out.println("Link: " + link.getFrom().getTier().getName() + "." + link.getFrom().getLabel() + " -> " + link.getTo().getTier().getName() + "." + link.getTo().getLabel());
                    if (link.getTo().equals(oldItem)) {
                        link.setTo(event);
                    }
                }
                eventTier.getItems().remove(index);
                eventTier.getItems().add(index, event);
                
                index = index + 1;
            }
        }
    }

    
    
    public void importItems(Vector<String>lines, Bundle bundle) {
        String context = "";
        String [] attributes = null;
        Level tier = null;
        Integer itemNo;
        Integer maxItemNo;
        boolean validContext = false;
        HashMap<Integer, Item> items = new HashMap<Integer, Item>();
        
        // read the first two lines and check whether the file is an emu hierarchy file and get the maximum item count
        if (! lines.get(0).equals(EMU_HIERARCHY_HEADER)) {
            return;
        }
        try {
            maxItemNo = Integer.valueOf(lines.get(1).trim(), 10);
        } catch (NumberFormatException e) {
            return;
        }
        
        // read the context lines, i.e. a block of lines beginning with a tier name followed by lines beginning with an item number
        // e.g.
        // Word Word Accent Text 
        // 2 C S amongst 
        // 24 F W her 
        // 30 C S friends 

        // if the line is not part of a context, then treat the line as containing links between items
        // e.g.
        // 24 104 120 154 181 
        // 30 105 121 122 123 124 125 155 156 157 158 159 182 

        // start from the third line!
        for (int lineNo = 2; lineNo < lines.size(); lineNo++) {
            String line = lines.get(lineNo).trim();
            
//            System.out.println(lineNo + "\t" + line);
            String [] fields = line.split("\\s+");
            
            // if the line is empty then the current context has ended
            if (fields[0].matches("")) {
                validContext = false;
            } else {
                
                // check whether the line defines a new context
                if (fields[0].matches("[a-zA-Z_]+")) {
                    context = fields[0];
                    attributes = line.split(" ");
                    tier = bundle.getTierByName(context);
                    if (tier != null) {
                        validContext = true;
                    } else {
                        validContext = false;
                    }
                    
                // if the line begins with an integer and there is a valid context, then create a new item from the line
                // and new Links with these items.
                // NOTE: fields are separated by single blanks, and the fields must be at least one character long, i.e. 
                // a field is either a single blank or some sequence of characters not containing a blank
                } else if (fields[0].matches("[0-9]+")) { 
                    try {
                        itemNo = Integer.valueOf(fields[0], 10);
                        
                        // the last line of the file contains a 0 which is not a valid item number
                        if (itemNo.intValue() == 0) {
                            return;
                        }
                        
                        if (validContext) {
                            // reading the context part of the hlb file
                            Item item = new Item();
                            item.setLevel(tier);
                            String [] values = line.split(" ");
                            if (attributes.length == values.length) {
                                for (int i = 0; i < attributes.length; i++) {
                                    item.setLabel(attributes[i], values[i]);
                                }
                            } else {
                                // should only be called for the top-most node of the hierarchy
                                item.setLabel(attributes[1], "");
                            }
                            items.put(itemNo, item);
                            tier.getItems().add(item);
                        } else {
                            // now reading the links part of the hlb file
                            Item fromItem = items.get(itemNo);
        
                            // start to read all item indices beginning at the second field in the line
                            for (int i = 1; i < fields.length; i++) {
                                try {
                                    Integer index = Integer.valueOf(fields[i], 10);
                                    Item toItem = items.get(index);
                                    
                                    // now check whether the toItem meets the constraints of the tier - if so, create a link between the items
                                    if (bundle.isValidConstraint(fromItem, toItem)) {
                                        Link link = new Link();
                                        link.setFrom(fromItem);
                                        link.setTo(toItem);
                                        link.setLabel(fromItem.getLevel().getName() + "-" + toItem.getLevel().getName());
                                        bundle.getLinksAsSet().add(link);
                                        System.out.println("Link " + fromItem.getLevel().getName() + "." + fromItem.toString() + " -> " + toItem.getLevel().getName() + "." + toItem.toString());
//                                        fromItem.getTier().getBundle().getLinks().add(link);
                                    } else {
//                                        System.err.println("Invalid constraint: " + fromItem.getLabel() + " =/=> " + toItem.getLabel());
                                    }
                                } finally {
                                    
                                }
                            }
                        }
                    } finally {}
                }
            }
        }
    }
}
