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
 * Date  : 01.07.2015
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ips.annot.text;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ips.annot.io.BundleAnnotationFilePersistor;
import ips.annot.model.db.Bundle;
import ips.annot.model.db.Item;
import ips.annot.model.db.Level;
import ips.annot.model.db.LevelDefinition;
import ipsk.io.StreamCopy;
import ipsk.text.EncodeException;
import ipsk.text.ParserException;
import ipsk.text.Version;
import ipsk.util.LocalizableMessage;

/**
 * Bundle parser/writer for simple text file annotations.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class SingleLevelTextFilePersistor implements
        BundleAnnotationFilePersistor {

    // Fixed UTF-8 charset
    public final static String DEFAULT_CHARSET_NAME="UTF-8";
    private File file;
    
    
    private LevelDefinition levelDefinition;
    private Charset charset;
    
    public SingleLevelTextFilePersistor() {
        super();
        charset=Charset.forName(DEFAULT_CHARSET_NAME);
    }

    @Override
    public boolean isLossless() {
        return false;
    }

  
    @Override
    public File getFile() {
        return file;
    }

    @Override
    public void setFile(File file) {
      this.file=file;

    }

    @Override
    public void write(Bundle bundle) throws IOException, EncodeException {

        String line=null;
        if(levelDefinition!=null){
            String levelName=levelDefinition.getName();
            Level tier=bundle.getTierByName(levelName);
            if(tier!=null){
                List<Item> items=tier.getItems();
                int itsCnt=items.size();
                if(itsCnt>0){
                    if(items.size()>1){
                        throw new EncodeException("Unable to encode multiple items to one text line");
                    }
                    Item it=items.get(0);
                    if(it!=null){
                        //                line=it.getLabelText();
                        Map<String,Object> lblMaps=it.getLabels();
                        int lblCnt=lblMaps.size();
                        if(lblCnt>0){
                            if(lblCnt>1){
                                throw new EncodeException("Unable to encode multiple attributes to one text line");
                            }
                            if(!lblMaps.containsKey(levelName)){
                                throw new EncodeException("Encoding failed: attribute name does not match level definition name");
                            }
                            Object lblVal=lblMaps.get(levelName);
                            if(lblVal!=null){
                                if(lblVal instanceof String){
                                    line=(String)lblVal;
                                }else{
                                    throw new EncodeException("Encoding failed: Only text can be encoded! (Value is of Java type: '"+lblVal.getClass().getName()+"')");
                                }
                            }
                        }

                    }
                }
            }
        }
        if(line!=null){
            FileOutputStream fos=new FileOutputStream(file);
            OutputStreamWriter wr=new OutputStreamWriter(fos, charset);

            try{
                wr.write(line);
            }catch(IOException ioe){
                throw ioe;
            }finally{
                wr.close();
            }
        }
    }


    @Override
    public String getPreferredFileExtension() {
        return "txt";
    }

    @Override
    public Bundle load() throws IOException, ParserException {


       String annoText= StreamCopy.readTextFile(file, charset);

        Bundle b=new Bundle();
        Level t=new Level();
        t.setDefinition(levelDefinition);
        t.setBundle(b);
        Item it=new Item();
        it.setLabel(levelDefinition.getName(), annoText);
        List<Item> its=new ArrayList<Item>();
        its.add(it);
        t.setItems(its);
        b.getLevels().add(t);
        return b;
    }

 

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public LevelDefinition getLevelDefinition() {
        return levelDefinition;
    }

    public void setLevelDefinition(LevelDefinition levelDefinition) {
        this.levelDefinition = levelDefinition;
    }
    
    public void setLevelDefinitionKeyName(String levelDefinitionKeyName) {
        this.levelDefinition = new LevelDefinition();
        this.levelDefinition.setName(levelDefinitionKeyName);
    }

    @Override
    public String getServiceImplementationClassname() {
        return getClass().getName();
    }

    @Override
    public LocalizableMessage getTitle() {
       return new LocalizableMessage("Single text line loader/writer");
    }

    @Override
    public LocalizableMessage getDescription() {
       return new LocalizableMessage("Simple text loader writer.");
    }

    @Override
    public String getVendor() {
       return "Institut of Phonetics and Speech Processing";
    }

    @Override
    public Version getSpecificationVersion() {
        return new Version(new int[]{0,0,1});
    }

    @Override
    public Version getImplementationVersion() {
        return new Version(new int[]{0,0,1});
    }

    @Override
    public Bundle load(Bundle bundle) throws IOException, ParserException {
        return load();
    }

    @Override
    public String getPreferredFilenameSuffix() {
        return null;
    }

}
