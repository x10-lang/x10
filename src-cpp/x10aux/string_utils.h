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

    // these need to use the x10_ primitive typedefs, because otherwise they
    // are not found. [DC] found by whom and by what mechanism?  overload
    // resolution respects typedefs...

    x10::lang::String to_string(x10_boolean v);
    x10::lang::String to_string(x10_byte v);
    x10::lang::String to_string(x10_char v);
    x10::lang::String to_string(x10_short v);
    x10::lang::String to_string(x10_int v);
    x10::lang::String to_string(x10_long v);
    x10::lang::String to_string(x10_float v);
    x10::lang::String to_string(x10_double v);
    x10::lang::String to_string(const char *v);

    x10::lang::String to_string(x10aux::ref<x10::lang::ValRail<x10_char> > v);

    template<class T> ref<x10::lang::String> to_stringp(T v) {
        return to_string(v);
    }

    template<class T> T from_string(const ref<x10::lang::String> &s);
}

    

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
