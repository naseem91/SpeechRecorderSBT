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

package ipsk.awt.test;

import ipsk.awt.ProgressWorker;
import ipsk.awt.event.ProgressEvent;

public class DemoWorker extends ProgressWorker {
	public DemoWorker(){
		super("DemoWorker");
		}
	




	public void doWork() {
		for(int i=1;i<=100;i++){
		try {
			Thread.sleep(100);
			progressStatus.setProgress(i);
			fireProgressEvent();
			if(progressStatus.hasCancelRequest())break;
			System.out.println(i);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		}
	}


}
