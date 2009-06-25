#ifndef X10_LANG_RAIL_H
#define X10_LANG_RAIL_H


#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>
#include <x10aux/rail_utils.h>

#include <x10/lang/Ref.h>
#include <x10/lang/Settable.h>
#include <x10/lang/ValRail.h>


namespace x10 {

    namespace lang {

        extern const x10aux::RuntimeType* _initRTTHelper_Rail(const x10aux::RuntimeType **location, const x10aux::RuntimeType *element,
                                                              const x10aux::RuntimeType *p1, const x10aux::RuntimeType *p2);
        extern const x10aux::RuntimeType* _initRTTHelper_RailIterator(const x10aux::RuntimeType **location, const x10aux::RuntimeType *element,
                                                                      const x10aux::RuntimeType *p1);
        
        template<class P1, class R> class Fun_0_1;

        template<class T> class Rail : public Ref,
                                       public virtual x10::lang::Settable<x10_int,T>,
                                       public x10aux::AnyRail<T> {
            public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT(); 
            virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

            private:

            Rail(const Rail<T>& arr); // disabled

            public:

            Rail(x10_int length_, T* storage) : x10aux::AnyRail<T>(length_, storage) { }

            GPUSAFE virtual T set(T v, x10_int index) { 
                return (*this)[index] = v; 
            } 

            class Iterator : public Ref, public virtual x10::lang::Iterator<T> {

                protected:

                x10_int i;
                x10aux::ref<Rail<T> > rail;

                public:
                static const x10aux::RuntimeType* rtt;
                static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
                static const x10aux::RuntimeType* _initRTT();
                virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

                Iterator (x10aux::ref<Rail> rail_)
                        : i(0), rail(rail_) { }

                virtual x10_boolean hasNext() {
                    return i < rail->FMGL(length);
                }
             
                virtual T next() {
                    return (*rail)[i++];
                }

                virtual x10_int hashCode() { return 0; }

                virtual x10_boolean equals(x10aux::ref<Ref> other) {
                    if (!x10aux::concrete_instanceof<Iterator>(other)) return false;
                    x10aux::ref<Iterator> other_i = other;
                    if (other_i->rail != rail) return false;
                    if (other_i->i != i) return false;
                    return true;
                }   

                virtual x10_boolean equals(x10aux::ref<Value> other) {
                    return this->Ref::equals(other);
                }

                virtual x10aux::ref<String> toString() {
                    return new (x10aux::alloc<String>()) String();
                }

            };  

            virtual x10aux::ref<x10::lang::Iterator<T> > iterator() {
                return new (x10aux::alloc<Iterator>()) Iterator (this);
            }   

            static x10aux::ref<Rail<T> > make(x10_int length);
            static x10aux::ref<Rail<T> > make(x10_int length,
                                              x10aux::ref<Fun_0_1<x10_int,T> > init);
            static x10aux::ref<Rail<T> > make(x10aux::ref<ValRail<T> > other);

            virtual x10aux::ref<String> toString() {
                return x10aux::AnyRail<T>::toString();
            }

        };

        template<class T> const x10aux::RuntimeType* Rail<T>::rtt = NULL;
        template<class T> const x10aux::RuntimeType* Rail<T>::Iterator::rtt = NULL;

        template<class T> const x10aux::RuntimeType* Rail<T>::_initRTT() {
            return x10::lang::_initRTTHelper_Rail(&rtt, x10aux::getRTT<T>(), x10aux::getRTT<Settable<x10_int,T> >(),
                                                  x10aux::getRTT<Iterable<T> >());
        }

        template<class T> const x10aux::RuntimeType* Rail<T>::Iterator::_initRTT() {
            return x10::lang::_initRTTHelper_RailIterator(&rtt, x10aux::getRTT<T>(), x10aux::getRTT<x10::lang::Iterator<T> >());
        }        

        template <class T> x10aux::ref<Rail<T> > Rail<T>::make(x10_int length) {
            x10aux::ref<Rail<T> > rail = x10aux::alloc_rail<T,Rail<T> >(length);
            for (x10_int i=0 ; i<length ; ++i) {
                // Initialise to zero, which should work for
                // numeric types and x10aux::ref<T> which I think
                // covers everything.
                (*rail)[i] = 0;
            }
            return rail;
        }

        template <class T> x10aux::ref<Rail<T> > Rail<T>::make(x10_int length,
                                                               x10aux::ref<Fun_0_1<x10_int,T> > init ) {
            x10aux::ref<Rail<T> > rail = x10aux::alloc_rail<T,Rail<T> >(length);
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = init->apply(i);
            }
            return rail;
        }

        template <class T> x10aux::ref<Rail<T> > Rail<T>::make(x10aux::ref<ValRail<T> > other) {
            x10_int length = other->FMGL(length);
            x10aux::ref<Rail<T> > rail = x10aux::alloc_rail<T,Rail<T> >(length);
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = (*other)[i];
            }
            return rail;
        }
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
