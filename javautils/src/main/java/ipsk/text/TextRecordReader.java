//    IPS Java Utils
// 	  (c) Copyright 2014
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

package ipsk.text;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * @author klausj
 *
 */
public class TextRecordReader{

    private static int BUF_SIZE_INCREMENT=100;
    private char[][] recordSeparators;
    private Reader sourceReader;
    private char[] buf=new char[1];
    private char[] recordBuf=new char[BUF_SIZE_INCREMENT];
    private int recordBufPos=0;
   
    private char[] matchedChars;
    private int matchedCharsFilled=0;
   
    public TextRecordReader(Reader sourceReader, char[][] recordSeparators) {
        super();
        this.recordSeparators = recordSeparators;
        this.sourceReader = sourceReader;
        int maxRecSeplen=0;
        for(char[] recordSep:recordSeparators){
            int recordSepLen=recordSep.length;
            if(recordSepLen>maxRecSeplen){
                maxRecSeplen=recordSepLen;
            }
        }
        matchedChars=new char[maxRecSeplen];
    }

    private char[] createRecordBuf(){
        char[] resBuf=new char[recordBufPos];
        for(int i=0;i<recordBufPos;i++){
            resBuf[i]=recordBuf[i];
        }
        recordBufPos=0;
        return resBuf;
    }
    
    public char[] readRecord() throws IOException{
//        int r=0;
        while((sourceReader.read(buf))!=-1){

            for(char[] recordSep:recordSeparators){
                int recordSepLen=recordSep.length;
                if(matchedCharsFilled<recordSepLen){
                    boolean match=true;
                    for(int i=0;i<matchedCharsFilled;i++){
                        if(matchedChars[i]!=recordSep[i]){
                            match=false;
                            break;
                        }
                    }
                    if(match){
                        // 
                        if(buf[0]==recordSep[matchedCharsFilled]){
                           
                            matchedChars[matchedCharsFilled]=buf[0];
                            matchedCharsFilled++;
                            if(matchedCharsFilled==recordSepLen){
                                // found record separator
                                matchedCharsFilled=0;
                                return(createRecordBuf());
                            }
                        }
                    }
                }
            }
           
            if(recordBuf.length<=recordBufPos+1){
                char[] newRecordBuf=new char[recordBufPos+BUF_SIZE_INCREMENT];
                for(int i=0;i<recordBufPos;i++){
                    newRecordBuf[i]=recordBuf[i];
                }
                recordBuf=newRecordBuf;
            }
            recordBuf[recordBufPos]=buf[0];
            recordBufPos++;
        }
        if(recordBufPos==0){
            return null;
        }else{
            return(createRecordBuf());
        }
    }

    /**
     * @param args
     * @throws FileNotFoundException 
     */
    public static void main(String[] args){
        Reader testReader=null;
       try {
        testReader=new FileReader("/homes/klausj/TESTFILES/Tabellenimporttest.csv");
        // test as platform independent text line reader
        TextRecordReader trr=new TextRecordReader(testReader,new char[][]{new char[]{'\r','\n'},new char[]{'\n'}, new char[]{'\r'}});
        char[] record=null;
        while((record=trr.readRecord())!=null){
            System.out.println("Record: "+new String(record));
        }
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }finally{
        if(testReader!=null){
            try {
                testReader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
       
    }


   

}
