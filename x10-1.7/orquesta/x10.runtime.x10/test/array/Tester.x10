// XXX integrate with Harness

import java.io.*;

public class Tester {

    final static nullable<PrintStream> dbg;
    
    static {
        System.setProperty("line.separator", "\n");
        nullable<PrintStream> d = null;
        try {
            OutputStream os = new FileOutputStream("dbg.out");
            d = new PrintStream(os);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        dbg = d;
    }

    public static void main(String [] args) {

        int rc = 0;
        try {
            for (int i=0; i<args.length; i++) {
                Test t = (Test) Class.forName(args[i]).newInstance();
                if (!t.execute())
                    rc = -1;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }

        System.exit(rc);
    }
}

