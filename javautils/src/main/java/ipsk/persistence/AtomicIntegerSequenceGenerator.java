//    IPS Java Utils
// 	  (c) Copyright 2017
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

package ipsk.persistence;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author klausj
 *
 */
public class AtomicIntegerSequenceGenerator implements IntegerSequenceGenerator {

	private AtomicInteger atomicInteger=new AtomicInteger();

	/* (non-Javadoc)
	 * @see ipsk.persistence.IntegerSequenceGenerator#getAndIncrement()
	 */
	@Override
	public int getAndIncrement() {
		return atomicInteger.getAndIncrement();
	}

}
