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

package x10.regionarray;

import x10.util.ArrayList;
import x10.io.Printer;


/**
 * Additional builder utility functions for Mats destined to become
 * PolyMats.
 */
class PolyMatBuilder(rank:Long) extends MatBuilder {

    // XTENLANG-49


    /**
     * Create a new empty builder.
     */

    public def this(rank:Long): PolyMatBuilder{self.rank==rank} {
        super((rank+1) as Int);
        property(rank);
    }


    /**
     * Get the result.
     */

    public def toSortedPolyMat(isSimplified:Boolean): PolyMat(rank) {
        mat.sort((x:Row,y:Row)=>PolyRow.compare(x,y));
        val result = new PolyMat(mat.size() as Int, (rank+1) as Int, (i:Int,j:Int)=>mat(i)(j), isSimplified);
        return result as PolyMat(rank); // XXXX
    }


    /**
     * a simple mechanism of somewhat dubious utility to allow
     * semi-symbolic specification of halfspaces. For example
     * X0-Y1 >= n is specified as add(X(0)-Y(1), GE, n)
     *
     * XXX coefficients must be -1,0,+1; can allow larger coefficients
     * by increasing # bits per coeff
     */

    private static ZERO:Int = 0xAAAAAAAn;

    public static GE:Int = 0n;
    public static LE:Int = 1n;

    final public static def X(axis:Int):Int {
        return 0x1n<<2n*axis;
    }

    public def add(var coeff:Int, op:Int, k:Int): void {
        coeff += ZERO;
        val as_ = new Array[Int](rank+1);
        for (var i:Int = 0n; i<rank; i++) {
            val a = (coeff&3n) - 2n;
            as_(i) = op==LE? a : - a;
            coeff = coeff >> 2n;
        }
        as_(rank) = op==LE? -k : k;
        add((i:Int) => as_(i));
    }
}
type PolyMatBuilder(rank:Long) = PolyMatBuilder{self.rank==rank};