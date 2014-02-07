/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#ifndef X10AUX_RTT_H
#define X10AUX_RTT_H

#include <x10aux/config.h>
#include <assert.h>
#include <pthread.h>
#include <x10aux/lock.h>
#include <x10/lang/Complex.h>

/* Macro to use in class declaration for boilerplate RTT junk */
#define RTT_H_DECLS_CLASS \
    static ::x10aux::RuntimeType rtt;                                   \
    static const ::x10aux::RuntimeType* getRTT() { if (!rtt.isInitialized) _initRTT(); return &rtt; } \
    static void _initRTT(); \
    virtual const ::x10aux::RuntimeType *_type() const { return getRTT(); }

#define RTT_H_DECLS_STRUCT \
    static ::x10aux::RuntimeType rtt;                                   \
    static const ::x10aux::RuntimeType* getRTT() { if (!rtt.isInitialized) _initRTT(); return &rtt; } \
    static void _initRTT(); \

#define RTT_H_DECLS_INTERFACE \
    static ::x10aux::RuntimeType rtt;                                   \
    static const ::x10aux::RuntimeType* getRTT() { if (!rtt.isInitialized) _initRTT(); return &rtt; } \
    static void _initRTT();

#define RTT_CC_DECLS1(TYPE,NAME,KIND,P1)                        \
    ::x10aux::RuntimeType TYPE::rtt;                            \
    void TYPE::_initRTT() {                                     \
        if (rtt.initStageOne(&rtt)) return;                     \
        const ::x10aux::RuntimeType* parents[1] = {P1::getRTT()};   \
        rtt.initStageTwo(NAME, KIND, 1, parents, 0, NULL, NULL); \
    }

#define RTT_CC_DECLS0(TYPE,NAME,KIND)                        \
    ::x10aux::RuntimeType TYPE::rtt;                            \
    void TYPE::_initRTT() {                                     \
        if (rtt.initStageOne(&rtt)) return;                     \
        rtt.initStageTwo(NAME, KIND, 0, NULL, 0, NULL, NULL); \
    }

namespace x10 {
    namespace lang {
        class Reference;
        class NullType;
        class String;
    }
}

namespace x10aux {

    class RuntimeType {
    private:
        static x10aux::reentrant_lock *initRTTLock;
        
    public:
        /*
         * RTT objects for all builtin primitive types.
         */
        static RuntimeType BooleanType;
        static RuntimeType ByteType;
        static RuntimeType CharType;
        static RuntimeType ShortType;
        static RuntimeType IntType;
        static RuntimeType FloatType;
        static RuntimeType LongType;
        static RuntimeType DoubleType;
        static RuntimeType ComplexType;
        static RuntimeType UByteType;
        static RuntimeType UShortType;
        static RuntimeType UIntType;
        static RuntimeType ULongType;

        enum Variance { covariant, contravariant, invariant };
        enum Kind { class_kind, struct_kind, interface_kind };
        
    public:
        const RuntimeType *canonical;
        const RuntimeType **parents;
        const RuntimeType **params;
        Variance *variances;
        const char* fullTypeName;
        const char* baseName;
        int parentsc;
        int paramsc;
        Kind kind;
        bool containsPtrs;
        bool isInitialized;
        bool hasZ;

        // Initialization protocol.
        // (a) Call initStageOne before attempting to acquire the RTT
        //     if any of the types parents or parameters.
        // (b) if initStageOne returns true, then the type is either
        //       (a) being recursively initialized by the same thread
        //       (b) has already been initialied by another thread
        //     In either case, return from initRTT because by the time
        //     we unwind all the way back to the "user" code, the recursive
        //     RTT cycle will have been fully initialized.
        // (c) call getRTT on any input RTTs
        // (d) call initStageTwo

        
        bool initStageOne(const RuntimeType* canonical_);
        
        void initStageTwo(const char* baseName_,
                          Kind kind_,
                          int parsentsc_, const RuntimeType** parents_,
                          int paramsc_, const RuntimeType** params_, Variance* variances_);

        virtual const char *name() const;

        bool subtypeOf(const RuntimeType * const other) const;

        bool instanceOf(const ::x10::lang::Reference* other) const;

        bool concreteInstanceOf(const ::x10::lang::Reference* other) const;

        bool equals(const RuntimeType * const other) const {
            return other == this;
        }

        // Can the X10 type this RTT instance represents contain pointers to other chunks of heap-allocated memory?
        // Used in conjunction with BDWGC to optimize allocation of non-pointer containing rails.
        // It is always safe to return true; returning false is used in places like rail allocation to select an
        // alternate allocation routine for memory that will not contain pointers (and therefore does not have to
        // be scanned for pointers during the GC). Returning false incorrectly can cause memory corruption by hiding
        // pointers from the GC's scanning logic. 
        bool containsPointers() const { return containsPtrs; }

        // Does the X10 type this RTT instance represents admit a 0 value?
        bool hasZero() const { return hasZ; }
        
        static void initBooleanType();
        static void initByteType();
        static void initCharType();
        static void initShortType();
        static void initIntType();
        static void initFloatType();
        static void initLongType();
        static void initDoubleType();
        static void initComplexType();
        static void initUByteType();
        static void initUShortType();
        static void initUIntType();
        static void initULongType();

        // This method must be called by the C main function before any
        // concurrent activity is enabled in this place.
        // It initializes the initRTTLock field which is used to synchronize
        // the initialization of RuntimeType objects
        static void initializeForMultiThreading();
    };

    class RuntimeFunType : public RuntimeType {
    public:
        virtual const char *name() const;
    };
    class RuntimeVoidFunType : public RuntimeType {
    public:
        virtual const char *name() const;
    };

    template <typename T> class remove_all_pointers{
    public:
        typedef T type;
    };
    template <typename T> class remove_all_pointers<T*>{
    public:
        typedef typename remove_all_pointers<T>::type type;
    };
    
    // this is the function we use to get runtime types from types
    template<class T> const ::x10aux::RuntimeType* getRTT() {
        return remove_all_pointers<T>::type::getRTT();
    }
    // specializations of getRTT template for primitive types
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_boolean>() {
        if (!::x10aux::RuntimeType::BooleanType.isInitialized) {
            ::x10aux::RuntimeType::initBooleanType();
        }
        return &::x10aux::RuntimeType::BooleanType;
    }
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_byte>() {
        if (!::x10aux::RuntimeType::ByteType.isInitialized) {
            ::x10aux::RuntimeType::initByteType();
        }
        return &::x10aux::RuntimeType::ByteType;
    }
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_char>() {
        if (!::x10aux::RuntimeType::CharType.isInitialized) {
            ::x10aux::RuntimeType::initCharType();
        }
        return &::x10aux::RuntimeType::CharType;
    }
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_short>() {
        if (!::x10aux::RuntimeType::ShortType.isInitialized) {
            ::x10aux::RuntimeType::initShortType();
        }
        return &::x10aux::RuntimeType::ShortType;
    }
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_int>() {
        if (!::x10aux::RuntimeType::IntType.isInitialized) {
            ::x10aux::RuntimeType::initIntType();
        }
        return &::x10aux::RuntimeType::IntType;
    }
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_float>() {
        if (!::x10aux::RuntimeType::FloatType.isInitialized) {
            ::x10aux::RuntimeType::initFloatType();
        }
        return &::x10aux::RuntimeType::FloatType;
    }
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_long>() {
        if (!::x10aux::RuntimeType::LongType.isInitialized) {
            ::x10aux::RuntimeType::initLongType();
        }
        return &::x10aux::RuntimeType::LongType;
    }
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_double>() {
        if (!::x10aux::RuntimeType::DoubleType.isInitialized) {
            ::x10aux::RuntimeType::initDoubleType();
        }
        return &::x10aux::RuntimeType::DoubleType;
    }
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_complex >() {
        if (!::x10aux::RuntimeType::ComplexType.isInitialized) {
            ::x10aux::RuntimeType::initComplexType();
        }
        return &::x10aux::RuntimeType::ComplexType;
    }
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_ubyte>() {
        if (!::x10aux::RuntimeType::UByteType.isInitialized) {
            ::x10aux::RuntimeType::initUByteType();
        }
        return &::x10aux::RuntimeType::UByteType;
    }
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_ushort>() {
        if (!::x10aux::RuntimeType::UShortType.isInitialized) {
            ::x10aux::RuntimeType::initUShortType();
        }
        return &::x10aux::RuntimeType::UShortType;
    }
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_uint>() {
        if (!::x10aux::RuntimeType::UIntType.isInitialized) {
            ::x10aux::RuntimeType::initUIntType();
        }
        return &::x10aux::RuntimeType::UIntType;
    }
    template<> inline const ::x10aux::RuntimeType *getRTT<x10_ulong>() {
        if (!::x10aux::RuntimeType::ULongType.isInitialized) {
            ::x10aux::RuntimeType::initULongType();
        }
        return &::x10aux::RuntimeType::ULongType;
    }

    // This is different to getRTT because it distinguishes between T and T*
    template<class T> struct TypeName { static const char *_() {
        const RuntimeType *t = getRTT<T>();
        if (NULL == t || !t->isInitialized) return "uninitialized RTT";
        assert(NULL != t);
        return t->name();
    } };

    template<class T> const char *typeName() {
        return TypeName<T>::_();
    }

    #define TYPENAME(T) ::x10aux::typeName<T>()
    class BootStrapClosure;
    template<> inline const char *typeName<BootStrapClosure>() { return "BootStrapClosure"; }
    template<> inline const char *typeName<reentrant_lock>() { return "reentrant_lock"; }
    template<> inline const char *typeName<void (*)()>() { return "void (*)()"; }
    template<> inline const char *typeName<void*>() { return "void *"; }
    template<> inline const char *typeName<const void*>() { return "const void *"; }
    template<> inline const char *typeName<volatile void*>() { return "volatile void *"; }
    template<> inline const char *typeName<char>() { return "char"; }
    template<> inline const char *typeName<const RuntimeType*>() { return "const RuntimeType *"; }
    template<> inline const char *typeName<RuntimeType::Variance>() { return "Variance"; }
    template<> inline const char *typeName< ::x10::lang::Reference>() { return "Reference"; }
    template<> inline const char *typeName< ::x10::lang::NullType>() { return "Null"; }
#ifndef NO_IOSTREAM
    template<> inline const char *typeName<std::stringstream>() { return "std::stringstream"; }
#endif
    
    template<class T, class S> struct Instanceof { static x10_boolean _(S v) {
        // NOTE: This is only correct because X10 doesn't allow subtyping of structs.
        //       If a future version of X10 allows struct subtyping, then we would
        //       have to have template specializations for all of the C primitive types
        //       and call a method on non-C primitive structs (which would then have to have
        //       C++-level vtables or pointers to RuntimeType*) to get the runtime RTT object for v.
        return ::x10aux::getRTT<S>()->subtypeOf(::x10aux::getRTT<T>());
    } };
    template<class T> struct Instanceof<T, T> { static x10_boolean _(T v) {
        return true;
    } };
    template<class T, class S> struct Instanceof<T, S*> {
        static x10_boolean _(S* v) {
            ::x10::lang::Reference* vAsRef = reinterpret_cast< ::x10::lang::Reference*>(v);
            return ::x10aux::getRTT<T>()->instanceOf(vAsRef);
        }
    };
    // Have to specialize again to disambiguate
    template<class T> struct Instanceof<T*, T*> {
        static x10_boolean _(T* v) {
            ::x10::lang::Reference* vAsRef = reinterpret_cast< ::x10::lang::Reference*>(v);
            return ::x10aux::getRTT<T>()->instanceOf(vAsRef);
        }
    };

    template<class T, class S> inline x10_boolean instanceof(S v) {
        return ::x10aux::Instanceof<T, S>::_(v);
    }

    template<class T, class S> struct ConcreteInstanceof { static x10_boolean _(S v) {
        return false;
    } };
    template<class T> struct ConcreteInstanceof<T, T> { static x10_boolean _(T v) {
        return true;
    } };
    template<class T, class S> struct ConcreteInstanceof<T, S*> {
        static x10_boolean _(S* v) {
            ::x10::lang::Reference* vAsRef = reinterpret_cast< ::x10::lang::Reference*>(v);
            return ::x10aux::getRTT<T>()->concreteInstanceOf(vAsRef);
        }
    };
    // Have to specialize again to disambiguate
    template<class T> struct ConcreteInstanceof<T*, T*> {
        static x10_boolean _(T* v) {
            ::x10::lang::Reference* vAsRef = reinterpret_cast< ::x10::lang::Reference*>(v);
            return ::x10aux::getRTT<T>()->concreteInstanceOf(vAsRef);
        }
    };

    template<class T, class S> inline x10_boolean concrete_instanceof(S v) {
        return ::x10aux::ConcreteInstanceof<T, S>::_(v);
    }

    template<class T1,class T2> inline x10_boolean subtypeof() {
        return ::x10aux::getRTT<T1>()->subtypeOf(::x10aux::getRTT<T2>());
    }

    template<class T1,class T2> inline x10_boolean sametype() {
        return ::x10aux::getRTT<T1>()->equals(::x10aux::getRTT<T2>());
    }

    template<class T> inline x10_boolean haszero() {
        return ::x10aux::getRTT<T>()->hasZero();
    }

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
