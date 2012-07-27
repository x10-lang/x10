/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#ifndef X10_UTIL_CONCURRENT_ATOMICREFERENCE_H
#define X10_UTIL_CONCURRENT_ATOMICREFERENCE_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>
#include <x10aux/string_utils.h>
#include <x10aux/atomic_ops.h>
#include <x10aux/basic_functions.h>


#include <x10/lang/Reference.h>

namespace x10 {
    namespace lang {
        class String;
    }

    namespace util {
        namespace concurrent {

                void _initRTTHelper_AtomicReference(x10aux::RuntimeType *location, const x10aux::RuntimeType *rtt);

                /*
                 * Implementation note: Extreme care must be taken when casting up/down
                 * from ref<T> to volatile S* that it is done in enough steps that any
                 * this pointer adjustment required by the C++ object model is still done.
                 * In particular, the only usage of a hard C style cast should be between
                 * S* and volatile S*.  Moving between S and T where S != T needs to be done
                 * with the C++ type system so that object model operations are performed.
                 */
                template<class T> class AtomicReference : public x10::lang::Reference {
                public:
                    RTT_H_DECLS_CLASS;

                    static x10aux::ref<AtomicReference<T> > _make();
                    static x10aux::ref<AtomicReference<T> > _make(T val);

                private:
                    x10aux::ref<AtomicReference<T> > _constructor(x10::lang::Reference *data) {
                        _data = data;
                        return this;
                    }

                public:
                    static const x10aux::serialization_id_t _serialization_id;

                    virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

                    virtual void _serialize_body(x10aux::serialization_buffer &buf);

                    static x10aux::ref<x10::lang::Reference> _deserializer(x10aux::deserialization_buffer &buf);

                    virtual void _deserialize_body(x10aux::deserialization_buffer& buf);

                private:
                    volatile x10::lang::Reference* _data;

                public:
                    T get();

                    void set(T val);

                    x10_boolean compareAndSet(T oldVal, T newVal);
                    
                    x10_boolean weakCompareAndSet(T oldVal, T newVal);

                    T getAndSet(T val);

                    virtual x10aux::ref<x10::lang::String> toString();
                };

                template<class T> x10aux::ref<AtomicReference<T> > AtomicReference<T>::_make() {
                    x10aux::ref<AtomicReference<T> > result = (new (x10aux::alloc<AtomicReference<T> >())AtomicReference<T>());
                    result->_constructor(NULL);
                    return result;
                }

                template<class T> x10aux::ref<AtomicReference<T> > AtomicReference<T>::_make(T val) {
                    x10aux::ref<AtomicReference<T> > result = (new (x10aux::alloc<AtomicReference<T> >())AtomicReference<T>());
                    x10::lang::Reference *tmp = val.operator->(); /* Does two things: gets backing S* ptr from ref<S> and upcasts S* to Reference* */
                    result->_constructor(tmp);
                    return result;
                }
                
                template<class T> T AtomicReference<T>::get() {
                    x10::lang::Reference *tmp = (x10::lang::Reference*)_data; /* drops volatile */
                    x10aux::ref<x10::lang::Reference> tmp2 = tmp; /* boxes to ref */
                    T tmp3 = tmp2;  /* downcast from ref<Reference> to T (ref<S>) */
                    return tmp3;
                }

                template<class T> void AtomicReference<T>::set(T val) {
                    x10::lang::Reference* tmp = val.operator->();
                    _data = tmp;
                }

                template<class T> x10_boolean AtomicReference<T>::compareAndSet(T oldVal, T newVal) {
                    x10::lang::Reference *oldValPtr = oldVal.operator->(); /* Does two things: gets backing S* ptr from ref<S> and upcasts S* to Reference* */
                    x10::lang::Reference *newValPtr = newVal.operator->(); /* Does two things: gets backing S* ptr from ref<S> and upcasts S* to Reference* */
                    x10::lang::Reference *res = (x10::lang::Reference *)x10aux::atomic_ops::compareAndSet_ptr((volatile void**)&_data, (void*)oldValPtr, (void*)newValPtr);
                    return res == oldValPtr;
                }

                template<class T> x10_boolean AtomicReference<T>::weakCompareAndSet(T oldVal, T newVal) {
                    // TODO: for minor optimization on ppc we could add a weakCompareAndSet_ptr in atomic_ops and use that here
                    return compareAndSet(oldVal, newVal);
                }
                
                template<class T> T AtomicReference<T>::getAndSet(T val) {
                    T res = get();
                    set(val);
                    return res;
                }

                template<class T> x10aux::ref<x10::lang::String> AtomicReference<T>::toString() {
                    x10::lang::Reference* tmp = (x10::lang::Reference*)_data; /* drops volatile */
                    x10aux::ref<x10::lang::Reference> tmp2 = tmp; /* boxes to ref */
                    T tmp3 = tmp2; /* downcast from ref<Reference> to T (ref<S) */
                    return x10aux::safe_to_string(tmp3);
                }
                
                template<class T> void AtomicReference<T>::_initRTT() {
                    if (rtt.initStageOne(x10aux::getRTT<AtomicReference<void> >())) return;
                    x10::util::concurrent::_initRTTHelper_AtomicReference(&rtt, x10aux::getRTT<T>());
                }

                template<class T> x10aux::RuntimeType AtomicReference<T>::rtt;

                template<class T> void
                AtomicReference<T>::_serialize_body(x10aux::serialization_buffer &buf) {
                    x10::lang::Reference* tmp = (x10::lang::Reference*)_data; /* drops volatile */
                    x10aux::ref<x10::lang::Reference> tmp2 = tmp; /* boxes to ref */
                    T tmp3 = tmp2; /* downcast from ref<Reference> to T (ref<S) */
                    buf.write(tmp3);
                }

                template<class T> void
                AtomicReference<T>::_deserialize_body(x10aux::deserialization_buffer& buf) {
                    T tmp = buf.read<T>();
                    x10::lang::Reference *tmp2 = tmp.operator->(); /* Does two things: gets backing S* ptr from ref<S> and upcasts S* to Reference* */
                    _data = tmp2;
                }

                template<class T> x10aux::ref<x10::lang::Reference>
                AtomicReference<T>::_deserializer(x10aux::deserialization_buffer &buf) {
                    x10aux::ref<AtomicReference<T> > this_ =
                        new (x10aux::alloc<AtomicReference<T> >()) AtomicReference<T> ();
                    buf.record_reference(this_);
                    this_->_deserialize_body(buf);
                    return this_;
                }

                template<class T>
                const x10aux::serialization_id_t AtomicReference<T>::_serialization_id =
                    x10aux::DeserializationDispatcher::addDeserializer(AtomicReference<T>::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

                template<> class AtomicReference<void> {
                public:
                    static x10aux::RuntimeType rtt;
                    static const x10aux::RuntimeType* getRTT() { return &rtt; }
                };
            }
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
