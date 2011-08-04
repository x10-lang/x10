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
        public global safe def zero() = new Totals2(0,0);
        public global safe def apply(a:Totals2 ,other:Totals2 ) = new Totals2(a.left+other.left,a.right+other.right); 
        public def this() {super();};
    }
    public static class Totals2 {
        public global val left :Int;
        public global val right : Int;
        public def this (l:Int, r:Int)  {left = l; right = r;}
        public def this (){left =0; right =0;}
    }

    public def run():Boolean {
            val b = new TotalsReducer1();
            val iteration = 100;
            val result = finish(b) {
            ateach(p in Dist.makeUnique()){
                      var case_ :Totals2 =new  Totals2(1,2);
                      for (var i:Int = 0; i < iteration; i++)
                      offer case_;
             }
            };
            var ret : Boolean = false ;
            if ((result.left == iteration * Place.MAX_PLACES) &&(result.right == iteration * 2 *Place.MAX_PLACES ))
                ret = true;
            return ret;
 
    }
        public static def main(args: Rail[String]) {
                new CF6().execute();
        }

}
