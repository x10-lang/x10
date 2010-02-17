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

import x10.lang.annotations.StatementAnnotation;

/**
 * <tt>@Unroll</tt> may be used to annotate a loop.
 * The body of the loop will be unrolled the specified number of times.
 */
public interface Unroll(factor: Int) extends StatementAnnotation { }
