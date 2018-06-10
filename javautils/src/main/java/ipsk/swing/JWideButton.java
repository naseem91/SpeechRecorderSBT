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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;


/**
 * A special button implementation for use in very large panels.
 * Only the clipping area of the view is painted for this button. This avoids long computing of very large buttons in scroll panels.
 * The Swing Look&Feel delegate UI can NOT be set for this button !
 * @see javax.swing.JButton
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */
public class JWideButton extends JButton implements AncestorListener{
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -664178466426652767L;

	public JWideButton() {
        this(null);
    }

    /**
     * Creates a button with label.
     * 
     * @param text the text of the button
     */
    public JWideButton(String text) {
    	super(text);
    	
    }

//    public void paint(Graphics g){
//    	paintComponent(g);
//    }
    
    public void paintComponent(Graphics g) {
        // Graphics2D g2=new Graphics2D(g);
        Rectangle clip = g.getClipBounds();
        ButtonModel bm = getModel();
        //g.setFont(g.getFont().deriveFont(Font.BOLD));
        Color defColor = getBackground();
       // Color darkColor = defColor;
        Color borderColor = defColor.darker().darker();

        Color compColor = defColor;

        if (!bm.isEnabled()) {
            borderColor = defColor.darker();
            compColor = defColor;
        } else if (bm.isPressed()) {

            borderColor = defColor;
            compColor = defColor.darker();
        } else if (bm.isRollover()) {
            borderColor = defColor.brighter();
            compColor = defColor;
        }
        Rectangle b = getBounds();
        Rectangle viewR = new Rectangle(b);
        viewR.x = 0;
        viewR.y = 0;

        // System.out.println("textw: "+r.getWidth()+"\nview: "+viewR+"\ntext:
        // "+textR);
        g.setColor(compColor);

        g.fillRect(clip.x, clip.y, clip.width, clip.height);
        g.setColor(borderColor);

        g.drawLine(0, 0, 0, b.height - 1);
        g.setColor(borderColor);
        g.drawLine(b.width - 1, 0, b.width - 1, b.height - 1);

        g.drawLine(clip.x, 0, clip.x + clip.width, 0);
        g.drawLine(clip.x, b.height - 1, clip.x + clip.width, b.height - 1);
        String text = getText();
        if (text != null) {
            FontMetrics fm = g.getFontMetrics();

            Rectangle iconR = new Rectangle();
            Rectangle textR = new Rectangle();
            SwingUtilities.layoutCompoundLabel(fm, text, null, CENTER, CENTER,
                    CENTER, CENTER, viewR, iconR, textR, 0);
            if (bm.isEnabled()) {
                g.setColor(getForeground());
            } else {
                g.setColor(defColor.darker());
            }
            g.drawString(text, textR.x, textR.y + fm.getAscent());
        }
    }

	public void ancestorAdded(AncestorEvent arg0) {
		Graphics g=getGraphics();
    	Rectangle2D maxCharBounds=g.getFontMetrics().getMaxCharBounds(g);
    	
        setPreferredSize(new Dimension(100, (int)maxCharBounds.getHeight()+2));
		
	}

	public void ancestorMoved(AncestorEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void ancestorRemoved(AncestorEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}
