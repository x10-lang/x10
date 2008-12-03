#ifndef X10_IO_OUTPUTSTREAM_H
#define X10_IO_OUTPUTSTREAM_H

#include <x10/lang/Ref.h>

namespace x10 {

    namespace lang {
        template<class T> class Rail;
    }

    namespace io {

        class OutputStream : public x10::lang::Ref {
            public:
            class RTT : public x10aux::RuntimeType {
                public:
                    static const RTT* const it;

                    RTT() : RuntimeType() { }

                    virtual std::string name() const {
                        return "x10.io.OutputStream.NativeOutputStream";
                    }

            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<OutputStream>();
            }

            protected:
            explicit OutputStream() : x10::lang::Ref() { }
            void println();
            void print(const x10aux::ref<x10::lang::String>& str);
            void print(x10_boolean b);
            void print(x10_int i);
            void print(x10_long l);
            void print(x10_double d);
            void print(const x10::lang::String& str);
            virtual void _vprintf(const char* format, va_list parms) = 0;
            void _printf(const char* format, ...);
            void printf(const x10aux::ref<x10::lang::String>& format, const x10aux::ref<x10::lang::Rail<x10aux::ref<x10::lang::Object> > >& parms);
            void printf(const x10aux::ref<x10::lang::String>& format);
            virtual void write(const char* str) = 0;

            public:
            virtual void close() { }
            virtual void flush() { }
            virtual void write(const x10aux::ref<x10::lang::Rail<x10_byte> >& b);
            virtual void write(const x10aux::ref<x10::lang::Rail<x10_byte> >& b, x10_int off, x10_int len);
            virtual void write(x10_int b) = 0;
            //friend class FilterOutputStream;
        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
