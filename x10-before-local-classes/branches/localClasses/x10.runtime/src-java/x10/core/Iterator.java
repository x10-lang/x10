/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.core;

import x10.rtt.RuntimeType;
import x10.rtt.RuntimeType.Variance;

/** Just like java.util.Iterator, but without remove(). */
public interface Iterator<T> {
	public boolean hasNext();
	public T next$G();

	public static final RuntimeType<Iterator<?>> _RTT = new RuntimeType<Iterator<?>>(
	    Iterator.class, 
	    new Variance[] {Variance.COVARIANT}
	) {
	    @Override
	    public String typeName() {
	        return "x10.lang.Iterator";
	    }
	};
}
