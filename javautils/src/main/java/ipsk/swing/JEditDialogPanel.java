//    IPS Java Utils
//    (c) Copyright 2009-2011
//    Institute of Phonetics and Speech Processing,
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


package ipsk.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class JEditDialogPanel extends JPanel{
	
	private JPanel applyPanel;
	private JPanel cancelPanel;
	private JButton cancelButton;
	//private CancelAction cancelAction;
	private JButton resetButton;
	private JButton applyButton;
	//private ApplyAction applyAction;
	private JButton okButton;
	//private OkAction okAction;
	
	public JEditDialogPanel(CancelAction cancelAction,ApplyAction applyAction,OkAction okAction){
		super(new BorderLayout());
		cancelPanel=new JPanel();
		cancelPanel.setLayout(new FlowLayout(FlowLayout.LEFT,2,2));
		applyPanel=new JPanel(new FlowLayout(FlowLayout.RIGHT,2,2));
		//applyPanel.setLayout(new BoxLayout(applyPanel,BoxLayout.X_AXIS));
		cancelButton=new JButton(cancelAction);
		resetButton=new JButton("Reset");
		//resetButton.addActionListener(this);
		applyButton=new JButton(applyAction);
	
		okButton=new JButton(okAction);
		
		cancelPanel.add(cancelButton);
		cancelPanel.add(resetButton);
		applyPanel.add(applyButton);
		applyPanel.add(okButton);
		add(cancelPanel,BorderLayout.WEST);
		add(applyPanel,BorderLayout.EAST);
	}
	
//	
//	public static void main(String[] args){
//	JFrame f = new JFrame();
//    //JEditDialogPanel je = new JEditDialogPanel();
//    f.getContentPane().add(je);
//    f.pack();
//    f.setVisible(true);
//	}

	
}
