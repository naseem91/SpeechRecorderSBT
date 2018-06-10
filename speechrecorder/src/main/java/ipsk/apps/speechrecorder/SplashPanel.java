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
 * Created on Sep 23, 2004
 *
 * Project: JSpeechRecorder
 * Original author: draxler
 */
package ipsk.apps.speechrecorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * @author draxler
 *
 * SplashScreen displays the splash screen at application startup. It 
 * is displayed either for a given time, or until all dynamically loadable
 * components or plugins have been loaded.
 */
public class SplashPanel extends JPanel implements DocumentListener {

	private static final long serialVersionUID = -664721791281526748L;
//	private static final int TINY = 8;
//	private static final int SMALL = 10;
	private static final int NORMAL = 12;
	private static final int LARGE = 18;
	private static final int HUGE = 36;
	
	// JEditorpane's size does not automatically fit to content size
	private static final int CREDITPANE_WIDTH=200;
	private static final int CREDITPANE_HEIGHT=250;
	
//	private final ImageIcon splashImageTop;
//	private final JLabel splashImageLabelTop;
//	private final ImageIcon splashImageBottom;
//	private final JLabel splashImageLabelBottom;
	private final JLabel splashTitle;
	private final JLabel splashVersion;
	private final JLabel splashAuthors;
	private final JLabel splashURL;
	private final JLabel splashCopyright;
	private final JLabel splashAddress;
	private final JLabel splashASIOTrademark;
	private final JLabel splashBatik;
	private final JLabel splashBatikURL;
	private JEditorPane splashCredits;

	private Logger logger;
	
	public SplashPanel () {
		super();
		setBackground(Color.WHITE);
		setOpaque(true);
		
		logger = Logger.getLogger("ipsk.apps.speechrecorder");
		
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		// add mouse empty listeners to prevent mouse events
		// reaching the panes below the glass pane
		addMouseListener(new MouseAdapter() {});
		addMouseMotionListener(new MouseMotionAdapter() {});
		
		JPanel splashTextPane = new JPanel();
		Border emptyBorder = BorderFactory.createEmptyBorder(10,10,10,10);
		splashTextPane.setBorder(emptyBorder);		
		splashTextPane.setLayout(new BoxLayout(splashTextPane, BoxLayout.Y_AXIS));
		splashTextPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		
//		splashImageTop = new ImageIcon(getClass().getResource("icons/SplashBackgroundTop.jpg"));
//		splashImageLabelTop = new JLabel(splashImageTop);
		
		splashTitle = new JLabel("SpeechRecorder", JLabel.CENTER);
		splashTitle.setFont(new Font("Sans-serif", Font.BOLD, HUGE));
		splashTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//splashVersion = new JLabel("Version 2.0", JLabel.CENTER);
		splashVersion = new JLabel("Version "+SpeechRecorder.VERSION, JLabel.CENTER);
		splashVersion.setFont(new Font("Sans-serif", Font.PLAIN, NORMAL));
		splashVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//splashAuthors = new JLabel("Chr. Draxler, K. J\u00E4nsch", JLabel.CENTER);
		splashAuthors = new JLabel(SpeechRecorder.AUTHORS, JLabel.CENTER);
		splashAuthors.setFont(new Font("Sans-serif", Font.PLAIN, LARGE));
		splashAuthors.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		splashURL = new JLabel("http://www.bas.uni-muenchen.de/Bas/software/", JLabel.CENTER);
		splashURL.setFont(new Font("sans-serif", Font.PLAIN, NORMAL));
		splashURL.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		//splashCopyright = new JLabel("Copyright 2004", JLabel.CENTER);
		splashCopyright = new JLabel(SpeechRecorder.COPYRIGHT, JLabel.CENTER);
		splashCopyright.setFont(new Font("Sans-serif", Font.PLAIN, NORMAL));
		splashCopyright.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		splashAddress = new JLabel("Institut f\u00FCr Phonetik und Sprachverarbeitung, LMU M\u00FCnchen", JLabel.CENTER);
		splashAddress.setFont(new Font("Sans-serif", Font.PLAIN, NORMAL));
		splashAddress.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		splashCredits = new JEditorPane();
		splashCredits.setEditable(false);
		splashCredits.setFont(new Font("Sans-serif", Font.PLAIN, NORMAL));
		URL creditURL = getClass().getResource("text/credits.html");
        
		try {
			splashCredits.setPage(creditURL);
		} catch (IOException e) {
			logger.severe("IOException when loading splash screen credits: " + creditURL.toExternalForm());
		}
		splashCredits.getDocument().addDocumentListener(this);
		splashCredits.setPreferredSize(new Dimension(CREDITPANE_WIDTH,CREDITPANE_HEIGHT));
		
		splashASIOTrademark=new JLabel("ASIO Technology by Steinberg");
		splashASIOTrademark.setFont(new Font("Sans-serif", Font.PLAIN, NORMAL));
		splashASIOTrademark.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		splashBatik=new JLabel("Apache Batik SVG by \"The Apache Software Foundation\"");
		splashBatik.setFont(new Font("Sans-serif", Font.PLAIN, NORMAL));
		splashBatik.setAlignmentX(Component.CENTER_ALIGNMENT);
		splashBatikURL=new JLabel("http://xmlgraphics.apache.org/batik/");
		splashBatikURL.setFont(new Font("Sans-serif", Font.PLAIN, NORMAL));
		splashBatikURL.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		splashTextPane.add(splashTitle);
		splashTextPane.add(Box.createRigidArea(new Dimension(0,20)));
		splashTextPane.add(splashVersion);
		splashTextPane.add(Box.createRigidArea(new Dimension(0,10)));
		splashTextPane.add(splashAuthors);
		splashTextPane.add(Box.createRigidArea(new Dimension(0,10)));
		splashTextPane.add(splashCopyright);
		splashTextPane.add(Box.createRigidArea(new Dimension(0,10)));
		splashTextPane.add(splashAddress);
		splashTextPane.add(Box.createRigidArea(new Dimension(0,10)));
		splashTextPane.add(splashURL);
		splashTextPane.add(Box.createRigidArea(new Dimension(0,20)));
		splashTextPane.add(splashASIOTrademark);
		splashTextPane.add(Box.createRigidArea(new Dimension(0,20)));
		splashTextPane.add(splashBatik);
		splashTextPane.add(splashBatikURL);
		splashTextPane.add(Box.createRigidArea(new Dimension(0,20)));
		splashTextPane.add(splashCredits);

//		add(splashImageLabelTop,BorderLayout.NORTH);
		add(splashTextPane,BorderLayout.CENTER);
	}

    private void updateSize(){
        //System.out.println("Updating");
        revalidate();
        Container parent=getParent();
        if (parent !=null){
            //parent.invalidate();
            if (parent instanceof JComponent){
                ((JComponent)parent).revalidate();
            }
        }
    }
    
    public void changedUpdate(DocumentEvent e) {
      updateSize();
    
    }

    public void insertUpdate(DocumentEvent e) {
      updateSize();
      
    }

    public void removeUpdate(DocumentEvent e) {
       updateSize();
      
        
    }
}
