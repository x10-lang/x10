import x10.lang.*;
import x10.lang.Region;
import x10.util.Iterator_Scanner;

import java.lang.Object;
import java.lang.Double;

import java.io.*;

import x10.array.BaseRegion;
import x10.array.PolyScanner;


public abstract class TestArray extends Test {
    
    PrintStream out;
    String testName;

    public boolean execute() throws java.lang.Exception {
        System.setProperty("line.separator", "\n");
        testName = this.getClass().getName();
        String refName = testName + ".ref";
        if (!(new File(refName)).exists()) {
            out = new PrintStream(new FileOutputStream(refName));
            System.out.println("creating " + refName);
            run();
            out.close();
            return true;
        } else {
            String outName = testName + ".out";
            out = new PrintStream(new FileOutputStream(outName));
            run();
            out.close();
            Process proc = java.lang.Runtime.getRuntime().exec("diff " + refName + " " + outName);
            InputStream is = proc.getInputStream();
            byte [] buf = new byte[10000];
            for (;;) {
                int n = is.read(buf);
                if (n<0) break;
                System.out.write(buf, 0, n);
            }
            proc.waitFor();
            if (proc.exitValue()==0) {
                System.out.println("test " + testName + " succeeds");
                return true;
            } else {
                System.out.println("test " + testName + " FAILS");
                return false;
            }
        }

    }

    //
    //
    //

    class Init implements Indexable_double {
        public double get(Point pt) {
            int [] cs = pt.coords();
            int val = 1;
            for (int i=0; i<cs.length; i++)
                val *= cs[i];
            return val%10;
        }
    }

    abstract class R {

        R(String test) {
            String r;
            try {
                r = run();
            } catch (Throwable e) {
                r = e.getMessage();
            }
            pr(test + " " + r);
        }

        abstract String run();

    }
            
    abstract class E {

        E(String test) {
            try {
                run();
            } catch (Throwable e) {
                pr(test + ": " + e.getMessage());
                return;
            }
            pr(test + ": expected exception not thrown");
        }

        abstract void run();
    }
            
    class Grid {

        nullable<Object> [] os = new Object[10];

        void set(int i0, double value) {
            os[i0] = new Double(value);
        }

        void set(int i0, int i1, double value) {
            nullable<Grid> grid = (nullable<Grid>) os[i0];
            if (grid==null)
                os[i0] = grid = new Grid();
            grid.set(i1, value);
        }

        void set(int i0, int i1, int i2, double value) {
            nullable<Grid> grid = (nullable<Grid>) os[i0];
            if (grid==null)
                os[i0] = grid = new Grid();
            grid.set(i1, i2, value);
        }

        void pr(int rank) {
            for (int i=0; i<os.length; i++) {
                nullable<Object> o = os[i];
                if (o==null) {
                    if (rank==1)
                        out.print(".");
                } else if (o instanceof Grid) {
                    if (rank==2)
                        out.print("    " + i + "  ");
                    else if (rank>=3) {
                        out.print("    ");
                        for (int j=0; j<rank; j++)
                            out.print("-");
                        out.print(" " + i + "\n");
                    }
                    ((nullable<Grid>)o).pr(rank-1);
                } else if (o instanceof Double) {
                    out.print(((nullable<Double>)o).intValue()+"");
                }

                if (rank==1)
                    out.print(" ");
            }
            if (rank==1)
                out.print("\n");
        }
    }

    Array_double prArray(String test, final Region r) {
        Array_double a = Array_double.make(r, new Init());
        prArray(test, a);
        return a;
    }


    void prRegion(String test, final Region r, boolean probe) {

        pr("--- " + testName + ": " + test);

        new R("rank")		{String run() {return "" + r.rank;}};
        new R("rect")		{String run() {return "" + r.rect;}};
        new R("zeroBased")	{String run() {return "" + r.zeroBased;}};
        new R("rail")		{String run() {return "" + r.rail;}};

        new R("isConvex()")	{String run() {return "" + r.isConvex();}};
        new R("size()")		{String run() {return "" + r.size();}};

        //((BaseRegion)r).printInfo(out);
        pr("region: " + r);

        // probe for unbounded
        if (probe) {
            Region.Scanner s = (Region.Scanner) r.scanners().next();
            /*
            if (s instanceof PolyScanner)
                ((PolyScanner)s).printInfo(out);
            */
            Region.Iterator i = r.iterator();
        }
    }
    

    void prArray(String test, Array_double a) {

        final Region r = a.region;

        prRegion(test, r, false);

        // scanner api
        Grid grid = new Grid();
        Iterator_Scanner it = r.scanners();
        while (it.hasNext()) {
            Region.Scanner s = (Region.Scanner) it.next();
            pr("  poly");
            /*
            if (s instanceof PolyScanner)
                ((PolyScanner)s).printInfo(out);
            */
            if (r.rank==0) {
                pr("ERROR rank==0");
            } else if (r.rank==1) {
                int min0 = s.min(0);
                int max0 = s.max(0);
                for (int i0=min0; i0<=max0; i0++)
                    grid.set(i0, a.get(i0));
            } else if (r.rank==2) {
                int min0 = s.min(0);
                int max0 = s.max(0);
                for (int i0=min0; i0<=max0; i0++) {
                    s.set(0, i0);
                    int min1 = s.min(1);
                    int max1 = s.max(1);
                    for (int i1=min1; i1<=max1; i1++)
                        grid.set(i0, i1, a.get(i0,i1));
                }
            } else if (r.rank==3) {
                int min0 = s.min(0);
                int max0 = s.max(0);
                for (int i0=min0; i0<=max0; i0++) {
                    s.set(0, i0);
                    int min1 = s.min(1);
                    int max1 = s.max(1);
                    for (int i1=min1; i1<=max1; i1++) {
                        s.set(1, i1);
                        int min2 = s.min(2);
                        int max2 = s.max(2);
                        for (int i2=min2; i2<=max2; i2++)
                            grid.set(i0, i1, i2, a.get(i0,i1,i2));
                    }
                }
            }
        }
        grid.pr(r.rank);

        // iterator api
        pr("  iterator");
        grid = new Grid();
        Region.Iterator ri = r.iterator();
        while (ri.hasNext()) {
            int [] x = ri.next();
            Point p = Point.make(x);
            double v = a.get(p);
            if (x.length==1)
                grid.set(x[0], v);
            else if (x.length==2)
                grid.set(x[0], x[1], v);
            else if (x.length==3)
                grid.set(x[0], x[1], x[2], v);
        }
        grid.pr(r.rank);
    }


    void prPoint(String test, Point p) {
        int sum = 0;
        for (int i=0; i<p.rank; i++)
            sum += p.get(i);
        pr(test + " " + p + " sum=" + sum);
    }

    void pr(String s) {
        out.println(s);
    }

}


