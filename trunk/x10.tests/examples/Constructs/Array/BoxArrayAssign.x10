/*
 *
 * (C) Copyright IBM Corporation 2006-2008
 *
 *  This file is part of X10 Test.
 *
 */
import x10.util.Box;

import harness.x10Test;

/**
 * 
 * 
 * @author shane
 * @author vj
 */

public class BoxArrayAssign extends x10Test {

    public def run(): boolean = {
        val table = Array.make[Box[Complex]](1..5->here, (Point)=>(null as Box[Complex]));
        foreach (val p: Point(1) in table) table(p) = null;
        return true;
    }
    
    public static def main(var args: Rail[String]): void = {
        new BoxArrayAssign().execute();
    }

}
