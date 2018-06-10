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

package ipsk.net.event;

/**
 * Event generated on connection status changes.
 * @author Klaus Jaensch
 *
 */
public class UploadConnectionEvent extends UploadEvent {

	public enum ConnectionState {DISCONNECTED,TRY_CONNECT,CONNECTED};
	private ConnectionState connectionState;
	public UploadConnectionEvent(Object source,ConnectionState connectionState) {
		super(source);
		this.connectionState=connectionState;
	}
	public ConnectionState getConnectionState() {
		return connectionState;
	}

}
