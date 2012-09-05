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

#ifndef X10AUX_BASIC_FUNCTIONS_H
#define X10AUX_BASIC_FUNCTIONS_H

#include <x10aux/config.h>
#include <x10aux/hash.h>
#include <x10aux/string_utils.h>

#ifndef X10AUX_THROW_H_NODEPS
#define X10AUX_THROW_H_NODEPS
#include <x10aux/throw.h>
#undef X10AUX_THROW_H_NODEPS
#endif

#define X10_LANG_IBOX_NODEPS
#include <x10/lang/IBox.h>
#undef X10_LANG_IBOX_NODEPS

namespace x10aux {

    /******* type_name ********/

    template<class T> inline x10::lang::String* type_name(T* x) {
        x10::lang::Reference* xAsRef = reinterpret_cast<x10::lang::Reference*>(x);
        return string_utils::lit(nullCheck(xAsRef)->_type()->name());
    }

    template<class T> inline x10::lang::String* type_name(captured_ref_lval<T> x) {
        return type_name(*x);
    }

    template<class T> inline x10::lang::String* type_name(captured_struct_lval<T> x) {
        return type_name(*x);
    }

    template<typename T> inline x10::lang::String* type_name(T x) {
        return string_utils::lit(getRTT<T>()->name());
    }


    /******* struct_equals ********/

    extern GPUSAFE x10_boolean compare_references_slow(x10::lang::Reference* x, x10::lang::Reference* y);
    inline GPUSAFE x10_boolean compare_references(x10::lang::Reference* x, x10::lang::Reference* y) {
        if (x == y) return true;
        if (NULL == x) return NULL == y;
        if (NULL == y) return false;
        return compare_references_slow(x, y);
    }        

    /*
     *Inner level of dispatching to actually do the comparisons
     */
    
    inline x10_boolean struct_equals_inner(const x10_double x,  const x10_double y)  { return x==y; }
    inline x10_boolean struct_equals_inner(const x10_float x,   const x10_float y)   { return x==y; }
    inline x10_boolean struct_equals_inner(const x10_long x,    const x10_long y)    { return x==y; }
    inline x10_boolean struct_equals_inner(const x10_int x,     const x10_int y)     { return x==y; }
    inline x10_boolean struct_equals_inner(const x10_short x,   const x10_short y)   { return x==y; }
    inline x10_boolean struct_equals_inner(const x10_byte x,    const x10_byte y)    { return x==y; }
    inline x10_boolean struct_equals_inner(const x10_ulong x,   const x10_ulong y)   { return x==y; }
    inline x10_boolean struct_equals_inner(const x10_uint x,    const x10_uint y)    { return x==y; }
    inline x10_boolean struct_equals_inner(const x10_ushort x,  const x10_ushort y)  { return x==y; }
    inline x10_boolean struct_equals_inner(const x10_ubyte x,   const x10_ubyte y)   { return x==y; }
    inline x10_boolean struct_equals_inner(const x10_char x,    const x10_char y)    { return x.v==y.v; }
    inline x10_boolean struct_equals_inner(const x10_boolean x, const x10_boolean y) { return x==y; }
    
    template<class T, class U> inline x10_boolean struct_equals_inner(T x, U y) {
        return x._struct_equals(y); // two structs
    }

    template<class T, class U> inline x10_boolean struct_equals_inner(T* x, U y) {
        // ref and struct. The ref could be an IBox<U>, so we have to handle that special case
        x10::lang::Reference* xAsRef = reinterpret_cast<x10::lang::Reference*>(x);
        if (NULL != x && xAsRef->_type()->equals(getRTT<U>())) {
            x10::lang::IBox<U>* xAsIBox = reinterpret_cast<x10::lang::IBox<U>*>(x);
            return struct_equals_inner(xAsIBox->value, y);
        } else {
            return false;
        }
    }

    template<class T, class U> inline x10_boolean struct_equals_inner(T x, U* y) {
        // struct and ref. The ref could be an IBox<T>, so we have to handle that special case
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<T>())) {
            x10::lang::IBox<T>* yAsIBox = reinterpret_cast<x10::lang::IBox<T>*>(y);
            return struct_equals_inner(x, yAsIBox->value);
        } else {
            return false;
        }
    }

    template<class T, class U> inline GPUSAFE x10_boolean struct_equals_inner(T* x, U* y) {
        x10::lang::Reference* xAsRef = reinterpret_cast<x10::lang::Reference*>(x);
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        return compare_references(xAsRef, yAsRef); // two refs
    }

    /*
     * Outer level of dispatching to cannonicalize to only rval types
     * and bound the explosion of possible combinations
     */

    template<class T, class U> inline x10_boolean struct_equals(T x, U y) {
        return struct_equals_inner(x, y);
    }
    template<class T, class U> inline x10_boolean struct_equals(captured_ref_lval<T> x, U y) {
        return struct_equals_inner(*x, y);
    }
    template<class T, class U> inline x10_boolean struct_equals(T x, captured_ref_lval<U> y) {
        return struct_equals_inner(x, *y);
    }
    template<class T, class U> inline x10_boolean struct_equals(captured_ref_lval<T> x, captured_ref_lval<U> y) {
        return struct_equals_inner(*x, *y);
    }
    template<class T, class U> inline x10_boolean struct_equals(captured_struct_lval<T> x, U y) {
        return struct_equals_inner(*x, y);
    }
    template<class T, class U> inline x10_boolean struct_equals(T x, captured_struct_lval<U> y) {
        return struct_equals_inner(x, *y);
    }
    template<class T, class U> inline x10_boolean struct_equals(captured_struct_lval<T> x, captured_struct_lval<U> y) {
        return struct_equals_inner(*x, *y);
    }
    
    /******* equals ********/

    // covers all heap-allocated values (Objects, Functions, Structs boxes to interface types)
    template<class T> inline x10_boolean equals(T* x, x10::lang::Any* y) {
        x10::lang::Reference* xAsRef = reinterpret_cast<x10::lang::Reference*>(x);
        return nullCheck(xAsRef)->equals(y);
    }

    template<class T> inline x10_boolean equals(captured_ref_lval<T> x, x10::lang::Any* y) {
        return equals(*x, y);
    }

    template<class T> inline x10_boolean equals(captured_struct_lval<T> x, x10::lang::Any* y) {
        return equals(*x, y);
    }
    
    // covers all X10 Structs that are not built-in C++ types and NativeRep'ed
    template<class T> inline x10_boolean equals(T x, x10::lang::Any*  y) { return x->equals(y); }

    // Cover all X10 Structs that are built-in C++ types
    inline x10_boolean equals(x10_boolean x, x10::lang::Any* y) {
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<x10_boolean>())) {
            x10::lang::IBox<x10_boolean>* yAsIBox = reinterpret_cast<x10::lang::IBox<x10_boolean>*>(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_byte x, x10::lang::Any* y) {
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<x10_byte>())) {
            x10::lang::IBox<x10_byte>* yAsIBox = reinterpret_cast<x10::lang::IBox<x10_byte>*>(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_ubyte x, x10::lang::Any* y) {
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<x10_ubyte>())) {
            x10::lang::IBox<x10_ubyte>* yAsIBox = reinterpret_cast<x10::lang::IBox<x10_ubyte>*>(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_char x, x10::lang::Any* y) {
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<x10_char>())) {
            x10::lang::IBox<x10_char>* yAsIBox = reinterpret_cast<x10::lang::IBox<x10_char>*>(y);
            return x.v == yAsIBox->value.v;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_short x, x10::lang::Any* y) {
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<x10_short>())) {
            x10::lang::IBox<x10_short>* yAsIBox = reinterpret_cast<x10::lang::IBox<x10_short>*>(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_ushort x, x10::lang::Any* y) {
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<x10_ushort>())) {
            x10::lang::IBox<x10_ushort>* yAsIBox = reinterpret_cast<x10::lang::IBox<x10_ushort>*>(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_int x, x10::lang::Any* y) {
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<x10_int>())) {
            x10::lang::IBox<x10_int>* yAsIBox = reinterpret_cast<x10::lang::IBox<x10_int>*>(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_uint x, x10::lang::Any* y) {
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<x10_uint>())) {
            x10::lang::IBox<x10_uint>* yAsIBox = reinterpret_cast<x10::lang::IBox<x10_uint>*>(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_long x, x10::lang::Any* y) {
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<x10_long>())) {
            x10::lang::IBox<x10_long>* yAsIBox = reinterpret_cast<x10::lang::IBox<x10_long>*>(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_ulong x, x10::lang::Any* y) {
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<x10_ulong>())) {
            x10::lang::IBox<x10_ulong>* yAsIBox = reinterpret_cast<x10::lang::IBox<x10_ulong>*>(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_float x, x10::lang::Any* y) {
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<x10_float>())) {
            x10::lang::IBox<x10_float>* yAsIBox = reinterpret_cast<x10::lang::IBox<x10_float>*>(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_double x, x10::lang::Any* y) {
        x10::lang::Reference* yAsRef = reinterpret_cast<x10::lang::Reference*>(y);
        if (NULL != y && yAsRef->_type()->equals(getRTT<x10_double>())) {
            x10::lang::IBox<x10_double>* yAsIBox = reinterpret_cast<x10::lang::IBox<x10_double>*>(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(const x10_double x,  const x10_double y)  { return x==y; }
    inline x10_boolean equals(const x10_float x,   const x10_float y)   { return x==y; }
    inline x10_boolean equals(const x10_long x,    const x10_long y)    { return x==y; }
    inline x10_boolean equals(const x10_int x,     const x10_int y)     { return x==y; }
    inline x10_boolean equals(const x10_short x,   const x10_short y)   { return x==y; }
    inline x10_boolean equals(const x10_byte x,    const x10_byte y)    { return x==y; }
    inline x10_boolean equals(const x10_ulong x,   const x10_ulong y)    { return x==y; }
    inline x10_boolean equals(const x10_uint x,    const x10_uint y)     { return x==y; }
    inline x10_boolean equals(const x10_ushort x,  const x10_ushort y)   { return x==y; }
    inline x10_boolean equals(const x10_ubyte x,   const x10_ubyte y)    { return x==y; }
    inline x10_boolean equals(const x10_char x,    const x10_char y)    { return x.v==y.v; }
    inline x10_boolean equals(const x10_boolean x, const x10_boolean y) { return x==y; }

    /******* hash_code ********/

    template<class T> inline x10_int hash_code(T* x) {
        x10::lang::Reference* xAsRef = reinterpret_cast<x10::lang::Reference*>(x);
        return nullCheck(xAsRef)->hashCode();
    }

    template<class T> inline x10_int hash_code(captured_ref_lval<T> x) {
        return hash_code(*x);
    }

    template<class T> inline x10_int hash_code(captured_struct_lval<T> x) {
        return hash_code(*x);
    }
    
    template<class T> inline x10_int hash_code(T x) {
        return x->hashCode();
    }

    inline x10_int hash_code(const x10_boolean x) { return x; }
    inline x10_int hash_code(const x10_byte x) { return x; }
    inline x10_int hash_code(const x10_ubyte x) { return x; }
    inline x10_int hash_code(const x10_short x) { return x; }
    inline x10_int hash_code(const x10_ushort x) { return x; }
    inline x10_int hash_code(const x10_char x) { return x.v; }
    inline x10_int hash_code(const x10_int x) { return x; }
    inline x10_int hash_code(const x10_uint x) { return x; }
    inline x10_int hash_code(const x10_ulong x) {
        return (x10_int)(x ^ (x >> 32));
    }
    inline x10_int hash_code(const x10_long x) {
        return hash_code((x10_ulong)x);
    }
    x10_int hash_code(const x10_double x);
    x10_int hash_code(const x10_float x);

    
    /******* to_string ********/

    template<class T> x10::lang::String* to_string(T* x) {
        x10::lang::Reference* xAsRef = reinterpret_cast<x10::lang::Reference*>(x);
        return nullCheck(xAsRef)->toString();
    }
    template<class T> x10::lang::String* to_string(captured_ref_lval<T> x) {
        return to_string(*x);
    }
    template<class T> x10::lang::String* to_string(captured_struct_lval<T> x) {
        return to_string(*x);
    }

    template<class T> x10::lang::String* to_string(T x) {
        return x.toString();
    }

    x10::lang::String* to_string(x10_boolean v);
    x10::lang::String* to_string(x10_ubyte v);
    x10::lang::String* to_string(x10_byte v);
    x10::lang::String* to_string(x10_ushort v);
    x10::lang::String* to_string(x10_short v);
    x10::lang::String* to_string(x10_uint v);
    x10::lang::String* to_string(x10_int v);
    x10::lang::String* to_string(x10_ulong v);
    x10::lang::String* to_string(x10_long v);

    x10::lang::String* to_string(x10_float v);
    x10::lang::String* to_string(x10_double v);

    x10::lang::String* to_string(x10_char v);


    /*
     * Wrapers around to_string to translate null to "null"
     */
    template<class T> x10::lang::String* safe_to_string(T* v) {
        if (NULL == v) return string_utils::lit("null");
        return to_string(v);
    }
    template<class T> x10::lang::String* safe_to_string(captured_ref_lval<T> v) {
        return safe_to_string(*v);
    }
    template<class T> x10::lang::String* safe_to_string(captured_struct_lval<T> v) {
        return to_string(*v);
    }
    template<class T> x10::lang::String* safe_to_string(T v) {
        return to_string(v);
    }
    
    /******* zeroValue ********/
    template<class T> struct Zero {
        static T _() {
            T ans;
            memset(&ans, 0, sizeof(T));
            return ans;
        }
    };
    template<class T> struct Zero<T*> {
        static T* _() { return NULL; }
    };
    #define X10_PRIM_ZERO(T) template<> struct Zero<T> { static T _() { return static_cast<T>(0); } };
    X10_PRIM_ZERO(x10_byte)
    X10_PRIM_ZERO(x10_ubyte)
    X10_PRIM_ZERO(x10_short)
    X10_PRIM_ZERO(x10_ushort)
    X10_PRIM_ZERO(x10_int)
    X10_PRIM_ZERO(x10_uint)
    X10_PRIM_ZERO(x10_long)
    X10_PRIM_ZERO(x10_ulong)
    X10_PRIM_ZERO(x10_float)
    X10_PRIM_ZERO(x10_double)
    #undef X10_PRIM_ZERO
    template<> struct Zero<x10_boolean> { static x10_boolean _() { return false; } };
    template<> struct Zero<x10_char> { static x10_char _() { return x10_char(0); } };

    template <class T> T zeroValue() { return Zero<T>::_(); }
    
}

#endif
// vim:textwidth=100:tabstop=4:shiftwidth=4:expandtab
