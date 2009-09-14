#ifndef X10AUX_CLASS_CAST_H
#define X10AUX_CLASS_CAST_H

#include <x10aux/config.h>
#include <x10aux/throw.h>

#include <x10aux/RTT.h>
#include <x10aux/ref.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/Object.h>
#include <x10/lang/Box.h>

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
        T* get() { return val.get(); }
    };
    #ifndef NO_IOSTREAM
    template<class T> std::ostream& operator<<(std::ostream& o, CAST_TRACER<T> t) {
        return o << t.get();
    }
    #endif

    template<class T> GPUSAFE ref<x10::lang::Box<T> > box(T obj) {
        _CAST_("boxed: "<<CAST_TRACER<T>(obj)<<" of type "<<TYPENAME(T));
        return x10::lang::Box<T>::_make(obj);
    }

    template<class T> GPUSAFE T unbox(ref<x10::lang::Box<T> > obj) {
        _CAST_("unboxed: "<<CAST_TRACER<ref<x10::lang::Box<T> > >(obj)<<" of type "<<TYPENAME(T));
        return obj->get();
    }

    // T stands for "to"
    // F stands for "from"

    template<class T, class F> struct ClassCastNotBothRef {
        // All possibilities accounted for, if you got here something has gone wrong
    };

    template<class T, class F> struct ClassCastNotBothRef<ref<T>,F> {
        // All possibilities accounted for, if you got here something has gone wrong
        static ref<T> _ (F obj, bool checked) {
            throwClassCastException();
            return NULL;
        }
    };

    template<class T, class F> struct ClassCastNotBothRef<T,ref<F> > {
      // All possibilities accounted for, if you got here something has gone wrong
        static T _ (ref<F> obj, bool checked) {
            throwClassCastException();
            return NULL;
        }
    };

    // Box primitives on casting to interfaces
    #define PRIMITIVE_INTERFACE_CAST(T,F) \
    template<> struct ClassCastNotBothRef<ref<T>,F> { \
        static GPUSAFE ref<T> _ (F obj, bool checked) { \
            _CAST_(TYPENAME(F) <<" converted to "<<TYPENAME(ref<T>)); \
            return class_cast<ref<T> >(box(obj), checked); \
        } \
    }
    PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_boolean);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_byte);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_char);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_short);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_int);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_long);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_ubyte);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_ushort);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_uint);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_ulong);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_float);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Object, x10_double);

    PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_boolean);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_byte);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_char);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_short);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_int);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_long);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_ubyte);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_ushort);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_uint);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_ulong);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_float);
    PRIMITIVE_INTERFACE_CAST(x10::lang::Ref, x10_double);

    // Unbox primitives on down-casting from interfaces
    #define INTERFACE_PRIMITIVE_CAST(T,F) \
    template<> struct ClassCastNotBothRef<T,ref<F> > { \
        static GPUSAFE T _ (ref<F> obj, bool checked) { \
            _CAST_(TYPENAME(ref<F>) <<" converted to "<<TYPENAME(T)); \
            return unbox(class_cast<ref<x10::lang::Box<T> > >(obj, checked)); \
        } \
    }
    INTERFACE_PRIMITIVE_CAST(x10_boolean, x10::lang::Object);
    INTERFACE_PRIMITIVE_CAST(x10_byte, x10::lang::Object);
    INTERFACE_PRIMITIVE_CAST(x10_char, x10::lang::Object);
    INTERFACE_PRIMITIVE_CAST(x10_short, x10::lang::Object);
    INTERFACE_PRIMITIVE_CAST(x10_int, x10::lang::Object);
    INTERFACE_PRIMITIVE_CAST(x10_long, x10::lang::Object);
    INTERFACE_PRIMITIVE_CAST(x10_float, x10::lang::Object);
    INTERFACE_PRIMITIVE_CAST(x10_double, x10::lang::Object);
    INTERFACE_PRIMITIVE_CAST(x10_ubyte, x10::lang::Object);
    INTERFACE_PRIMITIVE_CAST(x10_ushort, x10::lang::Object);
    INTERFACE_PRIMITIVE_CAST(x10_uint, x10::lang::Object);
    INTERFACE_PRIMITIVE_CAST(x10_ulong, x10::lang::Object);

    template<class T, class F> struct ClassCastNotBothRef<ref<T>,F*> {
        static GPUSAFE ref<T> _(F* obj) {
            return ref<T>(ref<F>(obj));
        }
    };

    // Boxing of primitives
    template<class T> struct ClassCastNotBothRef<ref<x10::lang::Box<T> >,T> {
        static GPUSAFE ref<x10::lang::Box<T> > _(T obj) {
            return box(obj);
        }
    };

    // Unboxing of primitives
    template<class T> struct ClassCastNotBothRef<T,ref<x10::lang::Box<T> > > {
        static GPUSAFE T _(ref<x10::lang::Box<T> > obj) {
            return unbox(obj);
        }
    };

    template<class T> struct ValueBox : public x10::lang::Value {
        T v;
        ValueBox(T v_) : v(v_) { }
        virtual x10_int hashCode() { return x10aux::hash_code(v); }
        virtual x10_boolean equals(x10aux::ref<x10::lang::Value> other) { return x10aux::equals(v, other); }
        virtual x10_boolean equals(x10aux::ref<x10::lang::Ref> other) { return false; }
        virtual x10_boolean _struct_equals(x10aux::ref<x10::lang::Object> p0) { return x10aux::struct_equals(v, p0); }
        virtual ref<x10::lang::String> toString() { return x10aux::to_string(v); }
    };

    // Primitive -> Value; Value -> Primitive
    #define PRIMITIVE_VALUE_CAST(P) \
    template<> struct ClassCastNotBothRef<ref<x10::lang::Value>,P> { \
        static ref<x10::lang::Value> _(P obj) { \
            _CAST_("converted to value: "<<CAST_TRACER<P>(obj)<<" of type "<<TYPENAME(P)); \
            return ref<x10::lang::Value>(new (x10aux::alloc<ValueBox<P> >()) ValueBox<P>(obj)); \
        } \
    }
    #define VALUE_PRIMITIVE_CAST(P) \
    template<> struct ClassCastNotBothRef<P,ref<x10::lang::Value> > { \
        static P _(ref<x10::lang::Value> obj) { \
            _CAST_("converted from value: "<<CAST_TRACER<ref<x10::lang::Value> >(obj)<<" of type "<<TYPENAME(P)); \
            return ref<ValueBox<P> >(obj)->v; \
        } \
    }
    #define PRIMITIVE_VALUE_CAST2(P) PRIMITIVE_VALUE_CAST(P); VALUE_PRIMITIVE_CAST(P)
    PRIMITIVE_VALUE_CAST2(x10_boolean);
    PRIMITIVE_VALUE_CAST2(x10_byte);
    PRIMITIVE_VALUE_CAST2(x10_char);
    PRIMITIVE_VALUE_CAST2(x10_short);
    PRIMITIVE_VALUE_CAST2(x10_int);
    PRIMITIVE_VALUE_CAST2(x10_long);
    PRIMITIVE_VALUE_CAST2(x10_float);
    PRIMITIVE_VALUE_CAST2(x10_double);
    PRIMITIVE_VALUE_CAST2(x10_ubyte);
    PRIMITIVE_VALUE_CAST2(x10_ushort);
    PRIMITIVE_VALUE_CAST2(x10_uint);
    PRIMITIVE_VALUE_CAST2(x10_ulong);

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

    // ClassCastBothRef
    template<class T, class F> struct ClassCastBothRef { static GPUSAFE ref<T> _(ref<F> obj, bool checked) {
        return real_class_cast<T>(obj, checked);
    } };

    // Boxing of ref types
    template<class T> struct ClassCastBothRef<x10::lang::Box<ref<T> >,T> {
        static GPUSAFE ref<x10::lang::Box<ref<T> > > _(ref<T> obj, bool checked) {
            return box(obj);
        }
    };

    // Unboxing of ref types
    template<class T> struct ClassCastBothRef<T,x10::lang::Box<ref<T> > > {
        static GPUSAFE ref<T> _(ref<x10::lang::Box<ref<T> > > obj, bool checked) {
            return unbox(obj);
        }
    };


    // ClassCastNotPrimitive
    template<class T, class F> struct ClassCastNotPrimitive { static GPUSAFE T _(F obj, bool checked) {
        return ClassCastNotBothRef<T,F>::_(obj, checked);
    } };

    template<class T, class F> struct ClassCastNotPrimitive<ref<T>,ref<F> > {
        static GPUSAFE ref<T> _(ref<F> obj, bool checked) {
            _CAST_("Ref to ref cast "<<TYPENAME(T)<<" to "<<TYPENAME(T));
            return ClassCastBothRef<T,F>::_(obj, checked);
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
