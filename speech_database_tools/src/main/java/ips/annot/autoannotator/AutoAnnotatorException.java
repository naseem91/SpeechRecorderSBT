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

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class AutoAnnotatorException extends Exception {

    public AutoAnnotatorException() {
       super();
    }

    public AutoAnnotatorException(String message) {
        super(message);
    }

    public AutoAnnotatorException(Throwable cause) {
        super(cause);
    }

    public AutoAnnotatorException(String message, Throwable cause) {
        super(message, cause);
    }

}
