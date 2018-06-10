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
 * Gaussian window implementation.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class GaussianWindow implements Window{

	public static double DEFAULT_SIGMA=0.3;
	// Gaussian window function, 
	// http://reference.wolfram.com/language/ref/GaussianWindow.html
	// val=exp(-50*x*x/9) => sigma=0.3
	
    private double[] buf;
    public GaussianWindow(int size) {
    	this(size,DEFAULT_SIGMA);
    }
    public GaussianWindow(int size,double sigma) {
        buf=new double[size];
        double center=(size-1)/2;
        for (int i=0;i<size;i++){
            double quot=(i-center)/(sigma*center);
            double exp=-0.5*quot*quot;
            double val=Math.exp(exp);
            buf[i]=val;
        }
    }
    
    public double getScale(int i){
        return buf[i];
    }
    

}
