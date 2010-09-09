#ifndef X10_RUNTIME_PLACELOCALHANDLE_H
#define X10_RUNTIME_PLACELOCALHANDLE_H

#include <x10rt17.h>

#include <x10/lang/Value.h>
#include <x10/lang/String.h>

namespace x10 {
    namespace runtime {

        void _initRTTHelper_PlaceLocalHandle(x10aux::RuntimeType *location, const x10aux::RuntimeType *rtt);
        
        template <class T> class PlaceLocalHandle : public x10::lang::Value {
        public:
            RTT_H_DECLS_CLASS;

            T FMGL(localStorage);
            x10_int FMGL(id);
            bool FMGL(cached);

            static x10aux::ref<PlaceLocalHandle<T> > createHandle() {
                x10_int id = x10aux::place_local::nextId();
                return (new (x10aux::alloc<PlaceLocalHandle<T> >())PlaceLocalHandle<T>())->_constructor(id);
            }

            x10aux::ref<PlaceLocalHandle<T> > _constructor(x10_int id_) {
                FMGL(id) = id_;
                FMGL(cached) = false;
                return this;
            }

            virtual void set(T newVal) {
                assert(!FMGL(cached));
                FMGL(localStorage) = newVal;
                FMGL(cached) = true;
                x10aux::place_local::registerHandle(FMGL(id), (void*)this);
            }

            virtual T get() {
                if (!FMGL(cached)) {
                    PlaceLocalHandle<T> *tmp = (PlaceLocalHandle<T> *)(x10aux::place_local::lookupHandle(FMGL(id)));
                    assert(NULL != tmp); // TODO: throw proper exception and/or implement lazy creation.
                    FMGL(localStorage) = tmp->FMGL(localStorage);
                    FMGL(cached) = true;
                }
                return FMGL(localStorage);
            }

            virtual x10_int hashCode() {
                return x10aux::hash_code(FMGL(id));
            }

            virtual x10aux::ref<x10::lang::String> toString() {
                if (FMGL(cached)) {
                    return x10aux::to_string(FMGL(localStorage));
                } else {
                    return x10::lang::String::Lit("PlaceLocalHandle(uncached data)");
                }
            }

            static const x10aux::serialization_id_t _serialization_id;

            static void _serialize(x10aux::ref<PlaceLocalHandle<T> > this_,
                                   x10aux::serialization_buffer &buf,
                                   x10aux::addr_map &m);
            virtual x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; };
            void _serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m);
            template<class S> static x10aux::ref<S> _deserialize(x10aux::deserialization_buffer &buf);
        };


        template <> class PlaceLocalHandle<void> : public x10::lang::Value {
        public:
            static x10aux::RuntimeType rtt;
            static void _static_init() { }
            static const x10aux::RuntimeType* getRTT() { return &rtt; }
        };

        template<class T> void PlaceLocalHandle<T>::_initRTT() {
            rtt.canonical = &rtt;
            x10::runtime::_initRTTHelper_PlaceLocalHandle(&rtt, x10aux::getRTT<T>());
        }
        
        template<class T> x10aux::RuntimeType PlaceLocalHandle<T>::rtt;

        template<class T> const x10aux::serialization_id_t PlaceLocalHandle<T>::_serialization_id =
            x10aux::DeserializationDispatcher::addDeserializer(PlaceLocalHandle<T>::template _deserialize<Object>);


        template <class T> void PlaceLocalHandle<T>::_serialize(x10aux::ref<PlaceLocalHandle<T> > this_,
                                                                x10aux::serialization_buffer &buf,
                                                                x10aux::addr_map &m) {
            // TODO: This happens when an uninitialized place local handle is serialized as part of its
            //       containing object (when the this pointer of a constructor escapes).
            //       Arguably, this should be reported as a static type error by the compiler.
            if (this_ == x10aux::null) {
                buf.write((x10_int)-1,m);
            } else {
                this_->_serialize_body(buf, m);
            }
        }

        template <class T> void PlaceLocalHandle<T>::_serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
            // NOTE specialized semantics.  Only id is serialized, cached and localStorage are place local!
            buf.write(this->FMGL(id),m);
        }

        template <class T> template<class S> x10aux::ref<S> PlaceLocalHandle<T>::_deserialize(x10aux::deserialization_buffer &buf) {
            x10_int id = buf.read<x10_int>();
            if (id == -1) {
                return NULL;
            }
            PlaceLocalHandle<T> *tmp =  (PlaceLocalHandle<T> *)(x10aux::place_local::lookupHandle(id));
            if (NULL != tmp) {
                // We already have a PlaceLocalHandle with this id registered here; just return it.
                return x10aux::ref<S>(tmp);
            } else {
                // This is a handle that has never reached this place before. Create a non-registered one.
                x10aux::ref<PlaceLocalHandle<T> > this_ = new (x10aux::alloc<PlaceLocalHandle<T> >()) PlaceLocalHandle<T>();
                this_->_constructor(id);
                return  this_;
            }
        }
    }
}
#endif

