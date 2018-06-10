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
import java.util.Iterator;

/**
 * @author klausj
 * 
 */
public abstract class ObservableCollection<E> extends BasicCollectionObserver<E> implements Collection<E>{

    protected Collection<E> collection;
    
    
    public int size() {
        return collection.size();
    }

    public boolean isEmpty() {
        return collection.isEmpty();
    }

    public boolean contains(Object o) {
        return collection.contains(o);
    }

    public Iterator<E> iterator() {
        return collection.iterator();
    }

    public Object[] toArray() {
        return collection.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return collection.toArray(a);
    }

    public boolean add(E e) {
        boolean changed=collection.add(e);
        if(changed){
            fireCollectionChangedEvent();
        }
        return changed;
    }

    public boolean remove(Object o) {
        boolean changed=collection.remove(o);
        if(changed){
            fireCollectionChangedEvent();
        }
        return changed;
    }

    public boolean containsAll(Collection<?> c) {
        return collection.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        boolean changed=collection.addAll(c);
        if(changed){
            fireCollectionChangedEvent();
        }
        return changed;
    }

    public boolean removeAll(Collection<?> c) {
        boolean changed=collection.removeAll(c);
        if(changed){
            fireCollectionChangedEvent();
        }
        return changed;
    }

    public boolean retainAll(Collection<?> c) {
        boolean changed=collection.retainAll(c);
        if(changed){
            fireCollectionChangedEvent();
        }
        return changed;
    }

    public void clear() {
        boolean changed=!collection.isEmpty();
        collection.clear();
        if(changed){
            fireCollectionChangedEvent();
        }
    }

    public boolean equals(Object o) {
        return collection.equals(o);
    }

    public int hashCode() {
        return collection.hashCode();
    }
   
}
