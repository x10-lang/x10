#ifndef X10_LANG_EXCEPTION_H
#define X10_LANG_EXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Throwable.h>
#include <x10/lang/Box.h>

namespace x10 {

    namespace lang {

        class Exception : public Throwable {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
                virtual void init() { initParents(1,x10aux::getRTT<Throwable>()); }
                virtual std::string name() const { return "x10.lang.Exception"; }
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Exception>();
            }

            Exception() : Throwable() { }
            Exception(x10aux::ref<String> message) : Throwable(message) {   }
            Exception(x10aux::ref<String> message, x10aux::ref<Throwable> cause)
                : Throwable(message, cause) { }
            Exception(x10aux::ref<Throwable> cause) : Throwable(cause) { }

            Exception(x10aux::SERIALIZATION_MARKER m) : Throwable(m) { }

            static const x10aux::serialization_id_t _serialization_id;

            virtual void _serialize_id(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                buf.write(_serialization_id,m);
            }

            virtual void _serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
                Throwable::_serialize_body(buf,m);
            }

            void _deserialize_body(x10aux::serialization_buffer& buf) {
                Throwable::_deserialize_body(buf);
            }

            template<class T>
            static x10aux::ref<T> _deserializer(x10aux::serialization_buffer &buf){
                x10aux::ref<Exception> this_ = X10NEW(Exception)(x10aux::SERIALIZATION_MARKER());
                this_->_deserialize_body(buf);
                return this_;
            }

        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
