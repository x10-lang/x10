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

#ifndef X10_LANG_INT_H_NODEPS
#define X10_LANG_INT_H_NODEPS

/*
 * Must include header files for any types
 * mentioned in @Native annotations but not
 * present in method return types.
 */
#define X10_LANG_INTRANGE_H_NODEPS
#include <x10/lang/IntRange.h>
#undef X10_LANG_INTRANGE_H_NODEPS

#endif
