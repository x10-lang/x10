#ifndef X10_IO_IOEXCEPTION_H
#define X10_IO_IOEXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Exception.h>

namespace x10 {

    namespace io {

        class IOException : public x10::lang::Exception {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
            
                virtual void init() {
                    initParents(1,x10aux::getRTT<x10::lang::Exception>());
                }
                
                virtual std::string name() const {
                    return "x10.io.IOException";
                }

            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<IOException>();
            }

            IOException() : Exception() { }
            IOException(x10aux::ref<x10::lang::String> message) : Exception(message) {   }
            IOException(x10aux::ref<x10::lang::String> message,
                        x10aux::ref<x10::lang::Throwable> cause)
              : Exception(message,cause) { }
            IOException(x10aux::ref<x10::lang::Throwable> cause) : Exception(cause) { }

            IOException(x10aux::SERIALIZATION_MARKER m) : Exception(m) { }

            static const x10aux::serialization_id_t _serialization_id;

            virtual void _serialize_id(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                buf.write(_serialization_id,m);
            }

            virtual void _serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
                Exception::_serialize_body(buf,m);
            }

            void _deserialize_body(x10aux::serialization_buffer& buf) {
                Exception::_deserialize_body(buf);
            }

            template<class T>
            static x10aux::ref<T> _deserializer(x10aux::serialization_buffer &buf) {
                x10aux::ref<IOException> this_ =
                    X10NEW(IOException)(x10aux::SERIALIZATION_MARKER());
                this_->_deserialize_body(buf);
                return this_;
            }


        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
