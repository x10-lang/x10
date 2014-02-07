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

#ifndef X10AUX_ARRAY_UTILS_H
#define X10AUX_ARRAY_UTILS_H

#include <cstdlib>
#include <cassert>

#include <x10aux/config.h>

namespace x10aux {

    // This is a convenience for writing @Native annotations.
    //
    // Often one will bind a c++ function e.g. f(C*) with
    // An x10 function of the form f(arr:Rail[C], offset:Int).
    // In this case we would like to have an elegant way of
    // handling the case where arr == null while avoiding duplication
    // which would cause the 'arr' argument to be evaluated twice.
    //
    // The function should be used as follows:
    //
    // @Native("c++", "f(lookup_or_null(#arr, #offset))")
    // static def f(arr:Rail[C], offset:Int) { }
    template<class T> T *lookup_or_null (::x10::lang::Rail<T>* arr, x10_int offset)
    {
        if (NULL == arr) return NULL;
        return &arr->raw[offset];
    }

} //namespace x10aux


#endif
