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

public class CharSequenceTest extends x10Test {

    class MyCharSequence(s:CharSequence) implements CharSequence {
        public def subSequence(fromIndex:Int, toIndex:Int) = s.subSequence(fromIndex, toIndex);
        public def charAt(index:Int) = s.charAt(index);
        public def length() = s.length();
        public def toString() = s.toString();
    }

    class MyCharSequence1(s:CharSequence) {
        public def subSequence(fromIndex:Int, toIndex:Int) = s.subSequence(fromIndex, toIndex);
        public def charAt(index:Int) = s.charAt(index);
        public def length() = s.length();
        public def toString() = s.toString();
    }
    class MyCharSequence2 extends MyCharSequence1 implements CharSequence {
        def this(s:CharSequence) {
            super(s);
        }
    }

    public def run(): boolean = {
        val s = "abcde";
        chk(s instanceof String);
        chk(s instanceof CharSequence);
        
        val a = s as Any;
        chk(a instanceof String);
        chk(a instanceof CharSequence);
        
        val c = s as CharSequence;
        chk(c.length() == s.length());
        chk(c.charAt(1n) == s.charAt(1n));
        chk(c.toString().equals(s.toString()));
        chk(c.subSequence(1n,2n).equals(s.substring(1n,2n)));

        val m = new MyCharSequence(s);
        chk(m.length() == s.length());
        chk(m.charAt(1n) == s.charAt(1n));
        chk(m.toString().equals(s.toString()));
        chk(m.subSequence(1n,2n).equals(s.substring(1n,2n)));
        
        val m1 = new MyCharSequence1(s);
        chk(m1.length() == s.length());
        chk(m1.charAt(1n) == s.charAt(1n));
        chk(m1.toString().equals(s.toString()));
        chk(m1.subSequence(1n,2n).equals(s.substring(1n,2n)));
        
        val m2 = new MyCharSequence2(s);
        chk(m2.length() == s.length());
        chk(m2.charAt(1n) == s.charAt(1n));
        chk(m2.toString().equals(s.toString()));
        chk(m2.subSequence(1n,2n).equals(s.substring(1n,2n)));
        
        val m21:MyCharSequence1 = m2;
        chk(m21.length() == s.length());
        chk(m21.charAt(1n) == s.charAt(1n));
        chk(m21.toString().equals(s.toString()));
        chk(m21.subSequence(1n,2n).equals(s.substring(1n,2n)));

        val m2c:CharSequence = m2;
        chk(m2c.length() == s.length());
        chk(m2c.charAt(1n) == s.charAt(1n));
        chk(m2c.toString().equals(s.toString()));
        chk(m2c.subSequence(1n,2n).equals(s.substring(1n,2n)));

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new CharSequenceTest().execute();
    }
}
