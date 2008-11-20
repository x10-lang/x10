#ifndef X10AUX_CLASS_CAST_H
#define X10AUX_CLASS_CAST_H

#include <x10aux/config.h>

#include <x10aux/RTT.h>

#include <x10/lang/Box.h>

namespace x10aux {

    // T stands for "to"
    // F stands for "from"

    #ifndef NO_EXCEPTIONS
    #define CHECK_CAST(frtt,T) \
            _CAST_(frtt->name()<<" to "<<getRTT<T>()->name()); \
            if (!frtt->subtypeOf(getRTT<T>())) { \
                std::cerr<<"Your type cast was eaten by a grue."<<std::endl; \
            }
    #else
    #define CHECK_CAST(F,T)
    #endif

    template<class T, class F> struct ClassCast {
        static T class_cast (F obj) {
            CHECK_CAST(obj->_type(),T)
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

    
/*
    template<typename F> float class_cast<float> (F obj) {
        return static_cast<float>(obj);
    }
*/





    template<typename T, typename F> T class_cast (F obj) {
        return ClassCast<T,F>::class_cast(obj);
    }

}

    

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
