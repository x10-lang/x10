/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#ifndef X10_IO_OUTPUTSTREAM_H
#define X10_IO_OUTPUTSTREAM_H

#include <x10/lang/X10Class.h>

namespace x10 {

    namespace lang {
        template<class T> class Rail;
        class String;
    }

    namespace io {

        class OutputStreamWriter__OutputStream : public ::x10::lang::X10Class {
            public:
            RTT_H_DECLS_CLASS;

            virtual void write(const char* str) = 0;

            public:
            OutputStreamWriter__OutputStream* _constructor() {
                return this;
            }

            virtual void close() { }
            virtual void flush() { }
            virtual void write(x10_int b) = 0;
            virtual void write(::x10::lang::Rail<x10_byte>* b, x10_long off, x10_long len) = 0;
            virtual void write(::x10::lang::String* s) = 0;

            static OutputStreamWriter__OutputStream* STANDARD_OUT();

            static OutputStreamWriter__OutputStream* STANDARD_ERR();

            // Serialization
            virtual void _serialize_body(::x10aux::serialization_buffer& buf);
            void _deserialize_body(::x10aux::deserialization_buffer& buf);
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
