#ifndef __X10_UTIL_INDEXEDMEMORYCHUNK_H
#define __X10_UTIL_INDEXEDMEMORYCHUNK_H

#include <x10rt.h>

#include <x10/util/IndexedMemoryChunk.struct_h>

#endif // X10_UTIL_INDEXEDMEMORYCHUNK_H

namespace x10 { namespace util { 
template<class FMGL(T)> class IndexedMemoryChunk;
} } 

#ifndef X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
#define X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
#include <x10/lang/Any.h>
#include <x10/lang/String.h>
#ifndef X10_UTIL_INDEXEDMEMORYCHUNK_H_GENERICS
#define X10_UTIL_INDEXEDMEMORYCHUNK_H_GENERICS
#endif // X10_UTIL_INDEXEDMEMORYCHUNK_H_GENERICS
#ifndef X10_UTIL_INDEXEDMEMORYCHUNK_H_IMPLEMENTATION
#define X10_UTIL_INDEXEDMEMORYCHUNK_H_IMPLEMENTATION
#include <x10/util/IndexedMemoryChunk.h>


// ITable junk, both for IndexedMemoryChunk and IBox<IndexedMemoryChunk>
namespace x10 {
    namespace util { 
        template<class FMGL(T)> class IndexedMemoryChunk_ithunk0 : public x10::util::IndexedMemoryChunk<FMGL(T)> {
        public:
            static x10::lang::Any::itable<IndexedMemoryChunk_ithunk0<FMGL(T)> > itable;
        };

        template<class FMGL(T)> x10::lang::Any::itable<IndexedMemoryChunk_ithunk0<FMGL(T)> >
            IndexedMemoryChunk_ithunk0<FMGL(T)>::itable(&IndexedMemoryChunk<FMGL(T)>::at,
                                                        &IndexedMemoryChunk<FMGL(T)>::at,
                                                        &IndexedMemoryChunk<FMGL(T)>::equals,
                                                        &IndexedMemoryChunk<FMGL(T)>::hashCode,
                                                        &IndexedMemoryChunk<FMGL(T)>::home,
                                                        &IndexedMemoryChunk<FMGL(T)>::toString,
                                                        &IndexedMemoryChunk_ithunk0<FMGL(T)>::typeName);

        template<class FMGL(T)> class IndexedMemoryChunk_iboxithunk0 : public x10::lang::IBox<x10::util::IndexedMemoryChunk<FMGL(T)> > {
        public:
            static x10::lang::Any::itable<IndexedMemoryChunk_iboxithunk0<FMGL(T)> > itable;
            x10_boolean at(x10aux::ref<x10::lang::Object> arg0) {
                return this->value->at(arg0);
            }
            x10_boolean at(x10::lang::Place arg0) {
                return this->value->at(arg0);
            }
            x10_boolean equals(x10aux::ref<x10::lang::Any> arg0) {
                return this->value->equals(arg0);
            }
            x10_int hashCode() {
                return this->value->hashCode();
            }
            x10::lang::Place home() {
                return this->value->home();
            }
            x10aux::ref<x10::lang::String> toString() {
                return this->value->toString();
            }
            x10aux::ref<x10::lang::String> typeName() {
                return this->value->typeName();
            }
        };

        template<class FMGL(T)> x10::lang::Any::itable<IndexedMemoryChunk_iboxithunk0<FMGL(T)> >
            IndexedMemoryChunk_iboxithunk0<FMGL(T)>::itable(&IndexedMemoryChunk_iboxithunk0<FMGL(T)>::at,
                                                            &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::at,
                                                            &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::equals,
                                                            &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::hashCode,
                                                            &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::home,
                                                            &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::toString,
                                                            &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::typeName);
    }
} 

template<class FMGL(T)> x10aux::itable_entry x10::util::IndexedMemoryChunk<FMGL(T)>::_itables[2] = {x10aux::itable_entry(x10aux::getRTT<x10::lang::Any>(), &IndexedMemoryChunk_ithunk0<FMGL(T)>::itable),
                                                                                                    x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::util::IndexedMemoryChunk<FMGL(T)> >())};

template<class FMGL(T)> x10aux::itable_entry x10::util::IndexedMemoryChunk<FMGL(T)>::_iboxitables[2] = {x10aux::itable_entry(x10aux::getRTT<x10::lang::Any>(), &IndexedMemoryChunk_iboxithunk0<FMGL(T)>::itable),
                                                                                                        x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::util::IndexedMemoryChunk<FMGL(T)> >())};



template<class FMGL(T)> void x10::util::IndexedMemoryChunk<FMGL(T)>::_serialize(x10::util::IndexedMemoryChunk<FMGL(T)> this_, x10aux::serialization_buffer& buf) {
    buf.write((size_t)(this_->FMGL(data)));
}

template<class FMGL(T)> void x10::util::IndexedMemoryChunk<FMGL(T)>::_deserialize_body(x10aux::deserialization_buffer& buf) {
    FMGL(data) = (FMGL(T)*)buf.read<size_t>();
}


template<class FMGL(T)> x10_boolean x10::util::IndexedMemoryChunk<FMGL(T)>::_struct_equals(x10aux::ref<x10::lang::Any> that) {
    if ((!(x10aux::instanceof<x10::util::IndexedMemoryChunk<FMGL(T)> >(that)))) {
        return false;
    }
    return _struct_equals(x10aux::class_cast<x10::util::IndexedMemoryChunk<FMGL(T)> >(that));
}

template<class FMGL(T)> x10aux::ref<x10::lang::String> x10::util::IndexedMemoryChunk<FMGL(T)>::toString() {
    char* tmp = x10aux::alloc_printf("x10.util.IndexedMemoryChunk<%s>(%p)", x10aux::getRTT<FMGL(T)>()->name(), FMGL(data));
    return x10::lang::String::Steal(tmp);
}

template<class FMGL(T)> x10aux::ref<x10::lang::String> x10::util::IndexedMemoryChunk<FMGL(T)>::typeName() {
    char* tmp = x10aux::alloc_printf("x10.util.IndexedMemoryChunk<%s>", x10aux::getRTT<FMGL(T)>()->name());
    return x10::lang::String::Steal(tmp);
}

template<class FMGL(T)> x10aux::RuntimeType x10::util::IndexedMemoryChunk<FMGL(T)>::rtt;

template<class FMGL(T)> void x10::util::IndexedMemoryChunk<FMGL(T)>::_initRTT() {
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::util::IndexedMemoryChunk<void> >();
    if (rtt.initStageOne(canonical)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Any>()};
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<FMGL(T)>()};
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant};
    const char *baseName = "x10.util.IndexedMemoryChunk";
    rtt.initStageTwo(baseName, 2, parents, 1, params, variances);
}
#endif // X10_UTIL_INDEXEDMEMORYCHUNK_H_IMPLEMENTATION
#endif // __X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
