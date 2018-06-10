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

import ipsk.util.SystemHelper;
import ipsk.util.debug.WindowDebug;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

// TODO 

/**
 * A workaround class for {@link javax.swing.JDialog JDialog}, which I guess
 * has some layout bugs.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class JDialogPanel extends JPanel implements ActionListener {

    public static final boolean DEBUG=false;
    public static enum Options{NONE,OK,CANCEL,OK_CANCEL,OK_APPLY_CANCEL};
    public final static int OK_OPTION = JOptionPane.OK_OPTION;

    public final static int OK_CANCEL_OPTION = JOptionPane.OK_CANCEL_OPTION;
    
    public final static int CANCEL_OPTION=JOptionPane.CANCEL_OPTION;

    private String frameTitle;

    private JPanel optionPanel;

    protected JButton okButton;

    protected JButton applyButton;

    protected JButton cancelButton;

    private Object value = JOptionPane.UNINITIALIZED_VALUE;

    protected JDialog dialog;

    protected Window owner;

    private Container contentPane;

    private Options options = Options.OK_CANCEL;
    
    private JButton helpButton;

    protected JMenuBar menuBar = null;
    
    private boolean resizable=true;

	private List<Image> iconImages;

    /**
	 * @return the iconImages
	 */
	public List<Image> getIconImages() {
		return iconImages;
	}

	/**
	 * @param iconImages the iconImages to set
	 */
	public void setIconImages(List<Image> iconImages) {
		this.iconImages = iconImages;
	}

	public JDialogPanel() {
        this(Options.OK_CANCEL);
    }
	
	public JDialogPanel(Options options){
	    this(options,false);
	}

    public JDialogPanel(Options options,boolean showHelpButton) {
        super(new BorderLayout());
        this.options = options;
        contentPane = new JPanel();
        add(contentPane, BorderLayout.CENTER);
        optionPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        if(showHelpButton){
            helpButton=new JButton("?");
            optionPanel.add(helpButton);
        }
        if (options.equals(Options.CANCEL)||options.equals(Options.OK_CANCEL)|| options.equals(Options.OK_APPLY_CANCEL)) {
            cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(this);
            optionPanel.add(cancelButton);
        }
        if (options.equals(Options.OK_APPLY_CANCEL)) {
            applyButton = new JButton("Apply");
            applyButton.addActionListener(this);
            optionPanel.add(applyButton);
        }

        if (options.equals(Options.OK) || options.equals(Options.OK_CANCEL)
                || options.equals(Options.OK_APPLY_CANCEL)) {
            okButton = new JButton("OK");
            okButton.addActionListener(this);
            optionPanel.add(okButton);
        }
        add(optionPanel, BorderLayout.SOUTH);
    }
    
    private void _reset(){
        if(cancelButton!=null){
            cancelButton.setEnabled(true);
        }
    }

    public JDialogPanel(String title) {
        this();
        setFrameTitle(title);
    }

    public JDialogPanel(Options options,String title) {
        this(options);
        setFrameTitle(title);
    }

    
    public Object getValue() {
        return value;
    }

    private JDialog createDialog(JDialog owner, boolean modal) {      
        if (dialog == null || (owner == null && this.owner != null)
                || (owner != null && !owner.equals(this.owner))) {
            dialog = new JDialog(owner, frameTitle, modal);
            if(iconImages!=null){
            	dialog.setIconImages(iconImages);
            }
            if(DEBUG)System.out.println("New dialog created.");
        }
        _reset();
        this.owner = owner;
        if (menuBar != null)
            dialog.setJMenuBar(menuBar);
        dialog.getContentPane().add(this);
        dialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent arg0) {
               // setValue(JOptionPane.CANCEL_OPTION);
            	doCancel();
            }
        });
        dialog.setResizable(resizable);
        return dialog;
    }

    public JDialog createDialog(JFrame owner) {
        return createDialog(owner, false);
    }

    public JDialog createDialog(JFrame owner, boolean modal) {
        if (dialog == null || (owner == null && this.owner != null)
                || (owner != null && !owner.equals(this.owner))) {
            dialog = new JDialog(owner, frameTitle, modal);
            if(iconImages!=null){
            	dialog.setIconImages(iconImages);
            }
            dialog.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent arg0) {
                    //setValue(JOptionPane.CANCEL_OPTION);
                    doCancel();
                }
            });
            if(DEBUG)System.out.println("New dialog created.");
        }
        _reset();
        this.owner = owner;
        if (menuBar != null)
        dialog.setJMenuBar(menuBar);
        dialog.getContentPane().add(this); 
        dialog.setResizable(resizable);
        return dialog;
    }
    
    public JDialog createDialog(JFrame owner, Dialog.ModalityType modalityType) {
        if (dialog == null || (owner == null && this.owner != null)
                || (owner != null && !owner.equals(this.owner))) {
            dialog = new JDialog(owner, frameTitle, modalityType);
            if(iconImages!=null){
            	dialog.setIconImages(iconImages);
            }
            dialog.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent arg0) {
                    //setValue(JOptionPane.CANCEL_OPTION);
                    doCancel();
                }
            });
            if(DEBUG)System.out.println("New dialog created.");
        }
        _reset();
        this.owner = owner;
        if (menuBar != null)
            dialog.setJMenuBar(menuBar);
        dialog.getContentPane().add(this);  
        dialog.setResizable(resizable);
        return dialog;
    }

    public void showNonModalDialog(JDialog parent) {
        dialog = createDialog(parent, false);
        final JDialog fParent = parent;
        Runnable packAndShow = new Runnable() {
            public void run() {
                dialog.pack();
                if (fParent != null) {
                    dialog.setLocationRelativeTo(fParent);
                }
                dialog.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(packAndShow);

    }

    public Object showDialog(JDialog parent) {
        dialog = createDialog(parent, true);
        final JDialog fParent = parent;
        // Runnable packAndShow = new Runnable() {
        // public void run() {
        dialog.pack();
        if (fParent != null) {
            dialog.setLocationRelativeTo(fParent);
        }
        dialog.setVisible(true);
        return getValue();
    }

    public void showNonModalDialog(JFrame parent) {
        dialog = createDialog(parent, false);
        final JFrame fParent = parent;
        Runnable packAndShow = new Runnable() {
            public void run() {
                dialog.pack();
                if (fParent != null) {
                    dialog.setLocationRelativeTo(fParent);
                }
                dialog.setVisible(true);
            }
        };
        SwingUtilities.invokeLater(packAndShow);
    }

    public Object showDialog(JFrame parent) {
        dialog = createDialog(parent, true);
        final JFrame fParent = parent;
        // Runnable packAndShow = new Runnable() {
        // public void run() {
        dialog.pack();
        if (fParent != null) {
            dialog.setLocationRelativeTo(fParent);
            fParent.toFront();
        }
        dialog.setVisible(true);
        
        // }
        // };
        // SwingUtilities.invokeLater(packAndShow);
        return getValue();
    }
    
    public Object showDialog(JFrame parent,Dialog.ModalityType modalityType) {
        dialog = createDialog(parent, modalityType);
        final JFrame fParent = parent;
        // Runnable packAndShow = new Runnable() {
        // public void run() {
        dialog.pack();
        if (fParent != null) {
            dialog.setLocationRelativeTo(fParent);
        }
        dialog.setVisible(true);
        // }
        // };
        // SwingUtilities.invokeLater(packAndShow);
        return getValue();
    }

    public Object showDialog(Component parent) {
       do{
           if(parent instanceof JFrame){
               return showDialog((JFrame)parent);
           }else if(parent instanceof JDialog){
               return showDialog((JDialog)parent);
           }
           parent=parent.getParent();
       }while(parent!=null);
       return null;
       
    }
    
    public void disposeDialog() {
        if (dialog != null){
            dialog.setVisible(false);
//            dialog.dispose();
            SystemHelper.disposeWindowForReuse(dialog);
          if(DEBUG){
              WindowDebug.printWindows();
          }
        }

        // TODO avoid memory leaks, reuse dialog window 
        // dialog=null;
    }

    // public static Object showDialog(Component parent, ProjectConfiguration
    // initialConfiguration,AudioController2 audioController) {
    // ProjectConfigurationView pv = new
    // ProjectConfigurationView(initialConfiguration,audioController);
    // pv.doLayout();
    // JOptionPane selPane = new JOptionPane(pv, JOptionPane.PLAIN_MESSAGE,
    // JOptionPane.OK_CANCEL_OPTION);
    // selPane.doLayout();
    // final JDialog d = selPane.createDialog(parent, "Project configuration");
    // d.doLayout();
    //	
    // d.setResizable(true);
    //		
    // d.setVisible(true);
    //	
    // return selPane.getValue();
    // }

    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    public void actionPerformed(ActionEvent arg0) {
        Object src = arg0.getSource();
        if (src == okButton) {
            setValue(OK_OPTION);
            doOk();
           // disposeDialog();
        } else if (src == applyButton) {
            setValue(OK_OPTION);
            doApply();
        } else if (src == cancelButton) { 
            doCancel();
        }
    }

    protected void doApply() {
        applyValues();
    }

    protected void doCancel() {
    	if(cancelButton!=null){
    		cancelButton.setEnabled(false);
    	}
    	setValue(CANCEL_OPTION);
    	disposeDialog();
    }

    protected void doOk() {
        disposeDialog();
        applyValues();
    }

    protected void applyValues(){}

    public void setValue(Object value) {
        this.value = value;
    }

    public String getFrameTitle() {
        return frameTitle;
    }

    public void setFrameTitle(String frameTitle) {
        this.frameTitle = frameTitle;
        if (dialog != null) {
            dialog.setTitle(frameTitle);
        }
    }

    public Container getContentPane() {
        return contentPane;
    }

    public void setContentPane(Container contentPane) {
        remove(this.contentPane);
        this.contentPane = contentPane;
        add(contentPane, BorderLayout.CENTER);
    }

    public JDialog getDialog() {
        return dialog;
    }

    public void setApplyingEnabled(boolean b) {
        if(okButton!=null)okButton.setEnabled(b);
        if(applyButton!=null)applyButton.setEnabled(b);
    }

	public boolean isResizable() {
		return resizable;
	}

	public void setResizable(boolean resizable) {
		this.resizable = resizable;
	}

    public JButton getHelpButton() {
        return helpButton;
    }

                
}
