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


package ipsk.apps.speechrecorder.prompting.text;

import ipsk.apps.speechrecorder.MIMETypes;
import ipsk.apps.speechrecorder.prompting.BasicPromptPresenter;
import ipsk.apps.speechrecorder.prompting.BasicPromptPresenterServiceDescriptor;
import ipsk.apps.speechrecorder.prompting.PromptPresenterServiceDescriptor;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterException;
import ipsk.apps.speechrecorder.prompting.presenter.UnsupportedContentException;
import ipsk.db.speech.Mediaitem;
import ipsk.util.LocalizableMessage;
import ipsk.util.services.Description;
import ipsk.util.services.Title;
import ipsk.util.services.Vendor;
import ipsk.util.services.Version;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;

/**
 
 */

@Title("Teleprompter")
@Description("Presents plain text like a teleprompter.")
@Vendor("Institute of Phonetics and Speech processing, Munich")
@Version(major=1)
public class TelePrompterViewer extends BasicPromptPresenter implements PromptPresenter {
    public static final PromptPresenterServiceDescriptor DESCRIPTOR=new BasicPromptPresenterServiceDescriptor(TelePrompterViewer.class.getName(),new LocalizableMessage("Teleprompter"), "Institute of Phonetics and Speech processing, Munich", new ipsk.text.Version(new int[]{1,0,0}), new LocalizableMessage("Presents plain text like a teleprompter."),TelePrompterViewer.getSupportedMIMETypes());
	
    
    public final static String DEF_CHARSET="UTF-8";

	private String promptText = "";
	private Color textColor = Color.BLACK;
	
	private Color backgroundColor = Color.WHITE;
	
	private String fontFamily = "sans-serif";
	private String fontStyleName = "bold";
	private int fontStyle;
	private int fontSize = 48;

	
	
	public TelePrompterViewer() {
		super();
		//fontFamily = System.getProperty("prompt.font_family");
		//fontStyleName = System.getProperty("prompt.font_style").toLowerCase();
		//fontSize = Integer.parseInt(System.getProperty("prompt.font_size"));
		setBackground(backgroundColor);
		promptFont = new Font("sans-serif", Font.BOLD, 48);
		
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		
	}
	
	



	public void paintComponent(Graphics g) {
		//System.out.println("paint PromptPlainTextViewer: " + promptText);
//		g.setColor(backgroundColor);
//		g.fillRect(1, 1, getSize().width - 2, getSize().height - 2);
	    super.paintComponent(g);
		g.setColor(textColor);
		g.drawString(promptText, 0, 0);
	}
	

	public void showContents() {};
	
	public void hideContents() {};
	
//	public void loadContents(URL url) {};

	/**
	* sets the prompt text
	* @param s text
	*/
	public void setContents(String s) {
	    promptText = s;
	}

	/**
	* sets the prompt text
	* @param s the text
	* @param d prompt text description
	*/
	public void setContents(String s, String d) {
		promptText = s;
	}
    
    /**
    * sets the prompt text
    * @param s the text is stored
    * @param d prompt text description
    */
    public void setContents(String s, String d,String type) {
        setContents(s, d);
    }
	
	/**
	* sets the prompt text
	* @param u URL where the text is stored
	*/
	public void setContents(URL u) {
	    setContents(u, null);
    }

	/**
	* sets the prompt text
	* @param u URL where the text is stored
	* @param d prompt text description
	*/
	public void setContents(URL u, String d) {
        setContents(u, null, null);
    }
    public void setContents(URL u, String d, String type) {
        setContents(u, null, null,null);
    }
	/**
	* sets the prompt text
	* @param u URL where the text is stored
	* @param d prompt text description
	* @param type type
	* @param cs charset
	*/
	public void setContents(URL u, String d, String type,String cs) {
	    try {
            InputStreamReader r=null;
            if(cs!=null){
                r=new InputStreamReader(u.openStream(),cs);
            }else{
                r=new InputStreamReader(u.openStream());
            }
            StringWriter sw=new StringWriter();
            char[] cbuf=new char[2048];
            int read=0;
            while((read=r.read(cbuf))>=0){
                sw.write(cbuf, 0, read);
            }
            r.close();
            sw.close();
            setContents(sw.getBuffer().toString(),d);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }


	/**
	 * Sets font for prompts.	 
	 * @param promptFont
	 */
	public void setPromptFont(Font promptFont) {
		this.promptFont = promptFont;
		fontSize = promptFont.getSize();
//		if (fontSize < MINFONTSIZE) {
//			fontSize = MINFONTSIZE;
//		} else if (fontSize > MAXFONTSIZE) {
//			fontSize = MAXFONTSIZE;
//		}
//		promptFont = promptFont.deriveFont(fontSize);
		fontStyle = promptFont.getStyle();
		fontFamily = promptFont.getFamily();
		//System.out.println("PromptPlainTextViewer promptFont: "+promptFont);
		//System.out.println("PromptPlainTextViewer promptFont: "+fontFamily+" "+fontSize+" "+fontStyle);
	}
	
	



    public boolean getEmphasized() {
        
        return emphasized;
    }

    public void setEmphasized(boolean emphasized) {
        this.emphasized=emphasized;
        if(emphasized){
            setForeground(Color.BLACK);
        }else{
           setForeground(Color.LIGHT_GRAY);
        }
    }
    
    public static String[][] getSupportedMIMETypes() {
        return getSupportedMIMETypes(MIMETypes.PLAINTEXTMIMETYPES);
    }

   
    public void loadContents()
            throws PromptPresenterException {
        if(mediaitems==null || mediaitems.length==0){
            throw new UnsupportedContentException("No media item to display!");
        }else if(mediaitems.length > 1){
            throw new UnsupportedContentException("Multiple media items not supported!");
        }
        String text=null;
        Mediaitem mi=mediaitems[0];
        URL u=applyContextToMediaitemURL(mi);
        if(u!=null){
            // external URL 
            Charset cs=null;
            String miCharset=mi.getCharSet();
            if(miCharset!=null){
                cs=Charset.forName(miCharset.trim());
            }
            InputStreamReader r=null;
            try {
               
                if(cs!=null){
                    r=new InputStreamReader(u.openStream(),cs);
                }else{
                    r=new InputStreamReader(u.openStream());
                }
                StringWriter sw=new StringWriter();
                char[] cbuf=new char[2048];
                int read=0;
                while((read=r.read(cbuf))>=0){
                    sw.write(cbuf, 0, read);
                }
               
                sw.close();
                //setContents(sw.getBuffer().toString(),d);
                text=sw.getBuffer().toString();
            } catch (IOException e) {
              throw new PromptPresenterException(e);
            }finally{
                try {
                    r.close();
                } catch (IOException e) {
                    throw new PromptPresenterException(e);
                }
            }
        }else{
            // string
            text=mi.getText();
        }
        promptText=text;
    }

    /* (non-Javadoc)
     * @see ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter#getServiceDescriptor()
     */
    public PromptPresenterServiceDescriptor getServiceDescriptor() {
       return DESCRIPTOR;
    }
	
}