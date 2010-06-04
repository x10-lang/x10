#ifndef X10_IO_FILEOUTPUTSTREAM_H
#define X10_IO_FILEOUTPUTSTREAM_H

#include <x10/io/OutputStreamWriter__OutputStream.h>
#include <x10aux/io/FILEPtrOutputStream.h>

namespace x10 {

    namespace io {

        class FileWriter__FileOutputStream : public x10::io::OutputStreamWriter__OutputStream {
        protected:
            x10aux::io::FILEPtrOutputStream _outputStream;
            
        public:
            RTT_H_DECLS_CLASS;

            FileWriter__FileOutputStream(FILE *f) : _outputStream(f) { }

            static x10aux::ref<FileWriter__FileOutputStream> _make(x10aux::ref<x10::lang::String> name);
            
            virtual void write(const char *str) {
                _outputStream.write(str);
            }

            virtual void write(x10_int i) {
                _outputStream.write(i);
            }

            virtual void write(x10aux::ref<x10::lang::Rail<x10_byte> > b, x10_int off, x10_int len) {
                _outputStream.write(b, off, len);
            }

            virtual void write(x10aux::ref<x10::lang::ValRail<x10_byte> > b,x10_int off,x10_int len) {
                _outputStream.write(b, off, len);
            }

            virtual void flush() {
                _outputStream.flush();
            }

            virtual void close() {
                _outputStream.close();
            }

            static x10aux::ref<FileWriter__FileOutputStream> STANDARD_OUT;

            static x10aux::ref<FileWriter__FileOutputStream> STANDARD_ERR;

            // Serialization
            static const x10aux::serialization_id_t _serialization_id;
            virtual x10aux::serialization_id_t _get_serialization_id() {
                return _serialization_id;
            }
            virtual void _serialize_body(x10aux::serialization_buffer& buf);
            template<class __T> static x10aux::ref<__T> _deserializer(x10aux::deserialization_buffer& buf);
            void _deserialize_body(x10aux::deserialization_buffer& buf);
            // No specialized serialization methods - not optimizing this final class
        };

        template<class __T> x10aux::ref<__T> FileWriter__FileOutputStream::_deserializer(x10aux::deserialization_buffer& buf) {
            // TODO: attempting to serialize _outputStream is nonsensical.
            //       The old 1.7 definition of this class simply didn't work either,
            //       it just silently didn't serialize the FILEPtrInputSteam field.
            // assert(false);
            x10aux::ref<FileWriter__FileOutputStream> this_ = new (x10aux::alloc_remote<FileWriter__FileOutputStream>()) FileWriter__FileOutputStream(NULL);
            buf.record_reference(this_);
            this_->_deserialize_body(buf);
            return this_;
        }
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
