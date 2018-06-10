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

/**
 * Hamming window implementation.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class HammingWindow implements Window{

    private double[] buf;
    public HammingWindow(int size) {
        buf=new double[size];
        for (int i=0;i<size;i++){
            int j=i-size/2;
            double arg=(j*2*Math.PI )/size;
            buf[i]=(0.54+0.46*Math.cos(arg));    
        }
    }
    
    public double getScale(int i){
        return buf[i];
    }
    

}
