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

package x10.runtime.impl.java;

//XTENLANG-2686: UnknownJavaThrowable is used to wrap Java exceptions that cannot be converted to X10 Exception
public class UnknownJavaThrowable extends x10.core.Throwable {
    
	private static final long serialVersionUID = 1L;

    public UnknownJavaThrowable(java.lang.Throwable t) {
        super(t);
    }
}
