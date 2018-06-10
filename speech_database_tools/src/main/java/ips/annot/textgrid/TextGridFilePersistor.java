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
 
package ips.annot.textgrid;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import ips.annot.io.BundleAnnotationFilePersistor;
import ips.annot.model.db.Bundle;
import ips.annot.model.db.Level;
import ips.annot.model.db.LevelDefinition;
import ipsk.audio.ajs.AJSAudioSystem;
import ipsk.text.EncodeException;
import ipsk.text.ParserException;
import ipsk.text.Version;
import ipsk.util.LocalizableMessage;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class TextGridFilePersistor implements
        BundleAnnotationFilePersistor {

    // Fixed UTF-8 charset
    public final static String DEFAULT_CHARSET_NAME="UTF-8";
    private File file;
    
    
    private LevelDefinition levelDefinition;
    private Charset charset;
    private TextGridFileParser parser;
    
    public TextGridFilePersistor() {
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
    
    private float deriveSampleRate(Bundle bundle) throws EncodeException, IOException, UnsupportedAudioFileException{
    	Float bSr=bundle.getSampleRate();
    	if(bSr!=null){
    		// Already set
    		return bSr;
    	}
    	List<String> sps=bundle.getSignalpaths();
    	if(sps==null || sps.size()==0){
    		throw new EncodeException("Bundle has no audio file! Cannot determine sample rate for TextGrid.");
    	}
    	String sp=sps.get(0);
    	File sf=new File(sp);
    	if(!sf.exists()){
    		throw new EncodeException("Bundle audio file "+sf+" does not exist.");
    	}
    	AudioFileFormat  aff=AJSAudioSystem.getAudioFileFormat(sf);
    	return aff.getFormat().getSampleRate();

    }

    @Override
    public void write(Bundle bundle) throws IOException, EncodeException {
        List<Level> lvls=bundle.getLevels();
        if(lvls==null || lvls.size()==0){
        	throw new EncodeException("Praat TextGrid file must have at least one level (tier)");
        }
        float sr;
        try {
            sr = deriveSampleRate(bundle);
        } catch (UnsupportedAudioFileException e) {
            throw new EncodeException(e);
        }
        FileOutputStream fos=new FileOutputStream(file);
        OutputStreamWriter wr=new OutputStreamWriter(fos, charset);
        try {
           
            TextGridFileParser parser=new TextGridFileParser(sr);
            parser.write(bundle, wr);
        }catch(IOException ioe){
            throw ioe;
        }finally{
            wr.close();
        }


    }


    @Override
    public String getPreferredFileExtension() {
        return "TextGrid";
    }

    @Override
    public Bundle load() throws IOException, ParserException {

        throw new ParserException("TextGrid parser requires existing bundle to get samplerate from signal file. Use load(Bundle) method instead.");
    }
    
    @Override
    public Bundle load(Bundle bundle) throws IOException, ParserException {
        float sampleRate;
        try {
            sampleRate = deriveSampleRate(bundle);
        } catch (EncodeException e) {
            // TODO use different exception types !!!
            throw new ParserException(e);
        } catch (UnsupportedAudioFileException e) {
            throw new ParserException(e);
        }

       
        TextGridFileParser parser=new TextGridFileParser(sampleRate);
        Bundle b=parser.parse(file, charset);
        bundle.getLevels().addAll(b.getLevels());
        return bundle;
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
       return new LocalizableMessage("TextGrid loader/writer");
    }

    @Override
    public LocalizableMessage getDescription() {
       return new LocalizableMessage("Loader/writer for Praat TextGrid annotation files.");
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
    public String getPreferredFilenameSuffix() {
        return "";
    }

    public String[] getLinks() {
        return new String[]{"http://www.praat.org"};
    }

}
