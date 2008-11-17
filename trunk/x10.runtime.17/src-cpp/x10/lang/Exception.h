#ifndef X10_LANG_EXCEPTION_H
#define X10_LANG_EXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/Throwable.h>

namespace x10 {

    namespace lang {

        class Exception : public Throwable {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
            
                virtual void init() {
                    initParents(1,x10aux::getRTT<Throwable>());
                }
                
                virtual std::string name() const {
                    return "x10.lang.Exception";
                }

            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Exception>();
            }

            Exception();
            Exception(const x10aux::ref<x10::lang::String> &message);
            Exception(const x10aux::ref<x10::lang::String> &message,
                      const x10aux::ref<Throwable> &cause);
            Exception(const x10aux::ref<Throwable> &cause);
            
            Exception(x10aux::SERIALIZATION_MARKER m) : Throwable(m){ (void) m; }

#if 0
TODO: 
            // Serialization
            public: static const int SERIALIZATION_ID = 16;
            public: virtual void _serialize(x10aux::serialization_buffer& buf, x10aux::addr_map& m) { x10aux::_serialize_ref(this, buf, m); }
            public: virtual void _serialize_fields(x10aux::serialization_buffer& buf, x10aux::addr_map& m);
            public: virtual void _deserialize_fields(x10aux::serialization_buffer& buf);
#endif


        };

    }
}


#endif
