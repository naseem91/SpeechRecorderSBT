//    IPS Java Utils
// 	  (c) Copyright 2009-2011
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

package ipsk.text;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Represents a version.
 * The version is stored as an array of integers.
 * @author klausj
 *
 */

public class Version implements Comparable<Version>{

    /**
     * XML adapter converts version from/to string.
     * @author klausj
     *
     */
    public static class VersionXMLAdapter extends XmlAdapter<String,Version> {
        /* (non-Javadoc)
         * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
         */
        @Override
        public String marshal(Version v) throws Exception {        
            return v.toString();
        }
        /* (non-Javadoc)
         * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
         */
        @Override
        public Version unmarshal(String v) throws Exception {
            return Version.parseString(v);
        }
    }
    
    private int[] version;
    

    /**
     * 
     */
    public Version(int[] version){
        super();
       this.version=version;
    }
    
    
    /**
     * Parse a version string.
     * @param versionString string to parse
     * @return the parsed version
     * @throws ParserException
     */
    public static Version parseString(String versionString) throws ParserException{
        String[] numberStrs=versionString.split("[.]");
        if(numberStrs==null || numberStrs.length==0){
            throw new ParserException("Could not parse version string");
        }
        int[] vns=new int[numberStrs.length];
        for(int i=0;i<numberStrs.length;i++){
            try{
            int vn=Integer.parseInt(numberStrs[i]);
            vns[i]=vn;
            }catch(NumberFormatException nfe){
                // convert the runtime exception to parser exception
                throw new ParserException(nfe);
            }
        }
        Version v=new Version(vns);
        return v;
    }



    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Version otherVersion) {
        int[] ovs=otherVersion.getVersion();
        int c=0;
        for(int i=0;i<version.length;i++){
            Integer vi=new Integer(version[i]);
            if(i>=ovs.length){
                if(vi>0){
                    return 1;
                }
            }else{
                c=vi.compareTo(ovs[i]);
                if(c!=0){
                    return c;
                }
            }
        }
        if(version.length<ovs.length){
            for(int i=version.length;i<ovs.length;i++){
                if(ovs[i]>0){
                    return -1;
                }
            }
        }
        return 0;
    }

    public String toString(){
        StringBuffer sb=new StringBuffer();
        if(version!=null && version.length>0){
            for(int i=0;i<version.length-1;i++){
                sb.append(version[i]);
                sb.append(".");
            }
            sb.append(version[version.length-1]);
        }
        return sb.toString();
    }

    public int[] getVersion() {
        return version;
    }
   
}
