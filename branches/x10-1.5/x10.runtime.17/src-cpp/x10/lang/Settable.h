#ifndef X10_LANG_SETTABLE_H
#define X10_LANG_SETTABLE_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {

        extern const x10aux::RuntimeType* _initRTTHelper_Settable(const x10aux::RuntimeType **location,
                                                                  const x10aux::RuntimeType *rtt1,
                                                                  const x10aux::RuntimeType *rtt2);

        template<class I, class V> class Settable : public virtual Object {
            public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT();
            virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

            virtual V set(V v, I i) = 0;
        };

        template<class I, class V> const x10aux::RuntimeType *Settable<I, V>::_initRTT() {
            return x10::lang::_initRTTHelper_Settable(&rtt, x10aux::getRTT<I>(), x10aux::getRTT<V>());
        }

        template<class I, class V> const x10aux::RuntimeType *Settable<I, V>::rtt = NULL;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
