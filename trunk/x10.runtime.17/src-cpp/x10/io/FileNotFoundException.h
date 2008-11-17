#ifndef X10_IO_FILENOTFOUNDEXCEPTION_H
#define X10_IO_FILENOTFOUNDEXCEPTION_H

#include <x10aux/config.h>
#include <x10aux/RTT.h>

#include <x10/io/IOException.h>

namespace x10 {

    namespace io {

        class FileNotFoundException : public IOException {
        public:
            class RTT : public x10aux::RuntimeType { 
                public:
                static RTT* const it; 
            
                virtual void init() {
                    initParents(1,x10aux::getRTT<IOException>());
                }
                
                virtual std::string name() const {
                    return "x10.io.FileNotFoundException";
                }

            };

/* TODO: initialise name
            FileNotFoundException()
              : {
                //
            }
*/

            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<FileNotFoundException>();
            }

        protected:
            x10aux::ref<x10::lang::String> FMGL(name);

        };

    }
}


#endif
