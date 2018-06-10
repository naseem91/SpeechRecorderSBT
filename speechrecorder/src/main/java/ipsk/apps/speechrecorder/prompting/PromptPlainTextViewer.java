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

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;

import javax.swing.BorderFactory;

/**
 * PromptPlainTextViewer displays prompt text in a text area. 
 * 
 * Text alignment can be set horizontally or vertically.
 * Font family, style, and size are set from the configuration file.
 * NOTE: if a word is longer than the width of the window the display does not work
 *
 * @author Chr. Draxler
 * @version 1.0 November 2002
 */

@Title("Plain text prompter")
@Description("Presents plain text. Automatically inserts line feeds.")
@Vendor("Institute of Phonetics and Speech processing, Munich")
@Version(major=1)
public class PromptPlainTextViewer extends BasicPromptPresenter implements PromptPresenter {
   
	private static final long serialVersionUID = 1L;
	public static final PromptPresenterServiceDescriptor DESCRIPTOR=new BasicPromptPresenterServiceDescriptor(PromptPlainTextViewer.class.getName(),new LocalizableMessage("Plain text prompter"), "Institute of Phonetics and Speech processing, Munich", new ipsk.text.Version(new int[]{1,0,0}), new LocalizableMessage("Presents plain text. Automatically inserts line feeds."),PromptPlainTextViewer.getSupportedMIMETypes());
	public final static int MINFONTSIZE = 12;
	public final static int MAXFONTSIZE = 48;
	public final static int FONTDOWNSTEP = 4;
	
	public final static int LEFT = 0;
	public final static int RIGHT = 1;
	public final static int CENTER = 2;
	public final static int TOP = 3;
	public final static int BOTTOM = 4;
	public final static int MIDDLE = 5;
    
    public final static String DEF_CHARSET="UTF-8";

	private String promptText = "";
	private Color textColor = Color.black;
//	private Color idleColor = Color.lightGray;
	private Color backgroundColor = Color.white;
	private int hAlign;
	private int vAlign;
	private String fontFamily = "sans-serif";
	private String fontStyleName = "bold";
	private int fontStyle;
	private int fontSize = 48;

	
	public PromptPlainTextViewer() {
		super();
		//fontFamily = System.getProperty("prompt.font_family");
		//fontStyleName = System.getProperty("prompt.font_style").toLowerCase();
		//fontSize = Integer.parseInt(System.getProperty("prompt.font_size"));
		setBackground(backgroundColor);
		promptFont = new Font("sans-serif", Font.BOLD, 48);
		if (fontSize < MINFONTSIZE) {
			fontSize = MINFONTSIZE;
		} else if (fontSize > MAXFONTSIZE) {
			fontSize = MAXFONTSIZE;
		}
		if (fontStyleName.equals("bold")) {
			fontStyle = Font.BOLD;
		} else if (fontStyleName.equals("italic")) {
			fontStyle = Font.ITALIC;
		} else {
			fontStyle = Font.PLAIN;
		}
		promptFont = new Font(fontFamily, fontStyle, fontSize);

		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		hAlign = PromptPlainTextViewer.CENTER;
		vAlign = PromptPlainTextViewer.MIDDLE;
	}
	
	public void setAlignment(int h, int v) {
		hAlign = h;
		vAlign = v;
	}

	public void prepareGraphics(Graphics g) {
	    if (g instanceof Graphics2D){
//	        ((Graphics2D)g).setRenderingHint(
//	                RenderingHints.KEY_TEXT_ANTIALIASING,
//	                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	        ((Graphics2D)g).setRenderingHint(
                  RenderingHints.KEY_TEXT_ANTIALIASING,
                  RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
	        
	    }
		int xMargin = 2;
		TextGraphicsItem[] itemList = null;

		int width = getSize().width;
		int height = getSize().height;
		//System.out.println("prepareGraphics(" + width + ", " + height + ")");
		
		int[] lineLengths;

		String[] pW = promptText.split("\\s");
		// at most each word can be displayed in a line on its own
		lineLengths = new int[pW.length];		

		if (pW != null && pW.length>0) {

			boolean textSizeOK = false;
			int newFontSize = fontSize;
			
			for (int fs = fontSize; ((fs >= MINFONTSIZE) && (!textSizeOK)); fs = fs - FONTDOWNSTEP) {
				int xMax = 0;
				int yMax = 0;
				int lineCount = 0;
				
				newFontSize = fs;
				promptFont = new Font(fontFamily, fontStyle, newFontSize);
				g.setFont(promptFont);
				FontMetrics fM = getFontMetrics(promptFont);

				int lineSpacing = (int) (fs / 10) + 1;
				int yStep = fs + lineSpacing;

				//System.out.println("reduce font size: " + fs + ", " + textSizeOK + ", " + yStep + ", " + lineSpacing);
				itemList = new TextGraphicsItem[pW.length];

				int x = xMargin;
				int y = yStep;

				int lineLength = xMargin;
				for (int i = 0; i < pW.length; i++) {
                   
					// step to the next line, if the string item does not fit
				    // does not apply to the first item to avoid an additional 
				    // empty line
                    if (i>0 && x + fM.stringWidth(pW[i] + " ") > width - 4) {
						lineLengths[lineCount] = lineLength;
						lineCount++;
						y = y + yStep;
						x = xMargin;
					}
					itemList[i] = new TextGraphicsItem(x, y, lineCount, pW[i]);

					lineLength = x + fM.stringWidth(pW[i]);
					x = x + fM.stringWidth(pW[i] + " ");
					if (x > xMax)
						xMax = x;
					if (y > yMax)
						yMax = y;
				}
				
				//System.out.println("yMax, height: " + yMax + ", " + height);
				
				if (yMax <= height) {
					textSizeOK = true;
					int tmpHAlign = hAlign;
					if (newFontSize < fontSize) {
						tmpHAlign = PromptPlainTextViewer.LEFT;
					}
                     
                    lineLengths[lineCount] = lineLength;
                   
					for (int i = 0; i < itemList.length; i++) {
						if (tmpHAlign == PromptPlainTextViewer.LEFT) {
							itemList[i].xPos = itemList[i].xPos;
						} else if (tmpHAlign == PromptPlainTextViewer.RIGHT) {
							itemList[i].xPos =
								width
									- xMargin
									- lineLengths[itemList[i].line]
									+ itemList[i].xPos;
						} else if (tmpHAlign == PromptPlainTextViewer.CENTER) {
							itemList[i].xPos =
								((int) ((width - lineLengths[itemList[i].line])
									/ 2))
									+ itemList[i].xPos;
						}
						if (vAlign == PromptPlainTextViewer.TOP) {
							itemList[i].yPos = itemList[i].yPos;
						} else if (vAlign == PromptPlainTextViewer.BOTTOM) {
							itemList[i].yPos =
								height
									- lineSpacing
									- (yMax + lineSpacing)
									+ itemList[i].yPos;
						} else if (vAlign == PromptPlainTextViewer.MIDDLE) {
							itemList[i].yPos =
								((int) ((height - yMax) / 2))
									+ itemList[i].yPos;
						}
					}
				}
			}

			for (int i = 0; i < pW.length; i++) {
				g.drawString(
					itemList[i].getString(),
					itemList[i].getX(),
					itemList[i].getY());
			}
		}
	}

	public void paintComponent(Graphics g) {
		//System.out.println("paint PromptPlainTextViewer: " + promptText);
//		g.setColor(backgroundColor);
//		g.fillRect(1, 1, getSize().width - 2, getSize().height - 2);
	    super.paintComponent(g);
		g.setColor(textColor);
        try{
            prepareGraphics(g);
        }catch(ArrayIndexOutOfBoundsException arrException){
            // TODO the cause should be fixed !
            arrException.printStackTrace();
        }
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
	* @param s text
	* @param d prompt text description
	*/
	public void setContents(String s, String d) {
		promptText = s.replaceAll("\\s{2,}"," ");
	}
    
    /**
    * sets the prompt text
    * @param s text
    * @param d prompt text description
    * @param type type
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
		if (fontSize < MINFONTSIZE) {
			fontSize = MINFONTSIZE;
		} 
//		if (fontSize > MAXFONTSIZE) {
//			fontSize = MAXFONTSIZE;
//		}
//		promptFont = promptFont.deriveFont(fontSize);
		fontStyle = promptFont.getStyle();
		fontFamily = promptFont.getFamily();
		//System.out.println("PromptPlainTextViewer promptFont: "+promptFont);
		//System.out.println("PromptPlainTextViewer promptFont: "+fontFamily+" "+fontSize+" "+fontStyle);
	}
	
	

	/**
	* auxiliary class for drawing text items into a JPanel. A TextGraphicsItem
	* contains the x,y coordinates of a string, the line number, and the string itself.
	*/

	class TextGraphicsItem {
		protected int xPos;
		protected int yPos;
		protected int line;
		protected String textString;

		protected TextGraphicsItem(int x, int y, int l, String s) {
			xPos = x;
			yPos = y;
			line = l;
			textString = s;
			//System.out.println("tgi - " + s + ": " + l + ", " + x + ", " + y);
		}

		public int getX() {
			return xPos;
		}

		public int getY() {
			return yPos;
		}

		public String getString() {
			return textString;
		}
	}



    public boolean getEmphasized() {
        
        return emphasized;
    }

    public void setEmphasized(boolean emphasized) {
        this.emphasized=emphasized;
        if(emphasized){
            setForeground(Color.black);
        }else{
           setForeground(Color.lightGray);
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
        promptText = text.replaceAll("\\s{2,}"," ");
    }

    /* (non-Javadoc)
     * @see ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter#getServiceDescriptor()
     */
    public PromptPresenterServiceDescriptor getServiceDescriptor() {
       return DESCRIPTOR;
    }
	
}