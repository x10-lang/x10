#ifndef X10_IO_OUTPUTSTREAM_H
#define X10_IO_OUTPUTSTREAM_H

#include <x10/lang/Ref.h>

namespace x10 {

    namespace lang {
        template<class T> class Rail;
        template<class T> class ValRail;
    }

    namespace io {

        class OutputStreamWriter__OutputStream : public x10::lang::Ref {
            public:
            RTT_H_DECLS_CLASS;

            virtual void write(const char* str) = 0;

            public:
            x10aux::ref<OutputStreamWriter__OutputStream> _constructor() {
                this->x10::lang::Ref::_constructor();
                return this;
            }

            virtual void close() { }
            virtual void flush() { }
            virtual void write(x10_int b) = 0;
            virtual void write(x10aux::ref<x10::lang::Rail<x10_byte> > b);
            virtual void write(x10aux::ref<x10::lang::ValRail<x10_byte> > b);
            virtual void write(x10aux::ref<x10::lang::Rail<x10_byte> > b, x10_int off, x10_int len);
            virtual void write(x10aux::ref<x10::lang::ValRail<x10_byte> > b, x10_int off, x10_int len);

            // Serialization
            virtual void _serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m);
            void _deserialize_body(x10aux::deserialization_buffer& buf);
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
