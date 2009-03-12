// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 12/2008
 */

import x10.io.StringWriter;
import x10.io.Printer;
    
class XTENLANG_162 extends x10Test {

    public def run():boolean {
    
        val os = new StringWriter();
        val ps = new Printer(os);
    
        ps.printf("hi");
        x10.io.Console.OUT.println(os.toString());

        return true;
    }
    
    public static def main(Rail[String]) {
        new XTENLANG_162().execute();
    }
}
