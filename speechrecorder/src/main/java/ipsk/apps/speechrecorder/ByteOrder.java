//    Speechrecorder
//    (c) Copyright 2009-2011
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

package ipsk.apps.speechrecorder;
public abstract class ByteOrder {

	public static String bytes2HexString(byte [] inBytes, int from, int to) {
		StringBuffer sb = new StringBuffer("");
		String tmpHexString;
//		int tmp;
		for(int i = from; i < to; i++) {
			tmpHexString = Integer.toHexString(inBytes[i]);
			if (tmpHexString.length() < 2) {
				sb.append("0" + tmpHexString);
			} else if (tmpHexString.length() > 2) {
				int l = tmpHexString.length();
				sb.append(tmpHexString.substring(l-2,l));
			} else {
				sb.append(tmpHexString);
			}
		}
            return sb.toString();
	}
	
        public static int bytes2int(byte [] inBytes, int from, int to) {
            int x = 0;
            for(int i = to - 1 ; i > from; i--) {
                x = x | (0xFF & (int) inBytes[i]);
                x = x << 8;
            }
            x = x | (0xFF & (int) inBytes[from]);
            return (int) x;
        }
    
	public static String bytes2String(byte [] inBytes, int from, int to) {
            StringBuffer sb = new StringBuffer("");		
            for(int i = from; i < to; i++) {
                sb.append((char) inBytes[i]);
            }
            return sb.toString();
	}

	public static String reverseHexCode(String inString) {
//		int j;
		StringBuffer isb = new StringBuffer(inString);
		if (isb.length() % 2 != 0) {
			isb.insert(0,'0');
		}
		StringBuffer sb = new StringBuffer("");
		
		for (int i = isb.length() - 1; i > 0; i = i - 2) {
			sb.append("" + isb.charAt(i - 1) + isb.charAt(i));
		}
		return sb.toString();
	}
	

	public static byte [] int2RevBytes(int inValue) {
		byte [] revBytes = new byte[4];
		revBytes[3] = (byte) ((inValue >> 24) & 0x000000FF);
		revBytes[2] = (byte) ((inValue >> 16) & 0x000000FF);
		revBytes[1] = (byte) ((inValue >> 8) & 0x000000FF);
		revBytes[0] = (byte) (inValue & 0x000000FF);
		return revBytes;
	}

	public static byte [] short2RevBytes(short inValue) {
		byte [] revBytes = new byte[2];
		revBytes[1] = (byte) ((inValue >> 8) & 0x000000FF);
		revBytes[0] = (byte) (inValue & 0x000000FF);
		return revBytes;
	}

	public static void main(String [] args) {
		//ByteOrder bo = new ByteOrder();
		int x = Integer.parseInt(args[0]);
		
		String lowEndian = Integer.toHexString(x);
		String highEndian = ByteOrder.reverseHexCode(lowEndian);
		int y = Integer.parseInt(highEndian,16);
		
		System.out.println("x, lowEndian: " + x + ", " + lowEndian);
		System.out.println("y, highEndian: " + y + ", " + highEndian);
	}
}