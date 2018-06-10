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
 * Date  : 19.10.2015
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ips.annot.model.emu;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

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
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class EmuBundleAnnotationPersistor implements BundleAnnotationFilePersistor{

    private File file;
    private static volatile JAXBContext jaxbContext;
    private static volatile Charset cs;
    private static String CS_NAME="UTF-8";
    public EmuBundleAnnotationPersistor() {
        super(); 
    }
    
    private synchronized JAXBContext jaxbContext() throws JAXBException{
        if(jaxbContext==null){
            cs=Charset.forName(CS_NAME);
        HashMap jaxbEclipseLinkPropsmap=new HashMap();
        jaxbEclipseLinkPropsmap.put("javax.xml.bind.context.factory","org.eclipse.persistence.jaxb.JAXBContextFactory");
//        JAXBContext jc = JAXBContext.newInstance(Bundle.class);
        jaxbContext=org.eclipse.persistence.jaxb.JAXBContextFactory.createContext(new Class[]{Bundle.class},jaxbEclipseLinkPropsmap);
        
        }
        return jaxbContext;
    }
    

    @Override
    public String getServiceImplementationClassname() {
        return getClass().getName();
    }

    
    @Override
    public LocalizableMessage getTitle() {
       return new LocalizableMessage("EMU DB annotation loader/writer");
    }

    @Override
    public LocalizableMessage getDescription() {
       return new LocalizableMessage("Imports/exports bundle annotations in EMU DB JSON format.");
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
    public boolean isLossless() {
        // Emu JSON is our reference format 
        return true;
    }
    
    
   
    private void applyItemIds(Bundle bundle){
       
        // determine highest ID
        Integer hstId=bundle.highestID();
        int hId=-1;
        if(hstId!=null){
            hId=hstId;
        }
        List<Level> lvls=bundle.getLevels();
        for(Level lvl :lvls){
            List<Item> its=lvl.getItems();
            for(Item it:its){
                Integer itemId=it.getBundleId();
                if(itemId==null){
                    hId++;
                    it.setBundleId(hId);
                }
            }
        }
        return;
    }

    @Override
    public void write(Bundle bundle) throws IOException, EncodeException {
        applyItemIds(bundle);
        Marshaller marshaller;
        try {
           
            //        Unmarshaller unmarshaller = jc.createUnmarshaller();
            //        unmarshaller.setProperty("eclipselink.media-type", "application/json");
            //        StreamSource source = new StreamSource("http://search.twitter.com/search.json?q=jaxb");
            //        JAXBElement<SearchResults> jaxbElement = unmarshaller.unmarshal(source, SearchResults.class);
            //        Result result = new Result();
            //        result.setCreatedAt(new Date());
            //        result.setFromUser("bsmith");
            //        result.setText("You can now use EclipseLink JAXB (MOXy) with JSON :)");
            //        jaxbElement.getValue().getResults().add(result);
            JAXBContext jc=jaxbContext();
            marshaller = jc.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty("eclipselink.media-type", "application/json");
//            unmarshaller.setProperty("eclipselink.json.include-root", false);
        } catch (JAXBException e) {
            throw new EncodeException(e);
        }
        PrintWriter ow=new PrintWriter(file,CS_NAME);
        try{
        	marshaller.marshal(bundle,ow);
        	
        } catch (JAXBException e) {
        	throw new EncodeException(e);
        }finally{
        	ow.close();
        }
    }

    @Override
    public Bundle load() throws IOException, ParserException {
    	Unmarshaller unmarshaller;
    	Bundle b=null;
    	try {
    		JAXBContext jc=jaxbContext();
    		unmarshaller = jc.createUnmarshaller();
    		unmarshaller.setProperty("eclipselink.media-type", "application/json");
    		unmarshaller.setProperty("eclipselink.json.include-root", false);
    	 } catch (JAXBException e) {
             throw new ParserException(e);
         }
    		FileInputStream fis=new FileInputStream(file);
    		InputStreamReader isr=new InputStreamReader(fis,cs);

    		StreamSource json = new StreamSource(isr);
    	try{
    		JAXBElement<Bundle> jb= unmarshaller.unmarshal(json,Bundle.class);
    		b=jb.getValue();
    		if(b!=null){
    		    b.applyBundleToLevels();
    		    b.applyLevelsToItems();
    		    b.applyItemPositions();
    			b.resolveLinkReferences();
    		}
    	} catch (JAXBException e) {
    		throw new ParserException(e);
    	}finally{
    		if(isr!=null){
    			isr.close();
    		}
    	}
    	return b;

    }

    @Override
    public Bundle load(Bundle bundle) throws IOException, ParserException {
       
        return load();
    }
    
    public static void main(String[] args){
        Bundle b= new Bundle();
        b.setId(5);
        b.setName("foo");
        b.setAnnotates("foo.wav");
        LevelDefinition ld=new LevelDefinition();
        ld.setName("Phonetic");
        Level l=new Level();
        l.setDefinition(ld);
        Item it1=new Item();
        
        it1.setLevel(l);
        it1.setBundleId(456);
        it1.setLabel("Phonetic", "Label foo");
        l.getItems().add(it1);
        b.getLevels().add(l);
        
        
        
        EmuBundleAnnotationPersistor p=new EmuBundleAnnotationPersistor();
        try {
            File f=new File("/homes/klausj/WORK/EmuDbs_NewEMU/ae/0000_ses/msajc003_bndl/msajc003_annot.json");
            p.setFile(f);
            Bundle bl=p.load();
            System.out.println(bl);
            File of=File.createTempFile("test",".json");
            p.setFile(of);
            p.write(bl);
            String ofC=StreamCopy.readTextFile(of, Charset.forName("UTF-8"));
            System.out.println(ofC);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (EncodeException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
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
    public String getPreferredFileExtension() {
        return "json";
    }

    @Override
    public String getPreferredFilenameSuffix() {
        return "_annot";
    }

   
    public String[] getLinks() {
        return new String[]{"https://github.com/IPS-LMU/EMU-webApp","https://github.com/IPS-LMU/emuR"};
    }


}
