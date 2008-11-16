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
                    static const RTT* const it;
                    
                    RTT() : RuntimeType() { }

                    virtual std::string name() const {
                        return "x10.io.FileWriter.FileInputStream";
                    }

            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<FileInputStream>();
            }   



        public:
            explicit FileInputStream(const x10aux::ref<x10::lang::String>& name)
              : FILEPtrInputStream(FILEPtrStream::open_file(name, "r")) { }

        };
    }
}

#endif
