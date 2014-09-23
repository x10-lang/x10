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

#include <x10aux/config.h>

#include <x10aux/alloc.h>
#include <x10aux/class_cast.h>
#include <x10aux/serialization.h>
#include <x10aux/basic_functions.h>
#include <x10aux/throw.h>

#include <x10/lang/Char.h>
#include <x10/lang/Rail.h>
#include <x10/lang/String.h>
#include <x10/lang/StringIndexOutOfBoundsException.h>

#include <cstdarg>
#include <sstream>

using namespace std;
using namespace x10::lang;
using namespace x10aux;

#ifndef NO_BOUNDS_CHECKS
static void throwStringIndexOutOfBoundsException(x10_int index, x10_int length) {
    char *msg = alloc_printf("index = %ld; length = %ld", (long)index, ((long)length));
    throwException(x10::lang::StringIndexOutOfBoundsException::_make(String::Lit(msg)));
}
#endif

static inline void checkStringBounds(x10_int index, x10_int length) {
#ifndef NO_BOUNDS_CHECKS
    if (((x10_uint)index) >= ((x10_uint)length)) {
        throwStringIndexOutOfBoundsException(index, length);
    }
#endif
}

void
String::_constructor(const char *content, bool steal) {
    size_t len = strlen(content);
    if (!steal) content = alloc_utils::strdup(content);
    this->FMGL(content) = content;
    this->FMGL(content_length) = len;
}

void
String::_constructor() {
    this->FMGL(content) = "";
    this->FMGL(content_length) = 0;
}

void
String::_constructor(String* s) {
    nullCheck(s);
    this->FMGL(content) = s->FMGL(content);
    this->FMGL(content_length) = s->FMGL(content_length);
}

String*
String::_make(x10::lang::Rail<x10_byte>* rail) {
    return _make(rail, 0, rail->FMGL(size));
}

String*
String::_make(x10::util::GrowableRail<x10_byte>* grail) {
    nullCheck(grail);
    String* this_ = new (::x10aux::alloc<String>()) String();
    x10_int i = 0;
    x10_int length = (x10_int)grail->size();
    char *content= x10aux::alloc<char>(length+1);
    x10::lang::Rail<x10_byte>* rail = grail->rail();
    for (i=0; i<length; i++) {
        content[i] = (char)(rail->raw[i]);
    }
    content[i] = '\0';
    this_->FMGL(content) = content;
    this_->FMGL(content_length) = i;
    return this_;
}


void
String::_constructor(x10::lang::Rail<x10_byte>* rail, x10_int start, x10_int length) {
    nullCheck(rail);
    x10_int i = 0;
    char *content= x10aux::alloc<char>(length+1);
    for (i=0; i<length; i++) {
        content[i] = (char)(rail->raw[start + i]);
    }
    content[i] = '\0';
    this->FMGL(content) = content;
    this->FMGL(content_length) = i;
}

String*
String::_make(x10::lang::Rail<x10_char>* rail) {
    return _make(rail, 0, rail->FMGL(size));
}

void
String::_constructor(x10::lang::Rail<x10_char>* rail, x10_int start, x10_int length) {
    nullCheck(rail);
    x10_int i = 0;
    char *content= x10aux::alloc<char>(length+1);
    for (i=0; i<length; i++) {
        content[i] = (char)(rail->raw[start + i].v);
    }
    content[i] = '\0';
    this->FMGL(content) = content;
    this->FMGL(content_length) = i;
}

x10_int String::hashCode() {
    x10_int hc = 0;
    x10_int l = length();
    const unsigned char* k = reinterpret_cast<const unsigned char*>(FMGL(content));
    for (; l > 0; k++, l--) {
        hc *= 31;
        hc += (x10_int) *k;
    }
    return hc;
}

static const char *strnstrn(const char *haystack, size_t haystack_sz,
                            const char *needle, size_t needle_sz)
{
    if (haystack_sz < needle_sz)
        return NULL;
    for (size_t i = 0; i <= haystack_sz-needle_sz; ++i) {
        if (!strncmp(&haystack[i], needle, needle_sz))
            return &haystack[i];
    }
    return NULL;
}

x10_int String::indexOf(String* str, x10_int i) {
    nullCheck(str);
    if (i<0) i = 0;
    if (((size_t)i) >= FMGL(content_length)) return -1;

    const char *needle = str->FMGL(content);
    size_t needle_sz = str->FMGL(content_length);
    const char *haystack = &FMGL(content)[i];
    size_t haystack_sz = FMGL(content_length) - i;
    const char *pos = strnstrn(haystack, haystack_sz, needle, needle_sz);
    if (pos == NULL)
        return (x10_int) -1;
    return i + (x10_int) (pos - haystack);
}

static const char *strnchr(const char *haystack, size_t haystack_sz, char needle)
{
    for (size_t i = 0; i < haystack_sz; ++i) {
        if (haystack[i] == needle)
            return &haystack[i];
    }
    return NULL;
}

x10_int String::indexOf(x10_char c, x10_int i) {
    if (i < 0) i = 0;
    if (((size_t)i) >= FMGL(content_length)) return -1;

    int needle = (int)c.v;
    const char *haystack = &FMGL(content)[i];
    size_t haystack_sz = FMGL(content_length) - i;
    const char *pos = strnchr(haystack, haystack_sz, needle);
    if (pos == NULL)
        return (x10_int) -1;
    return i + (x10_int) (pos - haystack);
}

// [DC] Java defines whitespace as any unicode codepoint <= U0020
// ref: javadoc for java.lang.String.trim()
static bool isws (char x) { return x <= 0x20; }

String* String::trim() {
    const char *start = FMGL(content);
    x10_int l = FMGL(content_length);
    bool didSomething = false;
    if (l==0) { return this; } // string is empty
    while (isws(start[0]) && l>0) { start++; l--; didSomething = true; }
    while (isws(start[l-1]) && l>0) { l--; didSomething = true; }
    if (!didSomething) { return this; }
    char *trimmed = alloc_utils::strndup(start, l);
    return _make(trimmed, true);
}

static const char *strnrstrn(const char *haystack, size_t haystack_sz,
                             const char *needle, size_t needle_sz)
{
    if (haystack_sz < needle_sz)
        return NULL;
    for (size_t i = haystack_sz-needle_sz; i > 0; --i) {
        if (!strncmp(&haystack[i], needle, needle_sz))
            return &haystack[i];
    }
    if (!strncmp(haystack, needle, needle_sz))
        return haystack;
    return NULL;
}

x10_int String::lastIndexOf(String* str, x10_int i) {
    nullCheck(str);
    if (i < 0) i = 0;
    if (((size_t)i) >= FMGL(content_length)) return -1;

    const char *needle = str->FMGL(content);
    size_t needle_sz = str->FMGL(content_length);
    const char *haystack = FMGL(content);
    size_t haystack_sz = (size_t)i+1;
    const char *pos = strnrstrn(haystack, haystack_sz, needle, needle_sz);
    if (pos == NULL)
        return (x10_int) -1;
    return (x10_int) (pos - haystack);
}

static const char *strnrchr(const char *haystack, size_t haystack_sz, int needle) {
    if (haystack_sz == 0)
        return NULL;
    for (size_t i = haystack_sz-1; i > 0; --i) {
        if (haystack[i] == needle)
            return &haystack[i];
    }
    if (*haystack == needle)
        return haystack;
    return NULL;
}

x10_int String::lastIndexOf(x10_char c, x10_int i) {
    if (i < 0) i = 0;
    if (((size_t)i) >= FMGL(content_length)) return -1;

    int needle = (int)c.v;
    const char *haystack = FMGL(content);
    size_t haystack_sz = (size_t)i+1;
    const char *pos = strnrchr(haystack, haystack_sz, needle);
    if (pos == NULL)
        return (x10_int) -1;
    return (x10_int) (pos - haystack);
}

String* String::substring(x10_int start, x10_int end) {
#ifndef NO_BOUNDS_CHECKS
    if (start < 0) throwStringIndexOutOfBoundsException(start, FMGL(content_length));
    if (start > end) throwStringIndexOutOfBoundsException(start, end);
    if (((size_t)end) > FMGL(content_length)) throwStringIndexOutOfBoundsException(end, FMGL(content_length));
#endif
    size_t sz = end - start;
    char *str = x10aux::alloc<char>(sz+1);
    for (size_t i = 0; i < sz; ++i)
        str[i] = FMGL(content)[start+i];
    str[sz] = '\0';
    return String::Steal(str);
}


x10_char String::charAt(x10_int i) {
    checkStringBounds(i, FMGL(content_length));
    return (x10_char) FMGL(content)[i];
}


x10::lang::Rail<x10_char>* String::chars() {
    x10_int sz = length();
    x10::lang::Rail<x10_char>* rail = x10::lang::Rail<x10_char>::_make(sz);
    for (int i = 0; i < sz; ++i)
        rail->__set(i, (x10_char) FMGL(content)[i]);
    return rail;
}

x10::lang::Rail<x10_byte>* String::bytes() {
    x10_int sz = length();
    x10::lang::Rail<x10_byte>* rail = x10::lang::Rail<x10_byte>::_make(sz);
    for (int i = 0; i < sz; ++i)
        rail->__set(i, FMGL(content)[i]);
    return rail;
}


// A lightweight version of std::ostringstream ss;
// Rolling it ourself is a huge performance win (roughly 3x).
class MyBuf {
  public:
    size_t capacity;
    size_t cursor;
    char* buffer;

    MyBuf(size_t cap) {
        capacity = cap;
        cursor = 0;
        buffer = x10aux::alloc<char>(capacity);
        buffer[0] = '\0';
    }

    inline size_t available() { return capacity - cursor; }
    
    // Implementation note: to better match snprintf API,
    // enable ensures that buffer has enough backing memory
    // to support a write of min+1 bytes
    void enable(size_t min) {
        size_t newCapacity = cursor + min + 1;
        buffer = x10aux::realloc<char>(buffer, newCapacity);
        capacity = newCapacity;
    }
    
    void sprint(char* val) {
        size_t writeSize = strlen(val);
        if (writeSize + 1 < available()) {
            ::memcpy(&buffer[cursor], val, writeSize+1);
            cursor += writeSize;
        } else {
            enable(writeSize);
            ::memcpy(&buffer[cursor], val, writeSize+1);
            cursor += writeSize;
        }
    }

    template <class T> void sprintf(char *fmt, T val) {
        size_t writeSize = ::snprintf(&buffer[cursor], available(), fmt, val);
        if (writeSize < available()) {
            cursor += writeSize;
        } else {
            enable(writeSize);
            size_t writeSize2 = ::snprintf(&buffer[cursor], available(), fmt, val);
            assert(writeSize2 == writeSize);
            cursor += writeSize2;
        }
    }
};    

String* String::format(String* format, x10::lang::Rail<Any*>* parms) {
    nullCheck(format);
    nullCheck(parms);

    MyBuf buf(format->length() + 32); // Add a little room to print a couple values.
    char* orig = alloc_utils::strdup(format->c_str());
    char* fmt = orig;
    char* next = NULL;
    for (x10_int i = 0; fmt != NULL; fmt = next) {
        next = strchr(fmt+1, '%');
        if (next != NULL)
            *next = '\0';
        if (*fmt != '%') {
            buf.sprint(fmt);
        } else {
            if (next == fmt+1) {
                *next = '%';
                next = strchr(next+1, '%');
                if (next != NULL)
                    *next = '\0';
                buf.sprintf(fmt, NULL);
            } else {
                Any* p = parms->__apply(i);
                i += 1;
                if (NULL == p) {
                    buf.sprintf(fmt, NULL);
                } else {
                    Reference* pAsRef = reinterpret_cast<x10::lang::Reference*>(p);
                    const x10aux::RuntimeType* rtt = pAsRef->_type();
                    // Ordered by guesstimate at frequency in printfs...
                    if (rtt->equals(x10aux::getRTT<x10_long>())) {
                        buf.sprintf(fmt, class_cast_unchecked<x10_long>(p));
                    } else if (rtt->equals(x10aux::getRTT<x10_double>())) {
                        buf.sprintf(fmt, class_cast_unchecked<x10_double>(p));
                    } else if (rtt->equals(x10aux::getRTT<x10_int>())) {
                        buf.sprintf(fmt, class_cast_unchecked<x10_int>(p));
                    } else if (rtt->equals(x10aux::getRTT<x10_float>())) {
                        buf.sprintf(fmt, class_cast_unchecked<x10_float>(p));
                    } else if (rtt->equals(x10aux::getRTT<x10::lang::String>())) {
                        buf.sprintf(fmt, class_cast_unchecked<String*>(pAsRef)->c_str());
                    } else if (rtt->equals(x10aux::getRTT<x10_boolean>())) {
                        buf.sprintf(fmt, class_cast_unchecked<x10_boolean>(p));
                    } else if (rtt->equals(x10aux::getRTT<x10_byte>())) {
                        buf.sprintf(fmt, class_cast_unchecked<x10_byte>(p));
                    } else if (rtt->equals(x10aux::getRTT<x10_short>())) {
                        buf.sprintf(fmt, class_cast_unchecked<x10_short>(p));
                    } else if (rtt->equals(x10aux::getRTT<x10_char>())) {
                        buf.sprintf(fmt, class_cast_unchecked<x10_char>(p).v);
                    } else if (rtt->equals(x10aux::getRTT<x10_ubyte>())) {
                        buf.sprintf(fmt, class_cast_unchecked<x10_ubyte>(p));
                    } else if (rtt->equals(x10aux::getRTT<x10_ushort>())) {
                        buf.sprintf(fmt, class_cast_unchecked<x10_ushort>(p));
                    } else if (rtt->equals(x10aux::getRTT<x10_uint>())) {
                        buf.sprintf(fmt, class_cast_unchecked<x10_uint>(p));
                    } else if (rtt->equals(x10aux::getRTT<x10_ulong>())) {
                        buf.sprintf(fmt, class_cast_unchecked<x10_ulong>(p));
                    } else {
                        buf.sprintf(fmt, pAsRef->toString()->c_str());
                    }
                }
            }
        }
        if (next != NULL)
            *next = '%';
    }
    x10aux::dealloc(orig);
    return String::Steal(buf.buffer);
}

x10_boolean String::equals(Any* p0) {
    if (NULL == p0) return false;
    if (this == reinterpret_cast<String*>(p0)) return true; // short-circuit trivial equality
    if (!x10aux::instanceof<x10::lang::String*>(p0)) return false;
    String* that = reinterpret_cast<String*>(p0);
    if (this->FMGL(content_length) != that->FMGL(content_length)) return false; // short-circuit trivial dis-equality
    if (strncmp(this->FMGL(content), that->FMGL(content), this->length()))
        return false;
    return true;
}

/* FIXME: Unicode support */
x10_boolean String::equalsIgnoreCase(String* s) {
    if (NULL == s) return false;
    if (s == this) return true; // short-circuit trivial equality
    if (this->FMGL(content_length) != s->FMGL(content_length)) return false; // short-circuit trivial dis-equality
    if (strncasecmp(this->FMGL(content), s->FMGL(content), this->length()))
        return false;
    return true;
}

/* FIXME: Unicode support */
String* String::toLowerCase() {
    char *str = x10aux::alloc<char>(FMGL(content_length)+1);
    bool all_lower = true;
    for (size_t i = 0; i < FMGL(content_length); ++i) {
        x10_char c = FMGL(content)[i];
        if (!CharNatives::isLowerCase(c))
            all_lower = false;
        x10_char l = CharNatives::toLowerCase(c);
        str[i] = (char)l.v;
    }
    if (all_lower) {
        x10aux::dealloc(str);
        return this;
    }
    str[FMGL(content_length)] = '\0';
    return String::Steal(str);
}

/* FIXME: Unicode support */
String* String::toUpperCase() {
    char *str = x10aux::alloc<char>(FMGL(content_length)+1);
    bool all_upper = true;
    for (size_t i = 0; i < FMGL(content_length); ++i) {
        x10_char c = FMGL(content)[i];
        if (!CharNatives::isUpperCase(c))
            all_upper = false;
        x10_char u = CharNatives::toUpperCase(c);
        str[i] = (char)u.v;
    }
    if (all_upper) {
        x10aux::dealloc(str);
        return this;
    }
    str[FMGL(content_length)] = '\0';
    return String::Steal(str);
}

x10_int String::compareTo(String* s) {
    nullCheck(s);
    if (s == this) return 0; // short-circuit trivial equality
    int length_diff = this->FMGL(content_length) - s->FMGL(content_length);
    size_t min_length = length_diff < 0 ? this->FMGL(content_length) : s->FMGL(content_length);
    int cmp = strncmp(this->FMGL(content), s->FMGL(content), min_length);
    if (cmp != 0)
        return (x10_int) cmp;
    return (x10_int) length_diff;
}

/* FIXME: Unicode support */
x10_int String::compareToIgnoreCase(String* s) {
    nullCheck(s);
    if (s == this) return 0; // short-circuit trivial equality
    int length_diff = this->FMGL(content_length) - s->FMGL(content_length);
    size_t min_length = length_diff < 0 ? this->FMGL(content_length) : s->FMGL(content_length);
    int cmp = strncasecmp(this->FMGL(content), s->FMGL(content), min_length);
    if (cmp != 0)
        return (x10_int) cmp;
    return (x10_int) length_diff;
}

x10_boolean String::startsWith(String* s) {
    nullCheck(s);
    size_t len = s->FMGL(content_length);
    if (len > this->FMGL(content_length))
        return false;
    int cmp = strncmp(this->FMGL(content), s->FMGL(content), len);
    return (cmp == 0);
}

x10_boolean String::endsWith(String* s) {
    nullCheck(s);
    size_t len = s->FMGL(content_length);
    if (len > this->FMGL(content_length))
        return false;
    int length_diff = this->FMGL(content_length) - s->FMGL(content_length);
    int cmp = strncmp(this->FMGL(content) + length_diff, s->FMGL(content), len);
    return (cmp == 0);
}


String* String::__plus(String* p1, String* p2) {
    if (NULL == p1) { p1 = makeStringLit("null"); }
    if (NULL == p2) { p2 = makeStringLit("null"); }
    std::size_t newLength = p1->FMGL(content_length) + p2->FMGL(content_length);
    char *newChars = x10aux::alloc<char>(newLength+1, false);
    memcpy(newChars, p1->FMGL(content), p1->FMGL(content_length));
    memcpy(&newChars[p1->FMGL(content_length)], p2->FMGL(content), p2->FMGL(content_length));
    newChars[newLength] = '\0';
    return String::Steal(newChars);
}

String* String::__plus(String* p1, x10_boolean p2) {
    // Note: to_string(x10_boolean) doesn't allocate; simply returns static String*
    return String::__plus(p1, x10aux::to_string(p2));
}

String* String::__plus(x10_boolean p1, String* p2) {
    // Note: to_string(x10_boolean) doesn't allocate; simply returns static String*
    return String::__plus(x10aux::to_string(p1), p2);
}


#define STRING_PLUS_DEFS(SZ,T,C,FMT) \
String* String::__plus(String* p1, T p2) { \
    char buf[SZ]; \
    size_t used = (size_t)(::snprintf(buf, SZ, FMT, (C)p2)); \
    if (NULL == p1) { p1 = makeStringLit("null"); } \
    std::size_t newLength = p1->FMGL(content_length) + used; \
    char *newChars = x10aux::alloc<char>(newLength+1, false); \
    memcpy(newChars, p1->FMGL(content), p1->FMGL(content_length)); \
    memcpy(&newChars[p1->FMGL(content_length)], buf, used); \
    newChars[newLength] = '\0'; \
    return String::Steal(newChars); \
} \
String* String::__plus(T p1, String* p2) { \
    char buf[SZ]; \
    size_t used = (size_t)(::snprintf(buf, SZ, FMT, (C)p1)); \
    if (NULL == p2) { p2 = makeStringLit("null"); } \
    std::size_t newLength = used + p2->FMGL(content_length); \
    char *newChars = x10aux::alloc<char>(newLength+1, false); \
    memcpy(newChars, buf, used);                              \
    memcpy(&newChars[used], p2->FMGL(content), p2->FMGL(content_length)); \
    newChars[newLength] = '\0'; \
    return String::Steal(newChars); \
}

// hh is C99, not ansi c, so we use h instead.
// This is fine as va_args turns everything to int anyway
STRING_PLUS_DEFS(4, x10_ubyte, unsigned char, "%hu")
STRING_PLUS_DEFS(5, x10_byte, signed char, "%hd")

STRING_PLUS_DEFS(6, x10_ushort, unsigned short, "%hu")
STRING_PLUS_DEFS(7, x10_short, signed short, "%hd")

STRING_PLUS_DEFS(11, x10_uint, unsigned long, "%lu")
STRING_PLUS_DEFS(12, x10_int, signed long, "%ld")
STRING_PLUS_DEFS(21, x10_ulong, unsigned long long, "%llu")
STRING_PLUS_DEFS(21, x10_long, signed long long, "%lld")




const serialization_id_t String::_serialization_id =
    DeserializationDispatcher::addDeserializer(String::_deserializer);

void String::_serialize_body(x10aux::serialization_buffer& buf) {
    // only support strings that are shorter than 4billion chars
    x10_int sz = FMGL(content_length);
    buf.write(sz);
    const char* content = FMGL(content);
    for (x10_int i = 0; i < sz; ++i) {
        buf.write((x10_char)content[i]);
    }
}

void String::_destructor() {
    dealloc(FMGL(content));
}

void String::_deserialize_body(x10aux::deserialization_buffer &buf) {
    x10_int sz = buf.read<x10_int>();
    char *content = x10aux::alloc<char>(sz+1);
    for (x10_int i = 0; i < sz; ++i) {
        content[i] = (char)buf.read<x10_char>().v;
    }
    content[sz] = '\0';
    this->FMGL(content) = content;
    this->FMGL(content_length) = strlen(content);
    _S_("Deserialized string was: \""<<this<<"\"");
}

Reference* String::_deserializer(x10aux::deserialization_buffer& buf) {
    String* this_ = new (x10aux::alloc<String>()) String();
    buf.record_reference(this_);
    this_->_deserialize_body(buf);
    return this_;
}

Comparable<String*>::itable<String> String::_itable_Comparable(&String::compareTo,
                                                               &String::equals, &String::hashCode,
                                                               &String::toString, &String::typeName);

CharSequence::itable<String> String::_itable_CharSequence(&String::charAt,
                                                          &String::equals, &String::hashCode,
                                                          &String::length, &String::subSequence,
                                                          &String::toString, &String::typeName);

x10aux::itable_entry String::_itables[3] = {
    x10aux::itable_entry(&x10aux::getRTT<Comparable<String*> >, &String::_itable_Comparable),
    x10aux::itable_entry(&x10aux::getRTT<CharSequence>, &String::_itable_CharSequence),
    x10aux::itable_entry(NULL,  (void*)"x10.lang.String")
};

x10aux::RuntimeType String::rtt;

void String::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    const x10aux::RuntimeType* parents[2] = { Comparable<String*>::getRTT(), CharSequence::getRTT() };
    
    rtt.initStageTwo("x10.lang.String", RuntimeType::class_kind, 2, parents, 0, NULL, NULL);
}    

// vim:tabstop=4:shiftwidth=4:expandtab
