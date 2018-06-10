//    IPS Java Utils
// 	  (c) Copyright 2015
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

package ipsk.util.dependency;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author klausj
 * @param <T>
 *
 */
public class DependencyResolver<D extends Dependent<T>, T> {
	
	public boolean isResolvable(List<D> availDependents,D dependent){
		Set<T> availProvides=new HashSet<T>();
		for(D availDependent:availDependents){
			availProvides.addAll(availDependent.getProvides());
		}
		dependent.getDependencies();
		boolean resolvable=availProvides.containsAll(dependent.getDependencies());
		return resolvable;
	}
	
	public List<D> resolve(Collection<D> dependents){
		Set<T> availProvides=new HashSet<T>();
		List<D> resolvedList=new ArrayList<D>();
		Collection<D> unresolvedDs=new ArrayList<D>(dependents);
		do{
			List<D> unresolvedDsTmp=new ArrayList<D>();
			
			for(D d:unresolvedDs){
				List<T> dDeps=d.getDependencies();
				boolean resolved=true;
				for(T dDep:dDeps){
					if(!availProvides.contains(dDep)){
						resolved=false;
						
						break;
					}
				}
				if(resolved){
					resolvedList.add(d);
					availProvides.addAll(d.getProvides());
				}else{
					unresolvedDsTmp.add(d);
				}
			}
			if(unresolvedDsTmp.size()==unresolvedDs.size()){
				// unresolvable from here
				// return partial list
				break;
			}
			unresolvedDs=unresolvedDsTmp;
		}while(unresolvedDs.size()>0);
		return resolvedList;
	}
	
	
	public static void main(String[] args){
		List<Dependent<String>> deps=new ArrayList<Dependent<String>>();
		DependentImpl<String> d1=new DependentImpl<String>(new String[]{"a"},new String[]{});
		deps.add(d1);
		DependentImpl<String> d2=new DependentImpl<String>(new String[]{"b","c"},new String[]{});
		deps.add(d2);
		DependentImpl<String> d3=new DependentImpl<String>(new String[]{"c"},new String[]{"a"});
		deps.add(d3);
		DependentImpl<String> d4=new DependentImpl<String>(new String[]{},new String[]{"c"});
		deps.add(d4);
		
		DependencyResolver<Dependent<String>,String> dr=new DependencyResolver<Dependent<String>,String>();
		List<Dependent<String>>rDeps=dr.resolve(deps);
		for(Dependent<String> rd:rDeps){
			System.out.println(rd);
		}
		
	}
}
