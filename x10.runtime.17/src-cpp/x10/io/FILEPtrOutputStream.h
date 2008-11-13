#ifndef X10_IO_FILEPTROUTPUTSTREAM_H
#define X10_IO_FILEPTROUTPUTSTREAM_H

#include <x10/lang/Ref.h>

namespace x10 {

    namespace io {

        // FILEPtrOutputStream
        class FILEPtrOutputStream : public FILEPtrStream {
        protected:
            void _vprintf(const char* format, va_list parms);
            void write(const char* s);
            virtual const x10_runtime_type _type() const { return TYPEID(*this,"java::io::FILEPtrOutputStream"); }
        public:
            explicit FILEPtrOutputStream(FILE* stream) : OutputStream(), FILEPtrStream(stream) { }
            void flush();
            void write(const x10aux::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len);
            void write(x10_int b);
            friend class PrintStream;
        };
    }
}

#endif
