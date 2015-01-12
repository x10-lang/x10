/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef X10_LANG_PLACELOCALHANDLE_IMPL_H
#define X10_LANG_PLACELOCALHANDLE_IMPL_H

#include <x10aux/basic_functions.h>
#include <x10aux/place_local.h>
#include <x10/lang/String.h>

namespace x10 {
    namespace lang {

        void _initRTTHelper_PlaceLocalHandle_Impl(::x10aux::RuntimeType *location, const ::x10aux::RuntimeType *rtt);

        template <class T> class PlaceLocalHandle_Impl  {
        public:
            RTT_H_DECLS_STRUCT

            T FMGL(localStorage);
            x10_long FMGL(id);
            bool FMGL(cached);

            PlaceLocalHandle_Impl<T>* operator->() { return this; }

            void set(T newVal) {
                FMGL(localStorage) = newVal;
                FMGL(cached) = true;
                ::x10aux::place_local::put(FMGL(id), (void*)newVal);
            }

	        static PlaceLocalHandle_Impl<T> _alloc () {PlaceLocalHandle_Impl<T> t; return t;}

            static PlaceLocalHandle_Impl<T> _make() {
                PlaceLocalHandle_Impl<T> result;
                result->_constructor();
                return result;
            }
            
            void _constructor () {
                x10_long id = ::x10aux::place_local::nextId();
                FMGL(id) = id;
                FMGL(cached) = false;
            }
            
            T __apply() {
                if (!FMGL(cached)) {
                    T tmp = (T)(::x10aux::place_local::get(FMGL(id)));
                    FMGL(localStorage) = tmp;
                    FMGL(cached) = true;
                }
                return FMGL(localStorage);
            }

            ::x10::lang::String* toString() {
                if (FMGL(cached)) {
                    return ::x10aux::to_string(FMGL(localStorage));
                } else {
                    return ::x10::lang::String::Lit("PlaceLocalHandle_Impl(uncached data)");
                }
            }

            x10_int hashCode() {
                return ::x10aux::hash_code(FMGL(id));
            }


            x10_boolean _struct_equals(PlaceLocalHandle_Impl<T> that) {
                return FMGL(id) == that->FMGL(id);
            }

            static void _serialize(PlaceLocalHandle_Impl<T> this_, ::x10aux::serialization_buffer &buf);

            static PlaceLocalHandle_Impl<T> _deserialize(::x10aux::deserialization_buffer& buf);
        };

        template <> class PlaceLocalHandle_Impl<void>  {
        public:
            static ::x10aux::RuntimeType rtt;
            static const ::x10aux::RuntimeType* getRTT() { return &rtt; }
        };

        template<class T> void PlaceLocalHandle_Impl<T>::_initRTT() {
            if (rtt.initStageOne(::x10aux::getRTT<PlaceLocalHandle_Impl<void> >())) return;
            ::x10::lang::_initRTTHelper_PlaceLocalHandle_Impl(&rtt, ::x10aux::getRTT<T>());
        }

        template<class T> ::x10aux::RuntimeType PlaceLocalHandle_Impl<T>::rtt;

        template <class T> void PlaceLocalHandle_Impl<T>::_serialize(PlaceLocalHandle_Impl<T> this_, ::x10aux::serialization_buffer &buf) {
            // NOTE specialized semantics.  Only id is serialized, cached and localStorage are place local!
            buf.write(this_->FMGL(id));
        }

        template<class T> PlaceLocalHandle_Impl<T> PlaceLocalHandle_Impl<T>::_deserialize(::x10aux::deserialization_buffer& buf) {
            // NOTE specialized semantics.  Only id is serialized, cached is automatically set to false; will be looked up on first use.
            PlaceLocalHandle_Impl<T> this_;
            this_->FMGL(id) = buf.read<x10_long>();
            this_->FMGL(cached) = false;
            return this_;
        }
    }
}

namespace x10 {
    namespace lang {
        namespace PlaceLocalHandle_Impl_ns {
        }
    }
}
#endif

