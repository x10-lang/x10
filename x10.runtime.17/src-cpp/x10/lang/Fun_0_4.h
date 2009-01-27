#ifndef X10_LANG_FUN_0_4_H
#define X10_LANG_FUN_0_4_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {
        template<class P1, class P2, class P3, class P4, class R> class Fun_0_4 : public virtual Object {
            public:
            class RTT : public x10aux::RuntimeType {
                public:
                static RTT * const it;
                virtual void init() { initParents(1,x10aux::getRTT<Object>()); }
                virtual char *name() const {
                    static const char *name =
                        x10aux::alloc_printf("x10.lang.Fun_0_4[%s,%s,%s,%s,%s]",
                                             x10aux::getRTT<P1>()->name(),
                                             x10aux::getRTT<P2>()->name(),
                                             x10aux::getRTT<P3>()->name(),
                                             x10aux::getRTT<P4>()->name(),
                                             x10aux::getRTT<R>()->name());
                    return name;
                }
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Fun_0_4<P1,P2,P3,P4,R> >();
            }

            virtual ~Fun_0_4() { };
            virtual R apply(P1 p1, P2 p2, P3 p3, P4 p4) = 0;
        };
        template<class P1, class P2, class P3, class P4, class R>
            typename Fun_0_4<P1,P2,P3,P4,R>::RTT * const Fun_0_4<P1,P2,P3,P4,R>::RTT::it =
                new (x10aux::alloc<typename Fun_0_4<P1,P2,P3,P4,R>::RTT>())
                    typename Fun_0_4<P1,P2,P3,P4,R>::RTT();
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
