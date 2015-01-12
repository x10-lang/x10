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

import x10.lang.annotations.StatementAnnotation;

/**
 * An annotation that instructs the CUDA backend to use conventional CUDA
 * kernel parameters to pass the capture enviornment, instead of DMA'ing a
 * structure to the GPU and passing a pointer to this structure.
 */
public interface CUDADirectParams extends StatementAnnotation { }
