#ifndef X10AUX_STRING_UTILS_H
#define X10AUX_STRING_UTILS_H

#include <x10aux/config.h>

#include <x10/lang/Rail.h>

namespace x10 {
    namespace lang {
        class String;
    }
}

namespace x10aux {
    x10::lang::Rail<ref<x10::lang::String> > *convert_args(int ac, char **av);

    void free_args(const ref<x10::lang::Rail<ref<x10::lang::String> > > &arr);

    // these need to use the x10_ primitive typedefs, because otherwise they
    // are not found.
    ref<x10::lang::String> to_string(x10_boolean v);
    ref<x10::lang::String> to_string(x10_byte v);
    ref<x10::lang::String> to_string(x10_char v);
    ref<x10::lang::String> to_string(x10_short v);
    ref<x10::lang::String> to_string(x10_int v);
    ref<x10::lang::String> to_string(x10_long v);
    ref<x10::lang::String> to_string(x10_float v);
    ref<x10::lang::String> to_string(x10_double v);
    ref<x10::lang::String> to_string(const char *v);

    ref<x10::lang::String> to_string(x10aux::ref<x10::lang::ValRail<x10_char> > v);

    template<class T> T from_string(const ref<x10::lang::String> &s);
}

    

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
