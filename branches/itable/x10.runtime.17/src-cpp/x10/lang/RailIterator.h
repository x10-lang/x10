#ifndef X10_LANG_RAILITERATOR_H
#define X10_LANG_RAILITERATOR_H

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>
#include <x10aux/rail_utils.h>

#include <x10/lang/Ref.h>
#include <x10/lang/Iterator.h>

namespace x10 {
    namespace lang {
        
        void _initRTTHelper_RailIterator(x10aux::RuntimeType *location,
                                         const x10aux::RuntimeType *element,
                                         const x10aux::RuntimeType *p1);
        
        template <class T> class RailIterator : public Ref {
        protected:
            x10_int index;
            const x10_int length;
            T const* data;

        public:
            RTT_H_DECLS

            static x10aux::itable_entry _railItITables[2];
            virtual x10aux::itable_entry* _getITables() { return _railItITables; }
    
            static x10_boolean _itable_thunk_hasNext(x10aux::ref<x10::lang::Iterator<T> > this_) {
                x10aux::ref<x10::lang::RailIterator<T> > tmp = this_;
                return tmp->hasNext();
            }

            static T _itable_thunk_next(x10aux::ref<x10::lang::Iterator<T> > this_) {
                x10aux::ref<x10::lang::RailIterator<T > > tmp = this_;
                return tmp->next();
            }
                
            RailIterator(x10_int length_, T* data_) : index(0), length(length_), data(data_) { }

            virtual x10_boolean hasNext() {
                return index < length;
            }
             
            virtual T next() {
                if (index >= length) {
                    x10aux::throwArrayIndexOutOfBoundsException(index, length);
                }
                return data[index++];
            }

            virtual x10_int hashCode() { return 0; }
            
            virtual x10_boolean equals(x10aux::ref<Ref> other) {
                if (!x10aux::concrete_instanceof<RailIterator>(other)) return false;
                x10aux::ref<RailIterator> other_i = other;
                if (other_i->data != data) return false;
                if (other_i->index != index) return false;
                return true;
            }   

            virtual x10_boolean equals(x10aux::ref<Value> other) {
                return this->Ref::equals(other);
            }

            virtual x10aux::ref<String> toString() {
                return new (x10aux::alloc<String>()) String();
            }
        };  

        template<class T> x10aux::RuntimeType RailIterator<T>::rtt;

        template<class T> void RailIterator<T>::_initRTT() {
            rtt.parentsc = -2;
            x10::lang::_initRTTHelper_RailIterator(&rtt, x10aux::getRTT<T>(), x10aux::getRTT<x10::lang::Iterator<T> >());
        }        

        template<class T> x10aux::itable_entry x10::lang::RailIterator<T>::_railItITables[2] = {
            x10aux::itable_entry(&x10::lang::Iterator<T>::rtt,
                                 new typename x10::lang::Iterator<T>::itable(&x10::lang::RailIterator<T>::_itable_thunk_hasNext,
                                                                             &x10::lang::RailIterator<T>::_itable_thunk_next)),
            x10aux::itable_entry(NULL, NULL)
        };
    }
}
#endif
