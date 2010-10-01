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
     * This class lets us wrap the address of a captured stack
     * location from a lexically enclosing scope in a way that lets
     * uses of the boxed_ref from the body of the closure apply
     * still refer to it as if it was a simple stack variable.
     * The main reason this requires some machination is that we have
     * to be able to support captuing an address on a 64-bit machine,
     * transfering it to a 32 bit machine, and then transfering it
     * back to the 64 bit machine and accessing it.  Therefore we
     * can't store it is a simple reference value, but have to wrap
     * it up in an x10_long (64 bits on all platforms).
     */
    template<class T> class boxed_ref {
    protected:
        // Actually contains a T*, but always stored as 64 bits even on 32 bit machines
        x10_long _val;
    public:
        GPUSAFE boxed_ref() { } // ok to not initialize; compiler will ensure we never read unitialized boxed_ref

        GPUSAFE boxed_ref(const boxed_ref<T>& _ref) : _val(_ref._val) { }

        GPUSAFE boxed_ref(T const &val) : _val((x10_long)(size_t)(void*)&val) { }

        GPUSAFE boxed_ref(T const *val) : _val((x10_long)(size_t)(void*)val) { }

        GPUSAFE const boxed_ref<T>& operator=(const boxed_ref<T>& _ref) {
            _val = _ref._val;
            return *this;
        }
        
        T operator=(const T &val) {
            *((T*)(size_t)_val)  = val;
            return val;
        }

        operator T() {
            return *((T*)(size_t)_val);
        }

        x10_long capturedAddress() {
            return _val;
        }

        void setCapturedAddress(x10_long addr) {
            _val = addr;
        }
    };
}

// vim:tabstop=4:shiftwidth=4:expandtab

#endif
