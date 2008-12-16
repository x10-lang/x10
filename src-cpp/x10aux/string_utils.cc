#include <x10aux/config.h>
#include <x10aux/string_utils.h>
#include <x10aux/rail_utils.h>
#include <x10aux/alloc.h>
#include <x10aux/math.h>

#include <x10/lang/String.h>
#include <x10/lang/Rail.h>

using namespace x10::lang;
using namespace x10aux;

Rail<ref<String> > *x10aux::convert_args(int ac, char **av) {
    assert(ac>=1);
    x10_int x10_argc = ac  - 1;
    Rail<ref<String> > *arr = alloc_rail<ref<String>, Rail<ref<String> > > (x10_argc);
    for (int i = 1; i < ac; i++) {
        ref<String> val = String::Lit(av[i]);
        (*arr)[i-1] = val;
    }
    return arr;
}

void x10aux::free_args(const ref<Rail<ref<String> > > &arr) {
    //cerr << "free_args: freeing " << arr->length << " elements" << endl;
    //x10_int length = arr->length;
    //cerr << "free_args: freeing array " << arr << endl;
    free_rail<Rail<ref<String> > >(arr);
    //cerr << "free_args: freed array " << arr << endl;
}


// [DC] I'm sure Igor will hate this but it will do for now.
/*
template<class T> T x10aux::from_string(const ref<String> &s) {
    std::istringstream ss(*s);
    T x;
    if (!(ss >> x)) {
        // number format exception
    }
    return x;
}
*/


// [DC] I'm sure Igor will hate this but it will do for now.
template<class T> String to_string_general(T v) {
    std::ostringstream ss;
    ss << v;
    String r;
    r._constructor(ss.str());
    return r;
}

#ifdef __CYGWIN__
extern "C" int snprintf(char *, size_t, const char *, ...); 
#endif

#define TO_STRING(SZ,T,FMT) \
String x10aux::to_string(T v) { \
    char buf[SZ]; \
    int amt = ::snprintf(buf, sizeof(buf), FMT, v); \
    assert((size_t)amt<sizeof(buf) && "buf too small "__TOKEN_STRING(SZ)" for "__TOKEN_STRING(T)); \
    String r; \
    r._constructor(buf); \
    return r; \
}

// hh is C99, not ansi c, so we use h instead.
// This is fine as va_args turns everything to int anyway
TO_STRING(4, unsigned char, "%hu")
TO_STRING(5, signed char, "%hd")

//TO_STRING(6, unsigned short, "%hu")
TO_STRING(7, signed short, "%hd")

TO_STRING(11, unsigned int, "%u")
TO_STRING(12, signed int, "%d")
TO_STRING(11, unsigned long, "%lu")
TO_STRING(12, signed long, "%ld")
TO_STRING(20, unsigned long long, "%llu")
TO_STRING(21, signed long long, "%lld")

String x10aux::to_string(float v) {
    return x10aux::to_string((double)v);
}

// precondition: buf contains decimal point
void kill_excess_zeroes(char *buf, size_t sz) {
    for(int i=sz-1 ; i>0 && (buf[i]=='0' || buf[i]=='\0') ; --i) {
        if (buf[i-1]=='.') break;
        buf[i] = '\0';
    } 
}


String x10aux::to_string(double v) {
    char buf[120] = "";
    if (x10aux::math::isnan(v)) {
        ::snprintf(buf, sizeof(buf), "NaN");
    } else if (x10aux::math::isinf(v) && v > 0.0) {
        ::snprintf(buf, sizeof(buf), "Infinity");
    } else if (x10aux::math::isinf(v) && v < 0.0) {
        ::snprintf(buf, sizeof(buf), "-Infinity");
    } else if (::fabs(v) >= 1E-3 && ::fabs(v) < 1E7) {
        ::snprintf(buf, sizeof(buf), "%.15f", v);
        kill_excess_zeroes(buf, sizeof(buf));
    } else if (v == 0.0) {
        ::snprintf(buf, sizeof(buf), "%.1f", v);
    } else {
        // scientific notation
        int e = (int)::floor(::log(::fabs(v))/::log(10.0)); //exponent
        // volatile because reordering could change computed floating point value
        volatile double m = v / ::pow(10, e); //mantissa
        if (e < -10) {
            // avoid touching -Infinity
            m = v * 1E10;
            m /= ::pow(10, e+10);
        }
        if (e < 0) {
            ::snprintf(buf, sizeof(buf), "%.1f", m);
        } else {
            ::snprintf(buf, sizeof(buf), "%.16f", m);
        }
        kill_excess_zeroes(buf, sizeof(buf));
        char *rest = buf + strlen(buf);
        ::snprintf(rest, sizeof(buf) + buf - rest, "E%d", e);
    }
    String r;
    r._constructor(buf);
    return r;
}
    
String x10aux::to_string(bool v) {
    String r;
    r._constructor(v?"true":"false");
    return r;
}
    
String x10aux::to_string(x10_char v) {
    char v_[] = {(char)v,'\0'};
    String r;
    r._constructor(v_);
    return r;
}


String x10aux::vrc_to_string(ref<ValRail<x10_char> > v) {
    std::string str(v->FMGL(length), '\0');
    for (int i = 0; i < v->FMGL(length); ++i)
        str[i] = (*v)[i];
    String r;
    r._constructor(str);
    return r;
}

    
// vim:tabstop=4:shiftwidth=4:expandtab
