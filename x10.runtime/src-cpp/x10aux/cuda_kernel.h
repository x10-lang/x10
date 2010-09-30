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

#ifndef X10AUX_CUDA_KERNEL_H
#define X10AUX_CUDA_KERNEL_H

namespace x10aux {

    template<class T> struct cuda_array {
        x10_int size;
        T *raw;
        T &apply (x10_int i) { return raw[i]; }
        T apply (const T &v, x10_int i) { return raw[i] = v; }
    };
}

#endif
