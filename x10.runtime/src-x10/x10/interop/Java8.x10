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

package x10.interop;

import x10.compiler.Native;
import x10.compiler.NativeRep;

@NativeRep("c++", "#error Undefined Java", "#error Undefined Java", null)
public class Java8 {
    private def this() { } // no instances


    // Utilities for Java functional interface


    // BinaryOperator
    public static def toX10(op:java.util.function.IntBinaryOperator):(Int,Int)=>Int {
        return (left:Int,right:Int)=>op.applyAsInt(left,right);
    }
    public static def toX10(op:java.util.function.LongBinaryOperator):(Long,Long)=>Long {
        return (left:Long,right:Long)=>op.applyAsLong(left,right);
    }
    public static def toX10(op:java.util.function.DoubleBinaryOperator):(Double,Double)=>Double {
        return (left:Double,right:Double)=>op.applyAsDouble(left,right);
    }
    public static def toX10[T](op:java.util.function.BinaryOperator/*[T]*/):(T,T)=>T {
        return (left:T,right:T)=>op.apply(left,right) as T;
    }

    public static def toJava(xop:(Int,Int)=>Int):java.util.function.IntBinaryOperator {
        return new java.util.function.IntBinaryOperator() {
            public def applyAsInt(left:Int,right:Int):Int { return xop(left,right); }
        };
    }
    public static def toJava(xop:(Long,Long)=>Long):java.util.function.LongBinaryOperator {
        return new java.util.function.LongBinaryOperator() {
            public def applyAsLong(left:Long,right:Long):Long { return xop(left,right); }
        };
    }
    public static def toJava(xop:(Double,Double)=>Double):java.util.function.DoubleBinaryOperator {
        return new java.util.function.DoubleBinaryOperator() {
            public def applyAsDouble(left:Double,right:Double):Double { return xop(left,right); }
        };
    }
    public static def toJava[T](xop:(T,T)=>T):java.util.function.BinaryOperator/*[T]*/ {
        return new java.util.function.BinaryOperator/*[T]*/() {
            public def apply(left:Any/*T*/,right:Any/*T*/):Any/*T*/ { return xop(left as T,right as T); }
        };
    }


    // UnaryOperator
    public static def toX10(op:java.util.function.IntUnaryOperator):(Int)=>Int {
        return (operand:Int)=>op.applyAsInt(operand);
    }
    public static def toX10(op:java.util.function.LongUnaryOperator):(Long)=>Long {
        return (operand:Long)=>op.applyAsLong(operand);
    }
    public static def toX10(op:java.util.function.DoubleUnaryOperator):(Double)=>Double {
        return (operand:Double)=>op.applyAsDouble(operand);
    }
    public static def toX10[T](op:java.util.function.UnaryOperator/*[T]*/):(T)=>T {
        return (operand:T)=>op.apply(operand) as T;
    }

    public static def toJava(xop:(Int)=>Int):java.util.function.IntUnaryOperator {
        return new java.util.function.IntUnaryOperator() {
            public def applyAsInt(operand:Int):Int { return xop(operand); }
        };
    }
    public static def toJava(xop:(Long)=>Long):java.util.function.LongUnaryOperator {
        return new java.util.function.LongUnaryOperator() {
            public def applyAsLong(operand:Long):Long { return xop(operand); }
        };
    }
    public static def toJava(xop:(Double)=>Double):java.util.function.DoubleUnaryOperator {
        return new java.util.function.DoubleUnaryOperator() {
            public def applyAsDouble(operand:Double):Double { return xop(operand); }
        };
    }
    public static def toJava[T](xop:(T)=>T):java.util.function.UnaryOperator/*[T]*/ {
        return new java.util.function.UnaryOperator/*[T]*/() {
            public def apply(operand:Any/*T*/):Any/*T*/ { return xop(operand as T); }
        };
    }


    // Predicate
    public static def toX10(op:java.util.function.IntPredicate):(Int)=>Boolean {
        return (value:Int)=>op.test(value);
    }
    public static def toX10(op:java.util.function.LongPredicate):(Long)=>Boolean {
        return (value:Long)=>op.test(value);
    }
    public static def toX10(op:java.util.function.DoublePredicate):(Double)=>Boolean {
        return (value:Double)=>op.test(value);
    }
    public static def toX10[T](op:java.util.function.Predicate/*[T]*/):(T)=>Boolean {
        return (value:T)=>op.test(value);
    }

    public static def toJava(xop:(Int)=>Boolean):java.util.function.IntPredicate {
        return new java.util.function.IntPredicate() {
            public def test(value:Int):Boolean { return xop(value); }
        };
    }
    public static def toJava(xop:(Long)=>Boolean):java.util.function.LongPredicate {
        return new java.util.function.LongPredicate() {
            public def test(value:Long):Boolean { return xop(value); }
        };
    }
    public static def toJava(xop:(Double)=>Boolean):java.util.function.DoublePredicate {
        return new java.util.function.DoublePredicate() {
            public def test(value:Double):Boolean { return xop(value); }
        };
    }
    public static def toJava[T](xop:(T)=>Boolean):java.util.function.Predicate/*[T]*/ {
        return new java.util.function.Predicate/*[T]*/() {
            public def test(value:Any/*T*/):Boolean { return xop(value as T); }
        };
    }


    // BiPredicate
    public static def toX10[T,U](op:java.util.function.BiPredicate/*[T,U]*/):(T,U)=>Boolean {
        return (t:T,u:U)=>op.test(t,u);
    }

    public static def toJava[T,U](xop:(T,U)=>Boolean):java.util.function.BiPredicate/*[T,U]*/ {
        return new java.util.function.BiPredicate/*[T,U]*/() {
            public def test(t:Any/*T*/,u:Any/*U*/):Boolean { return xop(t as T,u as U); }
        };
    }


    // Consumer
    public static def toX10(op:java.util.function.IntConsumer):(Int)=>void {
        return (value:Int)=>{ op.accept(value); };
    }
    public static def toX10(op:java.util.function.LongConsumer):(Long)=>void {
        return (value:Long)=>{ op.accept(value); };
    }
    public static def toX10(op:java.util.function.DoubleConsumer):(Double)=>void {
        return (value:Double)=>{ op.accept(value); };
    }
    public static def toX10[T](op:java.util.function.Consumer/*[T]*/):(T)=>void {
        return (value:T)=>{ op.accept(value); };
    }

    public static def toJava(xop:(Int)=>void):java.util.function.IntConsumer {
        return new java.util.function.IntConsumer() {
            public def accept(value:Int):void { xop(value); }
        };
    }
    public static def toJava(xop:(Long)=>void):java.util.function.LongConsumer {
        return new java.util.function.LongConsumer() {
            public def accept(value:Long):void { xop(value); }
        };
    }
    public static def toJava(xop:(Double)=>void):java.util.function.DoubleConsumer {
        return new java.util.function.DoubleConsumer() {
            public def accept(value:Double):void { xop(value); }
        };
    }
    public static def toJava[T](xop:(T)=>void):java.util.function.Consumer/*[T]*/ {
        return new java.util.function.Consumer/*[T]*/() {
            public def accept(value:Any/*T*/):void { xop(value as T); }
        };
    }


    // BiConsumer
    public static def toX10[T](op:java.util.function.ObjIntConsumer/*[T]*/):(T,Int)=>void {
        return (t:T,u:Int)=>{ op.accept(t,u); };
    }
    public static def toX10[T](op:java.util.function.ObjLongConsumer/*[T]*/):(T,Long)=>void {
        return (t:T,u:Long)=>{ op.accept(t,u); };
    }
    public static def toX10[T](op:java.util.function.ObjDoubleConsumer/*[T]*/):(T,Double)=>void {
        return (t:T,u:Double)=>{ op.accept(t,u); };
    }
    public static def toX10[T,U](op:java.util.function.BiConsumer/*[T,U]*/):(T,U)=>void {
        return (t:T,u:U)=>{ op.accept(t,u); };
    }

    public static def toJava[T](xop:(T,Int)=>void):java.util.function.ObjIntConsumer/*[T]*/ {
        return new java.util.function.ObjIntConsumer/*[T]*/() {
            public def accept(t:Any/*T*/,u:Int):void { xop(t as T,u); };
        };
    }
    public static def toJava[T](xop:(T,Long)=>void):java.util.function.ObjLongConsumer/*[T]*/ {
        return new java.util.function.ObjLongConsumer/*[T]*/() {
            public def accept(t:Any/*T*/,u:Long):void { xop(t as T,u); };
        };
    }
    public static def toJava[T](xop:(T,Double)=>void):java.util.function.ObjDoubleConsumer/*[T]*/ {
        return new java.util.function.ObjDoubleConsumer/*[T]*/() {
            public def accept(t:Any/*T*/,u:Double):void { xop(t as T,u); };
        };
    }
    public static def toJava[T,U](xop:(T,U)=>void):java.util.function.BiConsumer/*[T,U]*/ {
        return new java.util.function.BiConsumer/*[T,U]*/() {
            public def accept(t:Any/*T*/,u:Any/*U*/):void { xop(t as T,u as U); };
        };
    }


    // Supplier
    public static def toX10(op:java.util.function.IntSupplier):()=>Int {
        return ()=>op.getAsInt();
    }
    public static def toX10(op:java.util.function.LongSupplier):()=>Long {
        return ()=>op.getAsLong();
    }
    public static def toX10(op:java.util.function.DoubleSupplier):()=>Double {
        return ()=>op.getAsDouble();
    }
    public static def toX10(op:java.util.function.BooleanSupplier):()=>Boolean {
        return ()=>op.getAsBoolean();
    }
    public static def toX10[T](op:java.util.function.Supplier/*[T]*/):()=>T {
        return ()=>op.get() as T;
    }

    public static def toJava(xop:()=>Int):java.util.function.IntSupplier {
        return new java.util.function.IntSupplier() {
            public def getAsInt():Int { return xop(); }
        };
    }
    public static def toJava(xop:()=>Long):java.util.function.LongSupplier {
        return new java.util.function.LongSupplier() {
            public def getAsLong():Long { return xop(); }
        };
    }
    public static def toJava(xop:()=>Double):java.util.function.DoubleSupplier {
        return new java.util.function.DoubleSupplier() {
            public def getAsDouble():Double { return xop(); }
        };
    }
    public static def toJava(xop:()=>Boolean):java.util.function.BooleanSupplier {
        return new java.util.function.BooleanSupplier() {
            public def getAsBoolean():Boolean { return xop(); }
        };
    }
    public static def toJava[T](xop:()=>T):java.util.function.Supplier/*[T]*/ {
        return new java.util.function.Supplier/*[T]*/() {
            public def get():Any/*T*/ { return xop(); }
        };
    }


    // ToIntFunction
    public static def toX10(op:java.util.function.LongToIntFunction):(Long)=>Int {
        return (value:Long)=>op.applyAsInt(value);
    }
    public static def toX10(op:java.util.function.DoubleToIntFunction):(Double)=>Int {
        return (value:Double)=>op.applyAsInt(value);
    }
    public static def toX10[T](op:java.util.function.ToIntFunction/*[T]*/):(T)=>Int {
        return (value:T)=>op.applyAsInt(value);
    }

    public static def toJava(xop:(Long)=>Int):java.util.function.LongToIntFunction {
        return new java.util.function.LongToIntFunction() {
            public def applyAsInt(value:Long):Int { return xop(value); }
        };
    }
    public static def toJava(xop:(Double)=>Int):java.util.function.DoubleToIntFunction {
        return new java.util.function.DoubleToIntFunction() {
            public def applyAsInt(value:Double):Int { return xop(value); }
        };
    }
    public static def toJava[T](xop:(T)=>Int):java.util.function.ToIntFunction/*[T]*/ {
        return new java.util.function.ToIntFunction/*[T]*/() {
            public def applyAsInt(value:Any/*T*/):Int { return xop(value as T); }
        };
    }


    // ToLongFunction
    public static def toX10(op:java.util.function.IntToLongFunction):(Int)=>Long {
        return (value:Int)=>op.applyAsLong(value);
    }
    public static def toX10(op:java.util.function.DoubleToLongFunction):(Double)=>Long {
        return (value:Double)=>op.applyAsLong(value);
    }
    public static def toX10[T](op:java.util.function.ToLongFunction/*[T]*/):(T)=>Long {
        return (value:T)=>op.applyAsLong(value);
    }

    public static def toJava(xop:(Int)=>Long):java.util.function.IntToLongFunction {
        return new java.util.function.IntToLongFunction() {
            public def applyAsLong(value:Int):Long { return xop(value); }
        };
    }
    public static def toJava(xop:(Double)=>Long):java.util.function.DoubleToLongFunction {
        return new java.util.function.DoubleToLongFunction() {
            public def applyAsLong(value:Double):Long { return xop(value); }
        };
    }
    public static def toJava[T](xop:(T)=>Long):java.util.function.ToLongFunction/*[T]*/ {
        return new java.util.function.ToLongFunction/*[T]*/() {
            public def applyAsLong(value:Any/*T*/):Long { return xop(value as T); }
        };
    }


    // ToDoubleFunction
    public static def toX10(op:java.util.function.IntToDoubleFunction):(Int)=>Double {
        return (value:Int)=>op.applyAsDouble(value);
    }
    public static def toX10(op:java.util.function.LongToDoubleFunction):(Long)=>Double {
        return (value:Long)=>op.applyAsDouble(value);
    }
    public static def toX10[T](op:java.util.function.ToDoubleFunction/*[T]*/):(T)=>Double {
        return (value:T)=>op.applyAsDouble(value);
    }

    public static def toJava(xop:(Int)=>Double):java.util.function.IntToDoubleFunction {
        return new java.util.function.IntToDoubleFunction() {
            public def applyAsDouble(value:Int):Double { return xop(value); }
        };
    }
    public static def toJava(xop:(Long)=>Double):java.util.function.LongToDoubleFunction {
        return new java.util.function.LongToDoubleFunction() {
            public def applyAsDouble(value:Long):Double { return xop(value); }
        };
    }
    public static def toJava[T](xop:(T)=>Double):java.util.function.ToDoubleFunction/*[T]*/ {
        return new java.util.function.ToDoubleFunction/*[T]*/() {
            public def applyAsDouble(value:Any/*T*/):Double { return xop(value as T); }
        };
    }


    // Function
    public static def toX10[R](op:java.util.function.IntFunction/*[R]*/):(Int)=>R {
        return (value:Int)=>op.apply(value) as R;
    }
    public static def toX10[R](op:java.util.function.LongFunction/*[R]*/):(Long)=>R {
        return (value:Long)=>op.apply(value) as R;
    }
    public static def toX10[R](op:java.util.function.DoubleFunction/*[R]*/):(Double)=>R {
        return (value:Double)=>op.apply(value) as R;
    }
    public static def toX10[T,R](op:java.util.function.Function/*[T,R]*/):(T)=>R {
        return (value:T)=>op.apply(value) as R;
    }

    public static def toJava[R](xop:(Int)=>R):java.util.function.IntFunction/*[R]*/ {
        return new java.util.function.IntFunction/*[R]*/() {
            public def apply(value:Int):R { return xop(value); }
        };
    }
    public static def toJava[R](xop:(Long)=>R):java.util.function.LongFunction/*[R]*/ {
        return new java.util.function.LongFunction/*[R]*/() {
            public def apply(value:Long):R { return xop(value); }
        };
    }
    public static def toJava[R](xop:(Double)=>R):java.util.function.DoubleFunction/*[R]*/ {
        return new java.util.function.DoubleFunction/*[R]*/() {
            public def apply(value:Double):R { return xop(value); }
        };
    }
    public static def toJava[T,R](xop:(T)=>R):java.util.function.Function/*[T,R]*/ {
        return new java.util.function.Function/*[T,R]*/() {
            public def apply(value:Any/*T*/):R { return xop(value as T); }
        };
    }


    // BiFunction
    public static def toX10[T,U](op:java.util.function.ToIntBiFunction/*[T,U]*/):(T,U)=>Int {
        return (t:T,u:U)=>op.applyAsInt(t,u);
    }
    public static def toX10[T,U](op:java.util.function.ToLongBiFunction/*[T,U]*/):(T,U)=>Long {
        return (t:T,u:U)=>op.applyAsLong(t,u);
    }
    public static def toX10[T,U](op:java.util.function.ToDoubleBiFunction/*[T,U]*/):(T,U)=>Double {
        return (t:T,u:U)=>op.applyAsDouble(t,u);
    }
    public static def toX10[T,U,R](op:java.util.function.BiFunction/*[T,U,R]*/):(T,U)=>R {
        return (t:T,u:U)=>op.apply(t,u) as R;
    }

    public static def toJava[T,U](xop:(T,U)=>Int):java.util.function.ToIntBiFunction/*[T,U]*/ {
        return new java.util.function.ToIntBiFunction/*[T,U]*/() {
            public def applyAsInt(t:Any/*T*/,u:Any/*U*/):Int { return xop(t as T,u as U); }
        };
    }
    public static def toJava[T,U](xop:(T,U)=>Long):java.util.function.ToLongBiFunction/*[T,U]*/ {
        return new java.util.function.ToLongBiFunction/*[T,U]*/() {
            public def applyAsLong(t:Any/*T*/,u:Any/*U*/):Long { return xop(t as T,u as U); }
        };
    }
    public static def toJava[T,U](xop:(T,U)=>Double):java.util.function.ToDoubleBiFunction/*[T,U]*/ {
        return new java.util.function.ToDoubleBiFunction/*[T,U]*/() {
            public def applyAsDouble(t:Any/*T*/,u:Any/*U*/):Double { return xop(t as T,u as U); }
        };
    }
    public static def toJava[T,U,R](xop:(T,U)=>R):java.util.function.BiFunction/*[T,U,R]*/ {
        return new java.util.function.BiFunction/*[T,U,R]*/() {
            public def apply(t:Any/*T*/,u:Any/*U*/):Any/*R*/ { return xop(t as T,u as U); }
        };
    }


    // Other
    public static def toX10(op:java.lang.Runnable):()=>void {
        return ()=>{ op.run(); };
    }

    public static def toJava(xop:()=>void):java.lang.Runnable {
        return new java.lang.Runnable() {
            public def run():void { xop(); }
        };
    }

}
