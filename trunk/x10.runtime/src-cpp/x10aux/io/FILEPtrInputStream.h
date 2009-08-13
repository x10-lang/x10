#ifndef X10AUX_IO_FILEPTRINPUTSTREAM_H
#define X10AUX_IO_FILEPTRINPUTSTREAM_H

#include <x10aux/io/FILEPtrStream.h>

namespace x10 {
    namespace lang {
        template <class T> class Rail;
    }
}

namespace x10aux {
    namespace io {

        class FILEPtrInputStream : public FILEPtrStream {
        public:
            explicit FILEPtrInputStream(FILE* stream) : FILEPtrStream(stream) { }

            char* gets(char* s, int num);

            x10_int read(const x10aux::ref<x10::lang::Rail<x10_byte> > &b,
                         x10_int off, x10_int len);

            void close();

            x10_int read();

            void skip(x10_int bytes);
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
