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

package ips.incubator.swing.filemanager;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author klausj
 *
 */
public class JFileView extends JPanel {

    private File file;
    private JLabel typeLabel;
    private JTextField nameField;
   
    public JFileView(File f){
        super();
        typeLabel=new JLabel();
        nameField=new JTextField(10);
//        nameField.setHorizontalAlignment(JTextField.LEFT);
        nameField.setEditable(false);
        add(typeLabel);
        add(nameField);
//        nameField.setPreferredSize(new Dimension(200, 50));
        setFile(f);
        
    }
    public File getFile() {
        return file;
    }
    public void setFile(File file) {
        this.file = file;
        if(file.isDirectory()){
            typeLabel.setText("D:");
        }else{
            typeLabel.setText("F:");
        }
        String fName=file.getName();
        nameField.setText(fName);
        nameField.scrollRectToVisible(new Rectangle(0,0,1,1));
       revalidate();
       repaint();
    }
    
    
}
