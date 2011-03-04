/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

#ifndef __X10_LANG_STRUCT_H
#define __X10_LANG_STRUCT_H

#include <x10/lang/Struct.struct_h>

namespace x10 {
    namespace lang {

        struct Place;
        class Object;
        
        class Struct_methods {
        public:
            static void _instance_init(x10::lang::Struct& this_) {}
            static void _constructor(x10::lang::Struct& this_) {}
        };
    }
}
#endif // X10_LANG_STRUCT_H

namespace x10 {
    namespace lang {
        class Struct;
    }
}

#ifndef X10_LANG_STRUCT_H_NODEPS
#define X10_LANG_STRUCT_H_NODEPS
#include <x10/lang/Place.h>
#ifndef X10_LANG_STRUCT_H_IMPLEMENTATION
#define X10_LANG_STRUCT_H_IMPLEMENTATION

namespace x10 {
    namespace lang {
    }
}

#endif // X10_LANG_STRUCT_H_IMPLEMENTATION
#endif // X10_LANG_STRUCT_H_NODEPS

// vim:tabstop=4:shiftwidth=4:expandtab:textwidth=100
