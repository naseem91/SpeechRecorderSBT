//    IPS Speech database tools
// 	  (c) Copyright 2015
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Speech database tools
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
//    along with IPS Speech database tools.  If not, see <http://www.gnu.org/licenses/>.

/*
 * Date  : 01.07.2015
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ips.annot;

import java.io.IOException;

import ips.annot.model.db.Bundle;
import ipsk.text.EncodeException;
import ipsk.text.ParserException;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public interface BundleAnnotationPersistor extends BundleAnnotationPersistorServiceDescriptor{

    public boolean isLossless();
    public void write(Bundle bundle) throws IOException, EncodeException;
    public Bundle load() throws IOException,ParserException;
    public Bundle load(Bundle bundle) throws IOException, ParserException;
}
