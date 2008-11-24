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

            typedef x10aux::ref<Box<x10aux::ref<Throwable> > > Cause;

            Exception() : Throwable() { }
            Exception(x10aux::ref<String> message) : Throwable(message) {   }
            Exception(x10aux::ref<String> message, Cause cause) : Throwable(message, cause) { }
            Exception(Cause cause) : Throwable(cause) { }

            Exception(x10aux::SERIALIZATION_MARKER m) : Throwable(m) { }

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
