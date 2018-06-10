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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * @author klausj
 *
 */
public class JFileManager extends JPanel {

    private File directory;
    private JScrollPane directoryViewScrollPane;
	private JDirectoryView directoryView;
    public JFileManager(){
        super(new GridBagLayout());
        System.out.println(Toolkit.getDefaultToolkit().getScreenResolution());
        directoryView = new JDirectoryView();
        directoryViewScrollPane=new JScrollPane(directoryView);
        directoryViewScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        directoryViewScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        GridBagConstraints gc=new GridBagConstraints();
//      File[] files=this.directory.listFiles();
////      setLayout(new GridLayout(0, 1));
      gc.gridx=0;
      gc.gridy=0;
      gc.weightx=1.0;
      gc.weighty=1.0;
      gc.fill=GridBagConstraints.BOTH;
      add(directoryViewScrollPane,gc);
        String uHomePath=System.getProperty("user.home");
       File  directory=new File(uHomePath);
        setDirectory(directory);
    }
    
    
    
    
    
    
    public static void main(String[] args){
        Runnable show=new Runnable() {
            
            public void run() {
                JFrame f=new JFrame();
                JFileManager jfm=new JFileManager();
                f.getContentPane().add(jfm);
                f.pack();
           
                f.setVisible(true);
            }
        };
        
        SwingUtilities.invokeLater(show);
        
    }

    public File getDirectory() {
        return directory;
    }


    public void setDirectory(File directory) {
        this.directory = directory;
       
        directoryView.setDirectory(directory);
      
    }
    
}
