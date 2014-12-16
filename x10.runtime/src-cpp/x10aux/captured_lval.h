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

#ifndef X10CAPTURED_LVAL_H
#define X10CAPTURED_LVAL_H

#include <cstdlib>
#include <cassert>

#include <x10aux/config.h>

namespace x10 { namespace lang { class Reference; } }

namespace x10aux {

    /**
     * This class lets us wrap the address of a captured stack
     * location from a lexically enclosing scope in a way that lets
     * uses of the captured_lval from the body of the closure apply
     * still refer to it as if it was a simple stack variable.
     * The main reason this requires some machination is that we have
     * to be able to support captuing an address on a 64-bit machine,
     * transfering it to a 32 bit machine, and then transfering it
     * back to the 64 bit machine and accessing it.  Therefore we
     * can't store it is a simple reference value, but have to wrap
     * it up in an x10_long (64 bits on all platforms).
     */
    class captured_lval {
    protected:
        // Contains either a T* (captured_struct_lval) or T**(captured_ref_lval)
        x10_long _val;
    public:
        GPUSAFE captured_lval() { } // ok to not initialize; compiler will ensure we never read unitialized captured_lval

        GPUSAFE captured_lval(x10_long v) : _val(v) {}

        x10_long capturedAddress() {
            return _val;
        }

        void setCapturedAddress(x10_long addr) {
            _val = addr;
        }
    };

    template <class T> class captured_struct_lval : public captured_lval {
    public:
        GPUSAFE captured_struct_lval() { } // ok to not initialize; compiler will ensure we never read unitialized captured_lval

        GPUSAFE captured_struct_lval(const captured_struct_lval<T>& _ref) : captured_lval(_ref._val) { }

        GPUSAFE captured_struct_lval(T const &val) : captured_lval((x10_long)(size_t)(void*)&val) { }

        GPUSAFE captured_struct_lval(T const *val) : captured_lval((x10_long)(size_t)(void*)val) { }

        GPUSAFE const captured_struct_lval<T>& operator=(const captured_struct_lval<T>& _ref) {
            _val = _ref._val;
            return *this;
        }

        T operator=(const T &val) {
            *((T*)(size_t)_val)  = val;
            return val;
        }

        // &<captured_struct_lval<T> can simply be the thing itself.
        // Of dubious taste, but lets us avoid trying to
        // special case codegen of closures to detect when
        // the closure is the "first" to capture a variable
        // vs. when it is re-capturing something that has already
        // been captured.
        captured_struct_lval<T> operator&() {
            return *this;
        }
        
        GPUSAFE operator T() {
            return *((T*)(size_t)_val);
        }

        T& GPUSAFE operator*() const {
            return *((T*)_val);
        }

        T* GPUSAFE operator->() const { 
            return (T*)(size_t)_val;
        }
    };

    template <class T> class captured_ref_lval : public captured_lval {
    public:
        GPUSAFE captured_ref_lval() { } // ok to not initialize; compiler will ensure we never read unitialized captured_lval

        GPUSAFE captured_ref_lval(const captured_ref_lval<T>& _ref) : captured_lval(_ref._val) { }

        GPUSAFE captured_ref_lval(T* const *val) : captured_lval((x10_long)(size_t)(void*)val) { }

        GPUSAFE const captured_ref_lval<T>& operator=(const captured_ref_lval<T>& _ref) {
            _val = _ref._val;
            return *this;
        }

        T* operator=(const T* value) {
            *((T**)(size_t)_val) = const_cast<T*>(value);
            return const_cast<T*>(value);
        }
        
        // &<captured_ref_lval<T> can simply be the thing itself.
        // Of dubious taste, but lets us avoid trying to
        // special case codegen of closures to detect when
        // the closure is the "first" to capture a variable
        // vs. when it is re-capturing something that has already
        // been captured.
        captured_ref_lval<T> operator&() {
            return *this;
        }
        
        GPUSAFE operator T*() {
            return (*(T**)(size_t)_val);
        }

        T& GPUSAFE operator*() const {
            return *((T**)_val);
        }

        T* GPUSAFE operator->() const { 
            return *((T**)(size_t)_val);
        }
    };
}

// vim:tabstop=4:shiftwidth=4:expandtab

#endif /* X10CAPTURED_LVAL_H */
