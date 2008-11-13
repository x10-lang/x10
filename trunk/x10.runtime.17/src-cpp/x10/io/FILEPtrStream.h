#ifndef X10_IO_FILEPTRSTREAM_H
#define X10_IO_FILEPTRSTREAM_H

#include <x10/lang/Ref.h>

namespace x10 {

    namespace io {

        // FILEPtrStream
        class FILEPtrStream {
        protected:
            FILE* _stream;
            static FILE* check_stream(FILE* stream);
            explicit FILEPtrStream(FILE* stream) : _stream(check_stream(stream)) { }
        public:
            static FILE* open_file(const x10aux::ref<x10::lang::String>& name, const char* mode);
            void close();
        };

    }
}

#endif
