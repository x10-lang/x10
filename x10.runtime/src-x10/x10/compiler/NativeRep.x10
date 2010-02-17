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

import x10.lang.annotations.ClassAnnotation;

/**
 * Annotation to mark classes as having a particular native representation.
 * lang is the name of the language, typically "java" or "c".
 * type is the type of the native representation of T.
 * boxedType is the type the native representation of Box[T] (note: for java, Box[int] != Integer).  This should be non-null for value types.  For class types it's not examined.
 * rtt is an expression that returns the runtime type for T.
 */ 
public interface NativeRep(lang: String, type: String, boxedType: String, rtt: String) extends ClassAnnotation { }
