/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#ifndef X10_UTIL_GROWABLE_RAIL_H
#define X10_UTIL_GROWABLE_RAIL_H

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>
#include <x10aux/rail_utils.h>
#include <x10aux/ref.h>
#include <x10aux/serialization.h>

#include <x10/lang/Object.h>

namespace x10 {
    namespace array { template <class T> class Array; } 
    
    namespace util {

        void _initRTTHelper_GrowableRail(x10aux::RuntimeType *location, const x10aux::RuntimeType *rtt);

        template<class T> class GrowableRail : public x10::lang::Object {
        public:
            RTT_H_DECLS_CLASS;

        private:
            x10aux::ref<x10::lang::Rail<T> > _array;
            x10_int _len;

        public:

            T* raw() { return _array->raw(); }

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

            virtual void _serialize_body(x10aux::serialization_buffer &buf);

            template<class U> static x10aux::ref<U> _deserializer(x10aux::deserialization_buffer &buf);

            virtual void _deserialize_body(x10aux::deserialization_buffer& buf);

            // No specialized serialization methods - not optimizing this final class

            T __set(x10_int i, T v);

            void add(T v);
            void insert(x10_int loc, x10aux::ref<x10::lang::Rail<T> > items);

            T __apply(x10_int i);

            void removeLast();

            x10aux::ref<x10::lang::Rail<T> > moveSectionToRail(x10_int i, x10_int j);

            x10_int length();

            x10aux::ref<x10::array::Array<T> > toArray();

            x10aux::ref<x10::lang::Rail<T> > toRail();

            void setLength(x10_int newLength);

        private:
            void grow(x10_int newSize);

            void shrink(x10_int newSize);

            void grow_internal(x10_int newSize);

            void shrink_internal(x10_int newSize);

            x10_int size();
        };
    }
}
#endif

#ifndef __X10_UTIL_GROWABLERAIL_H_NODEPS
#define __X10_UTIL_GROWABLERAIL_H_NODEPS
#include <x10/lang/Rail.h>
#include <x10/array/Array.h>
#ifndef X10_UTIL_GROWABLERAIL_H_IMPLEMENTATION
#define X10_UTIL_GROWABLERAIL_H_IMPLEMENTATION


namespace x10 {
    namespace util {
        template<class T> x10aux::ref<GrowableRail<T> > GrowableRail<T>::_constructor(x10_int size) {
            this->x10::lang::Object::_constructor();
            _array = x10::lang::Rail<void>::make<T>(size);
            _len = 0;
            return this;
        }

        template<class T> inline x10_int GrowableRail<T>::size() { return _array->FMGL(length); }

        template<class T> inline x10_int GrowableRail<T>::length() { return _len; }

        template<class T> inline T GrowableRail<T>::__set(x10_int i, T v) {
            grow(i+1);
            return (*_array)[i] = v;
        }

        template<class T> inline void GrowableRail<T>::add(T v) {
            grow(_len+1);
            (*_array)[_len] = v;
            _len++;
        }

        template<class T> inline void GrowableRail<T>::insert(x10_int loc,
                    x10aux::ref<x10::lang::Rail<T> > items) {
            int addLen = items->FMGL(length);
            int newLen = _len + addLen;
            int movLen = _len - loc;
            grow(newLen);
            if (movLen > 0) {
                memmove(&(*_array)[loc + addLen], &(*_array)[loc], 
                        movLen * sizeof(T));
            }
            memcpy(&(*_array)[loc], &(*items)[0], addLen * sizeof(T));
            _len = newLen;
        }

        template<class T> inline T GrowableRail<T>::__apply(x10_int i) {
            return (*_array)[i];
        }

        template<class T> void GrowableRail<T>::removeLast() {
            memset(&(*_array)[_len-1], 0, sizeof(T));
            _len--;
            shrink(_len+1);
        }

        template<class T> x10aux::ref<x10::lang::Rail<T> > GrowableRail<T>::moveSectionToRail(x10_int i, x10_int j) {
            x10_int l = j - i + 1;
            if (l < 0) l = 0;
            x10aux::ref<x10::lang::Rail<T> > ans = x10::lang::Rail<void>::make<T>(l);
            if (l < 1) return ans;
            for (int k=0; k<l; k++) {
                (*ans)[k] = (*_array)[i+k];
            }
            for (int k=0; k<_len-j-1; k++) {
                (*_array)[i+k] = (*_array)[j+k];
            }
            _len -= l;
            memset(&(*_array)[_len], 0, l*sizeof(T));
            shrink(_len+1);
            return ans;
        }

        template<class T> x10aux::ref<x10::lang::Rail<T> > GrowableRail<T>::toRail() {
            x10aux::ref<x10::lang::Rail<T> > ans = x10::lang::Rail<void>::make<T>(_len);
            for (int i=0; i<_len; i++) {
                (*ans)[i] = (*_array)[i];
            }
            return ans;
        }

        template<class T> x10aux::ref<x10::array::Array<T> > GrowableRail<T>::toArray() {
            x10aux::ref<x10::array::Array<T> > ans = x10::array::Array<T>::_make(_len);
            for (int i=0; i<_len; i++) {
                ans->__set(i, (*_array)[i]);
            }
            return ans;
        }

        template<class T> void GrowableRail<T>::setLength(x10_int newLength) {
            grow(newLength);
            _len = newLength;
        }

        template<class T> inline void GrowableRail<T>::grow(x10_int newSize) {
            if (newSize > size()) {
                grow_internal(newSize);
            }
        }

        template<class T> void GrowableRail<T>::grow_internal(x10_int newSize) {
            x10_int oldStorage = size();

            if (newSize < oldStorage*2) {
                newSize = oldStorage*2;
            }
            if (newSize < _len) {
                newSize = _len;
            }
            if (newSize < 8) {
                newSize = 8;
            }

            x10aux::ref<x10::lang::Rail<T> > tmp = x10::lang::Rail<void>::make<T>(newSize);
            for (int i=0; i<_len; i++) {
                (*tmp)[i] = (*_array)[i];
            }

            x10aux::dealloc(_array.operator->());
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

            x10aux::ref<x10::lang::Rail<T> > tmp = x10::lang::Rail<void>::make<T>(newSize);
            for (int i=0; i<_len; i++) {
                (*tmp)[i] = (*_array)[i];
            }

            x10aux::dealloc(_array.operator->());
            _array = tmp;
        }

        template<class T> void GrowableRail<T>::_initRTT() {
            if (rtt.initStageOne(x10aux::getRTT<GrowableRail<void> >())) return;
            x10::util::_initRTTHelper_GrowableRail(&rtt, x10aux::getRTT<T>());
        }

        template<class T> x10aux::RuntimeType GrowableRail<T>::rtt;

        template<class T> void GrowableRail<T>::_serialize_body(x10aux::serialization_buffer &buf) {
            this->x10::lang::Object::_serialize_body(buf);
            buf.write(this->_len);
            buf.write(this->_array->FMGL(length));
            // Only serialize the part of the backing Rail that actually contains data.
            for (int i=0; i<_len; i++) {
                buf.write(this->_array->__apply(i)); 
            }                
        }

        template<class T> template<class U> x10aux::ref<U> GrowableRail<T>::_deserializer(x10aux::deserialization_buffer &buf) {
            x10aux::ref<GrowableRail> this_ = new (x10aux::alloc<GrowableRail>()) GrowableRail();
            buf.record_reference(this_); 
            this_->_deserialize_body(buf);
            return this_;
        }

        template<class T> void GrowableRail<T>::_deserialize_body(x10aux::deserialization_buffer& buf) {
            this->x10::lang::Object::_deserialize_body(buf);
            this->_len = buf.read<x10_int>();
            x10_int railLen = buf.read<x10_int>();
            this->_array = x10::lang::Rail<void>::make<T>(railLen);
            for (int i=0; i<_len; i++) {
                _array->__set(i, buf.read<T>());
            }
        }

        template<class T> const x10aux::serialization_id_t GrowableRail<T>::_serialization_id =
            x10aux::DeserializationDispatcher::addDeserializer(GrowableRail<T>::template _deserializer<x10::lang::Reference>, x10aux::CLOSURE_KIND_NOT_ASYNC);

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
