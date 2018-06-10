//    Speechrecorder
//    (c) Copyright 2012
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

package ipsk.apps.speechrecorder.prompting;

import ipsk.apps.speechrecorder.MIMETypes;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterException;
import ipsk.apps.speechrecorder.prompting.presenter.UnsupportedContentException;
import ipsk.db.speech.Mediaitem;
import ipsk.util.LocalizableMessage;
import ipsk.util.services.Description;
import ipsk.util.services.Title;
import ipsk.util.services.Vendor;
import ipsk.util.services.Version;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * PromptFormattedTextViewer displays prompt text in an uneditable editor pane. 
 *
 * @author Chr. Draxler
 * @version 1.0 November 2002
 */
@Title("Formatted text prompter")
@Description("Presents formatted text in HTML or RTF coding.")
@Vendor("Institute of Phonetics and Speech processing, Munich")
@Version(major=1)
public class PromptFormattedTextViewer extends BasicPromptPresenter implements PromptPresenter {
    
	private static final long serialVersionUID = 1L;

	public static final PromptPresenterServiceDescriptor DESCRIPTOR=new BasicPromptPresenterServiceDescriptor(PromptFormattedTextViewer.class.getName(),new LocalizableMessage("Formatted text prompter"), "Institute of Phonetics and Speech processing, Munich", new ipsk.text.Version(new int[]{1,0,0}), new LocalizableMessage("Presents formatted text in HTML or RTF coding."),PromptFormattedTextViewer.getSupportedMIMETypes());
	
	private JEditorPane formattedTextPane;
	private JScrollPane editorScrollPane;
	private JPanel centerPanel;
	//private Color textColor = Color.black;
	//private Color idleColor = Color.lightGray;
	//private Color backgroundColor = Color.white;
	

	
	public PromptFormattedTextViewer() {
		super();
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));	
		create();
	}

	/*
	 * It is necessary to create a new JEditorPane if content is switched 
	 * from text content to URL content. The URL is not displayed reliable. 
	 */
	private void create(){
		if(editorScrollPane != null){
		remove(editorScrollPane);
		}
		formattedTextPane = new JEditorPane();
		formattedTextPane.setEditable(false);
		centerPanel=new JPanel(new GridBagLayout());
		// setting GridBagConstraints not necesary,the defaults center the editor pane properly.
	
//		GridBagConstraints c=new GridBagConstraints();
//		c.weightx=2.0;
//		c.weighty=2.0;
//		c.fill=GridBagConstraints.NONE;
//		c.anchor=GridBagConstraints.CENTER;
		centerPanel.add(formattedTextPane);
		editorScrollPane = new JScrollPane(centerPanel);
		
		editorScrollPane.setVerticalScrollBarPolicy(
		                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		//editorScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(editorScrollPane,BorderLayout.CENTER);
	}

// Not good practice to set size in the paintComponent method.
// Borderlayout.CENTER fits the ScrollPanel automatically to parent size.
	
//	//TODO: make sure formatted text pane fills the visible area
//	public void paintComponent(Graphics g) {
//		formattedTextPane.setSize(getSize());
//		//editorScrollPane.setPreferredSize(getSize());
//		//formattedTextPane.setSize(editorScrollPane.getViewport().getExtentSize());
//	}

	public void showContents() {
	};
	public void hideContents() {
	};
	public void loadContents(URL url) {
	};

	/**
	* sets the prompt text
	* @param s text
	*/
	public void setContents(String s) {
		//promptText = s;
		formattedTextPane.setText(s);

	}

	/**
	* sets the prompt text
	* @param s text
	* @param d prompt text description
	*/
	public void setContents(String s, String d) {
		//promptText = s;
		setContents(s);
	}
	
	public void setContents(String string, String description,String type){
    	formattedTextPane.setContentType(type);
    	//promptText=string;
    	setContents(string);

    }
	/**
	* sets the prompt text
	* @param url URL where the text is stored
	*/
	public void setContents(URL url) {
		try{
			// Create new JeditorPane !
			// JEditorPane bug ?
			//create(url);
			//formattedTextPane.setText(null);
			
			// Switching between setText() and setPage() does not work reliable
			// so I create a new EditorPane here, Klaus J.
			centerPanel.remove(formattedTextPane);
			formattedTextPane =new JEditorPane(url);
			centerPanel.add(formattedTextPane);
			//formattedTextPane.setPage(url);
		
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	* sets the prompt text
	* @param url URL where the text is stored
	* @param d prompt text description
	*/
	public void setContents(URL url, String d) {
		setContents(url);
	}

	/**
	 * sets the prompt text and adjusts the editor kit to the type of the text
	 * @param url
	 * @param d
	 * @param type
	 */
	public void setContents(URL url, String d, String type) {
		
		setContents(url,d,type,null);		
	}
    
    /**
     * sets the prompt text and adjusts the editor kit to the type of the text
     * @param url
     * @param d
     * @param type
     */
    public void setContents(URL url, String d, String type,String charset) {
        centerPanel.remove(formattedTextPane);
        formattedTextPane =new JEditorPane();
        centerPanel.add(formattedTextPane);
        if (MIMETypes.isOfType(type, MIMETypes.FORMATTEDTEXTMIMETYPES)) {
//            String edKitClass=JEditorPane.getEditorKitClassNameForContentType(type);
            formattedTextPane.setContentType(type);
        }  
        try {
            formattedTextPane.setPage(url);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        setContents(url);       
    }
   
    public void loadContents()
    throws PromptPresenterException {
        if(mediaitems.length > 1){
            throw new UnsupportedContentException("Multiple media items not supported!");
        }  

        Mediaitem mi=mediaitems[0];
        URL url=applyContextToMediaitemURL(mi);
        String htmlText=mi.getText();
        centerPanel.remove(formattedTextPane);
        formattedTextPane =new JEditorPane();
        centerPanel.add(formattedTextPane);
        String type=mi.getMimetype();
        if (MIMETypes.isOfType(type, MIMETypes.FORMATTEDTEXTMIMETYPES)) {
//            String edKitClass=formattedTextPane.getEditorKitClassNameForContentType(type);
            formattedTextPane.setContentType(type);
        }  
        if(url==null){
            formattedTextPane.setText(htmlText);
        }else{
            try {
                formattedTextPane.setPage(url);
            } catch (IOException e) {
                throw new PromptPresenterException(e);
            }
        }
    }

    public static String[][] getSupportedMIMETypes() {
        return getSupportedMIMETypes(MIMETypes.FORMATTEDTEXTMIMETYPES);
        
    }

    /* (non-Javadoc)
     * @see ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter#getServiceDescriptor()
     */
    public PromptPresenterServiceDescriptor getServiceDescriptor() {
       return DESCRIPTOR;
    }

 
}