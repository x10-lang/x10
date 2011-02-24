#ifndef __X10_UTIL_INDEXEDMEMORYCHUNK_H
#define __X10_UTIL_INDEXEDMEMORYCHUNK_H

#include <x10rt.h>

#include <x10/util/IndexedMemoryChunk.struct_h>

#endif // X10_UTIL_INDEXEDMEMORYCHUNK_H

namespace x10 { namespace util { 
template<class T> class IndexedMemoryChunk;
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

        extern const x10aux::serialization_id_t IMC_copy_to_serialization_id;
        extern const x10aux::serialization_id_t IMC_copy_from_serialization_id;

        extern const x10aux::serialization_id_t IMC_uncounted_copy_to_serialization_id;
        extern const x10aux::serialization_id_t IMC_unconuted_copy_from_serialization_id;
        
        extern void IMC_notifyEnclosingFinish(x10aux::deserialization_buffer&);
        extern void IMC_serialize_finish_state(x10aux::place, x10aux::serialization_buffer&);
	    extern void *IMC_buffer_finder(x10aux::deserialization_buffer&, x10_int);
        extern void IMC_notifier(x10aux::deserialization_buffer&, x10_int);
        extern void IMC_uncounted_notifier(x10aux::deserialization_buffer&, x10_int);

        extern void IMC_copyToBody(void *srcAddr, void *dstAddr, x10_int numBytes,
                                   x10::lang::Place dstPlace, bool overlap, x10aux::ref<x10::lang::Reference> notif);
        extern void IMC_copyFromBody(void *srcAddr, void *dstAddr, x10_int numBytes,
                                     x10::lang::Place srcPlace, bool overlap, x10aux::ref<x10::lang::Reference> notif);
        extern void IMC_copyBody(void *srcAddr, void *dstAddr, x10_int numBytes, bool overlap);
        
        template<class T> class IndexedMemoryChunk_ithunk0 : public x10::util::IndexedMemoryChunk<T> {
        public:
            static x10::lang::Any::itable<IndexedMemoryChunk_ithunk0<T> > itable;
        };

        template<class T> x10::lang::Any::itable<IndexedMemoryChunk_ithunk0<T> >
            IndexedMemoryChunk_ithunk0<T>::itable(&IndexedMemoryChunk<T>::equals,
                                                  &IndexedMemoryChunk<T>::hashCode,
                                                  &IndexedMemoryChunk<T>::toString,
                                                  &IndexedMemoryChunk_ithunk0<T>::typeName);

        template<class T> class IndexedMemoryChunk_iboxithunk0 : public x10::lang::IBox<x10::util::IndexedMemoryChunk<T> > {
        public:
            static x10::lang::Any::itable<IndexedMemoryChunk_iboxithunk0<T> > itable;
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

        template<class T> x10::lang::Any::itable<IndexedMemoryChunk_iboxithunk0<T> >
            IndexedMemoryChunk_iboxithunk0<T>::itable(&IndexedMemoryChunk_iboxithunk0<T>::equals,
                                                      &IndexedMemoryChunk_iboxithunk0<T>::hashCode,
                                                      &IndexedMemoryChunk_iboxithunk0<T>::toString,
                                                      &IndexedMemoryChunk_iboxithunk0<T>::typeName);
    }
} 


template<class T> void x10::util::IndexedMemoryChunk<void>::asyncCopy(x10::util::IndexedMemoryChunk<T> src, x10_int srcIndex,
                                                                      x10::util::RemoteIndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                                      x10_int numElems) {
    void* srcAddr = (void*)(&src->raw()[srcIndex]);
    void* dstAddr = (void*)(&dst->raw()[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
    x10aux::checkRailBounds(srcIndex, src.len);
    x10aux::checkRailBounds(srcIndex+numElems, src.len+1);
    x10aux::checkRailBounds(dstIndex, dst.len);
    x10aux::checkRailBounds(dstIndex+numElems, dst.len+1);
    IMC_copyToBody(srcAddr, dstAddr, numBytes, dst.home, src->data == dst->data, X10_NULL);
}


template<class T> void x10::util::IndexedMemoryChunk<void>::asyncCopy(x10::util::IndexedMemoryChunk<T> src, x10_int srcIndex,
                                                                      x10::util::RemoteIndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                                      x10_int numElems,
                                                                      x10aux::ref<x10::lang::Reference> notif) {
    void* srcAddr = (void*)(&src->raw()[srcIndex]);
    void* dstAddr = (void*)(&dst->raw()[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
    x10aux::checkRailBounds(srcIndex, src.len);
    x10aux::checkRailBounds(srcIndex+numElems, src.len+1);
    x10aux::checkRailBounds(dstIndex, dst.len);
    x10aux::checkRailBounds(dstIndex+numElems, dst.len+1);
    IMC_copyToBody(srcAddr, dstAddr, numBytes, dst.home, src->data == dst->data, notif);
}


template<class T> void x10::util::IndexedMemoryChunk<void>::asyncCopy(x10::util::RemoteIndexedMemoryChunk<T> src, x10_int srcIndex,
                                                                      x10::util::IndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                                      x10_int numElems) {
    void* srcAddr = (void*)(&src->raw()[srcIndex]);
    void* dstAddr = (void*)(&dst->raw()[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
    x10aux::checkRailBounds(srcIndex, src.len);
    x10aux::checkRailBounds(srcIndex+numElems, src.len+1);
    x10aux::checkRailBounds(dstIndex, dst.len);
    x10aux::checkRailBounds(dstIndex+numElems, dst.len+1);
    IMC_copyFromBody(srcAddr, dstAddr, numBytes, src.home, src->data == dst->data, X10_NULL);
}

template<class T> void x10::util::IndexedMemoryChunk<void>::asyncCopy(x10::util::RemoteIndexedMemoryChunk<T> src, x10_int srcIndex,
                                                                      x10::util::IndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                                      x10_int numElems,
                                                                      x10aux::ref<x10::lang::Reference> notif) {
    void* srcAddr = (void*)(&src->raw()[srcIndex]);
    void* dstAddr = (void*)(&dst->raw()[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
    x10aux::checkRailBounds(srcIndex, src.len);
    x10aux::checkRailBounds(srcIndex+numElems, src.len+1);
    x10aux::checkRailBounds(dstIndex, dst.len);
    x10aux::checkRailBounds(dstIndex+numElems, dst.len+1);
    IMC_copyFromBody(srcAddr, dstAddr, numBytes, src.home, src->data == dst->data, notif);
}

template<class T> void x10::util::IndexedMemoryChunk<void>::copy(x10::util::IndexedMemoryChunk<T> src, x10_int srcIndex,
                                                                 x10::util::IndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                                 x10_int numElems) {
    void* srcAddr = (void*)(&src->raw()[srcIndex]);
    void* dstAddr = (void*)(&dst->raw()[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
    x10aux::checkRailBounds(srcIndex, src.len);
    x10aux::checkRailBounds(srcIndex+numElems, src.len+1);
    x10aux::checkRailBounds(dstIndex, dst.len);
    x10aux::checkRailBounds(dstIndex+numElems, dst.len+1);
    IMC_copyBody(srcAddr, dstAddr, numBytes, src->data == dst->data);
}


template<class T>
x10::util::RemoteIndexedMemoryChunk<T> x10::util::IndexedMemoryChunk<T>::getCongruentSibling (x10::lang::Place p)
{
    return RemoteIndexedMemoryChunk<T>(raw(), length(), p);
}
    

template<class T> void x10::util::IndexedMemoryChunk<T>::_serialize(x10::util::IndexedMemoryChunk<T> this_,
                                                                    x10aux::serialization_buffer& buf) {
    buf.write((this_->len));
    for (int i=0; i<this_->len; i++) {
        buf.write(this_->__apply(i));
    }
}

template<class T> void x10::util::IndexedMemoryChunk<T>::_deserialize_body(x10aux::deserialization_buffer& buf) {
    len = buf.read<x10_int>();

    bool containsPtrs = x10aux::getRTT<T>()->containsPtrs;
    size_t alignment = X10_MIN_INDEXEDMEMORYCHUNK_ALIGNMENT;
    size_t size = alignment + len*sizeof(T);
    T* allocMem = x10aux::alloc<T>(size, containsPtrs);
    size_t alignDelta = alignment-1;
    size_t alignMask = ~alignDelta;
    size_t alignedMem = ((size_t)allocMem + alignDelta) & alignMask;
    data = (x10_ulong)alignedMem;

    for (int i=0; i<len; i++) {
        __set(buf.read<T>(), i);
    }
}


template<class T> x10_boolean x10::util::IndexedMemoryChunk<T>::_struct_equals(x10aux::ref<x10::lang::Any> that) {
    if ((!(x10aux::instanceof<x10::util::IndexedMemoryChunk<T> >(that)))) {
        return false;
    }
    return _struct_equals(x10aux::class_cast<x10::util::IndexedMemoryChunk<T> >(that));
}

template<class T> x10_boolean x10::util::IndexedMemoryChunk<T>::_struct_equals(x10::util::IndexedMemoryChunk<T> that) { 
    return x10aux::struct_equals(data, that->data) && x10aux::struct_equals(len, that->len);
}

template<class T> x10aux::ref<x10::lang::String> x10::util::IndexedMemoryChunk<T>::toString() {
    char* tmp = x10aux::alloc_printf("x10.util.IndexedMemoryChunk<%s>(%llx of %llx elements)", x10aux::getRTT<T>()->name(), data, (unsigned long long)len);
    return x10::lang::String::Steal(tmp);
}

template<class T> x10aux::ref<x10::lang::String> x10::util::IndexedMemoryChunk<T>::typeName() {
    char* tmp = x10aux::alloc_printf("x10.util.IndexedMemoryChunk<%s>", x10aux::getRTT<T>()->name());
    return x10::lang::String::Steal(tmp);
}

template<class T> x10aux::RuntimeType x10::util::IndexedMemoryChunk<T>::rtt;

template<class T> x10aux::itable_entry x10::util::IndexedMemoryChunk<T>::_itables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &IndexedMemoryChunk_ithunk0<T>::itable),
                                                                                        x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::util::IndexedMemoryChunk<T> >())};

template<class T> x10aux::itable_entry x10::util::IndexedMemoryChunk<T>::_iboxitables[2] = {x10aux::itable_entry(&x10aux::getRTT<x10::lang::Any>, &IndexedMemoryChunk_iboxithunk0<T>::itable),
                                                                                            x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::util::IndexedMemoryChunk<T> >())};

template<class T> void x10::util::IndexedMemoryChunk<T>::_initRTT() {
    const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::util::IndexedMemoryChunk<void> >();
    if (rtt.initStageOne(canonical)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Any>()};
    const x10aux::RuntimeType* params[1] = { x10aux::getRTT<T>()};
    x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant};
    const char *baseName = "x10.util.IndexedMemoryChunk";
    rtt.initStageTwo(baseName, x10aux::RuntimeType::struct_kind, 2, parents, 1, params, variances);
}

#endif // X10_UTIL_INDEXEDMEMORYCHUNK_H_IMPLEMENTATION
#endif // __X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
