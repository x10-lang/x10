#ifndef X10AUX_BASIC_FUNCTIONS_H
#define X10AUX_BASIC_FUNCTIONS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/hash.h>
#include <x10/lang/String.h>

namespace x10aux {

    template<class T> static inline ref<x10::lang::String> class_name(ref<T> x) {
        return x10::lang::String::Lit(getRTT<T>()->name());
        
    }

    template<class T, class U>
    static inline x10_boolean equals(ref<T> x, ref<U> y) { return x->equals(y); }

    template<class T>
    static inline x10_boolean equals(ref<T> x, x10_double y) { return false; }
    template<class T>
    static inline x10_boolean equals(ref<T> x, x10_float y) { return false; }
    template<class T>
    static inline x10_boolean equals(ref<T> x, x10_long y) { return false; }
    template<class T>
    static inline x10_boolean equals(ref<T> x, x10_int y) { return false; }
    template<class T>
    static inline x10_boolean equals(ref<T> x, x10_short y) { return false; }
    template<class T>
    static inline x10_boolean equals(ref<T> x, x10_byte y) { return false; }
    template<class T>
    static inline x10_boolean equals(ref<T> x, x10_char y) { return false; }
    template<class T>
    static inline x10_boolean equals(ref<T> x, x10_boolean y) { return false; }

    template<class T>
    static inline x10_boolean equals(x10_double y, ref<T> x) { return false; }
    template<class T>
    static inline x10_boolean equals(x10_float y, ref<T> x) { return false; }
    template<class T>
    static inline x10_boolean equals(x10_long y, ref<T> x) { return false; }
    template<class T>
    static inline x10_boolean equals(x10_int y, ref<T> x) { return false; }
    template<class T>
    static inline x10_boolean equals(x10_short y, ref<T> x) { return false; }
    template<class T>
    static inline x10_boolean equals(x10_byte y, ref<T> x) { return false; }
    template<class T>
    static inline x10_boolean equals(x10_char y, ref<T> x) { return false; }
    template<class T>
    static inline x10_boolean equals(x10_boolean y, ref<T> x) { return false; }

    static inline x10_boolean equals(const x10_double x,  const x10_double y)  { return x==y; }
    static inline x10_boolean equals(const x10_float x,   const x10_float y)   { return x==y; }
    static inline x10_boolean equals(const x10_long x,    const x10_long y)    { return x==y; }
    static inline x10_boolean equals(const x10_int x,     const x10_int y)     { return x==y; }
    static inline x10_boolean equals(const x10_short x,   const x10_short y)   { return x==y; }
    static inline x10_boolean equals(const x10_byte x,    const x10_byte y)    { return x==y; }
    static inline x10_boolean equals(const x10_char x,    const x10_char y)    { return x==y; }
    static inline x10_boolean equals(const x10_boolean x, const x10_boolean y) { return x==y; }


    template<class T> static inline x10_int hash_code(ref<T> x) {
        return x->hashCode();
    }

    static inline x10_int hash_code(const x10_double x) {
        return hash(reinterpret_cast<const unsigned char*>(&x), sizeof(x));
    }
    static inline x10_int hash_code(const x10_float x) {
        return hash(reinterpret_cast<const unsigned char*>(&x), sizeof(x));
    }
    static inline x10_int hash_code(const x10_long x) {
        return hash(reinterpret_cast<const unsigned char*>(&x), sizeof(x));
    }

    static inline x10_int hash_code(const x10_int x) { return x; }
    static inline x10_int hash_code(const x10_short x) { return x; }
    static inline x10_int hash_code(const x10_byte x) { return x; }
    static inline x10_int hash_code(const x10_char x) { return x; }
    static inline x10_int hash_code(const x10_boolean x) { return x; }



    template<class T> static inline ref<x10::lang::String> to_string(ref<T> x) {
        return x->toString();
    }

    // [DC] importing the old code requires me to put these back to what they
    // were.  Please add more options if types are not found, but do not use
    // the x10_ typedefs since this will break the implementation in unpleasant
    // ways (e.g. by using the wrong printf format specifier).
    
    ref<x10::lang::String> to_string(bool v);
    ref<x10::lang::String> to_string(unsigned char v);
    ref<x10::lang::String> to_string(signed char v);
    // ref<x10::lang::String> to_string(unsigned short v); used for x10_char
    ref<x10::lang::String> to_string(signed short v);
    ref<x10::lang::String> to_string(unsigned int v);
    ref<x10::lang::String> to_string(signed int v);
    ref<x10::lang::String> to_string(unsigned long v);
    ref<x10::lang::String> to_string(signed long v);
    ref<x10::lang::String> to_string(unsigned long long v);
    ref<x10::lang::String> to_string(signed long long v);
    
    ref<x10::lang::String> to_string(float v);
    ref<x10::lang::String> to_string(double v);

    // special case -- we want a static error if it conflicts with any of the above
    ref<x10::lang::String> to_string(x10_char v);



}

#endif
// vim:textwidth=100:tabstop=4:shiftwidth=4:expandtab
