/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

//LIMITATION:
//This check is not being done by the compiler currently.
import harness.x10Test;
import x10.array.*;

/**
 * Dimensionality of array initializer must be checked.
 *
 * @author vj 12 2006
 */

public class DimCheck_MustFailCompile extends x10Test {

    public def run(): boolean = {
        // The compiler should check the type of the distribution in the array constructor.
        // If  the type does not specify a constraint on the arity of the underlying region
        // then the initializer cannot specify the arity of the point. Otherwise the arity of the
        // point must be the same as the arity of the distribution.
        var a1: DistArray[int] = DistArray.make[int](Dist.makeConstant((0..2)*(0..3), here), ([i]:Point(3))=> i); // ERR ERR ([Semantic Error: The rank of the exploded Point is 3 but it should be 1, Semantic Error: Method make[T](dist: x10.array.Dist, init: (a1:x10.array.Point{self.x10.array.Point#rank==dist.x10.array.Dist#region.x10.array.Region#rank})=> T): x10.array.DistArray[T]{self.x10.array.DistArray#dist==dist, self!=null} in x10.array.DistArray cannot be called with arguments [x10.lang.Int](x10.array.Dist{self.x10.array.Dist#region.x10.array.Region#rank==2, self.x10.array.Dist#region.x10.array.Region#rect==true, self.x10.array.Dist#region.x10.array.Region#zeroBased==true}, <anonymous class>);    Invalid Parameter.)
        var a2: DistArray[int] = DistArray.make[int](Dist.makeConstant((0..2)*(0..3), here), ([i]:Point)=> i); // ERR
        var a3: DistArray[int] = DistArray.make[int](Dist.makeConstant((0..2)*(0..3), here), ([i]:Point(2))=> i); // ERR: Semantic Error: The rank of the exploded Point is 2 but it should be 1
        x10.io.Console.OUT.println(a1);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new DimCheck_MustFailCompile().execute();
    }
}
