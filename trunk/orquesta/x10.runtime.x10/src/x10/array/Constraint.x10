package x10.array;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


//
// This class represents a single polyhedral constraint of the form
//
//     cs[0]*x0 + cs[1]*x1 + ... + constant <= 0
//
// The cs are stored in the first rank elements of the cs[] array; the
// constant is stored in cs[rank()] (using homogeneous coordinates).
//
// XXX shouldn't be public - temp hack to support Iterator_Constraint
//

public value class Constraint implements java.lang.Comparable {

    int [] cs;

    private int rank;

    Constraint(int [] cs) {
        this.cs = cs;
        this.rank = cs.length-1;
    }

    //
    // natural sort order for constraints: from lo to hi on each
    // axis, from most major to least major axis, with constant as
    // least siginficant part of key
    //
    public int compareTo(java.lang.Object t) {
        Constraint that = (Constraint) t;
        for (int i=0; i<cs.length; i++) {
            if (cs[i] < that.cs[i])
                return -1;
            else if (cs[i] > that.cs[i])
                return 1;
        }
        return 0;
    }


    //
    // two constraints are parallel if all coefficients are the
    // same; constants may differ
    //
    // XXX only right if first coefficients are the same; needs to
    // allow for multiplication by positive constant
    //
    boolean isParallel(Constraint that) {
        for (int i=0; i<cs.length-1; i++)
            if (cs[i]!=that.cs[i])
                return false;
        return true;
    }


    //
    // constraint is rectangular if only one coefficent is
    // non-zero
    //
    boolean isRect() {
        boolean nz = false;
        for (int i=0; i<cs.length-1; i++) {
            if (cs[i]!=0) {
                if (nz) return false;
                nz = true;
            }
        }
        return true;
    }


    //
    // determine whether point satisfies constraint
    //
    boolean contains(Point p) {
        int [] ps = p.coords();
        int sum = cs[rank];
        for (int i=0; i<rank; i++)
            sum += cs[i]*ps[i];
        return sum <= 0;
    }

    //
    // print a constraint in both matrix and equation form
    //
    public void printInfo(PrintStream ps) {
        ps.printf("[");
        for (int i=0; i<cs.length; i++) {
            ps.printf("%4d", cs[i]);
            if (i==cs.length-2) ps.printf(" |");
        }
        ps.printf(" ]   ");
        int sgn = 0;
        boolean first = true;
        for (int i=0; i<cs.length-1; i++) {
            if (sgn==0) {
                if (cs[i]<0)
                    sgn = -1;
                else if (cs[i]>0)
                    sgn = 1;
            }
            int c = sgn*cs[i];
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
        if (sgn>0)
            ps.printf(" <= %d", -cs[cs.length-1]);
        else
            ps.printf(" >= %d", cs[cs.length-1]);
        ps.printf("\n");
    }


    public String toString() {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(os);
        printInfo(ps);
        return os.toString();
    }
}
