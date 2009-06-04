#ifndef X10_IO_FILENOTFOUNDEXCEPTION_H
#define X10_IO_FILENOTFOUNDEXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/io/IOException.h>

namespace x10 {

    namespace io {

        class FileNotFoundException : public IOException {
        public:
            RTT_H_DECLS;

            static x10aux::ref<FileNotFoundException> _make();
            static x10aux::ref<FileNotFoundException> _make(x10aux::ref<x10::lang::String> message);
            static x10aux::ref<FileNotFoundException> _make(x10aux::ref<Throwable> cause);
            static x10aux::ref<FileNotFoundException> _make(x10aux::ref<x10::lang::String> message,
                                                            x10aux::ref<Throwable> cause);

            static const x10aux::serialization_id_t _serialization_id;

            virtual void _serialize_id(x10aux::serialization_buffer &buf, x10aux::addr_map &m) {
                buf.write(_serialization_id,m);
            }

            template<class T> static x10aux::ref<T> _deserializer(x10aux::serialization_buffer &buf);
        };

        template<class T> x10aux::ref<T> FileNotFoundException::_deserializer(x10aux::serialization_buffer &buf) {
            x10aux::ref<FileNotFoundException> this_ =
                new (x10aux::alloc<FileNotFoundException>()) FileNotFoundException();
            this_->_deserialize_body(buf);
            return this_;
        }
    }
}


#endif
// vim:tabstop=4:shiftwidth=4:expandtab
