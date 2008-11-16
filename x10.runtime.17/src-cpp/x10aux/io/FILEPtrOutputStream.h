#ifndef X10AUX_IO_FILEPTROUTPUTSTREAM_H
#define X10AUX_IO_FILEPTROUTPUTSTREAM_H

#include <x10aux/io/FILEPtrStream.h>

namespace x10aux {

    namespace io {

        // FILEPtrOutputStream
        class FILEPtrOutputStream : public FILEPtrStream {
        protected:
            void _vprintf(const char* format, va_list parms);
            void write(const char* s);
        public:
            explicit FILEPtrOutputStream(FILE* stream)
                : FILEPtrStream(stream) { }
            void flush();
            void write(const x10aux::ref<x10::lang::Rail<x10_byte> >& b, x10_int off, x10_int len);
            void write(x10_int b);
            //friend class PrintStream;
        };
    }
}

#endif
