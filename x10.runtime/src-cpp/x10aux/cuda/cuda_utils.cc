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

#ifdef X10_USE_CUDA_HOST

#include <x10aux/cuda/cuda_utils.h>

using namespace x10aux;

cudaStream_t x10aux::cuda_base_stream;
cudaStream_t x10aux::cuda_read_stream;

#endif

