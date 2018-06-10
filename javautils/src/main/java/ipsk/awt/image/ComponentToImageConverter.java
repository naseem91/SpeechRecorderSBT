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

package ipsk.awt.image;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

/**
 * Converts a GUI component to an image.
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class ComponentToImageConverter{

    private Component component;
    public ComponentToImageConverter(Component component){
        super();
        this.component=component;
    }
    
    /**
     * Creates a new image and calls the print method to paint the contents of the component to this image. 
     * @param component
     * @return rendered image
     */
    public static RenderedImage grab(Component component){
        Dimension dimension=component.getSize();      
        BufferedImage image = new BufferedImage(dimension.width, dimension.height,BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        component.print(g);
        g.dispose();
        return image;
    }
    
    public RenderedImage grab(){
       return grab(component);
    }
    
 
}
