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

#include <x10aux/config.h>
#include <x10aux/assert.h>
#include <stdio.h>
#include <stdlib.h>

#include <x10/lang/String.h>
#include <x10/lang/AssertionError.h>

using namespace x10aux;
using namespace x10::lang;

void x10aux::x10__assertion_failed(x10::lang::String* message) {
    if (NULL == message) {
        x10aux::throwException(x10::lang::AssertionError::_make());
    } else {
        x10aux::throwException(x10::lang::AssertionError::_make(message));
    }
}

// vim: textwidth=80:tabstop=4:shiftwidth=4:expandtab
