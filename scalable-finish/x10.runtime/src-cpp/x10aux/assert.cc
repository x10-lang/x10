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
#include <x10aux/assert.h>
#include <stdio.h>

#include <x10/lang/String.h>

using namespace x10aux;
using namespace x10::lang;

void x10aux::x10__assertion_failed(const ref<x10::lang::String>& message) {
    if (message == null) {
        fprintf(stderr,"Assertion failed.\n");
    } else {
        fprintf(stderr,"Assertion failed: \"%s\"\n",message->c_str());
    }
    abort();
}

// vim: textwidth=80:tabstop=4:shiftwidth=4:expandtab
