#ifndef X10_IO_FILENOTFOUNDEXCEPTION_H
#define X10_IO_FILENOTFOUNDEXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/io/IOException.h>

namespace x10 {

    namespace io {

        class FileNotFoundException : public IOException {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
                virtual void init() {
                    initParents(1,x10aux::getRTT<IOException>());
                }
                virtual const char *name() const { return "x10.io.FileNotFoundException"; }
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<FileNotFoundException>();
            }


            static x10aux::ref<FileNotFoundException> _make() {
                return (new (x10aux::alloc<FileNotFoundException>()) FileNotFoundException())
                    ->_constructor();
            }

            static x10aux::ref<FileNotFoundException> _make(x10aux::ref<x10::lang::String> message){
                return (new (x10aux::alloc<FileNotFoundException>()) FileNotFoundException())
                    ->_constructor(message);
            }

            static x10aux::ref<FileNotFoundException> _make(x10aux::ref<Throwable> cause) {
                return (new (x10aux::alloc<FileNotFoundException>()) FileNotFoundException())
                    ->_constructor(cause);
            }
    
            static x10aux::ref<FileNotFoundException> _make(x10aux::ref<x10::lang::String> message,
                                                            x10aux::ref<Throwable> cause)
            {
                return (new (x10aux::alloc<FileNotFoundException>()) FileNotFoundException())
                    ->_constructor(message, cause);
            }


            static const x10aux::serialization_id_t _serialization_id;

            virtual void _serialize_id(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                buf.write(_serialization_id,m);
            }

            template<class T>
            static x10aux::ref<T> _deserializer(x10aux::serialization_buffer &buf) {
                x10aux::ref<FileNotFoundException> this_ =
                    new (x10aux::alloc<FileNotFoundException>()) FileNotFoundException();
                this_->_deserialize_body(buf);
                return this_;
            }


        };

    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
