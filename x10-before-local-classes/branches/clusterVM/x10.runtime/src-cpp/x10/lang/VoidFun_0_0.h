#ifndef X10_LANG_VOIDFUN_0_0_H
#define X10_LANG_VOIDFUN_0_0_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/serialization.h>
#include <x10aux/fun_utils.h>

namespace x10 {
    namespace lang {
        class VoidFun_0_0 : public x10aux::AnyFun {
        public:
            RTT_H_DECLS_INTERFACE

            template <class I> struct itable {
                itable(void(I::*apply)()) : apply(apply) {}
                void (I::*apply)();
            };
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
