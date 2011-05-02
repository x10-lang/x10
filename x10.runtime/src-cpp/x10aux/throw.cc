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

#include <x10aux/config.h>
#include <x10aux/throw.h>

#include <x10/lang/ArithmeticException.h>
#include <x10/lang/String.h>

void x10aux::throwArithmeticException() {
#ifndef NO_EXCEPTIONS
    throwException(x10::lang::ArithmeticException::_make(x10::lang::String::Lit("divide by zero")));
#endif
}
