/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

package x10.interop.java;

import x10.lang.annotations.StatementAnnotation;
import x10.lang.annotations.TypeAnnotation;

/**
 * This annotation is used to declare a method or a constructor which throws java checked exception.
 * T is the class of the checked exception.  For readability, the annotation is actually on the body
 * or the return type of the method/constructor.
 * Usage:
 *   def m(a:Int) @Throws[java.io.IOException] { ... }
 *   def r(b:Double):Boolean @Throws[java.io.IOException] { ... }
 */
public interface Throws[T]{T<:java.lang.Throwable} extends StatementAnnotation, TypeAnnotation { }
