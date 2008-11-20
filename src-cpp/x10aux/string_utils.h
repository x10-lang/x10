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

    // these deliberately do not use the x10_ primitive typedefs, because their
    // implementations are tailored for the c types.  
    // TODO: perhaps only bool, char, int64_t, and const char* are necessary?
    ref<x10::lang::String> to_string(bool v);
    ref<x10::lang::String> to_string(char v);
    ref<x10::lang::String> to_string(short v);
    ref<x10::lang::String> to_string(int v);
    ref<x10::lang::String> to_string(int64_t v);
    ref<x10::lang::String> to_string(float v);
    ref<x10::lang::String> to_string(double v);
    ref<x10::lang::String> to_string(const char *v);

    template<class T> T from_string(const ref<x10::lang::String> &s);
}

    

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
