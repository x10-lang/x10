#ifndef X10_LANG_RUNTIMEEXCEPTION_H
#define X10_LANG_RUNTIMEEXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Exception.h>

namespace x10 {

    namespace lang {

        class RuntimeException : public Exception {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
                virtual void init() { initParents(1,x10aux::getRTT<Exception>()); }
                virtual std::string name() const { return "x10.lang.RuntimeException"; }
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<RuntimeException>();
            }

            typedef x10aux::ref<Box<x10aux::ref<Throwable> > > Cause;

            RuntimeException() : Exception() { }
            RuntimeException(x10aux::ref<String> message) : Exception(message) {   }
            RuntimeException(x10aux::ref<String> message, x10aux::ref<Throwable> cause)
              : Exception(message,cause) {}
            RuntimeException(x10aux::ref<Throwable> cause) : Exception(cause) { }

            RuntimeException(x10aux::SERIALIZATION_MARKER m) : Exception(m) { }

            // Serialization
            //static const int SERIALIZATION_ID = 16;
            virtual void _serialize(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
                (void)buf; (void)m; abort();
                //x10aux::_serialize_ref(this, buf, m);
            }
            virtual void _serialize_fields(x10aux::serialization_buffer& buf, x10aux::addr_map& m);
            virtual void _deserialize_fields(x10aux::serialization_buffer& buf);


        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
