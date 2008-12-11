#ifndef X10AUX_STRING_UTILS_H
#define X10AUX_STRING_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

#include <x10/lang/String.h>

namespace x10 {
    namespace lang {
        template<class T> class Rail;
        template<class T> class ValRail;
    }
}

namespace x10aux {
    x10::lang::Rail<ref<x10::lang::String> > *convert_args(int ac, char **av);

    void free_args(const ref<x10::lang::Rail<ref<x10::lang::String> > > &arr);

    // [DC] importing the old code requires me to put these back to what they
    // were.  Please add more options if types are not found, but do not use
    // the x10_ typedefs since this will break the implementation in unpleasant
    // ways.

    x10::lang::String to_string(bool v);
    x10::lang::String to_string(unsigned char v);
    x10::lang::String to_string(signed char v);
    x10::lang::String to_string(unsigned short v);
    x10::lang::String to_string(signed short v);
    x10::lang::String to_string(unsigned int v);
    x10::lang::String to_string(signed int v);
    x10::lang::String to_string(unsigned long v);
    x10::lang::String to_string(signed long v);
    x10::lang::String to_string(unsigned long long v);
    x10::lang::String to_string(signed long long v);

    x10::lang::String to_string(float v);
    x10::lang::String to_string(double v);

    // special case -- we want a static error if it conflicts with any of the above
    x10::lang::String to_string(x10_char v);

    // Used by x10/util/StringBuilder.x10
    x10::lang::String vrc_to_string(x10aux::ref<x10::lang::ValRail<x10_char> > v);

/*
    template<class T> ref<x10::lang::String> to_stringp(T v) {
        return to_string(v);
    }
*/

    //template<class T> T from_string(const ref<x10::lang::String> &s);
}

    

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
