// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

import x10.io.PrintStream;
import x10.io.ByteArrayOutputStream;

class XTENLANG_38 extends x10Test {

    public def run():boolean {

        val b = 'a' to byte;

        // the bug as originally reported was that this would output
        // the string "97" instead of the byte "a", but that's not
        // testable by our automated test scripts
        //System.out.write(b);

        // however we can reproduce the same bug in a testable way
        val os = new ByteArrayOutputStream();
        val ps = new PrintStream(os);
        ps.write(b);
        System.out.println("got " + os.toString());

        return os.toString().equals("a");
    }

    public static def main(Rail[String]) {
        new XTENLANG_38().execute();
    }
}
