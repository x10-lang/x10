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

#include <x10/lang/X10Class.h>

namespace x10 {
    namespace util {
        namespace concurrent {

                void _initRTTHelper_AtomicReference(x10aux::RuntimeType *location, const x10aux::RuntimeType *rtt);

                template<class T> class AtomicReference : public x10::lang::X10Class {
                public:
                    RTT_H_DECLS_CLASS;

                    static AtomicReference<T>* _make();
                    static AtomicReference<T>* _make(T val);

                private:
                    AtomicReference<T>* _constructor(T data) {
                        _data = data;
                        return this;
                    }

                public:
                    static const x10aux::serialization_id_t _serialization_id;

                    virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

                    virtual void _serialize_body(x10aux::serialization_buffer &buf);

                    static x10::lang::Reference* _deserializer(x10aux::deserialization_buffer &buf);

                    virtual void _deserialize_body(x10aux::deserialization_buffer& buf);

                private:
                    volatile T _data;

                public:
                    T get();

                    void set(T val);

                    x10_boolean compareAndSet(T oldVal, T newVal);
                    
                    x10_boolean weakCompareAndSet(T oldVal, T newVal);

                    T getAndSet(T val);

                    virtual x10::lang::String* toString();
                };

                template<class T> AtomicReference<T>* AtomicReference<T>::_make() {
                    AtomicReference<T>* result = (new (x10aux::alloc<AtomicReference<T> >())AtomicReference<T>());
                    result->_constructor(NULL);
                    return result;
                }

                template<class T> AtomicReference<T>* AtomicReference<T>::_make(T val) {
                    AtomicReference<T>* result = (new (x10aux::alloc<AtomicReference<T> >())AtomicReference<T>());
                    result->_constructor(val);
                    return result;
                }
                
                template<class T> T AtomicReference<T>::get() {
                    T tmp = const_cast<T>(_data); /* drops volatile */
                    return tmp;
                }

                template<class T> void AtomicReference<T>::set(T val) {
                    _data = val;
                }

                template<class T> x10_boolean AtomicReference<T>::compareAndSet(T oldVal, T newVal) {
                    T res = reinterpret_cast<T>(x10aux::atomic_ops::compareAndSet_ptr((volatile void**)&_data, (void*)oldVal, (void*)newVal));
                    return res == oldVal;
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

                template<class T> x10::lang::String* AtomicReference<T>::toString() {
                    T tmp = const_cast<T>(_data); /* drops volatile */
                    return x10aux::safe_to_string(tmp);
                }
                
                template<class T> void AtomicReference<T>::_initRTT() {
                    if (rtt.initStageOne(x10aux::getRTT<AtomicReference<void> >())) return;
                    x10::util::concurrent::_initRTTHelper_AtomicReference(&rtt, x10aux::getRTT<T>());
                }

                template<class T> x10aux::RuntimeType AtomicReference<T>::rtt;

                template<class T> void
                AtomicReference<T>::_serialize_body(x10aux::serialization_buffer &buf) {
                    T tmp = const_cast<T>(_data); /* drops volatile */
                    buf.write(tmp);
                }

                template<class T> void
                AtomicReference<T>::_deserialize_body(x10aux::deserialization_buffer& buf) {
                    T tmp = buf.read<T>();
                    _data = tmp;
                }

                template<class T> x10::lang::Reference* AtomicReference<T>::_deserializer(x10aux::deserialization_buffer &buf) {
                    AtomicReference<T>* this_ = new (x10aux::alloc<AtomicReference<T> >()) AtomicReference<T> ();
                    buf.record_reference(this_);
                    this_->_deserialize_body(buf);
                    return this_;
                }

                template<class T> const x10aux::serialization_id_t AtomicReference<T>::_serialization_id =
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
