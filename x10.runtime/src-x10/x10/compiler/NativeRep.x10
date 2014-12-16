/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.compiler;

import x10.lang.annotations.ClassAnnotation;

/**
 * Annotation to mark classes as having a particular native representation.
 * lang is the name of the language, typically "java" or "c++".
 * <dl><lh>For Java:</lh>
 *     <dt>referencedType</dt><dd>the type of the native representation of T.</dd>
 *     <dt>constructedType</dt><dd><b>unused</b>.</dd>
 *     <dt>rtt</dt><dd>an expression that returns the runtime type for T.</dd>
 *     <dt>SUBSTITUTION:</dt><dd>#0 is the name of the class, #1-#9 are type arguments.</dd>
 * </dl>
 * <dl><lh>For C++:</lh>
 *     <dt>referencedType</dt><dd>the type of the native representation of T when referenced.</dd>
 *     <dt>constructedType</dt><dd>the type of the native representation of T when constructed.</dd>
 *     <dt>rtt</dt><dd><b>unused</b>.</dd>
 *     <dt>SUBSTITUTION:</dt><dd>As in Java, but also type arguments can be accessed through by name e.g. #T.
                                 Properties can also be accessed using #prop.
                                 Note that #prop will raise an error at compile time if the property
                                 is not specified by the constraint of the type in question.  One can
                                 also use ##prop#default# which will use either the value of the property
                                 or the provided string if that property is not known.</dd>
 * </dl>
 *
 */ 
public interface NativeRep(lang: String, referencedType: String, constructedType: String, rtt: String) extends ClassAnnotation { }
