/*
 *
 * (C) Copyright IBM Corporation 2006-2009.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

/**
 * This class represents a complex number (a + b*i).
 */
public struct Complex {
    public val re:Double;
    public val im:Double;

    public const ZERO = Complex(0.0, 0.0);
    public const ONE = Complex(1.0, 0.0);
    public const I = Complex(0.0, 1.0);
    public const INF = Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    public const NaN = Complex(Double.NaN, Double.NaN);

   /**
    * Construct a complex number with the specified 
    * real and imaginary components
    *
    * @real the real component of the Complex number
    * @imaginay the imaginary component of the Complex number
    * @return the Complex number representing (real + imaginary*i)
    */
    public def this(real:Double, imaginary:Double) {
        this.re = real;
        this.im = imaginary;
    }
    
    /**
     * @return the sum of this complex number and the given complex number
     */
    public operator this + (that:Complex):Complex {
        return Complex(re + that.re, im + that.im);
    }

    /**
     * @return the sum of this complex number and the given Double
     */
    public operator this + (that:Double):Complex {
        return Complex(re + that, im);
    }

    /**
     * @return the difference between this complex number and the given complex number
     */
    public operator this - (that:Complex):Complex {
        return Complex(re - that.re, im - that.im);
    }

    /**
     * @return the difference between this complex number and the given Double
     */
    public operator this - (that:Double):Complex {
        return Complex(re - that, im);
    }
    
    /**
     * @return the product of this complex number and the given complex number
     */
    public operator this * (that:Complex):Complex {
        return Complex(re * that.re - im * that.im, 
                       re * that.im + im * that.re);
    }

    /**
     * @return the product of this complex number and the given Double
     */
    public operator this * (that:Double):Complex {
        return Complex(re * that, im * that);
    }

    /**
     * Gets the quotient of this complex number and the given complex number.
     * Uses Smith's algorithm <a href="http://doi.acm.org/10.1145/368637.368661"/>
     * TODO: consider using Priest's algorithm <a href="http://doi.acm.org/10.1145/1039813.1039814"/>
     * @return the quotient of this complex number and the given complex number
     */
    public operator this / (that:Complex):Complex {
        if (isNaN() || that.isNaN()) {
            return Complex.NaN;
        }

        val c:Double = that.re;
        val d:Double = that.im;
        if (c == 0.0 && d == 0.0) {
            return Complex.NaN;
        }
        
        if (that.isInfinite() && !isInfinite()) {
            return Complex.ZERO;
        }
              
        if (Math.abs(d) <= Math.abs(c)) {
            if (c == 0.0) {
                return Complex(im/d, -re/c);
            }
            val r:Double =  d / c;
            val denominator:Double = c + d * r;
            return Complex((re + im * r) / denominator,
                           (im - re * r) / denominator);
        } else {
            if (d == 0.0) {
                return Complex(re/c, im/c);
            }
            val r:Double = c / d;
            val denominator:Double = c * r + d;
            return Complex((re * r + im) / denominator,
                           (im * r - re) / denominator);
        }
    }

    /**
     * Gets the quotient of this complex number and the given Double.
     */
    public operator this / (that:Double):Complex {
        return Complex(re / that, im / that);
    }

    /** 
     * @return the conjugate of this complex number
     */
    public def conjugate():Complex {
        if (isNaN()) {
            return Complex.NaN;
        }   
        return Complex(re, -im);
    }

    /**
     * @return the negation of this complex number
     */
    public operator - this:Complex  = isNaN() ? Complex.NaN : Complex(-re, -im);

    /**
     * Return the absolute value of this complex number.
     * <p>
     * Returns <code>NaN</code> if either re or im part is
     * <code>NaN</code> and <code>Double.POSITIVE_INFINITY</code> if
     * neither part is <code>NaN</code>, but at least one part takes an infinite
     * value.
     *
     * @return the absolute value
     */
    public def abs():Double {
        if (isNaN()) {
            return Double.NaN;
        }
        
        if (isInfinite()) {
            return Double.POSITIVE_INFINITY;
        }

        if (im == 0.0) {
            return Math.abs(re);
        } else if (re == 0.0) {
            return Math.abs(im);
        } else {
            // use hypot to avoid unnecessary under/overflow
            return Math.hypot(re, im);
        }
    }

    /**
     * @return true if either part of this complex number is NaN
     */
    public def isNaN():boolean {
        return re.isNaN() || im.isNaN();        
    }

    /**
     * @return true if either part of this complex number is infinite
     * and neither part is <code>NaN</code>
     */
    public def isInfinite():boolean {
        return !isNaN() && 
        (re.isInfinite() || im.isInfinite());
    }

    public global safe def toString():String {
        return (re + " + " + im + "i");
    }

}
