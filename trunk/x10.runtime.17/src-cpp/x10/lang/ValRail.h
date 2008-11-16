#ifndef X10_LANG_VALRAIL_H
#define X10_LANG_VALRAIL_H

#include <sstream>


#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>


#include <x10aux/rail_utils.h>


namespace x10 {

    namespace lang {

        template<class T> class ValRail : public Value,
                                          public x10aux::AnyRail<T> {

            public:

            class RTT : public x10aux::RuntimeType {
                public:
                static const RTT* it;

                RTT() : RuntimeType(1,x10aux::getRTT<Value>()) { }

                virtual std::string name() const {
                    std::stringstream ss;
                    ss << "ValRail[" << x10aux::getRTT<T>()->name() << "]";
                    return ss.str();
                }
                 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<ValRail<T> >();
            }

            private:

            ValRail(const ValRail<T>& arr); // disabled

            public:

            ValRail(x10_int length_) : x10aux::AnyRail<T>(length_) { }

            ~ValRail() { }

            class Iterator : public x10::lang::Iterator<T> {

                protected:

                x10_int i;
                x10aux::ref<ValRail<T> > rail;

                public:

                class RTT : public x10aux::RuntimeType {
                    public:
                    static const RTT* it;

                    virtual std::string name() const {
                        std::stringstream ss;
                        ss<<"x10.lang.ValRail.Iterator["
                          <<x10aux::getRTT<T>()->name()<<"]";
                        return ss.str();
                    }

                };
                virtual const x10aux::RuntimeType *_type() const {
                    return x10aux::getRTT<Iterator>();
                }

                Iterator (const x10aux::ref<ValRail> &rail_)
                        : i(0), rail(rail_) { }

                virtual x10_boolean hasNext() {
                    return i < rail->FMGL(length);
                }

                virtual T next() {
                    return (*rail)[i++];
                }

                virtual x10_int hashCode() { return 0; }

                virtual x10aux::ref<String> toString() {
                    return new (x10aux::alloc<String>()) String();
                }

                virtual x10_boolean equals(const x10aux::ref<Object> &other) {
                    if (!RTT::it->concreteInstanceOf(other)) return false;
                    Iterator &other_i = static_cast<Iterator&>(*other);
                    if (other_i.rail != rail) return false;
                    if (other_i.i != i) return false;
                    return true;
                }
            };

            virtual x10aux::ref<x10::lang::Iterator<T> > iterator() {
                return new Iterator(this);
            }



        };

        template<class T> const typename ValRail<T>::RTT *ValRail<T>::RTT::it =
            new typename ValRail<T>::RTT();

        template<class T> const typename ValRail<T>::Iterator::RTT *
            ValRail<T>::Iterator::RTT::it =
                new typename ValRail<T>::Iterator::RTT();

    }
}


#endif
