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
 * Test the shorthand syntax for object array initializer.
 *
 * @author igor, 12/2005
 */

public class ObjectArrayInitializerShorthand extends x10Test {

    public def run(): boolean {
        val d  = Dist.makeConstant(Region.make(1..10, 1..10), here);
        val dr = GlobalRef[Dist](d);
        // DistArray could be copying when it initializes. So never use == unless
        // the object is global.
        val ia = DistArray.make[GlobalRef[Dist]](d, (Point) => dr);
        for ([i,j] in ia.region) chk(ia(i, j)==dr);
        return true;
    }

    public static def main(Rail[String]){
        new ObjectArrayInitializerShorthand().execute();
    }
}
