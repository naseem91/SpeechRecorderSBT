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

import javax.swing.table.AbstractTableModel;

/**
 * @author klausj
 *
 */
public class ChannelRoutingTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private int SRC_CH=0;
	private int ASS=1;
	private int TRG_CH=2;
	
	private String[] colNames=new String[]{"Source ch.","Assigned","Target ch."};
	private Class<?>[] colClasses=new Class[]{Integer.class,Boolean.class,Integer.class};
	
	private int inChannels=0;
	private Integer minInputChannelCount=null;
	private int[] routing=null;
	
	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount() {
		return inChannels;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount() {
		return colClasses.length;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int columnIndex) {
		return colNames[columnIndex];
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return colClasses[columnIndex];
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if(columnIndex==ASS){
			return true;
		}else if(columnIndex==SRC_CH){
			return false;
		}else if(columnIndex==TRG_CH){
			// check if assigned
			if(routing!=null){
				for(int r:routing){
					if(r==rowIndex){
						return true;
					}
				}
			}
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		int inCh=rowIndex;
		Integer outChannel=null;
		boolean assigned=false;
		if(routing!=null){
			for(int outCh=0;outCh<routing.length;outCh++){
				int inChR=routing[outCh];
				if(inChR==inCh){
					// assigned
					assigned=true;
					outChannel=outCh;
					break;
				}
			}
		}
		if(columnIndex==SRC_CH){
			return rowIndex;
		}else if(columnIndex==ASS){
			return assigned;
		}else if(columnIndex==TRG_CH){
			return outChannel;
		}else{
			return null;
		}
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(columnIndex==ASS){
				if(aValue instanceof Boolean){
					boolean assigned=((Boolean)aValue).booleanValue();
					int[] oldRouting=routing;
					if(assigned){
						if(oldRouting==null){
							oldRouting=new int[0];
						}
						int[] newRouting=new int[oldRouting.length+1];
						for(int i=0;i<oldRouting.length;i++){
							newRouting[i]=oldRouting[i];
						}

						newRouting[newRouting.length-1]=rowIndex;
						routing=newRouting;
					}else{
						int[] newRouting=new int[oldRouting.length-1];
						int j=0;
						for(int i=0;i<oldRouting.length;i++){
							int oldValue=oldRouting[i];
							if(oldValue!=rowIndex){
								newRouting[j]=oldValue;
								j++;
							}
						}
						routing=newRouting;
					}
					fireTableDataChanged();
				}
			}
	}
	
	private int getInChannelCount(){
		int inChs=0;
		int minChIdx=0;
		if(routing!=null){
			for(int r:routing){
				if(r>minChIdx){
					minChIdx=r;
				}
			}
		}
		inChs=minChIdx+1;
		if(minInputChannelCount!=null){
			if(minInputChannelCount>inChannels){
				inChs=minInputChannelCount;
			}
		}
		return inChs;
	}

	/**
	 * @param config
	 */
	public void setRouting(ChannelRouting config) {
		minInputChannelCount=config.getSrcChannelCount();
		routing=config.getAssign();
		inChannels=getInChannelCount();
		
		fireTableDataChanged();
	}

	/**
	 * @param minCh
	 */
	public void setMinChannelCount(int minCh) {
		minInputChannelCount=minCh;
		inChannels=getInChannelCount();
		fireTableDataChanged();
	}


}
