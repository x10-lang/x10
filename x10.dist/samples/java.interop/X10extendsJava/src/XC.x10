/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2012.
 */

public class XC extends JC {
    public def this(arg:String) {
        super(arg);
    }
    public static def main(args:Array[String](1)):void {
        val arg = args.size > 0 ? args(0) : "World";
        new XC(arg);
    }
}
