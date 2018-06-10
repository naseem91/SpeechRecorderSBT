//    IPS Java Utils
// 	  (c) Copyright 2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.io;

import java.io.File;

import javax.swing.filechooser.FileFilter;

// TODO Test !!!

/**
 * @author klausj
 *
 */
public class FileFilterByExtension extends FileFilter{
    
    public class Extension{
        public String extension;
        public String extensionMatch;
    }
    private String description;
    private String baseDescription;
    protected Extension[] exts;
//    protected String[] extMatches;
    private String[] extensions;
    public FileFilterByExtension() {
        super();
    }
    public FileFilterByExtension(String baseDescription,String[] extensions) {
       super();
       this.baseDescription=baseDescription;
       this.extensions=extensions;
       update();
    }
    
    public void setBaseDescription(String baseDescription){
        this.baseDescription=baseDescription;
        update();
    }
    
    public void setExtensions(String[] extensions){
        this.extensions=extensions;
        update();
    }
        
        
    public void update(){
        StringBuffer descriptionSB=new StringBuffer(baseDescription);
        
        int extCount=extensions.length;
        exts=new Extension[extCount];
        if(extCount>0){
        descriptionSB.append(" (");
        }
        for(int i=0;i<extCount;i++){
            exts[i]=new Extension();
            String ext=extensions[i];
            exts[i].extension=ext;
            int extLen=ext.length();
            StringBuffer extMatch=new StringBuffer(".*\\.");
            for(int c=0;c<extLen;c++){
                extMatch.append('[');
                char extC=ext.charAt(c);
                char lcExtC=Character.toLowerCase(extC);
                char ucExtC=Character.toUpperCase(extC);
                extMatch.append(lcExtC);
                extMatch.append(ucExtC);
                extMatch.append(']');
            }
            extMatch.append('$');
            
            // result e.g. for PNG ".*\\.[pP][nN][gG]$"
            
            exts[i].extensionMatch=extMatch.toString();
            descriptionSB.append("*.");
            descriptionSB.append(ext);
            if(i<extCount-1){
                descriptionSB.append(",");
            }
        }
        if(extCount>0){
            descriptionSB.append(")");
            }
        description=descriptionSB.toString();
    }
    
    public boolean accept(File f){
        if(f.isDirectory())return true;
        String fName=f.getName();
        for(Extension e:exts){
            if(fName.matches(e.extensionMatch)){
                return true;
            }
        }
      return false;
    }
    
    public String extension(File f){
        if(f.isDirectory())return null;
        String fName=f.getName();
        for(Extension e:exts){
            String extMatch=e.extensionMatch;
            if(fName.matches(extMatch)){
                return e.extension;
            }
        }
      return null;
    }

   @Override
   public String getDescription() {
       return description;
   }
   
}
