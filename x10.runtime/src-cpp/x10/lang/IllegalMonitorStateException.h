/*
 * (c) Copyright IBM Corporation 2008
 *
 * This file is part of XRX/C++ native layer implementation.
 */

/**
 * IllegalMonitorStateException that may be thrown by
 * Lock.unlock().
 */

#ifndef __XRX_ILLEGAL_MONITOR_STATE_EXCEPTION_H
#define __XRX_ILLEGAL_MONITOR_STATE_EXCEPTION_H

#include <x10/lang/Exception.h>

namespace x10 {
    namespace lang {

        class IllegalMonitorStateException : public x10::lang::Exception {
            // to do
        };
    }
}

#endif /* __XRX_ILLEGAL_MONITOR_STATE_EXCEPTION_H */
// vim:tabstop=4:shiftwidth=4:expandtab
