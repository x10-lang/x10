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

#include <x10aux/pcond.h>

#include <errno.h>

void x10aux::pcond::initialize() {
    pthread_mutex_init(&__mutex, NULL);
    pthread_cond_init(&__cond, NULL);
    blocked = true;
}

void x10aux::pcond::teardown() {
    pthread_mutex_destroy(&__mutex);
    pthread_cond_destroy(&__cond);
}

void x10aux::pcond::lock() {
    pthread_mutex_lock(&__mutex);
}

void x10aux::pcond::unlock() {
    pthread_mutex_unlock(&__mutex);
}

void x10aux::pcond::release() {
    pthread_mutex_lock(&__mutex);
    blocked = false;
    pthread_mutex_unlock(&__mutex);
    pthread_cond_signal(&__cond);
}

void x10aux::pcond::await() {
    if (blocked) {
        pthread_mutex_lock(&__mutex);
        while (blocked) {
           pthread_cond_wait(&__cond, &__mutex);
        }
        pthread_mutex_unlock(&__mutex);
    }
}

// vim:tabstop=4:shiftwidth=4:expandtab
