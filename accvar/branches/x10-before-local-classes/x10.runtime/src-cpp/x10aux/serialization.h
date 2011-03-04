/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#ifndef X10AUX_SERIALIZATION_H
#define X10AUX_SERIALIZATION_H


#include <x10aux/config.h>

#include <x10aux/ref.h>
#include <x10aux/alloc.h>
#include <x10aux/deserialization_dispatcher.h>


/* --------------------- 
 * Serialization support
 * ---------------------
 *
 * There are three separate mechanisms for (de)serialization --
 *
 * 1) Built-in primitives
 *
 * 2) Structs
 *
 * 3) Instances of ref<T>
 *
 *
 * The mechanism (1) copies raw implementation-dependent bytes to/from the stream and we will
 * discuss it no further.
 *
 *
 * The mechanism (2) invokes a static function S::_serialize to write the bytes to the stream,
 * and a static function S::_deserialize to read bytes from the stream and produce a S.
 * These functions must be generated (or hand-written) for every struct S.
 *
 *
 * The mechanism (3) is designed for 4 cases:
 *
 * a) Interfaces (unknown(*) whether they are an Object or a closure)
 *
 * b) null references
 *
 * c) Polymorphic subclasses of Object (whose concrete type is not known at compile time)
 *
 * d) Final subclasses of Object and closures
 *
 *
 * The mechanism (3) invokes a static function T::_serialize whenever a ref<T> is written into
 * a serialization_buffer, and T::_deserialize whenever a ref<T> is read from a
 * serialization_buffer.  The _serialize and _deserialize functions may either be defined
 * in the class or inherited from a superclass.
 *
 * The mechanism is used in exactly the same way by hand-written c++ classes and by c++ code that is
 * generated from X10 classes by the X10 compiler.
 *
 * 'Reference' is a common supertype of all Objects and closures, and is used to represent
 * interface types.
 * 'Reference' declares a static function _serialize which behaves the same way regardless of the
 * concrete type of the target.  Reference::_serialize will emit an interface id (via a virtual
 * function _get_serialization_id) that is unique to each class, and then serialize the object's
 * representation (via another virtual function _serialize_body).  For the special case of a null
 * reference (b) it does not defer to these virtual functions.  Since interfaces do not, in general,
 * define _serialize and _deserialize functions, they are "inherited" from Reference.
 * For all other cases, assuming all classes implement _get_serialization_id and _serialize_body
 * properly, this is sufficient.  All subclasses of Reference, namely closures and Object, must
 * define _serialize and _deserialize functions.
 *
 * Unique ids are generated at runtime in a place-independent fashion.  Classes obtain their id by
 * registering a deserialization function with DeserializationDispatcher at initialization time, and
 * storing this id in a static field.  Every non-abstract class and every closure has its own id.
 * The virtual _get_serialization_id function returns this id for writing into the buffer.
 *
 * To write data (of any kind) we use the method serialization_buffer::write(data) which does the
 * right thing, no matter which of the 3 methods (or which of the 4 categories (a-d)) it is given.
 * An internal cursor is incremented ready for the next write().  Note that a class's
 * _serialize_body function should also serialize its super class's representation, e.g. by
 * deferring to the super class's _serialize_body function.
 *
 * To implement (c) we have Object provide a _serialize_reference that serializes the location and
 * address of the object so that other places can use it as a remote reference.
 *
 * In the case (d) where the object is statically known to be a particular class at deserialization
 * time (e.g. if we are deserializing into a variable whose type is final), we would like to omit
 * the id from the communication, as it is not required.  This is achieved through a final class C
 * providing its own _serialize function that does not write the serialization id to the buffer.
 * The write() function will call C::_serialize() instead of resolving the call to
 * Object::_serialize().  This does not affect the behaviour of _serialize when invoked on an
 * instance of C that has been up-cast to Reference because _serialize is a static function.  In
 * this case the id would still be emitted.  This strategy is used to omit the id in the cases of
 * (b) and (d) above.
 *
 *
 * Deserialization is more complicated as an object has to be constructed.  In the case where we are
 * deserializing into a variable of non-final or interface type, the DeserializationDispatcher is
 * invoked to read an id from the stream and decide what to do.  Note that in such cases, the value
 * has always been serialized from a matching variable on the sending side, so an id will always be
 * present.  During initialization time, classes register deserialization functions with the
 * DeserializationDispatcher, which hands out the unique ids.  Thus the DeserializationDispatcher
 * can look up the id in its internal table and dispatch to the appropriate static function which
 * will construct the right kind of object and initialize it by deserializing the object's
 * representation from the stream (provided as a serialization_buffer).
 *
 * Reference::_deserialize is the complement of Reference::_serialize and defers deserialization to
 * DeserializationDispatcher.  Final classes and structs can provide their own static _deserialize
 * functions that do not use DeserializationDispatcher and assume that no id is found in the stream.
 * Thus classes should either define both _serialize and _deserialize or define neither.
 *
 * Classes define a _deserialize_body function that extracts the object's representation from the
 * stream.  The DeserializationDispatcher callback and _deserialize (if present) should usually just
 * create an object of the right type and then call _deserialize_body to handle the rest.  Arbitrary
 * data can be extracted from a serialization_buffer via its read<T>() function.  An internal cursor
 * is incremented so the buffer is ready for the next read().  This function will do the right thing
 * no matter what T is supplied.  Note that classes need to deserialize their parent class's
 * representation too, e.g. by calling their parent's _deserialize_body function.  The two functions
 * _serialize_body and _deserialize_body are dual, and obviously they should be written to match
 * each other.
 *
 * Deserialization of Object instances is handled through a special function,
 * Object::_deserialize_reference_state, which reads the location and address information from the
 * stream into an instance of Object::_reference_state.  This function must be invoked by all
 * subclasses of Object upon deserialization.  After invoking _deserialize_body, subclasses of
 * Object must call Object::_finalize_reference, which will return either the local address, the
 * constructed remote proxy, or null, as appropriate.  If a subclass of Object wants to override the
 * remote object behavior (i.e., handle remote references itself), the class must override the
 * virtual function _custom_deserialization to return true, which will cause _finalize_reference to
 * ignore the transmitted location information and always return the object produced by the
 * deserialization mechanism.
 *
 * Classes must call buf.record_reference(R) on the deserialization buffer buf right after
 * allocating the object in the DeserializationDispatcher callback (where R is the newly allocated
 * object).
 */


namespace x10 {
    namespace lang {
        class Reference;
    }
}


namespace x10aux {

    // Used to allow us to define 'do-nothing' constructors for classes that already have default
    // constructors.  Currently only used in closures.
    class SERIALIZATION_MARKER { };


    // addr_map can be used to detect and properly handle cycles when serializing object graphs
    // it can also be used to avoid serializing two copies of an object when serializing a DAG.
    class addr_map {
        int _size;
        const void** _ptrs;
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
            _ptrs(new (x10aux::alloc<const void*>((init_size)*sizeof(const void*)))
                      const void*[init_size]),
            _top(0)
        { }
        /* Returns 0 if the pointer has not been recorded yet */
        template<class T> int previous_position(const ref<T>& r) {
            int pos = _position((void*) r.operator->());
            if (pos == 0) {
                _S_("\t\tRecorded new reference "<<((void*) r.operator->())<<" of type "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" at "<<(_top+pos-1)<<" (absolute) in map: "<<this);
            } else {
                _S_("\t\tFound repeated reference "<<((void*) r.operator->())<<" of type "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" at "<<(_top+pos)<<" (absolute) in map: "<<this);
            }
            return pos;
        }
        template<class T> ref<T> get_at_position(int pos) {
            T* val = (T*)_get(pos);
            _S_("\t\tRetrieving repeated reference "<<((void*) val)<<" of type "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" at "<<(_top+pos)<<" (absolute) in map: "<<this);
            return ref<T>(val);
        }
        template<class T> ref<T> set_at_position(int pos, ref<T> newval) {
            T* val = (T*)_set(pos, newval.operator->());
            _S_("\t\tReplacing repeated reference "<<((void*) val)<<" of type "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" at "<<(_top+pos)<<" (absolute) in map: "<<this<<" by "<<((void*) newval.operator->()));
            return ref<T>(val);
        }
        void reset() { _top = 0; assert (false); }
        ~addr_map() { x10aux::dealloc(_ptrs); }
    };



    // Endian encoding/decoding support
    template<class T> void code_bytes(T *x) {
        (void) x;
        #if defined(__i386__) || defined(__x86_64__)
        unsigned char *buf = (unsigned char*) x;
        for (int i=0,j=sizeof(T)-1 ; i<j ; ++i,--j) {
            std::swap(buf[i], buf[j]);
        }
        #endif
    }


    // A growable buffer for serializing into
    class serialization_buffer {
    private:
        char *buffer;
        char *limit;
        char *cursor;
        addr_map map;

    public:

        serialization_buffer (void);

        ~serialization_buffer (void) {
            if (buffer!=NULL) {
                std::free(buffer);
            }
        }

        void grow (void);

        size_t length (void) { return cursor - buffer; }
        size_t capacity (void) { return limit - buffer; }

        char *steal() { char *buf = buffer; buffer = NULL; return buf; }
        char *borrow() { return buffer; }

        // Default case for primitives and other things that never contain pointers
        template<class T> struct Write;
        template<class T> struct Write<ref<T> >;
        template<typename T> void write(const T &val);

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
        memcpy(buf.cursor, &val, sizeof(TYPE)); \
        code_bytes((TYPE*)buf.cursor); \
        buf.cursor += sizeof(TYPE); \
    }
    #define PRIMITIVE_WRITE(TYPE) \
    template<> inline void serialization_buffer::Write<TYPE>::_(serialization_buffer &buf, \
                                                                const TYPE &val) {\
        _S_("Serializing "<<star_rating<TYPE>()<<" a "<<ANSI_SER<<TYPENAME(TYPE)<<ANSI_RESET<<": " \
                          <<val<<" into buf: "<<&buf); \
        /* *(TYPE*) buf.cursor = val; // Cannot do this because of alignment */ \
        if (buf.cursor + sizeof(TYPE) >= buf.limit) buf.grow(); \
        memcpy(buf.cursor, &val, sizeof(TYPE)); \
        code_bytes((TYPE*)buf.cursor); \
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
    //PRIMITIVE_WRITE(x10_addr_t) // already defined above
    //PRIMITIVE_WRITE(remote_ref)
    
    // Case for references e.g. ref<Reference>, 
    template<class T> struct serialization_buffer::Write<ref<T> > {
        static void _(serialization_buffer &buf, ref<T> val);
    };
    template<class T> void serialization_buffer::Write<ref<T> >::_(serialization_buffer &buf,
                                                                   ref<T> val) {
        _S_("Serializing a "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" into buf: "<<&buf);
        // FIXME: [IP] the code below happens to work for closures now because they are always
        // serialized via the appropriate function interface.  If they are ever serialized
        // via their exact type, this code will break (if the first captured variable in
        // the closure happens to have a value of -1).
        if (!val.isNull()) {
            int pos = buf.map.previous_position(val);
            if (pos != 0) {
                _S_("\tRepeated ("<<pos<<") serialization of a "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" into buf: "<<&buf);
                buf.write((x10_uint) 0xFFFFFFFF);
                buf.write((x10_int) pos);
                return;
            }
        }
        // Depends what T is (interface/Object/Closure)
        T::_serialize(val,buf);
    }
    
    template<typename T> void serialization_buffer::write(const T &val) {
        Write<T>::_(*this,val);
    }


    // A buffer from which we can deserialize x10 objects
    class deserialization_buffer {
    private:
        const char* buffer;
        const char* cursor;
        addr_map map;
    public:

        deserialization_buffer(const char *buffer_)
            : buffer(buffer_), cursor(buffer_), map()
        { }

        size_t consumed (void) { return cursor - buffer; }

        // Default case for primitives and other things that never contain pointers
        template<class T> struct Read;
        template<class T> struct Read<ref<T> >;
        template<typename T> GPUSAFE T read();
        template<typename T> GPUSAFE T peek() {
            const char* saved_cursor = cursor;
            T val = read<T>();
            cursor = saved_cursor;
            return val;
        }
        // This has to be called every time a remote reference is created, but
        // before the rest of the object is deserialized!
        template<typename T> bool record_reference(ref<T> r);

        template<typename T> void update_reference(ref<T> r, ref<T> newr);

        // So it can access the addr_map
        template<class T> friend struct Read;
    };

    template<typename T> bool deserialization_buffer::record_reference(ref<T> r) {
        int pos = map.previous_position(r);
        if (pos != 0) {
            _S_("\t"<<ANSI_SER<<ANSI_BOLD<<"OOPS!"<<ANSI_RESET<<" Attempting to repeatedly record a reference "<<((void*)r.operator->())<<" (already found at position "<<pos<<") in buf: "<<this);
        }
        return !pos;
    }

    template<typename T> void deserialization_buffer::update_reference(ref<T> r, ref<T> newr) {
        int pos = map.previous_position(r);
        if (pos == 0) {
            _S_("\t"<<ANSI_SER<<ANSI_BOLD<<"OOPS!"<<ANSI_RESET<<" Attempting to update a nonexistent reference "<<((void*)r.operator->())<<" in buf: "<<this);
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
        // Dispatch because we don't know what it is
        return T::_deserialize(buf);
    }

    // Specializations for all simple primitives
    #define PRIMITIVE_READ_AS_INT(TYPE) \
    template<> inline TYPE deserialization_buffer::Read<TYPE>::_(deserialization_buffer &buf) { \
        /* //TYPE &val = *(TYPE*) buf.cursor; // Cannot do this because of alignment */ \
        TYPE val; \
        memcpy(&val, buf.cursor, sizeof(TYPE)); \
        buf.cursor += sizeof(TYPE); \
        code_bytes(&val); \
        _S_("Deserializing "<<star_rating<TYPE>()<<" a "<<ANSI_SER<<TYPENAME(TYPE)<<ANSI_RESET<<": " \
            <<(int)val<<" from buf: "<<&buf); \
        return val; \
    }
    #define PRIMITIVE_READ(TYPE) \
    template<> inline TYPE deserialization_buffer::Read<TYPE>::_(deserialization_buffer &buf) { \
        /* //TYPE &val = *(TYPE*) buf.cursor; // Cannot do this because of alignment */ \
        TYPE val; \
        memcpy(&val, buf.cursor, sizeof(TYPE)); \
        buf.cursor += sizeof(TYPE); \
        code_bytes(&val); \
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
    //PRIMITIVE_READ(x10_addr_t) // already defined above
    //PRIMITIVE_READ(remote_ref)

    // Case for references e.g. ref<Reference>, 
    template<class T> struct deserialization_buffer::Read<ref<T> > {
        GPUSAFE static ref<T> _(deserialization_buffer &buf);
    };
    template<class T> ref<T> deserialization_buffer::Read<ref<T> >::_(deserialization_buffer &buf) {
        _S_("Deserializing a "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" from buf: "<<&buf);
        // FIXME: [IP] the code below happens to work for closures now because they are always
        // serialized via the appropriate function interface.  If they are ever serialized
        // via their exact type, this code will break (if the first captured variable in
        // the closure happens to have a value of -1).
        x10_uint code = buf.peek<x10_uint>();
        if (code == (x10_uint) 0xFFFFFFFF) {
            buf.read<x10_uint>();
            int pos = (int) buf.read<x10_int>();
            _S_("\tRepeated ("<<pos<<") deserialization of a "<<ANSI_SER<<ANSI_BOLD<<TYPENAME(T)<<ANSI_RESET<<" from buf: "<<&buf);
            return buf.map.get_at_position<T>(pos);
        }
        // Dispatch because we don't know what it is
        return T::template _deserialize<T>(buf);
    }

    template<typename T> GPUSAFE T deserialization_buffer::read() {
        return Read<T>::_(*this);
    }
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100

