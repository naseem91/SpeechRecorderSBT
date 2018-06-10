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

package ips.dsp;

import ipsk.math.Complex;
import ipsk.math.DFT;
import ipsk.math.HammingWindow;
import ipsk.math.Window;

/**
 * @author klausj
 *
 */
public class FIRFilterBuilder {

    public static double[] buildCoefficientsFromImpulsResponse(Complex[] impulseResponse){
        
        Complex[] symImpulseResponse=new Complex[impulseResponse.length*2];
     // mirror
        for(int i=0;i<impulseResponse.length;i++){
            symImpulseResponse[i]=impulseResponse[i];
            symImpulseResponse[impulseResponse.length+i]=impulseResponse[impulseResponse.length-1-i];
        }
       
        Complex[] coeffComplex=DFT.processInvers(symImpulseResponse);
        double[] coeff=new double[coeffComplex.length];
       
        for(int i=0;i<coeffComplex.length/2;i++){
            double c=coeffComplex[coeffComplex.length/2+i].real;
            //double c=coeffComplex[i].real;
            coeff[i]=c;
            
        }
        for(int i=0;i<coeffComplex.length/2;i++){
           
            double c2=coeffComplex[i].real;
            //double c=coeffComplex[i].real;
            coeff[coeffComplex.length/2+i]=c2;
        }
        
       
        HammingWindow hw=new HammingWindow(coeff.length);
      for(int i=0;i<coeff.length;i++){
            double scale=hw.getScale(i);
            coeff[i]=coeff[i]*scale;
        }
        return coeff;
       
        
    }
    
    
}
