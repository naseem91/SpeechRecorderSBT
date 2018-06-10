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

package ipsk.util.collections;

import java.util.Collection;
import java.util.Vector;

/**
 * @author klausj
 * 
 */
public abstract class BasicCollectionObserver<E> implements Collection<E> {

   
    protected Vector<CollectionListener> listeners=new Vector<CollectionListener>();

    protected BasicCollectionObserver(){
        super();
    }
    
    public void addCollectionListener(CollectionListener collectionListener){
        if(!listeners.contains(collectionListener)){
            listeners.add(collectionListener);
        }
    }
    
    public void removeCollectionListener(CollectionListener collectionListener){
        listeners.remove(collectionListener);
    }
    protected void fireCollectionChangedEvent(){
        fireCollectionChangedEvent(new CollectionChangedEvent(this));
    }
    protected void fireCollectionChangedEvent(CollectionChangedEvent collectionChangedEvent){
        for(CollectionListener cl:listeners){
            cl.collectionChanged(collectionChangedEvent);
        }
    }
    
}
