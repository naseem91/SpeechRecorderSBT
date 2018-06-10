//    Speechrecorder
// 	  (c) Copyright 2011
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


package ipsk.io;

import ipsk.util.LocalizableMessage;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Validator checks filenames for the validity.
 * 
 * @author klausj
 *
 */
public class FilenameValidator {
    
    // TODO not localized
    
    public static class ValidationResult{
        private LocalizableMessage message;

        // TODO
        public enum Type {OK,VALID_NOT_RECOMMENDED,INVALID}
        private boolean valid;
        
        public ValidationResult(boolean valid){
            this(valid,null);
        }
        public ValidationResult(String message){
           this(false,new LocalizableMessage(message));
        }
        public ValidationResult(boolean valid,LocalizableMessage message){
            super();
            this.valid=valid;
            this.message=message;
        }
        public boolean isValid() {
            return valid;
        }
        public LocalizableMessage getMessage() {
            return message;
        }
        
    }
    
    public static String[] RESERVED_NAMES=new String[]{".",".."};
    
    
    
    
    // See http://msdn.microsoft.com/en-us/library/windows/desktop/aa365247%28v=vs.85%29.aspx
    
    public static char[] RESERVED_CHARS_WINDOWS=new char[]{'<','>',':','"','/','\\','|','?','*'};
    public static String[] RESERVED_NAMES_WINDOWS=new String[]{"CON", "PRN", "AUX", "NUL", "COM1", "COM2", "COM3", "COM4", "COM5", "COM6", "COM7", "COM8", "COM9", "LPT1", "LPT2", "LPT3", "LPT4", "LPT5", "LPT6", "LPT7", "LPT8", "LPT9"};
    
    public static char[] RESERVED_CHARS_UNIX=new char[]{'\0','/'};
    
    public static ValidationResult validateFileNameCharacters(String string){
        char[] fileNameChars=string.toCharArray();
        for(char fnc:fileNameChars){
        
            for(char rc:RESERVED_CHARS_WINDOWS){
                if(rc==fnc){
                    return new ValidationResult("contains reserved character: \""+rc+"\"");
                }
            }
            
            for(char rc:RESERVED_CHARS_UNIX){
                if(rc==fnc){
                    return new ValidationResult("contains reserved character: \""+rc+"\"");
                }
            }
            
            if(Character.isISOControl(fnc)){
               // TODO
                // not recommended
            }
        }
        return new ValidationResult(true);
    }
    
    public static ValidationResult validate(String fileName){
        if(fileName==null){
            throw new NullPointerException();
        }
        for(String resName:RESERVED_NAMES){
            if(fileName.equals(resName)){
                return new ValidationResult("reserved name: "+fileName);
            }
        }
        ValidationResult vrc=validateFileNameCharacters(fileName);
        if(!vrc.isValid()){
            return vrc;
        }
        String fileNameBody=fileName;
        int extPos=fileName.lastIndexOf(".");
        if(extPos>0){
            fileNameBody=fileName.substring(0,extPos);
        }
       for(String resNameWindows:RESERVED_NAMES_WINDOWS){
           if(fileNameBody.equals(resNameWindows)){
               return new ValidationResult("reserved Windows filename: "+resNameWindows);
           }
       }
       char lastChar=fileName.charAt(fileName.length()-1);
       if(lastChar==' ' || lastChar=='.'){
           return new ValidationResult("on Windows last char of filename should not be a space or period");
       }
       
       return new ValidationResult(true);
    }
    
    
    
}
