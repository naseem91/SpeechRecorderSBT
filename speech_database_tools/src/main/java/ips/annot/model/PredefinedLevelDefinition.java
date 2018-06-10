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
 * Date  : 21.07.2015
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ips.annot.model;

import ips.annot.model.db.LevelDefinition;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public enum PredefinedLevelDefinition {
    PRT("Prompt",LevelDefinition.ITEM), TPL("Template",LevelDefinition.ITEM),ORT("Orthography",LevelDefinition.ITEM),KAN("Canonic",LevelDefinition.ITEM),MAU("MAUS segmented",LevelDefinition.SEGMENT);

    PredefinedLevelDefinition(String value,String type) {
        this.value = value;
        this.type=type;
        this.levelDefinition=new LevelDefinition(this);
    }
    private final String value;
    private final String type;
    private final LevelDefinition levelDefinition;

    public LevelDefinition getLevelDefinition() {
        return levelDefinition;
    }
    public String value() {
        return value; 
    }
    public String toString() {
        return value; 
    }
    
    public String getKeyName(){
        return (super.toString());
    }
    public static PredefinedLevelDefinition getByValue(String value){
        for(PredefinedLevelDefinition pp:PredefinedLevelDefinition.values()){
            if(pp.value.equals(value)){
                return pp;
            }
        }
        return null;
    }
    
    public static void main(String[] args){
        PredefinedLevelDefinition mausLd=PredefinedLevelDefinition.MAU;
        System.out.print(mausLd+" "+mausLd.getKeyName());
        
    }
    public String getType() {
        return type;
    }
    
}
