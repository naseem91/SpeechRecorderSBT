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
 * Created on 16.07.2013
 *
 * Project: SpeechDatabaseTools
 * Original author: draxler
 */
package ips.annot.model.emu;

import java.io.File;
import java.util.HashMap;
import java.util.Vector;

import ips.annot.model.db.AttributeDefinition;
import ips.annot.model.db.Database;
import ips.annot.model.db.LevelDefinition;
import ips.annot.model.db.LinkDefinition;
import ips.annot.model.db.Schema;

public class EmuTemplateConverter {
    
    public static final String SEGMENT = "SEGMENT";
    public static final String EVENT = "EVENT";
    
    private HashMap<String, EmuTierInfo> segmentTiers = new HashMap<String, EmuTierInfo>();
    private HashMap<String, EmuTierInfo> eventTiers = new HashMap<String, EmuTierInfo>();
    private HashMap<String, EmuPathInfo> filePaths = new HashMap<String, EmuPathInfo>();
    private HashMap<String, EmuTrackInfo> tracks = new HashMap<String, EmuTrackInfo>();
    
    private Schema schema;
    
    public EmuTemplateConverter(Database database) {
        schema = database.getSchema();
    }
    
    public HashMap<String, EmuTierInfo> getSegmentTiers() {
        return segmentTiers;
    }

    public HashMap<String, EmuTierInfo> getEventTiers() {
        return eventTiers;
    }

    public HashMap<String, EmuPathInfo> getFilePaths() {
        return filePaths;
    }

    public HashMap<String, EmuTrackInfo> getTracks() {
        return tracks;
    }

    private LevelDefinition addTierDefinitionByName(String tierName) {
        LevelDefinition tierDefinition = schema.getLevelDefinitionByName(tierName);
        // check if the tierDefinition already contains an attribute with the same name
        for (AttributeDefinition aDef : tierDefinition.getAttributeDefinitions()) {
            if (tierName.equals(aDef.getName())) {
                return tierDefinition;
            }
        }
        // tierDefinition does not contain an attribute with the same name, so 
        // add an attribute with this name
        AttributeDefinition attributeDefinition = new AttributeDefinition();
        attributeDefinition.setName(tierName);
        attributeDefinition.setType(AttributeDefinition.TEXT);
        tierDefinition.addAttributeDefinition(attributeDefinition);
        return tierDefinition;
    }
    
    /**
     * importDatabase() reads an Emu template file and adds the schema information to 
     * a database abject.
     * 
     * An Emu template file contains information about the hierarchy of tiers,
     * tier attributes and allowed tier labels, amongst others.
     * 
     * @param database
     * @param lines
     * @return a database object
     */
    public Database importTemplate(Database database, Vector<String> lines) {
        
        for (int i = 0; i < lines.size(); i++) {
            String [] items = lines.get(i).split("[\\t ]+");
            if (items[0].equalsIgnoreCase("level")) {
                // constraint definition line
                String tierName = items[1];
                LevelDefinition subTierDefinition = addTierDefinitionByName(tierName);
                
                LinkDefinition linkDefinition = new LinkDefinition();
                
                if (items.length == 2) {
                    // root tier, i.e. no supertier
                    linkDefinition.setSuperTier(null);
                    linkDefinition.setSubTier(subTierDefinition);
                    linkDefinition.setType(LinkDefinition.INIT);
                    
                } else if (items.length == 3 || items.length == 4) {
                    // tier with supertier
                    
                    String superTierName = items[2];
                    LevelDefinition superTierDefinition = addTierDefinitionByName(superTierName);

                    linkDefinition.setSuperTier(superTierDefinition);
                    linkDefinition.setSubTier(subTierDefinition);
  
                    if (items.length == 4) {
                        linkDefinition.setType(LinkDefinition.MANY_TO_MANY);
                    } else {
                        linkDefinition.setType(LinkDefinition.ONE_TO_MANY);
                    }
                }

                schema.getConstraints().add(linkDefinition);
                
                System.out.println(tierName.toString());
            } else if (items[0].equals("label")) {
                // attribute definition line
                String tierName = items[1];
                LevelDefinition tierDefinition = schema.getLevelDefinitionByName(tierName);
                String attributeName = items[2];
                AttributeDefinition attributeDefinition = new AttributeDefinition();
                attributeDefinition.setName(attributeName);
                attributeDefinition.setType(AttributeDefinition.TEXT);
                tierDefinition.addAttributeDefinition(attributeDefinition);
                
            } else if (items[0].equals("legal")) {
                
            } else if (items[0].equals("labfile")) {
                // labfile Phonetic :type SEGMENT :extension lab :time-factor 1000 
                float timeFactor = Float.parseFloat(items[7]);
                EmuTierInfo tierInfo = new EmuTierInfo(items[1], items[3], items[5], timeFactor);
                if (items[3].equals(SEGMENT)) {
                    segmentTiers.put(items[1], tierInfo);
                } else if (items[3].equals(EVENT)) {
                    eventTiers.put(items[1], tierInfo);
                }
            } else if (items[0].equals("path")) {
                EmuPathInfo pathInfo = new EmuPathInfo(items[1], new File(items[2]));
                filePaths.put(items[1], pathInfo);
                
            } else if (items[0].equals("track")) {
                EmuTrackInfo trackInfo = new EmuTrackInfo(items[1], items[2]);
                tracks.put(items[1], trackInfo);
                
            } else if (items[0].equals("set")) {
                
            }
        }
        return database;
    }
}
