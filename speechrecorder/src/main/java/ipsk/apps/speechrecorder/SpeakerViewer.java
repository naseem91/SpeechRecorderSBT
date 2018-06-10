//    Speechrecorder
//    (c) Copyright 2009-2011
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


package ipsk.apps.speechrecorder;
import java.awt.*;
import javax.swing.*;

import java.text.DecimalFormat;
import java.util.*;

public class SpeakerViewer extends JPanel {

	private static final long serialVersionUID = 938664794388265379L;
	private JLabel[] desc;
	private JLabel[] val;
    
    private JLabel idDescLabel;
    private JLabel idLabel;

	final static Font labelFont = new Font("sans-serif", Font.PLAIN, 12);
	final static Font valueFont = new Font("sans-serif", Font.BOLD, 12);

	private Vector<String> labels;
	private ipsk.db.speech.Speaker data;
	private int rows;
	private int hspace;
	private int vspace;
    private DecimalFormat idFormat=new DecimalFormat("0000");
    

	/**
		 * displays a panel in which speaker data is displayed.
		 */
	SpeakerViewer() {
		super();

		// rows set to 0 to allow automatic computation of rows
		rows = 0;
		hspace = 2;
		vspace = 2;

		labels = Speaker.getDescription();
		int l = labels.size();
		int cols = (int) ((l + 1 +1) / 2) * 2;
		setLayout(new GridLayout(rows, cols, hspace, vspace));
		setForeground(Color.black);
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        idDescLabel=new JLabel("ID:",JLabel.RIGHT);
        idDescLabel.setFont(labelFont);
        idLabel=new JLabel("");
        idLabel.setHorizontalAlignment(JLabel.LEFT);
        idLabel.setFont(valueFont);
        add(idDescLabel);
        add(idLabel);
        
		desc = new JLabel[l];
		val = new JLabel[l];

		for (int i = 0; i < l; i++) {
			desc[i] = new JLabel((String) labels.elementAt(i) + ":", JLabel.RIGHT);
			desc[i].setFont(labelFont);

			val[i] = new JLabel("");
			val[i].setHorizontalAlignment(JLabel.LEFT);
			val[i].setFont(valueFont);

			add(desc[i]);
			add(val[i]);
		}
        
		revalidate();
		repaint();
	}
	/**
	 * displays a panel in which speaker data is displayed.
	 * @param spk
	 */
	SpeakerViewer(ipsk.apps.speechrecorder.db.Speaker spk) {
		this();
		setData(spk);
	}

	/**
	 * sets the label texts and the values of the text fields
	 * for the current speaker
	 * 
	 * @param spk speaker to display
	 */

	public void setData(ipsk.apps.speechrecorder.db.Speaker spk) {
		if (spk == null) {
            idLabel.setText("");
			for (int i = 0; i < labels.size(); i++) {
				val[i].setText("");
			}
		} else {
           
//			data = spk.getData();
			for (int i = 0; i < labels.size(); i++) {
//				String dataStr=data.elementAt(i);
				String dataStr=null;
				int col=i;
				if(col==Speaker.COL_ID){
					int id=spk.getPersonId();
					idLabel.setText(idFormat.format(id));
				}else if(col==Speaker.COL_CODE){
					dataStr=spk.getCode();
				}else if(col==Speaker.COL_NAME){
					dataStr=spk.getName();
				}else if(col==Speaker.COL_FORENAME){
					dataStr=spk.getForename();
				}else if(col==Speaker.COL_GENDER){
					dataStr=spk.getGender();
				}else if(col==Speaker.COL_ACCENT){
					dataStr=spk.getAccent();
				}else if(col==Speaker.COL_BIRTHDATE){
					dataStr=spk.getDateOfBirthString();
				}
			   
			    if(dataStr !=null){
			        val[i].setText(dataStr);
			    }else{
			        val[i].setText("");
			    }
			}
		}
        
		revalidate();
		repaint();
	}

	public Dimension getPreferredSize() {
		return (new Dimension(400, 50));
	}
}
