#ifndef X10_IO_DATAOUTPUTSTREAM_H
#define X10_IO_DATAOUTPUTSTREAM_H

#include <x10/io/FilterOutputStream.h>

namespace x10 {

    namespace io {

        class DataOutputStream : public FilterOutputStream {

        protected:

            virtual const x10_runtime_type _type() const {
                return TYPEID(*this,"java::io::DataOutputStream");
            }


        public:

            explicit DataOutputStream(const x10aux::ref<OutputStream>& _out)
              : FilterOutputStream(_out) { }

            void writeBoolean(x10_boolean v) { out->write(v?1:0); }

            void writeByte(x10_int v) { out->write(v); }

            void writeShort(x10_int v) {
                out->write(v>>8&0xFF);
                out->write(v>>0&0xFF);
            }

            void writeChar(x10_int v) {
                out->write(v>>8&0xFF);
                out->write(v>>0&0xFF);
            }

            void writeInt(x10_int v) {
                out->write(v>>24&0xFF);
                out->write(v>>16&0xFF);
                out->write(v>>8&0xFF);
                out->write(v>>0&0xFF);
            }

            void writeLong(x10_long v) {
                out->write(v>>56&0xFF);
                out->write(v>>48&0xFF);
                out->write(v>>40&0xFF);
                out->write(v>>32&0xFF);
                out->write(v>>24&0xFF);
                out->write(v>>16&0xFF);
                out->write(v>>8&0xFF);
                out->write(v>>0&0xFF);
            }

            void writeFloat(x10_float v) {
                writeInt(x10::lang::Float::floatToIntBits(v));
            }

            void writeDouble(x10_double v) {xi
                 writeLong(java::lang::Double::doubleToLongBits(v));
            }

            void writeBytes(const x10aux::ref<x10::lang::String>& s);

            void writeChars(const x10aux::ref<x10::lang::String>& s);

            void writeUTF(const x10aux::ref<x10::lang::String>& str);

        };
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
