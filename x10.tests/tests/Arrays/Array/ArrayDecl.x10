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

import harness.x10Test;
import x10.regionarray.*;

/**
 * Testing miscellaneous array declarations and initializations.
 *
 * @author kemal 4/2005
 */

public class ArrayDecl extends x10Test {

    public static N: int = 24n;

    public def run(): boolean = {

        val ia0 = DistArray.make[int](Dist.makeConstant(Region.make(0, N-1), here));
        val p: Place = here;

        chk(ia0.dist.equals(Dist.makeConstant(Region.make(0, N-1), p)));

        finish ateach (val [i]: Point in ia0.dist) chk(ia0(i) == 0n);

        val v_ia2: DistArray[int](1) = DistArray.make[int](Dist.makeConstant(Region.make(0, N-1), here), ([i]: Point)=>(i as int));
        chk(v_ia2.dist.equals(Dist.makeConstant(Region.make(0, N-1), here)));
        for (val [i]: Point in v_ia2.region) chk(v_ia2(i) == (i as int));

        val w = Place.places();
        val ia2: DistArray[byte](1) = DistArray.make[byte](Dist.makeConstant(Region.make(0, N-1), w.prev(w.prev(here))), (Point)=> (0 as byte));
        chk(ia2.dist.equals(Dist.makeConstant(Region.make(0, N-1), w.prev(w.prev(here)))));
        finish ateach ([i]: Point in ia2.dist) chk(ia2(i) == (0 as byte));

        //Examples similar to section 10.3 of X10 reference manual

        val data1: DistArray[double](1) = DistArray.make[double](Dist.makeConstant(Region.make(0, 16), here), ([i]:Point)=> i as  Double);
        chk(data1.dist.equals(Dist.makeConstant(Region.make(0, 16), here)));
        for (val [i]: Point in data1.region) chk(data1(i) == (i as Double));

        val myStr: String = "abcdefghijklmnop";
        val data2 = DistArray.make[char](Dist.makeConstant(Region.make(1..2,1..3), here), ([i,j]: Point)=> myStr.charAt((i*j) as int));
        chk(data2.dist.equals(Dist.makeConstant(Region.make(1..2,1..3), here)));
        for (val [i,j]: Point in data2.region) chk(data2(i, j) == myStr.charAt((i*j) as int));

        // is a region R converted to R->here in a dist context?
        //final long[.] data3 = new long[1:11]
        val data3: DistArray[long](1) = DistArray.make[long](Dist.makeConstant(Region.make(1,11), here), ([i] : Point)=> i*i);
        chk(data3.dist.equals(Dist.makeConstant(Region.make(1, 11), here)));
        for (val [i]: Point in data3.region) chk(data3(i) == (i*i));

        val D: Dist{rank==1} = Dist.makeBlock(Region.make(0,9), 0);
        val d = DistArray.make[float](D, ([i]:Point) => ((10.0*i) as Float));
        chk(d.dist.equals(D));
        finish ateach (val [i]: Point in D) chk(d(i) == ((10.0*i) as Float));

        val E = Dist.makeBlock(Region.make(1..7, 0..1), 1);
        val result1  = DistArray.make[Short](E, ([i,j]: Point) => ((i+j) as Short));
        chk(result1.dist.equals(E));
        finish ateach (val [i,j]: Point in E) chk(result1(i, j) == ((i+j) as Short));

        val result2 = DistArray.make[Complex](Dist.makeConstant(Region.make(0, N-1), here), ([i] : Point) =>  Complex(i*N,-i));
        chk(result2.dist.equals(Dist.makeConstant(Region.make(0, N-1), here)));
        finish ateach (val [i]: Point in result2.dist) chk(result2(i)==(Complex(i*N,-i)));

        return true;
    }

    public static def main(Rail[String]):void = {
        new ArrayDecl().execute();
    }
}
