//    IPS Java Utils
// 	  (c) Copyright 2009
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

package ipsk.math.random;

import ipsk.io.FloatStream;
import ipsk.math.Complex;

/**
 * Generator for Gaussion (normal) distributed random values. 
 * References:
 * http://www.dspguru.com/dsp/howtos/how-to-generate-white-gaussian-noise
 * [Jer92] Simulation of Communication Systems by M. C. Jeruchim, P. Balaban and K. S. Shanmugan
 * [Ros88] A First Course on Probability by S. M. Ross
 * 
 * @author klausj
 *
 */
public class GaussianDistributionRandomGenerator implements FloatStream{

    public static final double DEFAULT_MEAN=0.0;
    public static final double DEFAULT_VARIANCE=1.0;
	private double mean;
	private double variance;
	private long pos=0;
	private Long length=null;
	private boolean closed=false;
	
	public GaussianDistributionRandomGenerator(){
		this(DEFAULT_MEAN,DEFAULT_VARIANCE);
	}
	public GaussianDistributionRandomGenerator(double mean, double variance){
		super();
		this.mean=mean;
		this.variance=variance;
	}
	
	public GaussianDistributionRandomGenerator(double mean, double variance,long length){
        super();
        this.mean=mean;
        this.variance=variance;
        this.length=length;
    }
	
	/**
     * @param frameLength length in frames
     */
    public GaussianDistributionRandomGenerator(long frameLength) {
       this(DEFAULT_MEAN,DEFAULT_VARIANCE,frameLength);
    }
    /**
	 * Generates gaussion distributed random value.
	 * Based on Math.random() method.
	 * 
	 * @return  gaussion distributed random value
	 */
	public Complex generateGaussionDistributedValues(){
		
		// polar method to generate normal distributed value
		double u1;
		double u2;
		double q;
		double x;
		double y;
		do{
		      u1=2*Math.random()-1.0;
		      u2=2*Math.random()-1.0;
//		      System.out.println("U1: "+u1+" U2: "+u2);
		      q=u1*u1+u2*u2;
		} while( q >=1);
		
		   x=Math.sqrt((-2.0 * Math.log(q)) / q) * u1;
		   y=Math.sqrt((-2.0 * Math.log(q)) / q) * u2;
		   double xmv = mean + Math.sqrt(variance) * x;
		  double ymv = mean + Math.sqrt(variance) * y ;
		return new Complex(xmv,  ymv);
	}
	
	/**
	 * Generates gaussian distributed random value.
	 * Based on Math.random() method.
	 * 
	 */
	public void fillWithGaussionDistributedValues(double[] buf,int off,int len){
		Complex doubleRandomValue;
		int i=0;
		for( i=0;i<len/2;i++){
			doubleRandomValue=generateGaussionDistributedValues();
			int j=i*2;
			buf[off+j]=doubleRandomValue.real;
			buf[off+j+1]=doubleRandomValue.img;
		}
		if(len % 2 >0){
			doubleRandomValue=generateGaussionDistributedValues();
			buf[off+len-1]=doubleRandomValue.real;
		}
		
	}
	
    /* (non-Javadoc)
     * @see ipsk.io.FloatStream#read(double[], int, int)
     */
    public int read(double[] buf, int offset, int len) {
       
        if(length!=null){
            if(pos>=length){
                return -1;
            }
            if(pos+len>length){
                len=(int)(length-pos);
            }
        }
       
        fillWithGaussionDistributedValues(buf, offset, len);
        pos+=len;
        return len;
    }
    
   
    /* (non-Javadoc)
     * @see ipsk.io.FloatStream#skip(long)
     */
    public long skip(long skip) {
        // random values nothing to skip
        return skip;
    }
    /* (non-Javadoc)
     * @see ipsk.io.FloatStream#close()
     */
    public void close() {
       closed=true;
    }
    
    /**
     * Test method.
     * @param args
     */
    public static void main(String[] args) {
        GaussianDistributionRandomGenerator gdrg=new GaussianDistributionRandomGenerator();
        double sum=0;
            for(int i=1;i<100000;i++){
                double v=gdrg.generateGaussionDistributedValues().real;
                sum+=v;
                double mean=sum/i;
                System.out.println("Value: "+v+" Mean: "+mean);
            }

    }
//    /* (non-Javadoc)
//     * @see ipsk.io.InterleavedFloatStream#read(double[][], int, int)
//     */
//    public int read(double[][] buf, int offset, int len) throws IOException {
//        if(length!=null){
//            if(pos>=length){
//                return -1;
//            }
//            if(pos+len>length){
//                len=(int)(length-pos);
//            }
//        }
//        for(int i=0;i<len;i++){
//            double[] chBuf=buf[offset+i];
//            int channels=chBuf.length;
//            for(int ch=0;ch<chBuf.length;ch++){
//                fillWithGaussionDistributedValues(chBuf, 0, channels);
//            }
//        }
//        pos+=len;
//        return len;
//    }
//   
}
