//    IPS Java Utils
// 	  (c) Copyright 2009
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

package ips.incubator.ipsk.swing.plaf;

import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.plaf.FontUIResource;

/**
 * @author klausj
 *
 */
public class LookAndFeelSelector extends JPanel implements ActionListener {

	private JComboBox lafsComboBox;
	private LookAndFeelInfo[] lafInfos;
	private Frame frame;
	public LookAndFeelSelector(Frame frame){
		super();
		this.frame=frame;
		lafInfos=UIManager.getInstalledLookAndFeels();
		String[] lafNames=new String[lafInfos.length];
		for(int i=0;i<lafInfos.length;i++){
			lafNames[i]=lafInfos[i].getName();
		}
		 lafsComboBox=new JComboBox(lafNames);
		 lafsComboBox.addActionListener(this);
		 add(lafsComboBox);
	}
	
//	public static void increaseFontSizeGlobal( int size )
//	{
//		for ( Enumeration<Object> e = UIManager.getDefaults().keys(); e.hasMoreElements(); )
//		{
//			Object key   = e.nextElement();
//
//			Object value = UIManager.get( key );
//
//			if ( value instanceof Font )
//			{
//				//System.out.println(key);
//				Font f = (Font) value;
//
//				UIManager.put( key, new FontUIResource( f.getName(), f.getStyle(), f.getSize()+size ) );
//				//UIManager.put( key, new FontUIResource( f.getName(), Font.PLAIN, size ) );
//			}
//		}
//	}  
	
	
	public static void printDefaultKeys(PrintStream out)
	{
		
		out.println("Icons:");
		for ( Enumeration<Object> e = UIManager.getDefaults().keys(); e.hasMoreElements(); )
		{
			Object key   = e.nextElement();

			Object value = UIManager.get( key );

			
			if ( value instanceof Icon)
			{
//				//System.out.println(key);
//				Font f = (Font) value;
				out.println(key);
		
			}
		}
		
		out.println("Insets:");
		for ( Enumeration<Object> e = UIManager.getDefaults().keys(); e.hasMoreElements(); )
		{
			Object key   = e.nextElement();

			Object value = UIManager.get( key );

			
			if ( value instanceof Insets)
			{
//				//System.out.println(key);
//				Font f = (Font) value;
				out.println(key);
		
			}
		}
	}  
	
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent arg0) {
		int ind=lafsComboBox.getSelectedIndex();
		String className=lafInfos[ind].getClassName();
		try {
			UIManager.setLookAndFeel(className);
			SwingUtilities.updateComponentTreeUI(frame);
//			increaseFontSizeGlobal(20);
			frame.pack();

		} catch (Exception e) {
			
		} 
	}
	
	public static void main(String[] args){
		
		printDefaultKeys(System.out);
	}

	
	
	
}
