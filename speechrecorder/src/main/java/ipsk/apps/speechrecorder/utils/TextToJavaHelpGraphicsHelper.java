//    Speechrecorder
// 	  (c) Copyright 2011
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


package ipsk.apps.speechrecorder.utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;

/**
 * @author klausj
 *
 */
public class TextToJavaHelpGraphicsHelper {

    public static String TEX_INCLUDEGRAPHICS="\\includegraphics";
    public static double PIXELS_CM=60;
    public TextToJavaHelpGraphicsHelper(){
        super();
    }
    
    public void convertTexForJavaHelp(Reader inputReader,Writer outputwriter,File imageDir) throws IOException{
        LineNumberReader lnr=new LineNumberReader(inputReader);
        String line=null;
        

                try {
                    while((line=lnr.readLine())!=null){
                   
                     StringBuffer convertedLine=new StringBuffer();
                     int startPos=0;
                     do{
                     int pos=line.indexOf(TEX_INCLUDEGRAPHICS,startPos);
                        if(pos!=-1){
                            convertedLine.append(line.substring(startPos, pos));
                            convertedLine.append(TEX_INCLUDEGRAPHICS);
                            pos+=TEX_INCLUDEGRAPHICS.length();
                            Integer pixelWidth=null;
                            if(line.substring(pos, pos+1).equals("[")){
                                convertedLine.append("[");
                                pos++;
                                int endParams=line.indexOf("]",pos);
                                String params=line.substring(pos, endParams);
                                StringTokenizer optionTokenizer=new StringTokenizer(params,",");
                                while(optionTokenizer.hasMoreTokens()){
                                    String option=optionTokenizer.nextToken();
                                    String trimmedOption=option.trim();
                                    if(trimmedOption.startsWith("width=")){
                                        String widthValue=trimmedOption.substring("width=".length());
                                        widthValue=widthValue.trim();
                                        int unitPos=widthValue.indexOf("cm");
                                        if(unitPos!=-1){
                                            String value=widthValue.substring(0, unitPos).trim();
                                            double cmVal=Double.parseDouble(value);
                                            pixelWidth=(int)(cmVal*PIXELS_CM);
                                            // htlatex does not use the width and height 
//                                            convertedLine.append("width=");
//                                            convertedLine.append(pixelVal);
//                                            convertedLine.append("px");
                                            
                                            // .. so we scale the image
                                            
                                        }
                                        convertedLine.append(option);
                                       
                                    }else{
                                        convertedLine.append(option);
                                    }
                                }
                                convertedLine.append("]");
                                pos=endParams+1;
//                                startPos=pos;
                                
                            }else{
                            
                              
//                               startPos=pos;
                            }
                            
                            // find PNG file
                            int filenamePos=line.indexOf("{", pos);
                            int filenameEndPos=line.indexOf("}", filenamePos);
                            String filename=line.substring(filenamePos+1, filenameEndPos).trim();
                            int extIndex=filename.lastIndexOf('.');
                            String fileNameBody=filename.substring(0, extIndex);
                            String ext=filename.substring(extIndex);
                            
                            BufferedImage img=ImageIO.read(new File(filename));
                            
                            int imgWidth=img.getWidth();
                            if(pixelWidth==null){
                                pixelWidth=imgWidth;
                            }
                            double factor=(double)pixelWidth/(double)imgWidth;
//                            int w = (int) (imgWidth * factor);
                            int h = (int) ((double)img.getHeight() * factor);
                            BufferedImage scaled = new BufferedImage(pixelWidth, h,
                                    BufferedImage.TYPE_INT_RGB);

                            Graphics2D g = scaled.createGraphics();
                            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                            g.drawImage(img, 0, 0, pixelWidth, h, null);
                            g.dispose();
                            
                            String jhFilename=fileNameBody+"_"+pixelWidth+"x"+h+".png";
                            File jhFile=null;
                            if(imageDir!=null){
                                
                                jhFile=new File(imageDir,jhFilename);
                                jhFile.mkdirs();
                            }else{
                                jhFile=new File(jhFilename);
                            }
                            ImageIO.write(scaled,"PNG",jhFile);
                           convertedLine.append("{"+jhFilename+"}");
                           pos=filenameEndPos+1;
                           startPos=pos;
                        }else{
                            
                            convertedLine.append(line.substring(startPos));
                            break;
                        }
                     }while(startPos<line.length());
                     
                    outputwriter.append(convertedLine);
                    outputwriter.append('\n');
                    }
                } catch (IOException e) {
                   try {
                    lnr.close();
                   } catch (IOException e1) {
                       // already throwing exception
                   }
                   throw e;
                }
        
    }
    /**
     * @param args
     */
    public static void main(String[] args) {
        TextToJavaHelpGraphicsHelper h=new TextToJavaHelpGraphicsHelper();
        Writer w=null;
        File imageDir=null;
        File inFile=new File(args[0]);
        if(args.length>=2){
            File outFile=new File(args[1]);
            try {
                w=new FileWriter(outFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else{
            w=new PrintWriter(System.out);
        }
        if(args.length==3){
            imageDir=new File(args[2]);
        }
        try {
            Reader r=new FileReader(inFile);
            h.convertTexForJavaHelp(r, w,imageDir);
            r.close();
            w.close();
            
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

}
