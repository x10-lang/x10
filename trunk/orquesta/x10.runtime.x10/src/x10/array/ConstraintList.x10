package x10.array;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import x10.util.Iterator_Constraint;


class ConstraintList(int rank) {

    private Constraint [] constraints = new Constraint[4];
    private int nconstraints = 0;

    ConstraintList(int rank) {
        this.rank = rank;
    }


    class Iterator implements Iterator_Constraint {
        
        private int i = 0;
        
        public boolean hasNext() {
            return i<nconstraints;
        }
        
        public Constraint next() {
            return constraints[i++];
        }
    }


    Iterator_Constraint iterator() {
        return new Iterator();
    }

    void add(Constraint c) {
        if (nconstraints==constraints.length) {
            Constraint [] x = new Constraint[constraints.length*2];
            for (int i=0; i<nconstraints; i++)
                x[i] = constraints[i];
            constraints = x;
        }
        constraints[nconstraints++] = c;
    }
            

    //
    // a simple mechanism of somewhat dubious utility to allow
    // semi-symbolic specification of constraints. For example
    // X0-Y1 >= n is specified as addConstraint(ZERO+X(0)-Y(1), GE, n)
    //

    static final int ZERO = 0xAAAAAAA;

    static final int GE = 0;
    static final int LE = 1;

    static final int X(int axis) {
        return 0x1<<2*axis;
    }

    void add(int coeff, int op, int k) {
        int [] cs = new int[rank+1];
        for (int i=0; i<rank; i++) {
            int c = (coeff&3) - 2;
            cs[i] = op==LE? c : - c;
            coeff = coeff >> 2;
        }
        cs[rank] = op==LE? -k : k;
        add(new Constraint(cs));
    }


    //
    //
    //

    private void sort() {
        qsort(constraints, 0, nconstraints-1);
    }

    private void qsort(Comparable [] a, int lo, int hi) {
        if (hi <= lo) return;
        int l = lo - 1;
        int h = hi;
        while (true) {
            while (a[++l].compareTo(a[hi])<0);
            while (a[hi].compareTo(a[--h])<0 && h>lo);
            if (l >= h) break;
            exch(a, l, h);
        }
        exch(a, l, hi);
        qsort(a, lo, l-1);
        qsort(a, l+1, hi);
    }

    private void exch(Comparable [] a, int i, int j) {
        Comparable temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }



    //
    // eliminate redundant parallel constraints. Since constraints
    // with equal coefficients are sorted in increasing order of
    // the constant (which is the least significant part of the
    // key) taking the last of a set of parallel constraints
    // captures the strongest constraint.
    //
    ConstraintList reduce() {
        sort();
        Iterator_Constraint it = iterator();
        if (!it.hasNext())
            return this;
        ConstraintList result = new ConstraintList(rank);
        Constraint last = it.next();
        while (it.hasNext()) {
            Constraint next = it.next();
            if (!next.isParallel(last))
                result.add(last);
            last = next;
        }
        result.add(last);
        return result;
    }


    //
    // apply Fourier-Motzkin Elimination to eliminate variable k:
    //
    // copy each constraint whose kth coefficient is already 0
    //
    // for each pair of constraints such that the kth coefficient
    // is of opposite sign, construct a new constraint by adding
    // the two original constraints with appropriate positive
    // multipliers to obtain a constraint with a kth coefficent of 0
    //
    // the result is a set of constraints that describe the
    // polyhedron that is the projection of the polyhedron
    // described by the original constraints onto a rank-1
    // dimensional subspace obtained by eliminating axis k
    //
    ConstraintList FME(int k) {
        ConstraintList result = new ConstraintList(rank);
        for (int i=0; i<nconstraints; i++) {
            Constraint iConstraint = constraints[i];
            int ic = iConstraint.cs[k];
            if (ic==0) {
                result.add(iConstraint);
            } else {
                for (int j=i+1; j<nconstraints; j++) {
                    Constraint jConstraint = constraints[j];
                    int jc = jConstraint.cs[k];
                    int [] cs = new int[rank+1];
                    if (ic>0 && jc<0) {
                        for (int l=0; l<=rank; l++)
                            cs[l] = ic*jConstraint.cs[l] - jc*iConstraint.cs[l];
                    } else if (ic<0 && jc>0) {
                        for (int l=0; l<=rank; l++)
                            cs[l] = jc*iConstraint.cs[l] - ic*jConstraint.cs[l];
                    }
                    boolean degenerate = true;
                    for (int l=0; l<rank; l++)
                        if (cs[l]!=0)
                            degenerate = false;
                    if (!degenerate) {
                        Constraint c = new Constraint(cs);
                        result.add(c);
                    }
                }
            }
        }
        result = result.reduce();
        return result;
    }


    //
    // scanner support
    // 
    // no longer used - now done in PolyScanner
    // keep this for reference for now
    //

    /*
    ConstraintList init(int axis) {
        ConstraintList cl = new ConstraintList(rank);
        Iterator it = iterator();
        while (it.hasNext()) {
            Constraint c = it.next();
            if (c.cs[axis]!=0) {
                c.sum[0] = c.cs[rank];
                cl.add(c);
            }
        }
        cl.axis = axis;
        return cl;
    }

    void set(int axis, int position) {
        Iterator it = iterator();
        while (it.hasNext()) {
            Constraint c = it.next();
            c.sum[axis+1] = c.cs[axis]*position + c.sum[axis];
        }
    }


    // XXX should get these from Integer but they are missing from
    // x10.lang.Integer in the Java runtime so just put them here
    final static int MAX_VALUE = 2147483647;
    final static int MIN_VALUE = -2147483648;


    //
    // constraints of the form a x + b <= 0, where a<0
    // imply x >= -b / a
    //
    int min() {
        int min = Integer.MIN_VALUE;
        Iterator_Constraint it = iterator();
        while (it.hasNext()) {
            Constraint c = it.next();
            int a = c.cs[axis];
            if (a < 0) {
                int b = c.sum[axis];
                int m = -b / a;
                if (m > min) min = m;
            }
        }
        return min;
    }

    //
    // constraints of the form a x + b <= 0, where a>0
    // imply x <= -b / a
    //
    int max() {
        int max = Integer.MAX_VALUE;
        Iterator_Constraint it = iterator();
        while (it.hasNext()) {
            Constraint c = it.next();
            int a = c.cs[axis];
            if (a > 0) {
                int b = c.sum[axis];
                int m = -b / a;
                if (m < max) max = m;
            }
        }
        return max;
    }
    */


    //
    // support for constructing rectangular regions: determining
    // whether or not a cl is rectangular, and computing min/max along
    // each axis if it is
    //
    // XXX cache these for efficiency during region construction
    // XXX assume constraints have been sorted and reduced - check/enforce
    // XXX rectMin/Max only work if isRect is true - check/enforce
    //

    boolean isRect() {
        Iterator_Constraint it = iterator();
        while (it.hasNext()) {
            Constraint c = it.next();
            if (!c.isRect())
                return false;
        }
        return true;
    }

    int rectMin(int axis) {
        Iterator_Constraint it = iterator();
        while (it.hasNext()) {
            Constraint c = it.next();
            int a = c.cs[axis];
            if (a < 0) {
                assert c.isRect();
                return -c.cs[rank()] / a;
            }
        }
        String msg = "axis " + axis + " has no minimum";
        throw new UnboundedRegionException(msg);
    }
    
    int rectMax(int axis) {
        Iterator_Constraint it = iterator();
        while (it.hasNext()) {
            Constraint c = it.next();
            int a = c.cs[axis];
            if (a > 0) {
                assert c.isRect();
                return -c.cs[rank()] / a;
            }
        }
        String msg = "axis " + axis + " has no maximum";
        throw new UnboundedRegionException(msg);
    }

    int [] rectMin() {
        int [] min = new int[rank];
        for (int i=0; i<min.length; i++)
            min[i] = rectMin(i);
        return min;
    }

    int [] rectMax() {
        int [] max = new int[rank];
        for (int i=0; i<max.length; i++)
            max[i] = rectMax(i);
        return max;
    }

    boolean isZeroBased() {
        if (!isRect())
            return false;
        for (int i=0; i<rank; i++)
            if (rectMin(i)!=0)
                return false;
        return true;
    }


    //
    //
    //

    public void printInfo(PrintStream ps, String label) {
        ps.printf("%s\n", label);
        Iterator_Constraint it = iterator();
        while (it.hasNext()) {
            ps.printf("    ");
            ((Constraint)it.next()).printInfo(ps);
        }
    }
}            


