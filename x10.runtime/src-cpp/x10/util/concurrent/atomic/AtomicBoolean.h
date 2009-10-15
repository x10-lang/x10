/*
 * (c) Copyright IBM Corporation 2008
 *
 * This file is part of XRX/C++ native layer implementation.
 */

#ifndef X10_UTIL_CONCURRENT_ATOMIC_ATOMICBOOLEAN_H
#define X10_UTIL_CONCURRENT_ATOMIC_ATOMICBOOLEAN_H

#include <x10rt17.h>
#include <x10/lang/Ref.h>
#include <x10aux/serialization.h>

namespace x10 {
    namespace util {
        namespace concurrent {
            namespace atomic {

                /**
                 * Native implementation of AtomicBoolean.
                 */
                class AtomicBoolean : public x10::lang::Ref {
                public:
                    RTT_H_DECLS_CLASS;

                    static x10aux::ref<AtomicBoolean> _make();
                    static x10aux::ref<AtomicBoolean> _make(x10_boolean val);

                protected:
                    x10aux::ref<AtomicBoolean> _constructor(x10_boolean val) {
                        this->x10::lang::Ref::_constructor();
                        _val = (val ? 1 :0);
                        return this;
                    }

                public:
                    static const x10aux::serialization_id_t _serialization_id;

                    virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

                    virtual void _serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                        this->x10::lang::Ref::_serialize_body(buf, m);
                    }

                    template<class T> static x10aux::ref<T> _deserializer(x10aux::deserialization_buffer &buf) {
                        x10aux::ref<AtomicBoolean> this_ = new (x10aux::remote_alloc<AtomicBoolean>()) AtomicBoolean();
                        this_->_deserialize_body(buf);
                        return this_;
                    }

                    void _deserialize_body(x10aux::deserialization_buffer& buf) {
                        this->x10::lang::Ref::_deserialize_body(buf);
                    }

                private:
                    /*
                     * Am x10_int that is constrained to a 0/1 and interpret as an x10_ boolean.
                     * We do this so that we know that compareAndSet_32 can work on the whole memory word.
                     */
                    volatile x10_int _val;

                public:
                    x10_boolean get() { return _val == 1; }
                    
                    void set(x10_boolean newVal) { _val = (newVal ? 1 : 0); }

                    x10_boolean compareAndSet(x10_boolean expect, x10_boolean update) {
                        x10_int expectI = expect ? 1 : 0;
                        x10_int updateI = update ? 1 : 0;
                        x10_int oldVal = x10aux::atomic_ops::compareAndSet_32(&_val, expectI, updateI) == expectI;
                        return oldVal == 1;
                    }
                    
                    x10_boolean weakCompareAndSet(x10_boolean expect, x10_boolean update) {
                        // TODO: for minor optimization on ppc we could add a weakCompareAndSet in atomic_ops and use that here
                        x10_int expectI = expect ? 1 : 0;
                        x10_int updateI = update ? 1 : 0;
                        x10_int oldVal = x10aux::atomic_ops::compareAndSet_32(&_val, expectI, updateI) == expectI;
                        return oldVal == 1;
                    }

                    x10_boolean getAndSet(x10_boolean update) {
                        x10_boolean oldVal = get();
                        set(update);
                        return oldVal;
                    }
                        
                    x10aux::ref<x10::lang::String> toString() {
                        return x10aux::boolean_utils::toString(_val);
                    }
                };
            }
        }
    }
}
        
#endif /* X10_UTIL_CONCURRENT_ATOMIC_ATOMICBOOLEAN_H */

// vim:tabstop=4:shiftwidth=4:expandtab
