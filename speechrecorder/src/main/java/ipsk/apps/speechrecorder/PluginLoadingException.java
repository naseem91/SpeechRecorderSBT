//    Speechrecorder
//    (c) Copyright 2009-2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.apps.speechrecorder;
/**
   Thrown if a plugin class cannot be loaded 

   @author Klaus Jaensch, klausj@phonetik.uni-muenchen.de
*/


public class PluginLoadingException extends Exception {
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 7336435999708209498L;

	/**
       @param className The name of class which was not found
       @param e The underlying exception (i.e. ClassNotFoundException )
    */
    public PluginLoadingException(String className, Exception e) {
	super("Error loading plugin " + className + ": " + e.toString());
    }

}
