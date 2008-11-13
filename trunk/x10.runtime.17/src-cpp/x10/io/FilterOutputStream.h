#ifndef X10_IO_FILTEROUTPUTSTREAM_H
#define X10_IO_FILTEROUTPUTSTREAM_H

#include <x10/io/OutputStream.h>

namespace x10 {

    namespace io {

        class FilterOutputStream : public OutputStream {
            public:
            class RTT : public x10aux::RuntimeType {
                public: 
                    static const RTT* const it;
                    
                    RTT() : RuntimeType() { }
                    
                    virtual std::string name() const {
                        return "x10.io.FilterOutputStream.NativeOutputStream";
                    }   
                    
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<Object>();
            }   

        protected:
            const x10aux::ref<OutputStream> out;
            virtual const x10_runtime_type _type() const { return TYPEID(*this,"java::io::FilterOutputStream"); }
            explicit FilterOutputStream(const x10aux::ref<OutputStream>& _out) : OutputStream(), out(_out) { }
            void _vprintf(const char* format, va_list parms) { out->_vprintf(format, parms); }
            void write(const char* str) { out->write(str); }
        public:
            void close() { out->close(); }
            void flush() { out->flush(); }
            void write(const x10aux::ref<x10::array<x10_byte> >& b) { out->write(b); }
            void write(const x10aux::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len) { out->write(b, off, len); }
            void write(x10_int b) { out->write(b); }
        };
    }
}

#endif
