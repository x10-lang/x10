#ifndef X10AUX_CLASS_CAST_H
#define X10AUX_CLASS_CAST_H

#include <x10aux/config.h>
#include <x10aux/throw.h>

#include <x10aux/RTT.h>
#include <x10aux/ref.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/Object.h>

namespace x10aux {

    extern void throwClassCastException() X10_PRAGMA_NORETURN;
    
    template<typename T, typename F> GPUSAFE T class_cast(F obj);
    template<typename T, typename F> GPUSAFE T class_cast(F obj, bool checked);

    template<class T> struct CAST_TRACER {
        CAST_TRACER(T val_) : val(val_) { }
        T val;
        T get() { return val; }
    };
    template<class T> struct CAST_TRACER<ref<T> > {
        CAST_TRACER(ref<T> val_) : val(val_) { }
        ref<T> val;
        T* get() { return val.operator->(); }
    };
    #ifndef NO_IOSTREAM
    template<class T> std::ostream& operator<<(std::ostream& o, CAST_TRACER<T> t) {
        return o << t.get();
    }
    #endif

    // T stands for "to"
    // F stands for "from"

    template<class T> static GPUSAFE ref<T> real_class_cast(ref<x10::lang::Object> obj, bool checked) {
        if (obj == x10aux::null) {
            // NULL passes any class cast check and remains NULL
            _CAST_("Special case: null gets cast to "<<TYPENAME(ref<T>));
            return obj;
        }
        if (checked) {
            const RuntimeType *from = obj->_type();
            const RuntimeType *to = getRTT<ref<T> >();
            #ifndef NO_EXCEPTIONS
            _CAST_(from->name()<<" to "<<to->name());
            if (!from->subtypeOf(to)) {
                throwClassCastException();
            }
            #else
            (void) from; (void) to;
            _CAST_("UNCHECKED! "<<from->name()<<" to "<<to->name());
            #endif
        }
        return static_cast<ref<T> >(obj);
    }

    // ClassCastNotPrimitive
    template<class T, class F> struct ClassCastNotPrimitive { static GPUSAFE T _(F obj, bool checked) {
        // If we get here, then we are doing a ref==>struct or struct==>ref, which is not allowed in X10 2.0.
        throwClassCastException();
        return NULL;
    } };

    template<class T, class F> struct ClassCastNotPrimitive<ref<T>,ref<F> > {
        static GPUSAFE ref<T> _(ref<F> obj, bool checked) {
            _CAST_("Ref to ref cast "<<TYPENAME(T)<<" to "<<TYPENAME(T));
            return real_class_cast<T>(obj, checked);
        }
    };

    // This is the second level that recognises primitive casts
    template<class T, class F> struct ClassCastPrimitive { static GPUSAFE T _(F obj, bool checked) {
        // if we get here it's not a primitive cast
        _CAST_("Not a primitive cast "<<TYPENAME(T)<<" to "<<TYPENAME(T));
        return ClassCastNotPrimitive<T,F>::_(obj, checked);
    } };

    #define PRIMITIVE_CAST(T,F) \
    template<> struct ClassCastPrimitive<T,F> { \
        static GPUSAFE T _ (F obj, bool checked) { \
            _CAST_(TYPENAME(F) <<" converted to "<<TYPENAME(T)); \
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


    // first level of template specialisation that recognises <T,T>
    // (needed because generic classes can be instantiated in ways that make casts redundant)
    template<class T, class F> struct ClassCast { static GPUSAFE T _ (F obj, bool checked) {
        return ClassCastPrimitive<T,F>::_(obj, checked);
    } };
    template<class T> struct ClassCast<T,T> { static GPUSAFE T _ (T obj, bool checked) {
        // nothing to do (until we have constraints)
        _CAST_("Identity cast to/from "<<TYPENAME(T));
        return obj;
    } };

    template<typename T, typename F> GPUSAFE T class_cast(F obj) {
        return ClassCast<T,F>::_(obj, true);
    }

    template<typename T, typename F> GPUSAFE T class_cast(F obj, bool checked) {
        return ClassCast<T,F>::_(obj, checked);
    }

    template<typename T, typename F> GPUSAFE T class_cast_unchecked(F obj) {
        return ClassCast<T,F>::_(obj, false);
    }
}

    

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
