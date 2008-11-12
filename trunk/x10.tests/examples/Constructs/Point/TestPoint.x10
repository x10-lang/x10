import x10.io.Printer;
import x10.io.StringWriter;

import harness.x10Test;

abstract public class TestPoint extends x10Test {
    
    var os: StringWriter;
    var out: Printer;
    val testName = className().substring(6,className().length());

    def this() {
        System.setProperty("line.separator", "\n");
        try {
            os = new StringWriter();
            out = new Printer(os);
        } catch (e:Exception) {
            e.printStackTrace();
        }
    }

    abstract def expected():String;

    def status() {
        val got = os.toString();
        if (got.equals(expected())) {
            return true;
        } else {
            System.out.println("=== got:\n" + got);
            System.out.println("=== expected:\n" + expected());
            System.out.println("=== ");
            return false;
        }
    }

    def prPoint(test: String, p: Point): void = {
        var sum: int = 0;
        for (var i: int = 0; i<p.rank; i++)
            sum += p(i);
        pr(test + " " + p + " sum=" + sum);
    }

    def pr(s: String): void = {
        out.println(s);
    }

}
