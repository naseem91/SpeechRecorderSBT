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

package ipsk.swing;

import ipsk.io.FileFilterByExtension;

import java.awt.Component;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 * @author klausj
 *
 */
public class JImageFileWriter{


    public static void showFileStoreDialog(Component dialogParent,RenderedImage image){
        try{
            String[] writerFileSuffixes=ImageIO.getWriterFileSuffixes();
            JFileChooser jfc=new JFileChooser();
            FileFilterByExtension pngFilter=new FileFilterByExtension("Image",writerFileSuffixes);
            jfc.setFileFilter(pngFilter);
            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            int returnVal = jfc.showSaveDialog(dialogParent);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = jfc.getSelectedFile();
                if(file.exists()){
                if (JOptionPane.showConfirmDialog(dialogParent, file.getName()
                        + " exists. Do you want to overwrite ?", "Overwrite file ?",
                        JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION){
                    return;
                }
                }
                    String selExtension=pngFilter.extension(file);
                    if(selExtension==null){
                        JOptionPane.showMessageDialog(dialogParent,"Could not determine image file type by filename extension.", "Image writing error",
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }else{
                    try {
                        Iterator<ImageWriter> wrs=ImageIO.getImageWritersBySuffix(selExtension);
                        if(wrs.hasNext()){
                            ImageWriter iw=wrs.next();
                           FileImageOutputStream fios=new FileImageOutputStream(file);
                            iw.setOutput(fios);
                            iw.write(image);
                            iw.dispose();

                        }else{
                            JOptionPane.showMessageDialog(dialogParent,"No image file writer found for extension: \""+selExtension+"\"", "Image writing error",
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(dialogParent, e
                                .getLocalizedMessage(), "Image writing error",
                                JOptionPane.ERROR_MESSAGE);
                        e.printStackTrace();
                        return;
                    }
                    }
                
            }else if (returnVal==JFileChooser.CANCEL_OPTION){
                JOptionPane.showMessageDialog(dialogParent,"Image save canceled", "Image writing cancel",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }else{
                return;
            }
        }catch(SecurityException se){
            JOptionPane.showMessageDialog(dialogParent, se.getLocalizedMessage(), "Security error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

   
}
