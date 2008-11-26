#include <x10aux/config.h>

#include <x10aux/alloc.h>
#include <x10aux/serialization.h>
#include <x10aux/string_utils.h>

#include <x10/lang/String.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;
using namespace x10aux;

/*
String::String(x10_boolean v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_byte v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_char v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_short v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_int v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_long v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_float v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_double v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
*/

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
    { return X10NEW(String)(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_byte v)
    { return X10NEW(String)(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_char v)
    { return X10NEW(String)(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_short v)
    { return X10NEW(String)(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_int v)
    { return X10NEW(String)(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_long v)
    { return X10NEW(String)(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_float v)
    { return X10NEW(String)(*s+x10aux::to_string(v)); }
ref<String> x10::lang::operator+(x10aux::ref<String> s, x10_double v)
    { return X10NEW(String)(*s+x10aux::to_string(v)); }

// prefix primitive operator+
ref<String> x10::lang::operator+(x10_boolean v, x10aux::ref<String> s)
    { return X10NEW(String)(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_byte v, x10aux::ref<String> s)
    { return X10NEW(String)(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_char v, x10aux::ref<String> s)
    { return X10NEW(String)(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_short v, x10aux::ref<String> s)
    { return X10NEW(String)(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_int v, x10aux::ref<String> s)
    { return X10NEW(String)(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_long v, x10aux::ref<String> s)
    { return X10NEW(String)(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_float v, x10aux::ref<String> s)
    { return X10NEW(String)(x10aux::to_string(v)+*s); }
ref<String> x10::lang::operator+(x10_double v, x10aux::ref<String> s)
    { return X10NEW(String)(x10aux::to_string(v)+*s); }


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

String String::substring(x10_int start, x10_int end) {
    return String(this->substr(start, end-start));
}

x10_char String::charAt(x10_int i) {
    return (x10_char) at(i);
}


ref<Rail<x10_char> > String::chars() {
    x10_int sz = size();
    Rail<x10_char> *rail = alloc_rail<x10_char,Rail<x10_char> > (sz);
    for (int i=0 ; i<sz ; i++)
        (*rail)[i] = (x10_char)at(i);
    return rail;
}

ref<Rail<x10_byte> > String::bytes() {
    x10_int sz = size();
    Rail<x10_byte> *rail = alloc_rail<x10_byte,Rail<x10_byte> > (sz);
    for (int i=0 ; i<sz ; i++)
        (*rail)[i] = (x10_byte)at(i);
    return rail;
}

static ref<String> format_impl(ref<String> format, ref<AnyRail<ref<Object> > > parms) {
    (void) parms;
    return format;
/* TODO: fix this up (if you dare)
    char* fmt = const_cast<char*>(format->c_str());
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
            this->_printf(fmt, p.get());
        if (next != NULL)
            *next = '%';
    }
    this->flush(); // FIXME [IP] temp
*/
}

ref<String> String::format(ref<String> format, ref<ValRail<ref<Object> > > parms) {
    return format_impl(format, ref<AnyRail<ref<Object> > >(parms));
}

ref<String> String::format(ref<String> format, ref<Rail<ref<Object> > > parms) {
    return format_impl(format, ref<AnyRail<ref<Object> > >(parms));
}

void String::_serialize_fields(x10aux::serialization_buffer& buf, x10aux::addr_map& m) {
    (void)buf; (void)m;
    abort();
}

void String::_deserialize_fields(x10aux::serialization_buffer& buf) {
    (void)buf;
}

DEFINE_RTT(String);
// vim:tabstop=4:shiftwidth=4:expandtab
