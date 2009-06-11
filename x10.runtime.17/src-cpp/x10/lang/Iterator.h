#ifndef X10_LANG_ITERATOR_H
#define X10_LANG_ITERATOR_H


#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/alloc.h>

#include <x10/lang/Object.h>



namespace x10 {

    namespace lang {

        extern const x10aux::RuntimeType* _initRTTHelper_Iterator(const x10aux::RuntimeType **location, const x10aux::RuntimeType *rtt);
        
        template<class T> class Iterator : public virtual Object {
            public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT();
            virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

            virtual x10_boolean hasNext() = 0;

            virtual T next() = 0;

        };

        template<class T> const x10aux::RuntimeType* Iterator<T>::_initRTT() {
            return x10::lang::_initRTTHelper_Iterator(&rtt, x10aux::getRTT<T>());
        }
        
        template<class T> const x10aux::RuntimeType *Iterator<T>::rtt = NULL;
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
