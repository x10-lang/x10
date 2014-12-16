/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2011-2013.
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

namespace x10 {
    namespace lang { 
        class LongRange;
        template<class T> class Iterator;
        template<class T> class RailIterator;
        template<class T> class GlobalRef;
        template<class T> class GlobalRail;
        class String;
        class Runtime__MemoryAllocator;
    }
}

namespace x10 {
    namespace lang { 

        template<class T> class Rail;
        template <> class Rail<void>;
        class Place;
        
        extern const ::x10aux::serialization_id_t Rail_copy_to_serialization_id;
        extern const ::x10aux::serialization_id_t Rail_copy_from_serialization_id;

        extern const ::x10aux::serialization_id_t Rail_uncounted_copy_to_serialization_id;
        extern const ::x10aux::serialization_id_t Rail_unconuted_copy_from_serialization_id;
        
        extern void Rail_notifyEnclosingFinish(::x10aux::deserialization_buffer&);
        extern void Rail_serialize_finish_state(::x10aux::place, ::x10aux::serialization_buffer&);
	    extern void *Rail_buffer_finder(::x10aux::deserialization_buffer&, x10_int);
        extern void Rail_notifier(::x10aux::deserialization_buffer&, x10_int);
        extern void Rail_uncounted_notifier(::x10aux::deserialization_buffer&, x10_int);

        extern void Rail_copyToBody(void *srcAddr, void *dstAddr, x10_int numBytes,
                                   ::x10::lang::Place dstPlace, bool overlap, ::x10::lang::VoidFun_0_0* notif);
        extern void Rail_copyFromBody(void *srcAddr, void *dstAddr, x10_int numBytes,
                                     ::x10::lang::Place srcPlace, bool overlap, ::x10::lang::VoidFun_0_0* notif);
        extern void Rail_copyBody(void *srcAddr, void *dstAddr, x10_int numBytes, bool overlap);
        
        extern void failAllocNoPointers(const char* msg);

        void throwArrayIndexOutOfBoundsException(x10_long index, x10_long size) X10_PRAGMA_NORETURN;
        void throwNegativeArraySizeException() X10_PRAGMA_NORETURN;
    
        inline void checkBounds(x10_long index, x10_long size) {
            #ifndef NO_BOUNDS_CHECKS
            // Since we know size is non-negative and Rails are zero-based,
            // the bounds check can be optimized to a single unsigned comparison.
            // The C++ compiler won't do this for us, since it doesn't know that size is non-negative.
            if (((x10_ulong)index) >= ((x10_ulong)size)) {
                ::x10::lang::throwArrayIndexOutOfBoundsException(index, size);
            }
            #endif
        }

        extern void rail_copyRaw(void *srcAddr, void *dstAddr, x10_long numBytes, bool overlap);

        template<class T> class Rail : public ::x10::lang::X10Class   {
          public:
            RTT_H_DECLS_CLASS
            #ifdef __i386__
            const x10_int FMGL(size);
            #else
            const x10_long FMGL(size);
            #endif
            T raw[1]; // It's not really 1, but this is how one declares the variable array at end of a struct in ISO C++

            Rail(x10_long numElems) : FMGL(size)(numElems) { }
            
            static ::x10aux::itable_entry _itables[3];
            virtual ::x10aux::itable_entry* _getITables() { return _itables; }
            static typename ::x10::lang::Iterable<T>::template itable< ::x10::lang::Rail<T> > _itable_0;
            static typename ::x10::lang::Fun_0_1<x10_long, T>::template itable< ::x10::lang::Rail<T> > _itable_1;
    
            static ::x10::lang::Rail<T>* _make();
            void _constructor();
    
            static ::x10::lang::Rail<T>* _makeUnsafe(x10_long size, x10_boolean allocatedZeroed);
    
            static ::x10::lang::Rail<T>* _make(::x10::lang::Rail<T>* src);
            void _constructor(::x10::lang::Rail<T>* src);
    
            static ::x10::lang::Rail<T>* _make(x10_long size);
            void _constructor(x10_long size);
    
            static ::x10::lang::Rail<T>* _make(x10_long size, T init);
            void _constructor(x10_long size, T init);

            static ::x10::lang::Rail<T>* _make(x10_long size, ::x10::lang::Fun_0_1<x10_long, T>* init);
            void _constructor(x10_long size, ::x10::lang::Fun_0_1<x10_long, T>* init);

            static ::x10::lang::Rail<T>* _make(x10_long size, ::x10::lang::Runtime__MemoryAllocator* alloc);
            void _constructor(x10_long size,  ::x10::lang::Runtime__MemoryAllocator* alloc);
            
            ::x10::lang::LongRange range();

            ::x10::lang::Iterator<T>* iterator();

            virtual ::x10::lang::String* toString();

            T __apply(x10_long index) {
                checkBounds(index, FMGL(size));
                return raw[index];
            }
            T unchecked_apply(x10_long index) {
                return raw[index];
            }                

            T __set(x10_long index, T v) {
                checkBounds(index, FMGL(size));
                raw[index] = v;
                return v;
            }
            T unchecked_set(x10_long index, T v) {
                raw[index] = v;
                return v;
            }
            
            T &operator[] (x10_long index) {
                checkBounds(index, FMGL(size));
                return raw[index];
            }
            const T &operator[] (x10_long index) const {
                checkBounds(index, FMGL(size));
                return raw[index];
            }

            void fill(T value);
            
            void clear();
            void clear(x10_long start, x10_long numElems);
    
            // Serialization
            static const ::x10aux::serialization_id_t _serialization_id;
            ::x10aux::serialization_id_t _get_serialization_id() {
                return _serialization_id;
            }
    
            void _serialize_body(::x10aux::serialization_buffer& buf);

            static ::x10::lang::Reference* _deserializer(::x10aux::deserialization_buffer& buf);
            void _deserialize_body(::x10aux::deserialization_buffer& buf);
        };


        // RTT
        template<class T> ::x10aux::RuntimeType x10::lang::Rail<T>::rtt;

        template<class T> void x10::lang::Rail<T>::_initRTT() {
            const ::x10aux::RuntimeType *canonical = ::x10aux::getRTT< ::x10::lang::Rail<void> >();
            if (rtt.initStageOne(canonical)) return;
            const ::x10aux::RuntimeType* parents[2] = { ::x10aux::getRTT< ::x10::lang::Iterable<T> >(),
                                                      ::x10aux::getRTT< ::x10::lang::Fun_0_1<x10_long, T> >()
            };
            const ::x10aux::RuntimeType* params[1] = { ::x10aux::getRTT<T>()};
            ::x10aux::RuntimeType::Variance variances[1] = { ::x10aux::RuntimeType::invariant};
            const char *baseName = "x10.lang.Rail";
            rtt.initStageTwo(baseName, ::x10aux::RuntimeType::class_kind, 2, parents, 1, params, variances);
        }


        // Static methods
        template <> class Rail<void> : public ::x10::lang::X10Class {
        public:
            static ::x10aux::RuntimeType rtt;
            static const ::x10aux::RuntimeType* getRTT() { return & rtt; }
            template<class T> static void copy(::x10::lang::Rail<T>* src,
                                               ::x10::lang::Rail<T>* dst);
    
            template<class T> static void copy(::x10::lang::Rail<T>* src,
                                               x10_long srcIndex, ::x10::lang::Rail<T>* dst,
                                               x10_long dstIndex, x10_long numElems);
    
            template<class T> static void asyncCopy(::x10::lang::Rail<T>* src, x10_long srcIndex,
                                                    ::x10::lang::GlobalRail<T> dst, x10_long dstIndex,
                                                    x10_long numElems);

            template<class T> static void asyncCopy(::x10::lang::GlobalRail<T> src, x10_long srcIndex,
                                                    ::x10::lang::Rail<T>* dst, x10_long dstIndex,
                                                    x10_long numElems);

            template<class T> static void uncountedCopy(::x10::lang::Rail<T>* src, x10_long srcIndex,
                                                        ::x10::lang::GlobalRail<T> dst, x10_long dstIndex,
                                                        x10_long numElems,
                                                        ::x10::lang::VoidFun_0_0* notif);

            template<class T> static void uncountedCopy(::x10::lang::GlobalRail<T> src, x10_long srcIndex,
                                                        ::x10::lang::Rail<T>* dst, x10_long dstIndex,
                                                        x10_long numElems,
                                                        ::x10::lang::VoidFun_0_0* notif);
        };

        template <class T, x10_long SZ> class StackAllocatedRail : public ::x10::lang::Rail<T> {
        public:
            T raw2[SZ > 1 ? SZ-1 : 0]; // get the rest of the storage we need for a Rail of size SZ
            StackAllocatedRail(x10_long numElems) : ::x10::lang::Rail<T>(numElems) { }
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
#include <x10/lang/LongRange.h>
#include <x10/lang/Iterator.h>
#include <x10/lang/RailIterator.h>
#include <x10/lang/Place.h>
#include <x10/lang/String.h>
#include <x10/lang/IllegalArgumentException.h>
#include <x10/lang/Runtime__MemoryAllocator.h>
#ifndef X10_LANG_RAIL_H_GENERICS
#define X10_LANG_RAIL_H_GENERICS
#endif // X10_LANG_RAIL_H_GENERICS
#ifndef X10_LANG_RAIL_H_IMPLEMENTATION
#define X10_LANG_RAIL_H_IMPLEMENTATION

/*
 * Construction (_make methods)
 */

template<class T> ::x10::lang::Rail<T>* x10::lang::Rail<T>::_make() {
    ::x10::lang::Rail<T>* this_ = new (::x10aux::alloc_internal(sizeof(::x10::lang::Rail<T>), false)) ::x10::lang::Rail<T>(0);
    return this_;
}
template<class T> void ::x10::lang::Rail<T>::_constructor() {
}


template<class T> ::x10::lang::Rail<T>* x10::lang::Rail<T>::_makeUnsafe(x10_long size, x10_boolean allocateZeroed) {
    if (size < 0) throwNegativeArraySizeException();
    bool containsPtrs = ::x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = size;
    x10_long allocElems = numElems > 0 ? numElems - 1 : numElems;   // account for raw[1]
    size_t numBytes = sizeof(::x10::lang::Rail<T>) + (allocElems * sizeof(T)); 
    ::x10::lang::Rail<T>* this_ = new (::x10aux::alloc_internal(numBytes, containsPtrs)) ::x10::lang::Rail<T>(numElems);

    if (allocateZeroed) {
        memset(&(this_->raw), 0, numElems*sizeof(T));
    }
    return this_;
}


template<class T> ::x10::lang::Rail<T>* x10::lang::Rail<T>::_make(::x10::lang::Rail<T>* src) {
    bool containsPtrs = ::x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = ::x10aux::nullCheck(src)->FMGL(size);
    x10_long allocElems = numElems > 0 ? numElems - 1 : numElems;   // account for raw[1]
    size_t numBytes = sizeof(::x10::lang::Rail<T>) + (allocElems * sizeof(T));
    ::x10::lang::Rail<T>* this_ = new (::x10aux::alloc_internal(numBytes, containsPtrs)) ::x10::lang::Rail<T>(numElems);

    rail_copyRaw(&src->raw, &this_->raw, numElems*sizeof(T), false);
    return this_;
}
template<class T> void ::x10::lang::Rail<T>::_constructor(::x10::lang::Rail<T>* src) {
    x10_long numElems = ::x10aux::nullCheck(src)->FMGL(size);
    rail_copyRaw(&src->raw, &this->raw, numElems*sizeof(T), false);
}


template<class T> ::x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_long size) {
    if (size < 0) throwNegativeArraySizeException();
    bool containsPtrs = ::x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = size;
    x10_long allocElems = numElems > 0 ? numElems - 1 : numElems;   // account for raw[1]
    size_t numBytes = sizeof(::x10::lang::Rail<T>) + (allocElems * sizeof(T));
    ::x10::lang::Rail<T>* this_ = new (::x10aux::alloc_internal(numBytes, containsPtrs)) ::x10::lang::Rail<T>(numElems);

    memset(&(this_->raw), 0, size*sizeof(T));
    return this_;
}
template<class T> void x10::lang::Rail<T>::_constructor(x10_long size) {
    memset(&(this->raw), 0, size*sizeof(T));
}


template<class T> ::x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_long size, T init) {
    if (size < 0) throwNegativeArraySizeException();
    bool containsPtrs = ::x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = size;
    x10_long allocElems = numElems > 0 ? numElems - 1 : numElems;   // account for raw[1]
    size_t numBytes = sizeof(::x10::lang::Rail<T>) + (allocElems * sizeof(T));
    ::x10::lang::Rail<T>* this_ = new (::x10aux::alloc_internal(numBytes, containsPtrs)) ::x10::lang::Rail<T>(numElems);

    for (x10_long i = 0ll; i < size; i++) {
        this_->raw[i] = init;
    }
    return this_;
}
template<class T> void x10::lang::Rail<T>::_constructor(x10_long size, T init) {
    for (x10_long i = 0ll; i < size; i++) {
        raw[i] = init;
    }
}

template<class T> ::x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_long size, ::x10::lang::Fun_0_1<x10_long, T>* init) {
    if (size < 0) throwNegativeArraySizeException();
    bool containsPtrs = ::x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = size;
    x10_long allocElems = numElems > 0 ? numElems - 1 : numElems;   // account for raw[1]
    size_t numBytes = sizeof(::x10::lang::Rail<T>) + (allocElems * sizeof(T));
    ::x10::lang::Rail<T>* this_ = new (::x10aux::alloc_internal(numBytes, containsPtrs)) ::x10::lang::Rail<T>(numElems);

    if (size > 0) {
        ::x10aux::nullCheck(init);
        ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(init);
        T (::x10::lang::Reference::*fun)(x10_long) = ::x10aux::findITable<Fun_0_1<x10_long, T> >(recv->_getITables())->__apply;
        for (x10_long i = 0ll; i < size; i++) {
            this_->raw[i] = (recv->*(fun))(i);
        }
    }
    return this_;
}
template<class T> void x10::lang::Rail<T>::_constructor(x10_long size, ::x10::lang::Fun_0_1<x10_long, T>* init) {
    if (size > 0) {
        ::x10aux::nullCheck(init);
        ::x10::lang::Reference* recv = reinterpret_cast< ::x10::lang::Reference*>(init);
        T (::x10::lang::Reference::*fun)(x10_long) = ::x10aux::findITable<Fun_0_1<x10_long, T> >(recv->_getITables())->__apply;
        for (x10_long i = 0ll; i < size; i++) {
            raw[i] = (recv->*(fun))(i);
        }
    }
}


template<class T> ::x10::lang::Rail<T>* x10::lang::Rail<T>::_make(x10_long size, ::x10::lang::Runtime__MemoryAllocator* alloc) {
    if (size < 0) throwNegativeArraySizeException();
    bool containsPtrs = ::x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = size;
    x10_long allocElems = numElems > 0 ? numElems - 1 : numElems;   // account for raw[1]
    size_t numBytes = sizeof(::x10::lang::Rail<T>) + (allocElems * sizeof(T));

    ::x10::lang::Rail<T>* this_;
    if (alloc->FMGL(congruent)) {
        if (containsPtrs) failAllocNoPointers("Congruent memory cannot contain pointers");
        this_ = new (::x10aux::alloc_internal_congruent(numBytes)) ::x10::lang::Rail<T>(numElems);
    } else if (alloc->FMGL(hugePages)) {
        if (containsPtrs) failAllocNoPointers("Huge pages cannot contain pointers");
        this_ = new (::x10aux::alloc_internal_huge(numBytes)) ::x10::lang::Rail<T>(numElems);
    } else {
        this_ = new (::x10aux::alloc_internal(numBytes, containsPtrs)) ::x10::lang::Rail<T>(numElems);
    }

    // NOT ZEROING HUGE PAGE ALLOCATION; OS WILL ZERO ON ALLOCATE
    if (!alloc->FMGL(hugePages)) memset(&(this_->raw), 0, size*sizeof(T));
    
    return this_;
}
template<class T> void x10::lang::Rail<T>::_constructor(x10_long size, ::x10::lang::Runtime__MemoryAllocator* alloc) {
    // NOT ZEROING HUGE PAGE ALLOCATION; OS WILL ZERO ON ALLOCATE
    if (!alloc->FMGL(hugePages)) memset(&(this->raw), 0, size*sizeof(T));
}

/*
 * Instance functions 
 */

template<class T> ::x10::lang::LongRange x10::lang::Rail<T>::range() {
    return ::x10::lang::LongRange::_make(0, FMGL(size)-1);
}    

template<class T> ::x10::lang::Iterator<T>* x10::lang::Rail<T>::iterator() {
    ::x10::lang::RailIterator<T>* it = ::x10::lang::RailIterator<T>::_make(this);
    return reinterpret_cast< ::x10::lang::Iterator<T>*>(it);
}

template<class T> ::x10::lang::String* x10::lang::Rail<T>::toString() {
    char* tmp = ::x10aux::alloc_printf("[");
    x10_long sz = FMGL(size) > 10 ? 10 : FMGL(size);
    for (x10_long i = 0; i < sz; i++) {
        if (i > 0) {
            tmp = ::x10aux::realloc_printf(tmp, ",");
        }
        tmp = ::x10aux::realloc_printf(tmp,"%s",::x10aux::to_string(__apply(i))->c_str());
    }
    if (sz < FMGL(size)) {
        tmp = ::x10aux::realloc_printf(tmp, "...(omitted %lld elements", (signed long long)(FMGL(size) - sz));
    }
    tmp = ::x10aux::realloc_printf(tmp, "]");
    return ::x10::lang::String::Steal(tmp);
}

template<class T> void x10::lang::Rail<T>::fill(T value) {
    for (x10_long i = 0; i < FMGL(size); i++) {
        raw[i] = value;
    }
}

template<class T> void x10::lang::Rail<T>::clear() {
    memset(&raw, 0, sizeof(T)*FMGL(size));
}


template<class T> void x10::lang::Rail<T>::clear(x10_long start, x10_long numElems) {
    checkBounds(start, FMGL(size));
    checkBounds(start+numElems-1, FMGL(size));
    memset(&raw[start], 0, sizeof(T)*numElems);
}

/*
 * Static functions 
 */

template<class T> void x10::lang::Rail<void>::copy(::x10::lang::Rail<T>* src, ::x10::lang::Rail<T>* dst) {
    ::x10aux::nullCheck(src);
    ::x10aux::nullCheck(dst);

    if (src == dst) return;
    
    if (src->FMGL(size) != dst->FMGL(size)) {
        ::x10aux::throwException(::x10::lang::IllegalArgumentException::_make(::x10aux::makeStringLit("source and destination do not have equal size")));
    }

    rail_copyRaw(&src->raw[0], &dst->raw[0], sizeof(T)*src->FMGL(size), false);
}

template<class T> void x10::lang::Rail<void>::copy(::x10::lang::Rail<T>* src,
                                                   x10_long srcIndex,
                                                   ::x10::lang::Rail<T>* dst,
                                                   x10_long dstIndex,
                                                   x10_long numElems) {
    if (numElems <= 0) return;

    ::x10aux::nullCheck(src);
    ::x10aux::nullCheck(dst);
    checkBounds(srcIndex, src->FMGL(size));
    checkBounds(srcIndex+numElems, src->FMGL(size)+1ll);
    checkBounds(dstIndex, dst->FMGL(size));
    checkBounds(dstIndex+numElems, dst->FMGL(size)+1ll);

    rail_copyRaw(&src->raw[srcIndex], &dst->raw[dstIndex], sizeof(T)*numElems, src == dst);
}

template<class T> void x10::lang::Rail<void>::asyncCopy(::x10::lang::Rail<T>* src, x10_long srcIndex,
                                                        ::x10::lang::GlobalRail<T> dst, x10_long dstIndex,
                                                        x10_long numElems) {
    ::x10::lang::Rail<void>::uncountedCopy(src, srcIndex, dst, dstIndex, numElems, NULL);
}

template<class T> void x10::lang::Rail<void>::asyncCopy(::x10::lang::GlobalRail<T> src, x10_long srcIndex,
                                                        ::x10::lang::Rail<T>* dst, x10_long dstIndex,
                                                        x10_long numElems) {
    ::x10::lang::Rail<void>::uncountedCopy(src, srcIndex, dst, dstIndex, numElems, NULL);
}

template<class T> void x10::lang::Rail<void>::uncountedCopy(::x10::lang::Rail<T>* src, x10_long srcIndex,
                                                            ::x10::lang::GlobalRail<T> dst, x10_long dstIndex,
                                                            x10_long numElems,
                                                            ::x10::lang::VoidFun_0_0* notif) {
    if (numElems <= 0) {
        if (notif == NULL) return;
    } else {
        checkBounds(srcIndex, src->FMGL(size));
        checkBounds(srcIndex+numElems, src->FMGL(size)+1);
        checkBounds(dstIndex, dst->FMGL(size));
        checkBounds(dstIndex+numElems, dst->FMGL(size)+1);
    }

    void* srcAddr = (void*)(&src->raw[srcIndex]);
    void* dstAddr;
    if (::x10::lang::Place::_make(dst->FMGL(rail)->location)->isCUDA()) {
        dstAddr = &((T*)(dst->FMGL(rail)->__apply()))[dstIndex];
    } else {
        dstAddr = (void*)(&dst->FMGL(rail)->__apply()->raw[dstIndex]);
    }
    size_t numBytes = numElems * sizeof(T);
    ::x10::lang::Rail_copyToBody(srcAddr, dstAddr, numBytes, ::x10::lang::Place::_make(dst->FMGL(rail)->location), src->raw == dst->FMGL(rail)->__apply()->raw, notif);
}

template<class T> void x10::lang::Rail<void>::uncountedCopy(::x10::lang::GlobalRail<T> src, x10_long srcIndex,
                                                            ::x10::lang::Rail<T>* dst, x10_long dstIndex,
                                                            x10_long numElems,
                                                            ::x10::lang::VoidFun_0_0* notif) {
    if (numElems <= 0) {
        if (notif == NULL) return;
    } else {
        checkBounds(srcIndex, src->FMGL(size));
        checkBounds(srcIndex+numElems, src->FMGL(size)+1);
        checkBounds(dstIndex, dst->FMGL(size));
        checkBounds(dstIndex+numElems, dst->FMGL(size)+1);
    }

    void* srcAddr;
    if (::x10::lang::Place::_make(src->FMGL(rail)->location)->isCUDA()) {
        srcAddr = &((T*)(src->FMGL(rail)->__apply()))[srcIndex];
    } else {
        srcAddr = (void*)(&src->FMGL(rail)->__apply()->raw[srcIndex]);
    }
    void* dstAddr = (void*)(&dst->raw[dstIndex]);
    size_t numBytes = numElems * sizeof(T);
    ::x10::lang::Rail_copyFromBody(srcAddr, dstAddr, numBytes, ::x10::lang::Place::_make(src->FMGL(rail)->location), src->FMGL(rail)->__apply()->raw == dst->raw, notif);
}

/*
 * Serialization and Deserialization
 */

template<class T> const ::x10aux::serialization_id_t x10::lang::Rail<T>::_serialization_id = 
    ::x10aux::DeserializationDispatcher::addDeserializer(::x10::lang::Rail<T>::_deserializer);

template<class T> void ::x10::lang::Rail<T>::_serialize_body(::x10aux::serialization_buffer& buf) {
    buf.write<x10_long>(this->FMGL(size));
    for (x10_long i=0; i<FMGL(size); i++) {
        buf.write(raw[i]);
    }
}

template<class T> ::x10::lang::Reference* x10::lang::Rail<T>::_deserializer(::x10aux::deserialization_buffer& buf) {
    bool containsPtrs = ::x10aux::getRTT<T>()->containsPtrs;
    x10_long numElems = buf.read<x10_long>();
    x10_long allocElems = numElems > 0 ? numElems - 1 : numElems;   // account for raw[1]
    size_t numBytes = sizeof(::x10::lang::Rail<T>) + (allocElems * sizeof(T));
    ::x10::lang::Rail<T>* this_ = new (::x10aux::alloc_internal(numBytes, containsPtrs)) ::x10::lang::Rail<T>(numElems);

    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

template<class T> void x10::lang::Rail<T>::_deserialize_body(::x10aux::deserialization_buffer& buf) {
    for (x10_long i=0; i<FMGL(size); i++) {
        raw[i] = buf.read<T>();
    }
}

#define PRIMITIVE_COPY_SERIALIZATION(TYPE) \
template<> inline void x10::lang::Rail<TYPE>::_serialize_body(::x10aux::serialization_buffer& buf) {\
    x10_long sz = FMGL(size);\
    buf.write<x10_long>(sz);\
    buf.copyIn(buf, raw, sz, sizeof(TYPE));\
}\
template<> inline void x10::lang::Rail<TYPE>::_deserialize_body(::x10aux::deserialization_buffer& buf) {\
    buf.copyOut(buf, raw, FMGL(size), sizeof(TYPE));\
}

#include <x10/lang/Complex.h>

namespace x10 {
    namespace lang {
        // efficient serialization for Rail of primitive types
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

        template<> inline void x10::lang::Rail<x10_complex >::_serialize_body(::x10aux::serialization_buffer& buf) {
            x10_long sz = FMGL(size);
            buf.write<x10_long>(sz);
            // Complex is serialized as two doubles
            buf.copyIn(buf, raw, sz*2, sizeof(x10_double));
        }

        template<> inline void x10::lang::Rail<x10_complex >::_deserialize_body(::x10aux::deserialization_buffer& buf) {
            buf.copyOut(buf, raw, FMGL(size)*2, sizeof(x10_double));
        }
    }
}


/*
 * ITables and object model support code
 */

template<class T> typename ::x10::lang::Iterable<T>::template itable< ::x10::lang::Rail<T> >  x10::lang::Rail<T>::_itable_0(&::x10::lang::Rail<T>::equals, &::x10::lang::Rail<T>::hashCode, &::x10::lang::Rail<T>::iterator, &::x10::lang::Rail<T>::toString, &::x10::lang::Rail<T>::typeName);

template<class T> typename ::x10::lang::Fun_0_1<x10_long, T>::template itable< ::x10::lang::Rail<T> >  x10::lang::Rail<T>::_itable_1(&::x10::lang::Rail<T>::equals, &::x10::lang::Rail<T>::hashCode, &::x10::lang::Rail<T>::__apply, &::x10::lang::Rail<T>::toString, &::x10::lang::Rail<T>::typeName);

template<class T> ::x10aux::itable_entry x10::lang::Rail<T>::_itables[3] = {
    ::x10aux::itable_entry(&::x10aux::getRTT< ::x10::lang::Iterable<T> >, &_itable_0),
    ::x10aux::itable_entry(&::x10aux::getRTT< ::x10::lang::Fun_0_1<x10_long, T> >, &_itable_1),
    ::x10aux::itable_entry(NULL, (void*)"x10.lang.Rail[T]")
};


#endif // X10_LANG_RAIL_H_IMPLEMENTATION
#endif // __X10_LANG_RAIL_H_NODEPS
