//    Speechrecorder
// 	  (c) Copyright 2014
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



package ipsk.apps.speechrecorder.config.ui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ipsk.apps.speechrecorder.config.AutoAnnotation.LaunchMode;
import ipsk.apps.speechrecorder.config.AutoAnnotator;
import ipsk.swing.EnumVector;
import ipsk.swing.text.EditorKitMenu;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;



/**
 * @author klausj
 *
 */
public class AutoAnnotatorPanel extends JPanel implements ActionListener {
	
	private AutoAnnotationServiceDescriptor serviceDescriptor;
	/**
	 * @return the serviceDescriptor
	 */
	public AutoAnnotationServiceDescriptor getServiceDescriptor() {
		return serviceDescriptor;
	}

	private JCheckBox enabledCheckBox;
    private JLabel enableLabel;
    
    private ActionListener actionListener=null;
	public boolean isSelected() {
        return enabledCheckBox.isSelected();
    }

    public AutoAnnotatorPanel(AutoAnnotationServiceDescriptor sd){
		super(new GridBagLayout());
		setBorder(BorderFactory.createLineBorder(Color.BLACK));
		this.serviceDescriptor=sd;
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(2, 5, 2, 5);
		c.anchor = GridBagConstraints.PAGE_START;
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0;
		
		enableLabel = new JLabel("Enable:");
		add(enableLabel, c);
		c.gridx++;
		enabledCheckBox=new JCheckBox();
		enabledCheckBox.addActionListener(this);
		add(enabledCheckBox, c);
		
		c.gridx = 0;
		c.gridy++;
		add(new JLabel("Name:"), c);
		c.gridx++;
		String title=serviceDescriptor.getTitle().localize();
		add(new JLabel(title), c);
		
		c.gridx = 0;
		c.gridy++;
		add(new JLabel("Description:"), c);
		c.gridx++;
		String descr=serviceDescriptor.getDescription().localize();
		c.weightx = 2;
		add(new JLabel(descr), c);
		c.weightx=0;
		String[] links=null;
		Method getLinksM=null;
		try {
			getLinksM = serviceDescriptor.getClass().getMethod("getLinks");
		} catch (NoSuchMethodException e2) {
			// OK 
		} catch (SecurityException e2) {
			// Hmm
		}
		if(getLinksM!=null){
			try {
				Object res=getLinksM.invoke(serviceDescriptor);
				if(res instanceof String[]){
					links=(String[])res;
				}
			} catch (Exception e) {
				// TODO
			} 
		}
		if(links!=null && links.length>0){
		    c.gridx = 0;
	        c.gridy++;
	       
	       
	        add(new JLabel("Links:"), c);
	        final Desktop desktop;
	        if(Desktop.isDesktopSupported()){
	            desktop=Desktop.getDesktop();
	        }else{
	          desktop=null;  
	        }
	        for(String link:links){
	            JTextField linkF=new JTextField(link);
	            URI linkUri=null;
                try {
                    linkUri = new URI(link);
                } catch (URISyntaxException e1) {
                   // OK, linkUri ==null
                }
                final URI browseUri=linkUri;
                final Container msgCnt=this;
                linkF.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if(desktop!=null && browseUri!=null){
                            try {
                                desktop.browse(browseUri);
                            } catch (IOException e1) {
                                JOptionPane.showMessageDialog(msgCnt, "Could not open browser!", "Desktop bowse error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });
	            EditorKitMenu nameEkm=new EditorKitMenu(linkF,false);
	            nameEkm.setPopupMenuActiv(true);
	            
	            c.gridx=1;
	            c.weightx = 2;
	            add(linkF, c);
	            c.gridy++;
	            c.weightx=0;
	            
	        }
		}
	}
	
	public void applyValues(AutoAnnotator aa){
		aa.setClassname(serviceDescriptor.getServiceImplementationClassname());
		aa.setEnabled(enabledCheckBox.isSelected());
	}

	/**
	 * @param aa
	 */
	public void setConfig(AutoAnnotator aa) {
		if(aa==null){
			enabledCheckBox.setSelected(false);
		}else{
			enabledCheckBox.setSelected(aa.isEnabled());			
		}
		
	}
	
	public void setEnabled(boolean enabled){
	    super.setEnabled(enabled);
	    enableLabel.setEnabled(enabled);
	    enabledCheckBox.setEnabled(enabled);
	    boolean selected=enabledCheckBox.isSelected();
	    // a disabled plugin cannot be selected
	    enabledCheckBox.setSelected(selected && enabled);
	}

    /**
     * @param autoAnnotationPanel
     */
    public void setActionListener(ActionListener actionListener) {
        this.actionListener=actionListener;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if(actionListener!=null){
            actionListener.actionPerformed(e);
        }
    }
	
	
	
}
