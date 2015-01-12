/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef X10_IO_FILEINPUTSTREAM_H
#define X10_IO_FILEINPUTSTREAM_H

#include <x10/io/InputStreamReader__InputStream.h>

#include <stdio.h>

namespace x10 { namespace lang { template<class T> class Rail; } }

namespace x10 {

    namespace io {

        class FileReader__FileInputStream : public InputStreamReader__InputStream {
        protected:
            FILE* FMGL(file);
            
        public:
            RTT_H_DECLS_CLASS;

            explicit FileReader__FileInputStream(FILE* file) : FMGL(file)(file) { } 
            FileReader__FileInputStream() : FMGL(file)(NULL) { } 
            
            static FileReader__FileInputStream* _make(::x10::lang::String* name);

            void _constructor (::x10::lang::String* file);
            void _constructor (FILE* file);

            virtual char * gets(char *buf, int sz);

            virtual void close();

            virtual x10_int read();

            virtual x10_int read(::x10::lang::Rail<x10_byte>* b,
                                 x10_int off,
                                 x10_int len);

            virtual void skip(x10_long bytes);

            virtual long offset();

            ::x10::lang::String* readLine();
            
            // Serialization
            static const ::x10aux::serialization_id_t _serialization_id;
            virtual ::x10aux::serialization_id_t _get_serialization_id() {
                return _serialization_id;
            }
            virtual void _serialize_body(::x10aux::serialization_buffer& buf);
            static ::x10::lang::Reference* _deserializer(::x10aux::deserialization_buffer& buf);
            void _deserialize_body(::x10aux::deserialization_buffer& buf);
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
