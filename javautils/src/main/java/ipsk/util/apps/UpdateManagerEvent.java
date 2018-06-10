//    IPS Java Utils
// 	  (c) Copyright 2012
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

package ipsk.util.apps;

import java.util.EventObject;

/**
 * @author klausj
 *
 */
public class UpdateManagerEvent extends EventObject {

   
	private UpdateManager.Status status;
    /**
	 * @return the status
	 */
	public UpdateManager.Status getStatus() {
		return status;
	}

	/**
     * @param source
     */
    public UpdateManagerEvent(Object source,UpdateManager.Status status) {
        super(source);
        this.status=status;
    }
    
    public String toString(){
        return "Update manager event";
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }

}
