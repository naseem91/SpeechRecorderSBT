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
 * Date  : Jun 24, 2004
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.apps.speechrecorder.config;

import ipsk.apps.speechrecorder.SpeechRecorder;

import java.awt.event.ActionListener;

import javax.swing.JComboBox;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class FormatterView extends JComboBox implements ActionListener{
   // public final static Formatter[] KNOWN_FORMATTERS={new Formatter(null,"(Default)"),new NamedFormatter("java.util.logging.SimpleFormatter","Plain Text"),new NamedFormatter("java.util.logging.XMLFormatter","XML"),new NamedFormatter(TimeLogFormatter.class.getName(),"Time logger")};
    
    /**
     * 
     */

//    public static class NamedFormatter extends Formatter{
//        
//        private String name;
//    
//        public NamedFormatter(String className,String name){
//            super();    
//            this.attributeClassName=className;
//            this.name=name;   
//        }
//        public NamedFormatter(){
//            super();    
//        }
//
//       
//        
//        
//        public String toString(){
//            return name;
//        }
//        
//    }
    //private Formatter p;
    
    //private static NamedFormatter[] knownFormatters={new NamedFormatter(null,"(Default)"),new NamedFormatter("java.util.logging.SimpleFormatter","Plain Text"),new NamedFormatter("java.util.logging.XMLFormatter","XML"),new NamedFormatter(TimeLogFormatter.class.getName(),"Time logger")};
   

    // java.util.Locale[] availLocales;

    public FormatterView(Formatter p) {
        this();

        setFormatter(p);
        
        
    }

    
    public FormatterView() {
        super();
        for(int i=0;i<SpeechRecorder.LOG_FORMATTERS.length;i++){
            addItem(SpeechRecorder.LOG_FORMATTERS[i]);
        }
        setSelectedIndex(0);
       
    }


    public void setFormatter(Formatter f){
        setSelectedItem(f);
        
    }
    public Formatter getFormatter(){
        return (Formatter)getSelectedItem();
        
    }
    
    
    

}
