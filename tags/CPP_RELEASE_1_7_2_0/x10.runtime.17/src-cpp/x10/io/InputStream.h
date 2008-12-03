#ifndef X10_IO_INPUTSTREAM_H
#define X10_IO_INPUTSTREAM_H

#include <x10/lang/Ref.h>

namespace x10 {

    namespace io {

        class NativeInputStream : public x10::lang::Ref {
            public:
            class RTT : public x10aux::RuntimeType {
                public: 
                    static RTT* const it;
                    
                    virtual void init() {
                        initParents(1,x10aux::getRTT<x10::lang::Ref>());
                    }
                    
                    virtual std::string name() const {
                        return "x10.io.InputStream.NativeInputStream";
                    }   
                    
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<InputStream>();
            }   


        protected:

            explicit InputStream() : Ref() { }

            virtual const x10_runtime_type _type() const {
                return TYPEID(*this,"java::io::InputStream");
            }

            virtual char* gets(char* s, int num) = 0;


        public:

            virtual void close() { }

            virtual x10_int read() = 0;

            virtual x10_int read(const x10aux::ref<x10::array<x10_byte> >& b);

            virtual x10_int read(const x10aux::ref<x10::array<x10_byte> >& b,
                                 x10_int off,
                                 x10_int len);

            friend class FilterInputStream;
        };

    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
