#ifndef X10AUX_RTT_H
#define X10AUX_RTT_H

#define INSTANCEOF(v,T) (x10aux::getRTT< T >()->instanceOf(v))
#define CONCRETE_INSTANCEOF(v,T) (x10aux::getRTT< T >()->concreteInstanceOf(v))

#define DEFINE_RTT(T) DEFINE_SPECIAL_RTT(T::RTT)
#define DEFINE_SPECIAL_RTT(T) T * const T::it = new T()


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

    template<class T> struct RTT_WRAP { static RuntimeType *_() {
        RuntimeType *it = T::RTT::it;
        if (!it->initialized()) {
            it->init();
        }
        return it;
    } };

    template<class T> struct RTT_WRAP<ref<T> > { static RuntimeType *_() {
        RuntimeType *it = T::RTT::it;
        if (!it->initialized()) {
            it->init();
        }
        return it;
    } };

    // this is the function we use to get runtime types from types
    template<class T> RuntimeType *getRTT() {
        return RTT_WRAP<T>::_();
    }


    class IntType : public RuntimeType {

        public:

        static IntType * const it;

        virtual void init();

        virtual ~IntType() { }

        virtual std::string name () const { return "x10.lang.Int"; }

    };  
    template<> struct RTT_WRAP<x10_int> { static RuntimeType *_() {
        return IntType::it;
    } };



    class ShortType : public RuntimeType {

        public:

        static ShortType * const it;

        virtual void init();

        virtual ~ShortType() { }

        virtual std::string name () const { return "x10.lang.Short"; }

    };  
    template<> struct RTT_WRAP<x10_short> { static RuntimeType *_() {
        return ShortType::it;
    } };



    class CharType : public RuntimeType {

        public:

        static CharType * const it;

        virtual void init();

        virtual ~CharType() { }

        virtual std::string name () const { return "x10.lang.Char"; }

    };  
    template<> struct RTT_WRAP<x10_char> { static RuntimeType *_() {
        return CharType::it;
    } };




}

#endif
