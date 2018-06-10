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

package ipsk.math;

import ipsk.math.Complex;

/**
 * Processor for DFT transformations.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

public class DFTProcessor {

    private int size;
    private int channels;
    //private HammingWindow hw;
    private Window w;

    /**
     * Create DFT processor
     * @param window window function
     * @param size DFT size
     * @param channels channel count
     */
    public DFTProcessor(int size,int channels,Window window) {
        this.size = size;
        this.channels=channels;
        w=window;
    }
    /**
     * Create DFT processor using Hamming window function
    * @param size DFT size
     * @param channels channel count
     */
    public DFTProcessor(int size,int channels) {
        this.size = size;
        this.channels=channels;
        w=new HammingWindow(size);
    }
    
    public DFTProcessor(int size) {
        this(size,1);
    }
    
    public void processFrame(double[][] srcBuf, int srcOff, Complex[][][] dstBuf,int dstOff,int computeTo) {

        
       
        for (int f = 0; f < channels; f++) {
            for (int m = 0; m < size; m++) {

                Complex cx = new Complex();

                double real = 0.0;
                double img = 0.0;

                if (m == 0) {

                    for (int n = 0; n < size; n++) {

                        real = real + w.getScale(n)*srcBuf[srcOff+n][f];
                    }
                } else {
                    double x;
                    double arg;

                    double argM = Math.PI * 2 * m;
                    for (int n = 0; n < size; n++) {

                        x = w.getScale(n)*srcBuf[srcOff+n][f];
                        arg = (argM * n) / size;
                        real = real + x * Math.cos(arg);
                        img = img - x * Math.sin(arg);
                    }
                }
                cx.real = real;

                cx.img =  img;

                dstBuf[dstOff][f][m] = cx;
            }
        }
    }
    
    
  

    
    
    public void process(double[] srcBuf, int srcOff, int len, Complex[] dstBuf,
            int dstOff) {

        if (len % size != 0)
            throw new IllegalArgumentException(
                    "Values to process must be multiple of frame size");
        int frames = len / size;
        if (dstBuf == null) {
            dstBuf = new Complex[len];
        } else {
            if (dstBuf.length < (dstOff + len))
                throw new IllegalArgumentException(
                        "Destination buffer to small");
        }
        for (int f = 0; f < frames; f++) {
            for (int m = 0; m < size; m++) {

                Complex cx = new Complex();

                double real = 0.0;
                double img = 0.0;

                if (m == 0) {

                    for (int n = 0; n < size; n++) {

                        real = real + srcBuf[n + f * size];
                    }
                } else {
                    double x;
                    double arg;

                    double argM = Math.PI * 2 * m;
                    for (int n = 0; n < size; n++) {

                        x = srcBuf[n + f * size];
                        arg = (argM * n) / size;
                        real = real + x * Math.cos(arg);
                        img = img - x * Math.sin(arg);
                    }
                }
                cx.real = real;

                cx.img = img;

                dstBuf[m] = cx;
            }
        }
    }
}
