#ifndef X10_LANG_FUN_0_1_H
#define X10_LANG_FUN_0_1_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        template<class P1, class R> class Fun_0_1 {
            public:
            class RTT : public x10aux::RuntimeType {
                public:
                static RTT * const it;
                virtual void init() { initParents(1,x10aux::getRTT<Value>()); }
                virtual std::string name() const {
                    std::stringstream ss;
                    ss<<"x10.lang.Fun_0_1["<<x10aux::getRTT<P1>()->name()<<","
                                           <<x10aux::getRTT<R>()->name()<<"]";
                    return ss.str();
                }
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Fun_0_1<P1,R> >();
            }

            virtual ~Fun_0_1() { }
            virtual R apply(const P1 &p1) = 0;
        };
        template<class P1, class R> typename Fun_0_1<P1,R>::RTT * const Fun_0_1<P1,R>::RTT::it =
            new typename Fun_0_1<P1,R>::RTT();
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
