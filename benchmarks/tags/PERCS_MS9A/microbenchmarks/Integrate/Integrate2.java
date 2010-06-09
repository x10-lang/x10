import jsr166y.*;

/**
 * Version of Integrate with exp hardwired to 2.
 *
 * Sample program using Guassian Quadrature for numerical integration.
 * Inspired by a 
 * <A href="http://www.cs.uga.edu/~dkl/filaments/dist.html"> Filaments</A>
 * demo program.
 * 
 */

public final class Integrate2 {

    static final double errorTolerance = 1.0e-12;

    static final int SERIAL = -1;
    static final int DYNAMIC = 0;
    static final int FORK = 1;
    static int forkPolicy = DYNAMIC;
    static String forkArg = "dynamic";

    /** for time conversion */
    static final long NPS = (1000L * 1000 * 1000);

    static final int reps = 10;

    public static void main(String[] args) throws Exception {
        final double start = 0.0;
        final double end = 1536.0; // 2048.0; // 8192.0;
        int procs;

        try {
            procs = Integer.parseInt(args[0]);
            if (args.length > 1) {
                forkArg = args[1];
                if (forkArg.startsWith("s"))
                    forkPolicy = SERIAL;
                else if (forkArg.startsWith("f"))
                    forkPolicy = FORK;
            }
        }
        catch (Exception e) {
            System.out.println("Usage: java Integrate2 threads <s[erial] | d[ynamic] | f[ork] - default d>");
            return;
        }

        ForkJoinPool g = new ForkJoinPool(procs);
        System.out.println("Integrating from " + start + " to " + end + 
                           " forkPolicy = " + forkArg);
        long lastTime = System.nanoTime();
        for (int i = 0; i < reps; ++i) {
            double a;
            if (forkPolicy == SERIAL)
                a = SQuad.computeArea(g, start, end);
            else if (forkPolicy == FORK) 
                a = FQuad.computeArea(g, start, end);
            else
                a = DQuad.computeArea(g, start, end);
            long now = System.nanoTime();
            double s = ((double)(now - lastTime))/NPS;
            lastTime = now;
            System.out.printf("Time: %7.3f", s);
            System.out.print(" Area: " + a);
            System.out.println();
        }
        System.out.println(g);
        g.shutdown();
    }

    static double computeFunction(double x)  {
        return (x * x + 1.0) * x;
    }

    static final class SQuad extends RecursiveAction {
        static double computeArea(ForkJoinPool pool, double l, double r) {
            SQuad q = new SQuad(l, r, 0);
            pool.invoke(q);
            return q.area;
        }

        final double left;       // lower bound
        final double right;      // upper bound
        double area;
        
        SQuad(double l, double r, double a) {
            this.left = l; this.right = r; this.area = a;
        }
        
        public final void compute() {
            double l = left;
            double r = right;
            area = recEval(l, r, (l * l + 1.0) * l, (r * r + 1.0) * r, area);
        }
        
        // fully recursive version
        static final double recEval(double l, double r, double fl,
                                    double fr, double a) {
            double h = (r - l) * 0.5;
            double c = l + h;
            double fc = (c * c + 1.0) * c; 
            double hh = h * 0.5;
            double al = (fl + fc) * hh; 
            double ar = (fr + fc) * hh;
            double alr = al + ar;
            if (Math.abs(alr - a) <= errorTolerance)
                return alr;
            else
                return recEval(c, r, fc, fr, ar) + recEval(l, c, fl, fc, al);
        }

    }

    // .................................

    static final class AQuad extends RecursiveAction {
        static double computeArea(ForkJoinPool pool, double l, double r) {
            AQuad q = new AQuad(l, r, 0);
            pool.invoke(q);
            return q.area;
        }

        final double left;       // lower bound
        final double right;      // upper bound
        double area;
        
        AQuad(double l, double r, double a) {
            this.left = l; this.right = r; this.area = a;
        }
        
        public final void compute() {
            double l = left;
            double r = right;
            area = accEval(l, r, (l * l + 1.0) * l, (r * r + 1.0) * r, area);
        }
        
        // accumulating version
        static final double accEval(double l, double r, double fl,
                                    double fr, double a) {
            double accum = 0.0;
            double h = (r - l) * 0.5;
            for (;;) {
                double c = l + h;
                double fc = (c * c + 1.0) * c; 
                h *= 0.5;
                double al = (fl + fc) * h; 
                double ar = (fr + fc) * h;
                double alr = al + ar;
                if (Math.abs(alr - a) <= errorTolerance)
                    return accum + alr;
                accum += accEval(c, r, fc, fr, ar);
                r = c;
                a = al;
                fr = fc;
            }
        }
    }

    //....................................

    static final class FQuad extends RecursiveAction {
        static double computeArea(ForkJoinPool pool, double l, double r) {
            FQuad q = new FQuad(l, r, 0);
            pool.invoke(q);
            return q.area;
        }

        final double left;       // lower bound
        final double right;      // upper bound
        double area;
        
        FQuad(double l, double r, double a) {
            this.left = l; this.right = r; this.area = a;
        }
        
        public final void compute() {
            double l = left;
            double r = right;
            area = recEval(l, r, (l * l + 1.0) * l, (r * r + 1.0) * r, area);
        }
        
        // fully recursive version
        static final double recEval(double l, double r, double fl,
                                    double fr, double a) {
            double h = (r - l) * 0.5;
            double c = l + h;
            double fc = (c * c + 1.0) * c; 
            double hh = h * 0.5;
            double al = (fl + fc) * hh; 
            double ar = (fr + fc) * hh;
            double alr = al + ar;
            if (Math.abs(alr - a) <= errorTolerance)
                return alr;
            FQuad q = new FQuad(l, c, al);
            q.fork();
            ar = recEval(c, r, fc, fr, ar);
            if (!q.tryUnfork()) {
                q.quietlyHelpJoin();
                return ar + q.area;
            }
            return ar + recEval(l, c, fl, fc, al);
        }

    }

    // ...........................

    static final class DQuad extends RecursiveAction {
        static double computeArea(ForkJoinPool pool, double l, double r) {
            DQuad q = new DQuad(l, r, 0);
            pool.invoke(q);
            return q.area;
        }

        final double left;       // lower bound
        final double right;      // upper bound
        double area;
        
        DQuad(double l, double r, double a) {
            this.left = l; this.right = r; this.area = a;
        }
        
        public final void compute() {
            double l = left;
            double r = right;
            area = recEval(l, r, (l * l + 1.0) * l, (r * r + 1.0) * r, area);
        }
        
        // fully recursive version
        static final double recEval(double l, double r, double fl,
                                    double fr, double a) {
            double h = (r - l) * 0.5;
            double c = l + h;
            double fc = (c * c + 1.0) * c; 
            double hh = h * 0.5;
            double al = (fl + fc) * hh; 
            double ar = (fr + fc) * hh;
            double alr = al + ar;
            if (Math.abs(alr - a) <= errorTolerance)
                return alr;
            DQuad q = null;
            if (getSurplusQueuedTaskCount() <= 3)
                (q = new DQuad(l, c, al)).fork();
            ar = recEval(c, r, fc, fr, ar);
            if (q != null && !q.tryUnfork()) {
                q.quietlyHelpJoin();
                return ar + q.area;
            }
            return ar + recEval(l, c, fl, fc, al);
        }

    }

}

  
