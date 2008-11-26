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
            class RTT : public x10aux::RuntimeType {
                public:
                static RTT * const it;
                virtual void init() { initParents(1,x10aux::getRTT<Object>()); }
                virtual std::string name() const { return "x10.lang.VoidFun"; }
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<VoidFun_0_0>();
            }

/*
            virtual void _serialize(x10aux::serialization_buffer&, x10aux::addr_map&) = 0;
*/

            virtual ~VoidFun_0_0() { }
            virtual void apply() = 0;
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
