/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#include <x10aux/config.h>
#include <x10aux/basic_functions.h>

#include <x10/lang/Double.h>
#include <x10/lang/Float.h>
#include <x10/lang/String.h>

#include <x10/util/StringBuilder.h>

#include <stdio.h>

using namespace x10aux;
using namespace x10::lang;

x10_int x10aux::hash_code(const x10_complex x) {
    return hash_code(x.real()) * hash_code(x.imag());
}

x10_int x10aux::hash_code(const x10_double x) {
    return hash_code(x10::lang::DoubleNatives::toLongBits(x));
}

x10_int x10aux::hash_code(const x10_float x) {
    return hash_code(x10::lang::FloatNatives::toIntBits(x));
}

String* x10aux::identity_type_name (Reference* ptr) {
    return String::Lit(alloc_printf("%s",ptr->_type()->name()));
}

String* x10aux::identity_to_string (Reference* ptr) {
    return String::Lit(alloc_printf("%s@%p",ptr->_type()->name(),(void*)ptr));
}

#define TO_STRING(SZ,T,C,FMT) \
String* x10aux::to_string(T v) { \
    char buf[SZ]; \
    int amt = ::snprintf(buf, sizeof(buf), FMT, (C)v); \
    (void)amt; \
    assert((size_t)amt<sizeof(buf) && "buf too small "__TOKEN_STRING(SZ)" for "__TOKEN_STRING(T)); \
    return x10::lang::String::Lit(buf);              \
}

// hh is C99, not ansi c, so we use h instead.
// This is fine as va_args turns everything to int anyway
TO_STRING(4, x10_ubyte, unsigned char, "%hu")
TO_STRING(5, x10_byte, signed char, "%hd")

TO_STRING(6, x10_ushort, unsigned short, "%hu")
TO_STRING(7, x10_short, signed short, "%hd")

TO_STRING(11, x10_uint, unsigned long, "%lu")
TO_STRING(12, x10_int, signed long, "%ld")
TO_STRING(21, x10_ulong, unsigned long long, "%llu")
TO_STRING(21, x10_long, signed long long, "%lld")

String* x10aux::to_string(x10_float v) {
    return x10aux::to_string((x10_double)v);
}

// precondition: buf contains decimal point
void kill_excess_zeroes(char *buf, size_t sz) {
    for(int i=sz-1 ; i>0 && (buf[i]=='0' || buf[i]=='\0') ; --i) {
        if (buf[i-1]=='.') break;
        buf[i] = '\0';
    }   
}

String* x10aux::to_string(x10_double v_) {
    double v = (double)v_;
    char buf[120] = "";
    if (x10::lang::DoubleNatives::isNaN(v)) {
        ::snprintf(buf, sizeof(buf), "NaN");
    } else if (x10::lang::DoubleNatives::isInfinite(v) && v > 0.0) {
        ::snprintf(buf, sizeof(buf), "Infinity"); 
    } else if (x10::lang::DoubleNatives::isInfinite(v) && v < 0.0) {
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
    return x10::lang::String::Lit(buf);
}   

String* x10aux::to_string(x10_complex v) {
    static x10::lang::String* c1 = x10::lang::String::Lit("(");
    static x10::lang::String* c2 = x10::lang::String::Lit(", ");
    static x10::lang::String* c3 = x10::lang::String::Lit(")");
    
    x10::util::StringBuilder * sb = x10::util::StringBuilder::_make();
    sb->add(c1);
    sb->add(x10aux::to_string(v.real()));
    sb->add(c2);
    sb->add(x10aux::to_string(v.imag()));
    sb->add(c3);
    return sb->toString();
}

String* x10aux::to_string(x10_boolean v) {
    static String* t = x10::lang::String::Lit("true");
    static String* f = x10::lang::String::Lit("false");
    return ((bool)v) ? t : f;
}   
    
String* x10aux::to_string(x10_char v) {
    char v_[] = {(char)v.v,'\0'};
    return x10::lang::String::Lit(v_);
}

String* x10aux::makeStringLit(const char* s) {
    return x10::lang::String::Lit(s);
}    

GPUSAFE x10_boolean x10aux::compare_references_slow(x10::lang::Reference* x,
                                                    x10::lang::Reference* y) {
    return x->_struct_equals(y);
}

// vim:tabstop=4:shiftwidth=4:expandtab

