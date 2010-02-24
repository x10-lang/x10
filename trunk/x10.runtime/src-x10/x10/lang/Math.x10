/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 *  (C) Copyright Australian National University 2009-2010.
 */

package x10.lang;

import x10.compiler.Native;

public final class Math {
   public const E = 2.718281828459045D;
   public const PI = 3.141592653589793D;

   public static safe def abs(a:Double):Double = a<=0.0 ? 0.0-a : a;
   public static safe def abs(a:Int):Int = a<0 ? -a : a;

   @Native("cuda", "fabsf(#1)")
   public static safe def abs(a:Float):Float = a<=0.0f ? 0.0f-a : a;

   public static safe def abs(a:Long):Long = a<0l ? -a : a;

   @Native("java", "java.lang.Math.ceil(#1)")
   @Native("c++", "x10aux::math_utils::ceil(#1)")
   public static native safe def ceil(a:Double):Double;

   @Native("java", "java.lang.Math.floor(#1)")
   @Native("c++", "x10aux::math_utils::floor(#1)")
   public static native safe def floor(a:Double):Double;

   @Native("java", "java.lang.Math.round(#1)")
   @Native("c++", "x10aux::math_utils::round(#1)")
   public static native safe def round(a:Double):Double;

   @Native("java", "java.lang.Math.pow(#1, #2)")
   @Native("c++", "x10aux::math_utils::pow(#1,#2)")
   public static native def pow(a:Double, b:Double):Double;

    /**
     * Returns the principal value of the complex power <code>a^b</code>.
     * The branch cuts are on the real line at (-inf, 0) for real(b) <= 0,
     * and (-inf, 0] for real(b) > 0.   pow(0,0) is not defined.
     * @return a raised to the power <code>b</code>
     * @see http://mathworld.wolfram.com/Power.html
     */
    public static safe def pow(a:Complex, b:Complex) = Math.exp(Math.log(a) * b);

   @Native("java", "java.lang.Math.exp(#1)")
   @Native("c++", "x10aux::math_utils::exp(#1)")
   public static native def exp(a:Double):Double;

   // GPUs don't like doubles
   @Native("java", "(float)java.lang.Math.exp(#1)")
   @Native("c++", "(x10_float)x10aux::math_utils::exp(#1)")
   @Native("cuda", "__expf(#1)")
   public static native def exp(a:Float):Float;

    /**
     * @return the exponential function <code>e^a</code>
     * @see http://mathworld.wolfram.com/ExponentialFunction.html
     */
    public static safe def exp(a:Complex):Complex {
       if (a.isNaN()) {
           return Complex.NaN;
       }
       val expRe = Math.exp(a.re);
       return Complex(expRe * Math.cos(a.im), expRe * Math.sin(a.im));
    }

   @Native("java", "java.lang.Math.expm1(#1)")
   @Native("c++", "x10aux::math_utils::expm1(#1)")
   public static native def expm1(a:Double):Double;

   @Native("java", "java.lang.Math.cos(#1)")
   @Native("c++", "x10aux::math_utils::cos(#1)")
   public static native def cos(a:Double):Double;

    /**
     * @return the cosine of <code>z</code>
     * @see http://mathworld.wolfram.com/Cosine.html
     */
    public static safe def cos(z:Complex):Complex {
        if (z.im == 0.0) {
            return Complex(Math.cos(z.re), 0.0);
        } else {
            return Complex(Math.cos(z.re) * Math.cosh(z.im), Math.sin(z.re) * Math.sinh(-(z.im)));
        }
    }

   @Native("java", "java.lang.Math.sin(#1)")
   @Native("c++", "x10aux::math_utils::sin(#1)")
   public static native def sin(a:Double):Double;

    /**
     * @return the sine of <code>z</code>
     * @see http://mathworld.wolfram.com/Sine.html
     */
    public static safe def sin(z:Complex):Complex {
        if (z.im == 0.0) {
            return Complex(Math.sin(z.re), 0.0);
        } else {
            return Complex(Math.sin(z.re) * Math.cosh(z.im), Math.cos(z.re) * Math.sinh(z.im));
        }
    }

   @Native("java", "java.lang.Math.tan(#1)")
   @Native("c++", "x10aux::math_utils::tan(#1)")
   public static native def tan(a:Double):Double;

    /**
     * @return the tangent of <code>z</code>
     * @see http://mathworld.wolfram.com/Tangent.html
     */
    public static safe def tan(z:Complex):Complex {
        if (z.im == 0.0) {
            return Complex(Math.tan(z.re), 0.0);
        } else {
            // tan(z) = e^2iz - 1 / i (e^21z + 1)
            val e2IZ = Math.exp(2.0 * Complex.I * z);
            return (e2IZ - 1.0) / (Complex.I * (e2IZ + 1.0));
        }
    }

   @Native("java", "java.lang.Math.acos(#1)")
   @Native("c++", "x10aux::math_utils::acos(#1)")
   public static native def acos(a:Double):Double;

    /**
     * Returns the principal value of the inverse cosine of <code>z</code>.
     * The branch cuts are on the real line at (-inf, -1) and (1, +inf)
     * The real part of the inverse cosine ranges from 0 to PI.
     * @return the inverse cosine of <code>z</code>
     * @see http://mathworld.wolfram.com/InverseCosine.html
     */
    public static safe def acos(z:Complex):Complex {
        if (z.im == 0.0 && Math.abs(z.re) <= 1.0) {
            return Complex(Math.acos(z.re), 0.0);
        } else {
            // acos(z) = pi / 2.0 + i log(iz + sqrt(1 - z^2))
            return Math.PI / 2.0 + Complex.I * Math.log(Complex.I * z + Math.sqrt(1.0 - z * z));
        }
    }

   @Native("java", "java.lang.Math.asin(#1)")
   @Native("c++", "x10aux::math_utils::asin(#1)")
   public static native def asin(a:Double):Double;

    /**
     * Returns the principal value of the inverse sine of <code>z</code>.
     * The branch cuts are on the real line at (-inf, -1) and (1, +inf)
     * The real part of the inverse sine ranges from -PI/2 to +PI/2.
     * @return the inverse sine of <code>z</code>
     * @see http://mathworld.wolfram.com/InverseSine.html
     */
    public static safe def asin(z:Complex):Complex {
        if (z.im == 0.0 && Math.abs(z.re) <= 1.0) {
            return Complex(Math.asin(z.re), 0.0);
        } else {
            // asin(z) = -i * log(iz + sqrt(1 - z^2))
            return -Complex.I * Math.log(Complex.I * z + Math.sqrt(1.0 - z * z));
        }
    }

   @Native("java", "java.lang.Math.atan(#1)")
   @Native("c++", "x10aux::math_utils::atan(#1)")
   public static native def atan(a:Double):Double;

    /**
     * Returns the principal value of the inverse tangent of <code>z</code>.
     * The branch cuts are on the imaginary line at (-i*inf, -i] and [i, i*inf)
     * The real part of the inverse tangent ranges from -PI/2 to +PI/2.
     * @return the principal value of the inverse tangent of <code>z</code>
     * @see http://mathworld.wolfram.com/InverseTangent.html
     */
    public static safe def atan(z:Complex):Complex {
        if (z.im == 0.0) {
            return Complex(Math.atan(z.re), 0.0);
        } else if (z == Complex.I) {
            return Complex(0.0, Double.POSITIVE_INFINITY);
        } else if (z == -Complex.I) {
            return Complex(0.0, Double.NEGATIVE_INFINITY);
        } else {
            // atan(z) = i/2 * log(1 - iz) - log(1 + iz))
            return Complex.I / 2.0 * (Math.log(1.0 - Complex.I * z) - Math.log(1.0 + Complex.I * z));
        }
    }

   @Native("java", "java.lang.Math.atan2(#1,#2)")
   @Native("c++", "x10aux::math_utils::atan2(#1,#2)")
   public static native def atan2(a:Double, b:Double):Double;

   @Native("java", "java.lang.Math.cosh(#1)")
   @Native("c++", "x10aux::math_utils::cosh(#1)")
   public static native def cosh(a:Double):Double;

    /**
     * @return the hyperbolic cosine of <code>z</code>
     * @see http://mathworld.wolfram.com/HyperbolicCosine.html
     */
    public static safe def cosh(z:Complex):Complex {
        if (z.isNaN()) {
            return Complex.NaN;
        } else if (z.im == 0.0) {
            return Complex(Math.cosh(z.re), 0.0);
        } else {
            return Complex(Math.cosh(z.re) * Math.cos(z.im), Math.sinh(z.re) * Math.sin(z.im));
        }
    }

   @Native("java", "java.lang.Math.sinh(#1)")
   @Native("c++", "x10aux::math_utils::sinh(#1)")
   public static native def sinh(a:Double):Double;

    /**
     * @return the hyperbolic sine of <code>z</code>
     * @see http://mathworld.wolfram.com/HyperbolicSine.html
     */
    public static safe def sinh(z:Complex):Complex {
        if (z.isNaN()) {
            return Complex.NaN;
        } else if (z.im == 0.0) {
            return Complex(Math.sinh(z.re), 0.0);
        } else {
            return Complex(Math.sinh(z.re) * Math.cos(z.im), Math.cosh(z.re) * Math.sin(z.im));
        }
    }

   @Native("java", "java.lang.Math.tanh(#1)")
   @Native("c++", "x10aux::math_utils::tanh(#1)")
   public static native def tanh(a:Double):Double;

    /**
     * @return the hyperbolic tangent of <code>z</code>
     * @see http://mathworld.wolfram.com/HyperbolicTangent.html
     */
    public static safe def tanh(z:Complex):Complex {
        if (z.isNaN()) {
            return Complex.NaN;
        }
        val d = Math.cosh(2.0 * z.re) + Math.cos(2.0 * z.im);
        return Complex(Math.sinh(2.0 * z.re)/d, Math.sin(2.0 * z.im)/d);
    }

   @Native("java", "java.lang.Math.sqrt(#1)")
   @Native("c++", "x10aux::math_utils::sqrt(#1)")
   public static native def sqrt(a:Double):Double;

    /**
     * Returns the principal value of the square root of <code>z</code>.
     * The branch cut is on the real line at (-inf, 0)
     * @return the principal square root of <code>z</code>
     * @see http://mathworld.wolfram.com/SquareRoot.html
     */
    public static safe def sqrt(z:Complex):Complex {
        if (z.isNaN()) {
            return Complex.NaN;
        } else if (z == Complex.ZERO) {
            return Complex.ZERO;
        } else {
            val t = Math.sqrt((Math.abs(z.re) + z.abs()) / 2.0);
            if (z.re >= 0.0) {
                return Complex(t, z.im / (2.0 * t));
            } else {
                return Complex(Math.abs(z.im) / (2.0 * t), Math.copySign(t, z.im));
            }
        }
    }

   // GPUs don't like doubles
   @Native("java", "(float)java.lang.Math.sqrt(#1)")
   @Native("c++", "(x10_float)x10aux::math_utils::sqrt(#1)")
   @Native("cuda", "sqrtf(#1)")
   public static native def sqrt(a:Float):Float;

   @Native("java", "java.lang.Math.cbrt(#1)")
   @Native("c++", "x10aux::math_utils::cbrt(#1)")
   public static native def cbrt(a:Double):Double;

   @Native("java", "java.lang.Math.hypot(#1,#2)")
   @Native("c++", "x10aux::math_utils::hypot(#1,#2)")
   public static native def hypot(a:Double, b:Double):Double;

   @Native("java", "java.lang.Math.log(#1)")
   @Native("c++", "x10aux::math_utils::log(#1)")
   public static native def log(a:Double):Double;

   // GPUs don't like doubles
   @Native("java", "(float)java.lang.Math.log(#1)")
   @Native("c++", "(x10_float)x10aux::math_utils::log(#1)")
   @Native("cuda", "__logf(#1)")
   public static native def log(a:Float):Float;

    /**
     * Returns the principal value of the natural logarithm of <code>z</code>.
     * The branch cut is on the real line at (-inf, 0]
     * @return the natural logarithm of <code>a</code>
     * @see http://mathworld.wolfram.com/NaturalLogarithm.html
     */
    public static safe def log(a:Complex):Complex {
        if (a.isNaN()) {
            return Complex.NaN;
        }
        return Complex(Math.log(a.abs()), Math.atan2(a.im, a.re));
    }

   @Native("java", "java.lang.Math.log10(#1)")
   @Native("c++", "x10aux::math_utils::log10(#1)")
   public static native def log10(a:Double):Double;

   @Native("java", "java.lang.Math.log1p(#1)")
   @Native("c++", "x10aux::math_utils::log1p(#1)")
   public static native def log1p(a:Double):Double;

   /* FIXME: since NativeRep of Int and UInt are the same, can't overload these methods with unsigned. */
   public static safe def max(a:Int, b:Int)= a<b?b:a;
   public static safe def min(a:Int, b:Int)= a<b?a:b;
// public static safe def max(a:UInt, b:UInt)= a<b?b:a;
// public static safe def min(a:UInt, b:UInt)= a<b?a:b;
   public static safe def max(a:Long, b:Long)= a<b?b:a;
   public static safe def min(a:Long, b:Long)= a<b?a:b;
// public static safe def max(a:ULong, b:ULong)= a<b?b:a;
// public static safe def min(a:ULong, b:ULong)= a<b?a:b;
   public static safe def max(a:Float, b:Float)= a<b?b:a;
   public static safe def min(a:Float, b:Float)= a<b?a:b;
   public static safe def max(a:Double, b:Double)= a<b?b:a;
   public static safe def min(a:Double, b:Double)= a<b?a:b;

    /**
     * @return the value of a with the sign of b
     */
    @Native("java", "java.lang.Math.signum(#1) == java.lang.Math.signum(#2) ? #1 : -1 * #1")
    @Native("c++", "x10aux::math_utils::copysign(#1,#2)")
    public static native def copySign(a:Double, b:Double):Double;

   public static safe def nextPowerOf2(val p: int): int {
        if (p==0) return 0;
        var pow2: int = 1;
        while (pow2 < p)
            pow2 <<= 1;
        return pow2;
    }
    
    public static safe def powerOf2(p:int) {
       return (p & -p) == p;
    }
    public static safe def log2(var p:Int):Int {
        assert powerOf2(p);
        var i:Int = 0;
        while (p > 1) { p = p/2; i++; }
        return i;
    }
    // returns 2^(max(0,i))
    public static safe def pow2(i:Int) {
        return 1 << i;
    }
}
