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

import harness.x10Test;
import x10.regionarray.*;

public class ReferenceConsistencyCheck(R: Region{rank==2, zeroBased, rect}, 
        D:Dist{region==this.R},
        A: DistArray[double]{dist==this.D}) extends x10Test {
    
    public def this(R: Region{rank==2&&zeroBased&&rect}, 
            D: Dist{region==R}, 
            A: DistArray[double]{dist==D}): 
            ReferenceConsistencyCheck{self.R==R, self.D==D, self.A==A}
             {
        property(R,D,A);
    }
    
    /** Update the submatrix A[k:m-1, k:n-1]*/
    def update(k:int):void {
        finish ateach (val [i,j] in D) A(i,j)++;
    }

    public def run()=true; 

    public static def main(Rail[String]) = {
//        set up a test problem
        val size=10;
        val R:Region{rank==2&&zeroBased&&rect} = Region.make(0..(size-1), 0..(size-1));
        val D:Dist{region==R} = Dist.makeBlock(R);
        val A:DistArray[double]{dist==D} = 
               DistArray.make[double](D, ([i,j]:Point) => 
                  { var res:long = i%2;
                    if (i-1==j) res=i*(res==0L?-1L:1L);
                    res as double
                  });
            
        new ReferenceConsistencyCheck(R, D, A).execute();
    }
}
