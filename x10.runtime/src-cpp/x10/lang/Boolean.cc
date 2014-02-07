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

#include <x10/lang/Boolean.h>
#include <x10/lang/String.h>

#include <x10aux/basic_functions.h>

#include <strings.h>

using namespace x10::lang;

String* x10::lang::BooleanNatives::toString(x10_boolean value) {
    return x10aux::to_string(value);
}

x10_boolean x10::lang::BooleanNatives::parseBoolean(String* s) {
    return NULL != s && !::strcasecmp(s->c_str(), "true");
}

// vim:tabstop=4:shiftwidth=4:expandtab
