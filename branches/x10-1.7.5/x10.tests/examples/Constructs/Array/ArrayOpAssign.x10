/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Simple test for operator assignment of array elements.
 */

public class ArrayOpAssign extends x10Test {

    public def run(): boolean = {
        var result: boolean = true;
        val R:Region(2) = [1..10, 1..10];
        var ia: Array[int](2) = Array.make[int](R-> here, (Point)=>0);
        ia(1, 1) = 1;
        ia(1, 1) += ia(1, 1);
        result &= (2 == ia(1, 1));
        x10.io.Console.OUT.println("ia[1,1])" + ia(1, 1));
        ia(1, 1) *= 2;
        x10.io.Console.OUT.println("ia[1,1])" + ia(1, 1));
        result &= (4 == ia(1, 1));
        var id: Array[double](2) = Array.make[double](R->here, (Point)=>0D);
        id(1, 1) += 42;
        result &= (42 == id(1, 1));
        x10.io.Console.OUT.println("id[1,1])" + id(1, 1));
        id(1, 1) *= 2;
        x10.io.Console.OUT.println("id[1,1])" + id(1, 1));
        result &= (84 == id(1, 1));
        return result;
    }

    public static def main(var args: Rail[String]): void = {
        new ArrayOpAssign().execute();
    }
}
