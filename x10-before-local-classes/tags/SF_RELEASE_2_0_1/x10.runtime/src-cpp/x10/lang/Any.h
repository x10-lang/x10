#ifndef X10_LANG_ANY_H
#define X10_LANG_ANY_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/ref.h>
#include <x10/lang/Reference.h>

#define X10_LANG_PLACE_H_NODEPS
#include <x10/lang/Place.struct_h>
#undef X10_LANG_PLACE_H_NODEPS

namespace x10 {
    namespace lang {
        class Object;
        class String;
        
        class Any {
        public:
            RTT_H_DECLS_INTERFACE

            template <class I> struct itable {
                itable(x10_boolean (I::*_m0__at)(x10aux::ref<x10::lang::Object>),
                       x10_boolean (I::*_m1__at)(x10::lang::Place),
                       x10_boolean (I::*equals)(x10aux::ref<x10::lang::Any>),
                       x10_int (I::*hashCode)(),
                       x10::lang::Place (I::*home)(),
                       x10aux::ref<x10::lang::String> (I::*toString)(),
                       x10aux::ref<x10::lang::String> (I::*typeName)()) : _m0__at(_m0__at), _m1__at(_m1__at), equals(equals), hashCode(hashCode),
                                                                          home(home), toString(toString), typeName(typeName) {}

                x10_boolean (I::*_m0__at)(x10aux::ref<x10::lang::Object>);
                x10_boolean (I::*_m1__at)(x10::lang::Place);
                x10_boolean (I::*equals)(x10aux::ref<x10::lang::Any>);
                x10_int (I::*hashCode)();
                x10::lang::Place (I::*home)();
                x10aux::ref<x10::lang::String> (I::*toString)();
                x10aux::ref<x10::lang::String> (I::*typeName)();
            };
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
