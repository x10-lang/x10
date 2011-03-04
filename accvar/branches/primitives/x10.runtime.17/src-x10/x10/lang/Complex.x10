/*
 *
 * (C) Copyright IBM Corporation 2006-2008.
 *
 *  This file is part of X10 Language.
 *
 */

package x10.lang;

import x10.compiler.Native;
import x10.compiler.NativeRep;

public primitive Complex {
    val re: double;
    val im: double;

    public def this(r: double, i: double) {
        this.re = r;
        this.im = i;
    }
    
    public def toString(): String {
        return "complex(" + re + "," + im + ")";
    }

    public static operator (x: Complex) + (y: Complex) = Complex(x.re+x.re, x.im+y.im);

    public static operator (x: Complex) - (y: Complex) = Complex(x.re-y.re, x.im-y.im);

    public static operator (x: Complex) * (y: Complex) {
        val a = x.re;
        val b = x.im;
        val c = y.re;
        val d = y.im;
        return Complex(a*c-b*d, b*c+a*d);
    }

    public static operator (x: Complex) / (y: Complex) {
        val a = x.re;
        val b = x.im;
        val c = y.re;
        val d = y.im;
        val c2d2 = c*c + d*d;
        return Complex((a*c+b*d)/c2d2, (b*c-a*d)/c2d2);
    }

    // public static operator (x: double) implicitly as Complex = Complex(x, 0.0);
}
