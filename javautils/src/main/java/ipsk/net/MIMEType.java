//    IPS Java Utils
// 	  (c) Copyright 2016
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

package ipsk.net;

import ipsk.text.ParserException;

/**
 * @author klausj
 *
 */
public class MIMEType {
	
	public static final String TYPE_AUDIO = "audio";
	public static MIMEType TEXT_XML=new MIMEType("text", "xml","xml");
	public static MIMEType APPLICATION_XML=new MIMEType("application", "xml","xml");
	public static MIMEType APPLICATION_JSON=new MIMEType("application", "json","json");
	// TODO multiple wav MIME types to accept
	public static MIMEType AUDIO_WAVE=new MIMEType("audio", "wav","wav");
	
	private static MIMEType[] KNOWN_TYPES={TEXT_XML,APPLICATION_XML,APPLICATION_JSON,AUDIO_WAVE};
	
	private String type;
	private String subType;
	static String WILDCARD_TYPE="*";
	
	private String fileExtension=null;
	
	public String getFileExtension() {
		return fileExtension;
	}

	/**
	 * @param type
	 * @param subType
	 */
	public MIMEType(String type, String subType,String fileExtension) {
		super();
		if(type==null){
			throw new NullPointerException("Type cannot be null!");
		}
		this.type = type;
		this.subType = subType;
		this.fileExtension=fileExtension;
	}

	private static MIMEType knownType(String type,String subType){
		for(MIMEType knownType : KNOWN_TYPES){
		if(knownType.getType().equals(type) && knownType.getSubType().equals(subType)){
			return knownType;
		}
		}
		return null;
	}
	
	public static MIMEType parse(String mimeTypeString)throws ParserException{
		String[] splitStr=mimeTypeString.trim().split("\\s*/\\s*");
		if(splitStr==null ){
			throw new ParserException("Could not parse MIME type: "+mimeTypeString);
		}
		int splitStrCmps=splitStr.length;
		if(splitStrCmps<1 || splitStrCmps>2){
			throw new ParserException("Could not parse MIME type: "+mimeTypeString);
		}else{
			String type=splitStr[0];
			String subType=null;
			// sub type can be null
			if(splitStrCmps>1){
				subType=splitStr[1];
			}
			MIMEType knownType=knownType(type, subType);
			
			return (knownType!=null)?knownType: new MIMEType(type,subType,null);
		}
	}
	
	private boolean matchesType(String type,String otherType){
		return WILDCARD_TYPE.equals(type) || WILDCARD_TYPE.equals(otherType) || type.equals(otherType);
	}
	private boolean matchesSubType(String subType,String otherSubType){
		// sub type can be null as well
		return subType==null || otherSubType==null || matchesType(subType,otherSubType);
	}
	public boolean matches(MIMEType otherMimeType){
		return matchesType(type, otherMimeType.getType()) && matchesSubType(subType, otherMimeType.getSubType());
	}
	

	
	public boolean equals(Object other){
		if(other instanceof MIMEType){
			MIMEType otherMimeType=(MIMEType)other;
			boolean typeEq=type.equals(otherMimeType.getType());
			if(!typeEq){
				return false;
			}
			String otherSubType=otherMimeType.getSubType();
			// Note: * and */* are treated as NOT equal !
			if(subType==null){
				return(otherSubType==null);
			}else{
				return (subType.equals(otherSubType));
			}
		}
		return false;
	}
	
	public String getType() {
		return type;
	}

	public String getSubType() {
		return subType;
	}
	
	public String toString(){
		String s=type;
		if(subType!=null){
			s=s.concat("/"+subType);
		}
		return s;
	}

	private int precedenceValue(MIMEType mime){
		int val=0;
		String type=mime.getType();
		if(!WILDCARD_TYPE.equals(type)){
			val+=8;
		}
		String subType=mime.getSubType();
		if(subType!=null){
			val+=2;
			if(!WILDCARD_TYPE.equals(subType)){
				val+=4;
			}
		}
		return val;
	}
	
	public int precedenceCompare(MIMEType o) {
		return precedenceValue(this)-precedenceValue(o);		
	}
	
	
}
