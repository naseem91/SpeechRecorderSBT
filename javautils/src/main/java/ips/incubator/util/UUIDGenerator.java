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

package ips.incubator.util;

import ipsk.util.RadixConverters;

import java.lang.reflect.Array;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.UUID;

/**
 * @author klausj
 *
 */
public class UUIDGenerator {

	public UUID createUUID(byte[] macAddr){
		UUID rUUID=UUID.randomUUID();
		long lsbs=rUUID.getLeastSignificantBits();
		long msbs=rUUID.getMostSignificantBits();
//		byte[] macAddr=getHardwareAdress();
		long macAddrLong=0;
//		long node=rUUID.node();
//		System.out.println("Node: "+Long.toHexString(node));
		System.out.println("Version: "+rUUID.version());
//		System.out.println(macAddrL);
		return rUUID;
		
	}
	
	public byte[] getHardwareAdress() throws SocketException{
		Enumeration<NetworkInterface> nifs=NetworkInterface.getNetworkInterfaces();
		while(nifs.hasMoreElements()){
			NetworkInterface nif=nifs.nextElement();
			byte[] macAddr=nif.getHardwareAddress();
			String nifName=nif.getName();
			String macAddrHex=null;
			if(macAddr!=null){
				macAddrHex=RadixConverters.bytesToHex(macAddr);
				System.out.println(nifName+" "+nif.isLoopback()+" "+macAddrHex);
				return macAddr;
			}
		
		}
		return  null;
	}
	public static void main(String[] args){
		UUIDGenerator uuidGen=new UUIDGenerator();
		try {
			UUID uuid=uuidGen.createUUID(uuidGen.getHardwareAdress());
			System.out.println(uuid);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
