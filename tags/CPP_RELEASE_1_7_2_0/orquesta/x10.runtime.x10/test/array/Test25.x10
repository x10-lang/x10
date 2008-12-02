/**
 * Distributed array
 *
 * (Was ArrayToDist)
 */

class Test25 extends TestArray {

    const int N = 4;



    public void run() {

        //final region R = [0:N-1,0:N-1];
        //final dist D = dist.factory.block(R);

        final Region r = r(0,N-1,0,N-1);
        pr("r " + r);

        final Dist d0 = Dist.makeCyclic(r, 0);
        pr("d0 " + d0);

        test(d0);

        final Dist d1 = Dist.makeCyclic(r, 1);
        pr("d1 " + d1);

        test(d1);

        //        /*
        //        final foo[.] A2 = new foo[D](point p[i,j]) { return new foo(f(i, j)); };
        //        for (point p[i,j]: a1)
        //        chk(f(i, j) == future(a1.distribution[i,j]) { a1[i,j] }.force(), "1");
        //        finish foreach (point p[i,j]: a1)
        //        chk(f(i, j) == future(a1.distribution[i,j]) { a1[i,j] }.force(), "2");
        //        finish ateach (point p[i,j]: a1)
        //        chk(f(i, j) == a1[i,j], "3");
        //        
        //        for (point p[i,j]: A2)
        //        chk(f(i, j) == future(A2.distribution[i,j]) { A2[i,j].val }.force(), "4");
        //        finish foreach (point p[i,j]: A2)
        //        chk(f(i, j) == future(A2.distribution[i,j]) { A2[i,j].val }.force(), "5");
        //        finish ateach (point p[i,j]: A2)
        //        chk(f(i, j) == A2[i,j].val, "6");
        //        
        //        return true;
        //	}
        //        */

    }


    void test(Dist d) {

        class Init1 implements Indexable_double/*int*/ {
            public double/*int*/ get(Point p) {
                return f(p.get(0), p.get(1));
            }
        };
        final Array_double/*int*/ a1 = Array_double/*int*/.make(d, new Init1());
        prDistributed("a1", a1);
    }


    static int f(int i, int j) {
        return N * i + j;
    }

    static class foo {
        public int val;
        public foo(int x) { this.val = x; }
    }

}

