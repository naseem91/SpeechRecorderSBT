/*
 * Date  : 05.11.2015
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ips.annot.model.emu;

import java.io.Console;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ips.annot.model.db.Bundle;
import ips.annot.model.db.Item;
import ips.annot.model.db.Level;
import ips.annot.model.db.Link;
import ipsk.text.ParserException;

/**
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class LoadTest {

    public class QRSeq {
        private Item startItem;
        private Item stopItem;

        public QRSeq(Item startItem, Item stopItem) {
            super();
            this.startItem = startItem;
            this.stopItem = stopItem;
        }
        
        public Item getStartItem() {
            return startItem;
        }

        public Item getStopItem() {
            return stopItem;
        }

        public String toString() {
            return new String("Sequence: " + startItem + " -> " + stopItem);
        }
    }

    public LoadTest() {
        super();
    }

    public List<Bundle> load(File emuDbDir) throws IOException, ParserException {
        long startTime = System.currentTimeMillis();
        List<Bundle> bundles = new ArrayList<Bundle>();
        EmuBundleAnnotationPersistor ebap = new EmuBundleAnnotationPersistor();
        int sCnt = 0;
        File[] sessDirs = emuDbDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.endsWith("_ses"));
            }
        });
        for (File sessDir : sessDirs) {
            sCnt++;
            System.out.println(sCnt + ": " + sessDir);
            File[] bundleDirs = sessDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return (name.endsWith("_bndl"));
                }
            });
            for (File bundleDir : bundleDirs) {
                File[] annotFiles = bundleDir.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return (name.endsWith("_annot.json"));
                    }
                });

                ebap.setFile(annotFiles[0]);
                Bundle b = ebap.load();
                bundles.add(b);
//                return(bundles);
            }
        }
        long endTime = System.currentTimeMillis();
        double durMs = endTime - startTime;
        System.out
                .println("Loaded " + bundles.size() + " annot files.(links resolved) in " + durMs / 1000L + " seconds");
        return (bundles);
    }

    public boolean dominatedBy(Item dominator, Item dominated) {
        Set<Item> dominatorToIts = dominator.getToItems();
        if (dominatorToIts.contains(dominated)) {
            return true;
        } else {
            for (Item toIt : dominatorToIts) {
                // recursion
                boolean rd = dominatedBy(toIt, dominated);
                if (rd) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean domination(Item item1, Item item2) {
        boolean dominates = dominatedBy(item1, item2);
        boolean dominated = dominatedBy(item2, item1);
        return (dominates || dominated);
    }

    public List<QRSeq> domination(QRSeq leftSeq, List<QRSeq> rightSeqs) {
        List<QRSeq> res = new ArrayList<QRSeq>();
        for (QRSeq rSeq : rightSeqs) {
            Item leftStartIt = leftSeq.getStartItem();
            Item rightStartIt = rSeq.getStartItem();
            boolean startDomination = domination(leftStartIt, rightStartIt);

            Item leftStopIt = leftSeq.getStopItem();
            Item rightStopIt = rSeq.getStopItem();
            boolean stopDomination = domination(leftStopIt, rightStopIt);
            if (startDomination && stopDomination) {
                res.add(leftSeq);
                break;
            }
        }
        return res;
    }

    public QRSeq sequenceQuery(QRSeq leftSeq, QRSeq rightSeq) {

        Item leftStartIt = leftSeq.getStartItem();
        Item rightStartIt = rightSeq.getStartItem();
        Item leftStopIt = leftSeq.getStopItem();
        Item rightStopIt = rightSeq.getStopItem();
        Level leftLvl = leftStartIt.getLevel();
        Level rightLvl = rightStartIt.getLevel();
        if (leftLvl.equals(rightLvl)) {
            Bundle leftB = leftLvl.getBundle();
            Bundle rightB = rightLvl.getBundle();
            if (leftB.equals(rightB)) {
                Integer leftStopPos = leftStopIt.getPosition();
                Integer rightStartPos = rightStartIt.getPosition();
                if (leftStopPos != null && rightStartPos != null) {
                    if (rightStartPos == leftStopPos + 1) {
                        return new QRSeq(leftStartIt, rightStopIt);
                    }
                } else {
                    System.err.println("Position of item not set!!");
                }
            } else {
                System.out.println("Bundles do not match!");
            }
        }
        return null;
    }

    public List<QRSeq> sequenceQuery(QRSeq leftSeq, List<QRSeq> rightSeqs) {
        List<QRSeq> res = new ArrayList<QRSeq>();
        for (QRSeq rSeq : rightSeqs) {
            QRSeq resSeq = sequenceQuery(leftSeq, rSeq);
            if (resSeq != null) {
                res.add(resSeq);
            }
        }
        return res;
    }

    public Map<Bundle, List<QRSeq>> domination(Map<Bundle, List<QRSeq>> leftSeqs, Map<Bundle, List<QRSeq>> rightSeqs) {
        Map<Bundle, List<QRSeq>> res = new HashMap<Bundle, List<QRSeq>>();
        for (Bundle b : leftSeqs.keySet()) {
            List<QRSeq> bResSeqs = new ArrayList<QRSeq>();
            List<QRSeq> leftBSeqs = leftSeqs.get(b);
            List<QRSeq> rightBSeqs = rightSeqs.get(b);
            for (QRSeq leftSeq : leftBSeqs) {
                List<QRSeq> r = domination(leftSeq, rightBSeqs);
                bResSeqs.addAll(r);
            }
            res.put(b, bResSeqs);
            int lSize=leftBSeqs.size();
            int rSize=bResSeqs.size();
//            if(lSize!=rSize){
//                System.out.println(lSize+" != "+rSize);
//            }
        }
        return res;
    }

    public Map<Bundle,List<QRSeq>> sequenceQuery(Map<Bundle,List<QRSeq>> leftSeqs, Map<Bundle,List<QRSeq>> rightSeqs) {
        Map<Bundle, List<QRSeq>> res = new HashMap<Bundle, List<QRSeq>>();
        for (Bundle b : leftSeqs.keySet()) {
            List<QRSeq> bResSeqs = new ArrayList<QRSeq>();
            List<QRSeq> leftBSeqs = leftSeqs.get(b);
            List<QRSeq> rightBSeqs = rightSeqs.get(b);
            for (QRSeq leftSeq : leftBSeqs) {
                List<QRSeq> r = sequenceQuery(leftSeq, rightBSeqs);
                bResSeqs.addAll(r);
            }
            res.put(b,bResSeqs);
        }
        return res;
    }

    public Map<Bundle, List<QRSeq>> queryLabel(List<Bundle> bundles, String level, String label) {
        Map<Bundle, List<QRSeq>> res = new HashMap<>();
        for (Bundle b : bundles) {
            List<QRSeq> bResSeqs = new ArrayList<QRSeq>();
            List<Level> bLvls = b.getLevels();
            for (Level bl : bLvls) {
                if (level.equals(bl.getName())) {
                    List<Item> items = bl.getItems();

                    for (int i = 0; i < items.size(); i++) {
                        Item it = items.get(i);
                        Map<String, Object> attrs = it.getLabels();
                        Object attr = attrs.get(level);
                        if (attr instanceof String) {
                            String strAttr = (String) attr;
                            if (label == null || strAttr.equals(label)) {
                                QRSeq qrSeq = new QRSeq(it, it);
                                bResSeqs.add(qrSeq);
                            }
                        }
                    }

                }
            }
            res.put(b, bResSeqs);
        }
        return res;
    }

    public Map<Bundle, List<QRSeq>> queryLabelRegex(List<Bundle> bundles, String level, String labelRegex,boolean searchAttributes) {
        Map<Bundle, List<QRSeq>> res = new HashMap<Bundle, List<QRSeq>>();
        Pattern p = Pattern.compile(labelRegex);

        for (Bundle b : bundles) {
            List<QRSeq> bResSeqs = new ArrayList<QRSeq>();
            List<Level> bLvls = b.getLevels();
            for (Level bl : bLvls) {
               
                if (level.equals(bl.getName()) || searchAttributes) {
                    List<Item> items = bl.getItems();

                    for (int i = 0; i < items.size(); i++) {
                        Item it = items.get(i);

                        Map<String, Object> attrs = it.getLabels();
                        Object attr = attrs.get(level);
                        if (attr instanceof String) {
                            String strAttr = (String) attr;
                            Matcher m = p.matcher(strAttr);
                            if (m.matches()) {
                                QRSeq qrSeq = new QRSeq(it, it);
                                bResSeqs.add(qrSeq);
                            }
                        }
                    }

                }
            }
            res.put(b, bResSeqs);
        }
        return res;
    }

    private int intermediateResultSize(Map<Bundle, List<QRSeq>> r) {
        int size = 0;
        for (List<QRSeq> seqs : r.values()) {
            size += seqs.size();
        }
        return size;
    }
    private void printSeglist(Map<Bundle,List<QRSeq>> seglist){
        for (List<QRSeq> lr : seglist.values()) {
            for (QRSeq r : lr) {
                System.out.println(r);
            }
        }
    }

    public Map<Bundle, List<QRSeq>> testQuery(List<Bundle> bundles) {
        List<Bundle> qbundles = new ArrayList<Bundle>();
//         qbundles.add(bundles.get(0));
        qbundles = bundles;
        Map<Bundle, List<QRSeq>> leftSeqSs = queryLabel(qbundles, "phonetic", "n");
//        Map<Bundle,List<QRSeq>> leftSeqTs=queryLabelRegex(qbundles, "SAP", ".*");
//        Map<Bundle,List<QRSeq>> seTs=sequenceQuery(leftSeqSs, leftSeqTs);
        Map<Bundle, List<QRSeq>> rightSeqsAll = queryLabelRegex(qbundles, "sex", "556",true);
//        Map<Bundle, List<QRSeq>> leftSeqSs=queryLabel(qbundles, "ORT", null);
        // List<QRSeq> rightSeqsAll=queryLabel(bundles, "SAP", null);
        // List<QRSeq> leftSeqSs=queryLabel(qbundles, "Phonetic", "p");
        // List<QRSeq> rightSeqsAll=queryLabel(bundles, "Word", "C");
        System.out.println(intermediateResultSize(leftSeqSs) + " -> " + intermediateResultSize(rightSeqsAll));
//        Map<Bundle, List<QRSeq>> domQ=domination(seTs, rightSeqsAll);
        Map<Bundle, List<QRSeq>> domQ = domination(leftSeqSs, rightSeqsAll);
        return domQ;
        // return seTs;
    }

    public static void main(String[] args) {

        String emuDbDirNm = args[0];
        File emuDbDir = new File(emuDbDirNm);
        LoadTest lt = new LoadTest();
        List<Bundle> bundleList=null;
        try {
            bundleList = lt.load(emuDbDir);
        } catch (IOException e1) {
            e1.printStackTrace();
            return;
        } catch (ParserException e1) {
            e1.printStackTrace();
            return;
        }
        long startTimeMs = System.currentTimeMillis();
        Map<Bundle, List<QRSeq>> res = lt.testQuery(bundleList);
        int segListSize = lt.intermediateResultSize(res);
//        lt.printSeglist(res);

        System.out.println("Found " + segListSize + " items in " + (System.currentTimeMillis() - startTimeMs) + " ms");

//        Console c = System.console();
//        if (c != null) {
//            PrintWriter cpw = c.writer();
//            printMemusage(cpw);
//            cpw.println("Press return to force gc");
//            c.readLine();
//            System.gc();
//            try {
//                Thread.sleep(4000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            printMemusage(cpw);
//            cpw.println("Press return to exit");
//            c.readLine();
//        }
    }

    public static void printMemusage(PrintWriter pw) {
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: " + format.format(freeMemory / 1024) + "\n");
        sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
        sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
        sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
        pw.println(sb);
    }

}
