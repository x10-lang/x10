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

}
    

#endif
