#ifndef __X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H
#define __X10_UTIL_REMOTEINDEXEDMEMORYCHUNK_H

#include <x10rt.h>

#include <x10/util/RemoteIndexedMemoryChunk.struct_h>

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
            x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
                return this->value->equals(arg0);
            }
            x10_int hashCode() {
                return this->value->hashCode();
            }
            x10aux::ref<x10::lang::String> toString() {
                return this->value->toString();
            }
            x10aux::ref<x10::lang::String> typeName() {
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


template<class T> x10_boolean x10::util::RemoteIndexedMemoryChunk<T>::_struct_equals(x10aux::ref<x10::lang::Any> that) {
    if ((!(x10aux::instanceof<x10::util::RemoteIndexedMemoryChunk<T> >(that)))) {
        return false;
    }
    return _struct_equals(x10aux::class_cast<x10::util::RemoteIndexedMemoryChunk<T> >(that));
}

template<class T> x10_boolean x10::util::RemoteIndexedMemoryChunk<T>::_struct_equals(x10::util::RemoteIndexedMemoryChunk<T> that) { 
    return x10aux::struct_equals(data, that->data) && x10aux::struct_equals(len, that->len) && x10aux::struct_equals(home, that->home);
}

template<class T> x10aux::ref<x10::lang::String> x10::util::RemoteIndexedMemoryChunk<T>::toString() {
    char* tmp = x10aux::alloc_printf("x10.util.RemoteIndexedMemoryChunk<%s>(%llx of %llx elements)", x10aux::getRTT<T>()->name(), data, (unsigned long long)len);
    return x10::lang::String::Steal(tmp);
}

template<class T> x10aux::ref<x10::lang::String> x10::util::RemoteIndexedMemoryChunk<T>::typeName() {
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
