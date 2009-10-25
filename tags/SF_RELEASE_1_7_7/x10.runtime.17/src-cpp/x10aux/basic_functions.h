#ifndef X10AUX_BASIC_FUNCTIONS_H
#define X10AUX_BASIC_FUNCTIONS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>
#include <x10aux/hash.h>
#include <x10/lang/String.h>

namespace x10aux {

    template<class T> inline ref<x10::lang::String> class_name(ref<T> x) {
        return x10::lang::String::Lit(((const T*)x.get())->_type()->name());
    }

    inline ref<x10::lang::String> class_name(const x10_double) {
        return x10::lang::String::Lit(getRTT<x10_double>()->name());
    }
    inline ref<x10::lang::String> class_name(const x10_float) {
        return x10::lang::String::Lit(getRTT<x10_float>()->name());
    }
    inline ref<x10::lang::String> class_name(const x10_long) {
        return x10::lang::String::Lit(getRTT<x10_long>()->name());
    }

    inline ref<x10::lang::String> class_name(const x10_int) {
        return x10::lang::String::Lit(getRTT<x10_int>()->name());
    }
    inline ref<x10::lang::String> class_name(const x10_short) {
        return x10::lang::String::Lit(getRTT<x10_short>()->name());
    }
    inline ref<x10::lang::String> class_name(const x10_byte) {
        return x10::lang::String::Lit(getRTT<x10_byte>()->name());
    }
    inline ref<x10::lang::String> class_name(const x10_char) {
        return x10::lang::String::Lit(getRTT<x10_char>()->name());
    }
    inline ref<x10::lang::String> class_name(const x10_boolean) {
        return x10::lang::String::Lit(getRTT<x10_boolean>()->name());
    }

    template<class T, class U>
    class Equals {
        public: static inline x10_boolean _(ref<T> x, ref<U> y) { return x->equals(y); }
    };

    template<class T>
    class Equals<T, x10::lang::Object> {
        public: static inline x10_boolean _(ref<T> x, ref<x10::lang::Object> y) { return x->x10::lang::Object::equals(y); }
    };

    template<class T, class U>
    inline x10_boolean equals(ref<T> x, ref<U> y) { return Equals<T, U>::_(x, y); }

    template<class T>
    inline x10_boolean equals(ref<T> x, x10_double y) { return false; }
    template<class T>
    inline x10_boolean equals(ref<T> x, x10_float y) { return false; }
    template<class T>
    inline x10_boolean equals(ref<T> x, x10_long y) { return false; }
    template<class T>
    inline x10_boolean equals(ref<T> x, x10_int y) { return false; }
    template<class T>
    inline x10_boolean equals(ref<T> x, x10_short y) { return false; }
    template<class T>
    inline x10_boolean equals(ref<T> x, x10_byte y) { return false; }
    template<class T>
    inline x10_boolean equals(ref<T> x, x10_char y) { return false; }
    template<class T>
    inline x10_boolean equals(ref<T> x, x10_boolean y) { return false; }

    template<class T>
    inline x10_boolean equals(x10_double y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean equals(x10_float y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean equals(x10_long y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean equals(x10_int y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean equals(x10_short y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean equals(x10_byte y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean equals(x10_char y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean equals(x10_boolean y, ref<T> x) { return false; }

    inline x10_boolean equals(const x10_double x,  const x10_double y)  { return x==y; }
    inline x10_boolean equals(const x10_float x,   const x10_float y)   { return x==y; }
    inline x10_boolean equals(const x10_long x,    const x10_long y)    { return x==y; }
    inline x10_boolean equals(const x10_int x,     const x10_int y)     { return x==y; }
    inline x10_boolean equals(const x10_short x,   const x10_short y)   { return x==y; }
    inline x10_boolean equals(const x10_byte x,    const x10_byte y)    { return x==y; }
    inline x10_boolean equals(const x10_char x,    const x10_char y)    { return x==y; }
    inline x10_boolean equals(const x10_boolean x, const x10_boolean y) { return x==y; }

    template<class T, class U>
    inline x10_boolean struct_equals(ref<T> x, ref<U> y) {
        if (x.isNull()) {
            return y.isNull();
        } else if (y.isNull()) {
            return false; // x != null, needed for remote refs
        } else if (remote_ref::is_remote(x.get()) || remote_ref::is_remote(y.get())) {
            return remote_ref::equals(x.get(), y.get());
        } else {
            return x->_struct_equals(y);
        }
    }

    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_double y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_float y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_long y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_int y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_short y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_byte y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_char y) { return false; }
    template<class T>
    inline x10_boolean struct_equals(ref<T> x, x10_boolean y) { return false; }

    template<class T>
    inline x10_boolean struct_equals(x10_double y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_float y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_long y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_int y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_short y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_byte y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_char y, ref<T> x) { return false; }
    template<class T>
    inline x10_boolean struct_equals(x10_boolean y, ref<T> x) { return false; }

    inline x10_boolean struct_equals(const x10_double x,  const x10_double y)  { return x==y; }
    inline x10_boolean struct_equals(const x10_float x,   const x10_float y)   { return x==y; }
    inline x10_boolean struct_equals(const x10_long x,    const x10_long y)    { return x==y; }
    inline x10_boolean struct_equals(const x10_int x,     const x10_int y)     { return x==y; }
    inline x10_boolean struct_equals(const x10_short x,   const x10_short y)   { return x==y; }
    inline x10_boolean struct_equals(const x10_byte x,    const x10_byte y)    { return x==y; }
    inline x10_boolean struct_equals(const x10_char x,    const x10_char y)    { return x==y; }
    inline x10_boolean struct_equals(const x10_boolean x, const x10_boolean y) { return x==y; }

    template<class T> inline x10_int hash_code(ref<T> x) {
        return x->hashCode();
    }

    inline x10_int hash_code(const x10_double x) {
        return hash(reinterpret_cast<const unsigned char*>(&x), sizeof(x));
    }
    inline x10_int hash_code(const x10_float x) {
        return hash(reinterpret_cast<const unsigned char*>(&x), sizeof(x));
    }
    inline x10_int hash_code(const x10_long x) {
        return hash(reinterpret_cast<const unsigned char*>(&x), sizeof(x));
    }

    inline x10_int hash_code(const x10_int x) { return x; }
    inline x10_int hash_code(const x10_short x) { return x; }
    inline x10_int hash_code(const x10_byte x) { return x; }
    inline x10_int hash_code(const x10_char x) { return x; }
    inline x10_int hash_code(const x10_boolean x) { return x; }



    template<class T> ref<x10::lang::String> to_string(ref<T> x) {
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
