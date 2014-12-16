/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

#ifndef X10_UTIL_CONCURRENT_ATOMIC_LONG_NATIVES_H
#define X10_UTIL_CONCURRENT_ATOMIC_LONG_NATIVES_H

#include <x10aux/config.h>

namespace x10 {
    namespace util {
        namespace concurrent {
            class AtomicLong; 

            class AtomicLongNatives {
            public:
                static x10_boolean compareAndSet(AtomicLong* obj,
                                                 x10_long expect, x10_long update);

                static x10_boolean weakCompareAndSet(AtomicLong* obj,
                                                     x10_long expect, x10_long update);

                static x10_long getAndAdd(AtomicLong* obj, x10_long delta);
	
                static x10_long addAndGet(AtomicLong* obj, x10_long delta);
            };
        }
    }
}
        
#endif /* X10_UTIL_CONCURRENT_ATOMIC_LONG_NATIVES_H */

// vim:tabstop=4:shiftwidth=4:expandtab
