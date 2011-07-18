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

import harness.x10Test;

import x10.compiler.tests.*; // err markers
import x10.util.*;

class XTENLANG_2638 extends x10Test
{
    public def run () : Boolean
    {
        val mShape = ((1..100) * (1..200)) as Region(2);
        val mDist:Dist(2) = Dist.makeBlock(mShape);
        val mat = DistArray.make[int] (mDist, 1);
        val rhs = DistArray.make[int] (mDist, 2);

        finish for (place in mat.dist.places()) async
        {
            at (place)
            {
                for (pt in mat | here)//(mat.dist | here))
                {
                    val x = mat(pt);
                    val y = rhs(pt);
                    mat(pt) = x + y;
                }
            }
        }
        return true;
    }

    public static def main (arg:Array[String](1)) : void
    {
        new XTENLANG_2638().execute();
    }
}

