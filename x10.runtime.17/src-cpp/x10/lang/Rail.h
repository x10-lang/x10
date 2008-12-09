#ifndef X10_LANG_RAIL_H
#define X10_LANG_RAIL_H

#include <sstream>


#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>
#include <x10aux/rail_utils.h>

#include <x10/lang/Ref.h>
#include <x10/lang/ValRail.h>


namespace x10 {

    namespace lang {

        template<class P1, class R> class Fun_0_1;

        template<class T> class Rail : public Ref, public x10aux::AnyRail<T> {

            public:

            class RTT : public x10aux::RuntimeType {
                public:
                static RTT * const it;

                virtual void init() { initParents(2,x10aux::getRTT<Ref>(),
                                                    x10aux::getRTT<Fun_0_1<x10_int,T> >()); }

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

            class Iterator : public Ref, public virtual x10::lang::Iterator<T> {

                protected:

                x10_int i;
                x10aux::ref<Rail<T> > rail;

                public:

                class RTT : public x10aux::RuntimeType {
                    public:
                    static RTT * const it;

                    virtual void init() {
                       initParents(2,x10aux::getRTT<Ref>(),
                                     x10aux::getRTT<x10::lang::Iterator<T> >());
                    }

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

                Iterator (x10aux::ref<Rail> rail_)
                        : i(0), rail(rail_) { }

                virtual x10_boolean hasNext() {
                    return i < rail->FMGL(length);
                }
             
                virtual T next() {
                    return (*rail)[i++];
                }

                virtual x10_int hashCode() { return 0; }

                virtual x10_boolean equals(x10aux::ref<Object> other) {
                    if (!x10aux::concrete_instanceof<Iterator>(other)) return false;
                    x10aux::ref<Iterator> other_i = other;
                    if (other_i->rail != rail) return false;
                    if (other_i->i != i) return false;
                    return true;
                }   

                virtual x10aux::ref<String> toString() {
                    return new (x10aux::alloc<String>()) String();
                }

            };  

            virtual x10aux::ref<x10::lang::Iterator<T> > iterator() {
                return new (x10aux::alloc<Iterator>()) Iterator (this);
            }   


            static x10aux::ref<Rail<T> > make(x10_int length) {
                x10aux::ref<Rail<T> > rail = x10aux::alloc_rail<T,Rail<T> >(length);
                for (x10_int i=0 ; i<length ; ++i) {
                        // Initialise to zero, which should work for
                        // numeric types and x10aux:;ref<T> which I think
                        // covers everything.
                        (*rail)[i] = 0;
                }
                return rail;
            }

            static x10aux::ref<Rail<T> > make(x10_int length,
                                              x10aux::ref<Fun_0_1<x10_int,T> > init ) {
                x10aux::ref<Rail<T> > rail = x10aux::alloc_rail<T,Rail<T> >(length);
                for (x10_int i=0 ; i<length ; ++i) {
                        (*rail)[i] = init->apply(i);
                }
                return rail;
            }

            static x10aux::ref<Rail<T> > make(x10aux::ref<ValRail<T> > other) {
                x10_int length = other->FMGL(length);
                x10aux::ref<Rail<T> > rail = x10aux::alloc_rail<T,Rail<T> >(length);
                for (x10_int i=0 ; i<length ; ++i) {
                        (*rail)[i] = (*other)[i];
                }
                return rail;
            }

            virtual x10aux::ref<String> toString() {
                return x10aux::AnyRail<T>::toString();
            }

        };

        template<class T> typename Rail<T>::RTT * const Rail<T>::RTT::it =
            new (x10aux::alloc<typename Rail<T>::RTT>()) typename Rail<T>::RTT();

        template<class T> typename Rail<T>::Iterator::RTT * const Rail<T>::Iterator::RTT::it =
            new (x10aux::alloc<typename Rail<T>::Iterator::RTT>())
                typename Rail<T>::Iterator::RTT();

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
