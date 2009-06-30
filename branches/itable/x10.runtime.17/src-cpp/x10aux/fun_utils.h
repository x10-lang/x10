#ifndef X10AUX_FUN_UTILS_H
#define X10AUX_FUN_UTILS_H

#include <x10aux/config.h>
#include <x10aux/ref.h>

namespace x10aux {
    class serialization_buffer;
    class addr_map;

    class AnyFun {
    public:
        static void _serialize(ref<AnyFun> this_,
                               x10aux::serialization_buffer &buf,
                               x10aux::addr_map &m);
    };
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
