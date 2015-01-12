/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/alloc.h>
#include <x10aux/atomic_ops.h>
#include <x10aux/reference_logger.h>

#include <x10/lang/Reference.h>
#include <x10/lang/Comparable.h>
#include <x10/lang/Arithmetic.h>
#include <x10/lang/Bitwise.h>
#include <x10/util/Ordered.h>

#include <cstdarg>

using namespace x10aux;
using namespace x10::lang;

#define TRACE_RTT_INIT 0

const char *RuntimeType::name() const {
    if (NULL == fullTypeName) {
        assert(paramsc > 0); // if paramssc == 0, then fullTypeName is set to baseName in initRTT();
        std::ostringstream ss;
        ss << baseName;
        ss << "[";
        for (int i=0; i<paramsc; i++) {
            if (i>0) ss << ",";
            ss << params[i]->name();
        }
        ss << "]";
        const_cast<RuntimeType*>(this)->fullTypeName = ::strdup(ss.str().c_str());
    }
    
    return fullTypeName;
}

bool RuntimeType::subtypeOf(const RuntimeType * const other) const {
    // Checks to try to catch partially initialized RTT objects before we use them.
    assert(isInitialized);
    assert(other->isInitialized);

    if (equals(other)) return true; // trivial case

    // the NullType should be considered a subtype of any class or interface 
    if (equals(getRTT<x10::lang::NullType>())) {
        if (other->kind == class_kind || other->kind == interface_kind) return true;
    }
    
    if (paramsc > 0 && canonical->equals(other->canonical)) {
        // Different instances of the same generic type (since canonical is equal).
        // this->subtypeOf(other) will be true exactly when the type parameters
        // of have the subtyping relationship specified by the variances.
        assert(other->paramsc == paramsc);
        for (int i=0; i<paramsc; i++) {
            assert(other->variances[i] == variances[i]);
            assert(params[i] != NULL);
            assert(other->params[i] != NULL);
            switch(variances[i]) {
            case covariant:
                if (!params[i]->subtypeOf(other->params[i])) return false;
                break;
            case contravariant:
                if (!other->params[i]->subtypeOf(params[i])) return false;
                break;
            case invariant:
                if (!params[i]->equals(other->params[i])) return false;
                break;
            }
        }
        return true;
    }

    for (int i = 0; i < parentsc; ++i) {
        if (parents[i]->subtypeOf(other)) return true;
    }
    return false;
}

bool RuntimeType::instanceOf (const Reference* other) const {
    if (NULL == other)
        return false;
    return other->_type()->subtypeOf(this);
}

bool RuntimeType::concreteInstanceOf (const Reference* other) const {
    if (NULL == other)
        return false;
    return other->_type()->equals(this);
}

void RuntimeType::initializeForMultiThreading() {
    initRTTLock = new (system_alloc<reentrant_lock>())reentrant_lock();
}

bool RuntimeType::initStageOne(const RuntimeType *canonical_) {

    // Ensure that at most one thread is doing any RTT initialization at a time.
    // This is overkill, since many RTT's have nothing to do with each other and
    // RTT initialization is idempotent. However, we want to make sure that if
    // a thread asks for an RTT, it gets a fully initialized RTT before it starts
    // operating on it and we don't get a race with multiple threads partially
    // initializing an RTT.
    // 
    // NOTE: Lock == NULL ===> single threaded C++ static initialization
    if (NULL != initRTTLock) {
        initRTTLock->lock();
    }

    if (TRACE_RTT_INIT) {
        fprintf(stdout, "RTT: entering initStageOne for %p with canonical_ %p\n", (void*)this, (void*)canonical_);
    }
    
    if (canonical != NULL) {
        if (isInitialized) {
            if (TRACE_RTT_INIT) {
                fprintf(stdout, "RTT: exiting initStageOne for %p because stageOne completed by another thread\n", (void*)this);
            }
            if (NULL != initRTTLock) {
                initRTTLock->unlock();
            }
            return true; // another thread finished the job while this thread was blocked on initRTTLock.
        }
        // We should only get here if there is a cyclic intialization in progress.
        // We don't have a 100% foolproof way to be sure that is what is happening, so
        // just hope that is what is happening and return.
        if (TRACE_RTT_INIT) {
            fprintf(stdout, "RTT: exiting initStageOne for %p due to apparent cyclic initialization (%p)\n", (void*)this, (void*)canonical);
        }
        if (NULL != initRTTLock) {
            initRTTLock->unlock();
        }
        return true;
    }
    
    canonical = canonical_;

    if (TRACE_RTT_INIT) {
        fprintf(stdout, "RTT: exiting initStageOne for %p after setting canonical to %p\n", (void*)this, (void*)canonical);
    }
    
    // NOTE: Intentionally did not call unlock before returning.
    //       the unlock will happen at the end of initStageTwo
    //       Return false to indicate that the thread should continue with initStageTwo.
    return false;  
}


void RuntimeType::initStageTwo(const char* baseName_,
                               Kind kind_,
                               int parentsc_, const RuntimeType** parents_,
                               int paramsc_, const RuntimeType** params_,
                               Variance* variances_) {
    // NOTE: Lock still held because it was not released before returning
    //       false from the end of initStageOne
    if (TRACE_RTT_INIT) {
        fprintf(stdout, "RTT: entering initStageTwo for %p\n", (void*)this);
    }
    
    baseName = baseName_;
    kind = kind_;
    parentsc = parentsc_;
    paramsc = paramsc_;
    containsPtrs = true; // TODO: Eventually the compiler should analyze structs and where possible set containsPtrs for their RTT's to false.
    hasZ = true; // TODO: Eventually the compiler should analyze types and set hasZ for their RTT's appropriately.
    if (parentsc > 0) {
        parents = system_alloc<const RuntimeType*>(parentsc * sizeof(const RuntimeType*));
        for (int i=0; i<parentsc; i++) {
            parents[i] = parents_[i];
        }
    } else {
        parents = NULL;
    }
    if (paramsc > 0) {
        params = system_alloc<const RuntimeType*>(paramsc * sizeof(const RuntimeType*));
        variances = system_alloc<Variance>(paramsc * sizeof(Variance));
        for (int i=0; i<paramsc; i++) {
            params[i] = params_[i];
            variances[i] = variances_[i];
        }
    } else {
        params = NULL;
        variances = NULL;
        fullTypeName = baseName;
    }

    x10aux::atomic_ops::store_load_barrier();
    isInitialized = true; // must come after the store_load_barrier

    if (TRACE_RTT_INIT) {
        fprintf(stdout, "RTT: initStageTwo complete for %p %s\n", (void*)this, baseName);
    }
        
    // Unlock paired with lock operation at entry to initStageOne.
    if (NULL != initRTTLock) {
        initRTTLock->unlock();
    }
}
    
void RuntimeType::initBooleanType() {
    if (BooleanType.initStageOne(&BooleanType)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Comparable<x10_boolean> >()};
    BooleanType.initStageTwo("x10.lang.Boolean", struct_kind, 2, parents, 0, NULL, NULL);
    BooleanType.containsPtrs = false;
}
void RuntimeType::initByteType() {
    if (ByteType.initStageOne(&ByteType)) return;
    const x10aux::RuntimeType* parents[5] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Comparable<x10_byte> >(), x10aux::getRTT<x10::lang::Arithmetic<x10_byte> >(), x10aux::getRTT<x10::lang::Bitwise<x10_byte> >(), x10aux::getRTT<x10::util::Ordered<x10_byte> >()};
    ByteType.initStageTwo("x10.lang.Byte", struct_kind, 5, parents, 0, NULL, NULL);
    ByteType.containsPtrs = false;
}
void RuntimeType::initCharType() {
    if (CharType.initStageOne(&CharType)) return;
    const x10aux::RuntimeType* parents[3] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Comparable<x10_char> >(), x10aux::getRTT<x10::util::Ordered<x10_char> >()};
    CharType.initStageTwo("x10.lang.Char", struct_kind, 3, parents, 0, NULL, NULL);
    CharType.containsPtrs = false;
}
void RuntimeType::initShortType() {
    if (ShortType.initStageOne(&ShortType)) return;
    const x10aux::RuntimeType* parents[5] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Comparable<x10_short> >(), x10aux::getRTT<x10::lang::Arithmetic<x10_short> >(), x10aux::getRTT<x10::lang::Bitwise<x10_short> >(), x10aux::getRTT<x10::util::Ordered<x10_short> >()};
    ShortType.initStageTwo("x10.lang.Short", struct_kind, 5, parents, 0, NULL, NULL);
    ShortType.containsPtrs = false;
}
void RuntimeType::initIntType() {
    if (IntType.initStageOne(&IntType)) return;
    const x10aux::RuntimeType* parents[5] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Comparable<x10_int> >(), x10aux::getRTT<x10::lang::Arithmetic<x10_int> >(), x10aux::getRTT<x10::lang::Bitwise<x10_int> >(), x10aux::getRTT<x10::util::Ordered<x10_int> >()};
    IntType.initStageTwo("x10.lang.Int", struct_kind, 5, parents, 0, NULL, NULL);
    IntType.containsPtrs = false;
}
void RuntimeType::initFloatType() {
    if (FloatType.initStageOne(&FloatType)) return;
    const x10aux::RuntimeType* parents[4] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Comparable<x10_float> >(), x10aux::getRTT<x10::lang::Arithmetic<x10_float> >(), x10aux::getRTT<x10::util::Ordered<x10_float> >()};
    FloatType.initStageTwo("x10.lang.Float", struct_kind, 4, parents, 0, NULL, NULL);
    FloatType.containsPtrs = false;
}
void RuntimeType::initLongType() {
    if (LongType.initStageOne(&LongType)) return;
    const x10aux::RuntimeType* parents[5] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Comparable<x10_long> >(), x10aux::getRTT<x10::lang::Arithmetic<x10_long> >(), x10aux::getRTT<x10::lang::Bitwise<x10_long> >(), x10aux::getRTT<x10::util::Ordered<x10_long> >()};
    LongType.initStageTwo("x10.lang.Long", struct_kind, 5, parents, 0, NULL, NULL);
    LongType.containsPtrs = false;
}
void RuntimeType::initDoubleType() {
    if (DoubleType.initStageOne(&DoubleType)) return;
    const x10aux::RuntimeType* parents[4] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Comparable<x10_double> >(), x10aux::getRTT<x10::lang::Arithmetic<x10_double> >(), x10aux::getRTT<x10::util::Ordered<x10_double> >()};
    DoubleType.initStageTwo("x10.lang.Double", struct_kind, 4, parents, 0, NULL, NULL);
    DoubleType.containsPtrs = false;
}
void RuntimeType::initComplexType() {
    if (ComplexType.initStageOne(&ComplexType)) return;
    const x10aux::RuntimeType* parents[2] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Arithmetic<x10_complex> >()};
    ComplexType.initStageTwo("x10.lang.Complex", struct_kind, 2, parents, 0, NULL, NULL);
    ComplexType.containsPtrs = false;
}
void RuntimeType::initUByteType() {
    if (UByteType.initStageOne(&UByteType)) return;
    const x10aux::RuntimeType* parents[5] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Comparable<x10_ubyte> >(), x10aux::getRTT<x10::lang::Arithmetic<x10_ubyte> >(), x10aux::getRTT<x10::lang::Bitwise<x10_ubyte> >(), x10aux::getRTT<x10::util::Ordered<x10_ubyte> >()};
    UByteType.initStageTwo("x10.lang.UByte", struct_kind, 5, parents, 0, NULL, NULL);
    UByteType.containsPtrs = false;
}
void RuntimeType::initUShortType() {
    if (UShortType.initStageOne(&UShortType)) return;
    const x10aux::RuntimeType* parents[5] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Comparable<x10_ushort> >(), x10aux::getRTT<x10::lang::Arithmetic<x10_ushort> >(), x10aux::getRTT<x10::lang::Bitwise<x10_ushort> >(), x10aux::getRTT<x10::util::Ordered<x10_ushort> >()};
    UShortType.initStageTwo("x10.lang.UShort", struct_kind, 5, parents, 0, NULL, NULL);
    UShortType.containsPtrs = false;
}
void RuntimeType::initUIntType() {
    if (UIntType.initStageOne(&UIntType)) return;
    const x10aux::RuntimeType* parents[5] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Comparable<x10_uint> >(), x10aux::getRTT<x10::lang::Arithmetic<x10_uint> >(), x10aux::getRTT<x10::lang::Bitwise<x10_uint> >(), x10aux::getRTT<x10::util::Ordered<x10_uint> >()};
    UIntType.initStageTwo("x10.lang.UInt", struct_kind, 5, parents, 0, NULL, NULL);
    UIntType.containsPtrs = false;
}
void RuntimeType::initULongType() {
    if (ULongType.initStageOne(&ULongType)) return;
    const x10aux::RuntimeType* parents[5] = { x10aux::getRTT<x10::lang::Any>(), x10aux::getRTT<x10::lang::Comparable<x10_ulong> >(), x10aux::getRTT<x10::lang::Arithmetic<x10_ulong> >(), x10aux::getRTT<x10::lang::Bitwise<x10_ulong> >(), x10aux::getRTT<x10::util::Ordered<x10_ulong> >()};
    ULongType.initStageTwo("x10.lang.ULong", struct_kind, 5, parents, 0, NULL, NULL);
    ULongType.containsPtrs = false;
}

RuntimeType RuntimeType::BooleanType;
RuntimeType RuntimeType::ByteType;
RuntimeType RuntimeType::CharType;
RuntimeType RuntimeType::ShortType;
RuntimeType RuntimeType::IntType;
RuntimeType RuntimeType::FloatType;
RuntimeType RuntimeType::LongType;
RuntimeType RuntimeType::DoubleType;
RuntimeType RuntimeType::ComplexType;
RuntimeType RuntimeType::UByteType;
RuntimeType RuntimeType::UShortType;
RuntimeType RuntimeType::UIntType;
RuntimeType RuntimeType::ULongType;


const char *RuntimeFunType::name() const {
    if (NULL == fullTypeName) {
        assert(paramsc > 0); // if paramssc == 0, then fullTypeName is set to baseName in initRTT();
        std::ostringstream ss;
        ss << "(";
        for (int i=0; i<paramsc-1; i++) {
            if (i>0) ss << ",";
            ss << params[i]->name();
        }
        ss << ")=>";
        ss << params[paramsc-1]->name();
        const_cast<RuntimeFunType*>(this)->fullTypeName = ::strdup(ss.str().c_str());
    }
    
    return fullTypeName;
}

const char *RuntimeVoidFunType::name() const {
    if (NULL == fullTypeName) {
        assert(paramsc > 0); // if paramssc == 0, then fullTypeName is set to baseName in initRTT();
        std::ostringstream ss;
        ss << "(";
        for (int i=0; i<paramsc; i++) {
            if (i>0) ss << ",";
            ss << params[i]->name();
        }
        ss << ")=>void";
        const_cast<RuntimeVoidFunType*>(this)->fullTypeName = ::strdup(ss.str().c_str());
    }
    
    return fullTypeName;
}


x10aux::reentrant_lock* RuntimeType::initRTTLock;

// vim:tabstop=4:shiftwidth=4:expandtab
