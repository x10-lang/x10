#ifndef X10_IO_FILEINPUTSTREAM_H
#define X10_IO_FILEINPUTSTREAM_H

#include <x10/io/NativeInputStream.h>
#include <x10aux/io/FILEPtrInputStream.h>

namespace x10 {

    namespace io {

        class FileInputStream : public x10aux::io::FILEPtrInputStream,
                                public NativeInputStream {

        public:
            class RTT : public x10aux::RuntimeType {
                public: 
                    static RTT* const it;
                    
                    virtual void init() {
                        initParents(1,x10aux::getRTT<NativeInputStream>());
                    }
                    
                    virtual const char *name() const {
                        return "x10.io.FileReader.FileInputStream";
                    }

            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<FileInputStream>();
            }   

            FileInputStream(FILE *f)
              : FILEPtrInputStream(f) { }

            static x10aux::ref<FileInputStream> _make(x10aux::ref<x10::lang::String> name) {
                return new (x10aux::alloc<FileInputStream>())
                    FileInputStream (FILEPtrStream::open_file(name, "r"));
            }

            virtual char * gets(char *buf, int sz) {
                return x10aux::io::FILEPtrInputStream::gets(buf,sz);
            }

            virtual x10_int read() {
                return x10aux::io::FILEPtrInputStream::read();
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
