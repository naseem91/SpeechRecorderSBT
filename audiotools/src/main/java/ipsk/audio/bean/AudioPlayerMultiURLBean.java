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

/*
 * Date  : Oct 20, 2005
 * Author: K.Jaensch, klausj@phonetik.uni-muenchen.de
 */

package ipsk.audio.bean;

import ipsk.audio.arr.Selection;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * Audio player bean for selectable audio URLs
 * 
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 * 
 */
public class AudioPlayerMultiURLBean extends JPanel implements ActionListener {

	public final static boolean DEBUG = false;

	public final static String VERSION = AudioPlayerMultiURLBean.class.getPackage()
			.getImplementationVersion();

	private URL[] audioUrls;
	private AudioPlayerBean playerBean;
	private JComboBox urlSelectBox;

	/**
	 * Constructor.
	 *
	 */
	public AudioPlayerMultiURLBean(URL[] urls)  {
		super(new BorderLayout());
		this.audioUrls=urls;
		playerBean=new AudioPlayerBean();
		playerBean.setVisualizing(true);
		urlSelectBox=new JComboBox(urls);
		add(playerBean,BorderLayout.CENTER);
		add(urlSelectBox,BorderLayout.SOUTH);
		urlSelectBox.addActionListener(this);
		playerBean.setURL(audioUrls[0]);
		//playerBean.setSelection(new Selection(0,100000));
	}


	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		playerBean.setURL((URL)urlSelectBox.getSelectedItem());
		//playerBean.setSelection(new Selection(100000,500000));
	}


	public void close() {
		playerBean.close();
	}


	
	public static void main(String[] args){
		
		if(args.length<1){
			System.err.println("Usage: AudioPlayerMultiURLBean audioURL [audioUrl2] ...");
			System.exit(-1);
		}
		
		try {
			final URL[] audioUrls = new URL[args.length];
			for(int i=0;i<audioUrls.length;i++){
				audioUrls[i]=new URL(args[i]);
			}
			Runnable show=new Runnable(){
				public void run() {
					JFrame f=new JFrame("Test audio player multi URL bean");
					final AudioPlayerMultiURLBean aBean=new AudioPlayerMultiURLBean(audioUrls);
					f.getContentPane().add(aBean);
					f.addWindowListener(new WindowAdapter(){
						public void windowClosing(WindowEvent e) {
							aBean.close();
						}
						public void windowClosed(WindowEvent e) {
							System.exit(0);
						}		
					});
					f.pack();
					f.setVisible(true);
					f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				}
			};
			SwingUtilities.invokeAndWait(show);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			System.exit(-2);
		} catch (InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			System.exit(-3);
		}
		
		//System.exit(0);
		
	}



}
