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

/**
* PromptImageViewer fetches the images found in the image directory
* and provides methods for selecting them.
*
* @author Christoph Draxler
* @version 1.0
* @since JDK 1.0
*/

package ipsk.apps.speechrecorder.prompting;

import ipsk.apps.speechrecorder.MIMETypes;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterException;
import ipsk.apps.speechrecorder.prompting.presenter.PromptPresenterListener;
import ipsk.apps.speechrecorder.prompting.presenter.UnsupportedContentException;
import ipsk.db.speech.Mediaitem;
import ipsk.util.LocalizableMessage;
import ipsk.util.services.Description;
import ipsk.util.services.Title;
import ipsk.util.services.Vendor;
import ipsk.util.services.Version;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.BorderFactory;

@Title("Image prompter")
@Description("Presents images.")
@Vendor("Institute of Phonetics and Speech processing, Munich")
@Version(major=1)
public class PromptImageViewer extends BasicPromptPresenter implements PromptPresenter {
    
	private static final long serialVersionUID = 1L;
	private static final PromptPresenterServiceDescriptor DESCRIPTOR=new BasicPromptPresenterServiceDescriptor(PromptImageViewer.class.getName(),new LocalizableMessage("Image prompter"), "Institute of Phonetics and Speech processing, Munich", new ipsk.text.Version(new int[]{1,0,0}), new LocalizableMessage("Presents images."),PromptImageViewer.getSupportedMIMETypes());
	private Hashtable promptImages;
//	private ImageIcon currentImage;
//	private String imageName;
//	private JLabel imageLabel;

	private Image image;
	
	private Logger logger;
//    private PromptPresenterListener listener;
    MediaTracker tracker;
	
	/**
	 * PromptImageViewer fetches images from a specified directory
	 * and caches them in a hashtable for quick access
	 *
	 */
	PromptImageViewer() {
		super();
		logger = Logger.getLogger("ipsk.apps.speechrecorder");
		setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		tracker = new MediaTracker(this);
	}
	
	/**
	* PromptImageViewer fetches images from a specified directory
	* and caches them in a hashtable for quick access
	*
	* @param Vector vector of images
	*/
/*	PromptImageViewer(Vector iv) {
		this();
		promptImages = fetchImages(iv);
	}

	PromptImageViewer(Hashtable ih) {
		this();
	}
*/
	/**
	 * Loads the images for the list of image sources given
	 * @param ih image sources
     */
	public void setRecScriptResources(Hashtable ih) {
		promptImages = ih;
	}
		
	/**
	 * fetchImages() fetches all images referred to in the recording script
	 * so that they can be displayed quickly. The images are stored in a
	 * Hashtable with their URL as key.
	 * 
	 * @return Hashtable
	 */
	
	public Hashtable fetchImages(Vector iv) {
		Hashtable images = new Hashtable();
		MediaTracker tracker = new MediaTracker(this);
		final int PRIORITY = 1;
		Image [] imagesToLoad = new Image[iv.size()];
		
		for (int i = 0; i < iv.size(); i++) {
			URL url = (URL) iv.elementAt(i);
			imagesToLoad[i] = Toolkit.getDefaultToolkit().getImage(url);
			tracker.addImage(imagesToLoad[i], PRIORITY);
			images.put(url, imagesToLoad[i]);
		}
		
		try {
			tracker.waitForAll();
		} catch (InterruptedException e) {
		}

		if (tracker.checkAll()) {
			if (tracker.isErrorAny()) {
				logger.severe("Error loading images.");
			}
		}
		return images;
	}

	public Dimension getPreferredSize() {
		return new Dimension(getSize());
	}

	public void showContents() {
	};
	
	public void hideContents() {
	};
	
	public void loadContents(URL url) {
	};

	/**
	* setContents() selects the next image to display
	*
	* @param imageName name of image file
	*/
	public void setContents(String imageName) {
//		System.out.println("setImage (String): " + imageName);
//		currentImage = (ImageIcon) promptImages.get(imageName);
//		imageLabel.setIcon(currentImage);
	}

	/**
	* setContents() selects the next image to display
	*
	* @param imageName name of image file
	* @param description description of image
	*/
	public void setContents(String imageName, String description) {
//		System.out.println("setImage (String, Description): " + imageName);
//		currentImage = (ImageIcon) promptImages.get(imageName);
//		imageLabel.setIcon(currentImage);
//		imageLabel.setText(description);
	}
      
    public void setContents(String cts, String description, String type) {
          
     }
      
	/**
	* setContents() selects the next image to display
	*
	* @param imageURL URL of image file
	*/
	public synchronized void setContents(URL imageURL) {
//		System.out.println("setImage (URL): " + imageURL.toExternalForm());
//		currentImage = (ImageIcon) promptImages.get(imageURL);
//		imageLabel.setIcon(currentImage);
		
//		if (image!=null){
//			image.flush();
//			tracker.removeImage(image,0);
//		}
	    if(promptImages!=null){
	        image = (Image) promptImages.get(imageURL);
	    }
		if (image==null){
			// image is not cached
			image = Toolkit.getDefaultToolkit().getImage(imageURL);
		}
//		tracker.addImage(image, 0);
//		try {
//			tracker.waitForAll();
//		} catch (InterruptedException e) {
//		}
//		if (tracker.isErrorAny()) {
//			logger.severe("Error loading image from "+imageURL);
//		}
	}

	/**
	* setContents() selects the next image to display
	*
	* @param imageURL URL of image file
	* @param d description of image
	*/
	public void setContents(URL imageURL, String d) {
		setContents(imageURL);
	}
	
	/**
	* setContents() selects the next image to display
	*
	* @param imageURL URL of image file
	* @param d description of image
	* @param t MIME-type of image
	*/
	public void setContents(URL imageURL, String d, String t) {
		setContents(imageURL);
	}
	
    public void setContents(URL imageUrl, String description, String type,String charset) {
        setContents(imageUrl);    
    }
    
   
    public void loadContents()
    throws PromptPresenterException {
        if(mediaitems==null || mediaitems.length==0){
            throw new UnsupportedContentException("No media item to display!");
        }else if(mediaitems.length > 1){
            throw new UnsupportedContentException("Multiple media items not supported!");
        }  
        
        Mediaitem mi=mediaitems[0];
        URL imageURL=applyContextToMediaitemURL(mi);
        if(promptImages!=null){
            image = (Image) promptImages.get(imageURL);
        }
        if (image==null){
            // image is not cached
            image = Toolkit.getDefaultToolkit().getImage(imageURL);
        }
    }
        
	/**
	 * paintComponents() draws the image; if needed, the image
	 * is scaled proportionally up or down to fit into the display area.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g); //paint background
		Graphics2D g2d=(Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		if (image != null) {
			//Now draw the image scaled.
			int cw = getSize().width;
			int ch = getSize().height;
			int iw = image.getWidth(this);
			int ih = image.getHeight(this);
			
			int w;
			int h;
	
			float wr = (float) cw / (float) iw;
			float hr = (float) ch / (float) ih;
			
			if (wr < hr) {
				w = (int) (iw * wr);
				h = (int) (ih * wr);
			} else {
				w = (int) (iw * hr);
				h = (int) (ih * hr);
			}
			
			// compute origin of image so that it is centered horizontally and vertically
			int x = (cw - w) / 2;
			int y = (ch - h) / 2;
			
			g.drawImage(image, x, y, w, h, this);
		}
	}
	
	 public static String[][] getSupportedMIMETypes() {
	        return getSupportedMIMETypes(MIMETypes.IMAGEMIMETYPES);
	    }

    /* (non-Javadoc)
     * @see ipsk.apps.speechrecorder.prompting.presenter.PromptPresenter#getServiceDescriptor()
     */
    public PromptPresenterServiceDescriptor getServiceDescriptor() {
        return DESCRIPTOR;
    }

 

}