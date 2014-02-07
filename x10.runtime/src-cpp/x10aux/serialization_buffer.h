/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#ifndef X10AUX_SERIALIZATION_BUFFER_H
#define X10AUX_SERIALIZATION_BUFFER_H

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/network.h>
#include <x10aux/simple_hashmap.h>
#include <x10aux/captured_lval.h>
#include <x10aux/deserialization_dispatcher.h>
#include <x10/lang/Complex.h>

namespace x10 { namespace lang { template<class T> class Rail; } }
namespace x10 { namespace lang { class Any; } }
namespace x10 { namespace io { class OutputStreamWriter; } }


namespace x10aux {
    // addr_map can be used to detect and properly handle cycles when serializing object graphs
    // it can also be used to avoid serializing two copies of an object when serializing a DAG.
    class addr_map {
        int _size;
        const void** _ptrs;
        simple_hashmap<const void*, int> *_ptrPos; // maps pointers to position in the ptr table
        int _top;
        void _grow();
        void _add(const void* ptr);
        int _find(const void* ptr);
        const void* _get(int pos);
        const void* _set(int pos, const void* ptr);
        int _position(const void* p);
    public:
        addr_map(int init_size = 4) :
            _size(init_size),
                // NB: Must call x10aux::alloc here because _ptrs may hold the only live reference
                //     to temporary objects allocated by custom serialization serialize methods.
                //     If we don't keep those objects live here, the storage may be reused during the
                //     serialization operation, resulting in incorrect detection of a repeated reference!
                _ptrs(new (::x10aux::alloc<const void*>((init_size)*sizeof(const void*), true)) const void*[init_size]),
                _ptrPos(new (::x10aux::alloc< ::x10aux::simple_hashmap<const void*, int> >()) ::x10aux::simple_hashmap<const void*, int>()),
            _top(0)
        { }
        /* Returns 0 if the pointer has not been recorded yet */
        template<class T> int previous_position(const T* r) {
            int pos = _position((void*)r);
            if (pos == 0) {
                _S_("\t\tRecorded new reference "<<((void*)r)<<" of type "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" at "<<(_top+pos-1)<<" (absolute) in map: "<<this);
            } else {
                _S_("\t\tFound repeated reference "<<((void*)r)<<" of type "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" at "<<(_top+pos)<<" (absolute) in map: "<<this);
            }
            return pos;
        }
        template<class T> T* get_at_position(int pos) {
            T* val = (T*)_get(pos);
            _S_("\t\tRetrieving repeated reference "<<((void*) val)<<" of type "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" at "<<(_top+pos)<<" (absolute) in map: "<<this);
            return val;
        }
        template<class T> T* set_at_position(int pos, T* newval) {
            T* val = (T*)_set(pos, newval);
            _S_("\t\tReplacing repeated reference "<<((void*) val)<<" of type "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" at "<<(_top+pos)<<" (absolute) in map: "<<this<<" by "<<((void*) newval));
            return val;
        }
        void reset() { _top = 0; assert (false); }
        ~addr_map() { ::x10aux::dealloc(_ptrs); ::x10aux::dealloc(_ptrPos); }
    };



    // copy bytes with Endian encoding/decoding support
    inline void copy_bytes(void *dest, const void *src, size_t n) {
        #if !defined(HOMOGENEOUS) && (defined(__i386__) || defined(__x86_64__))
        unsigned char *x = (unsigned char*) dest;
        unsigned char *y = (unsigned char*) src;
        for (size_t i=0,j=n-1; i<n; ++i,--j) {
            x[i] = y[j];
        }
        #else
        memcpy(dest, src, n);
        #endif
    }

    // copy bulk data of size n of a given length, with Endian encoding/decoding support
    inline void copy_bulk(void *dest, const void *src, x10_long length, size_t n) {
        #if !defined(HOMOGENEOUS) && (defined(__i386__) || defined(__x86_64__))
        unsigned char *x = (unsigned char*) dest;
        unsigned char *y = (unsigned char*) src;
        for (x10_long k=0; k<length; k++) {
            for (size_t i=0,j=n-1; i<n; ++i,--j) {
                x[i] = y[j];
            }
            x += n; y += n;
        }
        #else
        memcpy(dest, src, length*n);
        #endif
    }

    // A growable buffer for serializing into
    class serialization_buffer {
    private:
        char *buffer;
        char *limit;
        char *cursor;
        addr_map map;

        void grow (size_t new_capacity);

    public:

        serialization_buffer (void) : buffer(NULL), limit(NULL), cursor(NULL), map() {}

        ~serialization_buffer (void) {
            if (buffer!=NULL) {
                ::x10aux::dealloc(buffer);
            }
        }

        void _constructor() {}
        void _constructor(::x10::io::OutputStreamWriter*);
        
        void grow (void);

        size_t length (void) { return cursor - buffer; }
        size_t capacity (void) { return limit - buffer; }
        x10_int dataBytesWritten(void) { return (x10_int)length(); }

        char *steal() { char *buf = buffer; buffer = NULL; return buf; }
        char *borrow() { return buffer; }

        static void serialize_reference(serialization_buffer &buf, ::x10::lang::Reference*);
        static void copyIn(serialization_buffer &buf, const void* data, x10_long length, size_t sizeOfT);

        template <class T> void manually_record_reference(T* val) {
            map.previous_position(val);
        }

        ::x10::lang::Rail<x10_byte>* toRail();
        
        void newObjectGraph();

        void addDeserializeCount(long extraCount);

        // Default case for primitives and other things that never contain pointers
        template<class T> struct Write;
        template<class T> struct Write<T*>;
        template<class T> struct Write<captured_ref_lval<T> >;
        template<class T> struct Write<captured_struct_lval<T> >;
        template<typename T> void write(const T &val);

        void writeAny(::x10::lang::Any* val);
        
        // So it can access the addr_map
        template<class T> friend struct Write;
    };

    
    // Case for non-refs (includes simple primitives like x10_int and all structs)
    template<class T> struct serialization_buffer::Write {
        static void _(serialization_buffer &buf, const T &val);
    };
    // General case for structs
    template<class T> void serialization_buffer::Write<T>::_(serialization_buffer &buf,
                                                             const T &val) {
        _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" into buf: "<<&buf);
        T::_serialize(val,buf);
    }
    // Specializations for the simple primitives
    #define PRIMITIVE_WRITE_AS_INT(TYPE) \
    template<> inline void serialization_buffer::Write<TYPE>::_(serialization_buffer &buf, \
                                                                const TYPE &val) {\
        _S_("Serializing "<<star_rating<TYPE>()<<" a "<<ANSI_SER<<TYPENAME(TYPE)<<ANSI_RESET<<": " \
                          <<(int)val<<" into buf: "<<&buf); \
        /* *(TYPE*) buf.cursor = val; // Cannot do this because of alignment */ \
        if (buf.cursor + sizeof(TYPE) >= buf.limit) buf.grow(); \
        copy_bytes(buf.cursor, &val, sizeof(TYPE)); \
        buf.cursor += sizeof(TYPE); \
    }
    #define PRIMITIVE_WRITE(TYPE) \
    template<> inline void serialization_buffer::Write<TYPE>::_(serialization_buffer &buf, \
                                                                const TYPE &val) {\
        _S_("Serializing "<<star_rating<TYPE>()<<" a "<<ANSI_SER<<TYPENAME(TYPE)<<ANSI_RESET<<": " \
                          <<val<<" into buf: "<<&buf); \
        /* *(TYPE*) buf.cursor = val; // Cannot do this because of alignment */ \
        if (buf.cursor + sizeof(TYPE) >= buf.limit) buf.grow(); \
        copy_bytes(buf.cursor, &val, sizeof(TYPE)); \
        buf.cursor += sizeof(TYPE); \
    }
    #define PRIMITIVE_VOLATILE_WRITE(TYPE) \
    template<> inline void serialization_buffer::Write<volatile TYPE>::_(serialization_buffer &buf, \
                                                                         const volatile TYPE &val) {\
        _S_("Serializing "<<star_rating<TYPE>()<<" a volatile "<<ANSI_SER<<TYPENAME(TYPE)<<ANSI_RESET<<": " \
                          <<val<<" into buf: "<<&buf); \
        /* *(TYPE*) buf.cursor = val; // Cannot do this because of alignment */ \
        if (buf.cursor + sizeof(TYPE) >= buf.limit) buf.grow(); \
        copy_bytes(buf.cursor, const_cast<TYPE*>(&val), sizeof(TYPE)); \
        buf.cursor += sizeof(TYPE); \
    }
    PRIMITIVE_WRITE(x10_boolean)
    PRIMITIVE_WRITE_AS_INT(x10_byte)
    PRIMITIVE_WRITE_AS_INT(x10_ubyte)
    PRIMITIVE_WRITE(x10_char)
    PRIMITIVE_WRITE(x10_short)
    PRIMITIVE_WRITE(x10_ushort)
    PRIMITIVE_WRITE(x10_int)
    PRIMITIVE_WRITE(x10_uint)
    PRIMITIVE_WRITE(x10_long)
    PRIMITIVE_WRITE(x10_ulong)
    PRIMITIVE_WRITE(x10_float)
    PRIMITIVE_WRITE(x10_double)
    PRIMITIVE_WRITE(x10_complex)
        
    PRIMITIVE_VOLATILE_WRITE(x10_int)
    PRIMITIVE_VOLATILE_WRITE(x10_long)

    // Case for references e.g. Reference*
    template<class T> struct serialization_buffer::Write<T*> {
        static void _(serialization_buffer &buf, T* val);
    };
    template<class T> void serialization_buffer::Write<T*>::_(serialization_buffer &buf,
                                                                   T* val) {
        _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" into buf: "<<&buf);
        if (NULL != val) {
            int pos = buf.map.previous_position(val);
            if (pos != 0) {
                _S_("\tRepeated ("<<pos<<") serialization of a "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" into buf: "<<&buf);
                buf.write((::x10aux::serialization_id_t) 0xFFFF);
                buf.write((x10_int) pos);
                return;
            }
        }
        ::x10::lang::Reference* valAsRef = reinterpret_cast< ::x10::lang::Reference*>(val);
        serialize_reference(buf, valAsRef);
    }

    inline void serialization_buffer::copyIn(serialization_buffer &buf,
                                             const void *data,
                                             x10_long length,
                                             size_t sizeOfT) {
        size_t numBytes = length * sizeOfT;
        if (buf.cursor + numBytes >= buf.limit) buf.grow(buf.length() + numBytes);
        copy_bulk(buf.cursor, data, length, sizeOfT);
        buf.cursor += numBytes;
    }

    
    // Case for captured stack variables e.g. captured_ref_lval<T> and captured_struct_lval<T>.
    template<class T> struct serialization_buffer::Write<captured_ref_lval<T> > {
        static void _(serialization_buffer &buf, captured_ref_lval<T> val);
    };
    template<class T> void serialization_buffer::Write<captured_ref_lval<T> >::_(serialization_buffer &buf,
                                                                                 captured_ref_lval<T> val) {
        _S_("Serializing a stack variable of type ref<"<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<"> into buf: "<<&buf);
        x10_long capturedAddress = val.capturedAddress();
        _S_("\tCaptured address is "<<((void*)capturedAddress));
        buf.write(capturedAddress);
    }
    template<class T> struct serialization_buffer::Write<captured_struct_lval<T> > {
        static void _(serialization_buffer &buf, captured_struct_lval<T> val);
    };
    template<class T> void serialization_buffer::Write<captured_struct_lval<T> >::_(serialization_buffer &buf,
                                                                                    captured_struct_lval<T> val) {
        _S_("Serializing a stack variable of type "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" into buf: "<<&buf);
        x10_long capturedAddress = val.capturedAddress();
        _S_("\tCaptured address is "<<((void*)capturedAddress));
        buf.write(capturedAddress);
    }

    
    template<typename T> void serialization_buffer::write(const T &val) {
        Write<T>::_(*this,val);
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
