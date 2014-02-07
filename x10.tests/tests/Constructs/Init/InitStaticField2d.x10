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
 * Check lazy, per-field, per-place initialization semantics of static fields.
 * Valid for X10 2.2.3 and later.
 * 
 * @author mtake 7/2012
 */
public class InitStaticField2d extends x10Test {

    static val a = 1 as Any as String; // cast to String should throw CCE

    public def run():Boolean {
        var ok:Boolean = true;
        
        try {
        	val a = InitStaticField2d.a;
        	ok = false;
        	Console.OUT.println("BUG: no exception was thrown!");
        } catch (e:ExceptionInInitializer) {
        	// check ExceptionInInitializer is thrown
        	// e.printStackTrace();
        } catch (e:Exception) {
        	ok = false;
        	Console.OUT.println("BUG: something other than ExceptionInInitializer was thrown!");
        	// e.printStackTrace();
        }
        
        try {
        	val a = InitStaticField2d.a;
        	ok = false;
        	Console.OUT.println("BUG: no exception was thrown!");
        } catch (e:ExceptionInInitializer) {
        	// check ExceptionInInitializer is thrown
        	// e.printStackTrace();
        } catch (e:Exception) {
        	ok = false;
        	Console.OUT.println("BUG: something other than ExceptionInInitializer was thrown!");
        	// e.printStackTrace();
        }
        
        return ok;
    }

    public static def main(Rail[String]) {
        new InitStaticField2d().execute();
    }

}
