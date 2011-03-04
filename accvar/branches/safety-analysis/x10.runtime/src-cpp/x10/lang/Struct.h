#ifndef __X10_LANG_STRUCT_H
#define __X10_LANG_STRUCT_H

#include <x10rt17.h>

namespace x10 {
    namespace lang {

        class Struct_methods {
        public:
            static void _instance_init(x10::lang::Struct *this_);
            static void _constructor(x10::lang::Struct *this_);
        };

    }
}
#endif // X10_LANG_STRUCT_H

namespace x10 {
    namespace lang {
        class Struct;
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
