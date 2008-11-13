#ifndef X10_IO_FILEPTRINPUTSTREAM_H
#define X10_IO_FILEPTRINPUTSTREAM_H

#include <x10/io/InputStream.h>
#include <x10/io/FILEPtrStream.h>

namespace x10 {

    namespace io {

        class FILEPtrInputStream : public FILEPtrStream {

        protected:

            char* gets(char* s, int num);

            virtual const x10_runtime_type _type() const {
                return TYPEID(*this,"java::io::FILEPtrInputStream");
            }

        public:

            explicit FILEPtrInputStream(FILE* stream)
              : InputStream(), FILEPtrStream(stream) { }

            x10_int read(const x10aux::ref<x10::lang::Rail<x10_byte> > &b,
                         x10_int off, x10_int len);

            x10_int read();
        };

    }
}

#endif
