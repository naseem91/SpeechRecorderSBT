//    IPS Java Utils
//    (c) Copyright 2017
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



package ipsk.util.collections;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * @author klausj
 *
 */
public abstract class SetProxy<E> implements Set<E>{

	public enum ProxyState {UNAWARE,EMPTINESS,SIZE,LAZY,BASIC,COMPLETE}
	
	protected boolean empty=true;
	
	protected ProxyState proxyState=ProxyState.UNAWARE;
	
	protected Set<E> set=new HashSet<E>();
	
	protected Exception exception=null;
	
	public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }

   
	protected void setSet(Set<E> set){
	    this.set=set;
	    empty=equals(!(set==null || set.size()==0));
	}

	protected abstract ProxyState loadToProxyState(ProxyState proxyState);
	
	public void refresh(){
	    proxyState=ProxyState.UNAWARE;
	}
	
	public void complete(){
	    ensureProxyState(ProxyState.COMPLETE);
    }
	
	protected void ensureProxyState(ProxyState proxyState){
	    if(this.proxyState.compareTo(proxyState)<0){
	        this.proxyState=loadToProxyState(proxyState);
	     
	    }
	}
	
	/* (non-Javadoc)
	 * @see java.util.Set#add(java.lang.Object)
	 */
	@Override
	public boolean add(E e) {
		ensureProxyState(ProxyState.LAZY);
		boolean added=set.add(e);
		setSet(set);
		return added;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends E> c) {
	    ensureProxyState(ProxyState.LAZY);
        boolean added=set.addAll(c);
        setSet(set);
        return added;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#clear()
	 */
	@Override
	public void clear() {
		set.clear();
		setSet(set);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o) {
	    ensureProxyState(ProxyState.LAZY);
     
		return set.contains(o);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
	    ensureProxyState(ProxyState.LAZY);
	     
        return set.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.util.Set#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
	    ensureProxyState(ProxyState.EMPTINESS);
        return empty;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator<E> iterator() {
	    ensureProxyState(ProxyState.BASIC);
		return set.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Set#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o) {
	    ensureProxyState(ProxyState.LAZY);
	     
        boolean removed=set.remove(o);
        setSet(set);
        return removed;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
	    ensureProxyState(ProxyState.LAZY);
        
        boolean removed=set.removeAll(c);
        setSet(set);
        return removed;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c) {
	    ensureProxyState(ProxyState.LAZY);
        
        boolean changed=set.retainAll(c);
        setSet(set);
        return changed;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#size()
	 */
	@Override
	public int size() {
	    ensureProxyState(ProxyState.SIZE);
	    return set.size();
	}

	/* (non-Javadoc)
	 * @see java.util.Set#toArray()
	 */
	@Override
	public Object[] toArray() {
		ensureProxyState(ProxyState.BASIC);
		return set.toArray();
	}

	/* (non-Javadoc)
	 * @see java.util.Set#toArray(java.lang.Object[])
	 */
	@Override
	public <T> T[] toArray(T[] a) {
	    ensureProxyState(ProxyState.BASIC);
		return set.toArray(a);
	}

}
