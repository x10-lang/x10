/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef X10AUX_CLASS_CAST_H
#define X10AUX_CLASS_CAST_H

#include <x10aux/config.h>
#include <x10aux/throw.h>
#include <x10aux/RTT.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/IBox.h>

namespace x10aux {

    /*
     * Throughout this file:
     *  T stands for "to"
     *  F stands for "from"
     */
    
    template<typename T, typename F> GPUSAFE T class_cast(F obj);
    template<typename T, typename F> GPUSAFE T class_cast(F obj, bool checked);

    template<class T> static inline GPUSAFE T* real_class_cast(::x10::lang::Reference* obj, bool checked) {
        if (checked && NULL != obj) {
            const RuntimeType *from = obj->_type();
            const RuntimeType *to = getRTT<T>();
            if (!from->subtypeOf(to)) {
                throwClassCastException(from, to);
            }
        }
        return reinterpret_cast<T*>(obj);
    }

    // ClassCastNotPrimitive
    template<class T, class F> struct ClassCastNotPrimitive { static GPUSAFE T _(F obj, bool checked) {
        // If we get here, then the code must be doing a class cast which
        // is outside the X10 type system (ie, it must fail).
        // 
        // Many such cases will be flagged as static errors by the X10 typechecker,
        // however it is possible (using generic types) to write statically correct
        // X10 code that will result in an unconditional class cast exception at runtime.
        // In particular, consider:
        //
        // class Cast[S,T] { def m(s:S):T = s as T; }
        // new Cast[int,String]().m(10);
        // 

        const RuntimeType *from = getRTT<F>();
        const RuntimeType *to = getRTT<T>();
        throwClassCastException(from, to);

        // DUMMY return.  This is unreachable
        T dummy;
        return dummy;
    } };

    template<class T, class F> struct ClassCastNotPrimitive<T*, F*> {
        static GPUSAFE T* _(F* obj, bool checked) {
            ::x10::lang::Reference* objAsRef = reinterpret_cast< ::x10::lang::Reference*>(obj);
            return real_class_cast<T>(objAsRef, checked);
        }
    };

    template<class T, class F> struct ClassCastNotPrimitive<T*, F> {
        static GPUSAFE T* _(F val, bool checked) {
            if (checked) {
                const RuntimeType *from = getRTT<F>();
                const RuntimeType *to = getRTT<T>();
                if (!from->subtypeOf(to)) {
                    throwClassCastException(from, to);
                }
            }
            ::x10::lang::IBox<F>* obj = new (::x10aux::alloc< ::x10::lang::IBox<F> >()) ::x10::lang::IBox<F>(val);
            return reinterpret_cast<T*>(obj);
        }
    };
    
    template<class T, class F> struct ClassCastNotPrimitive<T, F*> {
        static GPUSAFE T _(F* val, bool checked) {
            const RuntimeType *to = getRTT<T>();
            if (NULL == val) {
                // NULL cannot be cast to a struct.
                throwClassCastException(NULL, to);
            }
            if (checked) {
                ::x10::lang::Reference* asRef = reinterpret_cast< ::x10::lang::Reference*>(val);
                const RuntimeType *from = asRef->_type();
                if (!from->subtypeOf(to)) {
                    throwClassCastException(from, to);
                }
            }
            ::x10::lang::IBox<T>* ibox = reinterpret_cast< ::x10::lang::IBox<T>*>(val);
            return ibox->value; 
        }
    };
    
    // This is the second level that recognises primitive casts
    template<class T, class F> struct ClassCastPrimitive { static GPUSAFE T _(F obj, bool checked) {
        // if we get here it's not a primitive cast
        return ClassCastNotPrimitive<T,F>::_(obj, checked);
    } };

    #define PRIMITIVE_CAST(T,F) \
    template<> struct ClassCastPrimitive<T,F> { \
        static GPUSAFE T _ (F obj, bool checked) { \
            return static_cast<T>(obj); \
        } \
    }

    // make reflexive
    #define PRIMITIVE_CAST2(T,F) PRIMITIVE_CAST(T,F) ; PRIMITIVE_CAST(F,T)

    // boolean can't be cast to anything except itself (handled below)
    // everything else is totally connected

    PRIMITIVE_CAST2(x10_byte,x10_short);
    PRIMITIVE_CAST2(x10_byte,x10_int);
    PRIMITIVE_CAST2(x10_byte,x10_long);
    PRIMITIVE_CAST2(x10_byte,x10_float);
    PRIMITIVE_CAST2(x10_byte,x10_double);
    PRIMITIVE_CAST2(x10_byte,x10_ubyte);
    PRIMITIVE_CAST2(x10_byte,x10_ushort);
    PRIMITIVE_CAST2(x10_byte,x10_uint);
    PRIMITIVE_CAST2(x10_byte,x10_ulong);

    PRIMITIVE_CAST2(x10_short,x10_int);
    PRIMITIVE_CAST2(x10_short,x10_long);
    PRIMITIVE_CAST2(x10_short,x10_float);
    PRIMITIVE_CAST2(x10_short,x10_double);
    PRIMITIVE_CAST2(x10_short,x10_ubyte);
    PRIMITIVE_CAST2(x10_short,x10_ushort);
    PRIMITIVE_CAST2(x10_short,x10_uint);
    PRIMITIVE_CAST2(x10_short,x10_ulong);

    PRIMITIVE_CAST2(x10_int,x10_long);
    PRIMITIVE_CAST2(x10_int,x10_float);
    PRIMITIVE_CAST2(x10_int,x10_double);
    PRIMITIVE_CAST2(x10_int,x10_ubyte);
    PRIMITIVE_CAST2(x10_int,x10_ushort);
    PRIMITIVE_CAST2(x10_int,x10_uint);
    PRIMITIVE_CAST2(x10_int,x10_ulong);

    PRIMITIVE_CAST2(x10_long,x10_float);
    PRIMITIVE_CAST2(x10_long,x10_double);
    PRIMITIVE_CAST2(x10_long,x10_ubyte);
    PRIMITIVE_CAST2(x10_long,x10_ushort);
    PRIMITIVE_CAST2(x10_long,x10_uint);
    PRIMITIVE_CAST2(x10_long,x10_ulong);

    PRIMITIVE_CAST2(x10_float,x10_double);
    PRIMITIVE_CAST2(x10_float,x10_ubyte);
    PRIMITIVE_CAST2(x10_float,x10_ushort);
    PRIMITIVE_CAST2(x10_float,x10_uint);
    PRIMITIVE_CAST2(x10_float,x10_ulong);

    PRIMITIVE_CAST2(x10_double,x10_ubyte);
    PRIMITIVE_CAST2(x10_double,x10_ushort);
    PRIMITIVE_CAST2(x10_double,x10_uint);
    PRIMITIVE_CAST2(x10_double,x10_ulong);

    PRIMITIVE_CAST2(x10_ubyte,x10_ushort);
    PRIMITIVE_CAST2(x10_ubyte,x10_uint);
    PRIMITIVE_CAST2(x10_ubyte,x10_ulong);

    PRIMITIVE_CAST2(x10_ushort,x10_uint);
    PRIMITIVE_CAST2(x10_ushort,x10_ulong);

    PRIMITIVE_CAST2(x10_uint,x10_ulong);

    #define PRIMITIVE_TO_CHAR_CAST(F)        \
        template<> struct ClassCastPrimitive<x10_char,F> {  \
        static GPUSAFE x10_char _ (F obj, bool checked) {   \
            return x10_char((x10_int)obj);                  \
        } \
    }

    PRIMITIVE_TO_CHAR_CAST(x10_byte);
    PRIMITIVE_TO_CHAR_CAST(x10_ubyte);
    PRIMITIVE_TO_CHAR_CAST(x10_short);
    PRIMITIVE_TO_CHAR_CAST(x10_ushort);
    PRIMITIVE_TO_CHAR_CAST(x10_int);
    PRIMITIVE_TO_CHAR_CAST(x10_uint);
    PRIMITIVE_TO_CHAR_CAST(x10_long);
    PRIMITIVE_TO_CHAR_CAST(x10_ulong);

    #define PRIMITIVE_FROM_CHAR_CAST(T)              \
        template<> struct ClassCastPrimitive<T,x10_char> {        \
        static GPUSAFE T _ (x10_char obj, bool checked) { \
            return static_cast<T>(obj.v); \
        } \
    }
    
    PRIMITIVE_FROM_CHAR_CAST(x10_byte);
    PRIMITIVE_FROM_CHAR_CAST(x10_ubyte);
    PRIMITIVE_FROM_CHAR_CAST(x10_short);
    PRIMITIVE_FROM_CHAR_CAST(x10_ushort);
    PRIMITIVE_FROM_CHAR_CAST(x10_int);
    PRIMITIVE_FROM_CHAR_CAST(x10_uint);
    PRIMITIVE_FROM_CHAR_CAST(x10_long);
    PRIMITIVE_FROM_CHAR_CAST(x10_ulong);
    
    // first level of template specialisation that recognises <T,T>
    // (needed because generic classes can be instantiated in ways that make casts redundant)
    template<class T, class F> struct ClassCast { static GPUSAFE T _ (F obj, bool checked) {
        return ClassCastPrimitive<T,F>::_(obj, checked);
    } };
    template<class T> struct ClassCast<T,T> { static GPUSAFE T _ (T obj, bool checked) {
        // nothing to do (until we have constraints)
        return obj;
    } };

    template<typename T, typename F> GPUSAFE T class_cast(F obj) {
        return ClassCast<T,F>::_(obj, true);
    }

    template<typename T, typename F> GPUSAFE T class_cast(F obj, bool checked) {
        return ClassCast<T,F>::_(obj, checked);
    }

    template<typename T, typename F> GPUSAFE T inline class_cast_unchecked(F obj) {
        return ClassCast<T,F>::_(obj, false);
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
