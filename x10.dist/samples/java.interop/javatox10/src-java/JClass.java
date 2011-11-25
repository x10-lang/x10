/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2011.
 */

public class JClass extends XClass {
    static void testXThrows() {
        JClass j = new JClass();
        j.xthrows();
    }
    public static void main(String[] args) {
        java.io.PrintStream o = System.out;
        JClass j = new JClass();
        o.println(j.xint$O());
        o.println(j.xobject());
        o.println(j.xstring$O());
        o.println(j.xclass());
	j.xstaticfield();
    }
}
