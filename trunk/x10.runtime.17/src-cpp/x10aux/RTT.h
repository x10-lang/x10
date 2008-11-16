#ifndef X10AUX_RTT_H
#define X10AUX_RTT_H

#define INSTANCEOF(v,T) (x10aux::getRTT< T >()->instanceOf(v))


#include <string>

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10 {
    namespace lang {
        class Object;
    }
}

namespace x10aux {

    class RuntimeType {

        public:

        virtual ~RuntimeType() { }

        virtual std::string name () const = 0;

        virtual bool subtypeOf (const RuntimeType * const other) const {
            //TODO: walk hierarchy
            return equals(other);
        }

        virtual bool instanceOf (
            const x10aux::ref<x10::lang::Object> &other) const;

        virtual bool concreteInstanceOf (
            const x10aux::ref<x10::lang::Object> &other) const;

        virtual bool equals (const RuntimeType * const other) const {
            if (other==this) return true;
            return false;
        }
            
    };

    template<class T> struct RTT_WRAP { static const RuntimeType *_() {
        return T::RTT::it;
    } };

    template<class T> struct RTT_WRAP<ref<T> > { static const RuntimeType *_() {
        return T::RTT::it;
    } };


    class IntType : public RuntimeType {

        public:

        static const RuntimeType * const it;

        virtual ~IntType() { }

        virtual std::string name () const { return "x10.lang.Int"; }

    };  
    template<> struct RTT_WRAP<x10_int> { static const RuntimeType *_() {
        return IntType::it;
    } };



    class ShortType : public RuntimeType {

        public:

        static const RuntimeType * const it;

        virtual ~ShortType() { }

        virtual std::string name () const { return "x10.lang.Short"; }

    };  
    template<> struct RTT_WRAP<x10_short> { static const RuntimeType *_() {
        return ShortType::it;
    } };



    class CharType : public RuntimeType {

        public:

        static const RuntimeType * const it;

        virtual ~CharType() { }

        virtual std::string name () const { return "x10.lang.Char"; }

    };  
    template<> struct RTT_WRAP<x10_char> { static const RuntimeType *_() {
        return CharType::it;
    } };



    // this is the function we use to get runtime types from types
    template<class T> const RuntimeType *getRTT() {
        return RTT_WRAP<T>::_();
    }

}

#endif
