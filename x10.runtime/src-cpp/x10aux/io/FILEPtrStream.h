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

#ifndef X10AUX_IO_FILEPTRSTREAM_H
#define X10AUX_IO_FILEPTRSTREAM_H

#include <stdio.h>

namespace x10 {
    namespace lang {
        class String;
    }
}

namespace x10aux {
    namespace io {

        class FILEPtrStream {
        protected:
            FILE* _stream;

            static FILE* check_stream(FILE* stream);
            explicit FILEPtrStream(FILE* stream) : _stream(check_stream(stream)) { }

        public:
            static FILE* open_file(x10::lang::String* name,
                                   const char* mode);
            void close();
        };

    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
