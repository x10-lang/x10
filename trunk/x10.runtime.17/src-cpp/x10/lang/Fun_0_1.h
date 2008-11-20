#ifndef X10_LANG_FUN_0_1_H
#define X10_LANG_FUN_0_1_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        template<class P1, class R> class Fun_0_1 {
            virtual ~Fun_0_1() { }
            virtual R apply(const P1 &p1) = 0;
        };
    }
}
#endif
// vim:tabstop=4:shiftwidth=4:expandtab
