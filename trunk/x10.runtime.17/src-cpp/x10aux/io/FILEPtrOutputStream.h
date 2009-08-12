#ifndef X10AUX_IO_FILEPTROUTPUTSTREAM_H
#define X10AUX_IO_FILEPTROUTPUTSTREAM_H

#include <x10aux/io/FILEPtrStream.h>
#include <cstdarg>

namespace x10 {
    namespace lang {
        template<class T> class ValRail;
        template<class T> class Rail;
    }
}

namespace x10aux {
    namespace io {

        class FILEPtrOutputStream : public FILEPtrStream {
        public:
            explicit FILEPtrOutputStream(FILE* stream) : FILEPtrStream(stream) { }

            void _vprintf(const char* format, va_list parms);
            void write(const char* s);
            void flush();
            void close();
            void write(x10aux::ref<x10::lang::ValRail<x10_byte> > b, x10_int off, x10_int len);
            void write(x10aux::ref<x10::lang::Rail<x10_byte> > b, x10_int off, x10_int len);
            void write(x10_int b);
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
