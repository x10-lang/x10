/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Tests 2D distributions constructed from regions.
 */
public class ConstructDist2D extends x10Test {

    public def run(): boolean = {
        val e = 1..10;
        val r = [e, e] as Region;
        val d= Dist.makeConstant(r, here);
        return d.equals(Dist.makeConstant([1..10, 1..10] as Region, here));
    }

    public static def main(var args: Rail[String]): void = {
        new ConstructDist2D().execute();
    }
}
