/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#ifndef X10AUX_ASSERT_H
#define X10AUX_ASSERT_H

#include <x10aux/config.h>
#include <x10aux/basic_functions.h>

namespace x10 { namespace lang { class String; } }

namespace x10aux {

    void x10__assertion_failed(::x10::lang::String* message) X10_PRAGMA_NORETURN;

    inline void x10__assert(x10_boolean val) {
        if (!val)
            x10__assertion_failed(NULL);
    }
    
    template <class T> inline void x10__assert(x10_boolean val, T message) {
        if (!val)
            x10__assertion_failed(::x10aux::safe_to_string(message));
    }
}

#endif

// vim:tabstop=4:shiftwidth=4:expandtab
