#ifndef X10_LANG_FUN_0_0_H
#define X10_LANG_FUN_0_0_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {

        void _initRTTHelper_Fun_0_0(x10aux::RuntimeType *location, const x10aux::RuntimeType *rtt0);

        template<class R> class Fun_0_0 : public virtual Object {
            public:
            RTT_H_DECLS

            virtual ~Fun_0_0() { }
            virtual R apply() = 0;
        };

        template<class R> void Fun_0_0<R>::_initRTT() {
            rtt.parentsc = -2;
            x10::lang::_initRTTHelper_Fun_0_0(&rtt, x10aux::getRTT<R>());
        }

        template<class R> x10aux::RuntimeType Fun_0_0<R>::rtt;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
