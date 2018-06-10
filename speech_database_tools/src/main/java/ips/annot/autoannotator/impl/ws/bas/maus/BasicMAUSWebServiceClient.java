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
 * Date  : Mar 4, 2013
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ips.annot.autoannotator.impl.ws.bas.maus;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClients;

import ips.annot.autoannotator.AutoAnnotation;
import ips.annot.autoannotator.AutoAnnotationServiceDescriptor;
import ips.annot.autoannotator.AutoAnnotatorException;
import ips.annot.autoannotator.impl.maus.BasicMAUSAnnotation;
import ips.annot.autoannotator.impl.ws.bas.BasicBasServiceClient;
import ips.annot.model.PredefinedLevelDefinition;
import ips.annot.model.db.Bundle;
import ips.annot.model.db.Item;
import ips.annot.model.db.Level;
import ips.annot.textgrid.TextGridFileParser;
import ipsk.io.StreamCopy;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

public class BasicMAUSWebServiceClient extends BasicBasServiceClient {

    public static final BasicMAUSAnnotatorServiceDescriptor DESCRIPTOR = new BasicMAUSAnnotatorServiceDescriptor();

    public class ServerCaps {
        private List<String> supportedLangs;

        public List<String> getSupportedLangs() {
            return supportedLangs;
        }

        public void setSupportedLangs(List<String> supportedLangs) {
            this.supportedLangs = supportedLangs;
        }

        public String toString() {
            StringBuffer sb = new StringBuffer();
            if (supportedLangs != null) {
                sb.append("Supported languages: ");
                int suppLanglen = supportedLangs.size();
                for (int i = 0; i < suppLanglen; i++) {
                    String suppLang = supportedLangs.get(i);
                    sb.append(suppLang);
                    if (i < suppLanglen - 1) {
                        sb.append(',');
                    }
                }
            }
            return sb.toString();

        }

    }

    private final static boolean DEBUG = false;
    // Only Basic should work
    // private static String URL =
    // "https://clarin.phonetik.uni-muenchen.de/BASWebServices/services/runMAUS";
    private static String URL = BASE_URL + "/runMAUSBasic";

    private static String PARAM_LANG = "LANGUAGE";

    private AnnotationRequest annotationRequest;

    private boolean insertKanonicalTier = true;

    /**
     * @return the insertKanonicalTier
     */
    public boolean isInsertKanonicalTier() {
        return insertKanonicalTier;
    }

    /**
     * @param insertKanonicalTier
     *            the insertKanonicalTier to set
     */
    public void setInsertKanonicalTier(boolean insertKanonicalTier) {
        this.insertKanonicalTier = insertKanonicalTier;
    }

    /**
     * @return the insertOrthographyTier
     */
    public boolean isInsertOrthographyTier() {
        return insertOrthographyTier;
    }

    /**
     * @param insertOrthographyTier
     *            the insertOrthographyTier to set
     */
    public void setInsertOrthographyTier(boolean insertOrthographyTier) {
        this.insertOrthographyTier = insertOrthographyTier;
    }

    private boolean insertOrthographyTier = true;

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    private static final ContentType DEFAULT_TEXT_CONTENT_TYPE = ContentType.create("text/plain", DEFAULT_CHARSET);

    private DocumentBuilderFactory docBuilderFactory;

    public BasicMAUSWebServiceClient() {
        super();
        docBuilderFactory = DocumentBuilderFactory.newInstance();

    }

    public AutoAnnotation webMAUSClient(File audioFile, String orthoGraphy, Locale loc) throws AutoAnnotatorException {

        // WebMaus always requires a filename for audiofile and orthography file
        String audioFilename = audioFile.getName();
        // remove extension
        String audioFilenameBody = audioFilename.replaceFirst("[.][^.]*$", "");
        // Build dummy text (file) name
        String orthoGraphyFilename = audioFilenameBody + ".txt";

        if (DEBUG) {
            System.out.println("Orthogrphy (dummy) filename: " + orthoGraphyFilename);
        }
        return webMAUSClient(audioFile, orthoGraphy, orthoGraphyFilename, loc);
    }

    public AutoAnnotation webMAUSClient(File audioFile, String orthoGraphy, String orthoGraphyFilename, Locale loc)
            throws AutoAnnotatorException {
        // InputStreamBody textBody=null;
        byte[] orthoBytes = orthoGraphy.getBytes(DEFAULT_CHARSET);
        // ByteArrayInputStream orthoBis=new ByteArrayInputStream(orthoBytes);

        // textBody = new InputStreamBody(orthoBis,ContentType.TEXT_PLAIN,
        // orthoGraphyFilename);
        ByteArrayBody textBody = new ByteArrayBody(orthoBytes, DEFAULT_TEXT_CONTENT_TYPE, orthoGraphyFilename);

        return webMAUSClient(audioFile, textBody, loc);
    }

    public AutoAnnotation webMAUSClient(File audioFile, File orthoGraphyFile, Locale loc)
            throws AutoAnnotatorException {
        FileBody textBody = new FileBody(orthoGraphyFile, DEFAULT_TEXT_CONTENT_TYPE, orthoGraphyFile.getName());
        if (DEBUG)
            System.out.println("filename: " + textBody.getFilename());
        return webMAUSClient(audioFile, textBody, loc);
    }

    public AutoAnnotation webMAUSClient(File audioFile, ContentBody textBody, Locale loc)
            throws AutoAnnotatorException {

        try {
            getServerCaps();
        } catch (Exception e) {
            // Ignore server caps and try to MAUS anyway
        }
        // Bundle bundle = null;
        BasicMAUSAnnotation anno = null;
        HttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(URL);
        AudioFileFormat aff = null;
        try {
            aff = AudioSystem.getAudioFileFormat(audioFile);
        } catch (UnsupportedAudioFileException e1) {

            e1.printStackTrace();
            throw new AutoAnnotatorException(e1);
        } catch (IOException e1) {
            e1.printStackTrace();
            throw new AutoAnnotatorException(e1);
        }
        AudioFormat af = aff.getFormat();
        float sampleRate = af.getSampleRate();
        FileBody audio = new FileBody(audioFile, ContentType.create("audio/wav"), audioFile.getName());

        String iso639ThreeLetters = loc.getISO3Language();
        if (DEBUG) {
            System.out.println("Set language param: " + iso639ThreeLetters);
        }
        StringBody languageBody = new StringBody(iso639ThreeLetters, DEFAULT_TEXT_CONTENT_TYPE);
        StringBody trueBody = new StringBody("true", ContentType.TEXT_PLAIN);
        // StringBody falseBody=new StringBody("false", ContentType.TEXT_PLAIN);
        MultipartEntityBuilder reqEntityBuilder = MultipartEntityBuilder.create();
        // reqEntity.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        // reqEntity.setLaxMode();
        // StringBody parBody=new StringBody("par", ContentType.TEXT_PLAIN);
        reqEntityBuilder.addPart("LANGUAGE", languageBody);
        reqEntityBuilder.addPart("TEXT", textBody);
        reqEntityBuilder.addPart("SIGNAL", audio);
        // reqEntityBuilder.addPart("OUTFORMAT", parBody);
        if (insertKanonicalTier) {
            reqEntityBuilder.addPart("INSKANTEXTGRID", trueBody);
        }
        if (insertOrthographyTier) {
            reqEntityBuilder.addPart("INSORTTEXTGRID", trueBody);
        }

        HttpEntity reqEntity = reqEntityBuilder.build();
        httppost.setEntity(reqEntity);
        HttpResponse response;

        try {
            response = httpclient.execute(httppost);

            StatusLine sl = response.getStatusLine();
            int status = sl.getStatusCode();
            if (status >= HttpStatus.SC_OK && status < HttpStatus.SC_MULTIPLE_CHOICES) {
                HttpEntity resEntity = response.getEntity();

                InputStream content = resEntity.getContent();

                java.net.URL dlUrl = downloadLinkFromResponse(content);
                URLConnection dlConn = dlUrl.openConnection();
                // System.out.println(dlConn.getContentType());
                // return text/plain without charset
                InputStream is = dlConn.getInputStream();
                // save contents to array
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                StreamCopy.copy(is, bos);
                byte[] textGridBinData = bos.toByteArray();

                // ByteArrayInputStream bisD=new
                // ByteArrayInputStream(textGridBinData);
                // InputStreamReader conReader=new InputStreamReader(bis);
                // StreamCopy.copy(bisD, System.out);

                ByteArrayInputStream bis = new ByteArrayInputStream(textGridBinData);
                InputStreamReader conReader = new InputStreamReader(bis,DEFAULT_CHARSET);
                TextGridFileParser tgfp = new TextGridFileParser(sampleRate);
                Bundle bundle = tgfp.parse(conReader);

                conReader.close();
                anno = new BasicMAUSAnnotation(textGridBinData, bundle.getLevels());

                // File targetDir=annotationRequest.getTargetDirectory();
                // String fbn=annotationRequest.getTargetFilebasename();
                // if(targetDir!=null && fbn!=null){
                // // write textGrid file
                // String tgFn=fbn+"."+BasicMAUSAnnotation.TEXTGRID_EXTENSION;
                //
                // File textGRidFile=new File(targetDir,tgFn);
                // ByteArrayInputStream tgFbis=new
                // ByteArrayInputStream(textGridBinData);
                // StreamCopy.copy(tgFbis, textGRidFile,true);
                // }

            }

        } catch (IOException e) {
            throw new AutoAnnotatorException(e);
        }

        return anno;

    }

    public static void main(String[] args) {
        BasicMAUSWebServiceClient wmc = new BasicMAUSWebServiceClient();

        String example2Txt = "Einzelzimmer 248 Mark.";
        try {
            AutoAnnotation aa = wmc.webMAUSClient(new File("/homes/klausj/examples-WebMAUS/example2.wav"), example2Txt,
                    Locale.GERMAN);
            BasicMAUSAnnotation bma = (BasicMAUSAnnotation) aa;

        } catch (AutoAnnotatorException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public AutoAnnotation call() throws Exception {
        // File audioFile=annotationRequest.getMediaFile();
        // String orthoGraphy=annotationRequest.getOrthoGraphy();
        Bundle inputBundle = annotationRequest.getBundle();
        List<String> sigPaths = inputBundle.getSignalpaths();
        if (sigPaths != null && sigPaths.size() == 1) {
            String sigPath = sigPaths.get(0);
            File audioFile = new File(sigPath);
            if (audioFile != null && audioFile.exists()) {
                Level tplLvl = inputBundle.getTierByName(PredefinedLevelDefinition.TPL.getKeyName());
                if (tplLvl != null) {
                    List<Item> its = tplLvl.getItems();
                    if (its.size() == 1) {
                        Item it = its.get(0);
                        Map<String, Object> itLbls = it.getLabels();
                        Object itLbl = itLbls.get(PredefinedLevelDefinition.TPL.getKeyName());
                        if (itLbl instanceof String) {
                            String templateText = (String) itLbl;
                            if (templateText != null) {
                                return webMAUSClient(audioFile, templateText, inputBundle.getLocale());
                            }
                        }
                    }
                }
            }
        }
        // TODO err message
        throw new AutoAnnotatorException();

    }

    @Override
    public void open() {
        // nothing todo (separate HTTP connections/requests for each annotation
    }

    @Override
    public void close() {
        // nothing todo
    }

    public void setAnnotationRequest(AnnotationRequest ar) {
        this.annotationRequest = ar;
    }

    @Override
    public boolean isBundleSupported(Bundle bundle) throws IOException {
        Locale l = bundle.getLocale();
        boolean langSupp = isLanguageSupported(l);
        List<String> sigPaths = bundle.getSignalpaths();
        if (sigPaths.size() == 0) {
            return false;
        }
        File sigFile = new File(sigPaths.get(0));
        boolean mSupp = isMediafileSupported(sigFile);
        return (langSupp && mSupp);
    }

    @Override
    public AutoAnnotationServiceDescriptor getServiceDescriptor() {
        return DESCRIPTOR;
    }

  

    public AutoAnnotation webMAUSClient(File audioFile, String transcript, String textFilename)
            throws AutoAnnotatorException {
        return webMAUSClient(audioFile, transcript, textFilename, null);
    }

}
