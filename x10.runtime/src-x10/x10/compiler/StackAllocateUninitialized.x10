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

import x10.lang.annotations.ExpressionAnnotation;
import x10.lang.annotations.StatementAnnotation;

/** 
 * An annotation that requests the compiler to stack allocate an object
 * must be used *exactly* as the following for now:
 * <pre>
 *   @StackAllocate val v = @StackAllocateUninitialized new T(...);
 * </pre>
 * This annotation causes the compiler to elide the call to the
 * constructor on the stack allocated storage.  Therefore, the 
 * storage will be uninitialized.
 *
 * The intended use is demonstrated in the KMeans benchmark,
 * where uninitialized stack allocated Rails are used to 
 * express a hand-unrolled loop.
 */
public interface StackAllocateUninitialized extends ExpressionAnnotation { }
