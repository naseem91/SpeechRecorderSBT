//    IPS Java Utils
// 	  (c) Copyright 2012
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Utils
//
//
//    IPS Java Utils is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Utils is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Utils.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.net.http;

import java.awt.PageAttributes.MediaType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ipsk.text.StringTokenizer;
import ipsk.text.quoting.QuoteParser;
import ipsk.text.quoting.TextPart;


/**
 * @author klausj
 *
 */
public class ContentType {

    private String mediaType;
    private Map<String,List<String>> parameters;
    
    public ContentType(String mediaType,Map<String,List<String>> parameters){
        super();
        this.mediaType=mediaType;
        this.parameters=parameters;
    }
    
    public static ContentType parseHttpString(String httpContentTypeString){
        // parse quotes
        List<TextPart> textPartList=QuoteParser.parseText(httpContentTypeString,'"', '\\', false);
        // separate fields
        String[] fields=StringTokenizer.split(textPartList, ';', true);
        // first field is media type 
        String mediaType=fields[0];
        
        // subsequent fields are parameters
        HashMap<String,List<String>> pMap=new HashMap<String, List<String>>();
        for(int i=1;i<fields.length;i++){
            String field=fields[i];
            int equInd=field.indexOf('=');
            if(equInd>0){
                // rfc2616: parameter names are case insensitive
                String key=field.substring(0, equInd).trim().toLowerCase(Locale.US);
                String val=field.substring(equInd+1).trim();
                List<String> paramVals=pMap.get(key);
                if(paramVals==null){
                    paramVals=new ArrayList<String>();
                    pMap.put(key,paramVals);
                }
                paramVals.add(val);
            }
        }
        
        ContentType ct=new ContentType(mediaType, pMap);
        return ct;
        
        
       
       
    }
//    
//    public String getMIMEType(){
//       
//       String[] fields=st.split(httpContentType,';', true);
//    }
//    

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }

    public Map<String, List<String>> getParameters() {
        return parameters;
    }
    public String getCharsetParameter(){
        String charset=null;
        List<String> charsetparamList=parameters.get("charset");
        if(charsetparamList!=null && charsetparamList.size()==1){
            charset=charsetparamList.get(0);
        }
        return charset;
    }
}
