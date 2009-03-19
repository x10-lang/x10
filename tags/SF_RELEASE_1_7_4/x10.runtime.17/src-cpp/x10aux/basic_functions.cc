#include <x10aux/config.h>
#include <x10aux/basic_functions.h>
#include <x10aux/math.h>

using namespace x10aux;
using namespace x10::lang;

#ifdef __CYGWIN__
extern "C" int snprintf(char *, size_t, const char *, ...);
#endif

#define TO_STRING(SZ,T,FMT) \
ref<String> x10aux::to_string(T v) { \
    char buf[SZ]; \
    int amt = ::snprintf(buf, sizeof(buf), FMT, v); \
    (void)amt; \
    assert((size_t)amt<sizeof(buf) && "buf too small "__TOKEN_STRING(SZ)" for "__TOKEN_STRING(T)); \
    return String::Lit(buf); \
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

ref<String> x10aux::to_string(float v) {
    return x10aux::to_string((double)v);
}


// precondition: buf contains decimal point
void kill_excess_zeroes(char *buf, size_t sz) {
    for(int i=sz-1 ; i>0 && (buf[i]=='0' || buf[i]=='\0') ; --i) {
        if (buf[i-1]=='.') break;
        buf[i] = '\0';
    }   
}   


ref<String> x10aux::to_string(double v) {
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
        volatile double m = v / ::pow(10.0, e); //mantissa
        if (e < -10) {
            // avoid touching -Infinity
            m = v * 1E10;
            m /= ::pow(10.0, e+10);
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
    return String::Lit(buf);
}   
    

ref<String> x10aux::to_string(bool v) {
    static ref<String> t = String::Lit("true");
    static ref<String> f = String::Lit("false");
    return v ? t : f;
}   
    
ref<String> x10aux::to_string(x10_char v) {
    char v_[] = {(char)v,'\0'};
    return String::Lit(v_);
}

// vim:tabstop=4:shiftwidth=4:expandtab

