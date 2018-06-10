//    IPS Speech database tools
// 	  (c) Copyright 2013
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Speech database tools
//
//
//    IPS Speech database tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Speech database tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Speech database tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : Mar 4, 2013
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ips.annot.autoannotator;

import java.io.IOException;
import java.util.concurrent.Callable;

import ips.annot.model.db.Bundle;
import ipsk.util.services.ServiceDescriptorProvider;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public interface AutoAnnotator extends Callable<AutoAnnotation>,ServiceDescriptorProvider<AutoAnnotationServiceDescriptor>{
    public static class AnnotationRequest{
//        private File mediaFile;
        private Bundle bundle;
//        private String orthoGraphy;
//        private File orthoGraphyFile;
//        private Locale locale;
//        private File targetDirectory;
//        private String targetFilebasename;
        
        public AnnotationRequest(Bundle bundle) {
            super();
            this.bundle=bundle;
        }
        
//        public AnnotationRequest(File mediaFile, String orthoGraphy,Locale locale) {
//            super();
//            this.mediaFile = mediaFile;
//            this.orthoGraphy = orthoGraphy;
//            this.locale=locale;
//   
//        }
//        public AnnotationRequest(File mediaFile, File orthoGraphyFile,Locale locale) {
//            super();
//            this.mediaFile = mediaFile;
//            this.orthoGraphyFile = orthoGraphyFile;
//            this.locale=locale;
//        }
//        public AnnotationRequest(File mediaFile,String orthoGraphy,Locale locale,
//                File targetDirectory, String targetFilebasename) {
//            super();
//            this.mediaFile = mediaFile;
//            this.orthoGraphy = orthoGraphy;
//            this.locale=locale;
//            this.targetDirectory = targetDirectory;
//            this.targetFilebasename = targetFilebasename;
//           
//        }
//
//        public AnnotationRequest(File mediaFile, File orthoGraphyFile,Locale locale,
//                File targetDirectory, String targetFilebasename) {
//            super();
//            this.mediaFile = mediaFile;
//            this.orthoGraphyFile = orthoGraphyFile;
//            this.locale=locale;
//            this.targetDirectory = targetDirectory;
//            this.targetFilebasename = targetFilebasename;
//        }

      
     
//        public File getMediaFile() {
//            return mediaFile;
//        }
//        public String getOrthoGraphy() {
//            return orthoGraphy;
//        }
//        public File getOrthoGraphyFile() {
//            return orthoGraphyFile;
//        }
//        public File getTargetDirectory() {
//            return targetDirectory;
//        }
//        public String getTargetFilebasename() {
//            return targetFilebasename;
//        }
//        public Locale getLocale() {
//            return locale;
//        }

        public Bundle getBundle() {
            return bundle;
        }
   
    }
    public boolean isBundleSupported(Bundle bundle) throws IOException;

    public boolean needsWorker();
    public void open();
    public void setAnnotationRequest(AnnotationRequest annotationRequest);
    public void close();
}
