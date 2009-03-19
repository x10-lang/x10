// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

import x10.io.Printer;
import x10.io.StringWriter;
import x10.io.IOException;

class XTENLANG_38 extends x10Test {

    public def run():boolean {

        val b = 'a' as byte;

        // the bug as originally reported was that this would output
        // the string "97" instead of the byte "a", but that's not
        // testable by our automated test scripts
        //x10.io.Console.OUT.write(b);

        // however we can reproduce the same bug in a testable way
        val os = new StringWriter();
        val ps = new Printer(os);
        try { ps.write(b); } catch (e:IOException) {}
        x10.io.Console.OUT.println("got " + os.toString());

        return os.toString().equals("a");
    }

    public static def main(Rail[String]) {
        new XTENLANG_38().execute();
    }
}
