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

#ifndef X10_LANG_COMPLEX_H
#define X10_LANG_COMPLEX_H

#include <x10aux/config.h>

#include <complex>
#include <x10/lang/Double.h>

typedef std::complex<double> x10_complex;

namespace x10 {
    namespace lang {
        class String;
        
        class ComplexNatives {
        public:
            static x10_complex _plus(x10_complex a, x10_complex b) {
                return x10_complex(a.real()+b.real(), a.imag()+b.imag());
            }
            static x10_complex _plus(x10_double a, x10_complex b) {
                return x10_complex(a+b.real(), b.imag());
            }
            static x10_complex _plus(x10_complex a, x10_double b) {
                return x10_complex(a.real()+b, a.imag());
            }

            static x10_complex _minus(x10_complex a, x10_complex b) {
                return x10_complex(a.real()-b.real(), a.imag()-b.imag());
            }
            static x10_complex _minus(x10_double a, x10_complex b) {
                return x10_complex(a-b.real(), -b.imag());
            }
            static x10_complex _minus(x10_complex a, x10_double b) {
                return x10_complex(a.real()-b, a.imag());
            }

            static x10_complex _times(x10_complex a, x10_complex b) {
                return x10_complex(a.real() * b.real() - a.imag() * b.imag(),
                                   a.real() * b.imag() + a.imag() * b.real());
            }
            static x10_complex _times(x10_double a, x10_complex b) {
                return x10_complex(a*b.real(), a*b.imag());
            }
            static x10_complex _times(x10_complex a, x10_double b) {
                return x10_complex(a.real()*b, a.imag()*b);
            }

            #if defined(__APPLE__) && defined(__MACH__)
            static x10_complex divideByZeroHandling(x10_complex a, x10_complex b);
            #endif
            static x10_complex _divide(x10_complex a, x10_complex b) {
                #if defined(__APPLE__) && defined(__MACH__)
	        // Compensate for nonstandard impl of division by zero in MacOS X std::complex
	        if (b.real() == 0 && b.imag() == 0) {
		    return divideByZeroHandling(a, b);
                }
                return a / b;
                #else
	        return a / b;
		#endif
            }
            static x10_complex _divide(x10_double a, x10_complex b) {
	        return a / b;
            }
            static x10_complex _divide(x10_complex a, x10_double b) {
	        return a / b;
            }

            static x10_complex conj(x10_complex a) {
                return x10_complex(a.real(), -a.imag());
            }
            
            static x10_boolean isInfinite(x10_complex value) {
                if (isNaN(value)) return false;
                return DoubleNatives::isInfinite(value.real()) ||
                    DoubleNatives::isInfinite(value.imag());
            }
                
            static x10_boolean isNaN(x10_complex value) {
                return DoubleNatives::isNaN(value.real()) ||
                    DoubleNatives::isNaN(value.imag());
            }
        };
    }
}

#endif /* X10_LANG_COMPLEX_H */
