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
 * Date  : Mar 19, 2010
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */
 
package ips.annot.view;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ips.annot.model.AnnotatedAudioClip;
import ips.annot.model.db.Bundle;
import ips.annot.model.db.EventItem;
import ips.annot.model.db.Item;
import ips.annot.model.db.Level;
import ips.annot.model.db.LevelDefinition;
import ips.annot.model.event.BundleChangedEvent;
import ips.annot.model.event.BundleListener;
import ipsk.audio.actions.StartPlaybackAction;
import ipsk.audio.arr.Selection;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.ui.BasicAudioClipUI;
import ipsk.audio.events.StartPlaybackActionEvent;


/**
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class AnnotationActionsAudioClipUI extends BasicAudioClipUI implements BundleListener, MouseListener, MouseMotionListener, PropertyChangeListener{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private static final boolean DEBUG = false;
//    private Tier annotationTier;
    private Bundle bundle;
//    private SegmentsViewer segmentsViewer;
   // private List<Segment> segments;
    private MouseEvent pressedEvent = null;

    private MouseEvent dragStartEvent = null;

    private MouseEvent selEndMoveEvent;

    private MouseEvent selStartMoveEvent;

    private MouseEvent mouseOverResizeWest;

    private MouseEvent mouseOverResizeEast;
    
    private int selectorWidth=5;
    private boolean changeSelectionOnDrag=false;
    private boolean snapToSegments=true;
    
    private int separatorHeight=1;
    
    private List<JLabel> yLabels=new ArrayList<JLabel>();
    
    private JPanel yScalesComponent;
    private StartPlaybackAction startPlaybackAction;

    public String getName(){
        return "Annotation";
    }
    
    public AnnotationActionsAudioClipUI(AnnotatedAudioClip annotatedAudioClip) {
        super();
//        setLayout(new BorderLayout());
//        setLayout(null);
//        addComponentListener(this);
       
//       setAudioSample(annotatedAudioClip);
        setAnnotatedAudioClip(annotatedAudioClip);
       
       addMouseListener(this);
       addMouseMotionListener(this);
       yScalesComponent=new JPanel();
       yScalesComponent.setLayout(null);
    }
    
    public void setAnnotatedAudioClip(AnnotatedAudioClip annotatedAudioClip){
        AudioClip currAudioClip=getAudioSample();
        if(currAudioClip!=null && currAudioClip instanceof AnnotatedAudioClip){
            ((AnnotatedAudioClip)currAudioClip).removeBundleListener(this);
        }
        if(annotatedAudioClip!=null){
            annotatedAudioClip.addBundleListener(this);
        }
       
        super.setAudioSample(annotatedAudioClip);
        if(annotatedAudioClip!=null){
//          setAnnotationTier(annotatedAudioClip.getAnnotation().getTierByName("SAP"));
            Bundle b =annotatedAudioClip.getBundle();
            setBundle(b);
        }
    }
    
//    /* (non-Javadoc)
//     * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
//     */
//    public void componentResized(ComponentEvent arg0) {
//       super.componentResized(arg0);
//       repaint();
//    }
    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
        //removeAll();
        if(bundle != null){
//            segmentsViewer=new SegmentsViewer(annotationTier);
//            add(segmentsViewer,BorderLayout.CENTER);
            List<Level> levels=bundle.getLevels();
//            int lvlsCnt=levels.size();
            yScalesComponent.removeAll();
//            yScalesComponent.setLayout(new GridLayout(lvlsCnt, 1));
            yLabels.clear();
            for(Level level:levels){
                JLabel yLabel=new JLabel(level.getName());
                yLabels.add(yLabel);
                yScalesComponent.add(yLabel);
            }
            doScalesLayout();
        }
        revalidate();
        repaint();
    }
    
   
    public Dimension getPreferredSize(){
        int prefHeight=500;
        int tiersCount=0;
        if(bundle != null){
            List<Level> tiers = (List<Level>) bundle.getLevels();
            tiersCount=tiers.size();
        }
        Font of=getFont();
        if(of!=null){
            Font f=of.deriveFont(Font.BOLD);
            FontMetrics fontMetrics = getFontMetrics(f);
            int fontHeight=fontMetrics.getHeight();
            prefHeight=tiersCount*(fontHeight+4);
            if(tiersCount>1){
                prefHeight+=tiersCount-1 * separatorHeight;
            }
        }
        return new Dimension(0,prefHeight);
    }

    public Dimension getMinimumSize(){
        return getPreferredSize();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getSize().width;
        int h= getSize().height;
        //System.out.println("paint "+w+"x"+h);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, w, h);
        g.setColor(Color.BLACK);

        if(bundle != null){
            List<Level> tiers= (List<Level>) bundle.getLevels();
            
            int tiersCount=tiers.size();
            int availHeight=h;
            if(tiersCount>1){
                // separator space
                availHeight-=tiersCount-1;
            }
            int tierHeight=availHeight/tiersCount;
            Font of=getFont();
            Font f=of.deriveFont(Font.BOLD);
            g.setFont(f);
            FontMetrics fontMetrics = getFontMetrics(f);
            int tierY=0;
            for(int t=0;t<tiersCount;t++){
                int itemXposition=0;
                String lType=LevelDefinition.ITEM;
                Level annotationTier=tiers.get(t);
                LevelDefinition ld=annotationTier.getDefinition();
                if(ld!=null){
                    lType=ld.getType();
                    
                }
                List<Item> itemsList = annotationTier.getItems();
                int itemsCnt=itemsList.size();
                for (int i = 0; i < itemsCnt; i++) {
                	
                    Item item=itemsList.get(i);
                    if(LevelDefinition.SEGMENT.equals(lType)){
//                        SegmentItem segItem=(SegmentItem)item;
                        Item segItem=item;
                        int xl = (int) (mapFrameToPixel(segItem.getSampleStart()));
                        int xr = (int) (mapFrameToPixel(segItem.getSampleStart() + segItem.getSampleDur()));
                        Collection<Object> labelObjs=item.getLabelValues();
                        int labelsWidth=0;
                        int labelsHeight=0;
                        //                    String label = item.getLabelText();
                        List<String> labelList=new ArrayList<String>();
                        for(Object labelObj:labelObjs){
                            String label=labelObj.toString();
                            labelList.add(label);
                            Rectangle2D labelBounds = fontMetrics.getStringBounds(label,g);
                            int labelWidth=(int)labelBounds.getWidth();
                            if(labelsWidth<labelWidth){
                                labelsWidth=labelWidth;
                            }
                            int labelHeight=(int)labelBounds.getHeight();
                            labelsHeight+=labelHeight;
                            labelsHeight+=2;
                        }


                        int segmentWidth=xr - xl;
                    // don't draw the first left boundary - it doesn't look nice
                    if (i > 0) {
                        g.drawLine(xl, tierY, xl, tierY+tierHeight);
                    }

                    int midx = xl + (segmentWidth / 2) - labelsWidth/2;
                    int midy = tierY+ tierHeight / 2 + labelsHeight/2;
                    for(String label:labelList){
                    	g.drawString(label, midx, midy );
                    	Rectangle2D labelBounds = fontMetrics.getStringBounds(label,g);
                    	int lblHeight=(int)labelBounds.getHeight();
                    	midx-=lblHeight;
                    }
                    }else if(LevelDefinition.EVENT.equals(lType)){
                        //                List<EventItem> marks=annotationTier.getMarksList();
                        //                for (int i = 0; i < marks.size(); i++) {
                        EventItem m=(EventItem)item;
                        int x = (int) (mapFrameToPixel(m.getSamplepoint()));

                        //                    String label = m.getLabelText();
                        Collection<Object> labelObjs=item.getLabelValues();
                        Iterator<Object> objsIt=labelObjs.iterator();
                        Object lblObj=objsIt.next();
                        if(lblObj!=null){
                            String label=lblObj.toString();
                            Rectangle2D labelBounds = fontMetrics.getStringBounds(label,g);
                            int labelWidth=(int)labelBounds.getWidth();
                            int labelHeight=(int)labelBounds.getHeight();

                            g.drawLine(x, tierY, x, tierY+tierHeight);

                            int midx = x - labelWidth/2;
                            int midy = tierY + tierHeight / 2 + labelHeight/2;

                            g.drawString(label, midx, midy );
                        }
                    }else{
                        
                        // TODO prepare labels and paint them center aligned after the iteration
                        Collection<Object> labelObjs=item.getLabelValues();
                        Iterator<Object> objsIt=labelObjs.iterator();
                        Object lblObj=objsIt.next();
                        if(lblObj!=null){
                            String label=lblObj.toString();
                            Rectangle2D labelBounds = fontMetrics.getStringBounds(label,g);
                            int labelWidth=(int)labelBounds.getWidth();
                            int labelHeight=(int)labelBounds.getHeight();

                            g.drawLine(itemXposition, tierY, itemXposition, tierY+tierHeight);

                         
                            int midy = tierY + tierHeight / 2 + labelHeight/2;

                            g.drawString(label,itemXposition, midy );
                            itemXposition+=labelWidth;
                            
                            // paint vertical separator lines
                            if(i<itemsCnt-1){
                                g.drawLine(itemXposition, tierY, itemXposition, tierY+tierHeight);
                            }
                            
                        }
                    }
                    
                
                }
                tierY+=tierHeight;
                if(t<tiersCount-1){
                    g.drawLine(0, tierY, w, tierY);
                    tierY++;
                }
            }
        }
    }

    public void bundleChanged(BundleChangedEvent event) {
        AudioClip currAudioClip=getAudioSample();
        Bundle b = null;
        if(currAudioClip!=null && currAudioClip instanceof AnnotatedAudioClip){
            b = ((AnnotatedAudioClip)currAudioClip).getBundle();
            
        }
        setBundle(b);
        revalidate();
        repaint();
        
    }
    
    /* (non-Javadoc)
     * @see ipsk.audio.arr.clip.ui.AudioClipUI#isPreferredFixedHeight()
     */
    public boolean isPreferredFixedHeight() {
        return true;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    public void mouseClicked(MouseEvent arg0) {
        long samplePosition = mapPixelToFrame(arg0.getX());
//        audioSample.setFramePosition(newSamplePosition);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    public void mouseEntered(MouseEvent arg0) {

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    public void mouseExited(MouseEvent arg0) {

    }

    
    private boolean isInSelectorStart(int x){
        int xStart=viewSelection.getXStart();
        return( x >= xStart-selectorWidth && x<=xStart+selectorWidth);
    }

    private boolean isInSelectorEnd(int x){
        int xEnd=viewSelection.getXEnd();
        return( x >= xEnd-selectorWidth && x<=xEnd+selectorWidth);
    }
    
    private boolean isInSelectorLeft(int x){
        int xLeft=viewSelection.getXLeft();
        return( x >= xLeft-selectorWidth && x<=xLeft+selectorWidth);
    }
    
    private boolean isInSelectorRight(int x){
        int xRight=viewSelection.getXRight();
        return( x >= xRight-selectorWidth && x<=xRight+selectorWidth);
    }
    
    private Level tierAtViewPosition(int y){
        Level tierAtViewPos=null;
        if(bundle != null){
            List<Level> tiers= (List<Level>) bundle.getLevels();
            int tiersCount=tiers.size();
            int h=getHeight();
            int tierandSepHeight=h/tiersCount;
            int tierIndx=y/tierandSepHeight;
            if(tierIndx>=tiersCount){
                tierIndx=tiersCount-1;
            }
            tierAtViewPos=tiers.get(tierIndx);
        }
        return tierAtViewPos;
    }
    
    private Item segmentAtPosition(Level selTier,long pos,boolean leftAligned){
        if(bundle == null) return null;
        List<Item> items = selTier.getItems();
//        List<Segment> segsAtPos=new ArrayList<Segment>();
        for(Item item:items){
//            SegmentItem intervalItem = (SegmentItem) item;
            Item intervalItem=item;
            long sBeg=intervalItem.getSampleStart();
            long sEnd=sBeg + intervalItem.getSampleDur();
            if(leftAligned){
                if(pos>=sBeg && pos<sEnd){
                    return intervalItem;
                }
            }else{
                if(pos>sBeg && pos<=sEnd){
                    return intervalItem;
                }
            }
        }
        return null;
    }
    
    
    
    private void selectionByMouse(MouseEvent me){
        if (dragStartEvent != null) {

            if (viewSelection != null) {
                viewSelection.limitTo(0, length);
                Selection sel=viewSelection.getSelection();
                if(snapToSegments){
                    // determine tier
                    Level selTier=tierAtViewPosition(me.getY());
                    if(selTier!=null){
                    long selLeft=sel.getLeft();
                    Item leftSeg=segmentAtPosition(selTier,selLeft,true);
                    long selRight=sel.getRight();
                    Item rightSeg=segmentAtPosition(selTier,selRight,false);
                    if(leftSeg!=null && rightSeg !=null){
                      
                       sel=new Selection(leftSeg.getSampleStart(), (rightSeg.getSampleStart() + rightSeg.getSampleDur()));
                       
                    }else{
                        // no match
                        sel=null;
                    }
                    }
                }
                audioSample.setSelection(sel);
                if (DEBUG)
                    System.out.println(viewSelection.getStart() + " - "
                            + viewSelection.getEnd() + " l: "
                            + viewSelection.getLength());
                
                repaint();
            }
            checkMouseResizeSelection(me);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    public void mousePressed(MouseEvent arg0) {

        dragStartEvent = null;
        pressedEvent = null;
        selStartMoveEvent = null;
        selEndMoveEvent = null;

        if (arg0.isPopupTrigger()) {
            getParent().dispatchEvent(arg0);
        } else {
            int x = arg0.getX();
            if (viewSelection != null) {
                if (isInSelectorStart(x)) {
                    selStartMoveEvent = arg0;
                } else if (isInSelectorEnd(x)) {
                    selEndMoveEvent = arg0;
                } else {
                    pressedEvent = arg0;
                }
                repaint();
            } else {
                pressedEvent = arg0;
            }
            //repaint();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    public void mouseReleased(MouseEvent arg0) {
        if (arg0.isPopupTrigger()) {
            getParent().dispatchEvent(arg0);
        } else {
            selectionByMouse(arg0);
        }
        dragStartEvent = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public synchronized void mouseDragged(MouseEvent arg0) {
        // TODO repaint only necessary areas
        if (pressedEvent != null) {
            dragStartEvent = pressedEvent;
            if (viewSelection == null) {
                viewSelection = new ViewSelection();
            }

            viewSelection.setXStart(dragStartEvent.getX());
            viewSelection.setXEnd(arg0.getX());

            repaint();
        } else if (selStartMoveEvent != null) {
            dragStartEvent = selStartMoveEvent;
            viewSelection.setXStart(arg0.getX());
            setCursor();
            repaint();
        } else if (selEndMoveEvent != null) {
            dragStartEvent = selEndMoveEvent;
            viewSelection.setXEnd(arg0.getX());
            setCursor();
            repaint();
        }
        if(changeSelectionOnDrag){
            selectionByMouse(arg0);
        }
    }

    
    private void checkMouseResizeSelection(MouseEvent arg0){
        int x = arg0.getX();
        if (viewSelection != null) {
            if (isInSelectorLeft(x)) {
                mouseOverResizeEast = null;
                mouseOverResizeWest = arg0;
            } else if (isInSelectorRight(x)) {
                mouseOverResizeEast = arg0;
                mouseOverResizeWest = null;
            } else {
                mouseOverResizeEast = null;
                mouseOverResizeWest = null;

            }
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(MouseEvent arg0) {
        checkMouseResizeSelection(arg0);
        setCursor();

    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        StartPlaybackActionEvent pae = null;
       
        pae = new StartPlaybackActionEvent(this, selection.getLeft(),
                    selection.getRight());
        
        if (pae != null)
            startPlaybackAction.actionPerformed(pae);
    }

    /**
     * @param startPlaybackAction
     */
    public void setStartPlaybackAction(StartPlaybackAction startPlaybackAction) {
        this.startPlaybackAction = startPlaybackAction;
        startPlaybackAction.addPropertyChangeListener(this);

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        boolean enabled = ((Action) evt.getSource()).isEnabled();
       

    }

    private void setCursor() {
        if (dragStartEvent != null) {
            if (dragStartEvent == selStartMoveEvent) {
                setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
            } else if (dragStartEvent == selStartMoveEvent) {
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                //System.out.println("drag");
            }
        } else if (mouseOverResizeWest != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
            //System.out.println("west");
        } else if (mouseOverResizeEast != null) {
            setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            //System.out.println("east");
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    private void doScalesLayout(){
        int compH=getSize().height;
        int borderLength=2;
        int yLblCnt=yLabels.size();
        if(yLblCnt>0){

            int scaleHeight=(compH-2*borderLength)/yLblCnt;
            
            // compute scale width
            int scaleWidth=0;
            for(int i=0;i<yLblCnt;i++){
                JLabel yLabel=yLabels.get(i);
                if(yLabel!=null){
                   
                    Dimension scalePrefSize=yLabel.getPreferredSize();
                    int sW=scalePrefSize.width;
                    if (sW>scaleWidth){
                        scaleWidth=sW;
                    }
                }
            }
            
            for(int i=0;i<yLblCnt;i++){
                JLabel yLabel=yLabels.get(i);
                if(yLabel!=null){
                    int sYPos=borderLength+scaleHeight*i;
                    Dimension scalePrefSize=yLabel.getPreferredSize();
                    int sW=scalePrefSize.width;
                    // right aligned 
                    yLabel.setBounds(scaleWidth-sW,sYPos,sW,scaleHeight);
                    yLabel.doLayout();
                }
            }
            Dimension preferredSize=new Dimension(scaleWidth,compH);
            yScalesComponent.setPreferredSize(preferredSize);
        }else{
            Dimension preferredSize=new Dimension(0,compH);
            yScalesComponent.setPreferredSize(preferredSize);
        }
        yScalesComponent.doLayout();
        yScalesComponent.repaint();
    }
    

    public void doLayout(){
      super.doLayout();
      doScalesLayout();
    }
    
    public JComponent[] getYScales() {
        return new JComponent[] { yScalesComponent };
        
    }
}
