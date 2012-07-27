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

#ifndef X10_IO_OUTPUTSTREAM_H
#define X10_IO_OUTPUTSTREAM_H

#include <x10/lang/Reference.h>

namespace x10 {

    namespace util {
        template<class T> class IndexedMemoryChunk;
    }

    namespace io {

        class OutputStreamWriter__OutputStream : public x10::lang::Reference {
            public:
            RTT_H_DECLS_CLASS;

            virtual void write(const char* str) = 0;

            public:
            x10aux::ref<OutputStreamWriter__OutputStream> _constructor() {
                return this;
            }

            virtual void close() { }
            virtual void flush() { }
            virtual void write(x10_int b) = 0;
            virtual void write(x10::util::IndexedMemoryChunk<x10_byte> b);
            virtual void write(x10::util::IndexedMemoryChunk<x10_byte> b, x10_int off, x10_int len);

            static x10aux::ref<OutputStreamWriter__OutputStream> STANDARD_OUT();

            static x10aux::ref<OutputStreamWriter__OutputStream> STANDARD_ERR();

            // Serialization
            virtual void _serialize_body(x10aux::serialization_buffer& buf);
            void _deserialize_body(x10aux::deserialization_buffer& buf);
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
