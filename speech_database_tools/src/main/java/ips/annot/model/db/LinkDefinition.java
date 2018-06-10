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
package ips.annot.model.db;

public class LinkDefinition {

    public static final String INIT = "INIT";
    public static final String ONE_TO_MANY = "ONE_TO_MANY";
    public static final String MANY_TO_ONE = "MANY_TO_ONE";
    public static final String MANY_TO_MANY = "MANY_TO_MANY";
    
    private LevelDefinition superTier;
    private LevelDefinition subTier;
    
    private String type;
    private String label = "";

    
    public LevelDefinition getSuperTier() {
        return superTier;
    }
    public void setSuperTier(LevelDefinition superTier) {
        this.superTier = superTier;
    }
    public LevelDefinition getSubTier() {
        return subTier;
    }
    public void setSubTier(LevelDefinition subTier) {
        this.subTier = subTier;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
    
    public boolean isValidTierLinkDefinition(LevelDefinition superTier, LevelDefinition subTier) {
        if (! getType().equals(INIT) && getSuperTier().equals(superTier) && getSubTier().equals(subTier)) {
            System.out.println("validTierLinkDefinition(): " + superTier.getName() + " -> " + subTier.getName());
            return true;
        } else {
            return false;
        }
    }
    
    public boolean isValidItemLinkDefinition(Item i1, Item i2) {
        LevelDefinition t1 = i1.getLevel().getDefinition();
        LevelDefinition t2 = i2.getLevel().getDefinition();
        
        return isValidTierLinkDefinition(t1, t2);
    }
    
    
    public String toString() {
        StringBuffer buffer = new StringBuffer("LinkDefinition");
        buffer.append(getLabel() + ": ");
        if (getType().equals(INIT)) {
            buffer.append("root: ");
            buffer.append(getSubTier().getName());
        } else {
            buffer.append(getSuperTier().getName() + " -> " + getSubTier().getName() + ": ");
            buffer.append(getType());
        }
        return buffer.toString();
    }
}
