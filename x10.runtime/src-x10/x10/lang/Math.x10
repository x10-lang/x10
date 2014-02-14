/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 *  (C) Copyright Australian National University 2009-2010.
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;

@NativeCPPInclude("x10/lang/MathNatives.h")
public final class Math {
   public static E = 2.718281828459045D;
   public static PI = 3.141592653589793D;

   @Native("java", "java.lang.Math.abs(#a)") // @Native for performance
   @Native("c++", "((x10_int)::labs(#a))") // @Native for performance
   public static def abs(a:Int):Int = a<0n ? -a : a;

   @Native("java", "java.lang.Math.abs(#a)") // @Native for performance
   @Native("c++", "((x10_long)::llabs(#a))") // @Native for performance
   public static def abs(a:Long):Long = a<0 ? -a : a;

   @Native("java", "java.lang.Math.abs(#a)") // @Native for performance
   @Native("cuda", "fabsf(#a)")
   @Native("c++", "::fabsf(#a)") // @Native for performance
   public static def abs(a:Float):Float = a<0.0f ? -a : a;

   @Native("java", "java.lang.Math.abs(#a)") // @Native for performance
   @Native("cuda", "fabs(#a)")
   @Native("c++", "::fabs(#a)") // @Native for performance
   public static def abs(a:Double):Double = a<0.0 ? -a : a;

   @Native("java", "java.lang.Math.ceil(#a)")
   @Native("c++", "::x10::lang::MathNatives::ceil(#a)")
   public static native def ceil(a:Double):Double;

   @Native("java", "java.lang.Math.floor(#a)")
   @Native("c++", "::x10::lang::MathNatives::floor(#a)")
   public static native def floor(a:Double):Double;

   @Native("java", "java.lang.Math.round(#a)")
   @Native("c++", "::x10::lang::MathNatives::round(#a)")
   public static native def round(a:Double):Double;

   @Native("java", "java.lang.Math.getExponent(#a)")
   @Native("c++", "::ilogbf(#a)")
   public static native def getExponent(a:Float):Int;

   @Native("java", "java.lang.Math.getExponent(#a)")
   @Native("c++", "::ilogb(#a)")
   public static native def getExponent(a:Double):Int;

   @Native("java", "(float)java.lang.Math.pow(#a, #b)")
   @Native("c++", "::powf(#a,#b)")
   public static native def powf(a:Float, b:Float):Float;

   @Native("java", "java.lang.Math.pow(#a, #b)")
   @Native("c++", "::x10::lang::MathNatives::pow(#a,#b)")
   public static native def pow(a:Double, b:Double):Double;

   /**
    * Returns the principal value of the complex power <code>a^b</code>.
    * The branch cuts are on the real line at (-inf, 0) for real(b) <= 0,
    * and (-inf, 0] for real(b) > 0.   pow(0,0) is not defined.
    * @return a raised to the power <code>b</code>
    * @see http://mathworld.wolfram.com/Power.html
    */
   @Native("c++", "::x10::lang::MathNatives::pow(#a, #b)")
   public static def pow(a:Complex, b:Complex):Complex = Math.exp(Math.log(a) * b);

   @Native("java", "java.lang.Math.exp(#a)")
   @Native("c++", "::x10::lang::MathNatives::exp(#a)")
   @Native("cuda", "exp(#a)")
   public static native def exp(a:Double):Double;

   // GPUs don't like doubles
   @Native("java", "(float)java.lang.Math.exp(#a)")
   @Native("c++", "(x10_float)::x10::lang::MathNatives::exp(#a)")
   @Native("cuda", "__expf(#a)")
   public static native def expf(a:Float):Float;

    /**
     * @return the exponential function <code>e^a</code>
     * @see http://mathworld.wolfram.com/ExponentialFunction.html
     */
    @Native("c++", "::x10::lang::MathNatives::exp(#a)")
    public static def exp(a:Complex):Complex {
       if (a.isNaN()) {
           return Complex.NaN;
       }
       val expRe = Math.exp(a.re);
       return Complex(expRe * Math.cos(a.im), expRe * Math.sin(a.im));
    }

   @Native("java", "java.lang.Math.expm1(#a)")
   @Native("c++", "::x10::lang::MathNatives::expm1(#a)")
   public static native def expm1(a:Double):Double;

   @Native("java", "java.lang.Math.cos(#a)")
   @Native("c++", "::x10::lang::MathNatives::cos(#a)")
   public static native def cos(a:Double):Double;

    /**
     * @return the cosine of <code>z</code>
     * @see http://mathworld.wolfram.com/Cosine.html
     */
    @Native("c++", "::x10::lang::MathNatives::cos(#z)")
    public static def cos(z:Complex):Complex {
        if (z.im == 0.0) {
            return Complex(Math.cos(z.re), 0.0);
        } else {
            return Complex(Math.cos(z.re) * Math.cosh(z.im), Math.sin(z.re) * Math.sinh(-(z.im)));
        }
    }

   @Native("java", "java.lang.Math.sin(#a)")
   @Native("c++", "::x10::lang::MathNatives::sin(#a)")
   public static native def sin(a:Double):Double;

    /**
     * @return the sine of <code>z</code>
     * @see http://mathworld.wolfram.com/Sine.html
     */
    @Native("c++", "::x10::lang::MathNatives::sin(#z)")
    public static def sin(z:Complex):Complex {
        if (z.im == 0.0) {
            return Complex(Math.sin(z.re), 0.0);
        } else {
            return Complex(Math.sin(z.re) * Math.cosh(z.im), Math.cos(z.re) * Math.sinh(z.im));
        }
    }

   @Native("java", "java.lang.Math.tan(#a)")
   @Native("c++", "::x10::lang::MathNatives::tan(#a)")
   public static native def tan(a:Double):Double;

    /**
     * @return the tangent of <code>z</code>
     * @see http://mathworld.wolfram.com/Tangent.html
     */
    @Native("c++", "::x10::lang::MathNatives::tan(#z)")
    public static def tan(z:Complex):Complex {
        if (z.im == 0.0) {
            return Complex(Math.tan(z.re), 0.0);
        } else {
            // tan(z) = e^2iz - 1 / i (e^21z + 1)
            val e2IZ = Math.exp(2.0 * Complex.I * z);
            return (e2IZ - 1.0) / (Complex.I * (e2IZ + 1.0));
        }
    }

   @Native("java", "java.lang.Math.acos(#a)")
   @Native("c++", "::x10::lang::MathNatives::acos(#a)")
   public static native def acos(a:Double):Double;

    /**
     * Returns the principal value of the inverse cosine of <code>z</code>.
     * The branch cuts are on the real line at (-inf, -1) and (1, +inf)
     * The real part of the inverse cosine ranges from 0 to PI.
     * @return the inverse cosine of <code>z</code>
     * @see http://mathworld.wolfram.com/InverseCosine.html
     */
    // @Native("c++", "::x10::lang::MathNatives::acos(#z)") /* C++11 */
    public static def acos(z:Complex):Complex {
        if (z.im == 0.0 && Math.abs(z.re) <= 1.0) {
            return Complex(Math.acos(z.re), 0.0);
        } else {
            // acos(z) = pi / 2.0 + i log(iz + sqrt(1 - z^2))
            return Math.PI / 2.0 + Complex.I * Math.log(Complex.I * z + Math.sqrt(1.0 - z * z));
        }
    }

   @Native("java", "java.lang.Math.asin(#a)")
   @Native("c++", "::x10::lang::MathNatives::asin(#a)")
   public static native def asin(a:Double):Double;

    /**
     * Returns the principal value of the inverse sine of <code>z</code>.
     * The branch cuts are on the real line at (-inf, -1) and (1, +inf)
     * The real part of the inverse sine ranges from -PI/2 to +PI/2.
     * @return the inverse sine of <code>z</code>
     * @see http://mathworld.wolfram.com/InverseSine.html
     */
    // @Native("c++", "::x10::lang::MathNatives::asin(#z)") / * C++11 */
    public static def asin(z:Complex):Complex {
        if (z.im == 0.0 && Math.abs(z.re) <= 1.0) {
            return Complex(Math.asin(z.re), 0.0);
        } else {
            // asin(z) = -i * log(iz + sqrt(1 - z^2))
            return -Complex.I * Math.log(Complex.I * z + Math.sqrt(1.0 - z * z));
        }
    }

   @Native("java", "java.lang.Math.atan(#a)")
   @Native("c++", "::x10::lang::MathNatives::atan(#a)")
   public static native def atan(a:Double):Double;

    /**
     * Returns the principal value of the inverse tangent of <code>z</code>.
     * The branch cuts are on the imaginary line at (-i*inf, -i] and [i, i*inf)
     * The real part of the inverse tangent ranges from -PI/2 to +PI/2.
     * @return the principal value of the inverse tangent of <code>z</code>
     * @see http://mathworld.wolfram.com/InverseTangent.html
     */
    // @Native("c++", "::x10::lang::MathNatices::atan(#z)") /* C++11 */
    public static def atan(z:Complex):Complex {
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

   @Native("java", "java.lang.Math.atan2(#a,#b)")
   @Native("c++", "::x10::lang::MathNatives::atan2(#a,#b)")
   public static native def atan2(a:Double, b:Double):Double;

   @Native("java", "java.lang.Math.cosh(#a)")
   @Native("c++", "::x10::lang::MathNatives::cosh(#a)")
   public static native def cosh(a:Double):Double;

    /**
     * @return the hyperbolic cosine of <code>z</code>
     * @see http://mathworld.wolfram.com/HyperbolicCosine.html
     */
    @Native("c++", "::x10::lang::MathNatives::cosh(#z)")
    public static def cosh(z:Complex):Complex {
        if (z.isNaN()) {
            return Complex.NaN;
        } else if (z.im == 0.0) {
            return Complex(Math.cosh(z.re), 0.0);
        } else {
            return Complex(Math.cosh(z.re) * Math.cos(z.im), Math.sinh(z.re) * Math.sin(z.im));
        }
    }

   @Native("java", "java.lang.Math.sinh(#a)")
   @Native("c++", "::x10::lang::MathNatives::sinh(#a)")
   public static native def sinh(a:Double):Double;

    /**
     * @return the hyperbolic sine of <code>z</code>
     * @see http://mathworld.wolfram.com/HyperbolicSine.html
     */
    @Native("c++", "::x10::lang::MathNatives::sinh(#z)")
    public static def sinh(z:Complex):Complex {
        if (z.isNaN()) {
            return Complex.NaN;
        } else if (z.im == 0.0) {
            return Complex(Math.sinh(z.re), 0.0);
        } else {
            return Complex(Math.sinh(z.re) * Math.cos(z.im), Math.cosh(z.re) * Math.sin(z.im));
        }
    }

   @Native("java", "java.lang.Math.tanh(#a)")
   @Native("c++", "::x10::lang::MathNatives::tanh(#a)")
   public static native def tanh(a:Double):Double;

    /**
     * @return the hyperbolic tangent of <code>z</code>
     * @see http://mathworld.wolfram.com/HyperbolicTangent.html
     */
    @Native("c++", "::x10::lang::MathNatives::tanh(#z)")
    public static def tanh(z:Complex):Complex {
        if (z.isNaN()) {
            return Complex.NaN;
        }
        val d = Math.cosh(2.0 * z.re) + Math.cos(2.0 * z.im);
        return Complex(Math.sinh(2.0 * z.re)/d, Math.sin(2.0 * z.im)/d);
    }

   @Native("java", "java.lang.Math.sqrt(#a)")
   @Native("c++", "::x10::lang::MathNatives::sqrt(#a)")
   @Native("cuda", "sqrt(#a)")
   public static native def sqrt(a:Double):Double;

    /**
     * Returns the principal value of the square root of <code>z</code>.
     * The branch cut is on the real line at (-inf, 0)
     * @return the principal square root of <code>z</code>
     * @see http://mathworld.wolfram.com/SquareRoot.html
     */
    public static def sqrt(z:Complex):Complex {
        if (z.isNaN()) {
            return Complex.NaN;
        } else if (z == Complex.ZERO) {
            return Complex.ZERO;
        } else {
            val t = Math.sqrt((Math.abs(z.re) + (z.abs())) / 2.0);
            if (z.re >= 0.0) {
                return Complex(t, z.im / (2.0 * t));
            } else {
                return Complex(Math.abs(z.im) / (2.0 * t), Math.copySign(t, z.im));
            }
        }
    }

   // GPUs don't like doubles
   @Native("java", "(float)java.lang.Math.sqrt(#a)")
   @Native("c++", "(x10_float)::x10::lang::MathNatives::sqrt(#a)")
   @Native("cuda", "sqrtf(#a)")
   public static native def sqrtf(a:Float):Float;

   @Native("java", "java.lang.Math.cbrt(#a)")
   @Native("c++", "::x10::lang::MathNatives::cbrt(#a)")
   public static native def cbrt(a:Double):Double;

   @Native("java", "org.apache.commons.math3.special.Erf.erf(#a)")
   @Native("c++", "::x10::lang::MathNatives::erf(#a)")
   public static native def erf(a:Double):Double;

   @Native("java", "org.apache.commons.math3.special.Erf.erfc(#a)")
   @Native("c++", "::x10::lang::MathNatives::erfc(#a)")
   public static native def erfc(a:Double):Double;

   @Native("java", "java.lang.Math.hypot(#a,#b)")
   @Native("c++", "::x10::lang::MathNatives::hypot(#a,#b)")
   public static native def hypot(a:Double, b:Double):Double;

   @Native("java", "java.lang.Math.log(#a)")
   @Native("c++", "::x10::lang::MathNatives::log(#a)")
   @Native("cuda", "log(#a)")
   public static native def log(a:Double):Double;

   // GPUs don't like doubles
   @Native("java", "(float)java.lang.Math.log(#a)")
   @Native("c++", "(x10_float)::x10::lang::MathNatives::log(#a)")
   @Native("cuda", "__logf(#a)")
   public static native def logf(a:Float):Float;

    /**
     * Returns the principal value of the natural logarithm of <code>z</code>.
     * The branch cut is on the real line at (-inf, 0]
     * @return the natural logarithm of <code>a</code>
     * @see http://mathworld.wolfram.com/NaturalLogarithm.html
     */
    @Native("c++", "::x10::lang::MathNatives::log(#a)")
    public static def log(a:Complex):Complex {
        if (a.isNaN()) {
            return Complex.NaN;
        }
        return Complex(Math.log(a.abs()), Math.atan2(a.im, a.re));
    }

   @Native("java", "java.lang.Math.log10(#a)")
   @Native("c++", "::x10::lang::MathNatives::log10(#a)")
   public static native def log10(a:Double):Double;

   @Native("java", "java.lang.Math.log1p(#a)")
   @Native("c++", "::x10::lang::MathNatives::log1p(#a)")
   public static native def log1p(a:Double):Double;

    @Native("java", "java.lang.Math.max(#a,#b)") // @Native for performance
    public static def max(a:Int, b:Int):Int = a<b?b:a;
    @Native("java", "java.lang.Math.min(#a,#b)") // @Native for performance
    public static def min(a:Int, b:Int):Int = a<b?a:b;
    public static def max(a:UInt, b:UInt):UInt = a<b?b:a;
    public static def min(a:UInt, b:UInt):UInt = a<b?a:b;
    @Native("java", "java.lang.Math.max(#a,#b)") // @Native for performance
    public static def max(a:Long, b:Long):Long = a<b?b:a;
    @Native("java", "java.lang.Math.min(#a,#b)") // @Native for performance
    public static def min(a:Long, b:Long):Long = a<b?a:b;
    public static def max(a:ULong, b:ULong):ULong = a<b?b:a;
    public static def min(a:ULong, b:ULong):ULong = a<b?a:b;
    @Native("java", "java.lang.Math.max(#a,#b)") // @Native for performance
    public static def max(a:Float, b:Float):Float = a<b?b:a;
    @Native("java", "java.lang.Math.min(#a,#b)") // @Native for performance
    public static def min(a:Float, b:Float):Float = a<b?a:b;
    @Native("java", "java.lang.Math.max(#a,#b)") // @Native for performance
    public static def max(a:Double, b:Double):Double = a<b?b:a;
    @Native("java", "java.lang.Math.min(#a,#b)") // @Native for performance
    public static def min(a:Double, b:Double):Double = a<b?a:b;

    public static def signum(a:Int):Int = (a == 0n) ? 0n : ((a>0n) ? 1n : -1n);
    public static def signum(a:Long):Long = (a == 0) ? 0 : ((a>0) ? 1 : -1);
    @Native("java", "java.lang.Math.signum(#a)") // @Native for performance
    public static def signum(a:Float):Float = (a == 0.0f) ? 0.0f : ((a>0.0f) ? 1.0f : -1.0f);
    @Native("java", "java.lang.Math.signum(#a)") // @Native for performance
    public static def signum(a:Double):Double = (a == 0.0d) ? 0.0d : ((a>0.0d) ? 1.0d : -1.0d);

    /**
     * @return the value of a with the sign of b
     */
    @Native("java", "java.lang.Math.copySign(#a,#b)")
    @Native("c++", "::x10::lang::MathNatives::copysign(#a,#b)")
    public static native def copySign(a:Double, b:Double):Double;

    public static def nextPowerOf2(p:Int):Int {
        if (p==0n) return 0n;
        var pow2:Int = 1n;
        while (pow2 < p)
            pow2 <<= 1n;
        return pow2;
    }

    public static def nextPowerOf2(p:Long):Long {
    	if (p==0) return 0;
    	var pow2:Long = 1;
    	while (pow2 < p)
    		pow2 <<= 1n;
    	return pow2;
    }

    public static def powerOf2(p:Int):Boolean = (p & -p) == p;
    public static def powerOf2(p:Long):Boolean = (p & -p) == p;

    public static def log2(var p:Int):Int {
        assert powerOf2(p);
        var i:Int = 0n;
        while (p > 1n) { p = p/2n; i++; }
        return i;
    }

    public static def log2(var p:Long):Long {
    	assert powerOf2(p);
    	var i:Long = 0;
    	while (p > 1) { p = p/2; i++; }
    	return i;
    }

    // returns 2^(max(0,i))
    public static def pow2(i:Int):Int {
        return 1n << i;
    }

    // returns 2^(max(0,i))
    public static def pow2(i:Long):Long {
        return 1 << (i as Int);
    }
}
