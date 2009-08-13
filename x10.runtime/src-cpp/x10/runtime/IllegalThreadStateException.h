/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

/**
 * IllegalThreadStateException that may be thrown by Thread.start()
 * when the calling thread is already started.
 */

#ifndef X10_RUNTIME_ILLEGAL_THREAD_STATE_EXCEPTION_H
#define X10_RUNTIME_ILLEGAL_THREAD_STATE_EXCEPTION_H

#include <x10/lang/Exception.h>

namespace x10 {
    namespace runtime {

        class IllegalThreadStateException : public x10::lang::Exception {
            // to do
        };
    }
}

#endif /* X10_RUNTIME_ILLEGAL_THREAD_STATE_EXCEPTION_H */
// vim:tabstop=4:shiftwidth=4:expandtab
