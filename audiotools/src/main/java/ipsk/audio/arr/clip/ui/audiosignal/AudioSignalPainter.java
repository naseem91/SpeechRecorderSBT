//    IPS Java Audio Tools
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.audio.arr.clip.ui.audiosignal;

import ipsk.audio.arr.clip.ui.AudioSignalUI;
import ipsk.audio.arr.clip.ui.AudioSignalUI.AmplitudeScaleType;
import ipsk.audio.arr.clip.ui.audiosignal.AudioSignalModelRenderer.RenderResult;
import ipsk.audio.dsp.DSPUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

/**
 * @author klausj
 *
 */
public class AudioSignalPainter<T> {
    
    private final static int DEFAULT_NOTIFY_ON_PIXELS=100;
    private final static int WAIT_FOR_THREAD_ON_CLOSE=1000;
    private final Color DEFAULT_SIGNAL_COLOR = Color.GREEN;
    public static final int DEFAULT_BASELOG_LEVEL = -40;
    private RenderResult renderResult;
  
    private AudioSignalUI.AmplitudeScaleType amplitudeScaleType=AmplitudeScaleType.LINEAR;
    private double baseLogLevel;
    private double borderLength;
    
    private Color backgroundColor=null;    //default transparent
    private Color signalColor=DEFAULT_SIGNAL_COLOR;
    private boolean paintPolygons=true;
    
    public AudioSignalPainter(RenderResult rr){
        super();
        this.renderResult=rr;
    }
    
    public AudioSignalPainter(){
        super();
    }
     
    /**
     * Paint method compatible to Java 7 Painter interface
     * @param g
     * @param obj
     * @param width
     * @param height
     */
     public void paint(Graphics2D g, T obj, int width, int height) {
        Rectangle cb=g.getClipBounds();
        int fromPixel=cb.x;
        int toPixel=cb.x+cb.width;
        if(backgroundColor!=null){
            g.setColor(backgroundColor);
            g.fillRect(fromPixel, 0, toPixel-fromPixel, height);
        }
        Integer channels=renderResult.channels;
        if(channels==null){
            return;
        }
//        int channels=renderResult.values[0].max.length;
        int imgHeight=height/channels;
        int[] minY = new int[channels];
        int[] maxY = new int[channels];
        int[][] polyX = new int[channels][4];
        int[][] polyY = new int[channels][4];

        g.setColor(signalColor);
        boolean firstValue = true;
        int valPos;
        //              // find first min/max point value to paint
        //              for (valPos = 0; valPos < vals.length - 1; valPos++) {
        //                  int valPixelPos = vals[valPos + 1].pixelPos;
        //                  if (valPixelPos > paintFrom)
        //                      break;
        //              }
        int to=renderResult.offset+renderResult.length;
        //System.out.println("Paint "+rr.length);
        for (valPos=renderResult.offset; valPos < to; valPos++) {
            AudioSignalModelRenderer.Value v=(renderResult.values)[valPos];
            if (v == null) {
                continue;
            }
            //int pixelPos = v.pixelPos;
            int pixelPos=valPos+renderResult.pixelOffset;

            for (int i = 0; i < channels; i++) {
                //                      minY[i] = (int) (((1-v.min[i]) /2) * (float) imgHeight
                //                              + i * imgHeight + borderLength);
                //                      maxY[i] = (int) (((1 - v.max[i])/2) * (float) imgHeight
                //                              + i * imgHeight + borderLength);

                double minLevel;
                double maxLevel;
                if(amplitudeScaleType.equals(AmplitudeScaleType.LINEAR)){
                    minLevel=v.min[i];
                    maxLevel=v.max[i];
                }else{

                    double logLevelMin=DSPUtils.toPowerLevelInDB(Math.abs(v.min[i]));
                    double logLevelMax=DSPUtils.toPowerLevelInDB(Math.abs(v.max[i]));
                    double normDblevelMin=1-(logLevelMin/baseLogLevel);
                    if(normDblevelMin<0)normDblevelMin=0;
                    double normDblevelMax=1-(logLevelMax/baseLogLevel);
                    if(normDblevelMax<0)normDblevelMax=0;
                    if(v.min[i]<0)normDblevelMin=-normDblevelMin;
                    if(v.max[i]<0)normDblevelMax=-normDblevelMax;
                    minLevel= normDblevelMin;
                    maxLevel= normDblevelMax;
                }
                minY[i] = (int) (((float) 0.5 - minLevel/2) * (float) imgHeight
                        + i * imgHeight + borderLength);
                maxY[i] = (int) (((float) 0.5 - maxLevel/2) * (float) imgHeight
                        + i * imgHeight + borderLength);
                if(paintPolygons){
                    // paint rendered values as polygons
                    // this works for all x-zoom values ( frame/pixel greater or
                    // less than 1)
                    if (firstValue) {
                        // We do not have the min/max values of the previous pixel
                        polyX[i][0] = pixelPos;
                        polyY[i][0] = minY[i];
                        polyX[i][1] = pixelPos;
                        polyY[i][1] = maxY[i];
                        polyX[i][2] = pixelPos;
                        polyY[i][2] = minY[i];
                        polyX[i][3] = pixelPos;
                        polyY[i][3] = maxY[i];

                    } else {
                        polyX[i][0] = polyX[i][2];
                        polyY[i][0] = polyY[i][2];
                        polyX[i][1] = polyX[i][3];
                        polyY[i][1] = polyY[i][3];
                        polyX[i][2] = pixelPos;
                        polyY[i][2] = minY[i];
                        polyX[i][3] = pixelPos;
                        polyY[i][3] = maxY[i];
                    }

                    Polygon p = new Polygon(polyX[i], polyY[i], 4);
                    g.drawPolygon(p);
                }else{
                    g.drawLine(pixelPos, minY[i], pixelPos, maxY[i]);
                }
            }
            firstValue = false;
            //                  // ignore values after paintTo
            //                  if (paintTo < pixelPos)
            //                      break;
        }
            
    }

    public RenderResult getRenderResult() {
        return renderResult;
    }

    public void setRenderResult(RenderResult renderResult) {
        this.renderResult = renderResult;
    }
}
