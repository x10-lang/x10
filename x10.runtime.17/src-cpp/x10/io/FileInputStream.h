#ifndef X10_IO_FILEPTRINPUTSTREAM_H
#define X10_IO_FILEPTRINPUTSTREAM_H

#include <x10/io/FILEPtrInputStream.h>

namespace x10 {

    namespace io {

        class FileInputStream : public FILEPtrInputStream {

        protected:
            virtual const x10_runtime_type _type() const {
                return TYPEID(*this,"java::io::FileInputStream");
            }


        public:
            explicit FileInputStream(const x10aux::ref<x10::lang::String>& name)
              : FILEPtrInputStream(FILEPtrStream::open_file(name, "r")) { }

        };
    }
}

#endif
