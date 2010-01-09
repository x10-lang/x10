/*
 * (c) Copyright IBM Corporation 2008
 *
 * This file is part of XRX/C++ native layer implementation.
 */

#ifndef X10_UTIL_CONCURRENT_ATOMIC_ATOMICLONG_H
#define X10_UTIL_CONCURRENT_ATOMIC_ATOMICLONG_H

#include <x10rt.h>
#include <x10/lang/Object.h>
#include <x10aux/serialization.h>

namespace x10 {
    namespace util {
        namespace concurrent {
            namespace atomic {

                /**
                 * Native implementation of AtomicLong.
                 */
                class AtomicLong : public x10::lang::Object {
                public:
                    RTT_H_DECLS_CLASS;

                    static x10aux::ref<AtomicLong> _make();
                    static x10aux::ref<AtomicLong> _make(x10_long val);

                protected:
                    x10aux::ref<AtomicLong> _constructor(x10_long val) {
                        this->x10::lang::Object::_constructor();
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
                    volatile x10_long _val;

                public:
                    x10_long get() { return _val; }
                    
                    void set(x10_long newVal) { _val = newVal; }

                    x10_boolean compareAndSet(x10_long expect, x10_long update) {
                        return x10aux::atomic_ops::compareAndSet_64(&_val, expect, update) == expect;
                    }

                    x10_boolean weakCompareAndSet(x10_long expect, x10_long update) {
                        // TODO: for minor optimization on ppc we could add a weakCompareAndSet in atomic_ops and use that here
                        return x10aux::atomic_ops::compareAndSet_64(&_val, expect, update) == expect;
                    }

                    x10_long getAndIncrement() {
                        return getAndAdd(1);
                    }
                        
                    x10_long getAndDecrement() {
                        return getAndAdd(-1);
                    }

                    x10_long getAndAdd(x10_long delta) {
                        x10_long oldValue = _val;
                        while (x10aux::atomic_ops::compareAndSet_64(&_val, oldValue, oldValue+delta) != oldValue) {
                            oldValue = _val;
                        }
                        return oldValue;
                    }
	
                    x10_long incrementAndGet() {
                        return addAndGet(1);
                    }

                    x10_long decrementAndGet() {
                        return addAndGet(-1);
                    }
	
                    x10_long addAndGet(x10_long delta) {
                        x10_long oldValue = _val;
                        while (x10aux::atomic_ops::compareAndSet_64(&_val, oldValue, oldValue+delta) != oldValue) {
                            oldValue = _val;
                        }
                        return oldValue + delta;
                    }
	
                    x10aux::ref<x10::lang::String> toString() {
                        return x10aux::long_utils::toString(_val);
                    }

                    x10_int intValue() {
                        return (x10_int)_val;
                    }

                    x10_long longValue() {
                        return _val;
                    }

                    x10_float floatValue() {
                        return (x10_float)_val;
                    }
	
                    x10_double doubleValue() {
                        return (x10_double)_val;
                    }
                };

                template<class T> x10aux::ref<T> AtomicLong::_deserializer(x10aux::deserialization_buffer &buf) {
                    x10aux::ref<AtomicLong> this_ = new (x10aux::alloc_remote<AtomicLong>()) AtomicLong();
                    buf.record_reference(this_); // TODO: avoid; no global refs; final class
                    this_->_deserialize_body(buf);
                    return this_;
                }
            }
        }
    }
}
        
#endif /* X10_UTIL_CONCURRENT_ATOMIC_ATOMICLONG_H */

// vim:tabstop=4:shiftwidth=4:expandtab
