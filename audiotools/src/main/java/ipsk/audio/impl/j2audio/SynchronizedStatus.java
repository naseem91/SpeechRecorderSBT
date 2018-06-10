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

package ipsk.audio.impl.j2audio;

/**
 * Helper class for synchronized state classes.
 * <p>
 * Inheriting classes should implement state strings.
 * </p>
 * <p>
 * e.g.:
 * </p>
 * <p>
 * <code>final static String IDLE="Idle";</code><br>
 * <code>final static String RUNNING="Running";</code><br>
 * ...
 * </p>
 * 
 * @author K.Jaensch
 *  
 */

public abstract class SynchronizedStatus {

	static protected boolean DEBUG = false;

	protected String status;

	public SynchronizedStatus() {
	}

	protected SynchronizedStatus(String status) {
		this.status = status;
	}

	/**
	 * Sets status and notifys waiting threads.
	 * 
	 * @param status
	 *            new status
	 */
	public synchronized void setStatus(String status) {
		this.status = status;
		this.notifyAll();
	}

	/**
	 * Gets current status.
	 * 
	 * @return current status
	 */
	public synchronized String getStatus() {
		return status;
	}

	/**
	 * Waits at least timeout ms for state waitStatus.
	 * 
	 * @param waitStatus
	 *            state to wait for
	 * @param timeout
	 *            timeout in ms
	 */
	public synchronized void waitFor(String waitStatus, int timeout) {
		if (!status.equals(waitStatus)) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Waits at least timeout ms for leaving the state waitStatus.
	 * 
	 * @param waitStatus
	 *            state to wait for leaving
	 * @param timeout
	 *            timeout in ms
	 */
	public synchronized void waitForNot(String waitStatus, int timeout) {
		if (status.equals(waitStatus)) {
			try {
				wait(timeout);
			} catch (InterruptedException e) {
			}
		}
	}

	/**
	 * Waits for state waitStatus.
	 * 
	 * @param status
	 *            state to wait for
	 */
	public synchronized void waitFor(String status) {
		int c = 0;
		while (!this.status.equals(status)) {
			try {
				if (DEBUG) {
					wait(1000);
				} else {
					wait();
				}

			} catch (InterruptedException e) {
			}
			if (DEBUG) {
				c++;
				if (c > 10) {
					System.err.println("MediaStatus hangs ! Status: "
							+ this.status + " Wait for: " + status);
					System.exit(1);
				}
			}
		}
	}

	/**
	 * Waits for leaving the state waitStatus.
	 * 
	 * @param status
	 *            state to wait for leaving
	 */
	public synchronized void waitForNot(String status) {
		int c = 0;
		while (this.status.equals(status)) {
			try {
				if (DEBUG) {
					wait(1000);
				} else {
					wait();
				}

			} catch (InterruptedException e) {
			}
			if (DEBUG) {
				c++;
				if (c > 10) {
					System.err.println("MediaStatus hangs ! Status: "
							+ this.status + " Wait for not: " + status);
				}
			}
		}
	}

	/**
	 * Returns the status.
	 * 
	 * @return current status
	 */
	public String toString() {
		return status;
	}
}
