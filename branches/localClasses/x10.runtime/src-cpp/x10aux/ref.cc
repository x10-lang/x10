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

#include <x10aux/ref.h>
#include <x10aux/network.h>
#include <x10aux/throw.h>
#include <x10/lang/NullPointerException.h>
#include <x10/lang/BadPlaceException.h>
#include <x10aux/reference_logger.h>


using namespace x10aux;
using namespace x10::lang;

// do not call this if NO_EXCEPTIONS is defined
// defined here because it depends on NullPointerException and we don't want a header cycle
void x10aux::throwNPE() { throwException<NullPointerException>(); }

// vim:tabstop=4:shiftwidth=4:expandtab
