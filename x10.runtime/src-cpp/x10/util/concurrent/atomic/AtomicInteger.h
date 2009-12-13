/*
 * (c) Copyright IBM Corporation 2008
 *
 * This file is part of XRX/C++ native layer implementation.
 */

#ifndef X10_UTIL_CONCURRENT_ATOMIC_ATOMICINTEGER_H
#define X10_UTIL_CONCURRENT_ATOMIC_ATOMICINTEGER_H

#include <x10rt.h>
#include <x10/lang/Ref.h>
#include <x10aux/serialization.h>

namespace x10 {
    namespace util {
        namespace concurrent {
            namespace atomic {

                /**
                 * Native implementation of AtomicInteger.
                 */
                class AtomicInteger : public x10::lang::Ref {
                public:
                    RTT_H_DECLS_CLASS;

                    static x10aux::ref<AtomicInteger> _make();
                    static x10aux::ref<AtomicInteger> _make(x10_int val);

                protected:
                    x10aux::ref<AtomicInteger> _constructor(x10_int val) {
                        this->x10::lang::Ref::_constructor();
                        _val = val;
                        return this;
                    }

                public:
                    static const x10aux::serialization_id_t _serialization_id;

                    virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

                    virtual void _serialize_body(x10aux::serialization_buffer &buf);

                    template<class T> static x10aux::ref<T> _deserializer(x10aux::deserialization_buffer &buf);

                    virtual void _deserialize_body(x10aux::deserialization_buffer& buf);

                private:
                    volatile x10_int _val;

                public:
                    x10_int get() { return _val; }
                    
                    void set(x10_int newVal) { _val = newVal; }

                    x10_boolean compareAndSet(x10_int expect, x10_int update) {
                        return x10aux::atomic_ops::compareAndSet_32(&_val, expect, update) == expect;
                    }

                    x10_boolean weakCompareAndSet(x10_int expect, x10_int update) {
                        // TODO: for minor optimization on ppc we could add a weakCompareAndSet in atomic_ops and use that here
                        return x10aux::atomic_ops::compareAndSet_32(&_val, expect, update) == expect;
                    }

                    x10_int getAndIncrement() {
                        return getAndAdd(1);
                    }
                        
                    x10_int getAndDecrement() {
                        return getAndAdd(-1);
                    }

                    x10_int getAndAdd(x10_int delta) {
                        x10_int oldValue = _val;
                        while (x10aux::atomic_ops::compareAndSet_32(&_val, oldValue, oldValue+delta) != oldValue) {
                            oldValue = _val;
                        }
                        return oldValue;
                    }
	
                    x10_int incrementAndGet() {
                        return addAndGet(1);
                    }

                    x10_int decrementAndGet() {
                        return addAndGet(-1);
                    }
	
                    x10_int addAndGet(x10_int delta) {
                        x10_int oldValue = _val;
                        while (x10aux::atomic_ops::compareAndSet_32(&_val, oldValue, oldValue+delta) != oldValue) {
                            oldValue = _val;
                        }
                        return oldValue + delta;
                    }
	
                    x10aux::ref<x10::lang::String> toString() {
                        return x10aux::int_utils::toString(_val);
                    }

                    x10_int intValue() {
                        return _val;
                    }

                    x10_long longValue() {
                        return (x10_long)_val;
                    }

                    x10_float floatValue() {
                        return (x10_float)_val;
                    }
	
                    x10_double doubleValue() {
                        return (x10_double)_val;
                    }
                };

                template<class T> x10aux::ref<T> AtomicInteger::_deserializer(x10aux::deserialization_buffer &buf) {
                    x10aux::ref<AtomicInteger> this_ = new (x10aux::alloc_remote<AtomicInteger>()) AtomicInteger();
                    this_->_deserialize_body(buf);
                    return this_;
                }
            }
        }
    }
}
        
#endif /* X10_UTIL_CONCURRENT_ATOMIC_ATOMICINTEGER_H */

// vim:tabstop=4:shiftwidth=4:expandtab
