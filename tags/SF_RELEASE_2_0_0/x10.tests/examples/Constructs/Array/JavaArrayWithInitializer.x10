/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * The x10-style initializer for java arrays should work.
 *
 * @author kemal 8/2005
 */

public class JavaArrayWithInitializer extends x10Test {

    const N: int = 25;

    public def run(): boolean = {

        val foo1  = Array.make[int](0..N-1->here, ((i): Point)=> i);

        x10.io.Console.OUT.println("1");

        for (val (i): Point in 0..N-1) chk(foo1(i) == i);
        val foo2  = Array.make[int](0..N-1 -> here, ((i): Point)=>i);

        x10.io.Console.OUT.println("2");

        for (val (i): Point(1) in 0..N-1) chk(foo2(i) == i);

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new JavaArrayWithInitializer().execute();
    }
}
