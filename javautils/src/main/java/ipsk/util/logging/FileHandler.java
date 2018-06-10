//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
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



package ipsk.util.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;


/**
 * Logging file handler.
 * Has autoflush capability.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class FileHandler extends StreamHandler {

	protected File file;  
    private Level DEFAULT_LEVEL=Level.ALL;  
    private boolean autoflush=false;
    
   
	/**
     * 
     */
    public FileHandler(File file,boolean append) throws SecurityException, FileNotFoundException {
        super();
        this.file=file;
        setOutputStream(new FileOutputStream(file,append));
        // default level of StreamHandler is INF, so we have to override
        setLevel(DEFAULT_LEVEL);
    }
	
    public FileHandler(File file) throws SecurityException, FileNotFoundException {
        this(file,false);
    }
    
    public void publish(LogRecord record){
    	super.publish(record);
    	if(autoflush){
    		flush();
    	}
    }
   
	/**
	 * @return file
	 */
	public File getFile() {
		return file;
	}
	
	public boolean isAutoflush() {
		return autoflush;
	}

	public void setAutoflush(boolean autoflush) {
		this.autoflush = autoflush;
	}


}
