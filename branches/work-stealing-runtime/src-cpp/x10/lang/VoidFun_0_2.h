#ifndef X10_LANG_VOIDFUN_0_2_H
#define X10_LANG_VOIDFUN_0_2_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {
        template<class P1, class P2> class VoidFun_0_2 : public virtual Object {
            public:
            class RTT : public x10aux::RuntimeType {
                public:
                static RTT * const it;
                virtual void init() { initParents(1,x10aux::getRTT<Object>()); }
                virtual const char *name() const {
                    static const char *name =
                        x10aux::alloc_printf("x10.lang.VoidFun_0_2[%s,%s]",
                                             x10aux::getRTT<P1>()->name(),
                                             x10aux::getRTT<P2>()->name());
                    return name;
                }
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<VoidFun_0_2<P1,P2> >();
            }

            virtual ~VoidFun_0_2() { }
            virtual void apply(P1 p1, P2 p2) = 0;
        };
        template<class P1, class P2> typename VoidFun_0_2<P1,P2>::RTT * const VoidFun_0_2<P1,P2>::RTT::it =
            new (x10aux::alloc<typename VoidFun_0_2<P1,P2>::RTT>()) typename VoidFun_0_2<P1,P2>::RTT();
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
