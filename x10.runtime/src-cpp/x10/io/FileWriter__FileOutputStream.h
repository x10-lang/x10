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

#ifndef X10_IO_FILEOUTPUTSTREAM_H
#define X10_IO_FILEOUTPUTSTREAM_H

#include <x10/io/OutputStreamWriter__OutputStream.h>

#include <cstdarg>
#include <stdio.h>

namespace x10 {

    namespace io {

        class FileWriter__FileOutputStream : public ::x10::io::OutputStreamWriter__OutputStream {
        protected:
            FILE* FMGL(file);
            
        public:
            RTT_H_DECLS_CLASS;

            FileWriter__FileOutputStream(FILE *f): FMGL(file)(f) { }
            FileWriter__FileOutputStream(): FMGL(file)(NULL) { }

            static FileWriter__FileOutputStream* _make(::x10::lang::String* name, bool append);

            void _constructor (::x10::lang::String* file, bool append);
            void _constructor (FILE* file);

            virtual void write(const char *str);

            virtual void write(x10_int i);

            virtual void write(::x10::lang::String* s); 

            virtual void write(::x10::lang::Rail<x10_byte>* b, x10_long off, x10_long len);
           
            virtual void flush();

            virtual void close();

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
