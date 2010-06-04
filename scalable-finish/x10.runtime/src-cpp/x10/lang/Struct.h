#ifndef __X10_LANG_STRUCT_H
#define __X10_LANG_STRUCT_H

#include <x10/lang/Struct.struct_h>

namespace x10 {
    namespace lang {

        struct Place;

        class Struct_methods {
        public:
            static void _instance_init(x10::lang::Struct *this_);
            static void _constructor(x10::lang::Struct *this_);
            template<class T> static x10::lang::Place home(T v);
            template<class T> static x10_boolean at(T v, x10aux::ref<x10::lang::Object> p);
            template<class T> static x10_boolean at(T v, x10::lang::Place p);
        };
    }
}
#endif // X10_LANG_STRUCT_H

namespace x10 {
    namespace lang {
        class Struct;
    }
}

#ifndef X10_LANG_STRUCT_H_NODEPS
#define X10_LANG_STRUCT_H_NODEPS
#include <x10/lang/Place.h>
#ifndef X10_LANG_STRUCT_H_IMPLEMENTATION
#define X10_LANG_STRUCT_H_IMPLEMENTATION

namespace x10 {
    namespace lang {

        template<class T> inline Place Struct_methods::home(T v) {
            return Place_methods::place((x10_int) x10aux::here);
        }

        template<class T> inline x10_boolean Struct_methods::at(T v, x10aux::ref<Object> p) {
            return x10aux::here == p->location;
        }

        template<class T> inline x10_boolean Struct_methods::at(T v, Place p) {
            return x10aux::here == p->FMGL(id);
        }

    }
}

#endif // X10_LANG_STRUCT_H_IMPLEMENTATION
#endif // X10_LANG_STRUCT_H_NODEPS

// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
