#include <x10aux/config.h>

#include <x10aux/alloc.h>
#include <x10aux/class_cast.h>
#include <x10aux/serialization.h>
#include <x10aux/string_utils.h>
#include <x10aux/throw.h>

#include <x10/lang/String.h>
#include <x10/lang/Rail.h>

#include <stdarg.h>

using namespace x10::lang;
using namespace x10aux;

x10_int String::hashCode() {
    //FIXME:
    //presumably this needs a general hashcode implementation
    //that is centralised and used everywhere
    return 0;
}

ref<String> String::toString() {
    return this;
}

x10_boolean String::equals(ref<Object> other) {
    if (!CONCRETE_INSTANCEOF(other,String)) return false;
    // now we can downcast the Object to String
    ref<String> other_str = other;
    // defer to std::string::compare to check string contents
    return !compare(*other_str);
}


// postfix primitive operator+
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_boolean v)
    { return String::_make(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_byte v)
    { return String::_make(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_char v)
    { return String::_make(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_short v)
    { return String::_make(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_int v)
    { return String::_make(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_long v)
    { return String::_make(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_float v)
    { return String::_make(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_double v)
    { return String::_make(*s+x10aux::to_string(v)); }

// prefix primitive operator+
ref<String> x10::lang::operator+(x10_boolean v, x10aux::ref<String> s)
    { return String::_make(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_byte v, x10aux::ref<String> s)
    { return String::_make(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_char v, x10aux::ref<String> s)
    { return String::_make(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_short v, x10aux::ref<String> s)
    { return String::_make(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_int v, x10aux::ref<String> s)
    { return String::_make(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_long v, x10aux::ref<String> s)
    { return String::_make(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_float v, x10aux::ref<String> s)
    { return String::_make(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_double v, x10aux::ref<String> s)
    { return String::_make(x10aux::to_string(v)+*s); }


/*
String operator+(const String &s1, const String& s2) {
    return static_cast<const std::string&>(s1)
         + static_cast<const std::string&>(s2);
}
String operator+(const String &s1, ref<Object> s2) {
    return static_cast<const std::string&>(s1)
         + static_cast<const std::string&>(*s2->toString());
}
String operator+(ref<Object> s1, const String& s2) {
    return static_cast<const std::string&>(*s1->toString())
         + static_cast<const std::string&>(s2);
}
String operator+(ref<Object> s1, ref<Object> s2) {
    return static_cast<const std::string&>(*s1->toString())
         + static_cast<const std::string&>(*s2->toString());
}

ref<String> operator+=(ref<String> &s1, const String& s2) {
    s1 = s1 + s2;
    return s1;
}
ref<String> operator+=(ref<String> &s1, ref<Object> s2) {
    s1 = s1 + s2;
    return s1;
}
*/


x10_int String::indexOf(ref<String> str, x10_int i) {
    size_type res = find(*str, (size_type)i);
    if (res == std::string::npos)
        return (x10_int) -1;
    return (x10_int) res;
}

x10_int String::indexOf(x10_char c, x10_int i) {
    size_type res = find((char)c, (size_type)i);
    if (res == std::string::npos)
        return (x10_int) -1;
    return (x10_int) res;
}

x10_int String::lastIndexOf(ref<String> str, x10_int i) {
    size_type res = rfind(*str, (size_type)i);
    if (res == std::string::npos)
        return (x10_int) -1;
    return (x10_int) res;
}

x10_int String::lastIndexOf(x10_char c, x10_int i) {
    size_type res = rfind((char)c, (size_type)i);
    if (res == std::string::npos)
        return (x10_int) -1;
    return (x10_int) res;
}

ref<String> String::substring(x10_int start, x10_int end) {
    return String::_make(this->substr(start, end-start));
}

x10_char String::charAt(x10_int i) {
    return (x10_char) at(i);
}


ref<ValRail<x10_char> > String::chars() {
    x10_int sz = size();
    ValRail<x10_char> *rail = alloc_rail<x10_char,ValRail<x10_char> > (sz);
    for (int i=0 ; i<sz ; i++)
        rail->raw()[i] = (x10_char)at(i); // avoid bounds check
    return rail;
}

ref<ValRail<x10_byte> > String::bytes() {
    x10_int sz = size();
    ValRail<x10_byte> *rail = alloc_rail<x10_byte,ValRail<x10_byte> > (sz);
    for (int i=0 ; i<sz ; i++)
        rail->raw()[i] = (x10_byte)at(i); // avoid bounds check
    return rail;
}

extern "C" int vsnprintf(char *, size_t, const char *, va_list); 
// Note: allocates the return buffer
static char* vformat_to_buf(char* fmt, ...) {
    va_list args;
    char try_buf[1];
    va_start(args, fmt);
    int sz = vsnprintf(try_buf, 0, fmt, args);
    va_end(args);
    char* buf = alloc<char>(sz+1);
    va_start(args, fmt);
    int s1 = vsnprintf(buf, sz+1, fmt, args);
    assert (s1 == sz);
    va_end(args);
    return buf;
}

// [IP] I'm sure I will hate this but it will do for now.
static ref<String> format_impl(ref<String> format, ref<AnyRail<ref<Object> > > parms) {
    std::ostringstream ss;
    char* fmt = const_cast<char*>(format->c_str());
    char* next = NULL;
    for (x10_int i = 0; fmt != NULL; i++, fmt = next) {
        next = strchr(fmt+1, '%');
        if (next != NULL)
            *next = '\0';
        if (*fmt != '%') {
            ss << fmt;
            if (next != NULL)
                *next = '%';
            i--;
            continue;
        }
#ifndef NO_EXCEPTIONS
        if (i >= parms->FMGL(length))
            throwException(RuntimeException::_make(String::Lit("Index out of bounds: ") +
                                                   i +
                                                   String::Lit(" out of (") +
                                                   (x10_int)0 +
                                                   String::Lit(",") +
                                                   parms->FMGL(length) +
                                                   String::Lit(")")));
#endif
        const ref<Object> p = parms->operator[](i);
        char* buf = NULL;
        if (p.isNull())
            ss << (buf = vformat_to_buf(fmt, "null")); // FIXME: Ignore nulls for now
        else if (INSTANCEOF(p, ref<String>))
            ss << (buf = vformat_to_buf(fmt, class_cast<ref<String> >(p)->c_str()));
        else if (INSTANCEOF(p, ref<Box<x10_boolean> >))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_boolean>(p)));
        else if (INSTANCEOF(p, ref<Box<x10_byte> >))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_byte>(p)));
        else if (INSTANCEOF(p, ref<Box<x10_char> >))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_char>(p)));
        else if (INSTANCEOF(p, ref<Box<x10_short> >))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_short>(p)));
        else if (INSTANCEOF(p, ref<Box<x10_int> >))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_int>(p)));
        else if (INSTANCEOF(p, ref<Box<x10_long> >))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_long>(p)));
        else if (INSTANCEOF(p, ref<Box<x10_float> >))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_float>(p)));
        else if (INSTANCEOF(p, ref<Box<x10_double> >))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_double>(p)));
        else
            ss << (buf = vformat_to_buf(fmt, p->toString()->c_str()));
        if (buf != NULL)
            dealloc(buf);
        if (next != NULL)
            *next = '%';
    }
    return String::Lit(ss.str());
}

ref<String> String::format(ref<String> format, ref<ValRail<ref<Object> > > parms) {
    return format_impl(format, ref<AnyRail<ref<Object> > >(parms));
}

ref<String> String::format(ref<String> format, ref<Rail<ref<Object> > > parms) {
    return format_impl(format, ref<AnyRail<ref<Object> > >(parms));
}

const serialization_id_t String::_serialization_id =
    DeserializationDispatcher::addDeserializer(String::_deserialize<Object>);

DEFINE_RTT(String);
// vim:tabstop=4:shiftwidth=4:expandtab
