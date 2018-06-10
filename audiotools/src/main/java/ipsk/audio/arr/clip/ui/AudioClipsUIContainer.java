//    IPS Java Audio Tools
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of IPS Java Audio Tools
//
//
//    IPS Java Audio Tools is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    IPS Java Audio Tools is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with IPS Java Audio Tools.  If not, see <http://www.gnu.org/licenses/>.

package ipsk.audio.arr.clip.ui;

import ips.media.MediaLengthUnit;
import ips.media.MediaView;
import ipsk.audio.AudioFormatNotSupportedException;
import ipsk.audio.AudioSource;
import ipsk.audio.AudioSourceException;
import ipsk.audio.ThreadSafeAudioSystem;
import ipsk.audio.arr.Selection;
import ipsk.audio.arr.clip.AudioClip;
import ipsk.audio.arr.clip.AudioClipListener;
import ipsk.audio.arr.clip.events.AudioClipChangedEvent;
import ipsk.audio.arr.clip.events.AudioSourceChangedEvent;
import ipsk.audio.arr.clip.events.SelectionChangedEvent;
import ipsk.audio.utils.AudioFormatUtils;
import ipsk.swing.JAutoScale;
import ipsk.swing.JMultiSplitPane;
import ipsk.swing.action.tree.AbstractActionLeaf;
import ipsk.swing.action.tree.ActionFolder;
import ipsk.swing.action.tree.ActionGroup;
import ipsk.swing.action.tree.ActionProvider;
import ipsk.swing.action.tree.ActionTreeRoot;
import ipsk.swing.action.tree.CheckActionLeaf;
import ipsk.text.TimeFormat;
import ipsk.util.LocalizableMessage;

import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.text.Format;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.Scrollable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;


/**
 * Audio clips list view.
 * Status: Incubation. 
 */
public class AudioClipsUIContainer extends JPanel implements Scrollable,
		AudioClipListener,ComponentListener,ActionProvider,MediaView {

	private final static boolean DEBUG = false;

//	private final static double DEFAULT_XZOOM = 400;
//
//	private final static int SEPERATOR_HEIGHT = 5;
	

	class PopupListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			maybeShowPopup(e);
		}

		public void mouseReleased(MouseEvent e) {
			maybeShowPopup(e);
		}

		private void maybeShowPopup(MouseEvent e) {
			// getParent().dispatchEvent(e);
			if (e.isPopupTrigger()) {
				popup.show(e.getComponent(), e.getX(), e.getY());
			} else {
				getParent().dispatchEvent(e);
			}
		}
	}

	public class Plugin{
		private TogglePluginAction togglePluginAction;
		private ShowPluginControlComponentAction showPluginControlComponentAction;
		private AudioClipUI clipUI;

		public Plugin(AudioClipUI clipUI,JComponent container){
			this.clipUI=clipUI;
			togglePluginAction=new TogglePluginAction(this,clipUI.getLocalizableName());
			if(clipUI.hasControlDialog()){
			    LocalizableMessage lm=new LocalizableMessage(clipUI.getLocalizableName().localize()+" control...");
			    showPluginControlComponentAction=new ShowPluginControlComponentAction(container,this,lm);
			}
		}
		
		public AudioClipUI getClipUI() {
			return clipUI;
		}
		public boolean isVisible() {
			return clipUI.asComponent().isVisible();
		}
		public void setVisible(boolean visible) {
		    clipUI.asComponent().setVisible(visible);
			if(togglePluginAction!=null){
			    togglePluginAction.setSelected(visible);
			}
		}
        public TogglePluginAction getTogglePluginAction() {
            return togglePluginAction;
        }
        public ShowPluginControlComponentAction getShowPluginControlComponentAction() {
            return showPluginControlComponentAction;
        }
	}

	protected double xZoom; // pixels per second

    private Vector<ActionListener> actionListener;

	private JPopupMenu popup;

	private boolean fixXZoomFitToPanel = true;

	private Container parent = null;
	private boolean internalChange=false;
	
//	private double lengthInSeconds;
	
	private double maxLengthInseconds;

	private List<AudioClip> audioClips;

	private long length;

	private AudioFormat audioFormat;

	private Vector<Plugin> plugins = new Vector<Plugin>();
	
	private List<AudioSignalUI> signalUis=new ArrayList<AudioSignalUI>();

	private int preferredWidth = 0;

	private JAutoScale xScale;

//	private JMultiSplitPane defaultPane;
	
	private MediaLengthUnit mediaLengthUnit=MediaLengthUnit.TIME;
	private Format timeFormat=TimeFormat.FIXED_SECONDS_MS_TIME_FORMAT;
	
	private ActionTreeRoot actionRoot=new ActionTreeRoot();
	
	public class XZoomInAction extends AbstractActionLeaf{
	    /**
	     * @param displayName
	     */
	    public XZoomInAction(LocalizableMessage displayName) {
	        super(displayName);
	    }

	    /* (non-Javadoc)
	     * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
	     */
	    @Override
	    public void actionPerformed(ActionEvent arg0) {
	        setFixXZoomFitToPanel(false);
	        setXZoom(getXZoom() * 2);
//	        repaint();
	    }
	}
	
	private XZoomInAction xZoomInAction;
	
	public class XZoomOutAction extends AbstractActionLeaf{
        /**
         * @param displayName
         */
        public XZoomOutAction(LocalizableMessage displayName) {
            super(displayName);
        }

        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            setFixXZoomFitToPanel(false);
            setXZoom(getXZoom() / 2);
//            repaint();
        }
    }
	private XZoomOutAction xZoomOutAction;
	
	public class XZoomFitToPanelAction extends AbstractActionLeaf{
        /**
         * @param displayName
         */
        public XZoomFitToPanelAction(LocalizableMessage displayName) {
            super(displayName);
        }

        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            setFixXZoomFitToPanel(false);
            xZoomFitToPanel();
        }
    }
    private XZoomFitToPanelAction xZoomFitToPanelAction;
	
    public class XZoomOnePixelPerSampleAction extends AbstractActionLeaf{
        /**
         * @param displayName
         */
        public XZoomOnePixelPerSampleAction(LocalizableMessage displayName) {
            super(displayName);
        }

        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            xZoomOnePixelPerSample();
        }
    }
    private XZoomOnePixelPerSampleAction xZoomOnePixelPerSampleAction;
    
    public class XZoomToSelectionAction extends AbstractActionLeaf{
        /**
         * @param displayName
         */
        public XZoomToSelectionAction(LocalizableMessage displayName) {
            super(displayName);
        }

        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
//            xZoomToSelection();
        }
    }
    private XZoomToSelectionAction xZoomToSelectionAction;
    
    public class XZoomFixFitToPanelAction extends CheckActionLeaf{

        public XZoomFixFitToPanelAction(LocalizableMessage displayMessage){
           super(displayMessage);
        }
        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
        
           boolean sel=isSelected();
           setFixXZoomFitToPanel(sel);
        }
        
    }
    private XZoomFixFitToPanelAction xZoomFixFitToPanelAction;
    
	public class ToggleXScaleVisibilityAction extends CheckActionLeaf{

	    public ToggleXScaleVisibilityAction(){
	        super(new LocalizableMessage("Time scale"));
	    }
        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            //System.out.println("X scale "+arg0);
           setXScaleVisible(isSelected());
        }
	    
	}
	
	public class TogglePluginAction extends CheckActionLeaf implements ComponentListener{

	    private Plugin plugin;
        public TogglePluginAction(Plugin plugin,LocalizableMessage displayMessage){
           super(displayMessage);
           this.plugin=plugin;
           this.plugin.getClipUI().asComponent().addComponentListener(this);
        }
        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
            plugin.setVisible(isSelected());
        }

        /* (non-Javadoc)
         * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
         */
        public void componentHidden(ComponentEvent e) {
           setSelected(false);
        }
        /* (non-Javadoc)
         * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
         */
        public void componentMoved(ComponentEvent e) {
        }
        /* (non-Javadoc)
         * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
         */
        public void componentResized(ComponentEvent e) {
        }
        /* (non-Javadoc)
         * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
         */
        public void componentShown(ComponentEvent e) {
            setSelected(true);
            
        }
        
    }
	
	public class ShowPluginControlComponentAction extends AbstractActionLeaf{

        private Plugin plugin;
        public ShowPluginControlComponentAction(Component parent,Plugin plugin,LocalizableMessage displayMessage){
           super(displayMessage);
           this.plugin=plugin;
        }
        /* (non-Javadoc)
         * @see ips.incubator.awt.action.ActionLeaf#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent arg0) {
//           JComponent ctrlC=plugin.getClipUI().getControlJComponent();
////           JDialogPanel jdp=new JDialogPanel(JDialogPanel.Options.OK_APPLY_CANCEL);
////           jdp.add(ctrlC);
////           Object ret=jdp.showDialog((JDialog)null);
////           if(ret.equals(JDialogPanel.OK_OPTION)){
////               
////           }
//           JFrame ctrlFrame=new JFrame();
//           ctrlFrame.getContentPane().add(ctrlC);
//           ctrlFrame.setVisible(true);
//           ctrlFrame.pack();
            plugin.getClipUI().showJControlDialog(parent);
        }
        
    }
   
	public class YScalesPanel extends JPanel{
		
		public YScalesPanel(){
			super(null);
		}
	public Dimension getMinimumSize(){
		int maxWidth=0;
		int maxY=0;
		Rectangle r=new Rectangle();
		Component[] comps=getComponents();
		for(Component c:comps){
			Rectangle b=c.getBounds();
			if(b.width>maxWidth){
				maxWidth=b.width;
			}
			int cMaxY=b.y+b.height;
			if(cMaxY>maxY){
				maxY=cMaxY;
			}
		}
		
		Dimension minSize=new Dimension(maxWidth,maxY);
		return minSize;
		
	}
		public Dimension getPreferredSize(){
			return getMinimumSize();
		}
		
		public void doLayout(){
			int minWidth=0;
			Component[] comps=getComponents();
			// calc width
			for(Component c:comps){
			    
				int cWidth=0;
				if(c.isVisible()){
//				    c.getWidth();
				    cWidth=c.getPreferredSize().width;
				}
				if(cWidth>minWidth){
					minWidth=cWidth;
				}
			}
			// right align
			for(Component c:comps){
				int cWidth=c.getWidth();
				Point l=c.getLocation();
				int alignedX=minWidth-cWidth;
				int w=0;
				int h=0;
				if(c.isVisible()){
				    Dimension ps=c.getPreferredSize();
				    w=ps.width;
				    h=ps.height;
				}
//				c.setLocation(alignedX, l.y);
				c.setBounds(alignedX,l.y,w,h);
			}
		}
	}
	private YScalesPanel yScalesComponent;

	public JPanel getyScalesComponent() {
		return yScalesComponent;
	}

    private ActionFolder actionViewFolder;

	private ToggleXScaleVisibilityAction toggleXScaleVisibilityAction=new ToggleXScaleVisibilityAction();
    

	public class ScrollToFramePositionRunnable implements Runnable{
	    private long framePosition;

        public ScrollToFramePositionRunnable(long framePosition) {
            super();
            this.framePosition = framePosition;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            if(DEBUG)System.out.println("ScrollFramePositionRunnable.run() "+framePosition);
            int newVisibleX=mapFrameToPixel(framePosition);
            Rectangle currentVisible=new Rectangle();
            computeVisibleRect(currentVisible);
            if(newVisibleX<0)newVisibleX=0;
            Rectangle newVisible=new Rectangle(newVisibleX,currentVisible.y,currentVisible.width,currentVisible.height);
            scrollRectToVisible(newVisible);
        }
	}
	
	public AudioClipsUIContainer() {
		super(null);
	
		String packageName = getClass().getPackage().getName();
		String resBundleName=packageName + ".ResBundle";
//		rb = ResourceBundle.getBundle(resBundleName);

        actionListener = new Vector<ActionListener>();

       // pixelPosition = 0;

		popup = new JPopupMenu();
      
		
		actionViewFolder = ActionFolder.buildTopLevelFolder(ActionFolder.VIEW_FOLDER_KEY);
		actionRoot.getChildren().add(actionViewFolder);
		
		
		ActionGroup zoomGroup=new ActionGroup(ActionGroup.ZOOM);
		ActionFolder xZoomSubMenuAF=new ActionFolder("xZoom", new LocalizableMessage("X-Zoom"));
		zoomGroup.add(xZoomSubMenuAF);
		actionViewFolder.add(zoomGroup);
		
		xZoomInAction=new XZoomInAction(new LocalizableMessage(resBundleName, "in"));
		xZoomSubMenuAF.getChildren().add(xZoomInAction);
		
		xZoomOutAction=new XZoomOutAction(new LocalizableMessage(resBundleName, "out"));
        xZoomSubMenuAF.getChildren().add(xZoomOutAction);
        
		xZoomFitToPanelAction=new XZoomFitToPanelAction(new LocalizableMessage(resBundleName, "fit_to_panel"));
		xZoomSubMenuAF.getChildren().add(xZoomFitToPanelAction);
		
		xZoomOnePixelPerSampleAction=new XZoomOnePixelPerSampleAction(new LocalizableMessage(resBundleName, "one_pixel_per_sample"));
		xZoomSubMenuAF.getChildren().add(xZoomOnePixelPerSampleAction);
		
		xZoomToSelectionAction=new XZoomToSelectionAction(new LocalizableMessage(resBundleName, "zoom_selection"));
		xZoomSubMenuAF.getChildren().add(xZoomToSelectionAction);
		
		xZoomFixFitToPanelAction=new XZoomFixFitToPanelAction(new LocalizableMessage("Fix fit to panel"));
		xZoomSubMenuAF.getChildren().add(xZoomFixFitToPanelAction);
		
//		defaultPane = new JMultiSplitPane();
//		super.add(defaultPane);
		
		setFixXZoomFitToPanel(true);
		
		yScalesComponent=new YScalesPanel();
		yScalesComponent.setLayout(null);
	}

//	public AudioClipsUIContainer(AudioClip audioSample)
//			throws AudioFormatNotSupportedException, AudioSourceException {
//		this();
//		// Preferences prefs=Preferences.userNodeForPackage(getClass());
//		// prefs.getInt("View.TimeFormat",0);
//		setAudioClip(audioSample);
//
//	}

//	public Component add(Component pluginUI) {
//
//	    if(pluginUI instanceof JAutoScale){
//	        super.add((Component)pluginUI);
//	        xScale=(JAutoScale)pluginUI;
//	        if(pluginUI instanceof AudioClipUI){
//	            AudioClipUI acui=(AudioClipUI) pluginUI;
//	            acui.setMediaLengthUnit(mediaLengthUnit);
//	            acui.setTimeFormat(timeFormat);
//	        }
//	        xScale.setFont(xScale.getFont().deriveFont((float)10.0));
//	        for(Plugin p:plugins){
//	            p.getClipUI().setTimeScaleTickProvider(xScale);
//	        }
//	        actionViewFolder.getChildren().add(toggleXScaleVisibilityAction);
//	    }else if (pluginUI instanceof FragmentActionBarUI) {
//	        super.add((Component)pluginUI);
//	        AudioClipUI acui=(AudioClipUI) pluginUI;
//	        Plugin plugin=new Plugin(acui,this);
//	        plugins.add(plugin);
//	        TogglePluginAction tpa=plugin.getTogglePluginAction();
//	        tpa.setSelected(true);
//
//	        actionViewFolder.add(tpa);
//	    }else if (pluginUI instanceof AudioClipUI) {
//	        AudioClipUI acui=(AudioClipUI) pluginUI;
//	        Plugin plugin=new Plugin(acui,this);
//	        plugins.add(plugin);
//	        acui.setAudioSample(audioClip);
//	        acui.setMediaLengthUnit(mediaLengthUnit);
//	        acui.setTimeFormat(timeFormat);
//	        if(xScale!=null){
//	            acui.setTimeScaleTickProvider(xScale);
//	        }
//	        JComponent[] yScales=acui.getYScales();
//	        if(yScales!=null){
//	            for(JComponent yScale:yScales){
//	                if(yScale!=null){
//	                    yScalesComponent.add(yScale);
//	                    yScale.addComponentListener(this);
//	                    pluginUI.addComponentListener(this);
//	                }
//	            }
//	        }
//
//	        if(acui.isPreferredFixedHeight()){
//	            super.add((Component)pluginUI);
//	        }else{
//	            defaultPane.add(pluginUI);
//	        }
//	        TogglePluginAction tpa=plugin.getTogglePluginAction();
//	        tpa.setSelected(true);
//	        actionViewFolder.add(tpa);
//	    }else{
//	        super.add((Component)pluginUI);
//	    }
//
//	    return pluginUI;
//	}

	
	public void remove(Component plugin) {
		for(int i=0;i<plugins.size();i++){
			Plugin p=plugins.get(i);
			AudioClipUI acUi=p.getClipUI();
			if(!internalChange && acUi.equals(plugin)){
				TogglePluginAction tpa=p.getTogglePluginAction();
				actionViewFolder.remove(tpa);
				plugins.remove(i);
			}
		}
		if (plugin instanceof AudioClipUI) {
			AudioClipUI acUI=(AudioClipUI) plugin;
			JComponent[] yScales=acUI.getYScales();
			plugin.removeComponentListener(this);
			if(yScales!=null){
				for(JComponent yScale:yScales){
					if(yScale!=null){
					yScalesComponent.remove(yScale);
				}
				}
			}
			
		}
//		defaultPane.remove(plugin);
		super.remove((Component) plugin);
	}

	protected long mapPixelToFrame(int pixelPosition) {
	    double currWidth=getWidth();
	    if(currWidth>0){
	        double framesPerPixel= (double) length / currWidth;
	        return (int) ((double) pixelPosition * framesPerPixel);
	    }else{
	        return 0;
	    }
	}

	protected int mapFrameToPixel(long framePosition) {
	    double currWidth=getWidth();
	    if(currWidth>0){
	        double framesPerPixel= (double) length / currWidth;
	        return (int) ((double) framePosition / framesPerPixel);
	    }else{
	        return 0;
	    }
	}
	
	public synchronized void setXZoom(double xZoom) {
	
	    // store current visible left position as frame position
    	Rectangle currentVisible=getVisibleRect();
    	long currentXFramePos=mapPixelToFrame(currentVisible.x);
    	
		this.xZoom = xZoom;
		
		preferredWidth = (int) (maxLengthInseconds * xZoom);
		revalidate();
		
        // move to old position (after revalidation)
      
        ScrollToFramePositionRunnable sr=new ScrollToFramePositionRunnable(currentXFramePos);
        SwingUtilities.invokeLater(sr);
	}

	private void stop() {

	}

	public synchronized void clear() {
		if (DEBUG)
			System.out.print("Clearing screen...");
		clearScreen();
		if (DEBUG)
			System.out.println("O.K.");
		// audioSource = null;

	}

	public synchronized void clearScreen() {
		stop();

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		if(DEBUG)System.out.println("Clear screen");
		repaint();
	}

	public void setAudioClips(List<AudioClip> audioSamples) {

		if (this.audioClips != null){
			for(AudioClip audioClip:audioClips){
				audioClip.removeAudioSampleListener(this);
			}
		}
		this.audioClips = audioSamples;
		if (this.audioClips != null) {
			for(AudioClip audioClip:audioClips){
				audioClip.addAudioSampleListener(this);
				audioClipChanged(new AudioSourceChangedEvent(this, audioClip
						.getAudioSource()));
			}
			
		} 
		// TODO
		preferredWidth = (int) (maxLengthInseconds * xZoom);
		
//		if (xScale != null) {
//			((AudioClipUI) xScale).setAudioSample(audioSample);
//		}
//		for (Plugin p : plugins) {
//			p.getClipUI().setAudioSample(audioSample);
//		}

		removeAll();
		signalUis.clear();
		for(int i=0;i<audioSamples.size();i++){
			AudioSignalUI asUI=new AudioSignalUI();
			signalUis.add(asUI);
			add(asUI);
			AudioClip ac=audioSamples.get(i);
			asUI.setAudioSample(ac);
		}
		xZoomFitToPanel();
		revalidate();
		repaint();

	}
	
	public void setMediaLengthUnit(MediaLengthUnit mediaLengthUnit) {
	    this.mediaLengthUnit=mediaLengthUnit;
//        Component[] childs = getComponents();
//        for (int i = 0; i < childs.length; i++) {
//            if (childs[i] instanceof AudioClipUI) {
        for(Plugin p:plugins){
                AudioClipUI pUI = p.getClipUI();
                pUI.setMediaLengthUnit(mediaLengthUnit);
        }
        if(xScale!=null){
            AudioClipUI plugin = (AudioClipUI) xScale;
            plugin.setMediaLengthUnit(mediaLengthUnit);
        }
        
    }
	public void setTimeFormat(Format timeFormat) {

	    this.timeFormat=timeFormat;
	    for(Plugin p:plugins){
            AudioClipUI pUI = p.getClipUI();
            pUI.setTimeFormat(timeFormat);
    }
		if(xScale!=null){
			AudioClipUI plugin = (AudioClipUI) xScale;
			plugin.setTimeFormat(timeFormat);
		}
		
	}

	public double getMaxLengthInSeconds() {
		return maxLengthInseconds;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableTracksViewportHeight()
	 */
	public boolean getScrollableTracksViewportHeight() {
		// Scale the heigth of the view automatically
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableTracksViewportWidth()
	 */
	public boolean getScrollableTracksViewportWidth() {
		// the view has a fixed width depending on xZoom
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getPreferredScrollableViewportSize()
	 */
	public Dimension getPreferredScrollableViewportSize() {
		Dimension d = getPreferredSize();
		return d;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableBlockIncrement(java.awt.Rectangle,
	 *      int, int)
	 */
	public int getScrollableBlockIncrement(Rectangle arg0, int arg1, int arg2) {
		// arbitrary value for now
		return 50;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.Scrollable#getScrollableUnitIncrement(java.awt.Rectangle,
	 *      int, int)
	 */
	public int getScrollableUnitIncrement(Rectangle arg0, int arg1, int arg2) {

		return 1;
	}

	/**
	 * Get X-zoom factor.
	 * 
	 * @return X-zoom factor
	 */
	public double getXZoom() {
		return xZoom;
	}

//	/**
//	 * Get sample rate.
//	 * @return sample rate
//	 */
//	public float getSampleRate() {
//		return sampleRate;
//	}

	private void setSelection(Selection s) {
//		if (s == null) {
//			// viewSelection = null;
//			xZoomSelectionMenuItem.setEnabled(false);
//			
//		} else {
//			// viewSelection = ViewSelection(s);
//			xZoomSelectionMenuItem.setEnabled(true);
//		}
		xZoomToSelectionAction.setEnabled((s!=null));
		repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ipsk.audio.arr.AudioClipListener#sourceChanged(java.lang.Object,
	 *      ipsk.audio.AudioSource)
	 */
	public void audioClipChanged(AudioClipChangedEvent event) {
		// super.audioClipChanged(event);
		if (event instanceof SelectionChangedEvent) {
			// setSelection();
			Selection newSelection = ((SelectionChangedEvent) event)
					.getSelection();
			setSelection(newSelection);
		} else if (event instanceof AudioSourceChangedEvent) {
			try {
				// setAudioSource(asEvent.getAudioSource());
				AudioSource audioSource = ((AudioSourceChangedEvent) event)
						.getAudioSource();
				// this.audioSource = audioSource;
				if (audioSource != null) {

					AudioInputStream ais = audioSource.getAudioInputStream();
					audioFormat = ais.getFormat();
					
					long frameLength = ais.getFrameLength();

					try {
						ais.close();
					} catch (IOException e) {
						throw new AudioSourceException(e);
					}
					// setProcessedFrameLength(frameLength);
					length = frameLength;
//					resized();
					double lengthInSeconds;
					if (length == ThreadSafeAudioSystem.NOT_SPECIFIED || audioFormat == null) {
			            lengthInSeconds = 0;
			        } else {
			            lengthInSeconds = length / audioFormat.getFrameRate();
			        }
					if(lengthInSeconds>maxLengthInseconds){
						maxLengthInseconds=lengthInSeconds;
					}
					if(length==0){
						preferredWidth=0;
					}else if (fixXZoomFitToPanel || xZoom == 0.0) {
						xZoomFitToPanel();
					} else {
						preferredWidth = (int) (lengthInSeconds * xZoom);
						// layout.setFixedWidth(width);
					}
				} else {
					// reset();
					audioFormat = null;
					length = ThreadSafeAudioSystem.NOT_SPECIFIED;
					maxLengthInseconds = 0;

					// layout.setFixedWidth(0);
					preferredWidth = 0;
					remove(yScalesComponent);
				}
				// viewSelection=null;
				// setSelection();

				revalidate();
				repaint();
			} catch (AudioSourceException e) {
				// Cannot handle this
			}

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	// public void actionPerformed(ActionEvent e) {
	// if(e instanceof PopupActionEvent){
	// MouseEvent me=((PopupActionEvent)e).getMouseEvent();
	// Component src=(Component)e.getSource();
	// me.translatePoint(src.getX(),src.getY());
	// }
	// e.setSource(this);
	// updateListeners(e);
	// }
	public synchronized void addActionListener(ActionListener acl) {
		if (acl != null && !actionListener.contains(acl)) {
			actionListener.addElement(acl);
		}
	}

	public synchronized void removeActionListener(ActionListener acl) {
		if (acl != null) {
			actionListener.removeElement(acl);
		}
	}

	protected synchronized void updateListeners(ActionEvent ae) {
		for(ActionListener listener:actionListener) {
			listener.actionPerformed(ae);
		}
		
	}

	private void doXZoomFitToPanel(){
		Container p = getParent();
		
		if (p != null && length>0){
			preferredWidth=p.getSize().width;
		}else{
		    preferredWidth=0;
		}
		if(length > 0 && maxLengthInseconds >0) {
			xZoom=preferredWidth/maxLengthInseconds;
		}
	}
	public void xZoomFitToPanel() {
		doXZoomFitToPanel();
			revalidate();
			repaint();
	}

	public void setFixXZoomFitToPanel(boolean b) {
		fixXZoomFitToPanel = b;
		xZoomFixFitToPanelAction.setSelected(fixXZoomFitToPanel);
		if (fixXZoomFitToPanel) {
			xZoomFitToPanel();
		}
	}
	
	public void xZoomOnePixelPerSample(){
	    setFixXZoomFitToPanel(false);
	    if(audioFormat!=null){
	        float frameRate=audioFormat.getFrameRate();
	        setXZoom(frameRate);
	    }
	}
	
//	public void xZoomToSelection(){
//	    setFixXZoomFitToPanel(false);
//	    Selection selection = audioClip.getSelection();
//	    if (selection != null){
//	        double selectedLength = selection.getLength() / audioFormat.getFrameRate();
//	        if(selectedLength>0){
//	            fixXZoomFitToPanel=false;
//	            Rectangle currentVisible=new Rectangle();
//	            computeVisibleRect(currentVisible);
//	            xZoom = currentVisible.width/selectedLength;
//	            preferredWidth = (int) (maxLengthInseconds * xZoom);
//	            revalidate();
//	            // move to old position (after revalidation)
//	            ScrollToFramePositionRunnable sr=new ScrollToFramePositionRunnable(selection.getLeft());
//	            SwingUtilities.invokeLater(sr);
//	        }
//	    }
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		// String actionCmd = ae.getActionCommand();
//		Object src = ae.getSource();
       
//       
//		if (src == showFramesMi) {
//			setTimeFormat(FRAMES_TIME_FORMAT);
//		} else if (src == showSecondsMi) {
//			setTimeFormat(FIXED_SECONDS_TIME_FORMAT);
//		} else if (src == showMediaTimeMi) {
//			setTimeFormat(MEDIA_TIME_FORMAT);
//		}
	}
	
	public void setXScaleVisible(boolean visible){
	    if(visible){
	        super.add(xScale);
	    }else{
	        super.remove(xScale);
	    }
	    validate();
	    repaint();
	}
    
	public void addNotify() {
		super.addNotify();

		parent = getParent();
		// We need to know if the parent has resized if fixXZoomFitToPanel is
		// set
		if (parent != null) {
			parent.addComponentListener(this);
		}
	}

	public void removeNotify() {
		if (parent != null){
			parent.removeComponentListener(this);
		}
		super.removeNotify();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentHidden(java.awt.event.ComponentEvent)
	 */
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
//		System.out.println("hidden bounds: "+getBounds());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentMoved(java.awt.event.ComponentEvent)
	 */
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
//		System.out.println("Move bounds: "+getBounds());
	}


	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ComponentListener#componentShown(java.awt.event.ComponentEvent)
	 */
	public void componentShown(ComponentEvent arg0) {
//		System.out.println("Show bounds: "+getBounds());
	}

	public void componentResized(ComponentEvent ce) {
	    if(DEBUG)System.out.println("componentResized()");
	    Component rc=ce.getComponent();
	    if(rc==parent){
	        Component parent=getParent();
	        if (parent != null && fixXZoomFitToPanel){
	            xZoomFitToPanel();
	        }
	    }else{
	        if(rc instanceof AudioClipUI){
	            AudioClipUI acui=(AudioClipUI) rc;
	            JComponent[] yScales= acui.getYScales();
	            if(yScales!=null) {

	                //int yScalesCompWidth=yScalesComponent.getWidth;
	                for(JComponent yScale:yScales){
	                    //					int scaleX=yScalesCompWidth-yScale.getWidth();
	                    yScale.setLocation(yScale.getX(), rc.getY());
	                    //					yScale.setLocation(scaleX, rc.getY());
	                    //					yScale.revalidate();
	                }
	                if(yScalesComponent!=null){
	                    yScalesComponent.revalidate();
	                }
	            }
	        }
	    }
	}


	public Dimension getPreferredSize() {
	   
		synchronized (getTreeLock()) {
			Insets insets = getInsets();
			Component[] cs = getComponents();
			// Component[] cs=(Component[])trackComps.toArray(new Component[0]);
			int preferredHeight = 0;
			for (int i = 0; i < cs.length; i++) {
				Dimension d = cs[i].getPreferredSize();
				if (d != null) {

					if (d.height > 0)
						preferredHeight += d.height;
				}
			}
			preferredHeight += insets.top;
			preferredHeight += insets.bottom;

			Dimension preferredSize = new Dimension(preferredWidth
					+ insets.left + insets.right, (int) preferredHeight);
			// System.out.println("Preferred size: "+preferredSize);
			return preferredSize;
		}
	}

	public void doLayout() {
	    if(DEBUG)System.out.println("doLayout()");

		synchronized (getTreeLock()) {
			Insets insets = getInsets();
			
			int height = getSize().height;
			int availableHeight = height - insets.top - insets.bottom;

			int width = getSize().width;
			
			
			
			if (xScale != null && toggleXScaleVisibilityAction.isSelected()) {
//				((Component) xScale).setBounds(0, 0, width, height);
				int xScaleHeight = ((Component) xScale).getMinimumSize().height;
				((Component) xScale).setBounds(0, height-xScaleHeight,width, xScaleHeight);
				availableHeight -= xScaleHeight;
			}
			
			int clipCount=signalUis.size();
			if(clipCount>0){
			int heightPerClip=availableHeight/clipCount;
//			for (Plugin p : plugins) {
			int y=0;
			
			for(AudioSignalUI plugin:signalUis){
//				sAudioClipUI plugin= p.getClipUI();
//				plugin.getAudioSample().getAudioSource().get
				Double lenInSecs=null;
				try {
					lenInSecs=AudioFormatUtils.lengthInSeconds(plugin.getAudioSample().getAudioSource());
				} catch (AudioSourceException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				int cWidth=(int)(lenInSecs*xZoom);
//				System.out.println("cw: "+cWidth+" "+lenInSecs+" "+xZoom);
				Component pc = plugin.asComponent();
				Component[] cmps=getComponents();
				for(Component c:cmps){
					if(c==pc){
						if (plugin.isPreferredFixedHeight()) {
						    
							int fbHeight = 0;
							
							if(pc.isVisible()) {
							    fbHeight=pc.getPreferredSize().height;
							}
							
							pc.setBounds(0, availableHeight - fbHeight, cWidth,
									fbHeight);
							availableHeight -= fbHeight;
						} else {
							pc.setBounds(0, y, cWidth, heightPerClip-1);
							y+=heightPerClip;
						}
					}
				}
			}
//			defaultPane.setBounds(0, 0, width, availableHeight);
//			defaultPane.doLayout();
			//System.out.println("defaultPane: "+width+" x "+availableHeight);
			}
		}

	}

	public JAutoScale getXScale() {
		return xScale;
	}

	public void setXScale(JAutoScale scale) {
		xScale = scale;
		
	}
	
    public void close(){
    	Component[] childs = getComponents();
        for (int i = 0; i < childs.length; i++) {
            if (childs[i] instanceof AudioClipUI) {
                AudioClipUI plugin = (AudioClipUI) childs[i];
                plugin.close();
            }
        }
    }

//    public AudioClip getAudioClip() {
//        return audioClip;
//    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    /* (non-Javadoc)
     * @see ips.incubator.awt.action.ActionProvider#getActionTree()
     */
    public ActionTreeRoot getActionTreeRoot() {
//        ActionFolder mergedTree=actionRoot;
//        ActionGroup pluginGroup=new ActionGroup("view.audio_clip_ui_plugins");
        for(Plugin p:plugins){
            TogglePluginAction tpa=p.getTogglePluginAction();
            if(tpa!=null){
//            pluginGroup.add(at);
              actionViewFolder.add(tpa);
            }
            ShowPluginControlComponentAction spcca=p.getShowPluginControlComponentAction();
            if(spcca!=null){
//            pluginGroup.add(at);
              actionViewFolder.add(spcca);
            }
        }
        for(Plugin p:plugins){
            ActionFolder at=p.getClipUI().getActionTreeRoot();
//            pluginGroup.add(at);
            actionRoot.merge(at);
        }
//        mergedTree.merge(pluginGroup);
        return actionRoot;
    }
    
    public void addPopupMouseListener(MouseListener ml){
//        defaultPane.addMouseListener(ml);
    }

    public MediaLengthUnit getMediaLengthUnit() {
        return mediaLengthUnit;
    }

    public Format getTimeFormat() {
        return timeFormat;
    }

   
}
