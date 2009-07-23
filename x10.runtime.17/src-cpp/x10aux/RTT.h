#ifndef X10AUX_RTT_H
#define X10AUX_RTT_H

#include <x10aux/config.h>
//#include <x10aux/alloc.h>

#include <pthread.h>

/* Macro to use in class declaration for boilerplate RTT junk */
#define RTT_H_DECLS \
    static x10aux::RuntimeType rtt; \
    static const x10aux::RuntimeType* getRTT() { if (-1==rtt.parentsc) _initRTT(); return &rtt; } \
    static void _initRTT(); \
    virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

#define RTT_H_DECLS_CLASS \
    static x10aux::RuntimeType rtt; \
    static const x10aux::RuntimeType* getRTT() { if (-1==rtt.parentsc) _initRTT(); return &rtt; } \
    static void _initRTT(); \
    virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

#define RTT_H_DECLS_INTERFACE \
    static x10aux::RuntimeType rtt; \
    static const x10aux::RuntimeType* getRTT() { if (-1==rtt.parentsc) _initRTT(); return &rtt; } \
    static void _initRTT(); \

#define RTT_CC_DECLS1(TYPE,NAME,P1) \
    x10aux::RuntimeType TYPE::rtt; \
    void TYPE::_initRTT() { rtt.parentsc = -2; rtt.init(NAME, 1, P1::getRTT()); }

namespace x10 {
    namespace lang {
        class Object;
    }
}

namespace x10aux {

    template<class T> class ref;

    class RuntimeType {
    private:
        static pthread_mutex_t installLock;
        static pthread_mutexattr_t installLockAttr;
        
    public:
        /*
         * RTT objects for all builtin primitive types.
         * These are created by the bootstrap method
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


        /**
         * RTT object for x10::lang::Object
         * Created by the bootstrap method because it
         * is needed as the parent object for the primitive RTT's
         */
        static RuntimeType ObjectType;
        
    public:
        int parentsc;
        const RuntimeType **parents;
        const char* typeName;

        RuntimeType () : parentsc(-1) { }
        
        void init(const char* n, int pc, ...);

        const char *name() const { return typeName; }

        bool subtypeOf(const RuntimeType * const other) const;

        // use "const ref<t> &" here to break circular dependency
        bool instanceOf(const x10aux::ref<x10::lang::Object> &other) const;

        // use "const ref<t> &" here to break circular dependency
        bool concreteInstanceOf(const x10aux::ref<x10::lang::Object> &other) const;

        bool equals(const RuntimeType * const other) const {
            return other == this;
        }

        static void bootstrap();
    };


    // this is the function we use to get runtime types from types
    template<class T> const x10aux::RuntimeType* getRTT() {
        return T::getRTT();
    }
    // specializations of getRTT template for primitive types
	template<> inline const x10aux::RuntimeType *getRTT<x10_boolean>() { return &x10aux::RuntimeType::BooleanType; }
	template<> inline const x10aux::RuntimeType *getRTT<x10_byte>() { return &x10aux::RuntimeType::ByteType; }
	template<> inline const x10aux::RuntimeType *getRTT<x10_short>() { return &x10aux::RuntimeType::ShortType; }
	template<> inline const x10aux::RuntimeType *getRTT<x10_char>() { return &x10aux::RuntimeType::CharType; }
	template<> inline const x10aux::RuntimeType *getRTT<x10_int>() { return &x10aux::RuntimeType::IntType; }
	template<> inline const x10aux::RuntimeType *getRTT<x10_float>() { return &x10aux::RuntimeType::FloatType; }
	template<> inline const x10aux::RuntimeType *getRTT<x10_long>() { return &x10aux::RuntimeType::LongType; }
	template<> inline const x10aux::RuntimeType *getRTT<x10_double>() { return &x10aux::RuntimeType::DoubleType; }
	template<> inline const x10aux::RuntimeType *getRTT<x10_ubyte>() { return &x10aux::RuntimeType::UByteType; }
	template<> inline const x10aux::RuntimeType *getRTT<x10_ushort>() { return &x10aux::RuntimeType::UShortType; }
	template<> inline const x10aux::RuntimeType *getRTT<x10_uint>() { return &x10aux::RuntimeType::UIntType; }
	template<> inline const x10aux::RuntimeType *getRTT<x10_ulong>() { return &x10aux::RuntimeType::ULongType; }

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
    template<> inline const char *typeName<const void*>() { return "const void *"; }
    template<> inline const char *typeName<char>() { return "char"; }
    template<> inline const char *typeName<const RuntimeType*>() { return "const RuntimeType *"; }

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
