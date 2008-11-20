#ifndef X10_IO_FILTERINPUTSTREAM_H
#define X10_IO_FILTERINPUTSTREAM_H

#include <x10/lang/Ref.h>

namespace x10 {

    namespace io {

        // java.io.FilterInputStream
        class FilterInputStream : public java::io::InputStream {
        protected:
            const x10aux::ref<InputStream> in;
            char* gets(char* s, int num) { return in->gets(s, num); }
            virtual const x10_runtime_type _type() const { return TYPEID(*this,"java::io::FilterInputStream"); }
            explicit FilterInputStream(const x10aux::ref<InputStream>& _in) : InputStream(), in(_in) { }
        public:
            void close() { in->close(); }
            x10_int read() { return in->read(); }
            x10_int read(const x10aux::ref<x10::array<x10_byte> >& b) { return in->read(b); }
            x10_int read(const x10aux::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len) { return in->read(b, off, len); }
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
