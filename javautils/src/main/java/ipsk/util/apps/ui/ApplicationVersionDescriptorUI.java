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

package ipsk.util.apps.ui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ipsk.swing.text.EditorKitMenu;
import ipsk.util.apps.descriptor.ApplicationVersionDescriptor;
import ipsk.util.apps.descriptor.InstallationPackage;

/**
 * @author klausj
 *
 */
public class ApplicationVersionDescriptorUI extends JPanel implements ActionListener {

    
    private ApplicationVersionDescriptor  applicationVersionDescriptor;
    private boolean downloadEnabled=false;
  
    public boolean isDownloadEnabled() {
        return downloadEnabled;
    }


    private JButton downloadButton;
    private URI downloadURI;
    
    public static class DownloadActionEvent extends ActionEvent{
       
        private ApplicationVersionDescriptor applicationVersionDescriptor;
        public ApplicationVersionDescriptor getApplicationVersionDescriptor() {
            return applicationVersionDescriptor;
        }
        /**
         * @param source
         * @param id
         * @param applicationVersionDescriptor
         */
        public DownloadActionEvent(Object source, int id, ApplicationVersionDescriptor applicationVersionDescriptor) {
            super(source, id,"start_download_and_quit");
            this.applicationVersionDescriptor=applicationVersionDescriptor;
        }
        
    }
    private ActionListener actionListener;
    public ActionListener getActionListener() {
        return actionListener;
    }


    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }


    public ApplicationVersionDescriptorUI(ApplicationVersionDescriptor avd,boolean downloadEnbled){
        super(new GridBagLayout());
        this.downloadEnabled=downloadEnbled;
        GridBagConstraints gc=new GridBagConstraints();
        Insets insets=new Insets(1,1,1,1);
        gc.insets=insets;
        gc.gridx=0;
        gc.gridy=0;
        gc.fill=GridBagConstraints.HORIZONTAL;
        gc.weightx=2;
        this.applicationVersionDescriptor=avd;
        JLabel versionLabel=new JLabel(applicationVersionDescriptor.getVersion().toString());
        add(versionLabel,gc);
        InstallationPackage ip=applicationVersionDescriptor.getPlatformInstallationPackage();
        if(ip!=null){
            URL downloadURL=ip.getDownloadURL();
            if(downloadURL!=null){
                try {

                    JTextField downloadUrlField=new JTextField(downloadURL.toString());
                    downloadUrlField.setEditable(false);
                    EditorKitMenu editorKitMenu=new EditorKitMenu(downloadUrlField,false);
                    gc.gridy++;
                    add(downloadUrlField,gc);
                    downloadURI=downloadURL.toURI();
                    if(downloadEnbled && downloadURI!=null){ 
                        downloadButton = new JButton("Start download in desktop browser and quit");
                        downloadButton.addActionListener(this);
                        gc.gridy++;
                        gc.weightx=1.0;
                        gc.fill=GridBagConstraints.NONE;
                        add(downloadButton,gc);
                    }
                } catch (URISyntaxException e) {
                    // OK simply ignore (no download button)
                }
            }
        }
    }
    
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }


    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src=e.getSource();
        if(src==downloadButton){
            DownloadActionEvent dae=new DownloadActionEvent(this, e.getID(), applicationVersionDescriptor);
            actionListener.actionPerformed(dae);
        }

    }

}
