package x10.array;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


//
// This class represents a single polyhedral halfspace of the form
//
//     a0*x0 + a1*x1 + ... + constant <= 0
//
// The as are stored in the first rank elements of the as[] array; the
// constant is stored in as[rank()] (using homogeneous coordinates).
//
// XXX shouldn't be public - temp hack to support Iterator_Halfspace
//

public value class Halfspace implements java.lang.Comparable {

    int [] as;

    private int rank;

    Halfspace(int [] as) {
        this.as = as;
        this.rank = as.length-1;
    }

    //
    // natural sort order for halfspaces: from lo to hi on each
    // axis, from most major to least major axis, with constant as
    // least siginficant part of key
    //
    public int compareTo(java.lang.Object t) {
        Halfspace that = (Halfspace) t;
        for (int i=0; i<as.length; i++) {
            if (as[i] < that.as[i])
                return -1;
            else if (as[i] > that.as[i])
                return 1;
        }
        return 0;
    }


    //
    // two halfspaces are parallel if all coefficients are the
    // same; constants may differ
    //
    // XXX only right if first coefficients are the same; needs to
    // allow for multiplication by positive constant
    //
    boolean isParallel(Halfspace that) {
        for (int i=0; i<as.length-1; i++)
            if (as[i]!=that.as[i])
                return false;
        return true;
    }


    //
    // halfspace is rectangular if only one coefficent is
    // non-zero
    //
    boolean isRect() {
        boolean nz = false;
        for (int i=0; i<as.length-1; i++) {
            if (as[i]!=0) {
                if (nz) return false;
                nz = true;
            }
        }
        return true;
    }


    //
    // determine whether point satisfies halfspace
    //
    boolean contains(Point p) {
        int [] ps = p.coords();
        int sum = as[rank];
        for (int i=0; i<rank; i++)
            sum += as[i]*ps[i];
        return sum <= 0;
    }

    //
    // given
    //    a0*x0 + ... +ar   <=  0
    // complement is
    //    a0*x0 + ... +ar   >   0
    //   -a0*x0 - ... -ar   <   0
    //   -a0*x0 - ... -ar   <= -1
    //   -a0*x0 - ... -ar+1 <=  0
    //
    Halfspace complement() {
        int [] as = new int[rank+1];
        for (int i=0; i<rank; i++)
            as[i] = -this.as[i];
        as[rank] = -this.as[rank]+1;
        return new Halfspace(as);
    }


    //
    // print a halfspace in both matrix and equation form
    //
    public void printInfo(PrintStream ps) {
        ps.printf("[");
        for (int i=0; i<as.length; i++) {
            ps.printf("%4d", as[i]);
            if (i==as.length-2) ps.printf(" |");
        }
        ps.printf(" ]   ");
        printEqn(ps, " ");
        ps.printf("\n");
    }

    //
    // print a halfspace in equation form
    //
    private void printEqn(PrintStream ps, String spc) {
        int sgn = 0;
        boolean first = true;
        for (int i=0; i<as.length-1; i++) {
            if (sgn==0) {
                if (as[i]<0)
                    sgn = -1;
                else if (as[i]>0)
                    sgn = 1;
            }
            int c = sgn*as[i];
            if (c==1) {
                if (first)
                    ps.printf("x%d", i);
                else
                    ps.printf("+x%d", i);
            } else if (c==-1)
                ps.printf("-x%d", i);
            else if (c!=0)
                ps.printf("%+d*x%d ", c, i);
            if (c!=0)
                first = false;
        }
        if (first)
            ps.printf("0");
        if (sgn>0)
            ps.printf("%s<=%s%d", spc, spc, -as[as.length-1]);
        else
            ps.printf("%s>=%s%d", spc, spc, as[as.length-1]);
    }

    public String toString() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        //printInfo(ps);
        printEqn(ps, "");
        return os.toString();
    }
}
