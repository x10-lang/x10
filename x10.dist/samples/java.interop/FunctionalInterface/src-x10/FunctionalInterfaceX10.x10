/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2018.
 */

import java.util.function.LongBinaryOperator;
import x10.interop.Java8;

public class FunctionalInterfaceX10 {
    public static def reduce(op:LongBinaryOperator, init:Long, from:Long, to:Long, check:Long):String {
        val xop = Java8.toX10(op);
        var sum:Long = init;
        for (var i:Long = from; i <= to; ++i) {
            //sum = op.applyAsLong(sum, i);
            sum = xop(sum, i);
        }
        val error = (sum == check) ? null : "ERROR: something is wrong with X10 long binary operator";
        return error;
    }
    public static def main(Rail[String]):void {
        //val error = reduce(new LongBinaryOperatorX10(), 0, 0, 10, 55);
        val error = reduce(Java8.toJava((left:Long,right:Long)=>left+right), 0, 0, 10, 55);
        if (error != null) {
            Console.OUT.println(error);
            System.setExitCode(1n);
            return;
        }
        Console.OUT.println("OK");
    }
}
