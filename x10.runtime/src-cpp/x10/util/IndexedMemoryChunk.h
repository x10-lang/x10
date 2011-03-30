#ifndef __X10_UTIL_INDEXEDMEMORYCHUNK_H
#define __X10_UTIL_INDEXEDMEMORYCHUNK_H

#include <x10rt.h>

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/RTT.h>
#include <x10aux/serialization.h>

#include <assert.h>

// platform-specific min chunk alignment
#if defined(_POWER) || defined(__bgp__)
#define X10_MIN_INDEXEDMEMORYCHUNK_ALIGNMENT 16
#else
#define X10_MIN_INDEXEDMEMORYCHUNK_ALIGNMENT ((x10_int)sizeof(x10_double))
#endif

namespace x10 {
    namespace lang { class Place; }
    
    namespace util { 

        template<class T> class RemoteIndexedMemoryChunk;
        
        template<class T> class IndexedMemoryChunk  {
          public:
            RTT_H_DECLS_STRUCT

            static x10aux::itable_entry _itables[2];
            static x10aux::itable_entry _iboxitables[2];

            x10aux::itable_entry* _getITables() { return _itables; }
            x10aux::itable_entry* _getIBoxITables() { return _iboxitables; }
    
            x10_ulong data; /* TODO: We would like this to be const */
            x10_int len; /* TODO: we would like this to be const */ /* TODO, this should be an x10_long */
            x10_int length() { return len; } 
            T *raw (void) const { return (T*)(size_t)data; }
            T &operator[] (int index) { return raw()[index]; }
            const T &operator[] (int index) const { return raw()[index]; }

            IndexedMemoryChunk(): data(0), len(0) {}
            IndexedMemoryChunk(T* _data, x10_int _len): data((size_t)_data), len(_len) {}
            IndexedMemoryChunk(x10_ulong _data, x10_int _len): data(_data), len(_len) {}

            inline T __apply(x10_int index) { 
                x10aux::checkRailBounds(index, len);
                return raw()[index]; 
            }
            inline T __apply(x10_long index) { 
                x10aux::checkRailBounds((x10_int)index, len);
                return raw()[index]; 
            }
            
            inline void __set(x10_int index, T val) { 
                x10aux::checkRailBounds(index, len);
                raw()[index] = val; 
            }
            inline void __set(x10_long index, T val) { 
                x10aux::checkRailBounds((x10_int)index, len);
                raw()[index] = val; 
            }

            inline T apply_unsafe(x10_int index) { return raw()[index]; }
            inline T apply_unsafe(x10_long index) { return raw()[index]; }
            
            inline void set_unsafe(T val, x10_int index) { raw()[index] = val; }
            inline void set_unsafe(T val, x10_long index) { raw()[index] = val; }

            RemoteIndexedMemoryChunk<T> getCongruentSibling (x10::lang::Place p);

            x10::util::IndexedMemoryChunk<T>* operator->() { return this; }
        
            static void _serialize(x10::util::IndexedMemoryChunk<T> this_, x10aux::serialization_buffer& buf);
    
            static x10::util::IndexedMemoryChunk<T> _deserialize(x10aux::deserialization_buffer& buf) {
                x10::util::IndexedMemoryChunk<T> this_;
                this_->_deserialize_body(buf);
                return this_;
            }
    
            void _deserialize_body(x10aux::deserialization_buffer& buf);
            
            x10_boolean equals(x10aux::ref<x10::lang::Any> that) { return _struct_equals(that); }
    
            x10_boolean equals(x10::util::IndexedMemoryChunk<T> that) { return _struct_equals(that); }
    
            x10_boolean _struct_equals(x10aux::ref<x10::lang::Any>);
    
            x10_boolean _struct_equals(x10::util::IndexedMemoryChunk<T> that);
    
            x10aux::ref<x10::lang::String> toString();
    
            x10_int hashCode() { return (x10_int)data; }

            x10aux::ref<x10::lang::String> typeName();
        };
    }
}

namespace x10 {
    namespace util { 

        template <> class IndexedMemoryChunk<void> {
          private:
            template<class T> static IndexedMemoryChunk<T> allocInternal(size_t numElements,
                                                                         x10_int alignment,
                                                                         x10_boolean congruent,
                                                                         x10_boolean zeroed);
          public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return &rtt; }

            template<class T> static IndexedMemoryChunk<T> allocate(x10_int numElements,
                                                                    x10_int alignment,
                                                                    x10_boolean congruent,
                                                                    x10_boolean zeroed) {
                assert(numElements >=0);
                return allocInternal<T>((size_t)numElements, alignment, congruent, zeroed);
            }
            
            template<class T> static IndexedMemoryChunk<T> allocate(x10_long numElements,
                                                                    x10_int alignment,
                                                                    x10_boolean congruent,
                                                                    x10_boolean zeroed) {
                assert(numElements >= 0);
                assert(((x10_long)((size_t)numElements)) == numElements); // check for alloc requests >31 bits on 32 bit system
                return allocInternal<T>((size_t)numElements, alignment, congruent, zeroed);
            }

            template<class T> static void asyncCopy(x10::util::IndexedMemoryChunk<T> src, x10_int srcIndex,
                                                    x10::util::RemoteIndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                    x10_int numElems);

            template<class T> static void asyncCopy(x10::util::IndexedMemoryChunk<T> src, x10_int srcIndex,
                                                    x10::util::RemoteIndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                    x10_int numElems,
                                                    x10aux::ref<x10::lang::Reference> notif);


            template<class T> static void asyncCopy(x10::util::RemoteIndexedMemoryChunk<T> src, x10_int srcIndex,
                                                    x10::util::IndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                    x10_int numElems);

            template<class T> static void asyncCopy(x10::util::RemoteIndexedMemoryChunk<T> src, x10_int srcIndex,
                                                    x10::util::IndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                    x10_int numElems,
                                                    x10aux::ref<x10::lang::Reference> notif);

            template<class T> static void copy(x10::util::IndexedMemoryChunk<T> src, x10_int srcIndex,
                                               x10::util::IndexedMemoryChunk<T> dst, x10_int dstIndex,
                                               x10_int numElems);
        };

        // avoid putting junk in the header, avoid putting junk in template code to save code size
        void checkCongruentArgs (x10_boolean zeroed, x10_boolean containsPtrs);

        template<class T> IndexedMemoryChunk<T> IndexedMemoryChunk<void>::allocInternal(size_t numElements,
                                                                                        x10_int alignment,
                                                                                        x10_boolean congruent, 
                                                                                        x10_boolean zeroed) {
            assert((alignment & (alignment-1)) == 0);
            if (alignment < X10_MIN_INDEXEDMEMORYCHUNK_ALIGNMENT) {
                alignment = X10_MIN_INDEXEDMEMORYCHUNK_ALIGNMENT;
            }

            bool containsPtrs = x10aux::getRTT<T>()->containsPtrs;
            T* allocMem;
            if (congruent) {
                // aligned on page boundaries, no point attempting to do better than that, we will just end up
                // using more memory than we want
                size_t size = numElements*sizeof(T);
                checkCongruentArgs(zeroed,containsPtrs);
                allocMem = static_cast<T*>(x10aux::alloc_internal_congruent(size));
            } else {
                size_t size = alignment + numElements*sizeof(T);
                allocMem = x10aux::alloc<T>(size, containsPtrs);
                if (zeroed) {
                    memset(allocMem, 0, size);
                }
            }
            size_t alignDelta = alignment-1;
            size_t alignMask = ~alignDelta;
            size_t alignedMem = ((size_t)allocMem + alignDelta) & alignMask;
            return IndexedMemoryChunk<T>((T*)alignedMem, (x10_int)numElements);
        }

    }
} 

#endif // X10_UTIL_INDEXEDMEMORYCHUNK_H

namespace x10 { namespace util { 
template<class T> class IndexedMemoryChunk;
} } 

#ifndef X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
#define X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
#include <x10/lang/Any.h>
#include <x10/lang/Place.h>
#include <x10/lang/String.h>
#include <x10/util/RemoteIndexedMemoryChunk.h>
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
        __set(i, buf.read<T>());
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
