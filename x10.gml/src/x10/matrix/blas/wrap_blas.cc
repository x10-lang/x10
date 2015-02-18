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

#include <stdio.h>
#include <stdlib.h>

//using namespace std;
#include "wrap_blas.h"

#ifndef __ElemTypeIsFloat__
#include "wrap_blas_double.cc"
#else
#include "wrap_blas_float.cc"
#endif



