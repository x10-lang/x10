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
#include <x10aux/ref.h>
#include <x10aux/hash.h>
#include <x10/lang/String.h>

#include <x10/lang/IBox.struct_h>

namespace x10aux {

    /******* type_name ********/
    
    template<class T> inline ref<x10::lang::String> type_name(ref<T> x) {
        return x10::lang::String::Lit(((const T*)x.operator->())->_type()->name());
    }

    template<typename T> inline ref<x10::lang::String> type_name(T x) {
        return x10::lang::String::Lit(getRTT<T>()->name());
    }

    /******* get_location ********/

    template<class T> inline place get_location(ref<T> x) {
        return (ref<x10::lang::Reference>(x))->location;
    }

    template<typename T> inline place get_location(T x) {
        return x10aux::here;
    }
    
    /******* equals ********/

    // covers all heap-allocated values (Objects, Functions, Structs boxes to interface types)
    template<class T> inline x10_boolean equals(ref<T> x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> xAsRef(x);
        return xAsRef->equals(y);
    }

    // covers all X10 Structs that are not built-in C++ types and NativeRep'ed
    template<class T> inline x10_boolean equals(T x, ref<x10::lang::Any>  y) { return x->equals(y); }
    
    // Cover all X10 Structs that are built-in C++ types
    inline x10_boolean equals(x10_boolean x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> yAsRef(y);
        if (yAsRef->_type()->equals(getRTT<x10_boolean>())) {
            ref<x10::lang::IBox<x10_boolean> > yAsIBox(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }
    
    inline x10_boolean equals(x10_byte x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> yAsRef(y);
        if (yAsRef->_type()->equals(getRTT<x10_byte>())) {
            ref<x10::lang::IBox<x10_byte> > yAsIBox(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }
    
    inline x10_boolean equals(x10_ubyte x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> yAsRef(y);
        if (yAsRef->_type()->equals(getRTT<x10_ubyte>())) {
            ref<x10::lang::IBox<x10_ubyte> > yAsIBox(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }
    
    inline x10_boolean equals(x10_char x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> yAsRef(y);
        if (yAsRef->_type()->equals(getRTT<x10_char>())) {
            ref<x10::lang::IBox<x10_char> > yAsIBox(y);
            return x.v == yAsIBox->value.v;
        } else {
            return false;
        }
    }
    
    inline x10_boolean equals(x10_short x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> yAsRef(y);
        if (yAsRef->_type()->equals(getRTT<x10_short>())) {
            ref<x10::lang::IBox<x10_short> > yAsIBox(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }
    
    inline x10_boolean equals(x10_ushort x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> yAsRef(y);
        if (yAsRef->_type()->equals(getRTT<x10_ushort>())) {
            ref<x10::lang::IBox<x10_ushort> > yAsIBox(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }
    
    inline x10_boolean equals(x10_int x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> yAsRef(y);
        if (yAsRef->_type()->equals(getRTT<x10_int>())) {
            ref<x10::lang::IBox<x10_int> > yAsIBox(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_uint x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> yAsRef(y);
        if (yAsRef->_type()->equals(getRTT<x10_uint>())) {
            ref<x10::lang::IBox<x10_uint> > yAsIBox(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }
    
    inline x10_boolean equals(x10_long x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> yAsRef(y);
        if (yAsRef->_type()->equals(getRTT<x10_long>())) {
            ref<x10::lang::IBox<x10_long> > yAsIBox(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }
    
    inline x10_boolean equals(x10_ulong x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> yAsRef(y);
        if (yAsRef->_type()->equals(getRTT<x10_ulong>())) {
            ref<x10::lang::IBox<x10_ulong> > yAsIBox(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }
    
    inline x10_boolean equals(x10_float x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> yAsRef(y);
        if (yAsRef->_type()->equals(getRTT<x10_float>())) {
            ref<x10::lang::IBox<x10_float> > yAsIBox(y);
            return x == yAsIBox->value;
        } else {
            return false;
        }
    }

    inline x10_boolean equals(x10_double x, ref<x10::lang::Any> y) {
        ref<x10::lang::Reference> yAsRef(y);
        if (yAsRef->_type()->equals(getRTT<x10_double>())) {
            ref<x10::lang::IBox<x10_double> > yAsIBox(y);
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

    /*******  struct_equals (==) ********/
    
    template<class T, class U>
    inline x10_boolean struct_equals(ref<T> x, ref<U> y) {
        if (x.isNull()) {
            return y.isNull();
        } else if (y.isNull()) {
            return false; // x != null, needed for remote refs
        } else {
            ref<x10::lang::Reference> xAsObj = x;
            ref<x10::lang::Reference> yAsObj = y;
            return xAsObj->_struct_equals(yAsObj);
        }
    }

    template<class T, class U>
    inline x10_boolean struct_equals(T x, U y) {
        return x._struct_equals(y);
    }

    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_double y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_float y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_long y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_int y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_short y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_byte y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_ulong y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_uint y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_ushort y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_ubyte y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_char y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_boolean y) { return false; }

    template<class T>
    inline x10_boolean struct_equals(x10_double y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_float y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_long y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_int y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_short y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_byte y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_ulong y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_uint y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_ushort y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_ubyte y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_char y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_boolean y, ref<T> x) { return false; }

    inline x10_boolean struct_equals(const x10_double x,  const x10_double y)  { return x==y; }
    inline x10_boolean struct_equals(const x10_float x,   const x10_float y)   { return x==y; }
    inline x10_boolean struct_equals(const x10_long x,    const x10_long y)    { return x==y; }
    inline x10_boolean struct_equals(const x10_int x,     const x10_int y)     { return x==y; }
    inline x10_boolean struct_equals(const x10_short x,   const x10_short y)   { return x==y; }
    inline x10_boolean struct_equals(const x10_byte x,    const x10_byte y)    { return x==y; }
    inline x10_boolean struct_equals(const x10_ulong x,   const x10_ulong y)    { return x==y; }
    inline x10_boolean struct_equals(const x10_uint x,    const x10_uint y)     { return x==y; }
    inline x10_boolean struct_equals(const x10_ushort x,  const x10_ushort y)   { return x==y; }
    inline x10_boolean struct_equals(const x10_ubyte x,   const x10_ubyte y)    { return x==y; }
    inline x10_boolean struct_equals(const x10_char x,    const x10_char y)    { return x.v==y.v; }
    inline x10_boolean struct_equals(const x10_boolean x, const x10_boolean y) { return x==y; }

    /******* hash_code ********/

    template<class T> inline x10_int hash_code(ref<T> x) {
        return nullCheck(x)->hashCode();
    }
    
    template<class T> inline x10_int hash_code(T x) {
        return x->hashCode();
    }
    
    inline x10_int hash_code(const x10_double x) {
        return hash(reinterpret_cast<const unsigned char*>(&x), sizeof(x));
    }
    inline x10_int hash_code(const x10_float x) {
        return hash(reinterpret_cast<const unsigned char*>(&x), sizeof(x));
    }
    inline x10_int hash_code(const x10_long x) {
        return hash(reinterpret_cast<const unsigned char*>(&x), sizeof(x));
    }
    inline x10_int hash_code(const x10_int x) { return x; }
    inline x10_int hash_code(const x10_short x) { return x; }
    inline x10_int hash_code(const x10_byte x) { return x; }
    inline x10_int hash_code(const x10_ulong x) {
        return hash(reinterpret_cast<const unsigned char*>(&x), sizeof(x));
    }
    inline x10_int hash_code(const x10_uint x) { return x; }
    inline x10_int hash_code(const x10_ushort x) { return x; }
    inline x10_int hash_code(const x10_ubyte x) { return x; }
    inline x10_int hash_code(const x10_char x) { return x.v; }
    inline x10_int hash_code(const x10_boolean x) { return x; }

    /******* to_string ********/
    
    template<class T> ref<x10::lang::String> to_string(ref<T> x) {
        ref<x10::lang::Reference> asRef = x;
        return nullCheck(asRef)->toString();
    }

    template<class T> ref<x10::lang::String> to_string(T x) {
        return x.toString();
    }

    ref<x10::lang::String> to_string(x10_boolean v);
    ref<x10::lang::String> to_string(x10_ubyte v);
    ref<x10::lang::String> to_string(x10_byte v);
    ref<x10::lang::String> to_string(x10_ushort v);
    ref<x10::lang::String> to_string(x10_short v);
    ref<x10::lang::String> to_string(x10_uint v);
    ref<x10::lang::String> to_string(x10_int v);
    ref<x10::lang::String> to_string(x10_ulong v);
    ref<x10::lang::String> to_string(x10_long v);

    ref<x10::lang::String> to_string(x10_float v);
    ref<x10::lang::String> to_string(x10_double v);

    ref<x10::lang::String> to_string(x10_char v);
}

#endif
// vim:textwidth=100:tabstop=4:shiftwidth=4:expandtab
