#ifndef X10_IO_PRINTSTREAM_H
#define X10_IO_PRINTSTREAM_H

#include <x10/lang/Ref.h>

namespace x10 {

    namespace io {

        // java.io.PrintStream
        class PrintStream : public java::io::FilterOutputStream {
        protected:
            virtual const x10_runtime_type _type() const { return TYPEID(*this,"java::io::PrintStream"); }
        public:
            explicit PrintStream(const x10aux::ref<OutputStream>& _out) : FilterOutputStream(_out) { }
            void println() { OutputStream::println(); }
//            void print(const x10aux::ref<x10::lang::Object>& str);
            void print(const x10aux::ref<x10::lang::String>& str) { OutputStream::print(str); }
            void print(x10_boolean b) { OutputStream::print(b); }
            void print(x10_int i) { OutputStream::print(i); }
            void print(x10_long l) { OutputStream::print(l); }
            void print(x10_double d) { OutputStream::print(d); }
//            template<typename T> void print(T* o);
            void print(const x10::lang::String& str) { OutputStream::print(str); }
//            void println(const x10aux::ref<x10::lang::Object>& str);
            void println(const x10aux::ref<x10::lang::String>& str) { OutputStream::print(str); println(); }
            void println(x10_boolean b) { OutputStream::print(b); println(); }
            void println(x10_int i) { OutputStream::print(i); println(); }
            void println(x10_long l) { OutputStream::print(l); println(); }
            void println(x10_double d) { OutputStream::print(d); println(); }
//            template<typename T> void println(T* o);
            void println(const x10::lang::String& str) { OutputStream::print(str); println(); }
            void printf(const x10aux::ref<x10::lang::String>& format, const x10aux::ref<x10::array<x10aux::ref<x10::lang::Object> > >& parms) {
                _D_("PrintStream::printf(\"" << ((const string&)(*format)).c_str() << "\", ...)");
                OutputStream::printf(format, parms);
            }
            void printf(const x10aux::ref<x10::lang::String>& format) { OutputStream::printf(format); }
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
