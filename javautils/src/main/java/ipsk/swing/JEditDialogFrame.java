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
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class JEditDialogFrame extends JFrame {
	private JEditDialogPanel editDialogPane;
	
	private CancelAction cancelAction;
	private ApplyAction applyAction;
	private OkAction okAction;

	private JPanel contentPane;
	
	public JEditDialogFrame(){
		super();
		cancelAction=new CancelAction(){
			public void actionPerformed(ActionEvent ae){
				dispose();
			}
		};
		applyAction=new ApplyAction(){
			public void actionPerformed(ActionEvent ae){
				// Do nothing
			}
		};
		okAction=new OkAction(){
			public void actionPerformed(ActionEvent ae){
				dispose();
			}
		};
		editDialogPane=new JEditDialogPanel(cancelAction,applyAction,okAction);
		
		Container fContent=getContentPane();
		fContent.setLayout(new BorderLayout());
		contentPane=new JPanel();
		fContent.add(contentPane,BorderLayout.CENTER);
		
		fContent.add(editDialogPane, BorderLayout.SOUTH);
	}

	public ApplyAction getApplyAction() {
		return applyAction;
	}

	public CancelAction getCancelAction() {
		return cancelAction;
	}

	public OkAction getOkAction() {
		return okAction;
	}
	
	public static void main(String args[]){
		JEditDialogFrame f=new JEditDialogFrame();
		f.pack();
		f.setVisible(true);
	}
	
}
