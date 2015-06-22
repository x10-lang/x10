/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef X10AUX_DESERIALIZATION_BUFFER_H
#define X10AUX_DESERIALIZATION_BUFFER_H

#include <x10aux/config.h>
#include <x10aux/captured_lval.h>
#include <x10aux/deserialization_dispatcher.h>
#include <x10/lang/Complex.h>

class Serializer;
namespace x10 { namespace lang { template<class T> class Rail; } }
namespace x10 { namespace io { class Serializer; class InputStreamReader; } }
namespace x10 { namespace lang { class Any; } }

namespace x10aux {

    // A buffer from which we can deserialize x10 objects
    class deserialization_buffer {
    private:
        char* buffer;
        const char* cursor;
        addr_map map;
        size_t len;
    public:
        static const serialization_id_t CUSTOM_SERIALIZATION_END = 0x4abe;
        
        char *getBuffer() { return buffer; }
        size_t getLen() { return len; }

        deserialization_buffer() : buffer(NULL), cursor(NULL), map(), len(0) {}
        
        deserialization_buffer(char *buffer_, size_t len_)
            : buffer(buffer_), cursor(buffer_), map(), len(len_)
        { }

        void _constructor(::x10::io::Serializer*);

        void _constructor(::x10::lang::Rail<x10_byte>*);
        
        void _constructor(::x10::io::InputStreamReader*);

        size_t consumed (void) { return cursor - buffer; }

        static ::x10::lang::Reference* deserialize_reference(deserialization_buffer &buf);
        static void copyOut(deserialization_buffer &buf, void* data, x10_long length, size_t sizeOfT);
        
        // Default case for primitives and other things that never contain pointers
        template<class T> struct Read;
        template<class T> struct Read<T*>;
        template<typename T> GPUSAFE T read();
        template<typename T> GPUSAFE T peek() {
            const char* saved_cursor = cursor;
            T val = read<T>();
            cursor = saved_cursor;
            return val;
        }
        // This has to be called every time a reference is created, but
        // before the rest of the object is deserialized!
        template<typename T> bool record_reference(T* r);

        template<typename T> void update_reference(T* r, T* newr);

        ::x10::lang::Any* readAny();
        
        // So it can access the addr_map
        template<class T> friend struct Read;
    };

    template<typename T> bool deserialization_buffer::record_reference(T* r) {
        int pos = map.previous_position(r);
        if (pos != 0) {
            _S_("\t"<<ANSI_SER<<ANSI_BOLD<<"OOPS!"<<ANSI_RESET<<" Attempting to repeatedly record a reference "<<((void*)r)<<" (already found at position "<<pos<<") in buf: "<<this);
        }
        return !pos;
    }

    template<typename T> void deserialization_buffer::update_reference(T* r, T* newr) {
        int pos = map.previous_position(r);
        if (pos == 0) {
            _S_("\t"<<ANSI_SER<<ANSI_BOLD<<"OOPS!"<<ANSI_RESET<<" Attempting to update a nonexistent reference "<<((void*)r)<<" in buf: "<<this);
        }
        map.set_at_position(pos, newr);
    }
    
    // Case for non-refs (includes simple primitives like x10_int and all structs)
    template<class T> struct deserialization_buffer::Read {
        GPUSAFE static T _(deserialization_buffer &buf);
    };
    // General case for structs
    template<class T> T deserialization_buffer::Read<T>::_(deserialization_buffer &buf) {
        _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" from buf: "<<&buf);
        // Call the struct's static deserialization method
        return T::_deserialize(buf);
    }

    inline void deserialization_buffer::copyOut(deserialization_buffer &buf,
                                                void *data,
                                                x10_long length,
                                                size_t sizeOfT) {
        copy_bulk(data, buf.cursor, length, sizeOfT);
        buf.cursor += length * sizeOfT;
    }

    // Specializations for all simple primitives
    #define PRIMITIVE_READ_AS_INT(TYPE) \
    template<> inline TYPE deserialization_buffer::Read<TYPE>::_(deserialization_buffer &buf) { \
        /* //TYPE &val = *(TYPE*) buf.cursor; // Cannot do this because of alignment */ \
        TYPE val; \
        copy_bytes(&val, buf.cursor, sizeof(TYPE)); \
        buf.cursor += sizeof(TYPE); \
        _S_("Deserializing "<<star_rating<TYPE>()<<" a "<<ANSI_SER<<TYPENAME(TYPE)<<ANSI_RESET<<": " \
            <<(int)val<<" from buf: "<<&buf); \
        return val; \
    }
    #define PRIMITIVE_READ(TYPE) \
    template<> inline TYPE deserialization_buffer::Read<TYPE>::_(deserialization_buffer &buf) { \
        /* //TYPE &val = *(TYPE*) buf.cursor; // Cannot do this because of alignment */ \
        TYPE val; \
        copy_bytes(&val, buf.cursor, sizeof(TYPE)); \
        buf.cursor += sizeof(TYPE); \
        _S_("Deserializing "<<star_rating<TYPE>()<<" a "<<ANSI_SER<<TYPENAME(TYPE)<<ANSI_RESET<<": " \
            <<val<<" from buf: "<<&buf); \
        return val; \
    }
    PRIMITIVE_READ(x10_boolean)
    PRIMITIVE_READ_AS_INT(x10_byte)
    PRIMITIVE_READ_AS_INT(x10_ubyte)
    PRIMITIVE_READ(x10_char)
    PRIMITIVE_READ(x10_short)
    PRIMITIVE_READ(x10_ushort)
    PRIMITIVE_READ(x10_int)
    PRIMITIVE_READ(x10_uint)
    PRIMITIVE_READ(x10_long)
    PRIMITIVE_READ(x10_ulong)
    PRIMITIVE_READ(x10_float)
    PRIMITIVE_READ(x10_double)
    PRIMITIVE_READ(x10_complex)

    // Case for references e.g. Reference*, 
    template<class T> struct deserialization_buffer::Read<T*> {
        GPUSAFE static T* _(deserialization_buffer &buf);
    };
    template<class T> T* deserialization_buffer::Read<T*>::_(deserialization_buffer &buf) {
        _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" from buf: "<<&buf);
        ::x10aux::serialization_id_t code = buf.peek< ::x10aux::serialization_id_t>();
        if (code == (::x10aux::serialization_id_t) 0xFFFF) {
            buf.read< ::x10aux::serialization_id_t>();
            int pos = (int) buf.read<x10_int>();
            _S_("\tRepeated ("<<pos<<") deserialization of a "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" from buf: "<<&buf);
            return buf.map.get_at_position<T>(pos);
        }
        // Deserialize it
        return reinterpret_cast<T*>(deserialize_reference(buf));
    }

    // Case for captured stack addresses, captured_ref_lval<T> and captured_struct_lval<T>
    template<class T> struct deserialization_buffer::Read<captured_ref_lval<T> > {
        GPUSAFE static captured_ref_lval<T> _(deserialization_buffer &buf);
    };
    template<class T> captured_ref_lval<T> deserialization_buffer::Read<captured_ref_lval<T> >::_(deserialization_buffer &buf) {
        _S_("Deserializing a stack variable of type ref<"<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<"> from buf: "<<&buf);
        x10_long addr = buf.read<x10_long>();
        _S_("\tCaptured address is "<<((void*)addr));
        ::x10aux::captured_ref_lval<T> result;
        result.setCapturedAddress(addr);
        return result;
    }
    template<class T> struct deserialization_buffer::Read<captured_struct_lval<T> > {
        GPUSAFE static captured_struct_lval<T> _(deserialization_buffer &buf);
    };
    template<class T> captured_struct_lval<T> deserialization_buffer::Read<captured_struct_lval<T> >::_(deserialization_buffer &buf) {
        _S_("Deserializing a stack variable of type "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" from buf: "<<&buf);
        x10_long addr = buf.read<x10_long>();
        _S_("\tCaptured address is "<<((void*)addr));
        ::x10aux::captured_struct_lval<T> result;
        result.setCapturedAddress(addr);
        return result;
    }

    
    template<typename T> GPUSAFE T deserialization_buffer::read() {
        return Read<T>::_(*this);
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
