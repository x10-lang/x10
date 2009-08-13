#ifndef X10_IO_OUTPUTSTREAM_H
#define X10_IO_OUTPUTSTREAM_H

#include <x10/lang/Value.h>

namespace x10 {

    namespace lang {
        template<class T> class Rail;
        template<class T> class ValRail;
    }

    namespace io {

        class OutputStreamWriter__OutputStream : public x10::lang::Value {
            public:
            RTT_H_DECLS_CLASS;

            virtual void write(const char* str) = 0;

            public:
            virtual void close() { }
            virtual void flush() { }
            virtual void write(x10_int b) = 0;
            virtual void write(x10aux::ref<x10::lang::Rail<x10_byte> > b);
            virtual void write(x10aux::ref<x10::lang::ValRail<x10_byte> > b);
            virtual void write(x10aux::ref<x10::lang::Rail<x10_byte> > b, x10_int off, x10_int len);
            virtual void write(x10aux::ref<x10::lang::ValRail<x10_byte> > b, x10_int off, x10_int len);

            virtual x10_boolean _struct_equals(x10aux::ref<x10::lang::Object> p0);
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
