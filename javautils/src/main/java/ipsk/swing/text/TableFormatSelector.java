//    IPS Java Utils
// 	  (c) Copyright 2014
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

import ipsk.swing.EnumSelectionItem;
import ipsk.swing.EnumVector;
import ipsk.text.TableTextFormat;
import ipsk.text.TableTextFormats;
import ipsk.text.TableTextFormats.UnitSeparator;
import ipsk.text.TableTextFormats.GroupSeparator;
import ipsk.text.TableTextFormats.Profile;
import ipsk.text.TableTextFormats.RecordSeparator;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author klausj
 *
 */
public class TableFormatSelector extends JPanel implements ActionListener {

    
    private static EnumVector<Profile> PROFILE=new EnumVector<Profile>(Profile.class,"<Custom>");
    private JComboBox profileBox;
    
    private static EnumVector<UnitSeparator> UNIT_SEPARATORS=new EnumVector<UnitSeparator>(UnitSeparator.class);
    private JComboBox unitSepBox;
    private static EnumVector<RecordSeparator> RECORD_SEPARATORS=new EnumVector<RecordSeparator>(RecordSeparator.class);
    private JComboBox recordSepBox;
    private static EnumVector<GroupSeparator> GROUP_SEPARATORS=new EnumVector<GroupSeparator>(GroupSeparator.class);
    private JComboBox groupSepBox;
    private boolean adjusting=false;
    
    
   
    /**
     * 
     */
    public TableFormatSelector() {
        super(new GridBagLayout());
        
        GridBagConstraints gbc=new GridBagConstraints();
        gbc.insets=new Insets(2, 2, 2, 2);
        gbc.anchor=GridBagConstraints.WEST;
        gbc.gridx=0;
        add(new JLabel("Profile:"),gbc);
        gbc.gridx++; 
        profileBox=new JComboBox(PROFILE);
        add(profileBox,gbc);
        profileBox.addActionListener(this);
        
        gbc.gridx=0;
        add(new JLabel("Field sep.:"),gbc);
        gbc.gridx++; 
        unitSepBox=new JComboBox(UNIT_SEPARATORS);
        unitSepBox.addActionListener(this);
        add(unitSepBox,gbc);
        
        gbc.gridx=0;
        add(new JLabel("Record sep.:"),gbc);
        gbc.gridx++; 
        recordSepBox=new JComboBox(RECORD_SEPARATORS);
        recordSepBox.addActionListener(this);
        add(recordSepBox,gbc);
        
        gbc.gridx=0;
        add(new JLabel("Group sep."),gbc);
        gbc.gridx++; 
        groupSepBox=new JComboBox(GROUP_SEPARATORS);
        add(groupSepBox,gbc);
        groupSepBox.addActionListener(this);
        
        setSelectedProfile(Profile.ASCII_UNICODE);
    }
    
    
    
    
    
    
    public static void main(String[] args){
        final TableFormatSelector tfs=new TableFormatSelector();
        Runnable show=new Runnable() {
            
            @Override
            public void run() {
                JFrame f=new JFrame();
                f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                f.getContentPane().add(tfs);
                f.pack();
                f.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(show);
    }

    public TableTextFormat getFormat(){
        TableTextFormat format=new TableTextFormat(getGroupSeparator().value(), getRecordSeparator().value(), new char[]{getUnitSeparator().value()});
        return format;
    }
    public UnitSeparator getUnitSeparator(){
        EnumSelectionItem<UnitSeparator> selIt=(EnumSelectionItem<UnitSeparator>) unitSepBox.getSelectedItem();
        return selIt.getEnumVal();
    }
    
    public RecordSeparator getRecordSeparator(){
        EnumSelectionItem<RecordSeparator> selIt=(EnumSelectionItem<RecordSeparator>) recordSepBox.getSelectedItem();
        return selIt.getEnumVal();
    }
    
    public GroupSeparator getGroupSeparator(){
        EnumSelectionItem<GroupSeparator> selIt=(EnumSelectionItem<GroupSeparator>) groupSepBox.getSelectedItem();
        return selIt.getEnumVal();
    }
    
    private void applySelectedProfile(){
        if(adjusting)return;
        EnumSelectionItem<Profile> selProfIt=(EnumSelectionItem<Profile>)profileBox.getSelectedItem();
        Profile selProf=selProfIt.getEnumVal();
        applyProfile(selProf);
    }

    
    private void applyProfile(Profile selProf){
        if(selProf!=null){
            unitSepBox.setSelectedItem(UNIT_SEPARATORS.getItem(selProf.getFieldSeparator()));
            recordSepBox.setSelectedItem(RECORD_SEPARATORS.getItem(selProf.getRecordSeparator()));
            groupSepBox.setSelectedItem(GROUP_SEPARATORS.getItem(selProf.getGroupSeparator()));
        }
    }
    
    private void updateProfile(){
        Profile matchProf=TableTextFormats.matchesProfile(getUnitSeparator(), getRecordSeparator(), getGroupSeparator());
        EnumSelectionItem<Profile> matchProfSi=new EnumSelectionItem<TableTextFormats.Profile>(matchProf);
         profileBox.setSelectedItem(matchProfSi);
    }

    public void setSelectedProfile(Profile profile){
        profileBox.setSelectedItem(new EnumSelectionItem<Profile>(profile));
    }
    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent arg0) {
        Object src=arg0.getSource();
       
        if(src==profileBox){
            applySelectedProfile();
        }else{
            adjusting=true;
            updateProfile();
        }
        adjusting=false;
    }
   

}
