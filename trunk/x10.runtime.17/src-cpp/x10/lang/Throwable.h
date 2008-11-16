#ifndef X10_LANG_THROWABLE_H
#define X10_LANG_THROWABLE_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Value.h>

namespace x10 {

    namespace io {
        class Printer;
    }

    namespace lang {

        class String;

        class Throwable : public Value {

            public:

            class RTT : public x10aux::RuntimeType { 
                public:
                static const RTT* const it; 
            
                RTT() : RuntimeType(1,x10aux::getRTT<Value>()) { }
                
                virtual std::string name() const {
                    return "x10.lang.Throwable";
                }

            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Throwable>();
            }

            x10aux::ref<Throwable> FMGL(cause);
            x10aux::ref<String> FMGL(message);

            Throwable();
            Throwable(const x10aux::ref<String> &message);
            Throwable(const x10aux::ref<Throwable> &cause);
            Throwable(const x10aux::ref<String> &message,
                      const x10aux::ref<Throwable> &cause);

            virtual x10aux::ref<String> getMessage();
            virtual x10aux::ref<Throwable> getCause();
            virtual x10aux::ref<String> toString();
            virtual void printStackTrace();
            virtual void printStackTrace(x10aux::ref<x10::io::Printer> p);

            explicit Throwable(x10aux::SERIALIZATION_MARKER m) : Value(m){
                (void) m;
            }

            /* TODO: don't care about this just yet
            // Serialization
            static const int SERIALIZATION_ID = 17;
            virtual void _serialize(x10aux::serialization_buffer& buf, x10aux::addr_map& m) { x10aux::_serialize_ref(this, buf, m); }
            virtual void _serialize_fields(x10aux::serialization_buffer& buf, x10aux::addr_map& m);
            virtual void _deserialize_fields(x10aux::serialization_buffer& buf);
            */

        };


    }
}


#endif
