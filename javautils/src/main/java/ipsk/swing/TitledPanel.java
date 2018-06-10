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

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * 
 * A JPanel with lowered and titled bevel border. Nice ;)
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class TitledPanel extends JPanel {
    private Border baseBorder;
    private Border border;
    private String title = null;
    private boolean enabled = true;

    /**
	 *  
	 */
    public TitledPanel() {
        this("");
    }

    public TitledPanel(String title) {
        super();
        baseBorder = BorderFactory.createLoweredBevelBorder();
        setTitle(title);
    }

    public TitledPanel(Border baseBorder, String title) {
        super();
        this.baseBorder = baseBorder;
        setTitle(title);
    }

    /**
     * Set the title.
     * 
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
        if (border instanceof TitledBorder) {
            if (title != null) {
                ((TitledBorder) border).setTitle(title);
            } else {
                border = baseBorder;
                setBorder(border);
            }
        } else {
            if (title != null) {
                border = BorderFactory.createTitledBorder(baseBorder, title);
                setBorder(border);
            }
        }
    }

    public String getTitle() {
        return title;
    }

    public void setEnabled(boolean enabled) {
//        this.enabled = enabled;
    	super.setEnabled(enabled);
        Color titleColor;

        if (enabled) {
            titleColor = getForeground();
        } else {
            titleColor = UIManager.getColor("Label.disabledForeground");
        }
        if (border instanceof TitledBorder) {
            ((TitledBorder) border).setTitleColor(titleColor);
           
        }
        repaint();
    }

//    public boolean isEnabled() {
//        return enabled;
//    }

}
