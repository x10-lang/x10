/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2011-2014.
 */

#include <stdio.h>
#include <stdlib.h>

#include <jni.h>
#include "wrap_lapack.h"

#ifndef __ElemTypeIsFloat__
#include "jni_lapack_double.cc"
#else
#include "jni_lapack_float.cc"
#endif

// vim:tabstop=4:shiftwidth=4:expandtab
