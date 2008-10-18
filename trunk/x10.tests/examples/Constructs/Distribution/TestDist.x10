import x10.io.PrintStream;
import x10.io.OutputStream;
import x10.io.ByteArrayOutputStream;

import harness.x10Test;


abstract public class TestDist extends x10Test {
    
    var os: OutputStream;
    var out: PrintStream;
    val testName = className().substring(6,className().length());

    def this() {
        System.setProperty("line.separator", "\n");
        try {
            os = new ByteArrayOutputStream();
            out = new PrintStream(os);
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


    //
    // Support for pretty-printing an array.
    //

    class Grid {

        var os: Rail[Object] = Rail.makeVar[Object](10);

        def set(i0: int, vue: double): void = {
            os(i0) = vue;
        }

        def set(i0: int, i1: int, vue: double): void = {
            if (os(i0)==null) os(i0) = new Grid();
            val grid = os(i0) as Grid;
            grid.set(i1, vue);
        }

        def set(i0: int, i1: int, i2: int, vue: double): void = {
            if (os(i0)==null) os(i0) = new Grid();
            val grid = os(i0) as Grid;
            grid.set(i1, i2, vue);
        }

        def pr(rank: int): void = {
            var min: int = os.length;
            var max: int = 0;
            for (var i: int = 0; i<os.length; i++) {
                if (os(i)!=null) {
                    if (i<min) min = i;
                    else if (i>max) max = i;
                }
            }
            for (var i: int = 0; i<os.length; i++) {
                var o: Object = os(i);
                if (o==null) {
                    if (rank==1)
                        out.print(".");
                    else if (rank==2) {
                        if (min<=i && i<=max)
                            out.print("    " + i + "\n");
                    }
                } else if (o instanceof Grid) {
                    if (rank==2)
                        out.print("    " + i + "  ");
                    else if (rank>=3) {
                        out.print("    ");
                        for (var j: int = 0; j<rank; j++)
                            out.print("-");
                        out.print(" " + i + "\n");
                    }
                    (o as Grid).pr(rank-1);
                } else if (o instanceof Double) {
                    val d = o to double; // XTENLANG-34
                    out.print((d to int)+"");
                }

                if (rank==1)
                    out.print(" ");
            }
            if (rank==1)
                out.print("\n");
        }
    }

    //
    // XXX how to make these generic for different ranks??
    //

    def prArray(a: Array[double](2), bump: boolean): void = {
        var grid: Grid = new Grid();
        for (p:Point(2) in a.region) {
            if (bump) a(p(0), p(1)) += 1;
            grid.set(p(0), p(1), a(p(0), p(1)));
        }
        grid.pr(a.rank);
    }

    def prDist(test: String, d: Dist(2)): void = {

        pr("--- " + test + ": " + d);

        val init = (Point) => -1.0D;
        var a: Array[double](2) = Array.make[double](d.region, init) as Array[double](2);

        var ps: Rail[Place] = d.places();
        for (var i: int = 0; i<ps.length; i++) {
            var r: Region = d.get(ps(i));
            for (p:Point(2) in r)
                a(p(0), p(1)) = a(p(0), p(1)) + ps(i).id + 1;
        }
        prArray(a, false);
    }
        

    def pr(s: String): void = {
        out.println(s);
    }

}
