#include "x10lang.h"
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <iostream>

using namespace std;

//////////////////////////////////////////////////////////////
// java::lang::Boolean implementation
//////////////////////////////////////////////////////////////

x10_boolean java::lang::Boolean::parseBoolean(const x10::ref<x10::lang::String>& s) {
    return s != NULL && !strcasecmp(((const string&)(*s)).c_str(), "true");
}

//////////////////////////////////////////////////////////////
// java::lang::Integer implementation
//////////////////////////////////////////////////////////////

x10_int java::lang::Integer::parseInt(const x10::ref<x10::lang::String>& s) {
    // FIXME: what about null?
    return atoi(((const string&)(*s)).c_str());
}

//////////////////////////////////////////////////////////////
// java::lang::Long implementation
//////////////////////////////////////////////////////////////

x10_long java::lang::Long::parseLong(const x10::ref<x10::lang::String>& s) {
    // FIXME: what about null?
    return atoll(((const string&)(*s)).c_str());
}

//////////////////////////////////////////////////////////////
// java::lang::Float implementation
//////////////////////////////////////////////////////////////

x10_float java::lang::Float::parseFloat(const x10::ref<x10::lang::String>& s) {
    // FIXME: what about null?
    return atof(((const string&)(*s)).c_str());
}

x10_int java::lang::Float::floatToIntBits(x10_float v) {
    // FIXME: check that this is correct w.r.t. endianness
    return *(x10_int*)&v;
}

x10_float java::lang::Float::intBitsToFloat(x10_int v) {
    // FIXME: check that this is correct w.r.t. endianness
    return *(x10_float*)&v;
}

//////////////////////////////////////////////////////////////
// java::lang::Double implementation
//////////////////////////////////////////////////////////////

x10_long java::lang::Double::doubleToLongBits(x10_double v) {
    // FIXME: check that this is correct w.r.t. endianness
    return *(x10_long*)&v;
}

x10_double java::lang::Double::longBitsToDouble(x10_long v) {
    // FIXME: check that this is correct w.r.t. endianness
    return *(x10_double*)&v;
}

namespace x10 {

//////////////////////////////////////////////////////////////
// x10::lang::Object implementation
//////////////////////////////////////////////////////////////

ref<x10::lang::String> x10::lang::Object::toString() {
    char buf[1000]; // FIXME
//    ::snprintf(buf, 1000, "%s@%x", "x10::lang::Object", this); // TODO: RTTI
    ::snprintf(buf, 1000, "%s@%lx", DEMANGLE(TYPENAME(*this)), (unsigned long)this);
    return new (x10::alloc<x10::lang::String>()) x10::lang::String(buf);
}

template<> ref<x10::lang::String>::ref(const x10::lang::String& val)
    : REF_INIT(new (x10::alloc<x10::lang::String>()) x10::lang::String(val))
{
    _R_("Copying '" << val << "' (" << (void*)&val << ") to " << this);
    INC(_val);
}

static x10::lang::String nullString("null");

// operator+(x10.ref<String>, x10.ref<String>)
template<> ref<x10::lang::String> operator+(const ref<x10::lang::String>& s1, const ref<x10::lang::String>& s2) {
//    cerr << "ref<String>::operator+: concatenating " << (const string&)*s1 << " and " << (const string&)*s2 << endl;
    // FIXME: HACK!
    if (s1.isNull()) {
        if (s2.isNull())
            return new (x10::alloc<x10::lang::String>()) x10::lang::String(nullString + nullString);
        else
            return new (x10::alloc<x10::lang::String>()) x10::lang::String(nullString + *s2);
    } else {
        if (s2.isNull())
            return new (x10::alloc<x10::lang::String>()) x10::lang::String(*s1 + nullString);
    }
    return new (x10::alloc<x10::lang::String>()) x10::lang::String(*s1 + *s2);
}

// assert implementation
void x10__assertion_failed(const ref<x10::lang::String>& message) {
    if (message != NULL)
        x10::lang::System::x10__err->println(message);
    else
        x10::lang::System::x10__err->println(x10::lang::String("Assertion failed"));
}

// x10.lang.Runtime.exitCode
x10_int exitCode = 0;

//////////////////////////////////////////////////////////////
// Command-line argument conversion implementation
//////////////////////////////////////////////////////////////

// FIXME: for some reason, the template operator[] is not instantiated unless
// it's used here
// [IP] I don't think the above is true anymore
array<ref<x10::lang::String> > *convert_args(int ac, char **av) {
    // TODO: assert that ac >= 1
    array<ref<x10::lang::String> >* arr = alloc_array<ref<x10::lang::String> >((x10_int)(ac-1));
//    cerr << "convert_args: allocated " << arr->length << " elements" << endl;
    for (int i = 1; i < ac; i++) {
//        cerr << "convert_args: allocating arg " << i << ": " << av[i] << endl;
        x10::lang::String* val = new (x10::alloc<x10::lang::String>()) x10::lang::String(av[i]);
//        cerr << "convert_args: allocated arg " << i << ": " << val._content << endl;
        (*arr)[i-1] = val;
//        cerr << "convert_args: assigned arg " << i << ": " << (*arr)[i-1]._content << endl;
    }
    return arr;
}

void free_args(array<ref<x10::lang::String> >* arr) {
//    cerr << "free_args: freeing " << arr->length << " elements" << endl;
//    x10_int length = arr->length;
//    cerr << "free_args: freeing array " << arr << endl;
    free_array(arr);
//    cerr << "free_args: freed array " << arr << endl;
}
}

namespace java {
namespace io {
    //////////////////////////////////////////////////////////////
    // java::io::OutputStream implementation
    //////////////////////////////////////////////////////////////

    void OutputStream::write(const x10::ref<x10::array<x10_byte> >& b) {
        this->write(b, 0, b->x10__length);
    }

    void OutputStream::write(const x10::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len) {
        for (x10_int i = 0; i < len; i++)
            this->write((x10_int) b->operator[](off + i));
    }

    void OutputStream::_printf(const char* format, ...) {
        va_list parms;
        va_start(parms, format);
        _vprintf(format, parms);
        va_end(parms);
    }

    void OutputStream::printf(const x10::ref<x10::lang::String>& format) {
        this->_printf("%s", ((const string&)(*format)).c_str());
        this->flush(); // FIXME [IP] temp
    }

    void OutputStream::printf(const x10::ref<x10::lang::String>& format, const x10::ref<x10::array<x10::ref<x10::lang::Object> > >& parms) {
        char* fmt = const_cast<char*>(((const string&)(*format)).c_str());
        char* next = NULL;
        for (x10_int i = 0; fmt != NULL; i++, fmt = next) {
            next = strchr(fmt+1, '%');
            if (next != NULL)
                *next = '\0';
            if (*fmt != '%') {
                this->_printf(fmt);
                if (next != NULL)
                    *next = '%';
                i--;
                continue;
            }
#ifndef NO_EXCEPTIONS
            if (i >= parms->x10__length)
                throw x10::ref<x10::lang::RuntimeException>(new (x10::alloc<x10::lang::RuntimeException>()) x10::lang::RuntimeException(x10::lang::String("Index out of bounds: ") + i + x10::lang::String(" out of (") + (x10_int)0 + x10::lang::String(",") + parms->x10__length + x10::lang::String(")")));
#endif
            const x10::ref<x10::lang::Object>& p = parms->operator[](i);
            if (p.isNull()) { } // Ignore nulls
            else if (INSTANCEOF(p, x10::ref<x10::lang::String>))
                this->_printf(fmt, ((const string&)(*((x10::ref<x10::lang::String>)p))).c_str());
            else if (INSTANCEOF(p, x10::ref<x10::compilergenerated::BoxedBoolean>))
                this->_printf(fmt, (*((x10::ref<x10::compilergenerated::BoxedBoolean>)p)).booleanValue());
            else if (INSTANCEOF(p, x10::ref<x10::compilergenerated::BoxedByte>))
                this->_printf(fmt, (*((x10::ref<x10::compilergenerated::BoxedByte>)p)).byteValue());
            else if (INSTANCEOF(p, x10::ref<x10::compilergenerated::BoxedCharacter>))
                this->_printf(fmt, (*((x10::ref<x10::compilergenerated::BoxedCharacter>)p)).charValue());
            else if (INSTANCEOF(p, x10::ref<x10::compilergenerated::BoxedShort>))
                this->_printf(fmt, (*((x10::ref<x10::compilergenerated::BoxedShort>)p)).shortValue());
            else if (INSTANCEOF(p, x10::ref<x10::compilergenerated::BoxedInteger>))
                this->_printf(fmt, (*((x10::ref<x10::compilergenerated::BoxedInteger>)p)).intValue());
            else if (INSTANCEOF(p, x10::ref<x10::compilergenerated::BoxedLong>))
                this->_printf(fmt, (*((x10::ref<x10::compilergenerated::BoxedLong>)p)).longValue());
            else if (INSTANCEOF(p, x10::ref<x10::compilergenerated::BoxedFloat>))
                this->_printf(fmt, (*((x10::ref<x10::compilergenerated::BoxedFloat>)p)).floatValue());
            else if (INSTANCEOF(p, x10::ref<x10::compilergenerated::BoxedDouble>))
                this->_printf(fmt, (*((x10::ref<x10::compilergenerated::BoxedDouble>)p)).doubleValue());
            else
                this->_printf(fmt, p.operator->());
            if (next != NULL)
                *next = '%';
        }
        this->flush(); // FIXME [IP] temp
    }

    void OutputStream::println() {
        this->_printf("\n");
    }

    void OutputStream::print(const x10::ref<x10::lang::String>& str) {
//        cerr << "OutputStream::println(\"" << ((const string&)(*str)).c_str() << "\")" << endl;
        if (str.isNull())
            this->_printf("null");
        else
            this->print(*str);
        this->flush(); // FIXME [IP] temp
    }

    void OutputStream::print(const x10::lang::String& str) {
//        cerr << "OutputStream::print(~\"" << ((const string&)str).c_str() << "\")" << endl;
        this->_printf("%s", ((const string&)str).c_str());
        this->flush(); // FIXME [IP] temp
    }

    void OutputStream::print(x10_boolean b) {
        this->_printf("%s", b ? "true" : "false");
    }

    void OutputStream::print(x10_int i) {
        this->_printf("%d", i);
    }

    void OutputStream::print(x10_long l) {
        this->_printf("%lld", l);
    }

    void OutputStream::print(x10_double d) {
        this->_printf("%llG", d);
//        this->print(x10::lang::String(d));
    }

    //////////////////////////////////////////////////////////////
    // java::io::ByteArrayOutputStream implementation
    //////////////////////////////////////////////////////////////

    void ByteArrayOutputStream::_vprintf(const char* format, va_list parms) {
        char buf[1000]; // FIXME
        ::vsnprintf(buf, 1000, format, parms);
        res += buf;
    }

    x10::ref<x10::lang::String> ByteArrayOutputStream::toString() {
        return x10::lang::String(res);
    }

    //////////////////////////////////////////////////////////////
    // java::io::PrintStream implementation
    //////////////////////////////////////////////////////////////

    // TODO [IP]
//    void PrintStream::print(const x10::ref<x10::lang::Object>& obj) {
//        if (obj.isNull())
//            this->_printf("null");
//        else {
//            x10::ref<x10::lang::String> str = const_cast<x10::ref<x10::lang::Object>&>(obj)->toString();
//            this->_printf("%s", ((const string&)(*str)).c_str());
//        }
//        this->flush(); // FIXME [IP] temp
//    }

    //////////////////////////////////////////////////////////////
    // java::io::FILEPtrStream implementation
    //////////////////////////////////////////////////////////////

    FILE* FILEPtrStream::open_file(const x10::ref<x10::lang::String>& name, const char* mode) {
        FILE* res = fopen(((const string&)(*name)).c_str(), mode);
#ifndef NO_EXCEPTIONS
        if (res == NULL) throw x10::ref<FileNotFoundException>(new (x10::alloc<FileNotFoundException>()) FileNotFoundException(name));
#endif
        return res;
    }

    void FILEPtrStream::close() {
        ::fclose(_stream);
    }

    //////////////////////////////////////////////////////////////
    // java::io::FILEPtrOutputStream implementation
    //////////////////////////////////////////////////////////////

    FILE* FILEPtrStream::check_stream(FILE* stream) {
#ifndef NO_EXCEPTIONS
        if (stream == NULL) throw x10::ref<IOException>(new (x10::alloc<IOException>()) IOException(x10::lang::String("Null file pointer")));
#endif
        return stream;
    }

    void FILEPtrOutputStream::_vprintf(const char* format, va_list parms) {
        ::vfprintf(_stream, format, parms);
    }

    void FILEPtrOutputStream::flush() {
        ::fflush(_stream);
    }

    void FILEPtrOutputStream::write(const x10::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len) {
        ::fwrite(((x10_byte*)b->raw())+off*sizeof(x10_byte), sizeof(x10_byte), len*sizeof(x10_byte), _stream);
    }

    void FILEPtrOutputStream::write(x10_int b) {
        ::fputc((char)b, _stream);
    }

    void FILEPtrOutputStream::write(const char* s) {
        ::fprintf(_stream, "%s", s);
    }

    //////////////////////////////////////////////////////////////
    // java::io::DataOutputStream implementation
    //////////////////////////////////////////////////////////////

    void DataOutputStream::writeBytes(const x10::ref<x10::lang::String>& s) {
        write(((const string&)*s).c_str());
    }

    // FIXME: UCS-2
    void DataOutputStream::writeChars(const x10::ref<x10::lang::String>& s) {
        const char* cs = ((const string&)*s).c_str();
        for (const char* c = cs; *c != '\0'; c++) {
            out->write((x10_int)0);
            out->write(*c);
        }
    }

    // FIXME: UTF-8
    void DataOutputStream::writeUTF(const x10::ref<x10::lang::String>& s) {
        x10_int len = s->length();
        out->write(len>>8&0xFF);
        out->write(len>>0&0xFF);
        const char* cs = ((const string&)*s).c_str();
        for (const char* c = cs; *c != '\0'; c++) {
            out->write(*c);
        }
    }

    //////////////////////////////////////////////////////////////
    // java::io::InputStream implementation
    //////////////////////////////////////////////////////////////

    x10_int InputStream::read(const x10::ref<x10::array<x10_byte> >& b) {
        return this->read(b, 0, b->x10__length);
    }

    x10_int InputStream::read(const x10::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len) {
        x10_int val;
        x10_int i;
        for (i = 0; i < len && (val = this->read()) != -1; i++)
            b->operator[](off + i) = (x10_byte) (val & 0xFF);
        return i;
    }

    //////////////////////////////////////////////////////////////
    // java::io::FILEPtrInputStream implementation
    //////////////////////////////////////////////////////////////

    x10_int FILEPtrInputStream::read(const x10::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len) {
        int res = ::fread(((x10_byte*)b->raw())+off*sizeof(x10_byte), sizeof(x10_byte), len*sizeof(x10_byte), _stream);
        return (x10_int)res;
    }

    x10_int FILEPtrInputStream::read() {
        int c = ::fgetc(_stream);
        return (x10_int)c;
    }

    void FILEPtrInputStream::gets(char* s, int num) {
        ::fgets(s, num, _stream);  // FIXME: check for errors
    }

    //////////////////////////////////////////////////////////////
    // java::io::DataInputStream implementation
    //////////////////////////////////////////////////////////////

    x10::ref<x10::lang::String> DataInputStream::readLine() {
        char buf[1000]; // FIXME
        this->gets(buf, 1000);
        return new (x10::alloc<x10::lang::String>()) x10::lang::String(buf); // FIXME: what about the trailing newline?
    }

    void DataInputStream::readFully(const x10::ref<x10::array<x10_byte> >& b) {
        this->readFully(b, 0, b->x10__length);
    }

    void DataInputStream::readFully(const x10::ref<x10::array<x10_byte> >& b, x10_int off, x10_int len) {
        do {
            x10_int num = read(b, off, len);
            off += num;
            len -= num;
        } while (len > 0);
    }

    // FIXME: UTF-8
    x10::ref<x10::lang::String> DataInputStream::readUTF() {
        x10_int length = (in->read()&0xFF)<<8 | (in->read()&0xFF);
        char buf[length];
        for (int i = 0; i < length; i++)
            buf[i] = in->read()&0xFF;
        return new (x10::alloc<x10::lang::String>()) x10::lang::String(buf);
    }

    const static FILEPtrOutputStream _stdout(stdout); // FIXME: create all objects with count=1
    const static FILEPtrOutputStream _stderr(stderr); // FIXME: create all objects with count=1
    const static FILEPtrInputStream sysin(stdin); // FIXME: create all objects with count=1
    const static PrintStream sysout(&_stdout); // FIXME: create all objects with count=1
    const static PrintStream syserr(&_stderr); // FIXME: create all objects with count=1
} // java::io namespace
}

x10::lang::place __here__ = x10::lang::here();

namespace x10 {
namespace lang {
    //////////////////////////////////////////////////////////////
    // x10::lang::System implementation
    //////////////////////////////////////////////////////////////

    const ref<java::io::InputStream> System::x10__in  = &java::io::sysin;
    const ref<java::io::PrintStream> System::x10__out = &java::io::sysout;
    const ref<java::io::PrintStream> System::x10__err = &java::io::syserr;
    void System::__init__in_out_err() {
//        cerr << "Initializing System.in to " << (void*)&java::io::sysin << endl;
//        cerr << "Initializing System.out to " << (void*)&java::io::sysout << " and System.err to " << (void*)&java::io::syserr << endl;
        const_cast<ref<java::io::InputStream>&>(System::x10__in)  = &java::io::sysin;
        const_cast<ref<java::io::PrintStream>&>(System::x10__out) = &java::io::sysout;
        const_cast<ref<java::io::PrintStream>&>(System::x10__err) = &java::io::syserr;
//        cerr << "Initialized System.in to " << System::x10__in.operator->() << endl;
//        cerr << "Initialized System.out to " << System::x10__out.operator->() << " and System.err to " << System::x10__err.operator->() << endl;
    }

    x10_long System::nanoTime() {
        struct timespec ts;
        // clock_gettime is POSIX!
        ::clock_gettime(CLOCK_REALTIME, &ts);
        return (x10_long)(ts.tv_sec * 1000000000LL + ts.tv_nsec);
    }
    
    x10_long System::currentTimeMillis() {
        struct timespec ts;
        // clock_gettime is POSIX!
        ::clock_gettime(CLOCK_REALTIME, &ts);
        return (x10_long)(ts.tv_sec * 1000L + ts.tv_nsec / 1000000);
    }

    void System::exit(x10_int ret) {
        exitCode = ret;
        // Cannot do ::exit here, as we'll need to clean up.
#ifndef NO_EXCEPTIONS
        throw ret;
#else
        // Hopefully the destructor will clean up.
        ::exit(ret);
#endif
        // We need not worry about any user code that looks like
        // catch(...), because --> We are generating code and will
        // never generate such code. Duh!
    }

    void System::loadLibrary(x10::ref<String> name) {
        // Do nothing -- everything should be linked statically
    }

    void System::load(x10::ref<String> name) {
        // Do nothing -- everything should be linked statically
    }

    void System::gc() {
        // Do nothing (for now)
    }

    //////////////////////////////////////////////////////////////
    // x10::lang::String implementation
    //////////////////////////////////////////////////////////////

    const string String::to_string(const x10_boolean b) {
        return string(b ? "true" : "false");
    }
    const string String::to_string(const x10_char c) {
        char buf[2];
        ::snprintf(buf, 2, "%c", c);
        return string(buf);
    }
    const string String::to_string(const x10_int i) {
        char buf[12];
        ::snprintf(buf, 12, "%d", (int)i);
        return string(buf);
    }
    const string String::to_string(const x10_long l) {
        char buf[24];
        ::snprintf(buf, 24, "%ld", (long)l);
        return string(buf);
    }
#define MAX_STR_LENGTH 120
//    static void mydtoa(char* buf, double value, int precision) {
//        const int DTOA_MODE = 2;
//        char result[MAX_STR_LENGTH];
//        int decpt, sign;
//        char *s, *d;
//        int i;
//
//        /* use mode 2 to get at the digit stream, all other modes are useless
//         * since mode 2 only gives us as many digits as we need in precision, we need to
//         * add the digits in front of the floating point to it, if there is more than one
//         * to be printed. That's the case if the value is going to be printed using the
//         * normal notation, i.e. if it is 0 or >= 1.0e-3 and < 1.0e7.
//         */
//        int digits_in_front_of_floating_point = (int)ceil(log10(value));
//
//        if (digits_in_front_of_floating_point > 1 && digits_in_front_of_floating_point < 7)
//            precision += digits_in_front_of_floating_point;
//
//        _dtoa(value, DTOA_MODE, precision, &decpt, &sign, NULL, buf, (int) false);
//        value = fabs(value);
//
//        s = buf;
//        d = result;
//
//        /* Handle negative sign */
//        if (sign)
//            *d++ = '-';
//
//        /* Handle normal represenation */
//        if ((value >= 1e-3 && value < 1e7) || (value == 0)) {
//            if (decpt <= 0)
//                *d++ = '0';
//            else {
//                for (i = 0; i < decpt; i++)
//                    if (*s)
//                        *d++ = *s++;
//                    else
//                        *d++ = '0';
//            }
//
//            *d++ = '.';
//
//            if (*s == 0) {
//                *d++ = '0';
//                decpt++;
//            }
//
//            while (decpt++ < 0)
//                *d++ = '0';
//
//            while (*s)
//                *d++ = *s++;
//
//            *d = 0;
//        }
//        /* Handle scientific representaiton */
//        else {
//            *d++ = *s++;
//            decpt--;
//            *d++ = '.';
//
//            if (*s == 0)
//                *d++ = '0';
//
//            while (*s)
//                *d++ = *s++;
//
//            *d++ = 'E';
//
//            if (decpt < 0) {
//                *d++ = '-';
//                decpt = -decpt;
//            }
//
//            char exp[4];
//            char *e = exp + sizeof exp;
//
//            *--e = 0;
//            do {
//                *--e = '0' + decpt % 10;
//                decpt /= 10;
//            }
//            while (decpt > 0);
//
//            while (*e)
//                *d++ = *e++;
//
//            *d = 0;
//        }
//
//        /* copy the result into the buffer */
//        memcpy(buf, result, MAX_STR_LENGTH);
//    }
//    static string double_to_string(const x10_double d) {
//        if (isnan(d))
//            return string("NaN");
//        if (isinf(d)) {
//            if (d > 0)
//                return string("Infinity");
//            else
//                return string("-Infinity");
//        }
//        char buf[MAX_STR_LENGTH];
//        const int MAX_PRECISION = 19;
//        for (int precision = 1; precision < MAX_PRECISION; precision++) {
//            /* Convert the value to a string and back. */
//            ::snprintf(buf, MAX_STR_LENGTH, "%.*llG", precision, (double)d);
////            mydtoa(buf, d, precision);
//            double check = ::atof(buf);
//            if (check == d)
//                break;
//        }
//        return string(buf);
//    }
#undef MAX_STR_LENGTH
    const string String::to_string(const x10_double d) {
        char buf[120];
        if ((x10_long)d==d)
            ::snprintf(buf, 120, "%llG.0", (double)d);
        else
            ::snprintf(buf, 120, "%llG", (double)d);
        return string(buf);
//        return double_to_string(d);
    }
    const string String::to_string(const ref<array<x10_char> >& value) {
        return to_string(value, 0, value->x10__length);
    }
    const string String::to_string(const ref<array<x10_char> >& value, x10_int offset, x10_int count) {
        return string(((char*)value->raw())+offset*sizeof(char), count);
    }
    const string String::to_string(const place& p) {
        return to_string(p.x10__id);
    }
    const string String::to_string(const ref<String>& s) {
        if (s.isNull())
            return nullString;
        else
            return *s;
    }

//    x10_boolean String::equals(const ref<Object>& obj) const {
//        // FIXME
//        return false;
//    }
    x10_boolean String::equals(const ref<String>& str) const {
        return !compare(dynamic_cast<const string&>(*str));
    }

    String String::operator+(const String& s) const {
//        cerr << "String::operator+: concatenating " << (const string&)*this << " and " << (const string&)s << endl;
        return String(dynamic_cast<const string&>(*this) + dynamic_cast<const string&>(s));
    }

    x10_int String::indexOf(const ref<String>& str, x10_int i) const {
        size_type res = find(dynamic_cast<const string&>(*str), (size_type)i);
        if (res == string::npos)
            return (x10_int) -1;
        return (x10_int) res;
    }

    x10_int String::indexOf(x10_char c, x10_int i) const {
        size_type res = find((char)c, (size_type)i);
        if (res == string::npos)
            return (x10_int) -1;
        return (x10_int) res;
    }

    String String::substring(x10_int start, x10_int end) const {
        return String(dynamic_cast<const string&>(*this).substr(start, end-start));
    }

    x10_char String::charAt(x10_int i) const {
        return (x10_char) at(i);
    }

    //////////////////////////////////////////////////////////////
    // x10::lang::StringBuffer implementation
    //////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////
    // x10::lang::Throwable implementation
    //////////////////////////////////////////////////////////////

    // x10.lang.Throwable.printStackTrace()
    void Throwable::printStackTrace() const {
        printStackTrace(x10::lang::System::x10__err);
    }

    // x10.lang.Throwable.printStackTrace(PrintStream)
    void Throwable::printStackTrace(ref<java::io::PrintStream> out) const {
        // TODO: RTTI and stack trace
        out->println(String(DEMANGLE(TYPENAME(*this))));
        x10::ref<String> message = getMessage();
        if (!message.isNull())
            out->println(String("\t") + message);
    }

    //////////////////////////////////////////////////////////////
    // x10::lang::region implementation
    //////////////////////////////////////////////////////////////

    _region_empty* _region_empty::_empty[MAX_EMPTY_RANK+1];
    _region<1>* _region_empty::_empty_1;

    _region<1>* _region_empty::EMPTY_1() {
        if (_empty_1 == NULL)
            _empty_1 = new (x10::alloc<_region<1> >()) _region<1>(0, -1); // FIXME: GC
        return _empty_1;
    }

    //////////////////////////////////////////////////////////////
    // x10::lang::place implementation
    //////////////////////////////////////////////////////////////

    const x10_int place::x10__MAX_PLACES = x10lib::numPlaces();
    void place::__init__MAX_PLACES() {
//        cerr << "Initializing MAX_PLACES to " << x10lib::numPlaces() << endl;
        const_cast<x10_int&>(x10__MAX_PLACES) = x10lib::numPlaces();
    }

    const place place::x10__FIRST_PLACE(0);

    //////////////////////////////////////////////////////////////
    // x10::lang::dist implementation
    //////////////////////////////////////////////////////////////

    static _dist_unique unique(place::x10__MAX_PLACES);
    const x10::ref<_dist_unique> dist::x10__UNIQUE = &unique;
    void dist::__init__UNIQUE() {
        //static _dist_unique unique(place::x10__MAX_PLACES);  // TODO: [IP]
        unique = _dist_unique(place::x10__MAX_PLACES);
//        cerr << "Initializing dist.UNIQUE to " << (void*)&unique << endl;
        const_cast<x10::ref<_dist_unique>&>(dist::x10__UNIQUE) = &unique;
    }

    _dist_empty* _dist_empty::_empty[MAX_EMPTY_RANK+1];

//	__System__static* __System__static::_instance = NULL;
//	const __System__static& System = __System__static::instance();

//////////////////////////////////////////////////////////////
// deserialization metadata for various hierarchies
//////////////////////////////////////////////////////////////

x10::subclass_vector point::_registered_subclasses;
x10::subclass_vector region::_registered_subclasses;
x10::subclass_vector dist::_registered_subclasses;

const _point<1>::__clinit _point<1>::clinit;
const _point<2>::__clinit _point<2>::clinit;
const _region_empty::__clinit _region_empty::clinit;
const _region<1>::__clinit _region<1>::clinit;
const _region<2>::__clinit _region<2>::clinit;
const _dist_unique::__clinit _dist_unique::clinit;
const _dist_empty::__clinit _dist_empty::clinit;
const _dist_local::__clinit _dist_local::clinit;
const _dist_block::__clinit _dist_block::clinit;

} // x10::lang namespace

//////////////////////////////////////////////////////////////
// x10::serialization_buffer implementation
//////////////////////////////////////////////////////////////

const double serialization_buffer::FACTOR = 2.0;

//////////////////////////////////////////////////////////////
// Serialization method implementation
//////////////////////////////////////////////////////////////

void _register_subclass_impl(int id, subclass_vector& registered_subclasses, void* (*deserialize_func)(serialization_buffer&)) {
    assert (id >= 0);
    if (registered_subclasses._length <= (size_t)id) {
        registered_subclasses._subclasses = (void*(**)(serialization_buffer&)) realloc(registered_subclasses._subclasses, (id+1) * sizeof(void*(*)(serialization_buffer&)));
        registered_subclasses._length = id+1;
    }
    registered_subclasses._subclasses[id] = deserialize_func;
}
void* _deserialize_subclass_impl(int id, serialization_buffer& buf, const subclass_vector& registered_subclasses) {
    assert (id >= 0);
    assert ((size_t)id < registered_subclasses._length);
    void* (*deserialize_func)(serialization_buffer&) = registered_subclasses._subclasses[id];
    if (deserialize_func == NULL) {
        assert(false); return NULL;
    }
    return deserialize_func(buf);
}

//////////////////////////////////////////////////////////////
// Finish/async support implementation
//////////////////////////////////////////////////////////////

int CS = 0;

void init() {
    x10lib::Init(NULL, 0);
    _X_("x10lib initialization complete");
}

void shutdown() {
    if (x10lib::here() == 0) x10::finish_start(TERMINATE_COMPUTATION);
    else x10::finish_start(CS);
    x10lib::Finalize();
    _X_("x10lib shutdown complete");
}

int finish_start(int CS) {
    _X_("invoking finish_start(" << CS << ")");
    CS = x10lib::FinishStart(CS);
    _X_("\treceived " << CS << " in finish_start()");
    return CS;
}

class x10libExceptionWrapper : public x10lib::Exception {
    char buffer[1024];
public:
    x10libExceptionWrapper(x10::ref<x10::lang::Exception> e) {
        x10::ref<x10::lang::String> m = x10::lang::String(DEMANGLE(TYPENAME(*this))) + x10::lang::String(": ") + e->getMessage();
        strncpy(buffer, m->c_str(), 1024);
    }
    virtual size_t size() { return sizeof(*this); }
    virtual void print() { cerr << buffer << endl; }
};

void finish_end(ref<x10::lang::Exception> e) {
#ifndef NO_EXCEPTIONS
    try {
#endif
        if (e.isNull()) {
            _X_("invoking finish_end(" << e._val << ")");
            x10lib::FinishEnd(NULL);
        } else {
            _X_("invoking finish_end(" << e._val << ")");
            e->printStackTrace(x10::lang::System::x10__err);
//            x10lib::FinishEnd(new (x10::alloc<x10libExceptionWrapper>()) x10libExceptionWrapper(e)); // TODO
            x10lib::FinishEnd(NULL);
//            x10lib::FinishEnd((x10lib::Exception*) e.operator->());
        }
#ifndef NO_EXCEPTIONS
    } catch (x10lib::MultiException me) {
        throw x10::ref<x10::lang::MultipleExceptions>(new (x10::alloc<x10::lang::MultipleExceptions>()) x10::lang::MultipleExceptions(me.size(), (x10::lang::Exception**)me.exceptions()));
    }
#endif
}

void clock_next() {
	x10lib::SyncGlobal();
}

void async_poll() {
    x10_msg_state_t ignored;
    x10lib::Poll(10, &ignored);
}

int __init__::count = 0;

} // x10 namespace
