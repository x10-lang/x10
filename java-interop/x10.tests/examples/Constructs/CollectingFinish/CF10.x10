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
 * This is to test the collecting finish 
 * should cast the offer parameter
 * @author Li Yan
 * 
 */


public class CF10 extends x10Test{

    static class Blig implements Reducible[Object] {
        public def zero() : Object {
           return "";
        }
        public operator this(a:Object, b:Object):Object {
           return "[" + a + "," + b + "]";
        }
    }

    public def run():Boolean {
        val vtwoa  = finish(new Blig()){
           offer "Hello" ;
           offer "CF" ;
        };

            var ret : Boolean = false ;
            val resultS : String = vtwoa as String;
            if(vtwoa.equals("[,[[,Hello],CF]]")) ret = true;
            return ret;
 
    }
        public static def main(args: Array[String](1)) {
                new CF10().execute();
        }

}

