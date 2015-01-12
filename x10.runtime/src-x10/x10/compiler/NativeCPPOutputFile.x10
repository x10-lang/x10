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

import x10.lang.annotations.ClassAnnotation;

/** Annotation on a class that indicates that the post compiler will need an additional file from
 * the same dir as the current x10 file.  This is used e.g. for headers included by headers that are
 * included using NativeCPPInclude.  Does nothing for the Java backend.
 */
public interface NativeCPPOutputFile(file: String) extends ClassAnnotation { }
