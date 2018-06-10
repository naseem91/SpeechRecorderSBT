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

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.Point;

/**
 * @author klausj
 *
 */
public class GridBuilder {

    private Container component;

    public GridBuilder(Container component) {
        super();
        this.component = component;
    }
    
    public void insertGrid(GridComponentProvider gridComponentProvider,GridBagConstraints constraints){
        Gridbag[][] comps=gridComponentProvider.getGrid();
        for(Gridbag[] gbRow:comps){
            for(Gridbag gbCell:gbRow){
                GridBagConstraints positionedGbc=gbCell.constraintsForPosition(constraints.gridx, constraints.gridy);
                component.add(gbCell.getComponent(),positionedGbc);
                constraints.gridx++;
            }
            constraints.gridy++;
        }
    }
    public Point insertGrid(GridComponentProvider gridComponentProvider,int gridx,int gridy){
        int gx=gridx;
        
        Gridbag[][] comps=gridComponentProvider.getGrid();
        for(Gridbag[] gbRow:comps){
            gx=gridx;
            for(Gridbag gbCell:gbRow){
                GridBagConstraints positionedGbc=gbCell.constraintsForPosition(gx,gridy);
                component.add(gbCell.getComponent(),positionedGbc);
                gx++;
            }
            gridy++;
        }
        return new Point(gx,gridy);
    }
    
}
