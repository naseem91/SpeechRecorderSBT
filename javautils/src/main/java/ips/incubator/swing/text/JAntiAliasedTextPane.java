//    IPS Java Utils
// 	  (c) Copyright 2018
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

package ips.incubator.swing.text;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;

/**
 * @author klausj
 *
 */
public class JAntiAliasedTextPane extends JTextPane {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1682277943317052567L;

	/**
	 * 
	 */
	public JAntiAliasedTextPane() {
		super();
	}

	/**
	 * @param doc
	 */
	public JAntiAliasedTextPane(StyledDocument doc) {
		super(doc);
	}
	
	// Overwriting paint or paintComponent does not work!!
	public void paint(Graphics g) {
		
//		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
//		g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		System.out.println("AA on");
		super.paintComponent(g);
	}

}
