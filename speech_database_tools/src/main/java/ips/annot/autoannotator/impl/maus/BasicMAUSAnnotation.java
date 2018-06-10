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
 * Date  : 19.03.2014
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ips.annot.autoannotator.impl.maus;

import java.util.ArrayList;
import java.util.List;

import ips.annot.autoannotator.AutoAnnotation;
import ips.annot.autoannotator.ParsedAutoAnnotation;
import ips.annot.model.db.Level;
import ips.annot.model.db.Link;
import ipsk.io.ByteArrayFileContent;
import ipsk.io.FileContent;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class BasicMAUSAnnotation implements AutoAnnotation,ParsedAutoAnnotation {

    private ByteArrayFileContent baFc;
    private List<FileContent> fileContents;
    private List<Level> levels;
    public static final String TEXTGRID_EXTENSION="TextGrid";
    public static final String BPF_EXTENSION="bpf";
    
    public BasicMAUSAnnotation(byte[]  textGridData,List<Level> levels) {
       super();
       baFc=new ByteArrayFileContent(textGridData,"text/x-text-grid", null, null,TEXTGRID_EXTENSION);
       fileContents=new ArrayList<FileContent>();
       fileContents.add(baFc);
      this.levels=levels;
    }
    
   
    @Override
    public List<Level> getLevels() {
       return levels;
    }

    @Override
    public List<Link> getLinks() {
        return null;
    }



}
