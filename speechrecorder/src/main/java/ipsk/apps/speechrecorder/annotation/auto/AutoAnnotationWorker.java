//    Speechrecorder
// 	  (c) Copyright 2013
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



package ipsk.apps.speechrecorder.annotation.auto;

import ips.annot.autoannotator.AutoAnnotation;

import ips.annot.autoannotator.AutoAnnotator;
import ips.annot.autoannotator.BundleAutoAnnotation;
import ips.annot.autoannotator.ParsedAutoAnnotation;
import ips.annot.model.db.Bundle;
import ipsk.awt.ProgressWorker;
import ipsk.awt.WorkerException;
import ipsk.awt.event.ProgressEvent;
import ipsk.util.ProgressStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author klausj
 *
 */
public class AutoAnnotationWorker extends ProgressWorker{

   public class BundleAnnotatedEvent extends ProgressEvent{
	   	/**
	 * @param arg0
	 * @param progressStatus
	 */
	public BundleAnnotatedEvent(Object arg0, Bundle bundle,ProgressStatus progressStatus) {
		super(arg0, progressStatus);
		annotatedBundle=bundle;
	}

		private Bundle annotatedBundle;

		/**
		 * @return the annotatedBundle
		 */
		public Bundle getAnnotatedBundle() {
			return annotatedBundle;
		}		
	   
   }
    
    private BlockingQueue<AutoAnnotator.AnnotationRequest> queue=new LinkedBlockingQueue<AutoAnnotator.AnnotationRequest>();
    
    
    private List<AutoAnnotator> autoAnnotators=new ArrayList<AutoAnnotator>();
    
  
    
    public AutoAnnotationWorker(){
        super("Auto annotation worker");
        
    }
    
    public void request(AutoAnnotator.AnnotationRequest annotationRequest){
        queue.add(annotationRequest);
    }
    
    protected void doWork() throws WorkerException{
        while(progressStatus.getStatus().isRunning()){
            synchronized (progressStatus) {
                if(State.CANCEL.equals(progressStatus.getStatus())){
                    progressStatus.canceled();
                    break;
                }
            }
            AutoAnnotator.AnnotationRequest ar;
            try {
                // wait 600 seconds for incoming request
                ar = queue.poll(600, TimeUnit.SECONDS);
                //            System.out.println("Status: "+progressStatus.getStatus());
                if(ar!=null){
                    Bundle bundle=ar.getBundle();
                    if(bundle!=null){
                        Exception exception=null; 
                        for(AutoAnnotator annotator:autoAnnotators){
                            try {
                                if(annotator.isBundleSupported(bundle)){
                                    annotator.setAnnotationRequest(ar);
                                    AutoAnnotation anno=annotator.call();
                                    if(anno!=null){

                                        if(anno instanceof BundleAutoAnnotation){
                                            Bundle resBundle=((BundleAutoAnnotation)anno).getBundle();
                                            if(resBundle!=null){
                                                bundle=resBundle;
                                            }
                                        }else if(anno instanceof ParsedAutoAnnotation){

                                            ParsedAutoAnnotation paa=(ParsedAutoAnnotation)anno;

                                            //                        		b.setName(ar.getTargetFilebasename());
                                            // TODO work on a copy ???
                                            bundle.getLevels().addAll(paa.getLevels());
                                            //                        		bundle.getLinks().addAll(paa.getLinks());
                                        }

                                        // Thread sync problem here: 
                                        // bundle is persisted in AWT Event thread
                                        // but the reference will nbe changed in this loop
                                        //                                    BundleAnnotatedEvent bae=new BundleAnnotatedEvent(this, bundle, progressStatus.clone());
                                        //                                    fireProgressEvent(bae);

                                    }

                                    //                        System.out.println("Anno: "+anno);
                                }
                            } catch (Exception e) {
                                // TODO log error
                                exception=e;
                                break;
                            }
                        }
//                        if(!progressStatus.hasCancelRequest()){
                        if(exception==null){
                        // This should be thread safe
                            BundleAnnotatedEvent bae=new BundleAnnotatedEvent(this, bundle, progressStatus.clone());
                            fireProgressEvent(bae);
                        }
//                        }

                    }    

                }
            } catch (InterruptedException e) {
                //System.out.println("Interrupted Status: "+progressStatus.getStatus());
                // OK
            }

        }
    }

    public List<AutoAnnotator> getAutoAnnotators() {
        return autoAnnotators;
    }
    
    
    
}
