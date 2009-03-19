/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Must allow binding components to an existing point.
 *
 * @author igor, 1/2006
 */

public class PointRebinding extends x10Test {

    public def run(): boolean = {

        val p: Point = [1, 2] as Point;
        val (i, j): Point = p;

        return (i == 1 && j == 2);
    }

    public static def main(var args: Rail[String]): void = {
        new PointRebinding().execute();
    }
}
