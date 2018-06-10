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
package ips.annot.model.db;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

/**
 * Schema contains a description of the database. This description is a set of TierDefinitions and
 * a set of Constraints of the allowed relationship between tiers and the items within the
 * tiers.
 * 
 * Discussion: where to put the tier information? Should the schema contain only TierDefinitions 
 * that per se do not contain data, or tiers, that may contain items? 
 * 
 * With TierDefinitions in the Schema, a schema may contain information not present in 
 * actual tiers (e.g. because a given tier is not present in a given annotation. 
 * 
 * @author draxler
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Schema {

    private Set<LevelDefinition> levelDefinitions = new HashSet<LevelDefinition>();
    private Set<LinkDefinition> constraints = new HashSet<LinkDefinition> ();

    public Set<LevelDefinition> getTierDefinitions() {
        return levelDefinitions;
    }

    public void setTierDefinitions(Set<LevelDefinition> tierDefinitions) {
        this.levelDefinitions = tierDefinitions;
    }

    public void addLevelDefinition(LevelDefinition tierDefinition) {
        levelDefinitions.add(tierDefinition);
    }
    
    public LevelDefinition getLevelDefinitionByName(String tierName) {
        for (LevelDefinition tierDefinition : levelDefinitions) {
            if (tierDefinition.getName().equals(tierName)) {
                return tierDefinition;
            }
        }
        LevelDefinition tierDefinition = new LevelDefinition();
        tierDefinition.setName(tierName);
        addLevelDefinition(tierDefinition);
        return tierDefinition;
    }
    
    public Set<LinkDefinition> getConstraints() {
        return constraints;
    }
    
    private void setConstraints(Set<LinkDefinition> constraints) {
        this.constraints = constraints;
    }

    //--- compute schema hierarchy, e.g. to draw it in a GUI
    
    /**
     * getRootTiers() returns a set of LinkDefinitions of type INIT
     * @return set of link definitions
     */
    public Set<LinkDefinition> getRootTiers() {
        HashSet<LinkDefinition> roots = new HashSet<LinkDefinition>();
        for (LinkDefinition ld : getConstraints()) {
            if (ld.getType().equals(LinkDefinition.INIT)) {
                roots.add(ld);
            }
        }
        return roots;
    }
    
    /**
     * getSubTiers() returns the list of linked (i.e. subtier) 
     * tiers.
     * 
     * @param linkDef link definition
     * @return list of link definitions
     */
    public List<LinkDefinition> getSubTiers(LinkDefinition linkDef) {
        Vector<LinkDefinition> subtiers = new Vector<LinkDefinition>();
        for(LinkDefinition ld : getConstraints()) {
            if (linkDef.getSubTier().equals(ld.getSuperTier())) {
                subtiers.add(ld);
            }
        }
        return subtiers;
    }
    
    
    public List getPaths(LinkDefinition current, List<LinkDefinition> lpath) {
        if(getSubTiers(current).size() == 0) {
            lpath.add(current);
            return lpath;
        } else {
            return lpath;
        }
    }
    
    
    
    public String toString() {
        StringBuffer buffer = new StringBuffer("Schema\tTier names: ");
        for (LevelDefinition tierDefinition : levelDefinitions) {
            buffer.append(tierDefinition.toString());
            buffer.append(", ");
        }
        buffer.append("Constraints: ");
        for (LinkDefinition constraint : constraints) {
            buffer.append(constraint.toString());
            buffer.append(", ");
        }
        return buffer.toString();
    }
}
