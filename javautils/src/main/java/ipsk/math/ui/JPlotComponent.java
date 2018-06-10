//    IPS Java Utils
// 	  (c) Copyright 2011
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

package ipsk.math.ui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * @author klausj
 *
 */
public class JPlotComponent extends JComponent {

    public class  Value{
        public double x;
        public double y;
    }
    private List<Value> values;
    
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    
    public JPlotComponent(){
        super();
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        if(values==null || values.size()==0){
            return;
        }
        Dimension size=getSize();
        if(size.width<=0 || size.height<=0){
            return;
        }
        
        Value prevValue=null;
        double scaleX=size.width/(maxX-minX);
        double scaleY=size.height/(maxY-minY);
        int prevX=0;
        int prevY=0;
        for(Value v:values){
            int x=(int)(v.x*scaleX);
            int y=(int)(v.y*scaleY);
            if(prevValue!=null){
                g.drawLine(prevX, prevY, x, y);
            }
            prevValue=v;
            prevX=x;
            prevY=y;
        }
        
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
       JFrame  f=new JFrame();
       
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
        minX=Double.MAX_VALUE;
        maxX=Double.MIN_VALUE;
        minY=Double.MAX_VALUE;
        maxY=Double.MIN_VALUE;
        for(Value v:values){
            if(v.x<minX){
                minX=v.x;
            }
            if(v.x>maxX){
                maxX=v.x;
            }
            if(v.y<minY){
                minY=v.y;
            }
            if(v.y>maxY){
                maxY=v.y;
            }
        }
        repaint();
    }

}
