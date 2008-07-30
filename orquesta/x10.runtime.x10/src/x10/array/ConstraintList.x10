package x10.array;

import java.util.Iterator;
import java.util.ArrayList;
import java.util.Collections; // sort
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import java.lang.AssertionError;


class ConstraintList(int rank) extends ArrayList {

    private int axis; // which axis this set of constraints determines min/max for

    ConstraintList(int rank) {
        this.rank = rank;
    }

    ConstraintList(ConstraintList that) {
        this(that.rank);
        this.addAll(that);
    }


    //
    // eliminate redundant parallel constraints. Since constraints
    // with equal coefficients are sorted in increasing order of
    // the constant (which is the least significant part of the
    // key) taking the last of a set of parallel constraints
    // captures the strongest constraint.
    //
    ConstraintList reduce() {
        Collections.sort(this);
        //printInfo(System.out, "unreduced constraints");
        Iterator it = iterator();
        if (!it.hasNext())
            return this;
        ConstraintList result = new ConstraintList(rank);
        Constraint last = (Constraint) it.next();
        while (it.hasNext()) {
            Constraint next = (Constraint) it.next();
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
    // multiplier to obtain a constraint with a kth coefficent of 0
    //
    // the result is a set of constraints that describe the
    // polyhedron that is the projection of the polyhedron
    // described by the original constraints onto a rank()-1
    // dimensional subspace obtained by eliminating axis k
    //
    ConstraintList FME(int k) {
        ConstraintList result = new ConstraintList(rank);
        for (int i=0; i<size(); i++) {
            Constraint iConstraint = (Constraint) get(i);
            int ic = iConstraint.cs[k];
            if (ic==0) {
                result.add(iConstraint);
            } else {
                for (int j=i+1; j<size(); j++) {
                    Constraint jConstraint = (Constraint) get(j);
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

    ConstraintList init(int axis) {
        ConstraintList cl = new ConstraintList(rank);
        Iterator it = iterator();
        while (it.hasNext()) {
            Constraint c = (Constraint) it.next();
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
            Constraint c = (Constraint) it.next();
            c.sum[axis+1] = c.cs[axis]*position + c.sum[axis];
        }
    }

    //
    // constraints of the form a x + b <= 0, where a<0
    // imply x >= -b / a
    //
    int min() {
        int min = java.lang.Integer.MIN_VALUE;
        Iterator it = iterator();
        while (it.hasNext()) {
            Constraint c = (Constraint) it.next();
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
        int max = java.lang.Integer.MAX_VALUE;
        Iterator it = iterator();
        while (it.hasNext()) {
            Constraint c = (Constraint) it.next();
            int a = c.cs[axis];
            if (a > 0) {
                int b = c.sum[axis];
                int m = -b / a;
                if (m < max) max = m;
            }
        }
        return max;
    }

    //
    // support for constructing rectangular regions: determining
    // whether or not a cl is rectangular, and computing min/max along
    // each axis if it is
    //
    // XXX cache these for efficiency during region construction
    // XXX assume constraints have been sorted and reduced - check/enforce that
    //

    boolean isRect() {
        Iterator it = iterator();
        while (it.hasNext()) {
            Constraint c = (Constraint) it.next();
            if (!c.isRect())
                return false;
        }
        return true;
    }

    int rectMin(int axis) {
        Iterator it = iterator();
        while (it.hasNext()) {
            Constraint c = (Constraint) it.next();
            int a = c.cs[axis];
            if (a < 0) {
                assert c.isRect();
                return -c.cs[rank()] / a;
            }
        }
        throw new AssertionError("no a<0");
    }
    
    int rectMax(int axis) {
        Iterator it = iterator();
        while (it.hasNext()) {
            Constraint c = (Constraint) it.next();
            int a = c.cs[axis];
            if (a > 0) {
                assert c.isRect();
                return -c.cs[rank()] / a;
            }
        }
        throw new AssertionError("no a>0");
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
        Iterator it = iterator();
        while (it.hasNext()) {
            ps.printf("    ");
            ((Constraint)it.next()).printInfo(ps);
        }
    }
}            


