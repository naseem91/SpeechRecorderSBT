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
 * Date  : Mar 22, 2010
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ips.annot.textgrid;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ips.annot.model.db.Bundle;
import ips.annot.model.db.EventItem;
import ips.annot.model.db.Item;
import ips.annot.model.db.Label;
import ips.annot.model.db.Level;
import ips.annot.model.db.LevelDefinition;
import ips.annot.model.db.SegmentItem;
import ips.annot.view.Viewer;


/**
 * Parser and exporter for Praat TextGrid annotation files.
 * I could not find a TextGrid syntax documentation. The syntax is derived only from example TextGrid files.   
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class TextGridFileParser {

    private final static boolean DEBUG=false;
    private final static int TAB_SPACES=4;
    private final static String FILE_TYPE_KEY="File type";
    private final static String CLASS_KEY="class";
    private final static String OBJECT_CLASS_KEY="Object "+CLASS_KEY;
    private final static String TIERS_SIZE_KEY="size";
    private final static String TIER_ITEM_KEY="item";
    private final static String NAME_KEY="name";
    private final static String INTERVALS_KEY="intervals";
    private final static String POINTS_KEY="points";
    private final static String XMIN_KEY="xmin";
    private final static String XMAX_KEY="xmax";
    private final static String TEXT_KEY="text";
    private final static String TIME_KEY="time";
    
    private final static String FILE_TYPE_VAL_OO_TEXTFILE="ooTextFile";
    private final static String OBJECT_CLASS_VAL_TEXTGRID="TextGrid";
    private final static String TIER_CLASS_VAL_INTERVAL="IntervalTier";
    private final static String TIER_CLASS_VAL_TEXT="TextTier";
    private float sampleRate;
    private String fileType=null;
    private String objectClass=null;
//    private Double fileXMin;
//    private Double fileXMax;
    private Integer tiersCount=null;
    private boolean hasTiers=false;
    private int tierIndex=-1;
    private Bundle bundle;
    private Level currentTier=null;
    private String currentTierClass=null;
    private String currentTierName=null;
    private Integer currentTierSize=null;
    
    private SegmentItem currentIntervalItem=null;
    private EventItem currentEventItem=null;
    private Integer currentElementIndex=null;
    private String tabString;
    
    private double minXmin;
    private double maxXmax;
    
//    private enum ParseState {HEADER,TIERS,TIER}
//   private ParseState parseState=ParseState.HEADER;
    
    /**
     * Create parser.
     * The sample rate is required, because the Java annotation model in this package is based on frame counts rather than time values.
     * @param sampleRate sample rate of the audio file
     */
    public TextGridFileParser(float sampleRate){
        super();
        this.sampleRate=sampleRate;
        StringBuffer tabStringSb=new StringBuffer();
        for(int t=0;t<TAB_SPACES;t++){
            tabStringSb.append(' ');
        }
        tabString=tabStringSb.toString();
    }
    
    public class Property {

    	public Property(String key,String value){
    		this.key=key;
    		this.value=value;
    	}
    	public String key;
    	public String value;
    }

    Property parseProperty(String line){
    	return parseProperty(line,false);
    }
    Property parseProperty(String line,boolean doublequoted){
    	return parseProperty(line, "=", doublequoted);
    }
    Property parseProperty(String line,String separator,boolean doublequoted){
    	Property p=null;
    	String[] kv=line.trim().split(separator, 2);
    	if(kv.length==2){
    		String key=kv[0].trim();
    		String val=kv[1].trim();
    		if(doublequoted){
    			val=val.replaceFirst("^\"", "");
    			val=val.replaceFirst("\"$","");
    		}
    		p=new Property(key,val);
    	}
    	return p;
    }
    
   
    private void parseTier(String trimmedLine){
        if(currentTierClass==null){
            Property p=parseProperty(trimmedLine,true);
            if(p !=null && "class".equals(p.key)){
                currentTierClass=p.value;
                if(currentTierClass.equals("IntervalTier")){
                    bundle.getLevels().add(currentTier);
                }else if(currentTierClass.equals("TextTier")){
                    bundle.getLevels().add(currentTier);
                }else{
               System.err.println("TextGrid tiers of class \""+currentTierClass+"\" not supported!");
                }
            }
        }
         if(currentTierName==null){
             Property p=parseProperty(trimmedLine,true);
             if(p!=null && "name".equals(p.key)){
                 currentTierName=p.value;
                 currentTier.getDefinition().setName(p.value);
//                 currentTier.setSampleRate(sampleRate);
             }
         }
         if(currentTierClass!=null){
             LevelDefinition ld=currentTier.getDefinition();
             if(ld==null){
                 ld=new LevelDefinition();
                 currentTier.setDefinition(ld);
             }
             if(currentTierClass.equals(TIER_CLASS_VAL_INTERVAL)){
                 ld.setType(LevelDefinition.SEGMENT);
                 parseIntervalTier(trimmedLine);
             }else if(currentTierClass.equals(TIER_CLASS_VAL_TEXT)){
                 ld.setType(LevelDefinition.EVENT);
                 parseTextTier(trimmedLine);
             }
         }
         
    }
    
    private long timeToFrames(double time){
      
        // find nearest sample
        double contFramePosition=sampleRate*time;
        return Math.round(contFramePosition);
    }
    
    private void parseTextTier(String trimmedLine) {
     // find size (and other properties)
        if(currentTierSize==null && trimmedLine.matches("points\\s*:.*")){
            String intervalsPropertyStr=trimmedLine.replaceFirst("points\\s*:","").trim();
            Property intervalsProperty=parseProperty(intervalsPropertyStr);
            if(intervalsProperty!=null && intervalsProperty.key.equals("size")){
                currentTierSize=Integer.parseInt(intervalsProperty.value);
                if(DEBUG)System.out.println("points: size="+currentTierSize);
            }
        }
        if(trimmedLine.matches("points\\s*\\[\\s*[0-9]+\\s*\\]\\s*:\\s*")){
            String pointIndexStr=trimmedLine.replaceFirst("points\\s*\\[\\s*","");
            pointIndexStr=pointIndexStr.replaceFirst("\\s*\\]\\s*:\\s*","");
            currentElementIndex=Integer.parseInt(pointIndexStr);
            currentEventItem=new EventItem();
            currentTier.getItems().add(currentEventItem);
            currentEventItem.setLevel(currentTier);
            if(DEBUG)System.out.println("Point: "+currentElementIndex);
            
        }else{
            Property p=parseProperty(trimmedLine, true);
            if(p!=null && currentEventItem!=null){
                if(p.key.equals("time")){
                    double time=Double.parseDouble(p.value);
                    long frames=timeToFrames(time);
                    currentEventItem.setSamplepoint(frames);
                }else if(p.key.equals("mark")){
                    currentEventItem.setLabel(currentTier.getName(), p.value);
                }else if(p.key.equals("text")){
                    currentEventItem.setLabel(currentTier.getName(), p.value);
                }
            }
        }
        
    }
    private void parseIntervalTier(String trimmedLine) {
        
        // find size (and other properties)
        if(currentTierSize==null && trimmedLine.matches("intervals\\s*:.*")){
            String intervalsPropertyStr=trimmedLine.replaceFirst("intervals\\s*:","").trim();
            Property intervalsProperty=parseProperty(intervalsPropertyStr);
            if(intervalsProperty!=null && intervalsProperty.key.equals("size")){
                currentTierSize=Integer.parseInt(intervalsProperty.value);
                if(DEBUG)System.out.println("intervals: size="+currentTierSize);
            }
        }
        if(trimmedLine.matches("intervals\\s*\\[\\s*[0-9]+\\s*\\]\\s*:\\s*")){
            String intervalIndexStr=trimmedLine.replaceFirst("intervals\\s*\\[\\s*","");
            intervalIndexStr=intervalIndexStr.replaceFirst("\\s*\\]\\s*:\\s*","");
            currentElementIndex=Integer.parseInt(intervalIndexStr);
            currentIntervalItem=new SegmentItem();
            currentTier.getItems().add(currentIntervalItem);
            currentIntervalItem.setLevel(currentTier);
            if(DEBUG)System.out.println("Interval: "+currentElementIndex);
            
        }else{
            Property p=parseProperty(trimmedLine, true);
            if(p!=null && currentIntervalItem!=null){
                if(p.key.equals("xmin")){
                    double minTime=Double.parseDouble(p.value);
                    long minFrames=timeToFrames(minTime);
                    currentIntervalItem.setSampleStart(minFrames);
                    if(DEBUG)System.out.println("Segment: "+currentIntervalItem);
                }else if(p.key.equals("xmax")){
                    double maxTime=Double.parseDouble(p.value);
                    long maxFrames=timeToFrames(maxTime);
                    currentIntervalItem.setSampleDur(maxFrames - currentIntervalItem.getSampleStart() -1);
                    if(DEBUG)System.out.println("Segment: "+currentIntervalItem);
                }else if(p.key.equals("text")){
                    currentIntervalItem.setLabel(currentTier.getName(), p.value);
                    if(DEBUG)System.out.println("Segment: "+currentIntervalItem);
                }
            }
        }
         
    }
    
    /**
     * Parse TextGrid file. 
     * @param textGridFile TextGrid file
     * @param charset encoding charset of the file
     * @return annotation object 
     * @throws IOException
     */
    public Bundle parse(File textGridFile,Charset charset) throws IOException{
        FileInputStream fis=new FileInputStream(textGridFile);
        Reader reader=new InputStreamReader(fis,charset);
        return parse(reader);
    }
    /**
     * Parse TxtGrid content from reader.
     * @param reader TextGrid input
     * @return annotation object
     * @throws IOException
     */
    public Bundle parse(Reader reader) throws IOException{
        bundle=new Bundle();
        bundle.setSampleRate(sampleRate);
        LineNumberReader lnr=new LineNumberReader(reader);
       
        String line=null;
        String trimmedLine=null;
        while((line=lnr.readLine())!=null){
            trimmedLine=line.trim();
            if(fileType==null){
               Property p=parseProperty(line,true);
               if(p!=null && p.key.equals(FILE_TYPE_KEY)){
                   fileType=p.value;
               }
               continue;
            }else{
                if(objectClass==null){
                    Property p=parseProperty(line,true);
                    if(p!=null && p.key.equals(OBJECT_CLASS_KEY)){
                        objectClass=p.value;
                    }
                    continue;
                }else{
                    if(fileType.equals(FILE_TYPE_VAL_OO_TEXTFILE) && objectClass.equals(OBJECT_CLASS_VAL_TEXTGRID)){
                      if(!hasTiers){
                          Property p=parseProperty(line,"\\s+", false);
                          hasTiers=(p!=null && p.key.equals("tiers?") && p.value.equals("<exists>"));
                      }else{
                          if(tiersCount==null){
                              Property p=parseProperty(line);
                              if(p!=null && p.key.equals("size")){
                                  tiersCount=Integer.parseInt(p.value);
                              }
                          }else{
                              if(trimmedLine.startsWith("item")){
                                  String tierIndexStr=trimmedLine.replaceFirst("item\\s*","");
                                  tierIndexStr=tierIndexStr.replaceFirst("\\s*:$","");
                                  if(tierIndexStr.matches("\\[\\s*[0-9]+\\s*\\]")){
                                      tierIndexStr=tierIndexStr.replaceFirst("\\[\\s*","");
                                      tierIndexStr=tierIndexStr.replaceFirst("\\s*\\]","");
                                      tierIndex=Integer.parseInt(tierIndexStr);
                                      currentTier=new Level();
//                                      currentTier.setSampleRate(sampleRate);
                                      
                                      currentTierClass=null;
                                      currentTierName=null;
                                      currentTierSize=null;
                                      currentIntervalItem=null;
                                      if(DEBUG)System.out.println("Tier index: "+tierIndex);
                                  }
                              }else {
                                  parseTier(trimmedLine);
                              }
                          }
                      }
                    }
                }
            }
        }
        reader.close();
        return bundle;
    }
    
    
    private String keyValueString(String key,Object value){
        String valueStr=null;
        if(value instanceof String){
            valueStr="\""+value+"\"";
        }else{
            valueStr=value.toString();
        }
        return key+" = "+valueStr+"\n";
    }
    private String keyValueString(String indent,String key,Object value){
        return indent+keyValueString(key, value);
    }
    private String header(){
        return keyValueString(FILE_TYPE_KEY, FILE_TYPE_VAL_OO_TEXTFILE)
        +keyValueString(OBJECT_CLASS_KEY, OBJECT_CLASS_VAL_TEXTGRID);
    }
    
    private Float sampleRateFromBundle(Item item){
        Float srFb=null;
        Level lvl=item.getLevel();
        if(lvl!=null){
            Bundle b=lvl.getBundle();
            if(b!=null){
                srFb=b.getSampleRate();
            }
        }
        return srFb;
    }
    
    private float sampleRateOfItem(Item item){
        Float srFb=sampleRateFromBundle(item);
        if(srFb!=null){
            return srFb;
        }else{
            return sampleRate;
        }
    }
    
    private double xminOfItem(Item item){

        float sr=sampleRateOfItem(item);
        Long sampleStart=item.getSampleStart();
        // check if segment item
        if(sampleStart==null){
            // if not try to find position by resolving linked items
//            sampleStart=bundle.startByLinkedSegmentItems(item);
        	sampleStart=bundle.startByContext(item);
        }
        double xmin;
        if(sampleStart!=null){
            xmin=(double)sampleStart / (double)sr;
        }else{
            // use total xmin
            xmin=minXmin;
        }
        return xmin;

    }
    private double xmaxOfItem(Item item){

        float sr=sampleRateOfItem(item);
        Long sampleEnd=item.getSampleEnd();
        // check if segment item
        if(sampleEnd==null){
            // if not try to find position by resolving linked items
//            sampleEnd=bundle.endByLinkedSegmentItems(item);
        	sampleEnd=bundle.endByContext(item);
        }
        double xmax;
        if(sampleEnd!=null){
            xmax=(double)sampleEnd / (double)sr;
        }else{
            // use total xmax
            xmax=maxXmax;
        }
        return xmax;
    }
     
    private void writeSegment(Item intervalItem,Writer writer,String tab) throws IOException{
        
        double xmin=xminOfItem(intervalItem);
        double xmax=xmaxOfItem(intervalItem);
        String text="";
        List<Label> lblsList=intervalItem.getLabelsList();
        if(lblsList!=null && lblsList.size()>0){
            text=lblsList.get(0).getValueString();
        }
//        String text=intervalItem.getLabelText();
        writer.write(keyValueString(tab, XMIN_KEY, xmin));
        writer.write(keyValueString(tab, XMAX_KEY, xmax));
        writer.write(keyValueString(tab, TEXT_KEY, text));
     }
    
     private void writeEvent(Item eventItem,Writer writer,String tab) throws IOException{
         float sr=sampleRateOfItem(eventItem);
        double time=eventItem.getSamplepoint() * sr;
//        String text=eventItem.getLabelText();
        String text="";
        List<Label> lblsList=eventItem.getLabelsList();
        if(lblsList!=null && lblsList.size()>0){
            text=lblsList.get(0).getValueString();
        }
        writer.write(keyValueString(tab, TIME_KEY,time));
        
        writer.write(keyValueString(tab, TEXT_KEY, text));
     }
    
     private void write(Level tier,Writer writer,String tab) throws IOException{
       String tierType=tier.getDefinition().getType();
       String tierName=tier.getName();
       double xmin;
       double xmax;
       if(LevelDefinition.SEGMENT.equals(tierType) || LevelDefinition.ITEM.equals(tierType)){
           List<Item> itemList=tier.getItems();
           writer.write(keyValueString(tab,CLASS_KEY, TIER_CLASS_VAL_INTERVAL));
           writer.write(keyValueString(tab,NAME_KEY,tierName));
           xmin = getXmin(tier);
           xmax = getXmax(tier);
           writer.write(keyValueString(tab, XMIN_KEY, xmin));
           writer.write(keyValueString(tab, XMAX_KEY, xmax));
           int itemCount=itemList.size();
           writer.write(keyValueString(tab, INTERVALS_KEY+": "+TIERS_SIZE_KEY, itemCount));
           for(int index=0;index<itemCount;index++){
               Item intervalItem=itemList.get(index);
               writer.write(tab+INTERVALS_KEY+"["+index+"]:\n");
               writeSegment(intervalItem,writer,tab.concat(tabString));
           }
       }else if (LevelDefinition.EVENT.equals(tierType)) {
           List<Item> itemList=tier.getItems();
           writer.write(keyValueString(tab,CLASS_KEY, TIER_CLASS_VAL_TEXT));
           writer.write(keyValueString(tab,NAME_KEY,tierName));
           xmin = getXmin(tier);
           xmax = getXmax(tier);
           writer.write(keyValueString(tab, XMIN_KEY, xmin));
           writer.write(keyValueString(tab, XMAX_KEY, xmax));
           int segsCount=itemList.size();
           writer.write(keyValueString(tab, POINTS_KEY+": "+TIERS_SIZE_KEY, segsCount));
           for(int sc=0;sc<segsCount;sc++){
               EventItem eventItem= (EventItem) itemList.get(sc);
               writer.write(tab+POINTS_KEY+"["+sc+"]:\n");
               writeEvent(eventItem,writer,tab.concat(tabString));
           }
       }
    }

    
     public double getXmin(Level tier) {
         if(tier!=null){
             List<Item> items=tier.getItems();
             if(items.size()>0){
                 Item item = items.get(0);
                 String lType=tier.getType();
                 if (LevelDefinition.SEGMENT.equals(lType) || LevelDefinition.ITEM.equals(lType)) {
                     return xminOfItem(item);
                 } else if (tier.getDefinition().getType().equals(LevelDefinition.EVENT)) {
                     float sr=sampleRateOfItem(item);
                     return (double)item.getSamplepoint() * (double)sr;
                 }
             }
         }
         return 0.0;
     }
    
     public double getXmax(Level tier) {
         if(tier!=null){
             List<Item> items=tier.getItems();
             if(items.size()>0){
                 int lastIndex = tier.getItems().size() - 1;
                 String lType=tier.getType();
                 if (LevelDefinition.SEGMENT.equals(lType) || LevelDefinition.ITEM.equals(lType)) {
                     Item item = tier.getItems().get(lastIndex);
                     return xmaxOfItem(item);
                 } else if (LevelDefinition.EVENT.equals(lType)) {
                     Item item = tier.getItems().get(lastIndex);
                     float sr=sampleRateOfItem(item);
                     return (double)item.getSamplepoint() / (double)sr;
                 }
             }
         }
         return 0.0;
     }
    
    
    private double getMinXmin(List<Level> levels) {
        double xmin = Double.MAX_VALUE;
        for (Level tier : levels) {
            double xMinLvl=getXmin(tier);
            if (xMinLvl < xmin) {
                xmin = xMinLvl;
            }
        }
        return xmin;
    }
    
    private double getMaxXmax(List<Level> levels) {
        double xmax = Double.MIN_VALUE;
        for (Level tier : levels) {
            double xmaxLvl=getXmax(tier);
            if (xmaxLvl > xmax) {
                xmax = xmaxLvl;
            }
        }
        return xmax;
    }
    
    private double maxXmax(){
        return getMaxXmax(bundle.getLevels());
    }
    
    private double minXmin(){
        return getMinXmin(bundle.getLevels());
    }
    
    public void write(Bundle bundle, Writer writer) throws IOException {
        String tab = "";
        this.bundle=bundle;
        try {
            writer.write(header());
            writer.write("\n");

            
            List<Level> levels = bundle.getLevels();
                int tierCount = levels.size();
                if (tierCount > 0) {
                    minXmin = minXmin();
                    maxXmax = maxXmax();

                    writer.write(keyValueString(tab, XMIN_KEY, minXmin));
                    writer.write(keyValueString(tab, XMAX_KEY, maxXmax));
                    writer.write("tiers? <exists>\n"); // does anybody know what
                                                       // this means?
                    writer.write(keyValueString(TIERS_SIZE_KEY, levels.size()));
                    writer.write(TIER_ITEM_KEY + " []:\n");
                    tab = tab.concat(tabString);
                    for (int tc = 0; tc < tierCount; tc++) {
                        writer.write(tabString + TIER_ITEM_KEY + " [" + tc  + "]:\n");
                        Level segmentLvl = (Level) levels.get(tc);
                        write(segmentLvl, writer, tab.concat(tabString));
                    }
                }

        } catch (IOException e) {
            throw e;
        } finally {
            writer.close();
        }
    }
    
    /**
     * main method for test purposes.
     */
    public static void main(String[] args){
        Charset cs=Charset.forName("UTF-8");
        TextGridFileParser tfp = new TextGridFileParser((float) 44100.0);
        File tgf = new File(args[0]);
//        File tgfOut=new File(args[1]);
       
        try {
            Bundle bundle = tfp.parse(tgf, Charset.forName("UTF-8"));
            bundle.getSignalpaths().add(tgf.getAbsolutePath());
            System.out.println("Bundle: " + bundle.toString());
            
            Viewer viewer = new Viewer(tgf.getName(), bundle);
            
//            Gson gson = new Gson();
            Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
            String json = gson.toJson(bundle);
            System.out.println("Json: " + json);

            String jsonFilename = tgf.getAbsolutePath().replaceAll(".ext.rid", "json");
            PrintWriter out1 = new PrintWriter(new File(jsonFilename), "UTF-8");
            out1.write(json);
            out1.close();
            
            bundle.toXML();
            
        } catch (FileNotFoundException e) {
           
            e.printStackTrace();
        } catch (IOException e) {
         
            e.printStackTrace();
        }
        
    }
    
}
