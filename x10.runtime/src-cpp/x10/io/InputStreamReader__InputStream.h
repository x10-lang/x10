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

#ifndef X10_IO_NATIVEINPUTSTREAM_H
#define X10_IO_NATIVEINPUTSTREAM_H

#include <x10/lang/X10Class.h>

namespace x10 {

    namespace lang {
        template<class T> class Rail;
    }

    namespace io {

        class InputStreamReader__InputStream : public ::x10::lang::X10Class {
        public:
            RTT_H_DECLS_CLASS;

        protected:


            virtual char* gets(char* s, int num) = 0;


        public:
            InputStreamReader__InputStream* _constructor() {
                return this;
            }
            
            virtual void close() { }

            virtual x10_int read() = 0;

            virtual x10_int read(::x10::lang::Rail<x10_byte>* b);

            virtual x10_int read(::x10::lang::Rail<x10_byte>* b,
                                 x10_int off,
                                 x10_int len);
            
            virtual x10_long available() { return 0L; }

            virtual void skip(x10_long) = 0;

            virtual void mark(x10_long) { };

            virtual void reset() { }

            virtual x10_boolean markSupported() { return false; }

            static InputStreamReader__InputStream* STANDARD_IN();

            // Serialization
            virtual void _serialize_body(::x10aux::serialization_buffer& buf);
            void _deserialize_body(::x10aux::deserialization_buffer& buf);
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
