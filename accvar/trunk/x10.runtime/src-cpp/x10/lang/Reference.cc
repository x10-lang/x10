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
#include <x10aux/alloc.h>

#include <x10/lang/Reference.h>
#include <x10/lang/Object.h>
#include <x10/lang/Place.h>

using namespace x10::lang;
using namespace x10aux;


x10aux::RuntimeType x10::lang::NullType::rtt;

void x10::lang::NullType::_initRTT() {
    if (rtt.initStageOne(&rtt)) return;
    rtt.initStageTwo("Null", RuntimeType::class_kind, 0, NULL, 0, NULL, NULL);
}

// vim:tabstop=4:shiftwidth=4:expandtab
