#ifndef X10AUX_IO_FILEPTRSTREAM_H
#define X10AUX_IO_FILEPTRSTREAM_H

#include <stdio.h>

#include <x10aux/ref.h>

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
            static FILE* open_file(const x10aux::ref<x10::lang::String>& name,
                                   const char* mode);
            void close();
        };

    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
