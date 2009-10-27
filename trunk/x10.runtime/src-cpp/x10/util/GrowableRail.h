#ifndef X10_UTIL_GROWABLE_RAIL_H
#define X10_UTIL_GROWABLE_RAIL_H

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>
#include <x10aux/rail_utils.h>
#include <x10aux/ref.h>
#include <x10aux/serialization.h>

#include <x10/lang/Ref.h>

namespace x10 {

    namespace util {

        void _initRTTHelper_GrowableRail(x10aux::RuntimeType *location, const x10aux::RuntimeType *rtt);
        
        template<class T> class GrowableRail : public x10::lang::Ref {
        public:
            RTT_H_DECLS_CLASS;

        private:
            x10aux::ref<x10::lang::Rail<T> > _array;
            x10_int _len;
            
        public:
            static x10aux::ref<GrowableRail<T> > _make() {
                return (new (x10aux::alloc<GrowableRail<T> >()) GrowableRail<T>())->_constructor();
            }
            static x10aux::ref<GrowableRail<T> > _make(x10_int sz) {
                return (new (x10aux::alloc<GrowableRail<T> >())GrowableRail<T>())->_constructor(sz);
            }
            x10aux::ref<GrowableRail> _constructor() {
                return this->_constructor(1);
            }
            x10aux::ref<GrowableRail> _constructor(x10_int size);

            static const x10aux::serialization_id_t _serialization_id;

            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };

            virtual void _serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m);

            template<class U> static x10aux::ref<U> _deserializer(x10aux::deserialization_buffer &buf);

            virtual void _deserialize_body(x10aux::deserialization_buffer& buf);

            // No specialized serialization methods - not optimizing this final class

            T set(T v, x10_int i);

            void add(T v);

            T apply(x10_int i);

            void removeLast();

            x10_int length();

            x10aux::ref<x10::lang::Rail<T> > toRail();

            x10aux::ref<x10::lang::ValRail<T> > toValRail();

        private:
            void grow(x10_int newSize);

            void shrink(x10_int newSize);
                
            x10_int size();
        };
    }
}
#endif

#ifndef __X10_UTIL_GROWABLERAIL_H_NODEPS
#define __X10_UTIL_GROWABLERAIL_H_NODEPS
#include <x10/lang/Rail.h>
#ifndef X10_UTIL_GROWABLERAIL_H_IMPLEMENTATION
#define X10_UTIL_GROWABLERAIL_H_IMPLEMENTATION


namespace x10 {
    namespace util {
        template<class T> x10aux::ref<GrowableRail<T> > GrowableRail<T>::_constructor(x10_int size) {
            this->x10::lang::Ref::_constructor();
            _array = x10::lang::Rail<T>::make(size);
            _len = 0;
            return this;
        }

        template<class T> T GrowableRail<T>::set(T v, x10_int i) {
            grow(i+1);
            return (*_array)[i] = v;
        }

        template<class T> void GrowableRail<T>::add(T v) {
            grow(_len+1);
            (*_array)[_len] = v;
            _len++;
        }

        template<class T> T GrowableRail<T>::apply(x10_int i) {
            return (*_array)[i];
        }

        template<class T> void GrowableRail<T>::removeLast() {
            (*_array)[_len-1] = (T)0;
            _len--;
            shrink(_len+1);
        }

        template<class T> x10_int GrowableRail<T>::length() { return _len; }

        template<class T> x10aux::ref<x10::lang::Rail<T> > GrowableRail<T>::toRail() {
            x10aux::ref<x10::lang::Rail<T> > ans = x10::lang::Rail<T>::make(_len);
            for (int i=0; i<_len; i++) {
                (*ans)[i] = (*_array)[i];
            }
            return ans;
        }

        template<class T> x10aux::ref<x10::lang::ValRail<T> > GrowableRail<T>::toValRail() {
            x10aux::ref<x10::lang::ValRail<T> > ans = x10::lang::ValRail<T>::make(_len);
            for (int i=0; i<_len; i++) {
                (*ans)[i] = (*_array)[i];
            }
            return ans;
        }

        template<class T> void GrowableRail<T>::grow(x10_int newSize) {
            x10_int oldStorage = size();

            if (newSize <= oldStorage) {
                return;
            }
            if (newSize < oldStorage*2) {
                newSize = oldStorage*2;
            }
            if (newSize < _len) {
                newSize = _len;
            }
            if (newSize < 8) {
                newSize = 8;
            }

            x10aux::ref<x10::lang::Rail<T> > tmp = x10::lang::Rail<T>::make(newSize);
            for (int i=0; i<_len; i++) {
                (*tmp)[i] = (*_array)[i];
            }

            _array = tmp;
        }


        template<class T> void GrowableRail<T>::shrink(x10_int newSize) {
            if (newSize > size()/2 || newSize < 8) {
                return;
            }

            if (newSize < _len) {
                newSize = _len;
            }
            if (newSize < 8) {
                newSize = 8;
            }

            x10aux::ref<x10::lang::Rail<T> > tmp = x10::lang::Rail<T>::make(newSize);
            for (int i=0; i<_len; i++) {
                (*tmp)[i] = (*_array)[i];
            }

            _array = tmp;
        }
            
        template<class T> x10_int GrowableRail<T>::size() { return _array->FMGL(length); }

        template<class T> void GrowableRail<T>::_initRTT() {
            rtt.canonical = &rtt;
            x10::util::_initRTTHelper_GrowableRail(&rtt, x10aux::getRTT<T>());
        }
        
        template<class T> x10aux::RuntimeType GrowableRail<T>::rtt;

        template<class T> void GrowableRail<T>::_serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
            this->x10::lang::Ref::_serialize_body(buf, m);
        }

        template<class T> template<class U> x10aux::ref<U> GrowableRail<T>::_deserializer(x10aux::deserialization_buffer &buf) {
            x10aux::ref<GrowableRail> this_ = new (x10aux::alloc_remote<GrowableRail>()) GrowableRail();
            this_->_deserialize_body(buf);
            return this_;
        }

        template<class T> void GrowableRail<T>::_deserialize_body(x10aux::deserialization_buffer& buf) {
            this->x10::lang::Ref::_deserialize_body(buf);
        }

        template<class T> const x10aux::serialization_id_t GrowableRail<T>::_serialization_id =
            x10aux::DeserializationDispatcher::addDeserializer(GrowableRail<T>::template _deserializer<Ref>);

        template<> class GrowableRail<void> {
        public:
            static x10aux::RuntimeType rtt;
            static const x10aux::RuntimeType* getRTT() { return &rtt; }
        };
        
    }
}


#endif

#endif

// vim: shiftwidth=4:tabstop=4:expandtab
