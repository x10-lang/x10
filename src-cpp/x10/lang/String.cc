#include <x10aux/alloc.h>

#include <x10/lang/String.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;
using namespace x10aux;

String::String(x10_boolean v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_byte v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_char v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_short v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_int v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_long v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_float v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }
String::String(x10_double v) : Value(), std::string(static_cast<std::string&>(*to_string(v))) { }

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
    


String operator+(const String &s1, const String& s2) {
    return static_cast<const std::string&>(s1)
         + static_cast<const std::string&>(s2);
}
String operator+(const String &s1, ref<String> s2) {
    return static_cast<const std::string&>(s1)
         + static_cast<const std::string&>(*s2);
}
String operator+(ref<String> s1, const String& s2) {
    return static_cast<const std::string&>(*s1)
         + static_cast<const std::string&>(s2);
}
String operator+(ref<String> s1, ref<String> s2) {
    return static_cast<const std::string&>(*s1)
         + static_cast<const std::string&>(*s2);
}


x10_int String::indexOf(ref<String> str, x10_int i) {
    size_type res = find(*static_cast<ref<std::string> >(str), (size_type)i);
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
    size_type res = rfind(static_cast<std::string&>(*str), (size_type)i);
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
    return String(static_cast<std::string&>(*this).substr(start, end-start));
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

std::ostream &operator << (std::ostream &o, x10aux::ref<x10::lang::String> s)
{
    if (s.isNull()) {
        o << "null";
    } else {
        o << *s;
    }

    return o;
}


DEFINE_RTT(String);
// vim:tabstop=4:shiftwidth=4:expandtab
