//    IPS Java Audio Tools
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
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

package ipsk.audio.dsp;

public class DSPUtils {

//	private static double LN_FACTOR=20/Math.log(10);
    
    private static double OCTAVE_FACTOR=1.0/Math.log10(2);
    /**
     * Get normalized amplitude level in dB
     * @param linearLevel a normalized positive linear level (1.0 corresponds to 0dB) 
     * @return level in dB
     */
    public static double toLevelInDB(double linearLevel){
        if(linearLevel<0)throw new IllegalArgumentException("Linear level argument must be positive.");
        return 10 * Math.log10(linearLevel);
    }
    
    public static double getLevelInDB(double linearLevel){
    	return toLevelInDB(linearLevel);
    }
    
    public static double toLinearLevel(double dbLevel){
        return Math.pow(10, (dbLevel/10));
    }
	/**
     * Get normalized amplitude power level in dB
     * @param linearLevel a normalized positive linear level (1.0 corresponds to 0dB) 
     * @return power level in dB
     */
    public static double toPowerLevelInDB(double linearLevel){
        if(linearLevel<0)throw new IllegalArgumentException("Linear level argument must be positive.");
        return 20 * Math.log10(linearLevel);
    }
    
	public static double toPowerLinearLevel(double dbLevel){
        return Math.pow(10, (dbLevel/20));
    }
	
	
	public static double toOctaves(double f1,double f2){
	    return toOctaves(f1/f2);
	}
	
	public static double toOctaves(double fq){
        return OCTAVE_FACTOR*Math.log10(fq);
    }
	
	public static void main(String[] args){	
		System.out.println(DSPUtils.toPowerLevelInDB(0.5));
		System.out.println(DSPUtils.toPowerLevelInDB(1.0));
		System.out.println(DSPUtils.toPowerLinearLevel(50.0));
		System.out.println(DSPUtils.toPowerLinearLevel(6.0));
	}

}
