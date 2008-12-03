/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * @author vj
 */

public class ArrayConstructor extends x10Test {

    public def run(): boolean = {
        val x  = 1.0;
        val a  = Array.make[double](0..5->here, (Point)=>2.0);
        return true;
    }

    public static def main(Rail[String]) {
        new ArrayConstructor().execute();
    }
}
