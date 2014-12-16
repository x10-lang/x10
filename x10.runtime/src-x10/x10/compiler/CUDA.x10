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

import x10.lang.annotations.*;

/**
 * An annotation that instructs the C++ backend to compile the block as a CUDA
 * kernel.  This implies that the block should be capable of running on a GPU,
 * which means restrictions on what language features are allowed.
 */
public interface CUDA extends StatementAnnotation, MethodAnnotation { }
