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

#ifndef X10REF_H
#define X10REF_H

#include <cstdlib>
#include <cassert>

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

namespace x10 { namespace lang { class NullType; } }

namespace x10aux {

    class __ref {}; // Provide common supertype for use in catch blocks
    
    template<class T> class ref :public __ref {
    public:
        T* _val;
        typedef T Type; // typedef used in struct codegen, not here.
        static const x10aux::RuntimeType* getRTT() { return T::getRTT(); }

        // Copy between refs of the same type
        GPUSAFE ref(const ref<T>& _ref) : _val(_ref._val) { }

        // Copy between refs of the same type
        // FIXME: something is wrong with the return value;
        // r1 = r2 = r3 doesn't work in xlC
        GPUSAFE const ref<T>& operator=(const ref<T>& _ref) {
            _val = _ref._val;
            return *this;
        }

        // Declare a ref: no need for a NULL __ref
        // The frontend will guarantee ref will not be accessed prior to initialization
        GPUSAFE ref() {
        }

        // This is the big one -- turns a pointer into a ref
        // currently an implicit conversion
        GPUSAFE ref(T* const val) : _val(val) {
        }

        // Allow conversions between ref<S> and ref<T>.
        // Because we have no multiple inheritance, we can
        // use a re-interpret cast here. Bad casts should
        // never happen, as the only places this operation is used are:
        //   class_cast (which is guarded by a check)
        //   class_cast_unchecked (front-end ensures they are safe)
        //   upcasts from x10 code
        // all operator T are implicit conversions, this is no exception

        // Allow the construction of a ref<T> from a ref<S>
        template<class S> GPUSAFE ref(const ref<S>& _ref)
          : _val(reinterpret_cast<T*>(_ref._val)) {
        }

        // Allow the assignment of a ref<S> to a ref<T>
        template<class S> GPUSAFE const ref<T> &operator=(const ref<S>& _ref) {
            _val = reinterpret_cast<T*>(_ref._val);
            return *this;
        }

        T& GPUSAFE operator*() const {
            return *(T*)_val;
        }

        T* GPUSAFE operator->() const { 
            return (T*)_val;
        }

        bool GPUSAFE isNull() const {
            return _val == NULL;
        }

        // trivial operations that compare the contents of the two refs
        bool operator==(const ref<T>& _ref) const { return _val == _ref._val; }
        bool operator!=(const ref<T>& _ref) const { return _val != _ref._val; }
    };

#ifndef NO_IOSTREAM
    template<class T> std::ostream& operator<<(std::ostream& o, ref<T> s) {
        if (s.isNull()) {
            o << "null";
        } else {
            o << *s;
        }
        return o;
    }
#endif

    void throwNPE() X10_PRAGMA_NORETURN;

    template <class T> inline ref<T> nullCheck(ref<T> obj) {
        #if !defined(NO_NULL_CHECKS) && !defined(NO_EXCEPTIONS)
        if (obj.isNull()) throwNPE();
        #endif
        return obj;
    }

    // A no-op for non-refs
    template <class T> inline T nullCheck(T str) {
        return str;
    }

    #define X10_NULL x10aux::ref<x10::lang::NullType>(NULL)

} //namespace x10aux


#endif

// vim:tabstop=4:shiftwidth=4:expandtab
