#ifndef X10AUX_RTT_H
#define X10AUX_RTT_H

#include <x10aux/config.h>

/* Macro to use in class declaration for boilerplate RTT junk */
#define RTT_H_DECLS_CLASS \
    static x10aux::RuntimeType rtt;                                     \
    static const x10aux::RuntimeType* getRTT() { if (NULL == rtt.canonical) _initRTT(); return &rtt; } \
    static void _initRTT(); \
    virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

#define RTT_H_DECLS_STRUCT \
    static x10aux::RuntimeType rtt;                                     \
    static const x10aux::RuntimeType* getRTT() { if (NULL == rtt.canonical) _initRTT(); return &rtt; } \
    static void _initRTT(); \

#define RTT_H_DECLS_INTERFACE \
    static x10aux::RuntimeType rtt; \
    static const x10aux::RuntimeType* getRTT() { if (NULL == rtt.canonical) _initRTT(); return &rtt; } \
    static void _initRTT();

#define RTT_CC_DECLS1(TYPE,NAME,P1)                             \
    x10aux::RuntimeType TYPE::rtt;                              \
    void TYPE::_initRTT() {                                     \
        rtt.canonical = &(TYPE::rtt);                           \
        const x10aux::RuntimeType* parents[1] = {P1::getRTT()}; \
        rtt.init(&rtt, NAME, 1, parents, 0, NULL, NULL);        \
    }

namespace x10 {
    namespace lang {
        class Object;
    }
}

namespace x10aux {

    template<class T> class ref;

    class RuntimeType {
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
        static RuntimeType UByteType;
        static RuntimeType UShortType;
        static RuntimeType UIntType;
        static RuntimeType ULongType;

        enum Variance { covariant, contravariant, invariant };
        
    public:
        const RuntimeType *canonical;
        int parentsc;
        int paramsc;
        const RuntimeType **parents;
        const RuntimeType **params;
        Variance *variances;
        const char* typeName;
        
        void init(const RuntimeType* canonical_, const char* typeName_, int parsentsc_, const RuntimeType** parents_,
                  int paramsc_, const RuntimeType** params_, Variance* variances_);

        // TODO: If we constructed the names lazily instead of passing the fully
        //       constructed char* to init, then I think we could completely avoid this problem.
        const char *name() const { return NULL == typeName ? "CYCLIC RTT" : typeName; }

        bool subtypeOf(const RuntimeType * const other) const;

        // use "const ref<t> &" here to break circular dependency
        bool instanceOf(const x10aux::ref<x10::lang::Object> &other) const;

        // use "const ref<t> &" here to break circular dependency
        bool concreteInstanceOf(const x10aux::ref<x10::lang::Object> &other) const;

        bool equals(const RuntimeType * const other) const {
            return other == this;
        }

        static void initBooleanType();
        static void initByteType();
        static void initCharType();
        static void initShortType();
        static void initIntType();
        static void initFloatType();
        static void initLongType();
        static void initDoubleType();
        static void initUByteType();
        static void initUShortType();
        static void initUIntType();
        static void initULongType();
    };


    // this is the function we use to get runtime types from types
    template<class T> const x10aux::RuntimeType* getRTT() {
        return T::getRTT();
    }
    // specializations of getRTT template for primitive types
    template<> inline const x10aux::RuntimeType *getRTT<x10_boolean>() {
        if (NULL == x10aux::RuntimeType::BooleanType.canonical) {
            x10aux::RuntimeType::initBooleanType();
        }
        return &x10aux::RuntimeType::BooleanType;
    }
    template<> inline const x10aux::RuntimeType *getRTT<x10_byte>() {
        if (NULL == x10aux::RuntimeType::ByteType.canonical) {
            x10aux::RuntimeType::initByteType();
        }
        return &x10aux::RuntimeType::ByteType;
    }
    template<> inline const x10aux::RuntimeType *getRTT<x10_char>() {
        if (NULL == x10aux::RuntimeType::CharType.canonical) {
            x10aux::RuntimeType::initCharType();
        }
        return &x10aux::RuntimeType::CharType;
    }
    template<> inline const x10aux::RuntimeType *getRTT<x10_short>() {
        if (NULL == x10aux::RuntimeType::ShortType.canonical) {
            x10aux::RuntimeType::initShortType();
        }
        return &x10aux::RuntimeType::ShortType;
    }
    template<> inline const x10aux::RuntimeType *getRTT<x10_int>() {
        if (NULL == x10aux::RuntimeType::IntType.canonical) {
            x10aux::RuntimeType::initIntType();
        }
        return &x10aux::RuntimeType::IntType;
    }
    template<> inline const x10aux::RuntimeType *getRTT<x10_float>() {
        if (NULL == x10aux::RuntimeType::FloatType.canonical) {
            x10aux::RuntimeType::initFloatType();
        }
        return &x10aux::RuntimeType::FloatType;
    }
    template<> inline const x10aux::RuntimeType *getRTT<x10_long>() {
        if (NULL == x10aux::RuntimeType::LongType.canonical) {
            x10aux::RuntimeType::initLongType();
        }
        return &x10aux::RuntimeType::LongType;
    }
    template<> inline const x10aux::RuntimeType *getRTT<x10_double>() {
        if (NULL == x10aux::RuntimeType::DoubleType.canonical) {
            x10aux::RuntimeType::initDoubleType();
        }
        return &x10aux::RuntimeType::DoubleType;
    }
    template<> inline const x10aux::RuntimeType *getRTT<x10_ubyte>() {
        if (NULL == x10aux::RuntimeType::UByteType.canonical) {
            x10aux::RuntimeType::initUByteType();
        }
        return &x10aux::RuntimeType::UByteType;
    }
    template<> inline const x10aux::RuntimeType *getRTT<x10_ushort>() {
        if (NULL == x10aux::RuntimeType::UShortType.canonical) {
            x10aux::RuntimeType::initUShortType();
        }
        return &x10aux::RuntimeType::UShortType;
    }
    template<> inline const x10aux::RuntimeType *getRTT<x10_uint>() {
        if (NULL == x10aux::RuntimeType::UIntType.canonical) {
            x10aux::RuntimeType::initUIntType();
        }
        return &x10aux::RuntimeType::UIntType;
    }
    template<> inline const x10aux::RuntimeType *getRTT<x10_ulong>() {
        if (NULL == x10aux::RuntimeType::ULongType.canonical) {
            x10aux::RuntimeType::initULongType();
        }
        return &x10aux::RuntimeType::ULongType;
    }

    // This is different to getRTT because it distinguishes between T and ref<T>
    template<class T> struct TypeName { static const char *_() {
        const RuntimeType *t = getRTT<T>();
        if (t == NULL) return "Uninitialized RTT";
        return t->name();
    } };

/*
    template<class T> struct TypeName<ref<T> > { static const char *_() {
        const RuntimeType *t = getRTT<T>();
        if (t == NULL) return "Uninitialized RTT";
        static const char *with_star = alloc_printf("%s*",t->name());
        return with_star;
    } };
*/

    template<class T> const char *typeName() {
        return TypeName<T>::_();
    }

    #define TYPENAME(T) x10aux::typeName<T>()
    class InitDispatcher;
    template<> inline const char *typeName<InitDispatcher>() { return "InitDispatcher"; }
    class remote_ref;
    template<> inline const char *typeName<remote_ref>() { return "remote_ref"; }
    template<> inline const char *typeName<void (*)()>() { return "void (*)()"; }
    template<> inline const char *typeName<void*>() { return "void *"; }
    template<> inline const char *typeName<const void*>() { return "const void *"; }
    template<> inline const char *typeName<volatile void*>() { return "volatile void *"; }
    template<> inline const char *typeName<char>() { return "char"; }
    template<> inline const char *typeName<const RuntimeType*>() { return "const RuntimeType *"; }
    template<> inline const char *typeName<RuntimeType::Variance>() { return "Variance"; }

    template<class T, class S> struct Instanceof { static x10_boolean _(S v) {
        return false;
    } };
    template<class T> struct Instanceof<T, T> { static x10_boolean _(T v) {
        return true;
    } };
    template<class T, class S> struct Instanceof<T, x10aux::ref<S> > {
        static x10_boolean _(x10aux::ref<S> v) {
            return x10aux::getRTT<T>()->instanceOf(v);
        }
    };
    // Have to specialize again to disambiguate
    template<class T> struct Instanceof<x10aux::ref<T>, x10aux::ref<T> > {
        static x10_boolean _(x10aux::ref<T> v) {
            return x10aux::getRTT<x10aux::ref<T> >()->instanceOf(v);
        }
    };

    template<class T, class S> inline x10_boolean instanceof(S v) {
        return x10aux::Instanceof<T, S>::_(v);
    }

    template<class T, class S> struct ConcreteInstanceof { static x10_boolean _(S v) {
        return false;
    } };
    template<class T> struct ConcreteInstanceof<T, T> { static x10_boolean _(T v) {
        return true;
    } };
    template<class T, class S> struct ConcreteInstanceof<T, x10aux::ref<S> > {
        static x10_boolean _(x10aux::ref<S> v) {
            return x10aux::getRTT<T>()->concreteInstanceOf(v);
        }
    };
    // Have to specialize again to disambiguate
    template<class T> struct ConcreteInstanceof<x10aux::ref<T>, x10aux::ref<T> > {
        static x10_boolean _(x10aux::ref<T> v) {
            return x10aux::getRTT<x10aux::ref<T> >()->concreteInstanceOf(v);
        }
    };

    template<class T, class S> inline x10_boolean concrete_instanceof(S v) {
        return x10aux::ConcreteInstanceof<T, S>::_(v);
    }

    template<class T1,class T2> inline x10_boolean subtypeof() {
        return x10aux::getRTT<T1>()->subtypeOf(x10aux::getRTT<T2>());
    }

    template<class T1,class T2> inline x10_boolean sametype() {
        return x10aux::getRTT<T1>()->equals(x10aux::getRTT<T2>());
    }

}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
