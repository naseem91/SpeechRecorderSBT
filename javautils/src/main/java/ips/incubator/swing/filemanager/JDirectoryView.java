//    IPS Java Utils
// 	  (c) Copyright 2013
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

package ips.incubator.swing.filemanager;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JList;
import javax.swing.JPanel;

/**
 * @author klausj
 *
 */
public class JDirectoryView extends JPanel implements MouseListener {
	private File directory;
	private File[] childs;
	private JFileView[] fileViews;
	private int cols=DEFAULT_COLUMNS;
	private int rows;
	public static final int DEFAULT_COLUMNS=6;
	
	public JDirectoryView(){
		super(new GridLayout(0, 4));
	}
	
	public File getDirectory() {
		return directory;
	}

	public void setDirectory(File directory) {
		this.directory = directory;
		removeAll();
		childs=directory.listFiles();
		
		int childCount=childs.length;
		rows=childCount/cols;
		if(childCount%cols >0){
			rows++;
		}
//		setLayout(new GridLayout(rows,cols,2,2));
		fileViews=new JFileView[childCount];
		
		for(int i=0;i<childCount;i++){
			JFileView fileView=new JFileView(childs[i]);
			fileView.addMouseListener(this);
			fileViews[i]=fileView;
			
			add(fileViews[i]);
		}
		revalidate();
	}

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
       Object src=e.getSource();
       for(JFileView jfv:fileViews){
           if(jfv.equals(src)){
               File f=jfv.getFile();
               if(f.isDirectory()){
                  setDirectory(f); 
               }else{
                   try {
                    Desktop.getDesktop().open(f);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
               }
           }
       }
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // TODO Auto-generated method stub
        
    }
	
	
//	public Dimension getPreferredSize(){
//		return new Dimension(400,100);
//	}
}
