// XXX integrate with Harness

public class Tester {

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

