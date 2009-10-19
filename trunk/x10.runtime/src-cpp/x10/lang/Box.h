#ifndef X10_LANG_BOX_H
#define X10_LANG_BOX_H


#include <x10aux/config.h>
#include <x10aux/RTT.h>
#include <x10aux/basic_functions.h>
#include <x10aux/serialization.h>


#include <x10/lang/Ref.h>


namespace x10 {

    namespace lang {

        class Object;
        class String;

        void _initRTTHelper_Box(x10aux::RuntimeType *location, const x10aux::RuntimeType *rtt);
        
        template<class T> class Box : public Ref {
        public:
            RTT_H_DECLS_CLASS;

            static inline x10aux::ref<Box<T> > _make(T contents_) {
                return (new (x10aux::alloc<Box<T> >())Box<T>())->_constructor(contents_);
            }

            inline x10aux::ref<Box<T> > _constructor(T contents_) {
                this->Ref::_constructor();
                FMGL(value) = contents_;
                return this;
            }

            static const x10aux::serialization_id_t _serialization_id;

            virtual inline x10aux::serialization_id_t _get_serialization_id() { return _serialization_id; }

            virtual void _serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m);

            template<class U> static x10aux::ref<U> _deserializer(x10aux::deserialization_buffer &buf);

            virtual void _deserialize_body(x10aux::deserialization_buffer& buf);

            virtual T get() {
                return FMGL(value);
            }

            virtual x10aux::ref<String> toString() {
                 return x10aux::to_string(FMGL(value));
            }

        public:

            T FMGL(value);

        };

        template<class T> const x10aux::serialization_id_t Box<T>::_serialization_id =
            x10aux::DeserializationDispatcher::addDeserializer(Box<T>::template _deserializer<Object>);

        template<class T> void Box<T>::_serialize_body(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
            this->x10::lang::Ref::_serialize_body(buf, m);
        }

        template<class T> template<class U> x10aux::ref<U> Box<T>::_deserializer(x10aux::deserialization_buffer &buf) {
            x10aux::ref<Box> this_ = new (x10aux::alloc_remote<Box>()) Box();
            this_->_deserialize_body(buf);
            return this_;
        }

        template<class T> void Box<T>::_deserialize_body(x10aux::deserialization_buffer& buf) {
            this->x10::lang::Ref::_deserialize_body(buf);
        }

        template <> class Box<void> : public Ref {
        };

        template<class T> void Box<T>::_initRTT() {
            rtt.canonical = &rtt;
            x10::lang::_initRTTHelper_Box(&rtt, x10aux::getRTT<T>());
        }
        
        template<class T> x10aux::RuntimeType Box<T>::rtt;
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
