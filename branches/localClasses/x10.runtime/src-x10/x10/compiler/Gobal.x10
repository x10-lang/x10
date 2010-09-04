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

import x10.lang.annotations.ExpressionAnnotation;
import x10.lang.annotations.MethodAnnotation;

/**
 * This annotation is used by the programmer to document that
 * this method is intended to be invoked at any place. 
 * Typically the body of this method will place-shift to the
 * root and perform operations on the root object. 
 *  
 *  <p>This method is not processed by any phase of the compiler at this time. 
 *  It is intended to document programmer intent.
 *  
 *  @see Root
 */
public interface Global extends MethodAnnotation  { }
