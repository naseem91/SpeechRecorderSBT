//    Speechrecorder
//    (c) Copyright 2012
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

/*
 * Date  : Oct 21, 2008
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ipsk.apps.speechrecorder.script.ui;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class StringSel {
	private String displayName;
	private String string;
    private boolean ignoreCase=false;
    
    public StringSel(String str,String displayName,boolean ignoreCase){
        this.string = str;
        this.displayName = displayName;
        this.ignoreCase=ignoreCase;
    }
	public StringSel(String str,String displayName){
		this(str,displayName,false);
	}
	public StringSel(String mode) {
		this(mode,mode);
	}

	public String toString() {
		return displayName;
	}

	public String getString() {
		return string;
	}

	public boolean equals(Object o) {
		if (o != null) {
			if (o instanceof StringSel) {
				StringSel mo = (StringSel) o;
				if (string == null) {
					if (mo.getString() == null)
						return true;
				} else {
                    if(ignoreCase){
                        if (string.equalsIgnoreCase(mo.getString())) {
                            return true;
                        }
                    }else{
					if (string.equals(mo.getString())) {
						return true;
					}
                    }
				}
			}
		}
		return false;
	}
}