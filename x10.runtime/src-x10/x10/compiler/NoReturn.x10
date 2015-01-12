/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.compiler;

import x10.lang.annotations.MethodAnnotation;

/**
 * This annotation is used to allow the programmer
 * to communicate to the compiler that the method
 * will never return.  The canonical usage of the 
 * annotation is to mark a method that only throws 
 * an exception. This information can be exploited by
 * the optimizer to generate more efficient code. 
 * This annotation can effect the semantics of the
 * program, so incorrect usage may result in
 * unexpected results.</p>
 *
 * This annotation is processed by the X10 compiler's
 * common optimizer.
 */
public interface NoReturn extends MethodAnnotation { }
