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

 

/**
 * Tests 2D distributions constructed from regions.
 */
public class ConstructDist2D   {

    public def run(): boolean = {
        val e = 1..10;
        val r = [e, e] as Region;
        val d= Dist.makeConstant(r, here);
        return d.equals(Dist.makeConstant([1..10, 1..10] as Region, here));
    }

    public static def main(var args: Rail[String]): void = {
        new ConstructDist2D().run ();
    }
}