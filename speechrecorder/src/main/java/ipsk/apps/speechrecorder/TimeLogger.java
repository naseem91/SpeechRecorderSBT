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

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author draxler
 *
 * TimeLogger provides an ordered list of named time stamps. The creation of a time
 * stamp is perfomed by the createLogEntry() method which in turn is usually 
 * triggered by events. The main use of a TimeLogger is to log the 
 * events during a recording, e.g. start times of the different recording phases, 
 * but arbitrary time stamps may be created, e.g. to log processing time, begin
 * and end of audio or video prompt display, etc.
 */
public class TimeLogger {

	private Logger timeLogger;
	private String timeLoggerName;
//	private Level logLevel = Level.INFO;
	private Handler logFileHandler;

	public TimeLogger(String logName, Handler handler)
	{
		timeLoggerName = logName;
		logFileHandler = handler;
		timeLogger = Logger.getLogger(timeLoggerName);
		if (logFileHandler == null)	// 
		{
			try
			{
				logFileHandler = new FileHandler(timeLoggerName);
//				logFileHandler = new FileHandler();
				logFileHandler.setFormatter(new TimeLogFormatter());
				timeLogger.addHandler(logFileHandler);
			}
			catch (IOException e)
			{
				timeLogger.severe("Could not associate a file with the current logger: " + e);
			}
			catch (SecurityException e)
			{
				timeLogger.severe("Could not write to a log file: " + e);
			}
		}
	}

	public TimeLogger(String logName) {
		this(logName,null);
	}

	/**
	 * creates a new log entry from the message argument
	 * @param message to print to the log
	 */
	public void createLogEntry(String message) {
		timeLogger.info(message);
	}
    
    /**
     * Set level of time logger.
     * @param timeLogLevel
     */
    public void setLevel(Level timeLogLevel){
        timeLogger.setLevel(timeLogLevel);
    }

    /**
     * Get level of time logger.
     * @return level of time logger
     */
    public Level getLevel(){
        return timeLogger.getLevel();
    }
    
	/**
	 * TimeLogFormatter takes a log record and prints the log level,
	 * the log message and the log time in milliseconds into a tab 
	 * delimited String.
	 * 
	 * @author draxler
	 */
	class TimeLogFormatter extends Formatter {

		public String format(LogRecord lr) {
			return lr.getLevel() + "\t" + lr.getMessage() + "\t" + lr.getMillis() + "\n";
		}
	}
	
	/**
	 * Remove handler.
	 * @param handler to remove
	 * @throws java.lang.SecurityException
	 */
	public synchronized void removeHandler(Handler handler) throws SecurityException {
		timeLogger.removeHandler(handler);
	}

}
