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

package ipsk.swing.text;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.Document;

/**
 * Editor for text.
 * @author K.Jaensch, klausj@phonetik.uni-muenchen.de
 *
 */

public class JTextPaneEditor extends JPanel implements CaretListener {

    protected JTextPane textPane;
    protected JPanel positionPanel;
    protected JLabel positionLabel;
    protected String text;
    private Pattern newLinePattern=Pattern.compile("\r\n|\r|\n");
    public JTextPaneEditor(){
        super(new BorderLayout());
        
        //JPanel content = getContentPane();
        textPane=new JTextPane();
        EditorKitMenu textPaneEkm=new EditorKitMenu(textPane);
        textPaneEkm.setPopupMenuActiv(true);
        textPane.addCaretListener(this);
        positionPanel=new JPanel(new FlowLayout(FlowLayout.TRAILING));
        positionLabel=new JLabel("-:-");
        positionPanel.add(positionLabel);
        add(new JScrollPane(textPane),BorderLayout.CENTER);
        add(positionPanel,BorderLayout.SOUTH);
    }
    
 
    public void setText(String text){
        textPane.setText(text);
    }


    public String getText() {
        return textPane.getText();
    }
    
    protected LinePosition convertToLinePosition(int pos){
        LinePosition p=new LinePosition(1,1);
       
        int c=0;
        String t=textPane.getText();
        Matcher m=newLinePattern.matcher(t);
        int ll=0;
        int mc=0; // matching newline position
        do{
        if(m.find()){
            //ll=m.end()-mc-((m.end()-m.start())-1); // new lines are counted as one position 
            ll=m.start()-mc+1; 
            mc=m.end();
           
           }else{
               p.col=pos-c+1;
               break;
           }
        
        if(c+ll>pos){
            // line found
            p.col=pos-c+1;
            break;
         }else{
             c+=ll;
             p.line++;
             
         }
        }while(c<pos);
        
        return p;
    }
    
    public static void main(String[] args){
        JTextPaneEditor textPaneEditor=null;
      
            textPaneEditor = new JTextPaneEditor();
      
        JFrame f=new JFrame();
        f.getContentPane().add(textPaneEditor);
        f.pack();
        f.setVisible(true);
        
    }


    public void caretUpdate(CaretEvent e) {
       int pos=e.getDot();
       LinePosition lp=convertToLinePosition(pos);
       positionLabel.setText(lp.toString());
        
    }


    public Document getDocument() {
        
        return textPane.getDocument();
    }
    
}
