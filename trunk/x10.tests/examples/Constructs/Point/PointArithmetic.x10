/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing point arithmetic operations.
 *
 * @author igor, 2/2006
 */

public class PointArithmetic extends x10Test {

    public const DIM: int = 5;

    public def run(): boolean = {

        var sum: int = 0;
        val p = [2, 2, 2, 2, 2] as Point(DIM);
        val q = [1, 1, 1, 1, 1] as Point(DIM);
        var c: int = 2;

        // First test that the point/point arithmetic works

        var a: Point(DIM) = p + q;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += a(i);
        x10.io.Console.OUT.println("p+p: sum = "+sum);
        if (sum != 15) return false;

        var s: Point(DIM) = p - q;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += s(i);
        x10.io.Console.OUT.println("p-p: sum = "+sum);
        if (sum != 5) return false;

        var m: Point(DIM) = p * q;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += m(i);
        x10.io.Console.OUT.println("p*p: sum = "+sum);
        if (sum != 10) return false;

        var d: Point(DIM) = p / q;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += d(i);
        x10.io.Console.OUT.println("p/p: sum = "+sum);
        if (sum != 10) return false;

        // Unary +/-

        var u: Point(DIM) = -q;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += u(i);
        x10.io.Console.OUT.println("-p: sum = "+sum);
        if (sum != -5) return false;

        u = +q;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += u(i);
        x10.io.Console.OUT.println("+p: sum = "+sum);
        if (sum != 5) return false;

        // Then test that the point/constant arithmetic works

        a = p + c;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += a(i);
        x10.io.Console.OUT.println("p+c: sum = "+sum);
        if (sum != 20) return false;

        s = p - c;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += s(i);
        x10.io.Console.OUT.println("p-c: sum = "+sum);
        if (sum != 0) return false;

        m = p * c;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += m(i);
        x10.io.Console.OUT.println("p*c: sum = "+sum);
        if (sum != 20) return false;

        d = p / c;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += d(i);
        x10.io.Console.OUT.println("p/c: sum = "+sum);
        if (sum != 5) return false;

        a = c + p;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += a(i);
        x10.io.Console.OUT.println("c+p: sum = "+sum);
        if (sum != 20) return false;

        s = c - p;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += s(i);
        x10.io.Console.OUT.println("c-p: sum = "+sum);
        if (sum != 0) return false;

        m = c * p;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += m(i);
        x10.io.Console.OUT.println("c*p: sum = "+sum);
        if (sum != 20) return false;

        d = c / p;
        sum = 0;
        for (var i: int = 0; i < DIM; i++)
            sum += d(i);
        x10.io.Console.OUT.println("c/p: sum = "+sum);
        if (sum != 5) return false;

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new PointArithmetic().execute();
    }
}
