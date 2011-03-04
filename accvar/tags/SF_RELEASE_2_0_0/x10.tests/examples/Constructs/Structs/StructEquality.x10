/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * @author Christian Grothoff
 */
public class StructEquality extends x10Test {
    public def run(): boolean = {
        val v1 = V(1);
        val v2 = V(1);
        return v1 == v2;
    }

    public static def main(args: Rail[String]): void = {
        new StructEquality().execute();
    }

    static struct V {
        val v: int;
        def this(i: int) {
            this.v = i;
        }
    }
}
