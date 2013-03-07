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
        class String;
        class Unsafe__Token;
    }
}

namespace x10 {
    namespace lang { 

        template<class T> class Rail;
        template <> class Rail<void>;

        template<class T> class Rail : public x10::lang::X10Class   {
          public:
            RTT_H_DECLS_CLASS
            x10_long FMGL(size);
            x10::util::IndexedMemoryChunk<T > FMGL(raw);
    
            static x10aux::itable_entry _itables[4];
            virtual x10aux::itable_entry* _getITables() { return _itables; }
            static typename x10::lang::Iterable<T>::template itable<x10::lang::Rail<T> > _itable_0;
            static typename x10::lang::Fun_0_1<x10_int, T>::template itable<x10::lang::Rail<T> > _itable_1;
            static typename x10::lang::Fun_0_1<x10_long, T>::template itable<x10::lang::Rail<T> > _itable_2;
    
            x10::lang::LongRange range();
            virtual x10::lang::Iterator<T>* iterator();
            virtual x10::lang::String* toString();
    
            virtual x10::util::IndexedMemoryChunk<T > raw();

            void _constructor(x10::util::IndexedMemoryChunk<T > backingStore);
            static x10::lang::Rail<T>* _make(x10::util::IndexedMemoryChunk<T > backingStore);
    
            void _constructor();
            static x10::lang::Rail<T>* _make();
    
            void _constructor(x10::lang::Unsafe__Token* id__123, x10_long size, x10_boolean allocatedZeroed);
            static x10::lang::Rail<T>* _make(x10::lang::Unsafe__Token* id__123, x10_long size, x10_boolean allocatedZeroed);
    
            void _constructor(x10::lang::Rail<T>* src);
            static x10::lang::Rail<T>* _make(x10::lang::Rail<T>* src);
    
            void _constructor(x10_long size);
            static x10::lang::Rail<T>* _make(x10_long size);
    
            void _constructor(x10_int size);
            static x10::lang::Rail<T>* _make(x10_int size);

            void _constructor(x10_long size, T init);
            static x10::lang::Rail<T>* _make(x10_long size, T init);

            void _constructor(x10_int size, T init);
            static x10::lang::Rail<T>* _make(x10_int size, T init);
            
            void _constructor(x10_long size, x10::lang::Fun_0_1<x10_long, T>* init);
            static x10::lang::Rail<T>* _make(x10_long size, x10::lang::Fun_0_1<x10_long, T>* init);
    
            void _constructor(x10_int size, x10::lang::Fun_0_1<x10_int, T>* init);
            static x10::lang::Rail<T>* _make(x10_int size, x10::lang::Fun_0_1<x10_int, T>* init);

            virtual T __apply(x10_long index);
            virtual T __apply(x10_int index);

            virtual T __set(x10_long index, T v);
            virtual T __set(x10_int index, T v);

            virtual void clear();
            virtual void clear(x10_long start, x10_long numElems);
    
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
                                               x10_int numElems);
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
#include <x10/lang/Long.h>
#include <x10/lang/Int.h>
#include <x10/lang/LongRange.h>
#include <x10/lang/Boolean.h>
#include <x10/lang/Iterator.h>
#include <x10/lang/RailIterator.h>
#include <x10/lang/String.h>
#include <x10/util/StringBuilder.h>
#include <x10/util/ArrayList.h>
#include <x10/lang/Char.h>
#include <x10/lang/Unsafe__Token.h>
#include <x10/lang/IllegalArgumentException.h>
#ifndef X10_LANG_RAIL_H_GENERICS
#define X10_LANG_RAIL_H_GENERICS
#endif // X10_LANG_RAIL_H_GENERICS
#ifndef X10_LANG_RAIL_H_IMPLEMENTATION
#define X10_LANG_RAIL_H_IMPLEMENTATION
#include <x10/lang/Rail.h>


// ITABLES
template<class T> typename x10::lang::Iterable<T>::template itable<x10::lang::Rail<T> >  x10::lang::Rail<T>::_itable_0(&x10::lang::Rail<T>::equals, &x10::lang::Rail<T>::hashCode, &x10::lang::Rail<T>::iterator, &x10::lang::Rail<T>::toString, &x10::lang::Rail<T>::typeName);

template<class T> typename x10::lang::Fun_0_1<x10_int, T>::template itable<x10::lang::Rail<T> >  x10::lang::Rail<T>::_itable_1(&x10::lang::Rail<T>::equals, &x10::lang::Rail<T>::hashCode, &x10::lang::Rail<T>::__apply, &x10::lang::Rail<T>::toString, &x10::lang::Rail<T>::typeName);

template<class T> typename x10::lang::Fun_0_1<x10_long, T>::template itable<x10::lang::Rail<T> >  x10::lang::Rail<T>::_itable_2(&x10::lang::Rail<T>::equals, &x10::lang::Rail<T>::hashCode, &x10::lang::Rail<T>::__apply, &x10::lang::Rail<T>::toString, &x10::lang::Rail<T>::typeName);

template<class T> x10aux::itable_entry x10::lang::Rail<T>::_itables[4] = {
    x10aux::itable_entry(&x10aux::getRTT<x10::lang::Iterable<T> >, &_itable_0),
    x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_int, T> >, &_itable_1),
    x10aux::itable_entry(&x10aux::getRTT<x10::lang::Fun_0_1<x10_long, T> >, &_itable_2),
    x10aux::itable_entry(NULL, (void*)x10aux::getRTT<x10::lang::Rail<T> >())
};

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

template<class T> x10::util::IndexedMemoryChunk<T >  x10::lang::Rail<T>::raw() {
    return this->FMGL(raw);
}

template<class T> void x10::lang::Rail<T>::_constructor(x10::util::IndexedMemoryChunk<T > backingStore) {
    FMGL(size) = ((x10_long) ((backingStore)->length()));
    this->FMGL(raw) = backingStore;
}
template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10::util::IndexedMemoryChunk<T > backingStore) {
    x10::lang::Rail<T>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<T> >(), 0, sizeof(x10::lang::Rail<T>))) x10::lang::Rail<T>();
    this_->_constructor(backingStore);
    return this_;
}


template<class T> void x10::lang::Rail<T>::_constructor() {
    FMGL(size) = 0ll;
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<T >(((x10_int)0), 8, false, false);
}
template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make() {
    x10::lang::Rail<T>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<T> >(), 0, sizeof(x10::lang::Rail<T>))) x10::lang::Rail<T>();
    this_->_constructor();
    return this_;
}


template<class T> void x10::lang::Rail<T>::_constructor(x10::lang::Unsafe__Token* id__123, x10_long size, x10_boolean allocateZeroed) {
    FMGL(size) = size;
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<T >(size, 8, false, allocateZeroed);
}
template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10::lang::Unsafe__Token* id__123, x10_long size, x10_boolean allocateZeroed) {
    x10::lang::Rail<T>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<T> >(), 0, sizeof(x10::lang::Rail<T>))) x10::lang::Rail<T>();
    this_->_constructor(id__123, size);
    return this_;
}

template<class T> void x10::lang::Rail<T>::_constructor(x10::lang::Rail<T>* src) {
    FMGL(size) = x10aux::nullCheck(src)->FMGL(size);
    x10_int size = (x10_int)FMGL(size);
    
    x10::util::IndexedMemoryChunk<T > dst = x10::util::IndexedMemoryChunk<void>::allocate<T >(size, 8, false, false);
    x10::util::IndexedMemoryChunk<void>::copy<T >(src->FMGL(raw), 0, dst, 0, size);
    this->FMGL(raw) = dst;
}
template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10::lang::Rail<T>* src) {
    x10::lang::Rail<T>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<T> >(), 0, sizeof(x10::lang::Rail<T>))) x10::lang::Rail<T>();
    this_->_constructor(src);
    return this_;
}


template<class T> void x10::lang::Rail<T>::_constructor(x10_long size) {
    FMGL(size) = size;
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<T >(size, 8, false, true);
}
template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_long size) {
    x10::lang::Rail<T>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<T> >(), 0, sizeof(x10::lang::Rail<T>))) x10::lang::Rail<T>();
    this_->_constructor(size);
    return this_;
}

template<class T> void x10::lang::Rail<T>::_constructor(x10_long size, T init) {
    FMGL(size) = size;
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<T >(size, 8, false, false);
    for (x10_long i = 0ll; i < size; i++) {
        (this->FMGL(raw))->__set(i, init);
    }
}
template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_long size, T init) {
    x10::lang::Rail<T>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<T> >(), 0, sizeof(x10::lang::Rail<T>))) x10::lang::Rail<T>();
    this_->_constructor(size, init);
    return this_;
}


template<class T> void x10::lang::Rail<T>::_constructor(x10_long size, x10::lang::Fun_0_1<x10_long, T>* init) {
    FMGL(size) = size;
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<T >(size, 8, false, false);
    
    for (x10_long i = 0ll; i < size; i++) {
        (this->FMGL(raw))->__set(i, x10::lang::Fun_0_1<x10_long, T>::__apply(x10aux::nullCheck(init), i));
    }
}
template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_long size, x10::lang::Fun_0_1<x10_long, T>* init) {
    x10::lang::Rail<T>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<T> >(), 0, sizeof(x10::lang::Rail<T>))) x10::lang::Rail<T>();
    this_->_constructor(size, init);
    return this_;
}


template<class T> T x10::lang::Rail<T>::__apply(x10_long index) {
    return (this->FMGL(raw))->__apply(index);
}

template<class T> T x10::lang::Rail<T>::__set(x10_long index, T v) {
    (this->FMGL(raw))->__set(index, v);
    return v;
}

template<class T> void x10::lang::Rail<T>::clear() {
    this->FMGL(raw)->clear(0ll, this->FMGL(size));
}


template<class T> void x10::lang::Rail<T>::clear(x10_long start, x10_long numElems) {
    this->FMGL(raw)->clear(start, numElems);
}


template<class T> void x10::lang::Rail<T>::_constructor(x10_int size) {
    FMGL(size) = ((x10_long) (size));
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<T >(size, 8, false, true);
}
template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_int size) {
    x10::lang::Rail<T>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<T> >(), 0, sizeof(x10::lang::Rail<T>))) x10::lang::Rail<T>();
    this_->_constructor(size);
    return this_;
}

template<class T> void x10::lang::Rail<T>::_constructor(x10_int size, T init) {
    FMGL(size) = ((x10_long) (size));
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<T >(size, 8, false, false);
    for (x10_int i = 0; i < size; i++) { 
        (this->FMGL(raw))->__set(i, init);
    }
}
template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_int size, T init) {
    x10::lang::Rail<T>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<T> >(), 0, sizeof(x10::lang::Rail<T>))) x10::lang::Rail<T>();
    this_->_constructor(size, init);
    return this_;
}

template<class T> void x10::lang::Rail<T>::_constructor(x10_int size, x10::lang::Fun_0_1<x10_int, T>* init) {
    FMGL(size) = ((x10_long) (size));
    this->FMGL(raw) = x10::util::IndexedMemoryChunk<void>::allocate<T >(size, 8, false, false);
    
    for (x10_int i = 0; i < size; i++) {
        (this->FMGL(raw))->__set(i, x10::lang::Fun_0_1<x10_int, T>::__apply(x10aux::nullCheck(init), i));
    }
}
template<class T> x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_int size, x10::lang::Fun_0_1<x10_int, T>* init) {
    x10::lang::Rail<T>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<T> >(), 0, sizeof(x10::lang::Rail<T>))) x10::lang::Rail<T>();
    this_->_constructor(size, init);
    return this_;
}


template<class T> T x10::lang::Rail<T>::__apply(x10_int index) {
    return (this->FMGL(raw))->__apply(index);
}

template<class T> T x10::lang::Rail<T>::__set(x10_int index, T v) {
    (this->FMGL(raw))->__set(index, v);
    return v;
}

/// More serialization stuff

template<class T> const x10aux::serialization_id_t x10::lang::Rail<T>::_serialization_id = 
    x10aux::DeserializationDispatcher::addDeserializer(x10::lang::Rail<T>::_deserializer, x10aux::CLOSURE_KIND_NOT_ASYNC);

template<class T> void x10::lang::Rail<T>::_serialize_body(x10aux::serialization_buffer& buf) {
    buf.write(this->FMGL(raw));
    buf.write(this->FMGL(size));
}

template<class T> x10::lang::Reference* x10::lang::Rail<T>::_deserializer(x10aux::deserialization_buffer& buf) {
    x10::lang::Rail<T>* this_ = new (memset(x10aux::alloc<x10::lang::Rail<T> >(), 0, sizeof(x10::lang::Rail<T>))) x10::lang::Rail<T>();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

template<class T> void x10::lang::Rail<T>::_deserialize_body(x10aux::deserialization_buffer& buf) {
    FMGL(raw) = buf.read<x10::util::IndexedMemoryChunk<T > >();
    FMGL(size) = buf.read<x10_long>();
}

template<class T> void x10::lang::Rail<void>::copy(x10::lang::Rail<T>* src,
                                                   x10::lang::Rail<T>* dst) {
    
    if ((!x10aux::struct_equals(x10aux::nullCheck(src)->FMGL(size), x10aux::nullCheck(dst)->FMGL(size)))) {
        x10aux::throwException(x10::lang::IllegalArgumentException::_make(x10aux::makeStringLit("source and destination do not have equal size")));
    }

    x10::util::IndexedMemoryChunk<void>::copy<T >(x10aux::nullCheck(src)->
                                                  FMGL(raw),((x10_int)0),x10aux::nullCheck(dst)->
                                                  FMGL(raw),((x10_int)0),(x10aux::nullCheck(src)->FMGL(raw))->length());
}

template<class T> void x10::lang::Rail<void>::copy(x10::lang::Rail<T>* src,
                                                   x10_long srcIndex,
                                                   x10::lang::Rail<T>* dst,
                                                   x10_long dstIndex,
                                                   x10_long numElems) {

    x10::util::IndexedMemoryChunk<void>::copy<T >(x10aux::nullCheck(src)->FMGL(raw),
                                                  (x10_int)srcIndex,
                                                  x10aux::nullCheck(dst)->FMGL(raw),
                                                  (x10_int)dstIndex,
                                                  (x10_int)numElems);
}

template<class T> void x10::lang::Rail<void>::copy(x10::lang::Rail<T>* src,
                                                   x10_int srcIndex,
                                                   x10::lang::Rail<T>* dst,
                                                   x10_int dstIndex,
                                                   x10_int numElems) {
    x10::util::IndexedMemoryChunk<void>::copy<T >(x10aux::nullCheck(src)->FMGL(raw),
                                                  srcIndex,
                                                  x10aux::nullCheck(dst)->FMGL(raw),
                                                  dstIndex,
                                                  numElems);
}

#endif // X10_LANG_RAIL_H_IMPLEMENTATION
#endif // __X10_LANG_RAIL_H_NODEPS
