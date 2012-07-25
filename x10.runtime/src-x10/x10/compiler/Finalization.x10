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

/** 
 * A class that is used in elimination of finally clauses for the C++ backend.
 * 
 * NOT INTENDED FOR USE BY X10 PROGRAMMERS
 * 
 * @author Bowen Alpern
 */

import x10.io.CustomSerialization;
import x10.io.SerialData;

public class Finalization extends x10.lang.Exception implements CustomSerialization {
    
    public var value: Any          = null;
    public var label: String       = null;
    public var isReturn: boolean   = false;
    public var isBreak: boolean    = false;
    public var isContinue: boolean = false;

    /** */
    public static def throwReturn(): void {
        val f      = new Finalization();
        f.isReturn = true;
        throw f;
    }

    /** */
    public static def throwReturn(v: Any): void {
        val f      = new Finalization();
        f.value    = v;
        f.isReturn = true;
        throw f;
    }

    /** */
    public static def throwBreak(): void {
        val f     = new Finalization();
        f.isBreak = true;
        throw f;
    }

    /** */
    public static def throwBreak(l: String): void {
        val f     = new Finalization();
        f.label   = l;
        f.isBreak = true;
        throw f;
    }

    /** */
    public static def throwContinue(): void {
        val f        = new Finalization();
        f.isContinue = true;
        throw f;
    }

    /** */
    public static def throwContinue(l: String): void {
        val f        = new Finalization();
        f.label      = l;
        f.isContinue = true;
        throw f;
    }

    /** */ // justify a catch block for the Java compiler
    public static def plausibleThrow(): void {
        if (x10.compiler.CompilerFlags.TRUE()) return;
        throw new Finalization();
    }

    /**
     * Serialization of Finalization objects is forbidden.
     * @throws UnsupportedOperationException
     */
    public def serialize():SerialData {
    	throw new UnsupportedOperationException("Cannot serialize "+typeName());
    }

    /**
     * Serialization of Finalization objects is forbidden.
     * @throws UnsupportedOperationException
     */
    public def this(SerialData) {
    	throw new UnsupportedOperationException("Cannot deserialize "+typeName());
    }

    public def this(){}

}
