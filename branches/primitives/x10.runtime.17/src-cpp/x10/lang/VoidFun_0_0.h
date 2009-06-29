#ifndef X10_LANG_VOIDFUN_0_0_H
#define X10_LANG_VOIDFUN_0_0_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/serialization.h>

#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {
        class VoidFun_0_0 : public virtual Object {
        public:
            RTT_H_DECLS;

            virtual ~VoidFun_0_0() { }
            virtual void apply() = 0;
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
