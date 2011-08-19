/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

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

            FileWriter__FileOutputStream(FILE *f): _outputStream(f) { }
            FileWriter__FileOutputStream(): _outputStream(NULL) { }

            static x10aux::ref<FileWriter__FileOutputStream> _make(x10aux::ref<x10::lang::String> name);

            void _constructor (x10aux::ref<x10::lang::String> file);
            void _constructor (FILE* file);
            void _constructor ();

            virtual void write(const char *str) {
                _outputStream.write(str);
            }

            virtual void write(x10_int i) {
                _outputStream.write(i);
            }

            virtual void write(x10::util::IndexedMemoryChunk<x10_byte> b, x10_int off, x10_int len);

            virtual void flush() {
                _outputStream.flush();
            }

            virtual void close() {
                _outputStream.close();
            }

            // Serialization
            static const x10aux::serialization_id_t _serialization_id;
            virtual x10aux::serialization_id_t _get_serialization_id() {
                return _serialization_id;
            }
            virtual void _serialize_body(x10aux::serialization_buffer& buf);
            static x10aux::ref<Reference> _deserializer(x10aux::deserialization_buffer& buf);
            void _deserialize_body(x10aux::deserialization_buffer& buf);
            // No specialized serialization methods - not optimizing this final class
        };

    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
