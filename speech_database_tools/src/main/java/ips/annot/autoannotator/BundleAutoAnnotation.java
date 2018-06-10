//    IPS Speech database tools
// 	  (c) Copyright 2016
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
 * Date  : 30.10.2015
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ips.annot.autoannotator;

import ips.annot.model.db.Bundle;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class BundleAutoAnnotation implements AutoAnnotation {

    private Bundle bundle;
    public Bundle getBundle() {
        return bundle;
    }
    public BundleAutoAnnotation(Bundle bundle) {
        this.bundle=bundle;
    }
    public String toString(){
      
        if(bundle==null){
            return "Empty bundle auto annoation";
        }else{
            return "Bundle auto annotation:\n"+bundle;
        }
    }

}
