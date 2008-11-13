#ifndef X10_IO_FILEPTROUTPUTSTREAM_H
#define X10_IO_FILEPTROUTPUTSTREAM_H

#include <x10/io/FILEPtrOutputStream.h>

namespace x10 {

    namespace io {

        class NativeFileOutputStream : public FILEPtrOutputStream {

            class RTT : public x10aux::RuntimeType {
                public: 
                    static const RTT* const it;
                    
                    RTT() : RuntimeType() { }
                    
                    virtual std::string name() const {
                        return "x10.io.FileOutputStream.NativeFileOutputStream";
                    }   
                    
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Object>();
            }   

        public:
            explicit FileOutputStream(const x10aux::ref<x10::lang::String>& name)
              : FILEPtrOutputStream(FILEPtrStream::open_file(name, "w")) { }

        };
    }
}

#endif
