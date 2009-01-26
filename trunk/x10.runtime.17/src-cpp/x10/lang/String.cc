#include <x10aux/config.h>

#include <x10aux/alloc.h>
#include <x10aux/class_cast.h>
#include <x10aux/serialization.h>
#include <x10aux/basic_functions.h>
#include <x10aux/throw.h>
#include <x10aux/hash.h>

#include <x10/lang/String.h>
#include <x10/lang/Rail.h>

#include <cstdarg>
#include <sstream>

using namespace x10::lang;
using namespace x10aux;

x10_int String::hashCode() {
    return x10aux::hash(reinterpret_cast<const unsigned char*>(FMGL(content)), length());
}

x10_boolean String::equals(ref<Object> other) {
    if (!x10aux::concrete_instanceof<String>(other)) return false;
    // now we can downcast the Object to String
    ref<String> other_str = other;
    return !strcmp(FMGL(content),other_str->FMGL(content));
}


x10_int String::indexOf(ref<String> str, x10_int i) {
    const char *needle = str->FMGL(content);
    // TODO: bounds check
    const char *haystack = &FMGL(content)[i];
    const char *pos = strstr(haystack, needle);
    if (pos == NULL)
        return (x10_int) -1;
    return (x10_int) (pos - needle);
}

x10_int String::indexOf(x10_char c, x10_int i) {
    int needle = (int)c;
    // TODO: bounds check
    const char *haystack = &FMGL(content)[i];
    const char *pos = strchr(haystack, needle);
    if (pos == NULL)
        return (x10_int) -1;
    return (x10_int) (pos - needle);
}


static const char *my_strrstr(const char *haystack, const char *needle, int give_up) {
    const char *last_find = NULL;
    for (int i=0 ; i<=give_up && haystack[i]!='\0' ; ++i) {
        for (int j=0 ; needle[j]!='\0' ; ++j) {
            if (haystack[i+j] != needle[j]) goto nonmatch;
        }
        last_find = &haystack[i];
        nonmatch: {}
    }
    return last_find;
}

x10_int String::lastIndexOf(ref<String> str, x10_int i) {
    const char *needle = str->FMGL(content);
    const char *haystack = FMGL(content);
    // TODO: bounds check
    const char *pos = my_strrstr(haystack, needle, i);
    if (pos == NULL)
        return (x10_int) -1;
    return (x10_int) (pos - needle);
}


static const char *my_strrchr(const char *haystack, int needle, int give_up) {
    const char *last_find = NULL;
    for (int i=0 ; i<=give_up && haystack[i]!='\0' ; ++i) {
        if (haystack[i] == needle) last_find = &haystack[i];
    }
    return last_find;
}

x10_int String::lastIndexOf(x10_char c, x10_int i) {
    int needle = (int)c;
    const char *haystack = FMGL(content);
    // TODO: bounds check
    const char *pos = my_strrchr(haystack, needle, i);
    if (pos == NULL)
        return (x10_int) -1;
    return (x10_int) (pos - needle);
}

ref<String> String::substring(x10_int start, x10_int end) {
    assert(end>=start); // TODO: proper bounds check
    std::size_t sz = end - start;
    char *str = (char *)malloc(sz+1);
    for (std::size_t i=0 ; i<sz ; ++i)
        str[i] = FMGL(content)[start+i];
    str[sz] = '\0';
    return String::Steal(str);
}

x10_char String::charAt(x10_int i) {
    // TODO: bounds check
    return (x10_char) FMGL(content)[i];
}


ref<ValRail<x10_char> > String::chars() {
    x10_int sz = length();
    ValRail<x10_char> *rail = alloc_rail<x10_char,ValRail<x10_char> > (sz);
    for (int i=0 ; i<sz ; i++)
        rail->raw()[i] = (x10_char) FMGL(content)[i]; // avoid bounds check
    return rail;
}

ref<ValRail<x10_byte> > String::bytes() {
    x10_int sz = length();
    ValRail<x10_byte> *rail = alloc_rail<x10_byte,ValRail<x10_byte> > (sz);
    for (int i=0 ; i<sz ; i++)
        rail->raw()[i] = (x10_char) FMGL(content)[i]; // avoid bounds check
    return rail;
}

#ifdef __CYGWIN__
extern "C" int vsnprintf(char *, size_t, const char *, va_list); 
#endif
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
    (void) s1;
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
        const ref<Object> p = parms->operator[](i);
        char* buf = NULL;
        if (p.isNull())
            ss << (buf = vformat_to_buf(fmt, "null")); // FIXME: Ignore nulls for now
        else if (x10aux::instanceof<ref<String> >(p))
            ss << (buf = vformat_to_buf(fmt, class_cast<ref<String> >(p)->c_str()));
        else if (x10aux::instanceof<ref<Box<x10_boolean> > >(p))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_boolean>(p)));
        else if (x10aux::instanceof<ref<Box<x10_byte> > >(p))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_byte>(p)));
        else if (x10aux::instanceof<ref<Box<x10_char> > >(p))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_char>(p)));
        else if (x10aux::instanceof<ref<Box<x10_short> > >(p))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_short>(p)));
        else if (x10aux::instanceof<ref<Box<x10_int> > >(p))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_int>(p)));
        else if (x10aux::instanceof<ref<Box<x10_long> > >(p))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_long>(p)));
        else if (x10aux::instanceof<ref<Box<x10_float> > >(p))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_float>(p)));
        else if (x10aux::instanceof<ref<Box<x10_double> > >(p))
            ss << (buf = vformat_to_buf(fmt, class_cast<x10_double>(p)));
        else
            ss << (buf = vformat_to_buf(fmt, p->toString()->c_str()));
        if (buf != NULL)
            dealloc(buf);
        if (next != NULL)
            *next = '%';
    }
    return String::Lit(ss.str().c_str());
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
