/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */

#include <x10/lang/Rail.h>

x10aux::RuntimeType x10::lang::Rail<void>::rtt;

using namespace x10aux;

namespace x10 {
    namespace lang {

        void throwArrayIndexOutOfBoundsException(x10_long index, x10_long length) {
            #ifndef NO_EXCEPTIONS
            char *msg = alloc_printf("Index %lld out of range (length is %lld)", (long long)index, (long long)length);
            throwException(x10::lang::ArrayIndexOutOfBoundsException::_make(String::Lit(msg)));
            #endif
        }

        void rail_copyRaw(void *srcAddr, void *dstAddr, x10_long numBytes, bool overlap) {
            if (overlap) {
                // potentially overlapping, use memmove
                memmove(dstAddr, srcAddr, (size_t)numBytes);
            } else {
                memcpy(dstAddr, srcAddr, (size_t)numBytes);
            }
        }

    }
}
