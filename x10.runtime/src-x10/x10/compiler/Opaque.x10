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

import x10.lang.annotations.*;

/** Annotation to mark property methods as being opaque to the constraint
 * solver.
 * 
 * This means they are not inlined, although the body (X10 code) is still used
 * to implement constraint checks in casts.
 */
public interface Opaque(name: String) extends MethodAnnotation { }
