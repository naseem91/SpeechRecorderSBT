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

/*
 * Date  : Apr 21, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.storage.net;

/**
 * Constants for the Speechrecorder storage transfer protocol. 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class HTTPStorageProtocol {

    public static final String SESSION_INSTANCE_ID_KEY="rec_session_id";
    public static final String CMD_KEY = "cmd";
    public static final String STORE_AUDIO = "store_audio";
    public static final String STORE_LOG = "store_log";
    public static final String STORE_TIMELOG = "store_timelog";
    public static final String STORE_ANNOTATION = "store_annotation";
   
    public static final String ITEM_CODE_KEY="itemcode";
    public static final String SPEAKER_CODE_KEY="speakercode";
    public static final String SPEAKER_ID_KEY="speakerid";
    public static final String EXTENSION_KEY="extension";
    public static final String SCRIPT_ID_KEY="script";
    public static final String SESSION_ID_KEY="session";
    public static final String LINE_KEY="line";
    public static final String OVERWRITE_KEY = "overwrite";
	public static final String VERSION_KEY = "version";
	
	
    
}
