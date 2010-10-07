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

#ifndef X10AUX_STRUCT_EQUALS_H
#define X10AUX_STRUCT_EQUALS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/captured_lval.h>

namespace x10aux {

    extern GPUSAFE x10_boolean compare_references_slow(ref<x10::lang::Reference> x, ref<x10::lang::Reference> y);
    inline GPUSAFE x10_boolean compare_references(ref<x10::lang::Reference> x, ref<x10::lang::Reference> y) {
        if (x == y) return true;
        if (x.isNull()) return y.isNull();
        return compare_references_slow(x, y);
    }        

    /*
     * Inner level of dispatching to cover combinations of:
     *   ref
     *   user-defined structs
     *   built-in C types
     */

    template<class T, class U> struct StructEquals { static inline GPUSAFE x10_boolean _(T x, U y) {
        return x._struct_equals(y); // two structs
    } };

    template<class T, class U> struct StructEquals<ref<T>,U> { static inline GPUSAFE x10_boolean _(ref<T> x, U y) {
        return false; // a ref and a struct
    } };

    template<class T, class U> struct StructEquals<T,ref<U> > { static inline GPUSAFE x10_boolean _(T x, ref<U> y) {
        return false; // a struct and a ref
    } };

    template<class T, class U> struct StructEquals<ref<T>,ref<U> > { static inline GPUSAFE x10_boolean _(ref<T> x, ref<U> y) {
        return compare_references(x, y); // two refs
    } };


    /*
     * Outer level of dispatching to cannonicalize to only rval types
     * and bound the explosion of possible combinations
     */

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

    template<class T, class U> inline x10_boolean struct_equals(T x, U y) {
        return StructEquals<T,U>::_(x, y);
    }
    template<class T, class U> inline x10_boolean struct_equals(captured_ref_lval<T> x, U y) {
        return StructEquals<ref<T>,U>::_(ref<T>(*x), y);
    }
    template<class T, class U> inline x10_boolean struct_equals(T x, captured_ref_lval<U> y) {
        return StructEquals<T,ref<U> >::_(x, ref<U>(*y));
    }
    template<class T, class U> inline x10_boolean struct_equals(captured_ref_lval<T> x, captured_ref_lval<U> y) {
        return StructEquals<ref<T>,ref<U> >::_(ref<T>(*x), ref<U>(*y));
    }
    template<class T, class U> inline x10_boolean struct_equals(captured_struct_lval<T> x, U y) {
        return struct_equals(*x, y);
    }
    template<class T, class U> inline x10_boolean struct_equals(T x, captured_struct_lval<U> y) {
        return struct_equals(x, *y);
    }
    template<class T, class U> inline x10_boolean struct_equals(captured_struct_lval<T> x, captured_struct_lval<U> y) {
        return struct_equals(*x, *y);
    }
}

#endif /* X10AUX_STRUCT_EQUALS_H */
