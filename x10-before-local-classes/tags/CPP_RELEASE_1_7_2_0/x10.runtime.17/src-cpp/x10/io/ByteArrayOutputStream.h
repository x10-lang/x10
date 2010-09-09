#ifndef X10_IO_BYTEARRAYOUTPUTSTREAM_H
#define X10_IO_BYTEARRAYOUTPUTSTREAM_H

#include <x10/lang/Ref.h>

#include <x10/io/OutputStream.h>

namespace x10 {

    namespace io {

        class ByteArrayOutputStream : public OutputStream {
            public:
            class RTT : public x10aux::RuntimeType {
                public: 
                    static RTT* const it;
                    
                    virtual void init() {
                        initParents(0);
                    }
                    
                    virtual std::string name() const {
                        return "x10.io.ByteArrayOutputStream.NativeByteArrayOutputStream";
                    }   
                    
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<ByteArrayOutputStream>();
            }   


            std::string res;


        protected:

            void _vprintf(const char* format, va_list parms);

            void write(const char* str) { res += str; }


        public:

            explicit ByteArrayOutputStream() : OutputStream() { }

            void write(x10_int b) { res += (char)b; }

            int size() { return res.size(); }

            void reset() { res = ""; }

            x10aux::ref<x10::lang::String> toString();


            friend class PrintStream;
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
