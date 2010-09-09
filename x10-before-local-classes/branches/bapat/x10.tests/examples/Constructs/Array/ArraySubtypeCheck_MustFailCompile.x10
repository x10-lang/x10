/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 */
public class ArraySubtypeCheck_MustFailCompile extends x10Test {

    class Sup {}

    class Sub extends Sup {}
    
    public def run(): boolean = {
        val R:Region = 0..3;
        var subarr00: Array[Sub] = Array.make[Sub](R->here, (Point)=>null as Sub);
        var suparr00: Array[Sup] = subarr00;
        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new ArraySubtypeCheck_MustFailCompile().execute();
    }
}
