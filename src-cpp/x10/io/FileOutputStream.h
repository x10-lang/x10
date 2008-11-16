#ifndef X10_IO_FILEOUTPUTSTREAM_H
#define X10_IO_FILEOUTPUTSTREAM_H

#include <x10/io/NativeOutputStream.h>
#include <x10aux/io/FILEPtrOutputStream.h>

namespace x10 {

    namespace io {

        class FileOutputStream : public x10aux::io::FILEPtrOutputStream,
                                 public x10::io::NativeOutputStream {

        public:
            class RTT : public x10aux::RuntimeType {
                public: 
                    static const RTT* const it;
                    
                    RTT()
                      : RuntimeType(1,x10aux::getRTT<NativeOutputStream>()) { }
                    
                    virtual std::string name() const {
                        return "x10.io.FileWriter.FileOutputStream";
                    }
                    
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<FileOutputStream>();
            }   

            explicit FileOutputStream(const x10aux::ref<x10::lang::String>& name)
              : FILEPtrOutputStream(FILEPtrStream::open_file(name, "w")) { }

        };
    }
}

#endif
