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

/**
 * Annotation to mark a static native method that should resolve to a native c++ function
 * with the given name and arguments. Has no effect in the Java backend.
 *
 * Restrictions (not enforced):
 * Non-generic methods only.
 * Method may be void or return a primitive numerical type.
 * Boolean, Byte, Char, Double, Float, Int, Long, Short, UByte, UInt, ULong, UShort.
 * Permitted argument types are the primitive numerical types and arrays of primitive numerical types.
 *
 * For arrays, the native method should expect a pointer to the backing storage.
 */
public interface NativeCPPExtern extends MethodAnnotation { }
