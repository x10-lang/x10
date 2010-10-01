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

#ifndef X10BOXEDREF_H
#define X10BOXEDREF_H

#include <cstdlib>
#include <cassert>

#include <x10aux/config.h>

namespace x10aux {

    /**
     * This class lets us wrap a pointer (eg an address on the stack)
     * in a way that lets uses of the boxed_ref be unchanged
     * (they can still pretend it is a stack variable), but
     * allows us to keep the pointer as a x10_long to enable
     * 32/64 bit platform interoperability for captured stack vars.
     * We need to be able to support a stack address being captured on
     * a 64 bit machine, transfered to a 32 bit machine via an at, then
     * coming back to the original 64 bit machine and being dereferenced.
     * 
     * TODO: actually hid the void* as an x10_long like we do in GlobalRef.
     */
    template<class T> class boxed_ref {
    protected:
        void *_val;
    public:
        GPUSAFE boxed_ref() { } // ok to not initialize; compiler will ensure we never read unitialized boxed_ref

        GPUSAFE boxed_ref(const boxed_ref<T>& _ref) : _val(_ref._val) { }

        GPUSAFE boxed_ref(T const &val) : _val((void*)&val) { }

        GPUSAFE boxed_ref(T const *val) : _val((void*)val) { }

        GPUSAFE const boxed_ref<T>& operator=(const boxed_ref<T>& _ref) {
            _val = _ref._val;
            return *this;
        }
        
        T operator=(const T &val) {
            *(T*)_val  = val;
            return val;
        }

        operator T() {
            return *((T*)_val);
        }

        x10_long capturedAddress() {
            return (x10_long)(size_t)(_val);
        }

        void setCapturedAddress(x10_long addr) {
            _val = (void*)(size_t)addr;
        }
    };
}

// vim:tabstop=4:shiftwidth=4:expandtab

#endif
