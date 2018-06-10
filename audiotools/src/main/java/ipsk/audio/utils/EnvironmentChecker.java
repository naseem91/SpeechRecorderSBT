//    IPS Java Audio Tools
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Jun 12, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.utils;

/**
 * Checks the current JVM version for audio bugs and recommended incremental
 * garbage collection. Sun's JAVA JRE 1.5.0 up to Update 6 has a serious
 * JavaSound Bug (ID 6261423) under Windows, so we recommend to use JAVA JRE
 * 1.5.0 Update 7 for speech recordings. This class checks for bug free
 * versions.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

public class EnvironmentChecker {

    public EnvironmentChecker() {
        super();

    }

    /**
     * Check JRE version.
     * 
     * @return null if OK, warning description else
     */
    public String checkJRE() {
        String osName = System.getProperty("os.name");
        String jreVersion = System.getProperty("java.version");
        String jreVendor = System.getProperty("java.vendor");
        if (osName.equals("Windows")) {
            if (!jreVendor.equals("Sun"))
                return new String("Unknown JRE vendor: " + jreVendor + ".");

            if (jreVersion.startsWith("1.5.0")) {
                String updateVersionStr=jreVersion.substring(jreVersion.indexOf("_")+1);
                int updateVersion=Integer.parseInt(updateVersionStr);
                if(updateVersion >=7){
//                if (jreVersion.equals("1.5.0_07")
//                        || jreVersion.equals("1.5.0_08")) {
                    return null;
                } else {
                    return new String(
                            "JRE Version "
                                    + jreVersion
                                    + " for Windows contains an audio bug ID 6261423 !\nPlease update to at least JRE 1.5.0 Update 7.");
                }
            }
            if (jreVersion.compareTo("1.6") > 0)
                return null;
            return new String("Windows JRE version " + jreVersion
                    + " not supported.\nPlease update your JAVA environment.");
        } else {
            if (jreVersion.compareTo("1.5.0") < 0)
                return new String("JRE version " + jreVersion
                        + " not supported !\nPlease update your JAVA version.");
            return null;
            //return new String("The audio reliability under " + osName
            //        + " is not yet tested !");
        }

    }
    
    
    // TODO Planned for JRE version 1.5
    
//    /**
//     * Check for incremental garbage collection.
//     * @return
//     */
//    public String checkGarbageCollection(){
//       
//    }
    

    public static void main(String[] args) {
        EnvironmentChecker envChecker = new EnvironmentChecker();
        System.out.println("Check JRE: " + envChecker.checkJRE());
        System.exit(0);

    }

}
