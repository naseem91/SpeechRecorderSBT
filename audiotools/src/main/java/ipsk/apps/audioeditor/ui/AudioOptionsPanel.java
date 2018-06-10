//    IPS Java Audio Tools
// 	  (c) Copyright 2011
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.apps.audioeditor.ui;

import java.awt.Container;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import ipsk.audio.AudioOptions;
import ipsk.audio.capture.PrimaryRecordTarget;
import ipsk.swing.EnumSelectionItem;
import ipsk.swing.EnumVector;
import ipsk.swing.JDialogPanel;

import javax.swing.JComboBox;
import javax.swing.JLabel;

/**
 * @author klausj
 *
 */
public class AudioOptionsPanel extends JDialogPanel {

    private AudioOptions audioOptions;
    private EnumVector<PrimaryRecordTarget> primaryRecordTargetVector;
    private JComboBox primaryRecordTargetBox;
    private Insets DEF_INSETS=new Insets(5, 5, 5, 5);
        public AudioOptionsPanel(AudioOptions audioOptions,String defPrimaryRecordTargetDescription){
            super();
            this.audioOptions=audioOptions;
            primaryRecordTargetVector=new EnumVector<PrimaryRecordTarget>(PrimaryRecordTarget.class,defPrimaryRecordTargetDescription);
            Container content=getContentPane();
            GridBagLayout gbl=new GridBagLayout();
            content.setLayout(gbl);
            GridBagConstraints cl=new GridBagConstraints();
            GridBagConstraints cv=new GridBagConstraints();
            cl.insets=DEF_INSETS;
            cl.anchor=GridBagConstraints.WEST;
            cv.insets=DEF_INSETS;
            cl.gridx=0;
            cl.gridy=0;
            
            JLabel primaryRecordTargetLabel=new JLabel("Primary record target:");
            content.add(primaryRecordTargetLabel,cl);
            
            cv.gridx=1;
            cv.gridy=0;
            primaryRecordTargetBox=new JComboBox(primaryRecordTargetVector);
            PrimaryRecordTarget prt=audioOptions.getPrimaryRecordTarget();
            EnumSelectionItem<PrimaryRecordTarget> eprt=primaryRecordTargetVector.getItem(prt);
            primaryRecordTargetBox.setSelectedItem(eprt);
            content.add(primaryRecordTargetBox,cv);
            
            cl.gridy++;
            content.add(new JLabel("Line buffer size:"),cl);
            
            cv.gridy++;
            String lineBufferSizeString="n/a";
            Integer lbs=audioOptions.getLineBufferSize();
            if(lbs!=null){
                lineBufferSizeString=lbs.toString();
            }
            JLabel lineBufferSizeLabel=new JLabel(lineBufferSizeString);
            content.add(lineBufferSizeLabel,cv);
            
        }
        
        
        public void applyValues(){
            PrimaryRecordTarget prt=((EnumSelectionItem<PrimaryRecordTarget>)primaryRecordTargetBox.getSelectedItem()).getEnumVal();
            audioOptions.setPrimaryRecordTarget(prt);
        }
}
