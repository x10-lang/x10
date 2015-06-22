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

#ifndef X10AUX_PLACELOCAL_H
#define X10AUX_PLACELOCAL_H

#include <x10aux/config.h>
#include <x10aux/basic_functions.h>
#include <x10aux/simple_hashmap.h>

namespace x10aux {
    class reentrant_lock;

    class place_local {
#ifndef __FCC_VERSION
    private:
#else
    public:
#endif
        static x10_int _nextId;
        static simple_hashmap<x10_long, void*> *_map;
        static x10aux::reentrant_lock* _lock;

    public:
        static void** _fastData;
        static void initialize();
        static x10_long nextId();
        static void* get(x10_long id);
        static void put(x10_long id, void *data);
        static void remove(x10_long id);
        template<class T> friend const char *::x10aux::typeName();
    };

    template<> inline const char *typeName<place_local>() { return "place_local"; }
}

#endif
