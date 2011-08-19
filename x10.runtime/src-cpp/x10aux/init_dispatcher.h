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

#ifndef X10AUX_INIT_DISPATCHER_H
#define X10AUX_INIT_DISPATCHER_H

#include <x10aux/config.h>
#include <x10aux/alloc.h>
#include <x10aux/RTT.h>

namespace x10aux {

    typedef void (*Initializer)();

    class InitDispatcher {
        protected:
        static InitDispatcher *it;

        Initializer *initv;
        int initc;
        size_t initsz;

        public:
        InitDispatcher () : initv(NULL), initc(0), initsz(0) { }
        ~InitDispatcher () { ::free(initv); }
        
        static void runInitializers();
        void runInitializers_(); 

        static void* addInitializer(Initializer init);
        void addInitializer_(Initializer init);
    };
}

#endif
// vim:tabstop=4:shiftwidth=4:expandtab
