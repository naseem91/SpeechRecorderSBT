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

package ipsk.awt;

import ipsk.util.ProgressStatus;

/**
 * Interface for worker classes.
 * @author klausj
 *
 */
public interface Worker {
	public static enum State {
		INIT(false,false,false),
		OPEN(true,false,false),
		STARTED(true,true,false),
		RUNNING(true,true,true),
		CANCEL(true,true,true),
		CANCELLED(true,false,false),
		DONE(true,false,false),
		ERROR(true,false,false),
		CLOSED(false,false,false);
	
		private boolean open;
		private boolean active;
		private boolean running;
		
		State(boolean open,boolean active,boolean running){
			this.open=open;
			this.active=active;
			this.running=running;
		}
		public boolean isOpen() {
			return open;
		}
		public boolean isRunning() {
			return running;
		}
		public boolean isActive() {
			return active;
		}
		
		
	} 

	public void open() throws WorkerException;
	public void start();
	public ProgressStatus getProgressStatus();
	public void cancel();
	public void close() throws WorkerException;
	public void addProgressListener(ProgressListener progressListener);
	public void removeProgressListener(ProgressListener progressListener);
}
