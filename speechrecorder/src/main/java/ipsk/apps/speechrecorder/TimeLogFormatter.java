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

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * TimeLogFormatter takes a log record and prints the log level, the log message
 * and the log time in milliseconds into a tab delimited String.
 * 
 * @author draxler
 */
public class TimeLogFormatter extends Formatter {

    public String format(LogRecord lr) {
        return lr.getLevel() + "\t" + lr.getMessage() + "\t" + lr.getMillis()
                + "\n";
    }

}
