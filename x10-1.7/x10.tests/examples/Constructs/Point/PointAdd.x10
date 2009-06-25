/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Testing point adition..
 *
 * @author vj 08/29/08
 */

public class PointAdd extends x10Test {

    public def run(): boolean = {

        val p = [2, 2, 2, 2, 2] as Point;
        val q = [1, 1, 1, 1, 1] as Point;
    
        var a:Point(p.rank) = p + q;

        return true;
    }

    public static def main(var args: Rail[String]): void = {
        new PointAdd().execute();
    }
}
