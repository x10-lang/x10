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

/** Annotation on a class that adds an C++ level #include to the generated
 * code.  This is useful to support @Native code snippets that may require a
 * system or other third party header.  Multiple annotations can be given on a
 * class and each will be included in order, in the generated code.  Has no
 * effect in the Java backend.
 */
public interface NativeCPPInclude(include: String) extends ClassAnnotation { }
