#ifndef X10_LANG_FUN_0_2_H
#define X10_LANG_FUN_0_2_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        template<class P1, class P2, class R> class Fun_0_2 {
            public:
            class RTT : public x10aux::RuntimeType {
                public:
                static RTT * const it;
                virtual void init() { initParents(1,x10aux::getRTT<Value>()); }
                virtual std::string name() const {
                    std::stringstream ss;
                    ss<<"x10.lang.Fun_0_2["<<x10aux::getRTT<P1>()->name()<<","
                                           <<x10aux::getRTT<P2>()->name()<<","
                                           <<x10aux::getRTT<R>()->name()<<"]";
                    return ss.str();
                }
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Fun_0_2<P1,P2,R> >();
            }

            virtual ~Fun_0_2() { };
            virtual R apply(const P1 &p1, const P2 &p2) = 0;
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
