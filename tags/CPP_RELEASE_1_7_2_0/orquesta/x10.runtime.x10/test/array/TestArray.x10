import x10.lang.*;
import x10.lang.Region;
import x10.util.Iterator_Scanner;

import java.lang.Object;
import java.lang.Double;

import java.io.*;

import x10.array.BaseRegion;
import x10.array.PolyScanner;
import x10.array.UnboundedRegionException;


public abstract class TestArray extends Test {
    
    PrintStream out;
    String testName;

    public boolean execute() throws java.lang.Exception {

        testName = this.getClass().getName();
        String refName = testName + ".ref";

        if (Tester.dbg!=null)
            Tester.dbg.println("--- " + testName);

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
            int min = os.length;
            int max = 0;
            for (int i=0; i<os.length; i++) {
                if (os[i]!=null) {
                    if (i<min) min = i;
                    else if (i>max) max = i;
                }
            }
            for (int i=0; i<os.length; i++) {
                nullable<Object> o = os[i];
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
        return prArray(test, r, false);
    }

    Array_double prArray(String test, final Region r, boolean bump) {
        Indexable_double init = bump? Array_double.NO_INIT : new Init();
        Array_double a = Array_double.make(r, init);
        prArray(test, a, bump);
        return a;
    }

    void prDistributed(final String test, final Array_double a) {
        place [] ps = a.dist.places();
        for (int i=0; i<ps.length; i++) {
            final place p = ps[i];
            finish {
                async (p) {
                    prArray(test + " at " + p + " (by place)", a.$bar(p));
                    Region r = a.dist.get(p);
                    prArray(test + " at " + p + " (by region)", a.$bar(r));
                }
            }
        }
    }


    void prUnbounded(String test, final Region r) {
        try {
            prRegion(test, r);
            Region.Scanner s = (Region.Scanner) r.scanners().next();
            Region.Iterator i = r.iterator();
            if (Tester.dbg!=null && s instanceof PolyScanner)
                ((PolyScanner)s).printInfo((PrintStream)Tester.dbg);
        } catch (UnboundedRegionException e) {
            pr(e.toString());
        }
    }


    void prRegion(String test, final Region r) {

        pr("--- " + testName + ": " + test);

        new R("rank")		{String run() {return "" + r.rank;}};
        new R("rect")		{String run() {return "" + r.rect;}};
        new R("zeroBased")	{String run() {return "" + r.zeroBased;}};
        new R("rail")		{String run() {return "" + r.rail;}};

        new R("isConvex()")	{String run() {return "" + r.isConvex();}};
        new R("size()")		{String run() {return "" + r.size();}};

        pr("region: " + r);

        if (Tester.dbg!=null && r instanceof BaseRegion)
            ((BaseRegion)r).printInfo((PrintStream)Tester.dbg);
    }
    

    void prArray(String test, Array_double a) {
        prArray(test, a, false);
    }

    void prArray(String test, Array_double a, boolean bump) {

        final Region r = a.region;

        prRegion(test, r);

        // scanner api
        Grid grid = new Grid();
        Iterator_Scanner it = r.scanners();
        while (it.hasNext()) {
            Region.Scanner s = (Region.Scanner) it.next();
            if (Tester.dbg!=null && s instanceof PolyScanner)
                ((PolyScanner)s).printInfo((PrintStream)Tester.dbg);
            pr("  poly");
            if (r.rank==0) {
                pr("ERROR rank==0");
            } else if (r.rank==1) {
                int min0 = s.min(0);
                int max0 = s.max(0);
                for (int i0=min0; i0<=max0; i0++) {
                    if (bump) a.set(i0, a.get(i0)+1);
                    grid.set(i0, a.get(i0));
                }
            } else if (r.rank==2) {
                int min0 = s.min(0);
                int max0 = s.max(0);
                for (int i0=min0; i0<=max0; i0++) {
                    s.set(0, i0);
                    int min1 = s.min(1);
                    int max1 = s.max(1);
                    for (int i1=min1; i1<=max1; i1++) {
                        if (bump) a.set(i0, i1, a.get(i0, i1)+1);
                        grid.set(i0, i1, a.get(i0,i1));
                    }
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
                        for (int i2=min2; i2<=max2; i2++) {
                            if (bump) a.set(i0, i2, i2, a.get(i0, i1, i2)+1);
                            grid.set(i0, i1, i2, a.get(i0,i1,i2));
                        }
                    }
                }
            }
        }
        grid.pr(r.rank);

        pr("  iterator");
        prArray1(a, bump);
    }

    void prArray1(Array_double a, boolean bump) {
        // iterator api
        Grid grid = new Grid();
        Region.Iterator ri = a.region.iterator();
        while (ri.hasNext()) {
            int [] x = ri.next();
            Point p = Point.make(x);
            double v = a.get(p);
            if (x.length==1) {
                if (bump) a.set(x[0], (a.get(x[0])+1));
                grid.set(x[0], v);
            } else if (x.length==2) {
                if (bump) a.set(x[0], x[1], (a.get(x[0], x[1])+1));
                grid.set(x[0], x[1], v);
            } else if (x.length==3) {
                if (bump) a.set(x[0], x[1], x[2], (a.get(x[0], x[1], x[2])+1));
                grid.set(x[0], x[1], x[2], v);
            }
        }
        grid.pr(a.rank);
    }


    void prPoint(String test, Point p) {
        int sum = 0;
        for (int i=0; i<p.rank; i++)
            sum += p.get(i);
        pr(test + " " + p + " sum=" + sum);
    }


    class InitM implements Indexable_double {
        public double get(Point pt) {
            return -1;
        }
    }

    void prDist(String test, Dist d) {
        pr("--- " + test + ": " + d);
        Array_double a = Array_double.make(d.region, new InitM());
        place [] ps = d.places();
        for (int i=0; i<ps.length; i++) {
            Region r = d.get(ps[i]);
            Region.Iterator it = r.iterator();
            while (it.hasNext()) {
                Point p = Point.make(it.next());
                a.set(p, a.get(p)+ps[i].id+1);
            }
        }
        prArray1(a, false);
    }
        

    void pr(String s) {
        out.println(s);
    }

    // substitute for [a:b,c:d]
    Region r(int a, int b, int c, int d) {
        int [] min = new int [] {a,c};
        int [] max = new int [] {b,d};
        return Region.makeRectangular(min, max);
    }


}


