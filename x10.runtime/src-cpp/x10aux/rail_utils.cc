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

#include <x10aux/rail_utils.h>
#include <x10aux/throw.h>

#include <x10/lang/String.h>

#include <x10/lang/ArrayIndexOutOfBoundsException.h>

using namespace x10::lang;
using namespace x10aux;

void x10aux::throwArrayIndexOutOfBoundsException(x10_int index, x10_int length) {
#ifndef NO_EXCEPTIONS
    char *msg = alloc_printf("Index %ld out of range (rail has length %ld)", (long)index, ((long)length));
    throwException(x10::lang::ArrayIndexOutOfBoundsException::_make(String::Lit(msg)));
#endif
}
// vim:tabstop=4:shiftwidth=4:expandtab
