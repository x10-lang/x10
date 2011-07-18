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

#ifndef X10AUX_IO_FILEPTRINPUTSTREAM_H
#define X10AUX_IO_FILEPTRINPUTSTREAM_H

#include <x10aux/io/FILEPtrStream.h>

namespace x10 {
    namespace util {
        template <class T> class IndexedMemoryChunk;
    }
}

namespace x10aux {
    namespace io {

        class FILEPtrInputStream : public FILEPtrStream {
        public:
            explicit FILEPtrInputStream(FILE* stream) : FILEPtrStream(stream) { }

            char* gets(char* s, int num);

            x10_int read(x10::util::IndexedMemoryChunk<x10_byte> b,
                         x10_int off, x10_int len);

            void close();

            x10_int read();

            void skip(x10_int bytes);
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
