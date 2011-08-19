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
/**
 * This is to test the collecting finish sum 
 * with at each 
 * @author Li Yan
 * 
 */


public class CF6 extends x10Test{

    public static class TotalsReducer1 implements Reducible[Totals2] {
        public  def zero() = Totals2(0,0);
        public  operator this(a:Totals2, other:Totals2 ) = Totals2(a.left+other.left,a.right+other.right);
        public def this() {super();};
    }
    public static struct Totals2(left:int, right:int) {
        public def this (l:int, r:int)  {property(l,r);}
        public def this (){this(0,0);}
        public def toString() = "(" + left  + "," + right + ")";
    }

    public def run():Boolean {
            val b = new TotalsReducer1();
            val iteration = 100;
            val result = finish(b) {
                 ateach(p in Dist.makeUnique()){
                      val v = Totals2(1,2);
                      for (var i:Int = 0; i < iteration; i++)
                        offer v;
                 }
            };
            Console.OUT.println("result =" + result + " P=" + Place.MAX_PLACES);
            return result == Totals2(iteration * Place.MAX_PLACES,iteration * 2 *Place.MAX_PLACES);
    }
        public static def main(args: Array[String](1)) {
                new CF6().execute();
        }

}

