#ifndef X10_IO_NATIVEINPUTSTREAM_H
#define X10_IO_NATIVEINPUTSTREAM_H

#include <x10/lang/Ref.h>

namespace x10 {

    namespace lang {
        template<class T> class Rail;
    }

    namespace io {

        class InputStreamReader__InputStream : public x10::lang::Ref {
        public:
            RTT_H_DECLS_CLASS;

        protected:


            virtual char* gets(char* s, int num) = 0;


        public:

            virtual void close() { }

            virtual x10_int read() = 0;

            virtual x10_int read(x10aux::ref<x10::lang::Rail<x10_byte> > b);

            virtual x10_int read(x10aux::ref<x10::lang::Rail<x10_byte> > b,
                                 x10_int off,
                                 x10_int len);
            
            virtual x10_int available() { return 0; }

            virtual void skip(x10_int) = 0;

            virtual void mark(x10_int) { };

            virtual void reset() { }

            virtual x10_boolean markSupported() { return false; }

            // Serialization
            virtual void _serialize_body(x10aux::serialization_buffer& buf, x10aux::addr_map& m);
            void _deserialize_body(x10aux::deserialization_buffer& buf);
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
