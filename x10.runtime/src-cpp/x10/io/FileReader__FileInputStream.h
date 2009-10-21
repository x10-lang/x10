#ifndef X10_IO_FILEINPUTSTREAM_H
#define X10_IO_FILEINPUTSTREAM_H

#include <x10/io/InputStreamReader__InputStream.h>
#include <x10aux/io/FILEPtrInputStream.h>

namespace x10 {

    namespace io {

        class FileReader__FileInputStream : public InputStreamReader__InputStream {
        protected:
            x10aux::io::FILEPtrInputStream _inputStream;
            
        public:
            RTT_H_DECLS_CLASS;

            FileReader__FileInputStream(FILE *f) : _inputStream(f) { } 

            static x10aux::ref<FileReader__FileInputStream> _make(x10aux::ref<x10::lang::String> name);

            virtual char * gets(char *buf, int sz) {
                return _inputStream.gets(buf,sz);
            }

            virtual void close() {
                _inputStream.close();
            }

            virtual x10_int read() {
                return _inputStream.read();
            }

            virtual x10_int read(x10aux::ref<x10::lang::Rail<x10_byte> > b,
                                 x10_int off,
                                 x10_int len) {
                return _inputStream.read(b, off, len);
            }

            virtual void skip(x10_int bytes) {
                return _inputStream.skip(bytes);
            }

            static x10aux::ref<FileReader__FileInputStream> STANDARD_IN;

            // Serialization
            static const x10aux::serialization_id_t _serialization_id;
            virtual x10aux::serialization_id_t _get_serialization_id() {
                return _serialization_id;
           }
            virtual void _serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m);
            template<class __T> static x10aux::ref<__T> _deserializer(x10aux::deserialization_buffer& buf);
            void _deserialize_body(x10aux::deserialization_buffer& buf);
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
