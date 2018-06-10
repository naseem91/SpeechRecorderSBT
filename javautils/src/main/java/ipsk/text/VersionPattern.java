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

public class VersionPattern {

    /**
     * XML adapter converts version from/to string.
     * @author klausj
     *
     */
    public static class VersionPatternXMLAdapter extends XmlAdapter<String,VersionPattern> {
        /* (non-Javadoc)
         * @see javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
         */
        @Override
        public String marshal(VersionPattern vp) throws Exception {        
            return vp.toString();
        }
        /* (non-Javadoc)
         * @see javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
         */
        @Override
        public VersionPattern unmarshal(String v) throws Exception {
            return VersionPattern.parseString(v);
        }
    }
    public static class DigitPattern{
        
        private boolean wildcard;
        private Integer digit;
        public DigitPattern(int digit){
            super();
            wildcard=false;
            this.digit=digit;
        }
        public DigitPattern(){
            super();
            wildcard=true;
            this.digit=null;
        }
        
        public boolean matches(int digit){
            return (wildcard || (this.digit != null && this.digit.equals(digit))); 
        }
        public String toString(){
            if(wildcard){
                return "*";
            }else{
                return digit.toString();
            }
        }
    }
    
    private DigitPattern[] versionPattern;
    
    /**
     * 
     */
    public VersionPattern(DigitPattern[] versionPattern){
        super();
        this.versionPattern=versionPattern;
    }
    
    
    /**
     * Parse a version string.
     * @param versionString string to parse
     * @return the parsed version
     * @throws ParserException
     */
    public static VersionPattern parseString(String versionString) throws ParserException{
        String[] numberStrs=versionString.split("[.]");
        if(numberStrs==null || numberStrs.length==0){
            throw new ParserException("Could not parse version string");
        }
        DigitPattern[] vns=new DigitPattern[numberStrs.length];
        for(int i=0;i<numberStrs.length;i++){
            String numberStr=numberStrs[i];
            DigitPattern dp=null;
            if(numberStr.equals("*") || numberStr.equals("x")){
                dp=new DigitPattern();
            }else{
            try{
            int vn=Integer.parseInt(numberStrs[i]);
            dp=new DigitPattern(vn);
            }catch(NumberFormatException nfe){
                // convert the runtime exception to parser exception
                throw new ParserException(nfe);
            }
            }
            vns[i]=dp;
        }
        VersionPattern v=new VersionPattern(vns);
        return v;
    }
    
    public boolean matches(Version v){
        int[] vds=v.getVersion();
        int vdsLen=vds.length;
        int vpLen=versionPattern.length;
        if(vdsLen>vpLen){
            return false;
        }
        for (int i=0;i<vds.length;i++){
            int vd=vds[i];
            DigitPattern dp=versionPattern[i];
            if(!dp.matches(vd))return false;
        }
        return true;
    }
    
    public String toString(){
        StringBuffer sb=new StringBuffer();
        if(versionPattern!=null && versionPattern.length>0){
            for(int i=0;i<versionPattern.length-1;i++){
                sb.append(versionPattern[i]);
                sb.append(".");
            }
            sb.append(versionPattern[versionPattern.length-1]);
        }
        return sb.toString();
        
    }
   
}
