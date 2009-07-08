#ifndef X10_IO_FILEOUTPUTSTREAM_H
#define X10_IO_FILEOUTPUTSTREAM_H

#include <x10/io/OutputStreamWriter__OutputStream.h>
#include <x10aux/io/FILEPtrOutputStream.h>

namespace x10 {

    namespace io {

        class FileWriter__FileOutputStream : public x10aux::io::FILEPtrOutputStream,
                                 public x10::io::OutputStreamWriter__OutputStream {

        public:
            RTT_H_DECLS;

            FileWriter__FileOutputStream(FILE *f)
              : FILEPtrOutputStream(f) { }

            static x10aux::ref<FileWriter__FileOutputStream> _make(x10aux::ref<x10::lang::String> name);
            
            virtual void write(const char *str) {
                x10aux::io::FILEPtrOutputStream::write(str);
            }

            virtual void write(x10_int i) {
                x10aux::io::FILEPtrOutputStream::write(i);
            }

            virtual void write(x10aux::ref<x10::lang::Rail<x10_byte> > b)
            { x10::io::OutputStreamWriter__OutputStream::write(b); }
            virtual void write(x10aux::ref<x10::lang::ValRail<x10_byte> > b)
            { x10::io::OutputStreamWriter__OutputStream::write(b); }
            virtual void write(x10aux::ref<x10::lang::Rail<x10_byte> > b, x10_int off, x10_int len)
            { x10aux::io::FILEPtrOutputStream::write(b, off, len); }
            virtual void write(x10aux::ref<x10::lang::ValRail<x10_byte> > b,x10_int off,x10_int len)
            { x10aux::io::FILEPtrOutputStream::write(b, off, len); }

            virtual void flush() {
                x10aux::io::FILEPtrOutputStream::flush();
            }

            /* [DC] Not sure these are needed now
            x10aux::ref<FileWriter__FileOutputStream> _constructor(x10aux::ref<x10::lang::String> name)
            { return this->FILEPtrOutputStream::_constructor(FILEPtrStream::open_file(name, "w")); }

            x10aux::ref<FileWriter__FileOutputStream> _constructor(FILE *file)
            { return this->FILEPtrOutputStream::_constructor(file); }
            */

            static x10aux::ref<FileWriter__FileOutputStream> STANDARD_OUT;

            static x10aux::ref<FileWriter__FileOutputStream> STANDARD_ERR;

        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
