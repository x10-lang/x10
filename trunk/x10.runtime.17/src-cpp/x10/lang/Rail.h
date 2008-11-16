#ifndef X10_LANG_RAIL_H
#define X10_LANG_RAIL_H

#include <sstream>


#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>
#include <x10aux/rail_utils.h>

#include <x10/lang/ValRail.h>


namespace x10 {

    namespace lang {

        template<class T> class Rail : public Value, public x10aux::AnyRail<T> {

            public:

            class RTT : public x10aux::RuntimeType {
                public:
                static const RTT* it;

                RTT() : RuntimeType(1,x10aux::getRTT<Value>()) { }

                virtual std::string name() const {
                    std::stringstream ss;
                    ss<<"x10.lang.Rail["<<x10aux::getRTT<T>()->name()<<"]";
                    return ss.str();
                }
                 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Rail<T> >();
            }

            private:

            Rail(const Rail<T>& arr); // disabled

            public:

            Rail(x10_int length_) : x10aux::AnyRail<T>(length_) { }

            ~Rail() { }

            class Iterator : public x10::lang::Iterator<T> {

                protected:

                x10_int i;
                x10aux::ref<Rail<T> > rail;

                public:

                class RTT : public x10aux::RuntimeType {
                    public:
                    static const RTT* it;

                    RTT()
                     : RuntimeType(1,x10aux::getRTT<x10::lang::Iterator<T> >())
                    { }

                    virtual std::string name() const {
                        std::stringstream ss;
                        ss<<"x10.lang.Rail.Iterator["
                          <<x10aux::getRTT<T>()->name()<<"]";
                        return ss.str();
                    }

                };
                virtual const x10aux::RuntimeType *_type() const {
                    return x10aux::getRTT<Iterator>();
                }   

                Iterator (const x10aux::ref<Rail> &rail_)
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


            static x10aux::ref<Rail<T> > makeVarRail(x10_int length) {
                x10aux::ref<Rail<T> > rail =    
                        x10aux::alloc_rail<T,Rail<T> >(length);
                for (x10_int i=0 ; i<length ; ++i) {
                        // Initialise to zero, which should work for
                        // numeric types and x10aux:;ref<T> which I think
                        // covers everything.
                        (*rail)[i] = 0;
                }
                return rail;
            }

            /* TODO: this needs function types, etc
            static x10aux::ref<Rail<T> > makeVarRail(x10_int length,
                                          const fun_0_1<x10_int,T> &init ) {
                x10aux::ref<Rail<T> > rail =    
                        x10aux::alloc_rail<T,Rail<T> >(length);
                for (x10_int i=0 ; i<length ; ++i) {
                        (*rail)[i] = init->apply(i);
                }
            }
            */

            static x10aux::ref<Rail<T> > makeVarRailFromValRail(
                                    const x10aux::ref<ValRail<T> > &other) {
                x10_int length = other->FMGL(length);
                x10aux::ref<Rail<T> > rail =    
                        x10aux::alloc_rail<T,Rail<T> >(length);
                for (x10_int i=0 ; i<length ; ++i) {
                        (*rail)[i] = (*other)[i];
                }
            }

        };

        template<class T> const typename Rail<T>::RTT *Rail<T>::RTT::it =
            new typename Rail<T>::RTT();

        template<class T> const typename Rail<T>::Iterator::RTT *
            Rail<T>::Iterator::RTT::it = new typename Rail<T>::Iterator::RTT();

    }
}


#endif
