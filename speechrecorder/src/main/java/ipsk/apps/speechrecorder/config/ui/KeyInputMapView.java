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
 * Date  : Jul 27, 2006
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.config.ui;

import ipsk.apps.speechrecorder.actions.CloseSpeakerDisplayAction;
import ipsk.apps.speechrecorder.config.KeyInputMap;
import ipsk.apps.speechrecorder.config.KeyStrokeAction;
import ipsk.swing.JKeyChooser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class KeyInputMapView extends JPanel implements ActionListener {
   
	private static final long serialVersionUID = 1L;
	private JCheckBox[] enableBoxes;
    private JKeyChooser[] keyChoosers;
    private JLabel[] ctrlLabels;
    private JCheckBox[] ctrlModifierBoxes;
    private JLabel[] shiftLabels;
    private JCheckBox[] shiftModifierBoxes;   
    private JLabel[] altLabels;
    private JCheckBox[] altModifierBoxes;
    private JCheckBox consumeAllBox;
    private Action[] actions;
//    private KeyInputMap keyInputMap;
    
    public KeyInputMapView(Action[] actions){
        super(new GridBagLayout());
//        this.keyInputMap=keyInputMap;
        //actions=SpeechRecorder.ACTIONS;
        this.actions=actions;
        int actionCount=0;
        if (actions!=null)actionCount=actions.length;
        enableBoxes=new JCheckBox[actionCount];
        keyChoosers=new JKeyChooser[actionCount];
        shiftLabels=new JLabel[actionCount];
        shiftModifierBoxes=new JCheckBox[actionCount];
        altLabels=new JLabel[actionCount];
        altModifierBoxes=new JCheckBox[actionCount];
        ctrlLabels=new JLabel[actionCount];
        ctrlModifierBoxes=new JCheckBox[actionCount];
        
        GridBagConstraints c=new GridBagConstraints();
        c.anchor = GridBagConstraints.PAGE_START;
        c.fill=GridBagConstraints.HORIZONTAL;
        c.gridy=0;
//        KeyStrokeAction[] kas=keyInputMap.getKeyStrokeAction();
        for (int i=0;i<actionCount;i++){
            c.gridx=0;
            add(new JLabel(actions[i].getValue(Action.SHORT_DESCRIPTION)+": "),c);
            c.gridx++;
            enableBoxes[i]=new JCheckBox();
            enableBoxes[i].addActionListener(this);
            add(enableBoxes[i],c);
            c.gridx++;
            keyChoosers[i]=new JKeyChooser();
            keyChoosers[i].setEnabled(false);
            ctrlLabels[i]=new JLabel("Ctrl");
            ctrlModifierBoxes[i]=new JCheckBox();
            shiftLabels[i]=new JLabel("Shift");
            shiftModifierBoxes[i]=new JCheckBox();
            altLabels[i]=new JLabel("Alt");
            altModifierBoxes[i]=new JCheckBox();

            setEnabled(i,false);
//            for(int j=0;j<kas.length;j++){
//                String actionCmd=kas[j].getAction();
//                if(actionCmd.equals(actions[i].getValue(Action.ACTION_COMMAND_KEY))){
//                    keyChoosers[i]=new JKeyChooser(kas[j].getCode());
//
//                    enableBoxes[i].setSelected(true);
//                    setEnabled(i,true);
//
//                    shiftModifierBoxes[i].setSelected(kas[j].getShift());
//                    altModifierBoxes[i].setSelected(kas[j].isAlt());
//                    ctrlModifierBoxes[i].setSelected(kas[j].isCtrl());
//                    break;
//                }
//            }
            keyChoosers[i].addActionListener(this);
            add(keyChoosers[i],c);
            c.gridx++;

            add(shiftLabels[i],c);
            c.gridx++;
            shiftModifierBoxes[i].addActionListener(this);
            add(shiftModifierBoxes[i],c);
            c.gridx++;

            add(altLabels[i],c);
            c.gridx++;
            altModifierBoxes[i].addActionListener(this);
            add(altModifierBoxes[i],c);
            c.gridx++;

            add(ctrlLabels[i],c);
            c.gridx++;
            ctrlModifierBoxes[i].addActionListener(this);
            add(ctrlModifierBoxes[i],c);
            c.gridy++;
        }
        c.gridx=0;

        add(new JLabel("Ignore other key strokes "),c);
        c.gridx++;
        consumeAllBox=new JCheckBox();
        //        consumeAllBox.setSelected(keyInputMap.isConsumeallkeys());
        consumeAllBox.addActionListener(this);
        add(consumeAllBox,c);
    }
    
    public void setKeyInputmap(KeyInputMap keyInputMap){
        consumeAllBox.setSelected(keyInputMap.isConsumeallkeys());
        KeyStrokeAction[] kas=keyInputMap.getKeyStrokeAction();
        int actionCount=0;
        if (actions!=null)actionCount=actions.length;
        for (int i=0;i<actionCount;i++){
            setEnabled(i,false);
            for(int j=0;j<kas.length;j++){
                String actionCmd=kas[j].getAction();
                if(actionCmd.equals(actions[i].getValue(Action.ACTION_COMMAND_KEY))){
//                    keyChoosers[i]=new JKeyChooser(kas[j].getCode());
                    keyChoosers[i].setSelectedItemByCode(kas[j].getCode());
                    enableBoxes[i].setSelected(true);
                    setEnabled(i,true);
                    shiftModifierBoxes[i].setSelected(kas[j].getShift());
                    altModifierBoxes[i].setSelected(kas[j].isAlt());
                    ctrlModifierBoxes[i].setSelected(kas[j].isCtrl());
                    break;
                }
            }
        }
    }

    public boolean isCloseSpeakerDisplayActionBound(){
        for(int i=0;i<actions.length;i++){
            Action a=actions[i];
            if(a instanceof CloseSpeakerDisplayAction){
                if(enableBoxes[i].isSelected()){
                    return true;
                }
            }
        }
        return false;
    }
    
    public void bindCloseSpeakerDisplayActiontoEscape(){
        for(int i=0;i<actions.length;i++){
            Action a=actions[i];
            if(a instanceof CloseSpeakerDisplayAction){
                
                setEnabled(i,true);
                enableBoxes[i].setSelected(true);
                keyChoosers[i].setSelectedItemByCode(KeyEvent.VK_ESCAPE);
                shiftModifierBoxes[i].setSelected(false);
                ctrlModifierBoxes[i].setSelected(false);
                altModifierBoxes[i].setSelected(false);
                break;
            }
        }
    }
    
    private void setEnabled(int i,boolean enabled){
        keyChoosers[i].setEnabled(enabled);
        shiftLabels[i].setEnabled(enabled);
        shiftModifierBoxes[i].setEnabled(enabled);
        ctrlLabels[i].setEnabled(enabled);
        ctrlModifierBoxes[i].setEnabled(enabled);
        altLabels[i].setEnabled(enabled);
        altModifierBoxes[i].setEnabled(enabled);
    }
    
    
    public void applyValues(KeyInputMap keyInputMap){
        keyInputMap.setConsumeallkeys(consumeAllBox.isSelected());
        
        ArrayList<KeyStrokeAction> newkas=new ArrayList<KeyStrokeAction>();
        for(int i=0;i<actions.length;i++){
            KeyStrokeAction ka=new KeyStrokeAction();
            boolean enabled=enableBoxes[i].isSelected();
            if(enabled){

                ka.setAction((String)actions[i].getValue(Action.ACTION_COMMAND_KEY));
                JKeyChooser.Key k=(JKeyChooser.Key)keyChoosers[i].getSelectedItem();
                ka.setCode(k.getCode());
                ka.setShift(shiftModifierBoxes[i].isSelected());
                ka.setAlt(altModifierBoxes[i].isSelected());
                ka.setCtrl(ctrlModifierBoxes[i].isSelected());
                newkas.add(ka);
            }
        }
        keyInputMap.setKeyStrokeAction((KeyStrokeAction[])newkas.toArray(new KeyStrokeAction[0]));
    }
  
    public void actionPerformed(ActionEvent arg0) {
        Object src=arg0.getSource();
        if(src==consumeAllBox){
//            keyInputMap.setConsumeallkeys(consumeAllBox.isSelected());  
        }else{
//            ArrayList<KeyStrokeAction> newkas=new ArrayList<KeyStrokeAction>();
            for(int i=0;i<actions.length;i++){
//                KeyStrokeAction ka=new KeyStrokeAction();
                boolean enabled=enableBoxes[i].isSelected();
                setEnabled(i,enabled);
//                if(enabled){
//
//                    ka.setAction((String)actions[i].getValue(Action.ACTION_COMMAND_KEY));
//                    JKeyChooser.Key k=(JKeyChooser.Key)keyChoosers[i].getSelectedItem();
//                    ka.setCode(Integer.toString(k.getCode()));
//                    ka.setShift(shiftModifierBoxes[i].isSelected());
//                    ka.setAlt(altModifierBoxes[i].isSelected());
//                    ka.setCtrl(ctrlModifierBoxes[i].isSelected());
//                    newkas.add(ka);
//                }
            }
//            keyInputMap.setKeyStrokeAction((KeyStrokeAction[])newkas.toArray(new KeyStrokeAction[0]));
        }

    }
    
    
    
    
    
}
