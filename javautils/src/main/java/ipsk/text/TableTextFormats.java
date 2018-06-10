//    Speechrecorder
// 	  (c) Copyright 2009-2011
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

package ipsk.text;


import ipsk.swing.EnumSelectionItem;
import ipsk.swing.EnumVector;
import ipsk.text.TableReader;
import ipsk.util.LocalizableMessage;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.SortedMap;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Text table format enumerations 
 * @author klausj
 *
 */
public class TableTextFormats{
	
    
    public enum Profile {
        ASCII_UNICODE("ASCII/Unicode",UnitSeparator.US,RecordSeparator.RS,GroupSeparator.GS),
        TAB_SEP_WIN("Tab separated Windows",UnitSeparator.TAB,RecordSeparator.CRLF,GroupSeparator.CRLF),
        TAB_SEP_UNIX("Tab separated UNIX",UnitSeparator.TAB,RecordSeparator.LF,GroupSeparator.LF),
        CSV("Comma separated",UnitSeparator.COMMA,RecordSeparator.CRLF,GroupSeparator.CRLF);
        Profile(String description,UnitSeparator unitSeparator,RecordSeparator recordSeparator,GroupSeparator groupSeparator){
            this.description=description;
            this.unitSeparator=unitSeparator;
            this.recordSeparator=recordSeparator;
            this.groupSeparator=groupSeparator;
        }
        private final UnitSeparator unitSeparator;
        private final RecordSeparator recordSeparator;
        private final GroupSeparator groupSeparator;
        private final String description;
        
        
        public String toString() {
            return description; 
        }


        public UnitSeparator getFieldSeparator() {
            return unitSeparator;
        }


        public RecordSeparator getRecordSeparator() {
            return recordSeparator;
        }


        public GroupSeparator getGroupSeparator() {
            return groupSeparator;
        }
    }
        
	public enum UnitSeparator {
        TAB('\t',"Tabulator"), COMMA(',',"',' Comma"), SEMICOLON(';',"';' Semicolon"), COLON(':',"':' Colon"),HYPHEN('-',"'-' Hyphen"),BLANK(' ',"' ' Blank"),US('\u001F'," US  (unit separator)");

        UnitSeparator(char value,String description) {
            this.value = value;
            this.description=description;
        }
        private final char value;
        private final String description;

        public char value() {
            return value; 
        }
        public String toString() {
            return description; 
        }
    }
	
	// TODO use it
	public enum RecordSeparator {
        LF(new char[]{'\n'},"New line (UNIX end of line)"),CRLF(new char[]{'\r','\n'},"Carriage return/line feed (Windows end of line)"),CR(new char[]{'\r'},"Carriage return"),RS(new char[]{'\u001E'}," RS  (record separator)");

        RecordSeparator(char[] value,String description) {
            this.value = value;
            this.description=description;
        }
        private final char[] value;
        private final String description;

        public char[] value() {
            return value; 
        }
        public String toString() {
            return description; 
        }
    }
	
	public enum GroupSeparator {
	    LF(new char[]{'\n'},"New line (UNIX end of line)"),CRLF(new char[]{'\r','\n'},"Carriage return/line feed (Windows end of line)"),CR(new char[]{'\r'},"Carriage return"),GS(new char[]{'\u001E'}," GS  (group separator)");

        GroupSeparator(char[] value,String description) {
            this.value = value;
            this.description=description;
        }
        private final char[] value;
        private final String description;

        public char[] value() {
            return value; 
        }
       
        public String toString() {
            return description; 
        }
    }
	
	
	
	public static Profile matchesProfile(UnitSeparator unitSeparator,RecordSeparator recordSeparator, GroupSeparator groupSeparator){
	    for(Profile prf:Profile.values()){
	        boolean matches=true;
	        if(unitSeparator!=null){
	            matches=matches & (unitSeparator.value==prf.getFieldSeparator().value);
	        }
	        if(recordSeparator!=null){
	            matches=matches & Arrays.equals(recordSeparator.value,prf.getRecordSeparator().value());
	        }
	        if(groupSeparator!=null){
	            matches=matches & Arrays.equals(groupSeparator.value,prf.getGroupSeparator().value());
	        }
	        if(matches){
	            return prf;
	        }
	    }
	    return null;
	}


   
}
