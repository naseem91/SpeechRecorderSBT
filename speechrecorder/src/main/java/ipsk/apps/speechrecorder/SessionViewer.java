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

/*
 * Created on May 3, 2004
 *
 * Project: JSpeechRecorder
 * Original author: draxler
 */
package ipsk.apps.speechrecorder;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.BorderLayout;

/**
 * @author draxler
 *
 * InfoViewer displays the key parameters for a given session, i.e.
 * <ul>
 * 	<li>recording script file name,</li>
 * 	<li>speaker data, and</li>
 * 	<li>target recording directory.</li>
 * </ul>
 * InfoViewer obtains this data from SpeechRecorder which thus
 * serves as the data model.
 * 
 */
public class SessionViewer extends JPanel {

	private static final long serialVersionUID = -4151582443094031725L;
	private final static int LEFT = 2;
	private final static int RIGHT = 2;
	private final static int TOP = 2;
	private final static int BOTTOM = 2;

	private SpeechRecorder speechRecorder;
	private SpeakerViewer speakerViewer;

//	private JLabel recScriptLabel;
//	private JLabel recDirLabel;

	private JLabel recScriptValue;
	private JLabel recDirValue;

	public SessionViewer(SpeechRecorder sr) {
		super();
		speechRecorder = sr;

		setBorder(BorderFactory.createEmptyBorder(LEFT, TOP, RIGHT, BOTTOM));
		setLayout(new BorderLayout());

//		recScriptLabel = new JLabel("Script", JLabel.RIGHT);
//		recDirLabel = new JLabel("Rec. Directory", JLabel.RIGHT);

		String recScriptName = speechRecorder.getRecScriptName();
		if (recScriptName == null) {
			recScriptName = new String("");
		}
		recScriptValue = new JLabel(recScriptName, JLabel.LEFT);
		String recDirName = speechRecorder.getRecDirName();
		if (recDirName == null) {
			recDirName = new String("");
		}
		recDirValue = new JLabel(recDirName, JLabel.LEFT);
		//speakerViewer = new SpeakerViewer(speechRecorder.getSpeaker());
		speakerViewer = new SpeakerViewer();

		add(recScriptValue,BorderLayout.NORTH);
		add(speakerViewer,BorderLayout.CENTER);
		add(recDirValue,BorderLayout.SOUTH);
	}

	/**
	 * Sets the display values to the parameters provided.
	 * 
	 * @param spk speaker data
	 * 
	 */
	public void setData(ipsk.apps.speechrecorder.db.Speaker spk) {
        // moved to Help->Info
		//recScriptValue.setText(rsn);
		speakerViewer.setData(spk);
        // moved to Help -> info
		//recDirValue.setText(rd);

		revalidate();
		repaint();
	}
}
