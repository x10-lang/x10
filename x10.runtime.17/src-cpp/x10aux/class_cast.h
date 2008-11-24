#ifndef X10AUX_CLASS_CAST_H
#define X10AUX_CLASS_CAST_H

#include <x10aux/config.h>
#include <x10aux/throw.h>

#include <x10aux/RTT.h>

#include <x10/lang/Box.h>

#include <x10/lang/ClassCastException.h>

namespace x10aux {

    // T stands for "to"
    // F stands for "from"

    template<class T, class F> struct ClassCast {
        // all cases should be covered by the below specialisations
    };

    template<class T, class F> struct ClassCast<ref<T>,ref<F> > {
        static ref<T> class_cast (ref<F> obj) {
            if (obj==x10aux::null) {
                // NULL passes any class cast check and remains NULL
                _CAST_("Special case: null gets cast to "<<TYPENAME(T));
                return obj;
            }
            const RuntimeType *from = obj->_type();
            const RuntimeType *to = getRTT<T>();
            #ifndef NO_EXCEPTIONS
            _CAST_(from->name()<<" to "<<to->name());
            if (!from->subtypeOf(to)) {
                throwException<x10::lang::ClassCastException>();
            }
            #else
            _CAST_("UNCHECKED! "<<from->name()<<" to "<<to->name());
            #endif
            return static_cast<ref<T> >(obj);
        }
    };

    // into a box
    template<class T> struct ClassCast<ref<x10::lang::Box<T> >,T> {
        static ref<x10::lang::Box<T> > class_cast (T obj) {
            _CAST_("boxed: "<<TYPENAME(T));
            return new (alloc<x10::lang::Box<T> >()) x10::lang::Box<T> (obj);
        }
    };

    // out of a box
    template<class T> struct ClassCast<T, ref<x10::lang::Box<T> > > {
        static T class_cast (ref<x10::lang::Box<T> > obj) {
            _CAST_("unboxed: "<<TYPENAME(T));
            return obj->get();
        }
    };

    #define SPECIALISE_CAST(T,F) \
    template<> struct ClassCast<T,F> { \
        static T class_cast (F obj) { \
            _CAST_(TYPENAME(F) <<" converted to "<<TYPENAME(T)); \
            return static_cast<T>(obj); \
        } \
    }

    // make reflexive
    #define SPECIALISE_CAST2(T,F) SPECIALISE_CAST(T,F) ; SPECIALISE_CAST(F,T)

    // boolean and char can't be cast to anything except themselves (handled above)
    //SPECIALISE_CAST(x10_boolean);
    //SPECIALISE_CAST(x10_char);

    SPECIALISE_CAST2(x10_byte,x10_short);
    SPECIALISE_CAST2(x10_byte,x10_int);
    SPECIALISE_CAST2(x10_byte,x10_long);
    SPECIALISE_CAST2(x10_byte,x10_float);
    SPECIALISE_CAST2(x10_byte,x10_double);

    SPECIALISE_CAST2(x10_short,x10_int);
    SPECIALISE_CAST2(x10_short,x10_long);
    SPECIALISE_CAST2(x10_short,x10_float);
    SPECIALISE_CAST2(x10_short,x10_double);

    SPECIALISE_CAST2(x10_int,x10_long);
    SPECIALISE_CAST2(x10_int,x10_float);
    SPECIALISE_CAST2(x10_int,x10_double);

    SPECIALISE_CAST2(x10_long,x10_float);
    SPECIALISE_CAST2(x10_long,x10_double);

    SPECIALISE_CAST2(x10_float,x10_double);

/*
    // from NULL
    // [DC] I think NULL should always be a ref<Object> in the generated code
    // and we should explicitly check for NULL In the class cast.  The following won't work
    // if NULL is no-longer an int, plus it will cause ambiguity when casting from int to Box<int>
    template<class T> struct ClassCast<ref<T>,int> {
        static ref<T> class_cast (int ptr) {
            assert (ptr == (int)NULL);
            _CAST_("from null to : "<<getRTT<T>()->name());
            return ref<T>();
        }
    };
*/

    // note this is separate sublevel of template specialisation so we can deal with <T,T>
    // (needed because generic classes can be instantiated in ways that make casts redundant)
    template<class T, class F> struct ClassCast2 { static T class_cast (F obj) {
        return ClassCast<T,F>::class_cast(obj);
    } };
    template<class T> struct ClassCast2<T,T> { static T class_cast (T obj) {
        // nothing to do (until we have constraints)
        _CAST_("Identify cast to/from "<<TYPENAME(T));
        return obj;
    } };

    template<typename T, typename F> T class_cast (F obj) {
        return ClassCast2<T,F>::class_cast(obj);
    }

}

    

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
