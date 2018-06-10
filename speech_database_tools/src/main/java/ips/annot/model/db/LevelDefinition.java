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
 * Created on 08.11.2013
 *
 * Project: SpeechDatabaseTools
 * Original author: draxler
 */
package ips.annot.model.db;

import java.util.List;
import java.util.Vector;

import ips.annot.model.PredefinedLevelDefinition;

public class LevelDefinition {

    public final static String ITEM = "ITEM";
    public final static String EVENT = "EVENT";
    public final static String SEGMENT = "SEGMENT";

    private String name;
    private String type;
    private List<AttributeDefinition> attributeDefinitions = new Vector<AttributeDefinition>();

    public LevelDefinition(){
        super();
    }
    public LevelDefinition(PredefinedLevelDefinition predefinedLevelDefinition){
        super();
        name=predefinedLevelDefinition.getKeyName();
        type=predefinedLevelDefinition.getType();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<AttributeDefinition> getAttributeDefinitions() {
        return attributeDefinitions;
    }

    public void setAttributeDefinitions(List<AttributeDefinition> attributeDefinitions) {
        this.attributeDefinitions = attributeDefinitions;
    }

    public void addAttributeDefinition(AttributeDefinition attributeDefinition) {
        attributeDefinitions.add(attributeDefinition);
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(getName() + "[" + getType() + "][");
        for (AttributeDefinition attributeDefinition : attributeDefinitions) {
            buffer.append(attributeDefinition.toString() + ",");
        }
        buffer.append("]");
        return buffer.toString();
    }
}
