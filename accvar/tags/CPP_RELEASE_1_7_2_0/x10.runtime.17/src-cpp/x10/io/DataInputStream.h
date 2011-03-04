#ifndef X10_IO_DATAINPUTSTREAM_H
#define X10_IO_DATAINPUTSTREAM_H

#include <x10/io/FilterInputStream.h>

namespace x10 {

    namespace io {

        // java.io.DataInputStream
        class DataInputStream : public FilterInputStream {


        protected:
            virtual const x10_runtime_type _type() const {
                return TYPEID(*this,"java::io::DataInputStream");
            }

        public:

            explicit DataInputStream(const x10aux::ref<InputStream>& _in)
              : FilterInputStream(_in) { }

            x10_boolean readBoolean() { return in->read() != 0; }

            x10_byte readByte() { return (x10_byte)(in->read() & 0xFF); }

            x10_int readUnsignedByte() { return in->read() & 0xFF; }

            x10_short readShort() {
                return (x10_short)
                    ((in->read()&0xFF)<<8 |
                     (in->read()&0xFF));
            }

            x10_int readUnsignedShort() {
                return
                    (in->read()&0xFF)<<8 |
                    (in->read()&0xFF);
            }

            x10_char readChar() {
                return
                    (in->read()<<8 |
                     (in->read()&0xFF));
            }

            x10_int readInt() {
                return
                    (in->read()&0xFF)<<24 |
                    (in->read()&0xFF)<<16 |
                    (in->read()&0xFF)<<8 |
                    (in->read()&0xFF)<<0;
            }

            x10_long readLong() {
                return
                    ((x10_long)(in->read()&0xFF))<<56 |
                    ((x10_long)(in->read()&0xFF))<<48 |
                    ((x10_long)(in->read()&0xFF))<<40 |
                    ((x10_long)(in->read()&0xFF))<<32 |
                    ((x10_long)(in->read()&0xFF))<<24 |
                    ((x10_long)(in->read()&0xFF))<<16 |
                    ((x10_long)(in->read()&0xFF))<<8 |
                    ((x10_long)(in->read()&0xFF))<<0;
            }

            x10_float readFloat() {
                return java::lang::Float::intBitsToFloat(this->readInt());
            }

            x10_double readDouble() {
                return java::lang::Double::longBitsToDouble(this->readLong());
            }

            x10aux::ref<x10::lang::String> readLine();

            void readFully(const x10aux::ref<x10::array<x10_byte> >& b);

            void readFully(const x10aux::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len);

            x10aux::ref<x10::lang::String> readUTF();

        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
