#ifndef __X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H
#define __X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H

#include <x10rt.h>

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>
#include <x10aux/serialization.h>

#include <assert.h>

#define X10_LANG_PLACE_H_NODEPS
#include <x10/lang/Place.h>
#undef X10_LANG_PLACE_H_NODEPS

namespace x10 {
    namespace util { 

	extern x10::lang::Place RIMC_here_hack();

        template<class T> class RemoteIndexedMemoryChunk  {
          public:
            RTT_H_DECLS_STRUCT

            static x10aux::itable_entry _itables[2];
            static x10aux::itable_entry _iboxitables[2];

            x10aux::itable_entry* _getITables() { return _itables; }
            x10aux::itable_entry* _getIBoxITables() { return _iboxitables; }
    
            x10_ulong data; /* TODO: We would like this to be const */
            x10::lang::Place home;
            x10_int len; /* TODO: we would like this to be const */ /* TODO, this should be an x10_long */

            x10_int length() { return len; } 
            T *raw (void) const { return (T*)(size_t)data; }

            RemoteIndexedMemoryChunk(): data(0), len(0), home(RIMC_here_hack()) {}
            RemoteIndexedMemoryChunk(T* _data, x10_int _len): data((size_t)_data), len(_len), home(RIMC_here_hack()) {}
            RemoteIndexedMemoryChunk(x10_ulong _data, x10_int _len): data(_data), len(_len), home(RIMC_here_hack) {}
            RemoteIndexedMemoryChunk(T* _data, x10_int _len, x10::lang::Place _home): data((size_t)_data), len(_len), home(_home) {}
            RemoteIndexedMemoryChunk(x10_ulong _data, x10_int _len, x10::lang::Place _home): data(_data), len(_len), home(_home) {}

            x10::util::RemoteIndexedMemoryChunk<T>* operator->() { return this; }
        
            static void _serialize(x10::util::RemoteIndexedMemoryChunk<T> this_, x10aux::serialization_buffer& buf);
    
            static x10::util::RemoteIndexedMemoryChunk<T> _deserialize(x10aux::deserialization_buffer& buf) {
                x10::util::RemoteIndexedMemoryChunk<T> this_;
                this_->_deserialize_body(buf);
                return this_;
            }
    
            void _deserialize_body(x10aux::deserialization_buffer& buf);
            
            x10_boolean equals(x10::lang::Any* that) { return _struct_equals(that); }
    
            x10_boolean equals(x10::util::RemoteIndexedMemoryChunk<T> that) { return _struct_equals(that); }
    
            x10_boolean _struct_equals(x10::lang::Any*);
    
            x10_boolean _struct_equals(x10::util::RemoteIndexedMemoryChunk<T> that);
    
            x10::lang::String* toString();
    
            x10_int hashCode() { return (x10_int)data; }

            x10::lang::String* typeName();

            void remoteAdd(x10_int idx, x10_ulong v)
            { x10aux::remote_op(home->FMGL(id), (x10rt_remote_ptr)(size_t)&raw()[idx], X10RT_OP_ADD, v); }

            void remoteAnd(x10_int idx, x10_ulong v)
            { x10aux::remote_op(home->FMGL(id), (x10rt_remote_ptr)(size_t)&raw()[idx], X10RT_OP_AND, v); }

            void remoteOr(x10_int idx, x10_ulong v)
            { x10aux::remote_op(home->FMGL(id), (x10rt_remote_ptr)(size_t)&raw()[idx], X10RT_OP_OR, v); }

            void remoteXor(x10_int idx, x10_ulong v)
            { x10aux::remote_op(home->FMGL(id), (x10rt_remote_ptr)(size_t)&raw()[idx], X10RT_OP_XOR, v); }
        };


        template <> class RemoteIndexedMemoryChunk<void> {
          public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return &rtt; }
        };
    }
} 

#endif // X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H

namespace x10 { namespace util { 
template<class T> class RemoteIndexedMemoryChunk;
} } 

#ifndef X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H_NODEPS
#define X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H_NODEPS
#include <x10/lang/Any.h>
#include <x10/lang/String.h>
#ifndef X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H_GENERICS
#define X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H_GENERICS
#endif // X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H_GENERICS
#ifndef X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H_IMPLEMENTATION
#define X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H_IMPLEMENTATION
#include <x10/util/RemoteIndexedMemoryChunk.h>


// ITable junk, both for RemoteIndexedMemoryChunk and IBox<RemoteIndexedMemoryChunk>
namespace x10 {
    namespace util { 
        template<class T> class RemoteIndexedMemoryChunk_ithunk0 : public x10::util::RemoteIndexedMemoryChunk<T> {
        public:
            static x10::lang::Any::itable<RemoteIndexedMemoryChunk_ithunk0<T> > itable;
        };

        template<class T> x10::lang::Any::itable<RemoteIndexedMemoryChunk_ithunk0<T> >
            RemoteIndexedMemoryChunk_ithunk0<T>::itable(&RemoteIndexedMemoryChunk<T>::equals,
                                                        &RemoteIndexedMemoryChunk<T>::hashCode,
                                                        &RemoteIndexedMemoryChunk<T>::toString,
                                                        &RemoteIndexedMemoryChunk_ithunk0<T>::typeName);

        template<class T> class RemoteIndexedMemoryChunk_iboxithunk0 : public x10::lang::IBox<x10::util::RemoteIndexedMemoryChunk<T> > {
        public:
            static x10::lang::Any::itable<RemoteIndexedMemoryChunk_iboxithunk0<T> > itable;
            x10_boolean equals(x10::lang::Any* arg0) {
                return this->value->equals(arg0);
            }
            x10_int hashCode() {
                return this->value->hashCode();
            }
            x10::lang::String* toString() {
                return this->value->toString();
            }
            x10::lang::String* typeName() {
                return this->value->typeName();
            }
        };

        template<class T> x10::lang::Any::itable<RemoteIndexedMemoryChunk_iboxithunk0<T> >
            RemoteIndexedMemoryChunk_iboxithunk0<T>::itable(&RemoteIndexedMemoryChunk_iboxithunk0<T>::equals,
                                                            &RemoteIndexedMemoryChunk_iboxithunk0<T>::hashCode,
                                                            &RemoteIndexedMemoryChunk_iboxithunk0<T>::toString,
                                                            &RemoteIndexedMemoryChunk_iboxithunk0<T>::typeName);
    }
} 


template<class T> void x10::util::RemoteIndexedMemoryChunk<T>::_serialize(x10::util::RemoteIndexedMemoryChunk<T> this_,
                                                                          x10aux::serialization_buffer& buf) {
    buf.write((this_->len));
    buf.write((this_->home));
    buf.write((this_->data));
}

template<class T> void x10::util::RemoteIndexedMemoryChunk<T>::_deserialize_body(x10aux::deserialization_buffer& buf) {
    len = buf.read<x10_int>();
    home = buf.read<x10::lang::Place>();
    data = buf.read<x10_ulong>();
}


template<class T> x10_boolean x10::util::RemoteIndexedMemoryChunk<T>::_struct_equals(x10::lang::Any* that) {
    if ((!(x10aux::instanceof<x10::util::RemoteIndexedMemoryChunk<T> >(that)))) {
        return false;
    }
    return _struct_equals(x10aux::class_cast<x10::util::RemoteIndexedMemoryChunk<T> >(that));
}

template<class T> x10_boolean x10::util::RemoteIndexedMemoryChunk<T>::_struct_equals(x10::util::RemoteIndexedMemoryChunk<T> that) { 
    return x10aux::struct_equals(data, that->data) && x10aux::struct_equals(len, that->len) && x10aux::struct_equals(home, that->home);
}

template<class T> x10::lang::String* x10::util::RemoteIndexedMemoryChunk<T>::toString() {
    char* tmp = x10aux::alloc_printf("x10.util.RemoteIndexedMemoryChunk<%s>(%llx of %llx elements)", x10aux::getRTT<T>()->name(), data, (unsigned long long)len);
    return x10::lang::String::Steal(tmp);
}

template<class T> x10::lang::String* x10::util::RemoteIndexedMemoryChunk<T>::typeName() {
    char* tmp = x10aux::alloc_printf("x10.util.RemoteIndexedMemoryChunk<%s>", x10aux::getRTT<T>()->name());
    return x10::lang::String::Steal(tmp);
}

template<class T> x10aux::RuntimeType x10::util::RemoteIndexedMemoryChunk<T>::rtt;

template<class T> x10aux::itable_entry x10::util::RemoteIndexedMemoryChunk<T>::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &RemoteIndexedMemoryChunk_ithunk0<T>::itable),
                                                                                              x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::util::RemoteIndexedMemoryChunk<T> >())};

template<class T> x10aux::itable_entry x10::util::RemoteIndexedMemoryChunk<T>::_iboxitables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &RemoteIndexedMemoryChunk_iboxithunk0<T>::itable),
                                                                                                  x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::util::RemoteIndexedMemoryChunk<T> >())};

template<class T> void x10::util::RemoteIndexedMemoryChunk<T>::_initRTT() {
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::util::RemoteIndexedMemoryChunk<void> >();
    if (rtt.initStageOne(canonical)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Any>()};
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<T>()};
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant};
    const char *baseName = "x10.util.RemoteIndexedMemoryChunk";
    rtt.initStageTwo(baseName, x10aux::RuntimeType::struct_kind, 2, parents, 1, params, variances);
}
#endif // X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H_IMPLEMENTATION
#endif // __X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H_NODEPS
