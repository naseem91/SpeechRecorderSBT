//    IPS Java Utils
// 	  (c) Copyright 2012
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

package ipsk.swing.text;

import java.awt.Component;
import java.awt.Container;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author klausj
 *
 */
public class JLocaleSelector extends JPanel {

    
    private JComboBox languageSelector;
	private Vector<LocaleView> localeViews;
    
    public class LocaleView implements Comparable<LocaleView>{
        private Locale locale;
        
        public LocaleView(Locale locale) {
            super();
            this.locale = locale;
        }

        public String toString(){
            if(locale==null){
                // TODO localize
                return "--Language,Country--";
            }else{
                return locale.getDisplayName();
            }
        }

        public boolean equals(Object obj) {
            
            if( obj != null && obj instanceof LocaleView){
                LocaleView oLv=(LocaleView)obj;
                Locale oLo=oLv.getLocale();
                if(locale==null){
                    return (oLo==null);
                }else{
                    return locale.equals(oLo);
                }
            }
           return false;
            
        }

        public int hashCode() {
            return locale.hashCode();
        }

        /* (non-Javadoc)
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        public int compareTo(LocaleView o) {
            
            return toString().compareTo(o.toString());
        }

        public Locale getLocale() {
            return locale;
        }
    }
    
    
    public JLocaleSelector(){
        super();
       
        Locale[] locales=Locale.getAvailableLocales();
        localeViews = new Vector<LocaleView>();
        for(Locale l:locales){
            localeViews.add(new LocaleView(l));
        }
        
        Collections.sort(localeViews);
        localeViews.insertElementAt(new LocaleView(null),0);
        languageSelector=new JComboBox(localeViews);
        add(languageSelector);
    }
    
    
    public Locale getSelectedLocale(){
        Locale retLoc=null;
       Object lv=languageSelector.getSelectedItem();
       if(lv instanceof LocaleView){
           retLoc=((LocaleView)lv).getLocale();
       }
        return retLoc;
    }
    /**
     * Set selected locale.
     * @param locale selected locale
     */
    public void setSelectedLocale(Locale locale) {
        LocaleView lv=new LocaleView(locale);
        languageSelector.setSelectedItem(lv);
    }
    
//    private void deepEnable(Container cnt,boolean enable){
//        Component[] cmps=cnt.getComponents();
//        for(Component cmp:cmps){
//            cmp.setEnabled(enable);
//            if(cmp instanceof Container){
//                Container cCnt=(Container)cmp;
//                deepEnable(cCnt, enable);
//            }
//        }
//    }
    
    public void setEnabled(boolean enabled){
//        if(enabled != isEnabled()){
//            System.out.println("Locale Sel enable: "+enabled);
//        }
    	super.setEnabled(enabled);
    	languageSelector.setEnabled(enabled);
//    	Component[] lsCmps=languageSelector.getComponents();
//    	for(Component lsCmp:lsCmps){
//    	    lsCmp.setEnabled(enabled);
//    	    if(lsCmp instanceof Container){
//    	        Container lsCnt=(Container)lsCmp;
//    	        deepEnable(lsCnt, enabled);
//    	    }
//    	}
    }
    
    public static void main(String[] args){
        Runnable uiRun=new Runnable() {
            
            public void run() {
               JLocaleSelector ls=new JLocaleSelector();
               JFrame f=new JFrame("JLocalSelector Demo");
               f.getContentPane().add(ls);
               f.pack();
               f.setVisible(true);
            }
        };
        try {
            SwingUtilities.invokeAndWait(uiRun);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
    }


  
    
}
