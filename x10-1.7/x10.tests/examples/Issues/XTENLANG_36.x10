// (C) Copyright IBM Corporation 2008
// This file is part of X10 Test. *

import harness.x10Test;

/**
 * @author bdlucas 10/2008
 */

import x10.io.Writer;
import x10.io.IOException;
    
class XTENLANG_36 extends x10Test {

    public class Bug {
    
        def foo(os:Writer, buf:Rail[Byte]) throws IOException {
            os.write(buf, 0, buf.length);
        }
    }

    public def run(): boolean {
        return true;
    }

    public static def main(Rail[String]) {
        new XTENLANG_36().execute();
    }
}
