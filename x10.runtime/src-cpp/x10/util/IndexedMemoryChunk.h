/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2010.
 *  (C) Copyright Australian National University 2011.
 */

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

        void throwArrayIndexOutOfBoundsException(x10_int index, x10_int length) X10_PRAGMA_NORETURN;
    
        inline void checkBounds(x10_int index, x10_int length) {
            #ifndef NO_BOUNDS_CHECKS
            // Since we know length is non-negative and IMCs are zero-based,
            // the bounds check can be optimized to a single unsigned comparison.
            // The C++ compiler won't do this for us, since it doesn't know that length is non-negative.
            if (((x10_uint)index) >= ((x10_uint)length)) {
                x10::util::throwArrayIndexOutOfBoundsException(index, length);
            }
            #endif
        }
        
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

            /*
             * Knowing the delta is required to support deallocation in the presence of alignment.
             * We have to have the real pointer as returned from x10aux::alloc to pass back to x10aux::dealloc.
             * There is nor real space cost for keeping this because C++ struct alignment constraints already
             * force sizeof(IndexedMemoryChunk) to 16 even without this field.
             * (8 for data + 4 for length == 12, which gets padded to 16).
             */
            x10_int deltaToAlloced;
            
            x10_int length() { return len; } 
            T *raw (void) const { return (T*)(size_t)data; }
            T &operator[] (int index) { return raw()[index]; }
            const T &operator[] (int index) const { return raw()[index]; }

            IndexedMemoryChunk(): data(0), len(0), deltaToAlloced(0) {}
            IndexedMemoryChunk(T* rawMem, T* alignedMem, x10_int _len) : data((size_t)alignedMem),
                len(_len),
                deltaToAlloced((x10_int)((size_t)alignedMem - (size_t)rawMem)) {}
            IndexedMemoryChunk(x10_ulong rawData, x10_ulong alignedData, x10_int _len): data(alignedData),
                len(_len),
                deltaToAlloced((x10_int)(alignedData-rawData)) {}

            inline T __apply(x10_int index) { 
                checkBounds(index, len);
                return raw()[index]; 
            }
            inline T __apply(x10_long index) { 
                checkBounds((x10_int)index, len);
                return raw()[index]; 
            }
            
            inline void __set(x10_int index, T val) { 
                checkBounds(index, len);
                raw()[index] = val; 
            }
            inline void __set(x10_long index, T val) { 
                checkBounds((x10_int)index, len);
                raw()[index] = val; 
            }

            void clear(x10_int index, x10_int numElems);
            void clear(x10_long index, x10_long numElems);

            void deallocate();
            
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
            if (0 == numElements) {
                return IndexedMemoryChunk<T>((T*)NULL, (T*)NULL, (x10_int)numElements);
            }

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
            return IndexedMemoryChunk<T>(allocMem, (T*)alignedMem, (x10_int)numElements);
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

template<class T> void x10::util::IndexedMemoryChunk<T>::clear(x10_int index, x10_int numElems) {
    if (numElems>0) {
        checkBounds(index, len);
        checkBounds(index+numElems, len+1);
        void* addr = (void*)(&raw()[index]);
        size_t numBytes = numElems * sizeof(T);
        memset(addr, 0, numBytes);
    }
}

template<class T> void x10::util::IndexedMemoryChunk<T>::clear(x10_long index, x10_long numElems) {
    // TODO: real 64 bit implementation
    clear((x10_int)index, (x10_int)numElems);
}

template<class T> void x10::util::IndexedMemoryChunk<T>::deallocate() {
    if (0 != data) {
        x10_ulong basePtr = data - deltaToAlloced;
        x10aux::dealloc((T*)basePtr);
        // fprintf(stderr, "Dealloc %p %p %d\n", (T*)basePtr, (T*)data, deltaToAlloced);
    }
    data = 0;
    len = 0;
}

template<class T> void x10::util::IndexedMemoryChunk<void>::asyncCopy(x10::util::IndexedMemoryChunk<T> src, x10_int srcIndex,
                                                                      x10::util::RemoteIndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                                      x10_int numElems) {
    if (numElems <= 0) return;
    void* srcAddr = (void*)(&src->raw()[srcIndex]);
    void* dstAddr = (void*)(&dst->raw()[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
    checkBounds(srcIndex, src.len);
    checkBounds(srcIndex+numElems, src.len+1);
    checkBounds(dstIndex, dst.len);
    checkBounds(dstIndex+numElems, dst.len+1);
    IMC_copyToBody(srcAddr, dstAddr, numBytes, dst.home, src->data == dst->data, X10_NULL);
}


template<class T> void x10::util::IndexedMemoryChunk<void>::asyncCopy(x10::util::IndexedMemoryChunk<T> src, x10_int srcIndex,
                                                                      x10::util::RemoteIndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                                      x10_int numElems,
                                                                      x10aux::ref<x10::lang::Reference> notif) {
    if (numElems <= 0) return;
    void* srcAddr = (void*)(&src->raw()[srcIndex]);
    void* dstAddr = (void*)(&dst->raw()[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
    checkBounds(srcIndex, src.len);
    checkBounds(srcIndex+numElems, src.len+1);
    checkBounds(dstIndex, dst.len);
    checkBounds(dstIndex+numElems, dst.len+1);
    IMC_copyToBody(srcAddr, dstAddr, numBytes, dst.home, src->data == dst->data, notif);
}


template<class T> void x10::util::IndexedMemoryChunk<void>::asyncCopy(x10::util::RemoteIndexedMemoryChunk<T> src, x10_int srcIndex,
                                                                      x10::util::IndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                                      x10_int numElems) {
    if (numElems <= 0) return;
    void* srcAddr = (void*)(&src->raw()[srcIndex]);
    void* dstAddr = (void*)(&dst->raw()[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
    checkBounds(srcIndex, src.len);
    checkBounds(srcIndex+numElems, src.len+1);
    checkBounds(dstIndex, dst.len);
    checkBounds(dstIndex+numElems, dst.len+1);
    IMC_copyFromBody(srcAddr, dstAddr, numBytes, src.home, src->data == dst->data, X10_NULL);
}

template<class T> void x10::util::IndexedMemoryChunk<void>::asyncCopy(x10::util::RemoteIndexedMemoryChunk<T> src, x10_int srcIndex,
                                                                      x10::util::IndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                                      x10_int numElems,
                                                                      x10aux::ref<x10::lang::Reference> notif) {
    if (numElems <= 0) return;
    void* srcAddr = (void*)(&src->raw()[srcIndex]);
    void* dstAddr = (void*)(&dst->raw()[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
    checkBounds(srcIndex, src.len);
    checkBounds(srcIndex+numElems, src.len+1);
    checkBounds(dstIndex, dst.len);
    checkBounds(dstIndex+numElems, dst.len+1);
    IMC_copyFromBody(srcAddr, dstAddr, numBytes, src.home, src->data == dst->data, notif);
}

template<class T> void x10::util::IndexedMemoryChunk<void>::copy(x10::util::IndexedMemoryChunk<T> src, x10_int srcIndex,
                                                                 x10::util::IndexedMemoryChunk<T> dst, x10_int dstIndex,
                                                                 x10_int numElems) {
    if (numElems <= 0) return;
    void* srcAddr = (void*)(&src->raw()[srcIndex]);
    void* dstAddr = (void*)(&dst->raw()[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
    checkBounds(srcIndex, src.len);
    checkBounds(srcIndex+numElems, src.len+1);
    checkBounds(dstIndex, dst.len);
    checkBounds(dstIndex+numElems, dst.len+1);
    IMC_copyBody(srcAddr, dstAddr, numBytes, src->data == dst->data);
}


template<class T>
x10::util::RemoteIndexedMemoryChunk<T> x10::util::IndexedMemoryChunk<T>::getCongruentSibling (x10::lang::Place p)
{
	size_t addr = (size_t)raw();

    if (x10aux::congruent_huge) {
		addr = addr - x10aux::congruent_offset * ((x10aux::here % (1 << x10aux::congruent_period))) + (x10aux::congruent_offset * (p.FMGL(id) % (1 << x10aux::congruent_period)));
    }
    return RemoteIndexedMemoryChunk<T>((T*)addr, length(), p);
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
    if (0 == len) {
        data = 0;
    } else {
        bool containsPtrs = x10aux::getRTT<T>()->containsPtrs;
        size_t alignment = X10_MIN_INDEXEDMEMORYCHUNK_ALIGNMENT;
        size_t size = alignment + len*sizeof(T);
        T* allocMem = x10aux::alloc<T>(size, containsPtrs);
        size_t alignDelta = alignment-1;
        size_t alignMask = ~alignDelta;
        size_t alignedMem = ((size_t)allocMem + alignDelta) & alignMask;
        data = (x10_ulong)alignedMem;
        deltaToAlloced = (x10_int)(alignedMem - (size_t)allocMem);
        
        for (int i=0; i<len; i++) {
            __set(i, buf.read<T>());
        }
    }
}

#define PRIMITIVE_COPY_SERIALIZATION(TYPE) \
template<> inline void x10::util::IndexedMemoryChunk<TYPE>::_serialize(x10::util::IndexedMemoryChunk<TYPE> this_, \
                                                                    x10aux::serialization_buffer& buf) {\
    buf.write((this_->len));\
    buf.copyIn(buf, this_->raw(), this_->len, sizeof(TYPE));\
}\
template<> inline void x10::util::IndexedMemoryChunk<TYPE>::_deserialize_body(x10aux::deserialization_buffer& buf) {\
    len = buf.read<x10_int>();\
    if (0 == len) {\
        data = 0;\
    } else {\
        size_t alignment = X10_MIN_INDEXEDMEMORYCHUNK_ALIGNMENT;\
        size_t size = alignment + len*sizeof(TYPE);\
        TYPE* allocMem = x10aux::alloc<TYPE>(size, false);\
        size_t alignDelta = alignment-1;\
        size_t alignMask = ~alignDelta;\
        size_t alignedMem = ((size_t)allocMem + alignDelta) & alignMask;\
        data = (x10_ulong)alignedMem;\
        deltaToAlloced = (x10_int)(alignedMem - (size_t)allocMem);\
        buf.copyOut(buf, raw(), (x10_long)len, sizeof(TYPE));   \
    }\
}
namespace x10 {
    namespace lang {
        class Complex;
    }
    namespace util {
        PRIMITIVE_COPY_SERIALIZATION(x10_boolean)
        PRIMITIVE_COPY_SERIALIZATION(x10_byte)
        PRIMITIVE_COPY_SERIALIZATION(x10_ubyte)
        PRIMITIVE_COPY_SERIALIZATION(x10_char)
        PRIMITIVE_COPY_SERIALIZATION(x10_short)
        PRIMITIVE_COPY_SERIALIZATION(x10_ushort)
        PRIMITIVE_COPY_SERIALIZATION(x10_int)
        PRIMITIVE_COPY_SERIALIZATION(x10_uint)
        PRIMITIVE_COPY_SERIALIZATION(x10_long)
        PRIMITIVE_COPY_SERIALIZATION(x10_ulong)
        PRIMITIVE_COPY_SERIALIZATION(x10_float)
        PRIMITIVE_COPY_SERIALIZATION(x10_double)

        template<> inline void x10::util::IndexedMemoryChunk<x10::lang::Complex>::_serialize(x10::util::IndexedMemoryChunk<x10::lang::Complex> this_, x10aux::serialization_buffer& buf) {
            buf.write((this_->len));
            // Complex is serialized as two doubles
            buf.copyIn(buf, this_->raw(), this_->len*2, sizeof(x10_double));
        }

        template<> inline void x10::util::IndexedMemoryChunk<x10::lang::Complex>::_deserialize_body(x10aux::deserialization_buffer& buf) {
            len = buf.read<x10_int>();
            if (0 == len) {
                data = 0;
            } else {
                size_t alignment = X10_MIN_INDEXEDMEMORYCHUNK_ALIGNMENT;
                // Complex is serialized as two doubles
                size_t size = alignment + len*2*sizeof(x10_double);
                x10::lang::Complex* allocMem = x10aux::alloc<x10::lang::Complex>(size, false);
                size_t alignDelta = alignment-1;
                size_t alignMask = ~alignDelta;
                size_t alignedMem = ((size_t)allocMem + alignDelta) & alignMask;
                data = (x10_ulong)alignedMem;
                deltaToAlloced = (x10_int)(alignedMem - (size_t)allocMem);
                buf.copyOut(buf, raw(), (x10_long)(len*2), sizeof(x10_double));
            }
        }
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
    char* tmp = x10aux::alloc_printf("IndexedMemoryChunk(");
    int sz = length() > 10 ? 10 : length();
    for (int i = 0; i < sz; i++) {
        if (i > 0)
            tmp = x10aux::realloc_printf(tmp, ",");
        tmp = x10aux::realloc_printf(tmp,"%s",x10aux::to_string(__apply(i))->c_str());
    }
    if (sz < length()) tmp = x10aux::realloc_printf(tmp, "...(omitted %d elements", length() - sz);
    tmp = x10aux::realloc_printf(tmp, ")");
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
