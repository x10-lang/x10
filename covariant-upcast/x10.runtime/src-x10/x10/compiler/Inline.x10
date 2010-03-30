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

package x10.compiler;

import x10.lang.annotations.MethodAnnotation;

/**
 * This annotation is used to allow the programmer
 * to direct the compiler to unconditionally inline 
 * the annotated method whenever a call expression is 
 * statically resolved to invoke the method.</p>
 *
 * Implementation Limitations: 
 * In the current implementation, this annotation
 * is only processed by the C++ backend and can only
 * be correctly applied to methods of generic types
 * and to methods whose bodies only operate
 * on fields/methods of the annotated method's class
 * and on primitive structs.  If the annotation is
 * improperly applied, the generated C++ code won't 
 * compile.</p>
 *
 * Eventually this annotation will be processed by the
 * X10 compiler's common optimizer and the implementation
 * limitations listed above will be lifted.
 */
public interface Inline extends MethodAnnotation { }
