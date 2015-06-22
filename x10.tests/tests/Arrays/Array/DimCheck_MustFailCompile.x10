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

//LIMITATION:
//This check is not being done by the compiler currently.
import harness.x10Test;
import x10.regionarray.*;

/**
 * Dimensionality of array initializer must be checked.
 *
 * @author vj 12 2006
 */

public class DimCheck_MustFailCompile extends x10Test {

    public def run(): boolean {
        // The compiler should check the type of the distribution in the array constructor.
        // If  the type does not specify a constraint on the arity of the underlying region
        // then the initializer cannot specify the arity of the point. Otherwise the arity of the
        // point must be the same as the arity of the distribution.
        var a1: DistArray[int] = DistArray.make[int](Dist.makeConstant(Region.make(0..2, 0..3), here), ([i]:Point(3))=> (i as int)); // ERR ERR ([Semantic Error: The rank of the exploded Point is 3 but it should be 1, Semantic Error: Method make[T](dist: x10.regionarray.Dist, init: (a1:x10.lang.Point{self.x10.lang.Point#rank==dist.x10.regionarray.Dist#region.x10.regionarray.Region#rank})=> T): x10.regionarray.DistArray[T]{self.x10.regionarray.DistArray#dist==dist, self!=null} in x10.regionarray.DistArray cannot be called with arguments [x10.lang.Int](x10.regionarray.Dist{self.x10.regionarray.Dist#region.x10.regionarray.Region#rank==2, self.x10.regionarray.Dist#region.x10.regionarray.Region#rect==true, self.x10.regionarray.Dist#region.x10.regionarray.Region#zeroBased==true}, <anonymous class>);    Invalid Parameter.)
        var a2: DistArray[int] = DistArray.make[int](Dist.makeConstant(Region.make(0..2, 0..3), here), ([i]:Point)=> (i as int)); // ERR
        var a3: DistArray[int] = DistArray.make[int](Dist.makeConstant(Region.make(0..2, 0..3), here), ([i]:Point(2))=> (i as int)); // ERR: Semantic Error: The rank of the exploded Point is 2 but it should be 1
        x10.io.Console.OUT.println(a1);
        return true;
    }

    public static def main(var args: Rail[String]): void {
        new DimCheck_MustFailCompile().execute();
    }
}
