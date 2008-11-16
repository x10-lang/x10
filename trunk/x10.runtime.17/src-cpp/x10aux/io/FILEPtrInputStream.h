#ifndef X10AUX_IO_FILEPTRINPUTSTREAM_H
#define X10AUX_IO_FILEPTRINPUTSTREAM_H

#include <x10aux/io/FILEPtrStream.h>

namespace x10aux {

    namespace io {

        class FILEPtrInputStream : public FILEPtrStream {

        protected:

            char* gets(char* s, int num);

        public:

            explicit FILEPtrInputStream(FILE* stream)
              : FILEPtrStream(stream) { }

            x10_int read(const x10aux::ref<x10::lang::Rail<x10_byte> > &b,
                         x10_int off, x10_int len);

            x10_int read();
        };

    }
}

#endif
