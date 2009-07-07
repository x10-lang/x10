#ifndef X10_LANG_RAIL_H
#define X10_LANG_RAIL_H


#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>
#include <x10aux/rail_utils.h>

#include <x10/lang/Ref.h>
#include <x10/lang/Settable.h>
#include <x10/lang/Iterable.h>
#include <x10/lang/ValRail.h>


namespace x10 {

    namespace lang {

        extern const x10aux::RuntimeType* _initRTTHelper_Rail(const x10aux::RuntimeType **location, const x10aux::RuntimeType *element,
                                                              const x10aux::RuntimeType *p1, const x10aux::RuntimeType *p2);
        extern const x10aux::RuntimeType* _initRTTHelper_RailIterator(const x10aux::RuntimeType **location, const x10aux::RuntimeType *element,
                                                                      const x10aux::RuntimeType *p1);
        
        template<class P1, class R> class Fun_0_1;

        template<class T> class Rail : public Ref {
            public:
            static const x10aux::RuntimeType* rtt;
            static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
            static const x10aux::RuntimeType* _initRTT(); 
            virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

            static x10aux::itable_entry _itables[3];
            virtual x10aux::itable_entry* _getITables() { return _itables; }

            static x10aux::ref<x10::lang::Iterator<T> >_itable_thunk_iterable(x10aux::ref<x10::lang::Iterable<T> > this_) {
                x10aux::ref<x10::lang::Rail<T> > tmp = this_;
                return tmp->iterator();
            }

            static T _itable_thunk_settable(x10aux::ref<x10::lang::Settable<x10_int, T> > this_, T arg0, x10_int arg1) {
                x10aux::ref<x10::lang::Rail<T> > tmp = this_;
                return tmp->set(arg0, arg1);
            }
            
            private:

            Rail(const Rail<T>& arr); // disabled

            public:
            // 32 bit array indexes
            const x10_int FMGL(length);

            // The Rail's data.
            // As a locality optimization, we are going to allocate all of the storage for the
            // Rail object and its data array contiguously (ie, in a single allocate call),
            // but to avoid making assumptions about the C++ object model, we will always
            // access it via this pointer instead of using the data[1] "struct hack."
            // This may cost us an extra load instruction (but no extra cache misses).
            // By declaring the pointer const, we should enable the C++ compiler to be reasonably
            // effective at hoisting this extra load out of loop nests.
            T* const _data;
            
            Rail(x10_int length_, T* storage) : FMGL(length)(length_),  _data(storage) { }

            GPUSAFE virtual T set(T v, x10_int index) { 
                return (*this)[index] = v; 
            } 

            GPUSAFE T apply(x10_int index) {
                return operator[](index);
            }   

            GPUSAFE T& operator[](x10_int index) {
                x10aux::checkRailBounds(index, FMGL(length));
                return _data[index];
            }
      
            T* raw() { return _data; }

            
            class RailIterator : public Ref, public virtual x10::lang::Iterator<T> {

                protected:

                x10_int i;
                x10aux::ref<Rail<T> > rail;

                public:
                static const x10aux::RuntimeType* rtt;
                static const x10aux::RuntimeType* getRTT() { return NULL == rtt ? _initRTT() : rtt; }
                static const x10aux::RuntimeType* _initRTT();
                virtual const x10aux::RuntimeType *_type() const { return getRTT(); }

                RailIterator (x10aux::ref<Rail> rail_)
                        : i(0), rail(rail_) { }

                virtual x10_boolean hasNext() {
                    return i < rail->FMGL(length);
                }
             
                virtual T next() {
                    return (*rail)[i++];
                }

                virtual x10_int hashCode() { return 0; }

                virtual x10_boolean equals(x10aux::ref<Ref> other) {
                    if (!x10aux::concrete_instanceof<RailIterator>(other)) return false;
                    x10aux::ref<RailIterator> other_i = other;
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
                return new (x10aux::alloc<RailIterator>()) RailIterator (this);
            }   

            static x10aux::ref<Rail<T> > make(x10_int length);
            static x10aux::ref<Rail<T> > make(x10_int length,
                                              x10aux::ref<Fun_0_1<x10_int,T> > init);
            static x10aux::ref<Rail<T> > make(x10aux::ref<ValRail<T> > other);

            virtual x10aux::ref<x10::lang::String> toString() { return x10aux::railToString<T,Rail<T> >(this); }
        };

        template<class T> const x10aux::RuntimeType* Rail<T>::rtt = NULL;
        template<class T> const x10aux::RuntimeType* Rail<T>::RailIterator::rtt = NULL;

        template<class T> const x10aux::RuntimeType* Rail<T>::_initRTT() {
            return x10::lang::_initRTTHelper_Rail(&rtt, x10aux::getRTT<T>(), x10aux::getRTT<Settable<x10_int,T> >(),
                                                  x10aux::getRTT<Iterable<T> >());
        }

        template<class T> const x10aux::RuntimeType* Rail<T>::RailIterator::_initRTT() {
            return x10::lang::_initRTTHelper_RailIterator(&rtt, x10aux::getRTT<T>(), x10aux::getRTT<x10::lang::Iterator<T> >());
        }        


        template <class T> x10aux::itable_entry x10::lang::Rail<T>::_itables[3] = {
            x10aux::itable_entry(&x10::lang::Iterable<T>::rtt, new typename x10::lang::Iterable<T>::itable(&x10::lang::Rail<T>::_itable_thunk_iterable)),
            x10aux::itable_entry(&x10::lang::Settable<x10_int, T>::rtt, new typename x10::lang::Settable<x10_int, T>::itable(&x10::lang::Rail<T>::_itable_thunk_settable)),
            x10aux::itable_entry(NULL, NULL)
        };

        
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
            typename Fun_0_1<x10_int,T>::itable *it = x10aux::findITable<Fun_0_1<x10_int,T> >(init);
            for (x10_int i=0 ; i<length ; ++i) {
                (*rail)[i] = it->apply(init, i);
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
