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

#ifndef X10_LANG_COMPLEX_H
#define X10_LANG_COMPLEX_H

#include <x10aux/config.h>

#include <complex>

typedef std::complex<double> x10_complex;

namespace x10 {
    namespace lang {
        class String;
        
        class ComplexNatives {
        public:
            static x10_boolean isInfinite(x10_complex value) {
                if (isNaN(value)) return false;
                return ::isinf(value.real()) || ::isinf(value.imag());
            }
                
            static x10_boolean isNaN(x10_complex value) {
                return ::isnan(value.real()) || ::isnan(value.imag());
            }
        };
    }
}

#endif /* X10_LANG_COMPLEX_H */
