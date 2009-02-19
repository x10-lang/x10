#ifndef __X10LANG_H
#define __X10LANG_H

/*
 * The following performance macros are supported:
 *   NO_EXCEPTIONS     - remove all exception-related code
 *   NO_BOUNDS_CHECKS  - remove all bounds-checking code
 *   NO_IOSTREAM       - remove all iostream-related code
 *
 *   USE_LONG_ARRAYS   - use 'long' as the type of array indices
 *
 * The following debugging macros are supported:
 *   TRACE_ALLOC       - trace allocation operations
 *   TRACE_CONSTR      - trace object construction
 *   TRACE_REF         - trace reference operations
 *   TRACE_SER         - trace serialization operations
 *   TRACE_X10LIB      - trace X10lib invocations
 *   DEBUG             - general debug trace printouts
 *
 *   REF_STRIP_TYPE    - experimental option: erase the exact content type in references
 *   REF_COUNTING      - experimental option: enable reference counting
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <string>
#ifndef NO_IOSTREAM
#  include <iostream>
#endif
#include <stdint.h>
#include <stdarg.h>
#include <unistd.h>
#include <math.h>
#include <errno.h>
#ifdef __GNUC__
#  include <cxxabi.h>
#endif
#include <x10/x10lib.h>
#define clock _x10_lang_clock
#include <remote_ref.h>

using namespace std;

#if !defined(NO_IOSTREAM) && defined(TRACE_ALLOC)
#define _M_(x) cerr << x10lib::here() << ": MM: " << x << endl
#else
#define _M_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_CONSTR)
#define _T_(x) cerr << x10lib::here() << ": CC: " << x << endl
#else
#define _T_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_REF)
#define _R_(x) cerr << x10lib::here() << ": RR: " << x << endl
#else
#define _R_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_SER)
#define _S_(x) cerr << x10lib::here() << ": SS: " << x << endl
#define _Sd_(x) x
#else
#define _S_(x)
#define _Sd_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(TRACE_X10LIB)
#define _X_(x) cerr << x10lib::here() << ": XX: " << x << endl
#else
#define _X_(x)
#endif

#if !defined(NO_IOSTREAM) && defined(DEBUG)
#define _D_(x) cerr << x10lib::here() << ": " << x << endl
#else
#define _D_(x)
#endif

#ifdef IGNORE_EXCEPTIONS
#define NO_EXCEPTIONS
#endif

#define TYPENAME(T) typeid(T).name()
#ifdef __GNUC__
#define MAX_NAME_LENGTH 512
namespace x10 {
    inline const char* __demangle(const char* N) {
        // FIXME: not thread-safe
        static char buffer[MAX_NAME_LENGTH];
        char* d_N = abi::__cxa_demangle(N, NULL, NULL, NULL);
        strncpy(buffer, d_N, MAX_NAME_LENGTH-1);
        buffer[MAX_NAME_LENGTH-1] = '\0';
        ::free(d_N);
        return (const char*) buffer;
    }
}
#undef MAX_NAME_LENGTH
#define DEMANGLE(N) x10::__demangle(N)
#define TYPEID(T,D) TYPENAME(T)
#else
#define DEMANGLE(N) N
#define TYPEID(T,D) D
#endif

typedef bool     x10_Boolean;
typedef int8_t   x10_Byte;
typedef uint16_t x10_Char;
typedef int16_t  x10_Short;
typedef int32_t  x10_Int;
typedef int64_t  x10_Long;
typedef float    x10_Float;
typedef double   x10_Double;
typedef void     x10_Void;

typedef bool     x10_boolean;
typedef int8_t   x10_byte;
typedef uint16_t x10_char;
typedef int16_t  x10_short;
typedef int32_t  x10_int;
typedef int64_t  x10_long;
typedef float    x10_float;
typedef double   x10_double;
typedef void     x10_void;

// FIXME: **Temporary** Static Harness for compiling simple examples

class x10__Ref { };

// End of: **Temporary** Static Harness for compiling simple examples

// Array index type
#ifdef USE_LONG_ARRAYS
typedef x10_long x10_index_t;
#else
typedef x10_int x10_index_t;
#endif

namespace x10 {
    class __init__;
    template<class T> class ref;
    namespace lang {
        class String;
        class place;
        // x10.lang.Object
        class Object {
#ifdef REF_COUNTING
            int __count; // Ref counting implementation
#endif
        protected:
            const char* _type;
        public:
            explicit Object(const char* _t = NULL) :
#ifdef REF_COUNTING
                __count(0),
#endif
                _type(_t?_t:TYPEID(*this,"x10::lang::Object"))
            {
                _T_("Creating object " << this << " of type " << DEMANGLE(_type));
            }
            virtual ~Object() { _T_("Destroying object " << this << " of type " << DEMANGLE(_type)); }
            template<class T> friend class x10::ref;
            virtual x10_int hashCode() { return 0; }
            virtual x10::ref<String> toString();
            // TODO: toString(), equals()
        };
    }

    // Object allocation
    template<class T> extern T* alloc(size_t size = sizeof(T));
    template<class T> extern void dealloc(T* obj);

    class __ref {
    protected:
#ifndef REF_STRIP_TYPE
        __ref(void* val = NULL) { }
#else
        __ref(void* val = NULL) : _val(val) { }
    public: // [IP] temporary
        void* _val;
#endif
    };
#ifndef REF_STRIP_TYPE
#  define REF_INIT(v) _val(v)
#else
#  define REF_INIT(v) __ref(v)
#endif
#ifdef REF_COUNTING
#  define INC(x) _inc(x)
#  define DEC(x) _dec(x)
#else
#  define INC(x)
#  define DEC(x)
#endif
    template<class T> class ref : public __ref {
#ifdef REF_COUNTING
        void _inc(T* o) { // TODO
            if (o != NULL) {
                o->__count++;
                //_R_("    type=" << TYPEID(o,"null"));
                //_R_("    count=" << o->__count);
            }
        }
        void _dec(T* o) { // TODO
            if (o != NULL) {
                o->__count--;
                //_R_("    type=" << DEMANGLE(TYPENAME(o)));
                //_R_("    count=" << o->__count);
                // TODO
                //if (!o->__count) { o->~T(); dealloc(o); }
            }
        }
#endif
    public:
        ~ref() { DEC(_val); }
        ref(const ref<T>& _ref) : REF_INIT(_ref._val) {
            _R_("Copying reference " << &_ref << "(" << _ref._val << ") of type " << DEMANGLE(TYPENAME(T)) << " to " << this);
            INC(_val);
        }
        // FIXME: something is wrong with the return value; r1 = r2 = r3 doesn't work in xlC
        const ref<T>& operator=(const ref<T>& _ref) {
            _val = _ref._val;
            _R_("Assigning reference " << &_ref << "(" << _ref._val << ") of type " << DEMANGLE(TYPENAME(T)) << " to " << this);
            INC(_val);
            return *this;
        }
        ref(const T* val = NULL) : REF_INIT(const_cast<T*>(val)) {
            INC(_val);
        }
        ref(const T& val);
        template<class S> operator ref<S>() const {
            ref<S> _ref(dynamic_cast<S*>((T*)_val));
            _R_("Casting reference " << this << "(" << _val << ") of type " << DEMANGLE(TYPENAME(T)) << " to type " << DEMANGLE(TYPENAME(S)) << " into " << &_ref);
            return _ref;
        }
        // TODO: find out which one is better -- the cast operator, or the constructor + assignment
//        template<class S> ref(const ref<S>& _ref) : REF_INIT(dynamic_cast<T*>((S*)_ref._val)) {
//            _R_("Casting reference " << &_ref << "(" << _ref._val << ") of type " << DEMANGLE(TYPENAME(S)) << " to type " << DEMANGLE(TYPENAME(T)) << " into " << this);
//            INC(_val);
//        }
//        template<class S> const ref<T>& operator=(const ref<S>& _ref) {
//            _val = dynamic_cast<T*>((S*)_ref._val);
//            _R_("Casting reference " << &_ref << "(" << _ref._val << ") of type " << DEMANGLE(TYPENAME(S)) << " to type " << DEMANGLE(TYPENAME(T)) << " into " << this);
//            INC(_val);
//            return *this;
//        }
        T& operator*() const { // FIXME: throw NullPointerException
            _R_("Accessing object (*) via reference " << this << "(" << _val << ") of type " << DEMANGLE(TYPENAME(T)));
            return *(T*)_val;
        }
        T* operator->() const { // FIXME: should we throw NullPointerException here?
            _R_("Accessing object (->) via reference " << this << "(" << _val << ") of type " << DEMANGLE(TYPENAME(T)));
            return (T*)_val;
        }
        bool isNull() const {
            _R_("Nullcheck reference " << this << "(" << _val << ") of type " << DEMANGLE(TYPENAME(T)));
            return _val == NULL;
        }
        bool operator==(const T* val) const { return _val == val; }
        bool operator==(const ref<T>& _ref) const { return _val == _ref._val; }
        bool operator!=(const T* val) const { return _val != val; }
        bool operator!=(const ref<T>& _ref) const { return _val != _ref._val; }
    private:
#ifndef REF_STRIP_TYPE
        T* _val;
#endif
    };

    template<> inline ref<x10::lang::place>::ref(const x10::lang::place& val);
    template<> ref<x10::lang::String>::ref(const x10::lang::String& val);

#define INSTANCEOF(v,T) (!!((T)(v)).operator->())

//    template<typename T> ref<x10::lang::String> operator+(const ref<x10::lang::String>& s1, const T& v);
    template<class T> ref<x10::lang::String> operator+(const ref<x10::lang::String>& s1, const ref<T>& v);
    template<> ref<x10::lang::String> operator+(const ref<x10::lang::String>& s1, const ref<x10::lang::String>& s2);
    template<class T> class array;

    void x10__assertion_failed(const ref<x10::lang::String>& message);
    inline void x10__assert(x10_boolean val, const ref<x10::lang::String>& message = NULL) {
        if (!val)
            x10__assertion_failed(message);
    }
};
namespace java {
    namespace lang {
        // java.lang.Boolean
        class Boolean : public x10::lang::Object {
            Boolean() { } // Cannot instantiate
        public:
            static x10_boolean parseBoolean(const x10::ref<x10::lang::String>& s);
        };
        // java.lang.Integer
        class Integer : public x10::lang::Object {
            Integer() {} // Cannot instantiate
        public:
            static x10_int parseInt(const x10::ref<x10::lang::String>& s);
	    const static x10_int x10__MIN_VALUE = -(unsigned)(1<<31); 
	    const static x10_int x10__MAX_VALUE = (x10_int) (((unsigned) 1<<31) - 1);;
        };
        // java.lang.Long
        class Long : public x10::lang::Object {
            Long() { } // Cannot instantiate
        public:
            static x10_long parseLong(const x10::ref<x10::lang::String>& s);
        };
        // java.lang.Float
        class Float : public x10::lang::Object {
            Float() { } // Cannot instantiate
        public:
            static x10_float parseFloat(const x10::ref<x10::lang::String>& s);
            static x10_int floatToIntBits(x10_float v);
            static x10_float intBitsToFloat(x10_int v);
        };
        // java.lang.Double
        class Double : public x10::lang::Object {
            Double() { } // Cannot instantiate
        public:
            static x10_long doubleToLongBits(x10_double v);
            static x10_double longBitsToDouble(x10_long v);
        };
    }
    namespace util {
        // java.util.Random
        class Random {
        public:
            Random() { }
            Random(x10_int seed) { ::srand(seed); }  // Assume only one instance
            x10_double nextDouble() { return ::rand()/((x10_double)RAND_MAX+1); }
            x10_boolean nextBoolean() { return ::rand()%2; }
            x10_int nextInt() { return ::rand(); }
            x10_int nextInt(int max) { return ::rand()%max; }
        };

        namespace concurrent {
            namespace atomic  {
                class AtomicIntegerArray  {
                private :
                    x10_int volatile* _data;
                    const x10_int _size;
                    //PosixLock *_locks;
                public :
                    AtomicIntegerArray(x10_int size) : _size(size) {
                        _data = new (x10::alloc<x10_int>(size*sizeof(x10_int))) x10_int[size];
                        //_locks =  new (x10::alloc<PosixLock>(size*sizeof(PosixLock))) PosixLock[size];
                    }
                    x10_int length() {
                        return _size;
                    }
                    void set(x10_int i, x10_int val) {
                        //_locks[i].lock_wait_mutex();
                        _data[i] = val;
                        //_locks[i].lock_signal_posix();
                    }
                    x10_int get(x10_int i) {
                        return _data[i];
                    }
                    ~AtomicIntegerArray() {
                        delete[] _data;
                        //delete _locks;
                    }
                };
            }
        }
    }
    namespace io {
        // java.io.OutputStream
        class OutputStream : public x10::lang::Object {
        protected:
            explicit OutputStream(const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"java::io::OutputStream")) { }
            void println();
            void print(const x10::ref<x10::lang::String>& str);
            void print(x10_boolean b);
            void print(x10_int i);
            void print(x10_long l);
            void print(x10_double d);
            void print(const x10::lang::String& str);
            virtual void _vprintf(const char* format, va_list parms) = 0;
            void _printf(const char* format, ...);
            void printf(const x10::ref<x10::lang::String>& format, const x10::ref<x10::array<x10::ref<x10::lang::Object> > >& parms);
            void printf(const x10::ref<x10::lang::String>& format);
            virtual void write(const char* str) = 0;
        public:
            virtual void close() { }
            virtual void flush() { }
            virtual void write(const x10::ref<x10::array<x10_byte> >& b);
            virtual void write(const x10::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len);
            virtual void write(x10_int b) = 0;
            friend class FilterOutputStream;
        };
        // java.io.ByteArrayOutputStream
        class ByteArrayOutputStream : public java::io::OutputStream {
            string res;
        protected:
            void _vprintf(const char* format, va_list parms);
            void write(const char* str) { res += str; }
        public:
            explicit ByteArrayOutputStream() : OutputStream(TYPEID(*this,"java::io::ByteArrayOutputStream")) { }
            void write(x10_int b) { res += (char)b; }
            int size() { return res.size(); }
            void reset() { res = ""; }
            x10::ref<x10::lang::String> toString();
            friend class PrintStream;
        };
        // FILEPtrStream
        class FILEPtrStream {
        protected:
            FILE* _stream;
            static FILE* check_stream(FILE* stream);
            explicit FILEPtrStream(FILE* stream) : _stream(check_stream(stream)) { }
        public:
            static FILE* open_file(const x10::ref<x10::lang::String>& name, const char* mode);
            void close();
        };
        // FILEPtrOutputStream
        class FILEPtrOutputStream : public java::io::OutputStream, public FILEPtrStream {
        protected:
            void _vprintf(const char* format, va_list parms);
            void write(const char* s);
        public:
            explicit FILEPtrOutputStream(FILE* stream, const char* _t = NULL) : OutputStream(_t?_t:TYPEID(*this,"java::io::FILEPtrOutputStream")), FILEPtrStream(stream) { }
            void flush();
            void write(const x10::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len);
            void write(x10_int b);
            friend class PrintStream;
        };
        // FileOutputStream
        class FileOutputStream : public java::io::FILEPtrOutputStream {
        public:
            explicit FileOutputStream(const x10::ref<x10::lang::String>& name) : FILEPtrOutputStream(FILEPtrStream::open_file(name, "w"), TYPEID(*this,"java::io::FileOutputStream")) { }
        };
        // java.io.FilterOutputStream
        class FilterOutputStream : public java::io::OutputStream {
        protected:
            const x10::ref<OutputStream> out;
            explicit FilterOutputStream(const x10::ref<OutputStream>& _out, const char* _t = NULL) : OutputStream(_t?_t:TYPEID(*this,"java::io::FilterOutputStream")), out(_out) { }
            void _vprintf(const char* format, va_list parms) { out->_vprintf(format, parms); }
            void write(const char* str) { out->write(str); }
        public:
            void close() { out->close(); }
            void flush() { out->flush(); }
            void write(const x10::ref<x10::array<x10_byte> >& b) { out->write(b); }
            void write(const x10::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len) { out->write(b, off, len); }
            void write(x10_int b) { out->write(b); }
        };
        // java.io.DataOutputStream
        class DataOutputStream : public java::io::FilterOutputStream {
        public:
            explicit DataOutputStream(const x10::ref<OutputStream>& _out) : FilterOutputStream(_out, TYPEID(*this,"java::io::DataOutputStream")) { }
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
            void writeFloat(x10_float v) { writeInt(java::lang::Float::floatToIntBits(v)); }
            void writeDouble(x10_double v) { writeLong(java::lang::Double::doubleToLongBits(v)); }
            void writeBytes(const x10::ref<x10::lang::String>& s);
            void writeChars(const x10::ref<x10::lang::String>& s);
            void writeUTF(const x10::ref<x10::lang::String>& str);
        };
        // java.io.PrintStream
        class PrintStream : public java::io::FilterOutputStream {
        public:
            explicit PrintStream(const x10::ref<OutputStream>& _out) : FilterOutputStream(_out, TYPEID(*this,"java::io::PrintStream")) { }
            void println() { OutputStream::println(); }
//            void print(const x10::ref<x10::lang::Object>& str);
            void print(const x10::ref<x10::lang::String>& str) { OutputStream::print(str); }
            void print(x10_boolean b) { OutputStream::print(b); }
            void print(x10_int i) { OutputStream::print(i); }
            void print(x10_long l) { OutputStream::print(l); }
            void print(x10_double d) { OutputStream::print(d); }
//            template<typename T> void print(T* o);
            void print(const x10::lang::String& str) { OutputStream::print(str); }
//            void println(const x10::ref<x10::lang::Object>& str);
            void println(const x10::ref<x10::lang::String>& str) { OutputStream::print(str); println(); }
            void println(x10_boolean b) { OutputStream::print(b); println(); }
            void println(x10_int i) { OutputStream::print(i); println(); }
            void println(x10_long l) { OutputStream::print(l); println(); }
            void println(x10_double d) { OutputStream::print(d); println(); }
//            template<typename T> void println(T* o);
            void println(const x10::lang::String& str) { OutputStream::print(str); println(); }
            void printf(const x10::ref<x10::lang::String>& format, const x10::ref<x10::array<x10::ref<x10::lang::Object> > >& parms) {
                _D_("PrintStream::printf(\"" << ((const string&)(*format)).c_str() << "\", ...)");
                OutputStream::printf(format, parms);
            }
            void printf(const x10::ref<x10::lang::String>& format) { OutputStream::printf(format); }
        };
//        template<typename T> void PrintStream::println(T* o) {
//            println(x10::ref<T>(o));
//        }

        // java.io.InputStream
        class InputStream : public x10::lang::Object {
        protected:
            explicit InputStream(const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"java::io::InputStream")) { }
            explicit InputStream() : Object() { }
            virtual char* gets(char* s, int num) = 0;
        public:
            virtual void close() { }
            virtual x10_int read() = 0;
            virtual x10_int read(const x10::ref<x10::array<x10_byte> >& b);
            virtual x10_int read(const x10::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len);
            friend class FilterInputStream;
        };
        // FILEPtrInputStream
        class FILEPtrInputStream : public java::io::InputStream, public FILEPtrStream {
        protected:
            char* gets(char* s, int num);
        public:
            explicit FILEPtrInputStream(FILE* stream, const char* _t = NULL) : InputStream(_t?_t:TYPEID(*this,"java::io::FILEPtrInputStream")), FILEPtrStream(stream) { }
            x10_int read(const x10::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len);
            x10_int read();
        };
        // FileInputStream
        class FileInputStream : public java::io::FILEPtrInputStream {
        public:
            explicit FileInputStream(const x10::ref<x10::lang::String>& name) : FILEPtrInputStream(FILEPtrStream::open_file(name, "r"), TYPEID(*this,"java::io::FileInputStream")) { }
        };
        // java.io.FilterInputStream
        class FilterInputStream : public java::io::InputStream {
        protected:
            const x10::ref<InputStream> in;
            char* gets(char* s, int num) { return in->gets(s, num); }
            explicit FilterInputStream(const x10::ref<InputStream>& _in, const char* _t = NULL) : InputStream(_t?_t:TYPEID(*this,"java::io::FilterInputStream")), in(_in) { }
        public:
            void close() { in->close(); }
            x10_int read() { return in->read(); }
            x10_int read(const x10::ref<x10::array<x10_byte> >& b) { return in->read(b); }
            x10_int read(const x10::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len) { return in->read(b, off, len); }
        };
        // java.io.DataInputStream
        class DataInputStream : public java::io::FilterInputStream {
        public:
            explicit DataInputStream(const x10::ref<InputStream>& _in) : FilterInputStream(_in, TYPEID(*this,"java::io::DataInputStream")) { }
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
            x10_float readFloat() { return java::lang::Float::intBitsToFloat(this->readInt()); }
            x10_double readDouble() { return java::lang::Double::longBitsToDouble(this->readLong()); }
            x10::ref<x10::lang::String> readLine();
            void readFully(const x10::ref<x10::array<x10_byte> >& b);
            void readFully(const x10::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len);
            x10::ref<x10::lang::String> readUTF();
        };
    }
};
namespace x10 {
    namespace lang {
        // x10.lang.System
        class System {
        private:
            System() { } // Cannot instantiate
        public:
            static x10_long nanoTime();
            static x10_long currentTimeMillis();
            static void exit(x10_int ret);
            static void loadLibrary(ref<String> name);
            static void load(ref<String> name);
            static void gc();
            static const ref<java::io::InputStream> x10__in;
            static const ref<java::io::PrintStream> x10__out;
            static const ref<java::io::PrintStream> x10__err;
        private:
            static void __init__in_out_err();
            friend class x10::__init__;
        };
// math.h defines M_PI
#undef M_PI
// math.h defines log2 as a macro
#undef log2
        // x10.lang.Math
        class Math {
        private:
            Math() { } // Cannot instantiate
        public:
            template<typename T> static T abs(T v) { return v < 0 ? -v : v; }
            template<typename T, typename U> static T max(T v1, U v2) { return v1 > (T)v2 ? v1 : (T)v2; }
            template<typename T, typename U> static T min(T v1, U v2) { return v1 < (T)v2 ? v1 : (T)v2; }
            inline static x10_double log(x10_double a) {
                double l = ::log((double)a);
                if (errno == EDOM) return (x10_double)(l + nan(NULL));
                return (x10_double)l;  // assume POSIX math (returns -HUGE_VAL on 0)
            }
            inline static x10_double exp(x10_double a) {
                return (x10_double)::exp((double)a);  // assume POSIX math
            }
            inline static x10_double sqrt(x10_double a) {
                return (x10_double)::sqrt((double)a);  // assume POSIX math
            }
            inline static x10_double sin(x10_double a) {
                return (x10_double)::sin((double)a);  // assume POSIX math
            }
            inline static x10_double cos(x10_double a) {
                return (x10_double)::cos((double)a);  // assume POSIX math
            }
            inline static x10_double pow(x10_double a, x10_double b) {
                if (b == 1) return a;
                else if (isnan(b) || (abs(a) == 1 && isinf(b))) return (x10_double)(a + nan(NULL));
                else return (x10_double)::pow((double)a, (double)b); // assume POSIX math (returns -HUGE_VAL on a=0&&b<0)
            }
            inline static x10_double random() {
                static java::util::Random rand;
                return rand.nextDouble();
            }
            static const double x10__PI;
	    template <typename T> static T ceil(T a){ return ::ceil(a);}
	    template <typename T> static T floor(T a){ return ::floor(a);}
	    template <typename T> static T round(T a){ return ::round(a);}
        };
    }
    namespace compilergenerated {
        // x10.compilergenerated.Boxed*
        // TODO: fix hierarchy
        class BoxedNumber : public x10::lang::Object { 
		public:
		x10_boolean static equals (ref<BoxedNumber> x1, ref<BoxedNumber> x2) {
			return (x1 == x2 || ((x1 != NULL) && (x2 != NULL) && x1->equals(x2)));
		}
		virtual x10_boolean equals (ref<BoxedNumber> x)  = 0;
	
	};
#define DECLARE_BOXED_TYPE(name, type) \
        class Boxed##name : public BoxedNumber { \
        private: \
            x10_##type _val; \
        public: \
            Boxed##name(x10_##type val) : _val(val) { } \
            x10_##type type##Value() { return _val; } \
            virtual x10_boolean equals(ref<Boxed##name> v) {return type##Value() == v->type##Value(); }\
	    virtual x10_boolean equals (ref<BoxedNumber> x)  {return equals((ref<Boxed##name>) x);} \
            virtual x10_boolean equals(x10_##type v) {return type##Value() == v; } \
        }
        DECLARE_BOXED_TYPE(Boolean, boolean);
        DECLARE_BOXED_TYPE(Byte, byte);
        DECLARE_BOXED_TYPE(Character, char);
        DECLARE_BOXED_TYPE(Short, short);
        DECLARE_BOXED_TYPE(Integer, int);
        DECLARE_BOXED_TYPE(Long, long);
        DECLARE_BOXED_TYPE(Float, float);
        DECLARE_BOXED_TYPE(Double, double);
    }
    extern array<ref<x10::lang::String> >* convert_args(int ac, char **av);
    extern void free_args(array<ref<x10::lang::String> > *arr);
    namespace lang {
        // x10.lang.String
        class String : public Object, public std::string { // value?
            static const string to_string(const x10_boolean b);
            static const string to_string(const x10_char c);
            static const string to_string(const x10_int i);
            static const string to_string(const x10_long i);
            static const string to_string(const x10_double i);
            static const string to_string(const place& p);
            static const string to_string(const ref<array<x10_char> >& value);
            static const string to_string(const ref<array<x10_char> >& value, x10_int offset, x10_int count);
            static const string to_string(const ref<String>& s);
        public:
            String() : Object(TYPEID(*this,"x10::lang::String")), string("") { }
            String(const string& content) : Object(TYPEID(*this,"x10::lang::String")), string(content) { }
            String(const char* s) : Object(TYPEID(*this,"x10::lang::String")), string(s) { }
            String(const x10_boolean b) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(b)) { }
            String(const x10_int i) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(i)) { }
            String(const x10_char c) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(c)) { }
            String(const x10_short s) : Object(TYPEID(*this,"x10::lang::String")), string(to_string((x10_int)s)) { }
            String(const x10_long i) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(i)) { }
            String(const x10_float f) : Object(TYPEID(*this,"x10::lang::String")), string(to_string((x10_double)f)) { }
            String(const x10_double i) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(i)) { }
            String(const place& p) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(p)) { }
            String(const ref<array<x10_char> >& value) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(value)) { }
            String(const ref<array<x10_char> >& value, x10_int offset, x10_int count) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(value, offset, count)) { }
            String(const String& s) : Object(TYPEID(*this,"x10::lang::String")), string(dynamic_cast<const string&>(s)) { }
            explicit String(const ref<String>& s) : Object(TYPEID(*this,"x10::lang::String")), string(to_string(s)) { }
            const String& operator=(const String& s) {
                string::operator=(dynamic_cast<const string&>(s));
                return *this;
            }
            x10::ref<String> toString() { return this; }
//            x10_boolean equals(const ref<Object>& s) const;  // FIXME [IP] investigate
            // Why do we need this? -Krishna. It causes a conflict with the following function.
            x10_boolean equals(const ref<String>& s) const;
            x10_int length() const { return (x10_int) std::string::length(); }
            String operator+(const String& s) const;
            x10_int indexOf(const ref<String>& s, x10_int i = 0) const;
            x10_int indexOf(x10_char c, x10_int i = 0) const;
            x10_int lastIndexOf(const x10::ref<String>& s, x10_int i = 0) const;
            x10_int lastIndexOf(x10_char c, x10_int i = 0) const;
            String substring(x10_int start, x10_int end) const;
            String substring(x10_int start) const { return substring(start, this->length()); }
            x10_char charAt(x10_int i) const;
            x10::ref<x10::array<x10_char> > toCharArray() const;
            friend array<ref<x10::lang::String> >* x10::convert_args(int ac, char **av);
            friend void x10::free_args(array<ref<x10::lang::String> > *arr);
        };

        template<typename T> String operator+(const T& v, const String& s);
        template<typename T> String operator+(const ref<T>& v, const String& s);

        template<typename T> String operator+(const T& v, const String& s) {
            return String(v) + s;
        }

        template<typename T> String operator+(const ref<T>& v, const String& s) {
            return String(*v) + s;
        }
    }

//    template<typename T> inline ref<x10::lang::String> operator+(const ref<x10::lang::String>& s1, const T& v) {
//        return s1 + ref<x10::lang::String>(new (alloc<x10::lang::String>()) x10::lang::String(v));
//    }

    template<typename T> inline ref<x10::lang::String> operator+(const ref<x10::lang::String>& s1, const ref<T>& v) {
        return s1 + v->toString();
    }

    namespace lang {
        class StringBuffer : public Object {
            String _buf;
        public:
            explicit StringBuffer() : Object(TYPEID(*this,"x10::lang::StringBuffer")), _buf() { }
            StringBuffer(const StringBuffer& sb) : Object(TYPEID(*this,"x10::lang::StringBuffer")), _buf(sb._buf) { }
            explicit StringBuffer(const String& s) : Object(TYPEID(*this,"x10::lang::StringBuffer")), _buf(s) { }
            explicit StringBuffer(const ref<String>& s) : Object(TYPEID(*this,"x10::lang::StringBuffer")), _buf(s) { }
            ref<StringBuffer> append(const String& s) { _buf = String(_buf + s); return this; }
            // The following covers x10_boolean, x10_char, x10_int, x10_long, x10_float, x10_double
            template<typename T> ref<StringBuffer> append(T v) { return append(String(v)); }
            ref<StringBuffer> append(const ref<array<x10_char> >& str) { return append(String(str)); }
            ref<StringBuffer> append(const ref<array<x10_char> >& str, x10_int offset, x10_int len) {
                return append(String(str, offset, len));
            }
            ref<StringBuffer> append(const ref<Object>& obj) { return append(obj->toString()); }
            ref<StringBuffer> append(const ref<String>& s) { return append(*s); }
            ref<StringBuffer> append(const ref<StringBuffer>& sb) { return append(sb->_buf); }
            x10_int length() { return _buf.length(); }
            ref<String> toString() { return new (x10::alloc<String>()) String(_buf); }  // FIXME: GC
        };

       	// x10.lang.Throwable
        class Throwable : public x10::lang::Object {
            const ref<String> message;
        public:
            explicit Throwable(const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::lang::Throwable")) , message(NULL) { }
            explicit Throwable(ref<String> message, const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::lang::Throwable")), message(message) { }
            virtual ~Throwable() { }
            virtual ref<String> getMessage() const { return message; }
            void printStackTrace() const;
            virtual void printStackTrace(ref<java::io::PrintStream> out) const;
        };
	 // x10.lang.Exception
	class Exception: public Throwable {
	public:
            explicit Exception(const char* _t = NULL) : Throwable(_t?_t:TYPEID(*this,"x10::lang::Exception")) { }
            explicit Exception(ref<String> message, const char* _t = NULL) : Throwable(_t?_t:TYPEID(*this,"x10::lang::Exception")) { }
	};


        // x10.lang.Error
        class Error : public Exception {
        public:
            explicit Error(const char* _t = NULL) : Exception(_t?_t:TYPEID(*this,"x10::lang::Error")) { }
            explicit Error(ref<String> message, const char* _t = NULL) : Exception(message, _t?_t:TYPEID(*this,"x10::lang::Error")) { }
        };

        // x10.lang.RuntimeException
        class RuntimeException : public Exception {
        public:
            explicit RuntimeException(const char* _t = NULL) : Exception(_t?_t:TYPEID(*this,"x10::lang::RuntimeException")) { }
            explicit RuntimeException(ref<String> message, const char* _t = NULL) : Exception(message, _t?_t:TYPEID(*this,"x10::lang::RuntimeException")) { }
        };

        // x10.lang.IndexOutOfBoundsException
        class IndexOutOfBoundsException : public x10::lang::RuntimeException {
            public:
                explicit IndexOutOfBoundsException(const char* _t = NULL) : x10::lang::RuntimeException(String("Index out of bounds"), _t?_t:TYPEID(*this,"x10::lang::IndexOutOfBoundsException")) { }
                explicit IndexOutOfBoundsException(x10::ref<x10::lang::String> message, const char* _t = NULL) : x10::lang::RuntimeException(String("Index out of bounds: ") + message, _t?_t:TYPEID(*this,"x10::lang::IndexOutOfBoundsException")) { }
        };

        // x10.lang.NumberFormatException
        // FIXME: this is not thrown by parseFloat
        class NumberFormatException : public x10::lang::RuntimeException {
            public:
                explicit NumberFormatException(const char* _t = NULL) : x10::lang::RuntimeException(_t?_t:TYPEID(*this,"x10::lang::NumberFormatException")) { }
                explicit NumberFormatException(x10::ref<x10::lang::String> message, const char* _t = NULL) : x10::lang::RuntimeException(message, _t?_t:TYPEID(*this,"x10::lang::NumberFormatException")) { }
        };

        // x10.lang.UnsupportedOperationException
        class UnsupportedOperationException : public x10::lang::RuntimeException {
            public:
                explicit UnsupportedOperationException(const char* _t = NULL) : x10::lang::RuntimeException(String("Unsupported operation"), _t?_t:TYPEID(*this,"x10::lang::UnsupportedOperationException")) { }
                explicit UnsupportedOperationException(x10::ref<x10::lang::String> message, const char* _t = NULL) : x10::lang::RuntimeException(String("Unsupported operation: ") + message, _t?_t:TYPEID(*this,"x10::lang::UnsupportedOperationException")) { }
        };
    }
};
namespace java {
    namespace io {
        // java.io.IOException
        class IOException : public x10::lang::Exception {
        public:
            explicit IOException(const char* _t = NULL) : x10::lang::Exception(_t?_t:TYPEID(*this,"java::io::IOException")) { }
            explicit IOException(x10::ref<x10::lang::String> message, const char* _t = NULL) : x10::lang::Exception(message, _t?_t:TYPEID(*this,"java::io::IOException")) { }
        };

        // java.io.FileNotFoundException
        class FileNotFoundException : public IOException {
        public:
            explicit FileNotFoundException(const char* _t = NULL) : IOException(_t?_t:TYPEID(*this,"java::io::FileNotFoundException")) { }
            explicit FileNotFoundException(x10::ref<x10::lang::String> name, const char* _t = NULL) : IOException(name, _t?_t:TYPEID(*this,"java::io::FileNotFoundException")) { }
        };
    }
};

/*
 * Serialization support.
 *
 * Only value classes need to be serialized.
 * The serialization mechanism is implemented as follows:
 *
 *   For every value class hierarchy, a set of serialization ids are
 *   defined.  Each serialization id has to be unique within that
 *   hierarchy.  Serialization ids should be small, as they are
 *   used by default to index into a table.
 *
 *   Each class knows how to deserialize values of its own type.
 *   It also knows how to deserialize its fields, and delegates
 *   to the superclass for superclass' fields.
 *   In addition, each base class knows how to dispatch deserialization
 *   to all of its subclasses.
 *
 *   Each client class must implement _serialize (the addr_map
 *   parameter can be used to keep track of reference sharing and
 *   cycles during serialization), and a virtual _serialize_fields.
 *   A helper function _serialize_ref with default functionality
 *   (write the id, then fields) is provided.
 *   Each client must implement a constructor with a
 *   SERIALIZATION_MARKER argument (to construct empty values of
 *   that type), and a virtual _deserialize_fields method to fill
 *   in the fields from a buffer.  Clients that want to override
 *   default behavior should specialize the _reference_serializer
 *   and the _reference_deserializer templates.
 *
 *   When serializing members of reference types, the appropriate
 *   _serialize_value_ref should be invoked.  When deserializing, the
 *   appropriate _deserialize_ref should be invoked, with the
 *   static type of the member being deserialized.  Not all members
 *   need to be serialized, but those that are must also be
 *   deserialized.
 *
 *   Each base class must define a virtual _serialize.  There should
 *   be a template specialization of _deserialize_value_ref for each
 *   base class in the hierarchy that invokes _deserialize_superclass.
 *   Each base class must also contain a field _registered_subclasses
 *   of type x10::subclass_vector parameterized by the base class.
 *   Each subclass must invoke _register_subclass (parameterized by
 *   the base class and the actual class) in its static initializer.
 *   It is recommended that subclasses first invoke the base class
 *   implementation in _serialize_fields and _deserialize_fields.
 *
 *   The buffer argument's cursor is modified on both serialization
 *   and deserialization.
 */
namespace x10 {
    class SERIALIZATION_MARKER { };

    template<class T> struct _serializer {
        static void _(char*& buf, T val) {
            // FIXME: endianness
            *(T*) buf = val;
            buf += sizeof(T);
        }
    };
    template<class T> struct _deserializer {
        static T _(const char*& buf) {
            // FIXME: endianness
            T val = *(T*) buf;
            buf += sizeof(T);
            return val;
        }
    };
    // If the helper class is used on a ref, treat as remote
    template<class T> struct _serializer<ref<T> > {
        static void _(char*& buf, ref<T> val) {
            x10_remote_ref_t rr = x10_serialize_ref((x10_addr_t) val.operator->());
            *(x10_remote_ref_t*) buf = rr;
            buf += sizeof(x10_remote_ref_t);
        }
    };
    // If the helper class is used on a ref, treat as remote
    template<class T> struct _deserializer<ref<T> > {
        static ref<T> _(const char*& buf) {
            x10_remote_ref_t rr = *(x10_remote_ref_t*) buf;
            buf += sizeof(x10_remote_ref_t);
            return ref<T>((T*) x10_deserialize_ref(rr));
        }
    };

    /* A growable buffer */
    class serialization_buffer {
    private:
        static const double FACTOR;
        static const size_t INITIAL_SIZE = 16;
        char* buffer;
        char* limit;
        char* cursor;
        static char* alloc(size_t size) { return x10::alloc<char>(size); }
        static void dealloc(char* buf) { x10::dealloc<char>(buf); }
        char* grow() {
            assert (limit != NULL);
            char* saved_buf = buffer;
            size_t length = cursor - buffer;
            size_t allocated = limit - buffer;
            size_t new_size = (size_t) (allocated * FACTOR);
            buffer = alloc(new_size);
            ::memcpy(buffer, saved_buf, length);
            limit = buffer + new_size;
            cursor = buffer + length;
            dealloc(saved_buf);
            return buffer;
        }
    public:
        serialization_buffer()
            : buffer(alloc(INITIAL_SIZE)), limit(buffer + INITIAL_SIZE), cursor(buffer)
        { }
        ~serialization_buffer() { dealloc(buffer); }
        operator const char*() const { return buffer; }
        /* To be used for primitive types */
        template<typename T> serialization_buffer& write(T val) {
            if (cursor + sizeof(T) >= limit)
                grow();
            x10::_serializer<T>::_(cursor, val);
            return *this;
        }
        /* To be used for primitive types */
        template<typename T> T read() {
            return x10::_deserializer<T>::_((const char*&) cursor);
        }
        template<typename T> serialization_buffer& read(T& val) {
            val = x10::_deserializer<T>::_((const char*&) cursor);
            return *this;
        }
        /* To be used for primitive types */
        template<typename T> T peek() {
            const char* tmp = (const char*) cursor;
            return x10::_deserializer<T>::_(tmp);
        }
        template<typename T> serialization_buffer& peek(T& val) {
            const char* tmp = (const char*) cursor;
            val = x10::_deserializer<T>::_(tmp);
            return *this;
        }
        void clean() {
            dealloc(buffer);
            buffer = alloc(INITIAL_SIZE);
            limit = buffer + INITIAL_SIZE;
            cursor = buffer;
        }
        void set(const char* buf) {
            buffer = cursor = const_cast<char*>(buf);
            limit = NULL;
        }
        size_t length() { return cursor - buffer; }
    };

    /* Address tracking */
    class addr_map {
        int _size;
        const void** _ptrs;
        int _top;
        void _grow() {
            _ptrs = (const void**) memcpy(new (x10::alloc<const void*>((_size<<1)*sizeof(const void*))) const void*[_size<<1], _ptrs, _size*sizeof(const void*));
            _size <<= 1;
        }
        void _add(const void* ptr) {
            if (_top == _size) _grow();
            _ptrs[_top++] = ptr;
        }
        bool _find(const void* ptr) {
            for (int i = 0; i < _top; i++)
                if (_ptrs[i] == ptr) return true;
            return false;
        }
    public:
        addr_map(int init_size = 4) : _size(init_size), _ptrs(new (x10::alloc<const void*>((init_size)*sizeof(const void*)))const void*[init_size]), _top(0) {
            assert (_ptrs != NULL);
        }
        template<class T> bool ensure_unique(const ref<T>& r) {
            return ensure_unique((void*) r.operator->());
        }
        bool ensure_unique(const void* p) {
            if (_find(p)) return false;
            _add(p);
            return true;
        }
        void reset() { _top = 0; assert (false); }
        ~addr_map() { delete _ptrs; }
    };

#ifndef NO_IOSTREAM
    // Debug helper; usage: o << _dump_chars(b, l)
    class _dump_chars {
        const char* _buf;
        int _len;
        const char* _delim;
    public:
        _dump_chars(const char* buf, int len, const char* delim=" ") :
            _buf(buf), _len(len), _delim(delim) { }
        friend std::ostream& operator<<(std::ostream& os, const _dump_chars d) {
            for (int i = 0; i < d._len; i++)
                os << (i==0?"":d._delim) << (int)(unsigned char)d._buf[i];
            return os;
        }
    };
#endif

    // Specialize for classes without SERIALIZATION_ID (e.g., final classes)
    template<class T> struct _reference_serializer {
        static void _(ref<T> v, serialization_buffer& buf, addr_map& m) {
            _Sd_(size_t len = buf.length());
            _S_("Serializing " << DEMANGLE(TYPENAME(T)));
            buf.write(T::SERIALIZATION_ID);
            _S_("Written " << T::SERIALIZATION_ID);
            v->_serialize_fields(buf, m);
            _S_(x10::_dump_chars(((const char*)buf) + len, buf.length() - len));
        }
    };

    template<class T> inline void _serialize_ref(ref<T> v, serialization_buffer& buf, addr_map& m) {
        _reference_serializer<T>::_(v, buf, m);
    }

    template<class T> inline void _serialize_ref(T* p, serialization_buffer& buf, addr_map& m) {
        _serialize_ref(ref<T>(p), buf, m);
    }

    template<class T> inline void _serialize_value_ref(serialization_buffer& buf, addr_map& m, const ref<T>& v) {
        v->_serialize(buf, m);
    }

    template<class T> inline void _serialize_value_ref(serialization_buffer& buf, addr_map& m, T* p) {
        _serialize_value_ref(buf, m, ref<T>(p));
    }

    // Specialize for classes without SERIALIZATION_ID (e.g., final classes)
    template<class T> struct _reference_deserializer {
        static ref<T> _(serialization_buffer& buf) {
            int id = buf.read<int>();
            // TODO
            //if (id == NULL_SERIALIZATION_ID)
            //    return NULL;
            assert (id == T::SERIALIZATION_ID);
            _S_("Deserializing " << DEMANGLE(TYPENAME(T)));
            ref<T> rv = ref<T>(new (alloc<T>()) T(SERIALIZATION_MARKER()));
            rv->_deserialize_fields(buf);
            return rv;
        }
    };

    template<class T> inline ref<T> _deserialize_ref(serialization_buffer& buf) {
        return _reference_deserializer<T>::_(buf);
    }

    template<class T> inline ref<T> _deserialize_value_ref(serialization_buffer& buf) {
        return _deserialize_ref<T>(buf);
    }

    template<class T> inline void serialize_value_type(serialization_buffer& buf, const ref<T>& v) {
        addr_map m;
        _serialize_value_ref(buf, m, v);
    }

    template<class T> inline x10::ref<T> deserialize_value_type(serialization_buffer& buf) {
        return _deserialize_value_ref<T>(buf);
    }

    // TODO: Specialize x10::lang::Object serialization/deserialization
    template<> void serialize_value_type<x10::lang::Object>(serialization_buffer& buf, const x10::ref<x10::lang::Object>& v);
    template<> x10::ref<x10::lang::Object> _deserialize_value_ref<x10::lang::Object>(serialization_buffer& buf);

    struct subclass_vector {
        size_t _length;
        void* (**_subclasses)(serialization_buffer&);
    };
    void _register_subclass_impl(int id, subclass_vector& registered_subclasses, void* (*deserialize_func)(serialization_buffer&));
    template<class S, class T> inline void _register_subclass() {
        _S_("Registering " << DEMANGLE(TYPENAME(T)) << " as a subclass of " << DEMANGLE(TYPENAME(S)) << " (id=" << id << ")");
        ref<T>(*deserialize_func)(serialization_buffer&) = _deserialize_ref<T>;
        _register_subclass_impl(T::SERIALIZATION_ID, S::_registered_subclasses, reinterpret_cast<void*(*)(serialization_buffer&)>(deserialize_func));
//        int id = T::SERIALIZATION_ID;
//        assert (id >= 0);
//        if (S::_registered_subclasses._length <= (size_t)id) {
//            S::_registered_subclasses._subclasses = (ref<S>(**)(serialization_buffer&)) realloc(S::_registered_subclasses._subclasses, (id+1) * sizeof(ref<S>(*)(serialization_buffer&)));
//            S::_registered_subclasses._length = id+1;
//        }
//        //extern ref<T> _deserialize_ref<T>(serialization_buffer& buf);
//        ref<T>(*deserialize_func)(serialization_buffer&) = _deserialize_ref<T>;
//        S::_registered_subclasses._subclasses[id] = reinterpret_cast<ref<S>(*)(serialization_buffer&)>(deserialize_func);
    }
    void* _deserialize_subclass_impl(int id, serialization_buffer& buf, const subclass_vector& registered_subclasses);
    template<class S> inline ref<S> _deserialize_subclass(int id, serialization_buffer& buf) {
        return ref<S>(reinterpret_cast<S*>(_deserialize_subclass_impl(id, buf, S::_registered_subclasses)));
//        assert (id >= 0);
//        assert ((size_t)id < S::_registered_subclasses._length);
//        ref<S>(*deserialize_func)(serialization_buffer&) = S::_registered_subclasses._subclasses[id];
//        if (deserialize_func == NULL) {
//            assert(false); return NULL;
//        }
//        return deserialize_func(buf);
    }
    template<class S> inline ref<S> _deserialize_superclass(serialization_buffer& buf) {
        _S_("Deserializing " << DEMANGLE(TYPENAME(S)) << " from ' " << x10::_dump_chars(buf, 40) << " '");
        int id = buf.peek<int>();
        _S_("Id = " << id);
        return _deserialize_subclass<S>(id, buf);
    }
};

namespace x10 {
    namespace lang {
        // x10.lang.RankMismatchException
        class RankMismatchException : public RuntimeException {
            x10_int _expected, _actual;
        public:
            RankMismatchException(x10_int expected, x10_int actual, const char* _t = NULL) :
                RuntimeException(_t?_t:TYPEID(*this,"x10::lang::RankMismatchException")), _expected(expected), _actual(actual) { }
            virtual ref<String> getMessage() const { return String("Expected ") + _expected + String("; actual ") + _actual; }
        };
    }

#ifndef NO_BOUNDS_CHECKS
    inline void _check_rank(int expected, int actual) {
        if (expected != actual) {
            _D_("Rank mismatch: Expected " << expected << "; actual " << _actual);
#ifndef NO_EXCEPTIONS
            throw x10::ref<x10::lang::RankMismatchException>(new (x10::alloc<x10::lang::RankMismatchException>()) x10::lang::RankMismatchException(expected, actual));
#endif
        }
    }
    inline void _check_rank_le(int expected, int actual) {
        if (expected > actual) {
            _D_("Rank mismatch: Expected " << expected << "; actual " << _actual);
#ifndef NO_EXCEPTIONS
            throw x10::ref<x10::lang::RankMismatchException>(new (x10::alloc<x10::lang::RankMismatchException>()) x10::lang::RankMismatchException(expected, actual));
#endif
        }
    }
#endif

// TODO
#define DIM_CONCAT(PRE,NUM,POST) (PRE #NUM POST)

    namespace lang {
        // x10.lang.MultipleExceptions
        class MultipleExceptions : public RuntimeException {
            const x10_int _num;
            Exception** _exc;
        public:
            MultipleExceptions(x10_int num, Exception const* const* exc, const char* _t = NULL) :
                RuntimeException(_t?_t:TYPEID(*this,"x10::lang::MultipleExceptions")),
                _num(num), _exc(new (x10::alloc<Exception*>(num*sizeof(Exception*))) Exception*[num]) // FIXME: GC
            {
                memcpy(_exc, exc, num*sizeof(Exception*));
            }
            ~MultipleExceptions() {
                delete _exc;
            }
        };

        class region;
        template<int R> class _region;

        // x10.lang.point
        class point : public x10::lang::Object { // value
        protected:
            explicit point(int rank, const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::lang::point")), x10__rank(rank) { }
        public:
            virtual ~point() { }
            virtual x10_index_t get(x10_int idx) const = 0;
            const int x10__rank;
            x10_index_t operator[](int idx) const {
#ifndef NO_BOUNDS_CHECKS
                _check_rank_le(idx+1, x10__rank);
#endif
                _D_("point->[]: applying to " << idx);
                return get(idx);
            }
            friend class region;
        public: // Serialization
            static const int SERIALIZATION_ID = 0;
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            virtual void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) { };
            virtual void _deserialize_fields(serialization_buffer& buf) { }
            static x10::subclass_vector _registered_subclasses;
        };
        template<int R> class _point : public point { // value
        public: // [IP] temporary
            x10_index_t _v[R];
        protected:
            x10_index_t get(x10_int idx) const { return _v[idx]; }
        public:
            // Assumes len(v) == R
            _point(const x10_index_t* v) : point(R, TYPEID(*this,DIM_CONCAT("x10::lang::_point<",R,">"))) {
                for (int i = 0; i < R; i++) _v[i] = v[i];
            }
            friend class _region<R>;
        public: // Serialization
            static const int SERIALIZATION_ID = R;
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            virtual void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) {
                point::_serialize_fields(buf, m);
                for (int i = 0; i < R; i++) {
                    buf.write(_v[i]);
                    _S_("Written " << _v[i]);
                }
            }
            virtual void _deserialize_fields(serialization_buffer& buf) {
                point::_deserialize_fields(buf);
                for (int i = 0; i < R; i++) {
                    _v[i] = buf.read<x10_index_t>();
                }
            }
            explicit _point(x10::SERIALIZATION_MARKER m) : point(R, TYPEID(*this,DIM_CONCAT("x10::lang::_point<",R,">"))) { }
        //private:
            class __clinit { public: __clinit() { x10::_register_subclass<point, _point<R> >(); } };
            static const __clinit clinit;
        };
        template<> class _point<1> : public point { // value
        public: // [IP] temporary
            x10_index_t _i;
        protected:
            x10_index_t get(x10_int idx) const { return _i; }
        public:
            _point(x10_index_t i) : point(1, TYPEID(*this,"x10::lang::_point<1>")), _i(i) { }
            friend class _region<1>;
        public: // Serialization
            static const int SERIALIZATION_ID = 1;
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            virtual void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) {
                point::_serialize_fields(buf, m);
                buf.write(_i);
                _S_("Written " << _i);
            }
            virtual void _deserialize_fields(serialization_buffer& buf) {
                point::_deserialize_fields(buf);
                _i = buf.read<x10_index_t>();
            }
            explicit _point(x10::SERIALIZATION_MARKER m) : point(1, TYPEID(*this,"x10::lang::_point<1>")) { }
        //private:
            class __clinit { public: __clinit() { x10::_register_subclass<point, _point<1> >(); } };
            static const __clinit clinit;
        };
        template<> class _point<2> : public point { // value
        public: // [IP] temporary
            x10_index_t _i;
            x10_index_t _j;
        protected:
            x10_index_t get(x10_int idx) const { return idx == 0 ? _i : _j; }
        public:
            _point(x10_index_t i, x10_index_t j) : point(2, TYPEID(*this,"x10::lang::_point<2>")), _i(i), _j(j) { }
            friend class _region<2>;
        public: // Serialization
            static const int SERIALIZATION_ID = 2;
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            virtual void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) {
                point::_serialize_fields(buf, m);
                buf.write(_i);
                _S_("Written " << _i);
                buf.write(_j);
                _S_("Written " << _j);
            }
            virtual void _deserialize_fields(serialization_buffer& buf) {
                point::_deserialize_fields(buf);
                _i = buf.read<x10_index_t>();
                _j = buf.read<x10_index_t>();
            }
            explicit _point(x10::SERIALIZATION_MARKER m) : point(2, TYPEID(*this,"x10::lang::_point<2>")) { }
        //private:
            class __clinit { public: __clinit() { x10::_register_subclass<point, _point<2> >(); } };
            static const __clinit clinit;
        };
    }

    // Specialize deserialization of the x10::lang::point hierarchy
    template<> inline ref<x10::lang::point> _deserialize_value_ref<x10::lang::point>(serialization_buffer& buf) {
        return _deserialize_superclass<x10::lang::point>(buf);
        //switch (buf.peek<int>()) {
        //case x10::lang::_point<1>::SERIALIZATION_ID: return _deserialize_ref<x10::lang::_point<1> >(buf);
        //case x10::lang::_point<2>::SERIALIZATION_ID: return _deserialize_ref<x10::lang::_point<2> >(buf);
        //}
    }


    namespace lang {
        // x10.lang.Iterator
        template<class T> class Iterator {
        protected:
            Iterator() { }
        public:
            virtual bool hasNext() const = 0;
            virtual const T& next() = 0;
        private:
            class EmptyIterator;
            static Iterator<T>* const _EMPTY_ITERATOR;
        public:
            static Iterator<T>& EMPTY_ITERATOR();
        };
        template<class T> Iterator<T>* const Iterator<T>::_EMPTY_ITERATOR = NULL;
        template<class T> class Iterator<T>::EmptyIterator : public Iterator<T> {
        public:
            bool hasNext() const { return false; }
            const T& next() {
                throw x10::ref<IndexOutOfBoundsException>(new (x10::alloc<IndexOutOfBoundsException>()) IndexOutOfBoundsException());
            }
        };
        template<class T> Iterator<T>& Iterator<T>::EMPTY_ITERATOR() {
            if (_EMPTY_ITERATOR == NULL)
                const_cast<Iterator<T>*&>(_EMPTY_ITERATOR) = new (x10::alloc<EmptyIterator>()) EmptyIterator(); // FIXME: GC
            return *_EMPTY_ITERATOR;
        }

        class dist;
        template<int R> class _dist;
        template<int R> class _region;
        // x10.lang.region
        class region : public x10::lang::Object { // value
        public: // [IP] temporary
            virtual x10_index_t _translate(x10_index_t idx) const { return _translate(_point<1>(idx)); }
            virtual x10_index_t _translate(const point& p) const = 0;
        public:
            virtual ~region() { }
#ifndef NO_BOUNDS_CHECKS
            virtual void _check_bounds(x10_index_t idx) const { _check_bounds(_point<1>(idx)); }
            virtual void _check_bounds(const point& p) const = 0;
#endif
            const int x10__rank; // transient
            virtual x10::ref<_region<1> > rank(x10_int dim) = 0;
            explicit region(int rank, const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::lang::region")), x10__rank(rank) { }
            x10_index_t translate(const point& p) const {
#ifndef NO_BOUNDS_CHECKS
                _check_rank(p.x10__rank, x10__rank);
#endif
                return _translate(p);
            }
            x10_index_t translate(x10_index_t idx) const {
#ifndef NO_BOUNDS_CHECKS
                _check_rank(1, x10__rank);
#endif
                return _translate(idx);
            }
            virtual x10_index_t size() const = 0;
            virtual x10_index_t size(x10_int dim) const = 0;
            virtual Iterator<point>& iterator() const = 0;
            virtual x10::ref<dist> toDistribution() const;
        public: // Serialization
            static const int SERIALIZATION_ID = 0;
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            virtual void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) { }
            virtual void _deserialize_fields(serialization_buffer& buf) { }
            static x10::subclass_vector _registered_subclasses;
        };
// FIXME: constant
// Size of the empty construct cache
#define MAX_EMPTY_RANK 10
        class _region_empty : public region { // value
        protected:
#ifndef NO_BOUNDS_CHECKS
            void _check_bounds(const point& p) const {
                _D_("Index out of bounds: " << p[0] << " out of {}");
#ifndef NO_EXCEPTIONS
                throw x10::ref<IndexOutOfBoundsException>(new (x10::alloc<IndexOutOfBoundsException>()) IndexOutOfBoundsException(String(p[0]) + String(" out of {}")));
#endif
            }
#endif
            x10_index_t _translate(const point& p) const {
#ifndef NO_BOUNDS_CHECKS
                _check_bounds(p);
#endif
                return 0;
            }
            _region_empty(const _region_empty& r) : region(r.x10__rank, TYPEID(*this,"x10::lang::_region_empty")) { }
            const _region_empty& operator=(const _region_empty&) { return *this; }
            static _region_empty* _empty[MAX_EMPTY_RANK+1];
            static _region<1>* _empty_1;
            static _region<1>* EMPTY_1();
        public:
            x10_index_t size() const { return 0; }
            x10_index_t size(x10_int dim) const {
#ifndef NO_BOUNDS_CHECKS
                _check_rank_le(dim+1, x10__rank);
#endif
                return 0;
            }
            x10::ref<_region<1> > rank(x10_int dim) {
#ifndef NO_BOUNDS_CHECKS
                _check_rank_le(dim+1, x10__rank);
#endif
                return EMPTY_1();
            }
            _region_empty(int rank) : region(rank, TYPEID(*this,"x10::lang::_region_empty")) { }
            Iterator<point>& iterator() const { return Iterator<point>::EMPTY_ITERATOR(); }
            static x10::ref<_region_empty> EMPTY(int rank) {
                if (rank > MAX_EMPTY_RANK)
                    return new (x10::alloc<_region_empty>()) _region_empty(rank); // FIXME: GC
                if (_empty[rank] == NULL) {
                    _empty[rank] = new (x10::alloc<_region_empty>()) _region_empty(rank); // FIXME: GC
                }
                return _empty[rank];
            }
        public: // Serialization
            static const int SERIALIZATION_ID = 1;
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            virtual void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) {
                // Do not call super::_serialize_fields()
                buf.write(x10__rank);
                _S_("Written " << x10__rank);
            }
            virtual void _deserialize_fields(serialization_buffer& buf) { assert (false); }
        //private:
            class __clinit { public: __clinit() { x10::_register_subclass<region, _region_empty>(); } };
            static const __clinit clinit;
        };
    }

    // Specialize for interned values
    template<> struct _reference_deserializer<x10::lang::_region_empty> {
        static ref<x10::lang::_region_empty> _(serialization_buffer& buf) {
            int id = buf.read<int>();
            assert (id == x10::lang::_region_empty::SERIALIZATION_ID);
            int rank = buf.read<int>();
            _S_("Deserializing x10::lang::_region_empty[" << rank << "]");
            return x10::lang::_region_empty::EMPTY(rank);
        }
    };

    namespace lang {
        template<int R> class _region : public region { // value
        public:
            _region() : region(R, TYPEID(*this,DIM_CONCAT("x10::lang::_region<",R,">"))) { }
        };
        template<> class _region<1> : public region { // value
            class Iter : public Iterator<point> {
                const _region<1>& region; // FIXME: loose reference
                x10_index_t _i;
            public:
                explicit Iter(const _region<1>& region) : region(region), _i(region._lo) { }
                bool hasNext() const { return _i <= region._hi; }
                const point& next() { return *(new (x10::alloc<_point<1> >()) _point<1>(_i++)); } // FIXME: GC
            };
            friend class _region<1>::Iter;
        protected:
#ifndef NO_BOUNDS_CHECKS
            void _check_bounds(x10_index_t idx) const {
                if (idx < _lo || idx > _hi) {  // Bounds check
                    _D_("Index out of bounds: " << idx << " out of (" << _lo << "," << _hi << ")");
#ifndef NO_EXCEPTIONS
                    throw x10::ref<IndexOutOfBoundsException>(new (x10::alloc<IndexOutOfBoundsException>()) IndexOutOfBoundsException(String(idx) + String(" out of (") + _lo + String(",") + _hi + String(")")));
#endif
                }
            }
            void _check_bounds(const point& p) const {
                _check_bounds(reinterpret_cast<const _point<1>&>(p)._i);
            }
#endif
            x10_index_t _translate(x10_index_t idx) const {
//                cerr << "Accessing [" << _lo << ":" << _hi << "] at " << idx << endl;
                return idx - _lo;
            }
            x10_index_t _translate(const point& p) const {
                return _translate(reinterpret_cast<const _point<1>&>(p)._i);
            }
            _region(const _region&) : region(1, TYPEID(*this,"x10::lang::_region<1>")), _lo(0), _hi(0) { }
            const _region<1>& operator=(const _region<1>&) { return *this; }
        public:
            x10_index_t size() const { return _hi - _lo + 1; }
            x10_index_t size(x10_int dim) const {
#ifndef NO_BOUNDS_CHECKS
                _check_rank(dim+1, 1);
#endif
                return size();
            }
            x10::ref<_region<1> > rank(x10_int dim) {
#ifndef NO_BOUNDS_CHECKS
                _check_rank(dim+1, 1);
#endif
                return this;
            }
            const x10_index_t _lo, _hi;
            x10_index_t low() { return _lo; }
            x10_index_t high() { return _hi; }
            _region(x10_index_t lo, x10_index_t hi) : region(1, TYPEID(*this,"x10::lang::_region<1>")), _lo(lo), _hi(hi) { }
            Iterator<point>& iterator() const { return *(new (x10::alloc<Iter>()) Iter(*this)); } // FIXME: GC
        public: // Serialization
            static const int SERIALIZATION_ID = 2;
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            virtual void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) {
                // Do not call super::_serialize_fields()
                buf.write(_lo);
                _S_("Written " << _lo);
                buf.write(_hi);
                _S_("Written " << _hi);
            }
            virtual void _deserialize_fields(serialization_buffer& buf) {
                // Do not call super::_deserialize_fields()
                const_cast<x10_index_t&>(_lo) = buf.read<x10_index_t>();
                const_cast<x10_index_t&>(_hi) = buf.read<x10_index_t>();
            }
            explicit _region(x10::SERIALIZATION_MARKER m) : region(1, TYPEID(*this,"x10::lang::_region<1>")), _lo(0), _hi(0) { }
        //private:
            class __clinit { public: __clinit() { x10::_register_subclass<region, _region<1> >(); } };
            static const __clinit clinit;
        };
        template<> class _region<2> : public region { // value
            class Iter : public Iterator<point> {
                const _region<2>& region; // FIXME: loose reference
                x10_index_t _i;
                x10_index_t _j;
            public:
                explicit Iter(const _region<2>& region) : region(region), _i(region._dims[0]->_lo), _j(region._dims[1]->_lo) { }
                bool hasNext() const { return _i <= region._dims[0]->_hi || _j <= region._dims[1]->_hi; }
                const point& next() {
                    _j++;
                    if (_j > region._dims[1]->_hi) {
                        _j = region._dims[1]->_lo;
                        _i++;
                    }
                    return *(new (x10::alloc<_point<2> >()) _point<2>(_i, _j)); // FIXME: GC
                }
            };
            friend class _region<2>::Iter;
            x10_index_t c_size; // transient
            x10_index_t r_lo;   // transient
            x10_index_t c_lo;   // transient
#ifndef NO_BOUNDS_CHECKS
            void _check_bounds(x10_index_t i, x10_index_t j) const {
                if (i < _dims[0]->_lo || i > _dims[0]->_hi || j < _dims[1]->_lo || j > _dims[1]->_hi) {
                    _D_("Index out of bounds: " << "(" << i << "," << j << ") out of (" << _dims[0]->_lo << "," << _dims[0]->_hi << ")x(" << _dims[1]->_lo << "," << _dims[1]->_hi << ")");
#ifndef NO_EXCEPTIONS
                    throw x10::ref<IndexOutOfBoundsException>(new (x10::alloc<IndexOutOfBoundsException>()) IndexOutOfBoundsException(String("(") + i + String(",") + j + String(") out of (") + _dims[0]->_lo + String(",") + _dims[0]->_hi + String(")x(") + _dims[1]->_lo + String(",") + _dims[1]->_hi + String(")")));
#endif
                }
            }
            void _check_bounds(const point& p) const {
                const _point<2>& p2 = reinterpret_cast<const _point<2>&>(p);
                _check_bounds(p2._i, p2._j);
            }
#endif
            x10_index_t _translate(x10_index_t i, x10_index_t j) const {
                //x10_index_t r = _dims[0]->translate(i); // TODO: call _translate instead
                //x10_index_t c = _dims[1]->translate(j); // TODO: call _translate instead
                x10_index_t r = i - r_lo;
                x10_index_t c = j - c_lo;
                return r*c_size + c;
            }
            _region<1>* _dims[2];
        protected:
            x10_index_t _translate(const point& p) const {
                const _point<2>& p2 = reinterpret_cast<const _point<2>&>(p);
                return _translate(p2._i, p2._j);
            }
            _region(const _region&) : region(2, TYPEID(*this,"x10::lang::_region<2>")) { }
            const _region<2>& operator=(const _region<2>&) { return *this; }
        public:
            x10_index_t size() const { return _dims[0]->size() * _dims[1]->size(); }
            x10_index_t size(x10_int dim) const {
#ifndef NO_BOUNDS_CHECKS
                _check_rank_le(dim+1, 2);
#endif
                return _dims[dim]->size();
            }
            x10::ref<_region<1> > rank(x10_int dim) {
#ifndef NO_BOUNDS_CHECKS
                _check_rank_le(dim+1, 2);
#endif
                return _dims[dim];
            }
            _region(const x10::ref<_region<1> >& rs, const x10::ref<_region<1> >& cs) : region(2, TYPEID(*this,"x10::lang::_region<2>")), c_size(cs->size()), r_lo(rs->_lo), c_lo(cs->_lo) {
                _dims[0] = rs.operator->();
                _dims[1] = cs.operator->();
            }
            Iterator<point>& iterator() const { return *(new (x10::alloc<Iter>()) Iter(*this)); } // FIXME: GC
        public: // Serialization
            static const int SERIALIZATION_ID = 3;
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            virtual void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) {
                // Do not call super::_serialize_fields()
                if (!m.ensure_unique(_dims[0])) assert (false);
                x10::_serialize_value_ref(buf, m, _dims[0]);
                _S_("Serialized _dims[0]");
                if (!m.ensure_unique(_dims[1])) assert (false);
                x10::_serialize_value_ref(buf, m, _dims[1]);
                _S_("Serialized _dims[1]");
            }
            virtual void _deserialize_fields(serialization_buffer& buf) {
                // Do not call super::_deserialize_fields()
                _dims[0] = x10::_deserialize_value_ref<_region<1> >(buf).operator->();
                _dims[1] = x10::_deserialize_value_ref<_region<1> >(buf).operator->();
            }
            explicit _region(x10::SERIALIZATION_MARKER m) : region(2, TYPEID(*this,"x10::lang::_region<2>")) { }
        //private:
            class __clinit { public: __clinit() { x10::_register_subclass<region, _region<2> >(); } };
            static const __clinit clinit;
        };
    }

    // Specialize deserialization of the x10::lang::region hierarchy
    template<> inline ref<x10::lang::region> _deserialize_value_ref<x10::lang::region>(serialization_buffer& buf) {
        return _deserialize_superclass<x10::lang::region>(buf);
        //switch (buf.peek<int>()) {
        //case x10::lang::_region_empty::SERIALIZATION_ID: return _deserialize_ref<x10::lang::_region_empty>(buf);
        //case x10::lang::_region<1>::SERIALIZATION_ID: return _deserialize_ref<x10::lang::_region<1> >(buf);
        //case x10::lang::_region<2>::SERIALIZATION_ID: return _deserialize_ref<x10::lang::_region<2> >(buf);
        //}
    }


    namespace lang {
        // x10.lang.place
        class place { // value
        public:
            const x10_int x10__id;
            place(x10_int id) : x10__id(id) { }
            operator x10_int() const { return x10__id; }
            static place places(x10_int id) { return id; }
            place next() const { return abs((x10__id + 1) % x10__MAX_PLACES); }
            place prev() const { return abs((x10__id - 1) % x10__MAX_PLACES); }
            const place* operator->() const { return this; }
            place(const place& p) : x10__id(p.x10__id) { }
            const place& operator=(const place& p) {
                const_cast<x10_int&>(x10__id) = p.x10__id;
                return *this;
            }
            static const x10_int x10__MAX_PLACES;
            static const place x10__FIRST_PLACE;
        private:
            static void __init__MAX_PLACES();
            friend class x10::__init__;
        public: // Serialization
            void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) {
                buf.write(x10__id);
                _S_("Written " << x10__id);
            }
            void _deserialize_fields(serialization_buffer& buf) { assert (false); }
        };
        inline place here() { return (place) x10lib::here(); }
    }

    // Specialized serialization of x10::lang::place
    template<> struct _reference_serializer<x10::lang::place> {
        static void _(const ref<x10::lang::place>& v, serialization_buffer& buf, addr_map& m) {
            _Sd_(size_t len = buf.length());
            _S_("Serializing " << DEMANGLE(TYPENAME(x10::lang::place)));
            v->_serialize_fields(buf, m);
            _S_(x10::_dump_chars(((const char*)buf) + len, buf.length() - len));
        }
    };
    template<> struct _reference_deserializer<x10::lang::place> {
        static ref<x10::lang::place> _(serialization_buffer& buf) {
            // TODO: null
            x10_int id = buf.read<x10_int>();
            _S_("Deserializing place " << id);
            return (x10::lang::place) id;
        }
    };

    // Specialize the ref<place>(place&) constructor
    template<> inline ref<x10::lang::place>::ref(const x10::lang::place& val)
        : REF_INIT(new (x10::alloc<x10::lang::place>()) x10::lang::place(val))
    {
        _R_("Copying '" << val << "' (" << (void*)&val << ") to " << this);
        INC(_val);
    }

    namespace lang {
        // x10.lang.dist
        class _dist_unique;
        class dist : public x10::lang::Object { // value
        public: // [IP] temporary
            virtual place _translate(x10_index_t idx) const { return _translate(_point<1>(idx)); }
            virtual place _translate(const point& p) const = 0;
            virtual x10::ref<dist> _restrict(const place& p) const = 0;
        public:
            const x10_int x10__rank; // transient?
            const x10::ref<x10::lang::region> x10__region;
            dist(int rank, const x10::ref<x10::lang::region>& region, const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::lang::dist")), x10__rank(rank), x10__region(region) {
                assert (rank == region->x10__rank);
            }
            Iterator<point>& iterator() const { return x10__region->iterator(); }
            place operator[](x10_index_t idx) const { return _translate(idx); }
            place operator[](const x10::ref<point>& p) const { return _translate(*p); }
            place get(const x10::ref<point>& p) const { return _translate(*p); }
            x10::ref<dist> operator|(const place& p) const { return _restrict(p); }
            static const x10::ref<_dist_unique> x10__UNIQUE;
        private:
            static void __init__UNIQUE();
            friend class x10::__init__;
        public: // Serialization
            static const int SERIALIZATION_ID = 0;
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            virtual void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) {
                if (!m.ensure_unique(x10__region)) assert (false);
                x10::_serialize_value_ref(buf, m, x10__region);
                _S_("Serialized x10__region");
            }
            virtual void _deserialize_fields(serialization_buffer& buf) {
                const_cast<x10::ref<region>&>(x10__region) = x10::_deserialize_value_ref<region>(buf);
                const_cast<x10_int&>(x10__rank) = x10__region->x10__rank;
            }
            explicit dist(x10::SERIALIZATION_MARKER m) : x10__rank(0), x10__region(NULL) { }
            static x10::subclass_vector _registered_subclasses;
        };
        inline x10::ref<dist> operator|(x10::ref<dist> d, place p) { return *d | p; }
        template<int R> class _dist : public dist { // value
        public:
            _dist(const x10::ref<_region<R> >& region, const char* _t) : dist(R, region, _t?_t:TYPEID(*this,DIM_CONCAT("x10::lang::_dist<",R,">"))) { }
        };
        // FIXME: A hacked-up unique distribution; either places 0..N or a single place
        class _dist_unique : public _dist<1> { // value
        protected:
            place _translate(x10_index_t idx) const {
                return (place) idx;
            }
            place _translate(const point& p) const {
                return (place) p[0];
            }
            x10::ref<dist> _restrict(const place& p) const;
            _dist_unique(const _dist_unique& d) : _dist<1>((const x10::ref<_region<1> >)d.x10__region, TYPEID(*this,"x10::lang::_dist_unique")) {}
            const _dist_unique& operator=(const _dist_unique&) { return *this; }
        public:
            explicit _dist_unique(x10_int nplaces) : _dist<1>(new (x10::alloc<_region<1> >()) _region<1>(0, nplaces-1), TYPEID(*this,"x10::lang::_dist_unique")) { } // FIXME: GC
            friend class dist;
        public: // Serialization
            static const int SERIALIZATION_ID = 1;
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            virtual void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) {
                // Do not call super::_serialize_fields()
                const x10::ref<_region<1> > r = (const x10::ref<_region<1> >) x10__region;
                x10_int p_s = r->size();
                buf.write(p_s);
                _S_("Written " << p_s);
            }
            virtual void _deserialize_fields(serialization_buffer& buf) { assert (false); }
        //private:
            class __clinit { public: __clinit() { x10::_register_subclass<dist, _dist_unique>(); } };
            static const __clinit clinit;
        };
    }

    // Specialize for optimized serialization values
    template<> struct _reference_deserializer<x10::lang::_dist_unique> {
        static ref<x10::lang::_dist_unique> _(serialization_buffer& buf) {
            int id = buf.read<int>();
            assert (id == x10::lang::_dist_unique::SERIALIZATION_ID);
            x10_int p_s = buf.read<x10_int>();
            _S_("Deserializing x10::lang::_dist_unique over [0," << p_s-1 << "]");
            return x10::ref<x10::lang::_dist_unique>(new (x10::alloc<x10::lang::_dist_unique>()) x10::lang::_dist_unique(p_s));
        }
    };

    namespace lang {
        class _dist_empty : public dist { // value
        protected:
            place _translate(x10_index_t idx) const {
#ifndef NO_BOUNDS_CHECKS
                _check_rank(1, x10__rank);
#ifndef NO_EXCEPTIONS
                throw x10::ref<IndexOutOfBoundsException>(new (x10::alloc<IndexOutOfBoundsException>()) IndexOutOfBoundsException(String(idx) + String(" out of {}")));
#endif
#endif
#if defined(NO_BOUNDS_CHECKS) || defined(NO_EXCEPTIONS)
                return place(0); // quell compiler warning
#endif
            }
            place _translate(const point& p) const {
#ifndef NO_BOUNDS_CHECKS
                _check_rank(p.x10__rank, x10__rank);
#ifndef NO_EXCEPTIONS
                throw x10::ref<IndexOutOfBoundsException>(new (x10::alloc<IndexOutOfBoundsException>()) IndexOutOfBoundsException(String(p[0]) + String(" out of {}")));
#endif
#endif
#if defined(NO_BOUNDS_CHECKS) || defined(NO_EXCEPTIONS)
                return place(0); // quell compiler warning
#endif
            }
            x10::ref<dist> _restrict(const place& p) const {
                return this;
            }
            _dist_empty(const _dist_empty& d) : dist(d.x10__rank, d.x10__region, TYPEID(*this,"x10::lang::_dist_empty")) {}
            const _dist_empty& operator=(const _dist_empty&) { return *this; }
            explicit _dist_empty(int rank) : dist(rank, _region_empty::EMPTY(rank), TYPEID(*this,"x10::lang::_dist_empty")) { }
            static _dist_empty* _empty[MAX_EMPTY_RANK+1];
        public:
            static x10::ref<_dist_empty> EMPTY(int rank) {
                if (rank > MAX_EMPTY_RANK)
                    return new (x10::alloc<_dist_empty>()) _dist_empty(rank); // FIXME: GC
                if (_empty[rank] == NULL) {
                    _empty[rank] = new (x10::alloc<_dist_empty>()) _dist_empty(rank); // FIXME: GC
                }
                return _empty[rank];
            }
            friend class region;
        public: // Serialization
            static const int SERIALIZATION_ID = 2;
            // Do not call super::_serialize_fields()
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            virtual void _deserialize_fields(serialization_buffer& buf) { assert (false); }
        //private:
            class __clinit { public: __clinit() { x10::_register_subclass<dist, _dist_empty>(); } };
            static const __clinit clinit;
        };
    }

    // Specialize for interned values
    template<> struct _reference_serializer<x10::lang::_dist_empty> {
        static void _(const ref<x10::lang::_dist_empty>& v, serialization_buffer& buf, addr_map& m) {
            _Sd_(size_t len = buf.length());
            _S_("Serializing _dist_empty[" << v->x10__rank << "]");
            buf.write(x10::lang::_dist_empty::SERIALIZATION_ID);
            _S_("Written " << x10::lang::_dist_empty::SERIALIZATION_ID);
            buf.write(v->x10__rank);
            _S_("Written " << v->x10__rank);
            _S_(x10::_dump_chars(((const char*)buf) + len, buf.length() - len));
        }
    };
    template<> struct _reference_deserializer<x10::lang::_dist_empty> {
        static ref<x10::lang::_dist_empty> _(serialization_buffer& buf) {
            int id = buf.read<int>();
            assert (id == x10::lang::_dist_empty::SERIALIZATION_ID);
            int rank = buf.read<int>();
            _S_("Deserializing x10::lang::_dist_empty[" << rank << "]");
            return x10::lang::_dist_empty::EMPTY(rank);
        }
    };

    namespace lang {
        class _dist_local : public dist { // value
            place _pl;
        protected:
            place _translate(x10_index_t idx) const {
#ifndef NO_BOUNDS_CHECKS
                _check_rank(1, x10__rank);
#endif
                return _pl;
            }
            place _translate(const point& p) const {
#ifndef NO_BOUNDS_CHECKS
                _check_rank(p.x10__rank, x10__rank);
#endif
                return _pl;
            }
            x10::ref<dist> _restrict(const place& p) const {
                if (p.x10__id == _pl.x10__id)
                    return this;
                return _dist_empty::EMPTY(x10__rank);
            }
            _dist_local(const _dist_local& d) : dist(d.x10__rank, d.x10__region, TYPEID(*this,"x10::lang::_dist_local")), _pl(d._pl) {}
            const _dist_local& operator=(const _dist_local&) { return *this; }
        public:
            explicit _dist_local(const x10::ref<x10::lang::region>& region, const x10::lang::place& pl = here()) : dist(region->x10__rank, region, TYPEID(*this,"x10::lang::_dist_local")), _pl(pl) { }
            explicit _dist_local(const x10::ref<x10::lang::region>& region, const x10::ref<x10::lang::place> pl) : dist(region->x10__rank, region, TYPEID(*this,"x10::lang::_dist_local")), _pl(*pl) { }
            friend class region;
        public: // Serialization
            static const int SERIALIZATION_ID = 3;
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            virtual void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) {
                dist::_serialize_fields(buf, m);
                _pl._serialize(buf, m); // known class, non-ref
                _S_("Serialized _pl");
            }
            virtual void _deserialize_fields(serialization_buffer& buf) {
                dist::_deserialize_fields(buf);
                _pl = *x10::_deserialize_value_ref<place>(buf);
            }
            explicit _dist_local(x10::SERIALIZATION_MARKER m) : dist(m), _pl(-1) { }
        //private:
            class __clinit { public: __clinit() { x10::_register_subclass<dist, _dist_local>(); } };
            static const __clinit clinit;
        };
        class _dist_block : public dist { // value
            x10_index_t _block_size; // transient
            x10_index_t _overflow;   // transient
        protected:
            place _translate(x10_index_t idx) const {
#ifndef NO_BOUNDS_CHECKS
                _check_rank(1, x10__rank);
#endif
                x10_index_t crosspoint = _overflow * (_block_size+1);
                return idx < crosspoint ? idx / (_block_size + 1) : _overflow + (idx - crosspoint)/_block_size;
            }
            place _translate(const point& p) const {
#ifndef NO_BOUNDS_CHECKS
                _check_rank(p.x10__rank, x10__rank);
#endif
                x10_index_t idx = p[0];
                x10_index_t crosspoint = _overflow * (_block_size+1);
                return idx < crosspoint ? idx / (_block_size + 1) : _overflow + (idx - crosspoint)/_block_size;
            }
            x10::ref<dist> _restrict(const place& p) const {
#ifndef NO_EXCEPTIONS
                if (1 != x10__rank)
                    throw x10::ref<RuntimeException>(new (x10::alloc<RuntimeException>()) RuntimeException(String("Cannot restrict block distributions of rank ") + x10__rank));
#endif
                x10::ref<_region<1> > rgn = (x10::ref<_region<1> >) x10__region;
                x10_index_t size = rgn->size() / place::x10__MAX_PLACES;
                x10_int __here__ = (x10_int) here();
                x10::ref<_region<1> > r = new (x10::alloc<_region<1> >()) _region<1> (rgn->_lo + size*__here__, rgn->_lo + size*(__here__+1) - 1);  // FIXME: GC
                return new (x10::alloc<_dist_local>()) _dist_local(r, p);  // FIXME: GC
            }
            _dist_block(const _dist_block& d) : dist(d.x10__rank, d.x10__region, TYPEID(*this,"x10::lang::_dist_block")), _block_size(d._block_size), _overflow(d._overflow) {}
            const _dist_block& operator=(const _dist_block&) { return *this; }
        public:
            explicit _dist_block(const x10::ref<x10::lang::region>& region) : dist(region->x10__rank, region, TYPEID(*this,"x10::lang::_dist_block")), _block_size(region->size(0) / place::x10__MAX_PLACES), _overflow(region->size(0) % place::x10__MAX_PLACES) { }
            friend class region;
        public: // Serialization
            static const int SERIALIZATION_ID = 4;
            virtual void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
            //virtual void _serialize_fields(serialization_buffer& buf, x10::addr_map& m); // No override
            //virtual void _deserialize_fields(serialization_buffer& buf);           // No override
            explicit _dist_block(x10::SERIALIZATION_MARKER m) : dist(m) { }
        //private:
            class __clinit { public: __clinit() { x10::_register_subclass<dist, _dist_block>(); } };
            static const __clinit clinit;
        };

        // x10.lang.region.toDistribution()
        inline x10::ref<dist> region::toDistribution() const {
            return new (x10::alloc<_dist_local>()) _dist_local(this);
        }

        inline x10::ref<dist> _dist_unique::_restrict(const place& p) const {
            return new (x10::alloc<_dist_local>()) _dist_local(new (x10::alloc<_region<1> >()) _region<1>(p.x10__id, p.x10__id), p);  // FIXME: GC
        }
    }

    // Specialize deserialization of the x10::lang::dist hierarchy
    template<> inline ref<x10::lang::dist> _deserialize_value_ref<x10::lang::dist>(serialization_buffer& buf) {
        return _deserialize_superclass<x10::lang::dist>(buf);
        //switch (buf.peek<int>()) {
        //case x10::lang::_dist_unique::SERIALIZATION_ID: return _deserialize_ref<x10::lang::_dist_unique>(buf);
        //case x10::lang::_dist_empty::SERIALIZATION_ID: return _deserialize_ref<x10::lang::_dist_empty>(buf);
        //case x10::lang::_dist_local::SERIALIZATION_ID: return _deserialize_ref<x10::lang::_dist_local>(buf);
        //case x10::lang::_dist_block::SERIALIZATION_ID: return _deserialize_ref<x10::lang::_dist_block>(buf);
        //}
    }


    namespace lang {
        // x10.lang.clock
        class clock : public x10::lang::Object { // value?
        private:
            clock(const clock& c) { }
            const clock& operator=(const clock& c) { return *this; }
        public:
            clock() : x10::lang::Object(TYPEID(*this,"x10::lang::clock")) { }
            const clock* operator->() const { return this; }
        public: // Serialization
            // TODO: do clocks need to be serialized?
        };

        class ClockUseException : public RuntimeException { // FIXME: Just a place holder.
		 
	};
    } // x10::lang namespace

    // x10 array allocation
    template<class T> class x10array;
    template<class R> class _array_init;
    template<class T> x10array<T>* x10newArray(const ref<x10::lang::dist>& d, _array_init<T>* init = NULL);

    template<class T> void x10array_serialize_data(const x10array<T>& a, serialization_buffer& buf, x10::addr_map& m);
    template<class T> void x10array_deserialize_data(x10array<T>& a, serialization_buffer& buf);

    // x10 arrays
    class __x10array : public x10::lang::Object {
    protected:
        explicit __x10array(const x10::ref<x10::lang::dist>& d, const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::__x10array")), x10__distribution(d), x10__region(d->x10__region), __fixup(d->x10__region->_translate(0)) { }
        virtual ~__x10array() { }
    public:
        const x10::ref<x10::lang::dist> x10__distribution;
        const x10::ref<x10::lang::region> x10__region;
    public: // [IP] temporary
        const x10_index_t __fixup;
    };
    template<class T> class x10array : public __x10array { // possibly value
    public: // [IP] temporary
        T* const _data;
        const bool _isView;
    private:
        // TODO: construct the type name from the parameter type and '[]'
        x10array(const x10array<T>& arr) : __x10array(arr.distribution, TYPEID(*this,"x10::array<T>")),
            _data(arr._data), _isView(arr._isView) { }
        x10array(const x10array<T>& arr, const x10::ref<x10::lang::dist>& d) :
            __x10array(d, TYPEID(*this,"x10::array<T>")), _data(arr._data), _isView(true) { }
    public:
        explicit x10array(const x10::ref<x10::lang::dist>& d) : __x10array(d, TYPEID(*this,"x10::x10array<T>")),
            _data(new (x10::alloc<T>(d->x10__region->size()*sizeof(T))) T[d->x10__region->size()]),
            _isView(false) { }
        ~x10array() {
            if (_isView) return;
            for (int i = 0; i < x10__region->size(); i++) _data[i].~T(); x10::dealloc(_data);
        }
        const T& operator[](x10_index_t index) const;
        T& operator[](x10_index_t index);
        const T& operator[](const x10::lang::point& p) const;
        T& operator[](const x10::lang::point& p);
        const T& operator[](const x10::ref<x10::lang::point>& p) const { return this->operator[](*p); }
//        T& get(x10_index_t i, ...) { va_list c; va_start(c, i); /* TODO */ }
//        const T& get(x10_index_t i, ...) const { va_list c; va_start(c, i); /* TODO */ }
        T& operator[](const x10::ref<x10::lang::point>& p) { return this->operator[](*p); }
        T* raw() { return _data; }
        T* rawRegion() { return _data + __fixup; } // FIXME: assumes rectangular
        friend x10array<T>* x10newArray<>(const ref<x10::lang::dist>& d, _array_init<T>* init);
        // TODO
        //friend void x10freeArray<>(x10array<T>* arr);
        x10::ref<x10array<T> > local() {
            return new (x10::alloc<x10array<T> >())                       // FIXME: GC
                       x10array<T>(*this, new                             // FIXME: GC
                               (x10::alloc<x10::lang::_dist_local>())     // FIXME: GC
                               x10::lang::_dist_local(new                 // FIXME: GC
                                   (x10::alloc<x10::lang::_region<1> >()) // FIXME: GC
                                   x10::lang::_region<1>(0, x10__region->size())));
        }
    public: // Serialization
        void _serialize(serialization_buffer& buf, x10::addr_map& m) { x10::_serialize_ref(this, buf, m); }
        friend void x10array_serialize_data<>(const x10array<T>& a, serialization_buffer& buf, x10::addr_map& m);
        void _serialize_fields(serialization_buffer& buf, x10::addr_map& m) {
            if (!m.ensure_unique(x10__distribution)) assert (false);
            x10::_serialize_value_ref(buf, m, x10__distribution);
            _S_("Serialized x10__distribution");
            // TODO: avoid copying the elements if primitive
            x10array_serialize_data(*this, buf, m);
        }
        friend void x10array_deserialize_data<>(x10array<T>& a, serialization_buffer& buf);
        void _deserialize_fields(serialization_buffer& buf) { assert (false); }
    };

    // Specialized serialization of x10::x10array
    template<class T> struct _reference_serializer<x10::x10array<T> > {
        static void _(const ref<x10::x10array<T> >& v, serialization_buffer& buf, addr_map& m) {
            _Sd_(size_t len = buf.length());
            _S_("Serializing " << DEMANGLE(TYPENAME(x10::x10array<T>)));
            v->_serialize_fields(buf, m);
            _S_(x10::_dump_chars(((const char*)buf) + len, buf.length() - len));
        }
    };
    template<class T> struct _reference_deserializer<x10::x10array<T> > {
        static ref<x10::x10array<T> > _(serialization_buffer& buf) {
            // TODO: null
            x10::ref<x10::lang::dist> d = x10::_deserialize_value_ref<x10::lang::dist>(buf);
            _S_("Deserializing " << DEMANGLE(TYPENAME(x10::x10array<T>)));
            x10::ref<x10array<T> > a = x10newArray<T>(d);
            x10array_deserialize_data(*a, buf);
            return a;
        }
    };

    // This is in a separate template so it can be partially specialized
    template<class T> void x10array_serialize_data(const x10array<T>& a, serialization_buffer& buf, x10::addr_map& m) {
        for (int i = 0; i < a.x10__region->size(); i++)
            buf.write(a._data[i]);
        _S_("Written " << a.x10__region->size() << " values of type " << DEMANGLE(TYPENAME(T)));
    }
    // This is in a separate template so it can be partially specialized
    template<class T> void x10array_deserialize_data(x10array<T>& a, serialization_buffer& buf) {
        for (int i = 0; i < a.x10__region->size(); i++)
            a._data[i] = buf.read<T>();
    }
    // Reference version
    template<class T> void x10array_serialize_data(const x10array<ref<T> >& a, serialization_buffer& buf, x10::addr_map& m) {
        for (int i = 0; i < a.x10__region->size(); i++) {
            if (!m.ensure_unique(a._data[i])) assert (false);
            _serialize_value_ref(buf, m, a._data[i]);
        }
        _S_("Serialized " << a.x10__region->size() << " values of type " << DEMANGLE(TYPENAME(ref<T>)));
    }
    // Reference version
    template<class T> void x10array_deserialize_data(x10array<ref<T> >& a, serialization_buffer& buf) {
        for (int i = 0; i < a.x10__region->size(); i++)
            a._data[i] = _deserialize_value_ref<T>(buf);
    }

    //////////////////////////////////////////////////////////////
    // x10 array implementation
    //////////////////////////////////////////////////////////////

    template<class T> inline const T& x10array<T>::operator[](const x10::lang::point& p) const {
////        cerr << "x10array->[] const: applying to " << p << endl;
////        cerr << "x10array->[] const: region rank is " << this->x10__region->x10__rank << endl;
////        cerr << "x10array->[] const: point rank is " << p.rank << endl;
#ifndef NO_BOUNDS_CHECKS
        x10::_check_rank(p.x10__rank, x10__region->x10__rank);
        x10__region->_check_bounds(p);
#endif
//        int t_idx = this->x10__region->_translate(p);
////        cerr << "x10array->[] const: translated index " << t_idx << endl;
//        return this->_data[t_idx];
        return this->_data[this->x10__region->_translate(p)];
    }

    template<class T> inline T& x10array<T>::operator[](const x10::lang::point& p) {
////        cerr << "x10array->[]: applying to " << p << endl;
////        cerr << "x10array->[]: region rank is " << this->x10__region->x10__rank << endl;
////        cerr << "x10array->[]: point rank is " << p.rank << endl;
#ifndef NO_BOUNDS_CHECKS
        x10::_check_rank(p.x10__rank, x10__region->x10__rank);
        x10__region->_check_bounds(p);
#endif
        return this->_data[this->x10__region->_translate(p)];
    }

    template<class T> inline const T& x10array<T>::operator[](x10_index_t index) const {
////        cerr << "x10array->[] const: applying to " << index << endl;
////        cerr << "x10array->[] const: region rank is " << this->x10__region->x10__rank << endl;
#ifndef NO_BOUNDS_CHECKS
        x10::_check_rank(1, x10__region->x10__rank);
        x10__region->_check_bounds(index);
#endif
//        const x10::lang::_point<1>& p = x10::lang::_point<1>(index);
////        cerr << "x10array->[] const: point is (" << p[0] << ")" << endl;
//        int t_idx = this->x10__region->_translate(p);
////        cerr << "x10array->[] const: translated index " << t_idx << endl;
//        return this->_data[t_idx];
        return this->_data[this->x10__region->_translate(index)];
    }

    template<class T> inline T& x10array<T>::operator[](x10_index_t index) {
////        cerr << "x10array->[]: applying to " << index << endl;
////        cerr << "x10array->[]: region rank is " << this->x10__region->rank << endl;
#ifndef NO_BOUNDS_CHECKS
        x10::_check_rank(1, x10__region->x10__rank);
        x10__region->_check_bounds(index);
#endif
        return this->_data[this->x10__region->_translate(index)];
    }

    // reduction operations
    template<class T> void sum(T& acc, const T& val) { acc += val; }
    template<class T> void mul(T& acc, const T& val) { acc *= val; }
    template<class T> void max(T& acc, const T& val) { if (val > acc) acc = val; }
    template<class T> void min(T& acc, const T& val) { if (val < acc) acc = val; }

    // x10.lang.Runtime.exitCode
    extern x10_int exitCode;

    namespace lang {
        // x10.lang.Runtime
        class Runtime {
        private:
            Runtime() { } // Cannot instantiate
        public:
            template<class T> static void arrayCopy(x10::ref<x10::x10array<T> > src, x10_index_t srcoffset,
                    x10::ref<x10::x10array<T> > dest, x10_index_t destoffset, x10_index_t length);
            template<class T> static void arrayCopy(x10::ref<x10::x10array<T> > src,
                    x10::ref<x10::x10array<T> > dest);
            static void setExitCode(x10_int ret) { x10::exitCode = ret; }
	    static void sleep(x10_int secs) { ::sleep(secs);}
        };

        template<class T> void Runtime::arrayCopy(x10::ref<x10::x10array<T> > src,
                x10::ref<x10::x10array<T> > dest)
        {
            if (src->x10__region->size() != dest->x10__region->size())
                throw x10::ref<RuntimeException>(new (x10::alloc<RuntimeException>()) RuntimeException(String("Arrays must be of the same size")));
            arrayCopy(src, 0, dest, 0, src->x10__region->size());
        }

        template<class T> void Runtime::arrayCopy(x10::ref<x10::x10array<T> > src, x10_index_t srcoffset,
                x10::ref<x10::x10array<T> > dest, x10_index_t destoffset, x10_index_t length)
        {
            x10_index_t soff = src->x10__region->translate(srcoffset);
            x10_index_t doff = dest->x10__region->translate(destoffset);
            // TODO: distributed copy
            for (int i = 0; i < length; i++) {
                dest->_data[doff+i] = src->_data[soff+i];
            }
        }

        // x10.lang.RemoteDoubleArrayCopier
        class RemoteDoubleArrayCopier {
        public:
            RemoteDoubleArrayCopier() { }
            virtual x10::ref<x10::x10array<x10_double> > getDestArray() = 0;
            virtual x10::ref<x10::x10array<x10_double> > getSourceArray() = 0;
            virtual void postCopyRun(x10_int s) = 0;
        };
    }

    // Java array allocation
    template<class T> extern array<T>* alloc_array(x10_int length);
    template<class T> extern void free_array(array<T>* arr);
    // Java arrays
    class __array : public x10::lang::Object {
    public:
        const x10_int x10__length;
//        __array* operator->() { return this; }
//        const __array* operator->() const { return this; }
    protected:
        // Hack to quell compiler error
        __array(const char* _t = NULL) : Object(_t?_t:TYPEID(*this,"x10::__array")), x10__length(0) { }
        virtual ~__array() { }
    };
    // Java arrays
    template<class T> class array : public __array {
    public: // [IP] temporary
        T _data[1];
    private:
        array(const array<T>& arr) : __array(TYPEID(*this,"x10::array<T>")) { }
    public:
        array() : __array(TYPEID(*this,"x10::array<T>")) { }
        ~array() { for (int i = 0; i < x10__length; i++) _data[i].~T(); }
        const T& operator[](x10_int index) const;
        T& operator[](x10_int index);
        T* raw() { return _data; }
	T& get(x10_int index) { return operator[](index);}
	void set(T& x, x10_int index) { operator[](index)=x;}
        friend array<T>* alloc_array<T>(x10_int length);
        friend void free_array<>(array<T>* arr);
    };

    //////////////////////////////////////////////////////////////
    // Java array implementation
    //////////////////////////////////////////////////////////////

    template<class T> inline const T& array<T>::operator[](x10_int index) const {
        // TODO: bounds check
        return this->_data[index];
    }

    template<class T> inline T& array<T>::operator[](x10_int index) {
        // TODO: bounds check
        return this->_data[index];
    }

    //////////////////////////////////////////////////////////////
    // Array allocation implementation
    //////////////////////////////////////////////////////////////

    template<class T> array<T>* alloc_array(x10_int length) {
//        cerr << "alloc_array: allocating " << length << " elements" << endl;
        // The code below is the same as
        //   size_t size = sizeof(array<T>);
        // but ensures alignment
        array<T>* obj = NULL;
        size_t size = (size_t) (obj->_data);
        size += length * sizeof(T);
        array<T>* arr = new (x10::alloc<array<T> >(size)) array<T>();
        *(const_cast<x10_int *> (&arr->x10__length)) = length;
//        cerr << "alloc_array: allocated " << arr->x10__length << " elements" << endl;
        _M_("In alloc_array<" << DEMANGLE(TYPENAME(T)) << ">: arr = " << (void*)arr << "; length = " << length);
        return arr;
    }

    template<class T> array<T>* alloc_array(x10_int length, const T& v0) {
        array<T>* arr = alloc_array<T>(length);
        assert (length > 0);
        (*arr)[0] = v0;
        return arr;
    }

    template<class T> array<T>* alloc_array(x10_int length, const T& v0, const T& v1) {
        array<T>* arr = alloc_array<T>(length);
        assert (length > 1);
        (*arr)[0] = v0;
        (*arr)[1] = v1;
        return arr;
    }

    template<class T> array<T>* alloc_array(x10_int length, const T& v0, const T& v1, const T& v2) {
        array<T>* arr = alloc_array<T>(length);
        assert (length > 2);
        (*arr)[0] = v0;
        (*arr)[1] = v1;
        (*arr)[2] = v2;
        return arr;
    }

    template<class T> array<T>* alloc_array(x10_int length, const T& v0, const T& v1, const T& v2, const T& v3) {
        array<T>* arr = alloc_array<T>(length);
        assert (length > 3);
        (*arr)[0] = v0;
        (*arr)[1] = v1;
        (*arr)[2] = v2;
        (*arr)[3] = v3;
        return arr;
    }

    template<class T> array<T>* alloc_array(x10_int length, const T& v0, const T& v1, const T& v2, const T& v3, const T& v4) {
        array<T>* arr = alloc_array<T>(length);
        assert (length > 4);
        (*arr)[0] = v0;
        (*arr)[1] = v1;
        (*arr)[2] = v2;
        (*arr)[3] = v3;
        (*arr)[4] = v4;
        return arr;
    }

    template<class T> array<T>* alloc_array(x10_int length, const T& v0, const T& v1, const T& v2,
                                            const T& v3, const T& v4, const T& v5)
    {
        array<T>* arr = alloc_array<T>(length);
        assert (length > 5);
        (*arr)[0] = v0;
        (*arr)[1] = v1;
        (*arr)[2] = v2;
        (*arr)[3] = v3;
        (*arr)[4] = v4;
        (*arr)[5] = v5;
        return arr;
    }

    // This version will only work for primitive types
    template<class T> array<T>* alloc_array(x10_int length, const T& v0, const T& v1, const T& v2,
                                            const T& v3, const T& v4, const T& v5, const T& v6, ...)
    {
        array<T>* arr = alloc_array<T>(length);
        assert (length > 6);
        (*arr)[0] = v0;
        (*arr)[1] = v1;
        (*arr)[2] = v2;
        (*arr)[3] = v3;
        (*arr)[4] = v4;
        (*arr)[5] = v5;
        (*arr)[6] = v6;
        va_list init;
        va_start(init, v6);
        for (int i = 7; i < length; i++)
            (*arr)[i] = va_arg(init, T);
        va_end(init);
        return arr;
    }

    template<class T> void free_array(array<T>* arr) {
        arr->~array<T>();
        dealloc(arr);
    }

    //////////////////////////////////////////////////////////////
    // Object allocation implementation
    //////////////////////////////////////////////////////////////

    template<class T> T* alloc(size_t size) {
        _M_("Allocating " << size << " bytes of type " << DEMANGLE(TYPENAME(T*)));
        T* ret = (T*)::calloc(size, 1);
        _M_("\t-> " << ret);
        if (ret == NULL && size > 0)
            _M_("Out of memory allocating " << size << " bytes");
        return ret;
//        return (T*)::calloc(size, 1);
    }

    template<class T> void dealloc(T* obj) {
        _M_("Freeing chunk " << obj << " of type " << DEMANGLE(TYPENAME(T*)));
        ::free((void*) obj);
    }

//	namespace lang {
//		class __System__static {
//		private:
//			__System__static() : out(System::out), err(System::err) { }
//			~__System__static() {
//				if (_instance != NULL)
//					delete _instance;
//			}
//			static __System__static* _instance;
//		public:
//			static const __System__static& instance() {
//				if (_instance == NULL)
//					_instance = new (x10::alloc<__System__static>()) __System__static();
//				return *_instance;
//			}
//            const ref<java::io::PrintStream> out;
//            const ref<java::io::PrintStream> err;
//		};
//		extern const __System__static& System;
//	} // x10::lang namespace

    //////////////////////////////////////////////////////////////
    // Finish/async support
    //////////////////////////////////////////////////////////////

    extern int CS;
    void init();
    void shutdown();
#define TERMINATE_COMPUTATION 9999999
    int finish_start(int CS);
    void finish_end(ref<x10::lang::Exception> e);
    void clock_next();
    void async_poll();
    struct closure_args {
//        const int ___dummy;
//        closure_args() : ___dummy(0) { };
        closure_args() { };
        inline closure_args* ptr() { return this; }
    };

    //////////////////////////////////////////////////////////////
    // Array initializers
    //////////////////////////////////////////////////////////////

    // x10 array initializers
    template<class R> class _array_init {
    public:
        virtual R operator()(const x10::lang::point& p) const = 0;
        inline _array_init* ptr() { return this; }
    };

    template<class R> class __init_closure : public _array_init<R> {
        __init_closure(const __init_closure&) { }
    public:
//        __init_closure(R(* const _func)(void*,const x10::lang::point&), void* const _args)
        __init_closure(R(* const _func)(void*,x10::ref<x10::lang::point>), void* const _args)
            : func(_func), args(_args) { }
        virtual R operator()(const x10::lang::point& p) const { return func(args, &p); }
    private:
//        R(* const func)(void*,const x10::lang::point&);
        R(* const func)(void*,x10::ref<x10::lang::point>);
        void* const args;
    };

    //////////////////////////////////////////////////////////////
    // x10 array allocation implementation
    //////////////////////////////////////////////////////////////

//    template<class T> inline void x10initArray(x10array<T>* arr, T (*init)(void *, const x10::lang::point&), void* arg) { // }
    template<class T> /*inline*/ void x10initArray(x10array<T>* arr, _array_init<T>* init) {
        ref<x10::lang::region> rgn = arr->x10__distribution->x10__region;
        x10::lang::Iterator<x10::lang::point>& i = rgn->iterator();
//        cerr << "x10newArray: got iterator" << endl;
        for (; i.hasNext(); ) {
            const x10::lang::point& p = i.next();
//            cerr << "\titerating over point " << reinterpret_cast<const x10::lang::_point<1>&>(p)._i << endl;
//            T val = init(arg, p);
            T val = (*init)(p);
//            cerr << "\tfinished initializer" << endl;
            x10_index_t idx = rgn->_translate(p);
//            cerr << "\tobtained index: " << idx << endl;
            arr->_data[idx] = val;
//            cerr << "\tassigned value" << endl;
//            arr->_data[rgn->_translate(p)] = *init(p);
            dealloc(&p); // FIXME: make sure the initializer does not allow p to escape!
        }
//        delete (&i); // FIXME
        dealloc(&i);
    }

//    template<class T> inline x10array<T>* x10newArray(const ref<x10::lang::dist>& d, T (*init)(void *, const x10::lang::point&), void* arg) { // }
    template<class T> /*inline*/ x10array<T>* x10newArray(const ref<x10::lang::dist>& d, _array_init<T>* init) {
        ref<x10::lang::dist> d_r = d->_restrict(x10::lang::here());
        x10array<T>* arr = new (x10::alloc<x10array<T> >()) x10array<T>(d_r);
//        cerr << "x10newArray: allocated " << rgn->size() << " elements" << endl;
        if (init != NULL)
            x10initArray(arr, init);
        return arr;
    }

// [IP,PV]: this can be used for anything but field initializers.
// However, field initializers will not have an environment to capture (or, rather,
// will have trivial environment, i.e., static class scope for static fields, and "this" plus
// static class scope for instance fields).  So, use this for all array allocs except
// field inits, and pass an instance of __init_closure to x10newArray for field inits.
#define CONCAT(A,B) A##B
#define MakeName_(PFX,LINE) CONCAT(PFX,LINE)
#define MakeName(PFX) MakeName_(PFX,__LINE__)
////#define array_init(ret_type,body,args) \
////    ({ class MakeName(__array_init__) : public x10::_array_init<ret_type> {\
////       public: ret_type operator()args const body;\
////     } val; &val; })
//    // To allow x10::array_init(...)
//    template<class T> inline T __id__(T val) { return val; }
//// Warning: ret_type is repeated more than once in the macro below
//// [PV] args comes after body because they become available only after body traversal
//#define array_init(ret_type,body,args) \
//  __id__((x10::_array_init<ret_type>*)({ class MakeName(__array_init__) : public x10::_array_init<ret_type> {\
//             public: ret_type operator()args const body;\
//         } val; &val; }))

}; // x10 namespace

extern x10::lang::place __here__;

#define CONCAT(A,B) A##B

// [IP] TODO: replace the macros below with templates -- that's exactly what
// templates were designed for, AND they won't have the comma problem
#define async_name(id) CONCAT(async__,id)
#define async_closure(container, id, UnpackedBody, args) \
    inline void container::async_name(id)args UnpackedBody

#define init_name(id) CONCAT(__init__,id)
#define array_init_closure_and_args_struct(container, id, type, UnpackedBody, args, structure) \
    structure; \
    inline type container::init_name(id)args UnpackedBody

#define array_copy_name(id) CONCAT(array_copy__,id)
#define array_copy_closure_and_args_struct(container, id, type, UnpackedBody, args, structure) \
    structure; \
    inline type container::array_copy_name(id)args UnpackedBody

#define async_unpacked_body(body, unpack) { unpack body }

#define array_init_unpacked_body(ignorable, body, unpack) { unpack body }

#define args_name(closure) CONCAT(closure,_args)

#define async_args_struct(id, typesAndArgs) \
    struct args_name(async_name(id)) : public x10::closure_args { \
        /* TODO: constructor */ \
        typesAndArgs \
    };

// TODO: handle clocks
#define async_invocation(container, id, place, args) \
    do { \
        x10_int CONCAT(__PLACE,id) = place; \
        if (CONCAT(__PLACE,id) == __here__) container::async_name(id)args; \
        else x10lib::AsyncSpawnInline(CONCAT(__PLACE,id), id, args_name(async_name(id))args.ptr(), sizeof(args_name(async_name(id)))); \
    } while(0)

// TODO: handle clocks
#define agg_async_invocation(container, id, place, args) \
    do { \
        x10_int CONCAT(__PLACE,id) = place; \
        if (CONCAT(__PLACE,id) == __here__) container::async_name(id)args; \
        else x10lib::AsyncSpawnInlineAgg(CONCAT(__PLACE,id), id, args_name(async_name(id))args.ptr(), sizeof(args_name(async_name(id)))); \
    } while(0)

#define async_flush(id) \
    do { \
        x10lib::AsyncFlush(id, sizeof(args_name(async_name(id)))); \
    } while(0)

// TODO: handle clocks
#define async_array_copy_invocation(src, args, len, target) \
    do { \
        x10lib::AsyncArrayPut(src, args, len, target); \
    } while(0)

#define array_init_args_struct(id, typesAndArgs) \
    struct args_name(init_name(id)) { \
        /* TODO: constructor */ \
        typesAndArgs \
    };

//#define array_init_closure_invocation(id, distribution, args) \
//    x10::x10newArray(distribution, \
//            init_name(id), \
//            args_name(init_name(id))args.ptr(), \
//            sizeof(args_name(init_name(id))))

#define array_init_closure_invocation(id, distribution, type, args) \
    x10::x10newArray(distribution, \
            x10::__init_closure<type >(init_name(id), \
            args_name(init_name(id))args.ptr()).ptr())

#define closure_name(id) CONCAT(__closure__,id)
#define array_copy_closure_invocation(id, args) \
    new (x10::alloc<closure_name(id)>()) closure_name(id)args

#define variable_broadcast(addr, size) \
    do { \
        x10lib::Broadcast(addr, size); \
    } while(0)

// Warning: buf has to be a variable of type serialization_buffer
// TODO: move serialization_buffer to x10lib and rename to Buffer
#define buffer_broadcast(buf) \
    do { \
        const char* charbuf##__LINE__ = buf; \
        size_t buflen##__LINE__ = buf.length(); \
        charbuf##__LINE__ = (const char*) x10lib::Broadcast_buffer((void*)charbuf##__LINE__, buflen##__LINE__); \
        buf.set(charbuf##__LINE__); \
    } while(0)

namespace x10 {
    class __init__ {
        static int count;
    public:
        __init__() {
            if (count++ == 0) {
                init();
            }
//            cerr << "__init__; count = " << count << endl;
//            cerr << "Initialized System.in to " << x10::lang::System::in.operator->() << endl;
//            cerr << "Initialized System.out to " << x10::lang::System::out.operator->() << " and System.err to " << x10::lang::System::err.operator->() << endl;
            __here__ = x10::lang::here();
        }
        ~__init__() {
            if (--count == 0) {
                shutdown();
            }
        }
        static void init() {
            x10::init();
            x10::lang::System::__init__in_out_err();
            x10::lang::place::__init__MAX_PLACES();
            x10::lang::dist::__init__UNIQUE();
//            cerr << "Initialized System.in to " << x10::lang::System::x10__in.operator->() << endl;
//            cerr << "Initialized System.out to " << x10::lang::System::x10__out.operator->() << " and System.err to " << x10::lang::System::x10__err.operator->() << endl;
        }
        static void shutdown() {
            x10::shutdown();
        }
    };
    static __init__ __init__counter;
};
namespace x10{
	namespace lang{
	    void general_finish_start();
	    void general_finish_end();
	}
}

#endif

typedef x10::lang::String     x10__String;
