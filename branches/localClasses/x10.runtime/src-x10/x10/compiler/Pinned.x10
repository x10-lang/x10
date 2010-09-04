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
 * Annotation intended for use by the programmer to document that instances of this class
 * are not intended to cross place boundaries. 
 * 
 * <p> This supports the following common idiom for defining global objects. Define a Pinned class C
 * that contains the mutable state of the global object. This represents the "root" object. Methods
 * on this class should directly perform read/write operations on the fields of this class, as opposed
 * to place shifting. This fields need not be marked transient since the programmer intends that the 
 * instances of C will never be transmitted to other places.
 * 
 * <p> Now define a class GlobalC that contains a val root:GlobalRef[C] field pointing at this object. 
 * Instances of GlobalC will cross place boundaries --  they represent the global object.
 * Public methods on GlobalC should be marked @Global and should specify code that can be 
 * executed on the global object at any place.  Typically such methods will use an at operation in their body
 * to place-shift to root.home and perform operations on the encapsulated C object.
 * 
 * <p> It may make sense for the runtime to dynamically throw errors if an instance of an @Pinned class
 * is detected crossing place boundaries.
 * 
 */
public interface Pinned extends ClassAnnotation { }
