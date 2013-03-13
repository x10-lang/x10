/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2013.
 */

#ifndef __X10_LANG_RAIL_H
#define __X10_LANG_RAIL_H

#include <x10aux/config.h>

#include <x10/lang/X10Class.h>

#define X10_LANG_ITERABLE_H_NODEPS
#include <x10/lang/Iterable.h>
#undef X10_LANG_ITERABLE_H_NODEPS

#define X10_LANG_FUN_0_1_H_NODEPS
#include <x10/lang/Fun_0_1.h>
#undef X10_LANG_FUN_0_1_H_NODEPS

#define X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS
#include <x10/util/IndexedMemoryChunk.h>
#undef X10_UTIL_INDEXEDMEMORYCHUNK_H_NODEPS

namespace x10 {
    namespace lang { 
        class LongRange;
        template<class T> class Iterator;
        template<class T> class RailIterator;
        template<class T> class GlobalRef;
        class String;
    }
}

namespace x10 {
    namespace lang { 

        template<class T> class Rail;
        template <> class Rail<void>;

        void throwArrayIndexOutOfBoundsException(x10_long index, x10_long size) X10_PRAGMA_NORETURN;
    
        inline void checkBounds(x10_long index, x10_long size) {
            #ifndef NO_BOUNDS_CHECKS
            // Since we know size is non-negative and Rails are zero-based,
            // the bounds check can be optimized to a single unsigned comparison.
            // The C++ compiler won't do this for us, since it doesn't know that size is non-negative.
            if (((x10_ulong)index) >= ((x10_ulong)size)) {
                x10::util::throwArrayIndexOutOfBoundsException(index, size);
            }
            #endif
        }

        extern void rail_copyRaw(void *srcAddr, void *dstAddr, x10_long numBytes, bool overlap);

        template<class T> class Rail : public x10::lang::X10Class   {
          public:
            RTT_H_DECLS_CLASS
            const x10_long FMGL(size);
            T raw[1]; // It's not really 1, but this is how one declares the variable array at end of a struct in ISO C++

            Rail(x10_long numElems) : FMGL(size)(numElems) { }
            
            static x10aux::itable_entry _itables[4];
            virtual x10aux::itable_entry* _getITables() { return _itables; }
            static typename x10::lang::Iterable<T>::template itable<x10::lang::Rail<T> > _itable_0;
            static typename x10::lang::Fun_0_1<x10_int, T>::template itable<x10::lang::Rail<T> > _itable_1;
            static typename x10::lang::Fun_0_1<x10_long, T>::template itable<x10::lang::Rail<T> > _itable_2;
    
            static x10::lang::Rail<T>* _make(x10::util::IndexedMemoryChunk<T > backingStore);
    
            static x10::lang::Rail<T>* _make();
    
            static x10::lang::Rail<T>* _makeUnsafe(x10_long size, x10_boolean allocatedZeroed);
    
            static x10::lang::Rail<T>* _make(x10::lang::Rail<T>* src);
    
            static x10::lang::Rail<T>* _make(x10_long size);
    
            static x10::lang::Rail<T>* _make(x10_int size) {
                return _make((x10_long)size);
            }

            static x10::lang::Rail<T>* _make(x10_long size, T init);

            static x10::lang::Rail<T>* _make(x10_int size, T init) {
                return _make((x10_long)size, init);
            }
            
            static x10::lang::Rail<T>* _make(x10_long size, x10::lang::Fun_0_1<x10_long, T>* init);

            // NB:  Can't redirect this one to the long variant because of the function!
            static x10::lang::Rail<T>* _make(x10_int size, x10::lang::Fun_0_1<x10_int, T>* init);

            x10::lang::LongRange range();

            virtual x10::lang::Iterator<T>* iterator();

            virtual x10::lang::String* toString();

            virtual T __apply(x10_long index);
            virtual T __apply(x10_int index);

            virtual T __set(x10_long index, T v);
            virtual T __set(x10_int index, T v);

            T &operator[] (x10_long index) {
                checkBounds(index, FMGL(size));
                return raw[index];
            }
            const T &operator[] (x10_long index) const {
                checkBounds(index, FMGL(size));
                return raw[index];
            }

            
            virtual void clear();
            virtual void clear(x10_long start, x10_long numElems);
            virtual void clear(x10_int start, x10_int numElems);
    
            // Serialization
            static const x10aux::serialization_id_t _serialization_id;
            x10aux::serialization_id_t _get_serialization_id() {
                return _serialization_id;
            }
    
            virtual void _serialize_body(x10aux::serialization_buffer& buf);

            static x10::lang::Reference* _deserializer(x10aux::deserialization_buffer& buf);
            void _deserialize_body(x10aux::deserialization_buffer& buf);
        };


        // RTT
        template<class T> x10aux::RuntimeType x10::lang::Rail<T>::rtt;

        template<class T> void x10::lang::Rail<T>::_initRTT() {
            const x10aux::RuntimeType *canonical = x10aux::getRTT<x10::lang::Rail<void> >();
            if (rtt.initStageOne(canonical)) return;
            const x10aux::RuntimeType* parents[3] = { x10aux::getRTT<x10::lang::Iterable<T> >(),
                                                      x10aux::getRTT<x10::lang::Fun_0_1<x10_int, T> >(),
                                                      x10aux::getRTT<x10::lang::Fun_0_1<x10_long, T> >()
            };
            const x10aux::RuntimeType* params[1] = { x10aux::getRTT<T>()};
            x10aux::RuntimeType::Variance variances[1] = { x10aux::RuntimeType::invariant};
            const char *baseName = "x10.lang.Rail";
            rtt.initStageTwo(baseName, x10aux::RuntimeType::class_kind, 3, parents, 1, params, variances);
        }


        // Static methods
        template <> class Rail<void> : public x10::lang::X10Class {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return & rtt; }
            template<class T> static void copy(x10::lang::Rail<T>* src,
                                               x10::lang::Rail<T>* dst);
    
            template<class T> static void copy(x10::lang::Rail<T>* src,
                                               x10_long srcIndex, x10::lang::Rail<T>* dst,
                                               x10_long dstIndex, x10_long numElems);
    
            template<class T> static void copy(x10::lang::Rail<T>* src,
                                               x10_int srcIndex, x10::lang::Rail<T>* dst,
                                               x10_int dstIndex,
                                               x10_int numElems) {
                copy(src, (x10_long)srcIndex, dst, (x10_long)dstIndex, (x10_long)numElems);
            }

            template<class T> static void asyncCopy(x10::lang::Rail<T>* src, x10_long srcIndex,
                                                    x10::lang::GlobalRef<x10::lang::Rail<T>*> dst, x10_long dstIndex,
                                                    x10_long numElems,
                                                    x10::lang::VoidFun_0_0* notif);

            template<class T> static void asyncCopy(x10::lang::GlobalRef<x10::lang::Rail<T>*> src, x10_long srcIndex,
                                                    x10::lang::Rail<T>* dst, x10_long dstIndex,
                                                    x10_long numElems,
                                                    x10::lang::VoidFun_0_0* notif);
        };
    }
} 
#endif // X10_LANG_RAIL_H

namespace x10 {
    namespace lang { 
        template<class T> class Rail;
    }
} 

#ifndef X10_LANG_RAIL_H_NODEPS
#define X10_LANG_RAIL_H_NODEPS
#include <x10/lang/Iterable.h>
#include <x10/lang/Fun_0_1.h>
#include <x10/util/IndexedMemoryChunk.h>
#include <x10/lang/LongRange.h>
#include <x10/lang/Iterator.h>
#include <x10/lang/RailIterator.h>
#include <x10/lang/String.h>
#include <x10/lang/IllegalArgumentException.h>
#ifndef X10_LANG_RAIL_H_GENERICS
#define X10_LANG_RAIL_H_GENERICS
#endif // X10_LANG_RAIL_H_GENERICS
#ifndef X10_LANG_RAIL_H_IMPLEMENTATION
#define X10_LANG_RAIL_H_IMPLEMENTATION
#include <x10/lang/Rail.h>

/*
 * Construction (_make methods)
 */

template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10::util::IndexedMemoryChunk<T > backingStore) {
    bool containsPtrs = x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = backingStore->length();
    size_t numBytes = sizeof(x10::lang::Rail<T>) - sizeof(T) + (numElems * sizeof(T)); // -sizeof(T) accounts for raw[1]
    x10::lang::Rail<T>* this_ = new (x10aux::alloc_internal(numBytes, containsPtrs)) x10::lang::Rail<T>(numElems);

    rail_copyRaw(backingStore->raw(), &this_->raw, sizeof(T)*backingStore->length(), false);
    return this_;
}


template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make() {
    x10::lang::Rail<T>* this_ = new (x10aux::alloc_internal(sizeof(x10::lang::Rail<T>), false)) x10::lang::Rail<T>(0);
    return this_;
}


template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_makeUnsafe(x10_long size, x10_boolean allocateZeroed) {
    bool containsPtrs = x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = size;
    size_t numBytes = sizeof(x10::lang::Rail<T>) -sizeof(T) + (numElems * sizeof(T)); // -sizeof(T) accounts for raw[1]
    x10::lang::Rail<T>* this_ = new (x10aux::alloc_internal(numBytes, containsPtrs)) x10::lang::Rail<T>(numElems);

    if (allocateZeroed) {
        memset(&(this_->raw), 0, numElems*sizeof(T));
    }
    return this_;
}


template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10::lang::Rail<T>* src) {
    bool containsPtrs = x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = x10aux::nullCheck(src)->FMGL(size);
    size_t numBytes = sizeof(x10::lang::Rail<T>) -sizeof(T) + (numElems * sizeof(T)); // -sizeof(T) accounts for raw[1]
    x10::lang::Rail<T>* this_ = new (x10aux::alloc_internal(numBytes, containsPtrs)) x10::lang::Rail<T>(numElems);

    rail_copyRaw(&src->raw, &this_->raw, numElems*sizeof(T), false);
    return this_;
}


template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_long size) {
    bool containsPtrs = x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = size;
    size_t numBytes = sizeof(x10::lang::Rail<T>) -sizeof(T) + (numElems * sizeof(T)); // -sizeof(T) accounts for raw[1]
    x10::lang::Rail<T>* this_ = new (x10aux::alloc_internal(numBytes, containsPtrs)) x10::lang::Rail<T>(numElems);

    memset(&(this_->raw), 0, size*sizeof(T));
    return this_;
}


template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_long size, T init) {
    bool containsPtrs = x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = size;
    size_t numBytes = sizeof(x10::lang::Rail<T>) -sizeof(T) + (numElems * sizeof(T)); // -sizeof(T) accounts for raw[1]
    x10::lang::Rail<T>* this_ = new (x10aux::alloc_internal(numBytes, containsPtrs)) x10::lang::Rail<T>(numElems);

    for (x10_long i = 0ll; i < size; i++) {
        this_->raw[i] = init;
    }
    return this_;
}

template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_long size, x10::lang::Fun_0_1<x10_long, T>* init) {
    bool containsPtrs = x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = size;
    size_t numBytes = sizeof(x10::lang::Rail<T>) -sizeof(T) + (numElems * sizeof(T)); // -sizeof(T) accounts for raw[1]
    x10::lang::Rail<T>* this_ = new (x10aux::alloc_internal(numBytes, containsPtrs)) x10::lang::Rail<T>(numElems);

    if (size > 0) {
        x10aux::nullCheck(init);
        x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(init);
        T (x10::lang::Reference::*fun)(x10_long) = x10aux::findITable<Fun_0_1<x10_long, T> >(recv->_getITables())->__apply;
        for (x10_long i = 0ll; i < size; i++) {
            this_->raw[i] = (recv->*(fun))(i);
        }
    }
    return this_;
}

template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_int size, x10::lang::Fun_0_1<x10_int, T>* init) {
    bool containsPtrs = x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = (x10_long)size;
    size_t numBytes = sizeof(x10::lang::Rail<T>) -sizeof(T) + (numElems * sizeof(T)); // -sizeof(T) accounts for raw[1]
    x10::lang::Rail<T>* this_ = new (x10aux::alloc_internal(numBytes, containsPtrs)) x10::lang::Rail<T>(numElems);

    if (size > 0) {
        x10aux::nullCheck(init);
        x10::lang::Reference* recv = reinterpret_cast<x10::lang::Reference*>(init);
        T (x10::lang::Reference::*fun)(x10_int) = x10aux::findITable<Fun_0_1<x10_int, T> >(recv->_getITables())->__apply;
        for (x10_int i = 0; i < size; i++) {
            this_->raw[i] = (recv->*(fun))(i);
        }
    }
    return this_;
}


/*
 * Instance functions 
 */

template<class T> x10::lang::LongRange x10::lang::Rail<T>::range() {
    return x10::lang::LongRange::_make(0, FMGL(size)-1);
}    

template<class T> x10::lang::Iterator<T>* x10::lang::Rail<T>::iterator() {
    x10::lang::RailIterator<T>* it = x10::lang::RailIterator<T>::_make(this);
    return reinterpret_cast<x10::lang::Iterator<T>*>(it);
}

template<class T> x10::lang::String* x10::lang::Rail<T>::toString() {
    char* tmp = x10aux::alloc_printf("[");
    x10_long sz = FMGL(size) > 10 ? 10 : FMGL(size);
    for (x10_long i = 0; i < sz; i++) {
        if (i > 0) {
            tmp = x10aux::realloc_printf(tmp, ",");
        }
        tmp = x10aux::realloc_printf(tmp,"%s",x10aux::to_string(__apply(i))->c_str());
    }
    if (sz < FMGL(size)) {
        tmp = x10aux::realloc_printf(tmp, "...(omitted %lld elements", (signed long long)(FMGL(size) - sz));
    }
    tmp = x10aux::realloc_printf(tmp, "]");
    return x10::lang::String::Steal(tmp);
}

template<class T> T x10::lang::Rail<T>::__apply(x10_long index) {
    checkBounds(index, FMGL(size));
    return raw[index];
}

template<class T> T x10::lang::Rail<T>::__set(x10_long index, T v) {
    checkBounds(index, FMGL(size));
    raw[index] = v;
    return v;
}

template<class T> void x10::lang::Rail<T>::clear() {
    memset(&raw, 0, sizeof(T)*FMGL(size));
}


template<class T> void x10::lang::Rail<T>::clear(x10_long start, x10_long numElems) {
    memset(&raw[start], 0, sizeof(T)*numElems);
}

template<class T> void x10::lang::Rail<T>::clear(x10_int start, x10_int numElems) {
    memset(&raw[start], 0, sizeof(T)*numElems);
}

template<class T> T x10::lang::Rail<T>::__apply(x10_int index) {
    checkBounds(index, FMGL(size));
    return raw[index];
}

template<class T> T x10::lang::Rail<T>::__set(x10_int index, T v) {
    checkBounds(index, FMGL(size));
    raw[index] = v;
    return v;
}

/*
 * Static functions 
 */

template<class T> void x10::lang::Rail<void>::copy(x10::lang::Rail<T>* src, x10::lang::Rail<T>* dst) {
    x10aux::nullCheck(src);
    x10aux::nullCheck(dst);

    if (src == dst) return;
    
    if (src->FMGL(size) != dst->FMGL(size)) {
        x10aux::throwException(x10::lang::IllegalArgumentException::_make(x10aux::makeStringLit("source and destination do not have equal size")));
    }

    rail_copyRaw(&src->raw[0], &dst->raw[0], sizeof(T)*src->FMGL(size), false);
}

template<class T> void x10::lang::Rail<void>::copy(x10::lang::Rail<T>* src,
                                                   x10_long srcIndex,
                                                   x10::lang::Rail<T>* dst,
                                                   x10_long dstIndex,
                                                   x10_long numElems) {
    if (numElems <= 0) return;

    x10aux::nullCheck(src);
    x10aux::nullCheck(dst);
    checkBounds(srcIndex, src->FMGL(size));
    checkBounds(srcIndex+numElems, src->FMGL(size)+1ll);
    checkBounds(dstIndex, dst->FMGL(size));
    checkBounds(dstIndex+numElems, dst->FMGL(size)+1ll);

    rail_copyRaw(&src->raw[srcIndex], &dst->raw[dstIndex], sizeof(T)*numElems, src == dst);
}

template<class T> void x10::lang::Rail<void>::asyncCopy(x10::lang::Rail<T>* src, x10_long srcIndex,
                                                        x10::lang::GlobalRef<x10::lang::Rail<T>*> dst, x10_long dstIndex,
                                                        x10_long numElems,
                                                        x10::lang::VoidFun_0_0* notif) {
    if (numElems <= 0) return;
    void* srcAddr = (void*)(&src->raw[srcIndex]);
    void* dstAddr = (void*)(&dst->__apply()->raw[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
    checkBounds(srcIndex, src->FMGL(size));
    checkBounds(srcIndex+numElems, src->FMGL(size)+1);
//    checkBounds(dstIndex, dst.len);
//    checkBounds(dstIndex+numElems, dst.len+1);
    x10::util::IMC_copyToBody(srcAddr, dstAddr, numBytes, x10::lang::Place::place(dst.location), src->raw == dst->__apply()->raw, notif);
}

template<class T> void x10::lang::Rail<void>::asyncCopy(x10::lang::GlobalRef<x10::lang::Rail<T>*> src, x10_long srcIndex,
                                                        x10::lang::Rail<T>* dst, x10_long dstIndex,
                                                        x10_long numElems,
                                                        x10::lang::VoidFun_0_0* notif) {
    if (numElems <= 0) return;
    void* srcAddr = (void*)(&src->__apply()->raw[srcIndex]);
    void* dstAddr = (void*)(&dst->raw[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
//    checkBounds(srcIndex, src.len);
//    checkBounds(srcIndex+numElems, src.len+1);
    checkBounds(dstIndex, dst->FMGL(size));
    checkBounds(dstIndex+numElems, dst->FMGL(size)+1);
    x10::util::IMC_copyFromBody(srcAddr, dstAddr, numBytes, x10::lang::Place::place(src.location), src->__apply()->raw == dst->raw, notif);
}

/*
 * Serialization and Deserialization
 */

template<class T> const x10aux::serialization_id_t x10::lang::Rail<T>::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(x10::lang::Rail<T>::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

template<class T> void x10::lang::Rail<T>::_serialize_body(x10aux::serialization_buffer& buf) {
    buf.write(this->FMGL(size));
    for (x10_long i=0; i<FMGL(size); i++) {
        buf.write(raw[i]);
    }
}

// TODO: Replicate template specialization for primitives & Complex from IMC class for rails
template<class T> x10::lang::Reference* x10::lang::Rail<T>::_deserializer(x10aux::deserialization_buffer& buf) {
    bool containsPtrs = x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = buf.read<x10_long>();
    size_t numBytes = sizeof(x10::lang::Rail<T>) -sizeof(T) + (numElems * sizeof(T)); // -sizeof(T) accounts for raw[1]
    x10::lang::Rail<T>* this_ = new (x10aux::alloc_internal(numBytes, containsPtrs)) x10::lang::Rail<T>(numElems);

    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

template<class T> void x10::lang::Rail<T>::_deserialize_body(x10aux::deserialization_buffer& buf) {
    for (x10_long i=0; i<FMGL(size); i++) {
        raw[i] = buf.read<T>();
    }
}


/*
 * ITables and object model support code
 */

template<class T> typename x10::lang::Iterable<T>::template itable<x10::lang::Rail<T> >  x10::lang::Rail<T>::_itable_0(&x10::lang::Rail<T>::equals, &x10::lang::Rail<T>::hashCode, &x10::lang::Rail<T>::iterator, &x10::lang::Rail<T>::toString, &x10::lang::Rail<T>::typeName);

template<class T> typename x10::lang::Fun_0_1<x10_int, T>::template itable<x10::lang::Rail<T> >  x10::lang::Rail<T>::_itable_1(&x10::lang::Rail<T>::equals, &x10::lang::Rail<T>::hashCode, &x10::lang::Rail<T>::__apply, &x10::lang::Rail<T>::toString, &x10::lang::Rail<T>::typeName);

template<class T> typename x10::lang::Fun_0_1<x10_long, T>::template itable<x10::lang::Rail<T> >  x10::lang::Rail<T>::_itable_2(&x10::lang::Rail<T>::equals, &x10::lang::Rail<T>::hashCode, &x10::lang::Rail<T>::__apply, &x10::lang::Rail<T>::toString, &x10::lang::Rail<T>::typeName);

template<class T> x10aux::itable_entry x10::lang::Rail<T>::_itables[4] = {
    x10aux::itable_entry(&x10aux::getRTT<x10::lang::Iterable<T> >, &_itable_0),
    x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_int, T> >, &_itable_1),
    x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_long, T> >, &_itable_2),
    x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::lang::Rail<T> >())
};


#endif // X10_LANG_RAIL_H_IMPLEMENTATION
#endif // __X10_LANG_RAIL_H_NODEPS
