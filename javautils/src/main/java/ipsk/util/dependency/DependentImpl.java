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

import java.util.Arrays;
import java.util.List;

import ipsk.text.StringSequenceBuilder;

/**
 * @author klausj
 *
 */
public class DependentImpl<T> implements Dependent<T>{
	private List<T> dependencies;
	public List<T> getDependencies() {
		return dependencies;
	}
	public DependentImpl(List<T> dependencies, List<T> provides) {
		super();
		this.dependencies = dependencies;
		this.provides = provides;
	}
	
	public DependentImpl(T[] dependencies, T[] provides) {
		super();
		this.dependencies = Arrays.asList(dependencies);
		this.provides =  Arrays.asList(provides);
	}
	
	public List<T> getProvides() {
		return provides;
	}
	private List<T> provides;
	
	
	public String toString(){
		StringBuffer s=new StringBuffer("Depends: ");
		s.append(StringSequenceBuilder.buildStringOfObjs(dependencies,','));
		s.append(" Provides: ");
		s.append(StringSequenceBuilder.buildStringOfObjs(provides,','));
		return s.toString();
	}
	
	
}
