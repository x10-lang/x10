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

#ifndef X10_AUX_PCOND_H
#define X10_AUX_PCOND_H

#include <x10aux/config.h>

#include <pthread.h>

namespace x10aux {
    class pcond {
    public:
        pcond() { initialize(); }
        ~pcond() { teardown(); }
        void lock();
        void unlock();
        void release();
        void await();
        void await(x10_long timeout);
        x10_boolean complete();

    private:
        pthread_mutex_t __mutex;
        pthread_cond_t __cond;
        bool unblocked;
        void initialize();
        void teardown();
    };
}

#endif /* X10_AUX_PCOND_H */

// vim:tabstop=4:shiftwidth=4:expandtab
