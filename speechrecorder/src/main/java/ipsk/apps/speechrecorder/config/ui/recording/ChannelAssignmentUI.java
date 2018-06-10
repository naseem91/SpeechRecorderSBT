//    Speechrecorder
// 	  (c) Copyright 2014
// 	  Institute of Phonetics and Speech Processing,
//    Ludwig-Maximilians-University, Munich, Germany
//
//
//    This file is part of Speechrecorder
//
//
//    Speechrecorder is free software: you can redistribute it and/or modify
//    it under the terms of the GNU Lesser General Public License as published by
//    the Free Software Foundation, version 3 of the License.
//
//    Speechrecorder is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU Lesser General Public License for more details.
//
//    You should have received a copy of the GNU Lesser General Public License
//    along with Speechrecorder.  If not, see <http://www.gnu.org/licenses/>.



package ipsk.apps.speechrecorder.config.ui.recording;

import ipsk.apps.speechrecorder.config.ChannelRouting;
import ipsk.apps.speechrecorder.config.RecordingConfiguration;
import ipsk.swing.TitledPanel;
import ipsk.swing.panel.JConfigPanel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author klausj
 *
 */
public class ChannelAssignmentUI extends JConfigPanel implements ChangeListener, ActionListener {

	private static final long serialVersionUID = 1L;

	public class TrgChannelListUI extends JPanel{
		
		private static final long serialVersionUID = 1L;
		private List<JLabel> trgChLabels=new ArrayList<JLabel>();
		private List<JComboBox> trgChSelectors=new ArrayList<JComboBox>();

		private boolean enabled=true;
		
		/**
		 * @return the enabled
		 */
		public boolean isEnabled() {
			return enabled;
		}

		/**
		 * @param enabled the enabled to set
		 */
		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
			apply();
		}


		private int srcChannelCount=0;
		
		/**
		 * @return the srcChannelCount
		 */
		public int getSrcChannelCount() {
			return srcChannelCount;
		}

		/**
		 * @param srcChannelCount the srcChannelCount to set
		 */
		public void setSrcChannelCount(int srcChannelCount) {
			this.srcChannelCount = srcChannelCount;
	
			if(routing!=null){
				for(int i=0;i<routing.length;i++){
					int r=routing[i];
					if(r>=srcChannelCount){
						int newIdx=srcChannelCount-1;
						if(newIdx<0){
							newIdx=0;
						}
						routing[i]=newIdx;
					}
				}
			}
			createUI();
		}


		private int recordingChannelCount=0;
		
		
		private int[] routing;
		
		
		
		public TrgChannelListUI(){
			super(new GridBagLayout());
		}
		
		public void setRouting(int[] routing){
			this.routing=routing;
			apply();
		}
		
		public int[] getRouting(){
			int trgChCount=trgChSelectors.size();
			int[] routing=new int[trgChCount];
			for(int i=0;i<trgChCount;i++){
				routing[i]=trgChSelectors.get(i).getSelectedIndex();
			}
			return routing;
		}
		
		public void setRecordingChannelCount(int recChannels){
			this.recordingChannelCount=recChannels;
			int[] newRouting=new int[this.recordingChannelCount];
			for(int i=0;i<this.recordingChannelCount;i++){
				if(routing!=null && i<routing.length){
					// copy
					newRouting[i]=routing[i];
				}else{
					// or default to one to one routing
					int defCh=i;
//					if(srcChannelCount!=null){
						if(defCh>=srcChannelCount){
							// limit
							defCh=srcChannelCount-1;
							if(defCh<0){
								defCh=0;
							}
						}
//					}
					newRouting[i]=defCh;
				}
			}
			routing=newRouting;
			createUI();
		}
		
		
		private void createUI(){
			removeAll();
//			List<JLabel> newTrgChLbls=new ArrayList<JLabel>();
//			List<JComboBox> newTrgChSels=new ArrayList<JComboBox>();
			trgChLabels.clear();
			trgChSelectors.clear();
//			int srcChs=recordingChannelCount;
//			if(srcChannelCount!=null){
//				srcChs=srcChannelCount;
//			}
			for(int i=0;i<recordingChannelCount;i++){
				JLabel trgChLabel=new JLabel(": recording file channel idx: "+Integer.toString(i));
				trgChLabels.add(trgChLabel);
				String[] elems=new String[srcChannelCount];
				for(int inCh=0;inCh<elems.length;inCh++){
					elems[inCh]="Source channel idx: "+Integer.toString(inCh);
				}
				JComboBox trgChBox=new JComboBox(elems);
				trgChSelectors.add(trgChBox);
			}
			
			
//			trgChSelectors=newTrgChSels;
			
			GridBagConstraints c=new GridBagConstraints();
			c.insets = new Insets(1, 1, 1, 1);

			for(int r=0;r<recordingChannelCount;r++){
				c.gridx=0;
				c.gridy=r;
				add(trgChSelectors.get(r),c);
				c.gridx++;
				add(trgChLabels.get(r),c);
			}
			apply();
			revalidate();
			repaint();
			
			
		}
		
		
		private void apply(){

			for(int trgIdx=0;trgIdx<trgChSelectors.size();trgIdx++){
				JLabel trgChLbl=trgChLabels.get(trgIdx);
				JComboBox trgChSel=trgChSelectors.get(trgIdx);
				
				if(srcChannelCount==0){
					trgChSel.setEnabled(false);
					trgChLbl.setEnabled(false);
				}else{
					
					int srcCh;

					if(routing!=null){
						srcCh=routing[trgIdx];
					}else{
						srcCh=trgIdx;
//						if(srcChannelCount!=null){
							if(srcCh>=srcChannelCount){
								srcCh=srcChannelCount-1;
							}
//						}
					}
					trgChSel.setSelectedIndex(srcCh);
					
					trgChLbl.setEnabled(enabled);
					trgChSel.setEnabled(enabled);
					
				}
			}
		}
		
		
		

	}
	
	private ChannelRouting config;	
	private ChannelRouting initialChannelRouting;
	/**
	 * @return the config
	 */
	public ChannelRouting getConfig() {
		return config;
	}

	
	private int targetChannelCount;

	/**
	 * @return the targetChannelCount
	 */
	public int getTargetChannelCount() {
		return targetChannelCount;
	}

	/**
	 * @param targetChannelCount the targetChannelCount to set
	 */
	public void setTargetChannelCount(int targetChannelCount) {
		int oldTargetChannelCount=this.targetChannelCount;
		this.targetChannelCount = targetChannelCount;
//		System.out.println("Trg ch count: "+oldTargetChannelCount+" -> "+targetChannelCount);
		if(oldTargetChannelCount!=targetChannelCount){
//			System.out.println("Change trg ch count: "+targetChannelCount);
			channelAssignmentUI.setRecordingChannelCount(targetChannelCount);
		}
	}
	
	
	

	/**
	 * @param config the config to set
	 */
	public void setConfig(ChannelRouting config) {
	    initialChannelRouting=config; 
	    applyConfig(config);
	}
	
	public void applyConfig(ChannelRouting config){

		this.config = config;
		
		boolean channelOffsetMode=false;
		boolean routingEnabled=false;
		int[] routing=null;
		Integer srcChannelCount=null;
		int channelOffset=0;
		if(config!=null){

			routing=config.getAssign();

			channelOffset=config.getChannelOffset();
			channelOffsetMode=channelOffset!=0;
			routingEnabled=!channelOffsetMode && routing !=null;

			srcChannelCount=config.getSrcChannelCount();
			if(srcChannelCount==null){
				srcChannelCount=srcChannelCountByAssignment(routing);
			}
		}
		channelOffsetModel.setValue(channelOffset);
		channelAssignmentUI.setRouting(routing);
//		channelAssignmentUI.setEnabled(routingEnabled);
//		enableBox.setEnabled(!channelOffsetMode);
		enableBox.setSelected(routingEnabled);
		
		//		System.out.println("set src ch count box: "+srcChannelCount);
		if(srcChannelCount==null){
			setDefaultSrcChannels();
		}else{
			channelAssignmentUI.setSrcChannelCount(srcChannelCount);
			inputChannelCountBox.setValue(srcChannelCount);
		}
//		inputChannelCountBox.setEnabled(routingEnabled);
//		inputChannelCountLbl.setEnabled(routingEnabled);
		//		routingTableModel.setRouting(config);
		//		channelAssignmentUI.setRecordingChannelCount(8);

		
		setDependencies();
	}

	private TitledPanel channelOffsetPanel;
	private JLabel channelOffsetLabel;
	private JSpinner channelOffsetBox;
	
	private TitledPanel channelRoutingPanel;
	private JLabel enableLabel;
	private JCheckBox enableBox;
	
	private JSpinner inputChannelCountBox;
//	private JTable routingTable;
//	private ChannelRoutingTableModel routingTableModel;
	private TrgChannelListUI channelAssignmentUI;
	private JLabel inputChannelCountLbl;
	private SpinnerNumberModel channelOffsetModel;
	private SpinnerNumberModel inputChannelCountModel;
	
	
	/**
	 * 
	 */
	public ChannelAssignmentUI() {
		super();
		JPanel cp=getContentPane();
		cp.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		GridBagConstraints pc=new GridBagConstraints();
		c.insets = new Insets(1, 1, 1, 1);
		c.anchor=GridBagConstraints.WEST;
		pc.insets = new Insets(1, 1, 1, 1);
		c.weightx=2;
	
		
		c.gridx = 0;
		c.gridy = 0;
		
		channelOffsetPanel=new TitledPanel("Channel offset");
		channelOffsetPanel.setLayout(new GridBagLayout());
		pc.gridx=0;
		pc.gridy=0;
		pc.weightx=0;
		pc.anchor=GridBagConstraints.WEST;
//		pc.fill=GridBagConstraints.HORIZONTAL;
		channelOffsetLabel=new JLabel("Channel offset:");
		channelOffsetPanel.add(channelOffsetLabel,pc);
		channelOffsetModel = new SpinnerNumberModel(0,0,256,1);
		channelOffsetBox=new JSpinner(channelOffsetModel);
		channelOffsetModel.addChangeListener(this);
		pc.gridx++;
		pc.weightx=2;
//		pc.anchor=GridBagConstraints.EAST;
		channelOffsetPanel.add(channelOffsetBox,pc);
		
//		c.gridwidth=2;
		c.fill=GridBagConstraints.HORIZONTAL;
		cp.add(channelOffsetPanel,c);
		
		channelRoutingPanel=new TitledPanel("Channel routing");
		channelRoutingPanel.setLayout(new GridBagLayout());
		
		pc.gridx=0;
		pc.gridy=0;
		pc.weightx=0;
		enableLabel=new JLabel("Enable: ");
		channelRoutingPanel.add(enableLabel,pc);
		enableBox=new JCheckBox();
		pc.gridx++;
		pc.weightx=2;
		channelRoutingPanel.add(enableBox,pc);
		enableBox.addActionListener(this);
		
		pc.gridx=0;
		pc.gridy++;
		pc.weightx=0;
		inputChannelCountLbl=new JLabel("Input channel count:");
		channelRoutingPanel.add(inputChannelCountLbl,pc);
		inputChannelCountModel = new SpinnerNumberModel(1, 1,256, 1);
		inputChannelCountBox=new JSpinner(inputChannelCountModel);	
		pc.gridx++;
		pc.weightx=2;
		channelRoutingPanel.add(inputChannelCountBox,pc);
		inputChannelCountBox.addChangeListener(this);
		
		pc.gridx=0;
		pc.gridy++;
		pc.weightx=2;
		pc.weighty=2;
		pc.fill=GridBagConstraints.BOTH;
		pc.gridwidth=2;
		
		
		channelAssignmentUI = new TrgChannelListUI();
		JScrollPane sp=new JScrollPane(channelAssignmentUI);
		channelRoutingPanel.add(sp,pc);
		
		c.weighty=2;
		c.gridy++;	
		c.fill=GridBagConstraints.BOTH;
		cp.add(channelRoutingPanel,c);
		
		}
	
	private Integer srcChannelCountByAssignment(int[] assign){
		Integer iccba=null;
		if(assign!=null && assign.length>0){
			int maxAssChIdx=-1;
			for(int ac:assign){
				if(ac>maxAssChIdx){
					maxAssChIdx=ac;
				}
			}
			iccba=maxAssChIdx+1;
		}
		
		return iccba;
		
	}
	
	public boolean anyChannelRoutingSelected(){
		
		int chOffset=channelOffsetModel.getNumber().intValue();
		boolean crSel=(chOffset !=0 || enableBox.isSelected());
		return crSel;
	}
	
	public void applyStereoCaptureForMonoRecording(){
		enableBox.setSelected(true);
		inputChannelCountModel.setValue(new Integer(2));
		setDependencies();
	}

	public void applyValues(RecordingConfiguration recCfg){
		ChannelRouting chRouting=null;
		int chOffset=channelOffsetModel.getNumber().intValue();
		if(enableBox.isSelected()){
			chRouting=new ChannelRouting();
			Number minChsNum=inputChannelCountModel.getNumber();
			int minChs=minChsNum.intValue();
			int[] routing=channelAssignmentUI.getRouting();
			
			// set source channel count only if it is greater then 
			// required count from assignment
			
			int srcChsByassign=srcChannelCountByAssignment(routing);
			Integer srcChannelsEff=null;
			if(srcChsByassign<minChs){
				srcChannelsEff=minChs;
			}
			chRouting.setSrcChannelCount(srcChannelsEff);
			
			chRouting.setAssign(channelAssignmentUI.getRouting());
			
			
		}else if(chOffset!=0){
			chRouting=new ChannelRouting();
			chRouting.setChannelOffset(chOffset);
		}
		recCfg.setChannelAssignment(chRouting);
	}
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				ChannelAssignmentUI ui=new ChannelAssignmentUI();
				JFrame f=new JFrame();
				f.getContentPane().add(ui);
				f.pack();
				f.setVisible(true);
			}
		});
	}


	private void setDefaultSrcChannels(){
		
		Number chOffsetNumber=channelOffsetModel.getNumber();
		int chOffset=chOffsetNumber.intValue();
		int minVal=chOffset+targetChannelCount;
		inputChannelCountModel.setValue(minVal);
		channelAssignmentUI.setSrcChannelCount(minVal);
	}
	
	private void setDependencies(){
	    Number chOffsetNumber=channelOffsetModel.getNumber();
	    int chOffset=chOffsetNumber.intValue();
	    boolean channelOffsetMode=(chOffset!=0);

	    // and enable/disable routing option
	    enableLabel.setEnabled(!channelOffsetMode);
	    enableBox.setEnabled(!channelOffsetMode);
	    boolean routingEnabled=!channelOffsetMode && enableBox.isSelected();
	    channelOffsetLabel.setEnabled(!routingEnabled);
	    channelOffsetBox.setEnabled(!routingEnabled);
	    channelAssignmentUI.setEnabled(routingEnabled);
	    inputChannelCountBox.setEnabled(routingEnabled);
	    inputChannelCountLbl.setEnabled(routingEnabled);
	}    

	/* (non-Javadoc)
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		Object src=e.getSource();
		Number chOffsetNumber=channelOffsetModel.getNumber();
		int chOffset=chOffsetNumber.intValue();
		boolean channelOffsetMode=(chOffset!=0);
		if(src==channelOffsetModel){
			setDefaultSrcChannels();
		}else if(src==inputChannelCountBox){
			if(!channelOffsetMode){
				Number valNum=inputChannelCountModel.getNumber();
				int minCh=valNum.intValue();
				channelAssignmentUI.setSrcChannelCount(minCh);
			}
		}
		setDependencies();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src=e.getSource();
		if(src==enableBox){
			setDefaultSrcChannels();
		}else{
		    // if reset button pressed
		    super.actionPerformed(e);
		}
		setDependencies();
	}

    /* (non-Javadoc)
     * @see ipsk.swing.panel.JConfigPanel#resetToDefaults()
     */
    @Override
    public void resetToDefaults() {
       applyConfig(new ChannelRouting());
    }

    /* (non-Javadoc)
     * @see ipsk.swing.panel.JConfigPanel#resetToInitial()
     */
    @Override
    public void resetToInitial() {
       applyConfig(initialChannelRouting);
    }

}
