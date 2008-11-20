#ifndef X10AUX_RTT_H
#define X10AUX_RTT_H

#define INSTANCEOF(v,T) \
    (x10aux::getRTT< T >()->instanceOf(v))

#define CONCRETE_INSTANCEOF(v,T) \
    (x10aux::getRTT< T >()->concreteInstanceOf(v))

#define SUBTYPEOF(T1,T2) \
    (x10aux::getRTT< T1 >()->subTypeOf(x10aux::getRTT< T2 >()))


#define DEFINE_RTT(T) \
    DEFINE_SPECIAL_RTT(T::RTT)

#define DEFINE_SPECIAL_RTT(T) \
    T * const T::it = new T()

// [DC] can't do RTT macros for generic classes because they would have to be
// variadic to handle the varying number of type parameters

#include <string>
#include <stdarg.h>

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/alloc.h>

namespace x10 {
    namespace lang {
        class Object;
    }
}

namespace x10aux {

    class RuntimeType {

        public:

        int parentsc;
        const RuntimeType ** parents;

        RuntimeType() : parentsc(-1), parents(0) { }

        bool initialized() { return parentsc>=0; }

        virtual void init() = 0;

        void initParents(int parentsc_, ...) {
            parentsc = parentsc_;
            parents = x10aux::alloc<const RuntimeType*>
                      (parentsc * sizeof(const RuntimeType*));
            _RTT_("Initialising RTT: "<<name());
            va_list parentsv;
            va_start(parentsv, parentsc_);
            for (int i=0 ; i<parentsc ; ++i)
                parents[i] = va_arg(parentsv,const RuntimeType*);
            va_end(parentsv);
        }

        virtual ~RuntimeType() {
            x10aux::dealloc(parents);
        }

        virtual std::string name () const = 0;

        virtual bool subtypeOf (const RuntimeType * const other) const {
            if (equals(other)) return true; // trivial case
            for (int i=0 ; i<parentsc ; ++i) {
                if (parents[i]->subtypeOf(other)) return true;
            }
            return false;
        }

        virtual bool instanceOf (
            x10aux::ref<x10::lang::Object> other) const;

        virtual bool concreteInstanceOf (
            x10aux::ref<x10::lang::Object> other) const;

        virtual bool equals (const RuntimeType * const other) const {
            if (other==this) return true;
            return false;
        }
            
    };

    template<class T> struct RTT_WRAP { static const RuntimeType *_() {
        RuntimeType *it = T::RTT::it;
        if (!it->initialized()) {
            it->init();
        }
        return it;
    } };

    template<class T> struct RTT_WRAP<ref<T> > { static const RuntimeType *_() {
        return RTT_WRAP<T>::_();
    } };

    // this is the function we use to get runtime types from types
    template<class T> const RuntimeType *getRTT() {
        return RTT_WRAP<T>::_();
    }

    #define PRIMITIVE_RTT(RTTNAME,PRIMNAME,FQNAME) \
    class RTTNAME : public RuntimeType { \
        public: \
        static RTTNAME * const it; \
        virtual void init(); \
        virtual ~RTTNAME() { } \
        virtual std::string name () const { return FQNAME; } \
    }; \
    template<> const RuntimeType *getRTT<PRIMNAME>();

    PRIMITIVE_RTT(BooleanType,x10_boolean,"x10.lang.Boolean")
    PRIMITIVE_RTT(ByteType,x10_byte,"x10.lang.Byte")
    PRIMITIVE_RTT(CharType,x10_char,"x10.lang.Char")
    PRIMITIVE_RTT(ShortType,x10_short,"x10.lang.Short")
    PRIMITIVE_RTT(IntType,x10_int,"x10.lang.Int")
    PRIMITIVE_RTT(LongType,x10_long,"x10.lang.Long")
    PRIMITIVE_RTT(FloatType,x10_float,"x10.lang.Float")
    PRIMITIVE_RTT(DoubleType,x10_double,"x10.lang.Double")

    #undef PRIMITIVE_RTT



}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
