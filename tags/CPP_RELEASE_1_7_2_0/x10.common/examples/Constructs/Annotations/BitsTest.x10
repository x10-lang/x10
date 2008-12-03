//OPTIONS: -PLUGINS=bits.plugin.BitsTypePlugin

import harness.x10Test;
import x10.lang.*;
import bits.*;

public class BitsTest extends harness.x10Test {
    int @Bits(3) a;
    int @Bits(17) b;
    
    public boolean run() {
    	b = 65536;
    	b = 0;
        a = 7 & 1;
        b = 0x01234567 & 0x1ffff;
        return a == 1 && b == 0x14567;
    }

    public static void main(String[] args) {
        new BitsTest().execute();
    }
}
