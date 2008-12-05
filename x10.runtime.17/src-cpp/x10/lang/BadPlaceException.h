#ifndef X10_LANG_BADPLACEEXCEPTION_H
#define X10_LANG_BADPLACEEXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/lang/RuntimeException.h>

namespace x10 {

    namespace lang {

        class BadPlaceException : public RuntimeException {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
                virtual void init() { initParents(1,x10aux::getRTT<x10::lang::RuntimeException>());}
                virtual std::string name() const { return "x10.lang.BadPlaceException"; } 
            };

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<BadPlaceException>();
            }


            static x10aux::ref<BadPlaceException> _make()
            {
                return (new (x10aux::alloc<BadPlaceException>()) BadPlaceException())
                    ->_constructor();
            }

            static x10aux::ref<BadPlaceException> _make(x10aux::ref<String> message) {
                return (new (x10aux::alloc<BadPlaceException>()) BadPlaceException())
                    ->_constructor(message);
            }

            static x10aux::ref<BadPlaceException> _make(x10aux::ref<Throwable> cause) {
                return (new (x10aux::alloc<BadPlaceException>()) BadPlaceException())
                    ->_constructor(cause);
            }
    
            static x10aux::ref<BadPlaceException> _make(x10aux::ref<String> message,
                                                        x10aux::ref<Throwable> cause)
            {
                return (new (x10aux::alloc<BadPlaceException>()) BadPlaceException())
                    ->_constructor(message, cause);
            }


            static const x10aux::serialization_id_t _serialization_id;
            
            virtual void _serialize_id(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                buf.write(_serialization_id,m);
            }   

            template<class T>
            static x10aux::ref<T> _deserializer(x10aux::serialization_buffer &buf){
                x10aux::ref<BadPlaceException> this_ =
                    new (x10aux::alloc<BadPlaceException>()) BadPlaceException();
                this_->_deserialize_body(buf);
                return this_;
            }   
            
        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
