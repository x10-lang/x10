/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * 
 * @author vj
 */
public class ArrayTypeCheck_MustFailCompile extends x10Test {

    public def run(): boolean = {
        val a1:Array[Int](3)  =  // should be Array[Int](2)
            Array.make[Int](Dist.makeConstant([0..2, 0..3], here), (p(i): Point)=>i);
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayTypeCheck_MustFailCompile().execute();
    }
}
