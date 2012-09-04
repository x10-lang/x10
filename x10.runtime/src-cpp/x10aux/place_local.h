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

#ifndef X10AUX_PLACELOCAL_H
#define X10AUX_PLACELOCAL_H

#include <x10aux/config.h>

namespace x10aux {
    class reentrant_lock;

    class place_local {
    private:
        class Bucket {
        public:
            x10_int _id;
            void *_data;
            Bucket *_next;
        };

        static x10_int _nextId;
        static Bucket **_buckets;
        static x10aux::reentrant_lock* _lock;

    public:
        static void** _fastData;
        static void initialize();
        static x10_int nextId();
        static void* lookupData(x10_int id);
        static void registerData(x10_int id, void *data);
        static void unregisterData(x10_int id);
        template<class T> friend const char *x10aux::typeName();
    };

	template<> inline const char *typeName<place_local>() { return "place_local"; }
    template<> inline const char *typeName<place_local::Bucket*>() { return "place_local::Bucket *"; }
    template<> inline const char *typeName<place_local::Bucket>() { return "place_local::Bucket"; }
}

#endif


