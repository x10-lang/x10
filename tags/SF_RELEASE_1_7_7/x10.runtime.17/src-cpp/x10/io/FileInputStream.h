#ifndef X10_IO_FILEINPUTSTREAM_H
#define X10_IO_FILEINPUTSTREAM_H

#include <x10/io/NativeInputStream.h>
#include <x10aux/io/FILEPtrInputStream.h>

namespace x10 {

    namespace io {

        class FileInputStream : public x10aux::io::FILEPtrInputStream,
                                public NativeInputStream {

        public:
            RTT_H_DECLS;

            FileInputStream(FILE *f)
              : FILEPtrInputStream(f) { }

            static x10aux::ref<FileInputStream> _make(x10aux::ref<x10::lang::String> name);

            virtual char * gets(char *buf, int sz) {
                return x10aux::io::FILEPtrInputStream::gets(buf,sz);
            }

            virtual void close() {
                x10aux::io::FILEPtrInputStream::close();
            }

            virtual x10_int read() {
                return x10aux::io::FILEPtrInputStream::read();
            }

            virtual x10_int read(x10aux::ref<x10::lang::Rail<x10_byte> > b) {
                return x10::io::NativeInputStream::read(b);
            }

            virtual x10_int read(x10aux::ref<x10::lang::Rail<x10_byte> > b,
                                 x10_int off,
                                 x10_int len) {
                return x10aux::io::FILEPtrInputStream::read(b, off, len);
            }

            virtual void skip(x10_int bytes) {
                return x10aux::io::FILEPtrInputStream::skip(bytes);
            }

            static x10aux::ref<FileInputStream> STANDARD_IN;


        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
