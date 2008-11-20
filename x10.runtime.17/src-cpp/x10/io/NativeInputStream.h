#ifndef X10_IO_NATIVEINPUTSTREAM_H
#define X10_IO_NATIVEINPUTSTREAM_H

#include <x10/lang/Value.h>

namespace x10 {

    namespace lang {
        template<class T> class Rail;
    }

    namespace io {

        class NativeInputStream : public x10::lang::Value {
            public:
            class RTT : public x10aux::RuntimeType {
                public: 
                    static RTT* const it;
                    
                    virtual void init() {
                        initParents(1,x10aux::getRTT<x10::lang::Value>());
                    }
                    
                    virtual std::string name() const {
                        return "x10.io.InputStreamReader.NativeInputStream";
                    }   
                    
            };
            virtual const x10aux::RuntimeType *_type() const {
                return x10aux::getRTT<NativeInputStream>();
            }   


        protected:

            explicit NativeInputStream() : Value() { }

            virtual char* gets(char* s, int num) = 0;


        public:

            virtual void close() { }

            virtual x10_int read() = 0;

            virtual x10_int read(x10aux::ref<x10::lang::Rail<x10_byte> > b);

            virtual x10_int read(x10aux::ref<x10::lang::Rail<x10_byte> > b,
                                 x10_int off,
                                 x10_int len);

            
            virtual x10_int available() { return 0; }

            virtual void skip(x10_int) = 0;

            virtual void mark(x10_int) { };

            virtual void reset() { }

            virtual x10_boolean markSupported() { return false; }

            virtual void _serialize(x10aux::serialization_buffer& buf,
                                    x10aux::addr_map& m) {
                (void) buf;
                (void) m;
                ::fprintf(stderr,"Why are we serializing streams?");
                ::abort();
            }
            virtual void _serialize_fields(x10aux::serialization_buffer& buf,
                                           x10aux::addr_map& m) {
                (void)buf;
                (void)m;
                ::fprintf(stderr,"Why are we serializing streams?");
                ::abort();
            }
            virtual void _deserialize_fields(x10aux::serialization_buffer& buf){
                (void)buf;
                ::fprintf(stderr,"Why are we serializing streams?");
                ::abort();
            }

            //friend class FilterInputStream;
        };

    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
