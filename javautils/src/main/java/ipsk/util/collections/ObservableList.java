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
import java.util.List;
import java.util.ListIterator;

/**
 * @author klausj
 * @param <E>
 * 
 */
public class ObservableList<E> extends BasicCollectionObserver<E> implements
        List<E> {

    protected List<E> list;

    public ObservableList(List<E> list){
        super();
        this.list=list;
    }
    
    public int size() {
        return list.size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public boolean contains(Object o) {
        return list.contains(o);
    }

    public Iterator<E> iterator() {
        return list.iterator();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] a) {
        return list.toArray(a);
    }

    public boolean add(E e) {
        boolean changed=list.add(e);
        if(changed){
            fireCollectionChangedEvent();
        }
        return changed;
    }

    public boolean remove(Object o) {
        boolean changed=list.remove(o);
        if(changed){
            fireCollectionChangedEvent();
        }
        return changed;
    }

    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    public boolean addAll(Collection<? extends E> c) {
        boolean changed=list.addAll(c);
        if(changed){
            fireCollectionChangedEvent();
        }
        return changed;
    }

    public boolean addAll(int index, Collection<? extends E> c) {
        return list.addAll(index, c);
    }

    public boolean removeAll(Collection<?> c) {
        boolean changed=list.removeAll(c);
        if(changed){
            fireCollectionChangedEvent();
        }
        return changed;
    }

    public boolean retainAll(Collection<?> c) {
        boolean changed=list.retainAll(c);
        if(changed){
            fireCollectionChangedEvent();
        }
        return changed;
    }

    public void clear() {
        boolean changed=!list.isEmpty();
        list.clear();
        if(changed){
            fireCollectionChangedEvent();
        }
    }

    public boolean equals(Object o) {
        return list.equals(o);
    }

    public int hashCode() {
        return list.hashCode();
    }

    public E get(int index) {
        return list.get(index);
    }

    public E set(int index, E element) {
        E s=list.set(index, element);
        fireCollectionChangedEvent();
        return s;
    }

    public void add(int index, E element) {
        list.add(index, element);
        fireCollectionChangedEvent();
    }

    public E remove(int index) {
        E r=list.remove(index);
        fireCollectionChangedEvent();
        return r;
    }

    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    public ListIterator<E> listIterator() {
        return list.listIterator();
    }

    public ListIterator<E> listIterator(int index) {
        return list.listIterator(index);
    }

    public List<E> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

}
