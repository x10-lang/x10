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
            RTT_H_DECLS_CLASS;

            static typename Iterator<T>::template itable<RailIterator<T> > _itable_iterator;
            static x10aux::itable_entry _railItITables[2];
            virtual x10aux::itable_entry* _getITables() { return _railItITables; }
    
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
            
            virtual x10_boolean equals(x10aux::ref<Object> other) {
                if (!x10aux::concrete_instanceof<RailIterator>(other)) return false;
                x10aux::ref<RailIterator> other_i = other;
                if (other_i->data != data) return false;
                if (other_i->index != index) return false;
                return true;
            }   

            virtual x10aux::ref<String> toString() {
                return new (x10aux::alloc<String>()) String();
            }
        };  

        template<class T> x10aux::RuntimeType RailIterator<T>::rtt;
        
        template<> class RailIterator<void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return &rtt; }
        };

        template<class T> void RailIterator<T>::_initRTT() {
            rtt.canonical = &rtt;
            _initRTTHelper_RailIterator(&rtt, x10aux::getRTT<T>(), x10aux::getRTT<Iterator<T> >());
        }

        template <class T>
            typename Iterator<T>::template itable<RailIterator<T> >
            RailIterator<T>::_itable_iterator(&RailIterator<T>::hasNext, &RailIterator<T>::next);

        template<class T> x10aux::itable_entry RailIterator<T>::_railItITables[2] = {
            x10aux::itable_entry(&Iterator<T>::rtt, &RailIterator<T>::_itable_iterator),
            x10aux::itable_entry(NULL,  (void*)x10aux::getRTT<RailIterator<T> >())
        };
    }
}
#endif
