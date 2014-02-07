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


/**
 * @author mtake 7/2012
 */
public class GenericCast14 extends x10Test {
        
    static def f[T](a:Any):T {
        val t = a as T;
        return t;
    }

    public def run():Boolean {

        var ok:Boolean = true;

        try {
            val i = null as Any as Int;
            ok = false;
            Console.OUT.println("Error: ClassCastException should be thrown for null as Any as Int!");
        }
        catch (e:ClassCastException) {
        }
        catch (e:Exception) {
            ok = false;
            Console.OUT.println("Error: something other than ClassCastException was thrown for null as Any as Int!");
            e.printStackTrace();
        }

        try {
            val i = f[Long](null);
            ok = false;
            Console.OUT.println("Error: ClassCastException should be thrown for null as T{T <: long}!");
        }
        catch (e:ClassCastException) {
        }
        catch (e:Exception) {
            ok = false;
            Console.OUT.println("Error: something other than ClassCastException was thrown for null as T{T <: long}!");
            e.printStackTrace();
        }

        try {
        	val i = f[Any](null);
        }
        catch (e:ClassCastException) {
        	ok = false;
        	Console.OUT.println("Error: ClassCastException was thrown for null as T{T <: Any}!");
        	e.printStackTrace();
        }
        catch (e:Exception) {
        	ok = false;
        	Console.OUT.println("Error: something other than ClassCastException was thrown for null as T{T <: Any}!");
        	e.printStackTrace();
        }

        return ok;
    }

    public static def main(Rail[String]) {
        new GenericCast14().execute();
    }

}
