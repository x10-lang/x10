#ifndef X10_UTIL_GROWABLE_RAIL_H
#define X10_UTIL_GROWABLE_RAIL_H

#include <sstream>

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>
#include <x10aux/rail_utils.h>
#include <x10aux/ref.h>

#include <x10/lang/Ref.h>
#include <x10/lang/Rail.h>


namespace x10 {

    namespace util {

        template<class T> class GrowableRail : public x10::lang::Ref {
        public:

            class RTT : public x10aux::RuntimeType {
                public:
                static RTT * it;

                virtual void init() { initParents(1,x10aux::getRTT<Ref>()); }

                virtual std::string name() const {
                    std::stringstream ss;
                    ss << "GrowableRail[" << x10aux::getRTT<T>()->name() << "]";
                    return ss.str();
                }
                 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<GrowableRail<T> >();
            }

        private:
            x10aux::ref<x10::lang::Rail<T> > _array;
            x10_int _length;
            
        public:
            GrowableRail() : x10::lang::Ref(), _length(0), _array(x10::lang::Rail<T>::make(1)) { }
            GrowableRail(x10_int size) : x10::lang::Ref(), _length(0), _array(x10::lang::Rail<T>::make(size)) { }

            T set(x10aux::ref<T> v, x10_int i) {
                grow(i+1);
                return _array[i] = v;
            }

            void add(x10aux::ref<T> v) {
                grow(length+1);
                set(v, length);
                length++;
            }

            void removeLast() {
                _array[length-1] = (T)0;
                length--;
                shrink(length+1);
            }

            x10_int length() { return _length; }

            x10aux::ref<x10::lang::Rail<T> > toRail();

            x10aux::ref<x10::lang::ValRail<T> > toValRail();

        private:
            void grow(x10_int newSize);
            void shrink(x10_int newSize);
            x10_int size() { return _array->length(); }
        };

        template<class T> typename GrowableRail<T>::RTT *GrowableRail<T>::RTT::it =
            new (x10aux::alloc<typename GrowableRail<T>::RTT>()) typename GrowableRail<T>::RTT();


    }
}


#endif /* X10_LANG_GROWABLE_RAIL */
