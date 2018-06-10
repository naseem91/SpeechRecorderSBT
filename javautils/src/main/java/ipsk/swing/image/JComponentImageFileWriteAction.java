//    IPS Java Utils
// 	  (c) Copyright 2011
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

package ipsk.swing.image;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.image.RenderedImage;

import ipsk.awt.image.ComponentToImageConverter;
import ipsk.swing.JImageFileWriter;
import ipsk.swing.action.tree.AbstractActionLeaf;
import ipsk.util.LocalizableMessage;

/**
 * @author klausj
 *
 */
public class JComponentImageFileWriteAction extends AbstractActionLeaf {

    private Component component;
    public JComponentImageFileWriteAction(Component component,LocalizableMessage localizableMessage){
        super(localizableMessage);
        this.component=component;
    }
    
    /* (non-Javadoc)
     * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent arg0) {
      
        RenderedImage ri=ComponentToImageConverter.grab(component);
        JImageFileWriter.showFileStoreDialog(component, ri);
    }

}
