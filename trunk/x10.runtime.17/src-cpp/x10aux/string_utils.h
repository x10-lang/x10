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

    // Used by x10/util/StringBuilder.x10
    x10::lang::String vrc_to_string(x10aux::ref<x10::lang::ValRail<x10_char> > v);

}

    

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
