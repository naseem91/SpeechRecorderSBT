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

package ipsk.swing;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.lang.reflect.Field;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JFrame;



public class JKeyChooser extends JComboBox {
	

	public class Key{
        private int code;
        public Key(int code){
            this.code=code;
        }
        public String toString(){
            return KeyEvent.getKeyText(code);
        }
        public boolean equals(Object o){
            if(o==null)return false;
            if(! (o instanceof Key))return false;
            Key k=(Key)o;
            if(k.getCode()==code){
                return true;
            }
            return false;
        }
        /**
         * @return Returns the code.
         */
        public int getCode() {
            return code;
        }
    }
	public JKeyChooser(int keyCode){
        this();
        //setSelectedItem(new Key(keyCode));
        setSelectedItemByCode(keyCode);
    }
    public JKeyChooser(){
		super();
       
		Field[] keFields=KeyEvent.class.getFields();
		//Vector keys=new Vector();
		for (int i=0;i<keFields.length;i++){
			Class<?> type=keFields[i].getType();
			String name=keFields[i].getName();
			if (type.isPrimitive() && type.getName().equals("int") && name.startsWith("VK_")){
                
				try {
                    
                    int code=keFields[i].getInt(null);
                    
                    // TODO Why ?
                    // the modifier keys are not working in SpeechRecorder 
                    if(code==KeyEvent.VK_SHIFT || code==KeyEvent.VK_CONTROL || code==KeyEvent.VK_ALT){
                        continue;
                    }
                    Key k=new Key(code);
                    //System.out.println(k+" : "+code);
					addItem(k);
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
       

		
		
	}
    
    public void setSelectedItemByCode(int keyCode){
        Key k=new Key(keyCode);
        setSelectedItem(k);
    }
    
    public void setEnabled(boolean enabled){
        super.setEnabled(enabled);
        Component[] ch=getComponents();
        for (int i=0;i<ch.length;i++){
            ch[i].setEnabled(enabled);
        }
    }
	
	public static void main(String[] args){
		JKeyChooser kc=new JKeyChooser();
		JFrame f=new JFrame("KeyCooser");
		f.getContentPane().add(kc);
		f.pack();
		f.setVisible(true);
	}
}
