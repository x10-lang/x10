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
 
package x10.compiler;

public class ClockedVar[T] {
    var xRead:T;
    var xWrite: T;
    public def this () { }
    public def this(x:T){this.xRead=x;}
    public def get():T = xRead;
    public def set(x:T){this.xWrite=x;}

    
}