#ifndef X10_LANG_FUN_0_0_H
#define X10_LANG_FUN_0_0_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        template<class R> class Fun_0_0 {
            virtual ~Fun_0_0() { }
            virtual R apply() = 0;
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
