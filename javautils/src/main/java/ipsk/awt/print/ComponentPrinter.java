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

package ipsk.awt.print;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import javax.swing.RepaintManager;

/**
 * Helper class to print AWT/Swing components.
 * Limited to component classes which are rendered solely by the AWT event thread. 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class ComponentPrinter implements Printable {

    private Component component;
    
    
    public ComponentPrinter(Component component){
        this.component=component;
    }
    public int print(Graphics g, PageFormat pageFormat, int pageIndex)
            throws PrinterException {
        if(component instanceof Printable){
            // pass through if component has its own print method
           return ((Printable)component).print(g, pageFormat, pageIndex);
        }else{
            // use paint method of component
            // does not work reliably on all components
            // especially on components with own worker (render) threads
        if (pageIndex > 0) {
            return(NO_SUCH_PAGE);
          } else {
            Graphics2D g2d = (Graphics2D)g;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            double printablewidth=pageFormat.getImageableWidth();
            double printableHeight=pageFormat.getImageableHeight();
            Dimension compDimension=component.getSize();
            double compWidth=compDimension.getWidth();
            double compHeight=compDimension.getHeight();
            double sx=printablewidth/compWidth;
            double sy=printableHeight/compHeight;
            // keep ratio
            double scale=Math.min(sx, sy);
            AffineTransform scaleTransform=AffineTransform.getScaleInstance(scale, scale);
            
            
            RepaintManager repaintManager = RepaintManager.currentManager(component);
            boolean doubleBuffered=repaintManager.isDoubleBufferingEnabled();
            // Switch of double buffering temporarily
            repaintManager.setDoubleBufferingEnabled(false);
            //g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            //scaleTransform.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            //g2d.setTransform(scaleTransform);
            g2d.transform(scaleTransform);
           
            component.print(g2d);
            repaintManager.setDoubleBufferingEnabled(doubleBuffered);
            return(PAGE_EXISTS);
          }
        }
    }

}
