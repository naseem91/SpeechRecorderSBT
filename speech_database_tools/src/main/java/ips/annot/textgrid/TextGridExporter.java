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

package ips.annot.textgrid;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;

import ips.annot.model.db.Bundle;
import ips.annot.model.db.EventItem;
import ips.annot.model.db.Item;
import ips.annot.model.db.SegmentItem;


/*
 * Created on 07.06.2010
 *
 * Project: SpeechDatabaseTools
 * Original author: draxler
 */

public class TextGridExporter {

    String [] filenames;
    
    public TextGridExporter(String dirName, float sampleRate, String tierName, BufferedWriter writer) throws IOException {
        File directory = new File(dirName);

        FilenameFilter textGridFilter = new FilenameFilter() {
            public boolean accept(File directory, String name) {
                return name.matches(".*\\..ext.rid");
            }
        };
        
        filenames = directory.list(textGridFilter);
//        filenames = directory.list();
        TextGridFileParser parser = new TextGridFileParser((float) sampleRate);

        for (int i = 0; i < filenames.length; i++) {
            String textGridFileName = directory + "/" + filenames[i];
            File textGridFile = new File(textGridFileName);
            Bundle bundle = parser.parse(textGridFile, Charset.forName("UTF-8"));
            ips.annot.model.db.Level tier = bundle.getTierByName(tierName);
            if (tier != null) {
                for (Item item: tier.getItems()) {
                    
                    if (item.getClass().equals(EventItem.class)) {
                        EventItem ei = (EventItem) item;
                        if (ei.getLabelText().trim().matches(".+")) {
                            writer.write(filenames[i] + "\t" + ei.getSamplepoint() + "\t" + ei.getLabelText().trim() + "\n");
                        }
                    } else if (item.getClass().equals(SegmentItem.class)) {
                        SegmentItem ii = (SegmentItem) item;
                        if (item.getLabelText().trim().matches(".+")) {
                            writer.write(filenames[i] + "\t" + ii.getSampleStart() + "\t" + ii.getSampleDur() + "\t" + ii.getLabelText().trim() + "\n");
                            writer.flush();
                        }
                    }
                }
            }
        }
//            }
    }

    
    
    public static void main(String[] args) {
        String directoryName = args[0];
        float sampleRate = Float.parseFloat(args[1]);
        String tierName = args[2];
        String outfileName = args[3];

        try {
            File outputFile = new File(directoryName + "/" + outfileName);

            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            
            File directory = new File(directoryName);
            if (directory.isDirectory()) {
                File[] files = directory.listFiles();
                for (int i = 0; i < files.length; i++) {
                    File speakerDirectory = files[i];
                    if (speakerDirectory.isDirectory()) {
                        String dirName = speakerDirectory.getAbsolutePath();
                        new TextGridExporter(speakerDirectory.getAbsolutePath(), sampleRate, tierName, writer);
                    }
                }
            }
            
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
