/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

#include <x10/lang/Complex.h>
#include <assert.h>

using namespace x10::lang;

#if defined(__APPLE__) && defined(__MACH__)
x10_complex ComplexNatives::divideByZeroHandling(x10_complex a, x10_complex b) {
    assert(b.real() == 0 && b.imag() == 0);
    if (a.real() != 0 || a.imag() != 0) {
        // Inf
        return x10_complex(::x10::lang::DoubleNatives::fromLongBits(0x7ff0000000000000LL),
                           ::x10::lang::DoubleNatives::fromLongBits(0x7ff0000000000000LL));
    } else {
        // NaN
        return x10_complex(::x10::lang::DoubleNatives::fromLongBits(0x7ff8000000000000LL),
			   ::x10::lang::DoubleNatives::fromLongBits(0x7ff8000000000000LL));
    }
}

#endif
