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

#ifndef X10AUX_STATIC_INIT_H
#define X10AUX_STATIC_INIT_H

#include <x10aux/config.h>

#include <x10aux/deserialization_dispatcher.h>
#include <x10aux/serialization.h>
#include <x10aux/network.h>

namespace x10 {
    namespace lang {
        class CheckedThrowable;
    }
}

namespace x10aux {

    class StaticInitController {
      public:
        enum status {
            UNINITIALIZED = 0,
            INITIALIZING,
            INITIALIZED,
            EXCEPTION_RAISED
        };

        static void initField(volatile status* flag,
                              void (*init_func)(void),
                              ::x10::lang::CheckedThrowable**,
                              const char* fname);

      private:
        static void lock();
        static void await();
        static void unlock();
        static void notify();
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
