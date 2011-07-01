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
