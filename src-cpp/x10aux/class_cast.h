#ifndef X10AUX_CLASS_CAST_H
#define X10AUX_CLASS_CAST_H

#include <x10aux/config.h>

#include <x10aux/RTT.h>

#include <x10/lang/Box.h>

#include <x10/lang/ClassCastException.h>

namespace x10aux {

    // T stands for "to"
    // F stands for "from"

    template<class T, class F> struct ClassCast {
        static T class_cast (F obj) {
            const RuntimeType *from = obj->_type();
            const RuntimeType *to = getRTT<T>();
            #ifndef NO_EXCEPTIONS
            _CAST_(from->name()<<" to "<<to->name());
            if (!from->subtypeOf(to)) {
                typedef x10::lang::ClassCastException CCE;
                throw (ref<CCE>) new (alloc<CCE>()) CCE();
            }
            return static_cast<T>(obj);
            #else
            _CAST_("UNCHECKED! "<<from->name()<<" to "<<to->name());
            #endif
            return static_cast<T>(obj);
        }
    };

    // needed because alloc_rail returns a pointer but can be casted
    template<class T, class F> struct ClassCast<T,F*> {
        static T class_cast (F *obj) {
            // forward to the ref version
            return ClassCast<T,ref<F> >::class_cast(obj);
        }
    };

    // into a box
    template<class T> struct ClassCast<ref<x10::lang::Box<T> >,T> {
        static ref<x10::lang::Box<T> > class_cast (T obj) {
            _CAST_("boxed: "<<getRTT<T>()->name());
            return new (alloc<x10::lang::Box<T> >()) x10::lang::Box<T> (obj);
        }
    };

    // out of a box
    template<class T> struct ClassCast<T, ref<x10::lang::Box<T> > > {
        static T class_cast (ref<x10::lang::Box<T> > obj) {
            _CAST_("unboxed: "<<getRTT<T>()->name());
            return obj->get();
        }
    };

    #define SPECIALISE_CAST(TYPE) \
    template<class F> struct ClassCast<TYPE,F> { \
        static TYPE class_cast (F obj) { \
            _CAST_(getRTT<F>()->name() \
                   <<" converted to "<<getRTT<TYPE>()->name()); \
            return static_cast<TYPE>(obj); \
        } \
    };

    SPECIALISE_CAST(x10_boolean)
    SPECIALISE_CAST(x10_byte)
    SPECIALISE_CAST(x10_char)
    SPECIALISE_CAST(x10_short)
    SPECIALISE_CAST(x10_int)
    SPECIALISE_CAST(x10_long)
    SPECIALISE_CAST(x10_float)
    SPECIALISE_CAST(x10_double)

    
    template<typename T, typename F> T class_cast (F obj) {
        return ClassCast<T,F>::class_cast(obj);
    }

}

    

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
