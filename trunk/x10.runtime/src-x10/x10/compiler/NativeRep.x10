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
 * lang is the name of the language, typically "java" or "c++".
 * <dl><lh>For Java:</lh>
 *     <dt>type</dt><dd>the type of the native representation of T.</dd>
 *     <dt>constructedType</dt><dd><b>unused</b>.</dd>
 *     <dt>rtt</dt><dd>an expression that returns the runtime type for T.</dd>
 * </dl>
 * <dl><lh>For C++:</lh>
 *     <dt>type</dt><dd>the type of the native representation of T when referenced.</dd>
 *     <dt>constructedType</dt><dd>the type of the native representation of T when constructed.</dd>
 *     <dt>rtt</dt><dd><b>unused</b>.</dd>
 * </dl>
 */ 
public interface NativeRep(lang: String, type: String, constructedType: String, rtt: String) extends ClassAnnotation { }
