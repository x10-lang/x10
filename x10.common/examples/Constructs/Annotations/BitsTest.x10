//OPTIONS: -PLUGINS=bits.BitsTypePlugin

import harness.x10Test;
import x10.lang.*;
import bits.*;

public class BitsTest extends harness.x10Test {
    int @Bits(3) a;
    int @Bits(17) b;
    
    public boolean run() {
    	a = 7; // error
    	b = 65536; // ok
    	a = (@Bits(1)) 1; // ok
    	b = 0; // ok
        a = 7 & 1; // ok 
        b = 65536 & 1; // ok
        return a == 1 && b == 0;
    }

    public static void main(String[] args) {
        new BitsTest().execute();
    }
}
