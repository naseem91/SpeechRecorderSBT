//    IPS Java Utils
// 	  (c) Copyright 2015
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

package ipsk.util;

import java.awt.Window;
import java.util.regex.Pattern;


/**
 * @author klausj
 *
 */
public class SystemHelper {

	private String osName;
	private String javaVersion;
	private String javaVendor;
	
	private static volatile SystemHelper instance=null;
	
	private static final String OS_MAC_OS_X_NAME= "Mac OS X";
	private static final String OS_WINDOWS_NAME= "Windows";
	private static final String OS_LINUX_NAME= "Linux";
	private boolean isWindows=false;
	
	public SystemHelper(){
		super();
		osName=System.getProperty("os.name");
		// "Windows 7" ... "Windows 10"
		// "Mac OS X"
		// "Linux"
		javaVersion=System.getProperty("java.version");
		javaVendor=System.getProperty("java.vendor");
		Pattern winPattern = Pattern.compile("^"+OS_WINDOWS_NAME+".*", Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
		isWindows=winPattern.matcher(osName).matches();
	}
	public synchronized static SystemHelper getInstance(){
		if(instance==null){
			instance=new SystemHelper();
		}
		return instance;
	}
	
	public boolean isMacOSX(){
		return(OS_MAC_OS_X_NAME.equalsIgnoreCase(osName));
	}
	
	public boolean isWindows(){
        return(isWindows);
    }
	
	public boolean isLinux(){
        return(OS_LINUX_NAME.equalsIgnoreCase(osName));
    }
	
	public boolean avoidWindowDispose(){
		return isMacOSX();
	}
	
	public static void disposeWindowForReuse(Window window){
		SystemHelper sh=getInstance();
		if(sh.avoidWindowDispose()){
			window.setVisible(false);
		}else{
			window.dispose();
		}
		
	}
	
}
