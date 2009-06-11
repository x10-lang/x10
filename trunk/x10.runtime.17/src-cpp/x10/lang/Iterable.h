#ifndef X10_LANG_ITERABLE_H
#define X10_LANG_ITERABLE_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10/lang/Object.h>

namespace x10 {
    namespace lang {

        extern const x10aux::RuntimeType* _initRTTHelper_Iterable(const x10aux::RuntimeType **location, const x10aux::RuntimeType *rtt);
        
        template<class T> class Iterable : public virtual Object {
            public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT();
            virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

            virtual x10aux::ref<Iterator<T > > iterator() = 0;
        };

        template<class T> const x10aux::RuntimeType* Iterable<T>::_initRTT() {
            return x10::lang::_initRTTHelper_Iterable(&rtt, x10aux::getRTT<T>());
        }
        
        template<class T> const x10aux::RuntimeType *Iterable<T>::rtt = NULL;
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
