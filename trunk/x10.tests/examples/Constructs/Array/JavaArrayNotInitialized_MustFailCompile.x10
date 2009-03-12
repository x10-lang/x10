/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Purpose: A regular java array must be initialized.
 * Issue: Java arrays are not initialized. This is a contradiction with X10's nullable semantic
 * @author vcave
 **/

public class JavaArrayNotInitialized_MustFailCompile extends x10Test {

    public def run(): boolean = {
        var array: Rail[JavaArrayNotInitialized_MustFailCompile] = new Array[JavaArrayNotInitialized_MustFailCompile](1);
        return array(0) != null;
    }

    public static def main(var args: Rail[String]): void = {
        new JavaArrayNotInitialized_MustFailCompile().execute();
    }

}
