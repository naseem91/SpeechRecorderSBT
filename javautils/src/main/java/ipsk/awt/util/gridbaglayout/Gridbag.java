//    IPS Java Utils
// 	  (c) Copyright 2012
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

package ipsk.awt.util.gridbaglayout;

import java.awt.Component;
import java.awt.GridBagConstraints;

/**
 * @author klausj
 *
 */
public class Gridbag {

    private GridBagConstraints constraints;
    private Component component;
    

    public Gridbag(Component component,GridBagConstraints constraints) {
        super();
        this.constraints = (GridBagConstraints)constraints.clone();
        this.component = component;
    }
    
    public GridBagConstraints constraintsForPosition(int gridx, int gridy){
        GridBagConstraints positionedConstraints=(GridBagConstraints)constraints.clone();
        positionedConstraints.gridx=gridx;
        positionedConstraints.gridy=gridy;
        return positionedConstraints;
    }
    
   
    
    public Component getComponent() {
        return component;
    }
    public void setComponent(Component component) {
        this.component = component;
    }
    
}
