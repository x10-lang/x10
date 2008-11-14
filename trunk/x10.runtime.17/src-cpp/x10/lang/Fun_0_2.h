#ifndef X10_LANG_FUN_0_2_H
#define X10_LANG_FUN_0_2_H

#include <x10aux/config.h>

namespace x10 {
    namespace lang {
        template<class P1, class P2, class R> class Fun_0_2 {
            virtual ~Fun_0_2() { };
            virtual R apply(const P1 &p1, const P2 &p2) = 0;
        };
    }
}
#endif
