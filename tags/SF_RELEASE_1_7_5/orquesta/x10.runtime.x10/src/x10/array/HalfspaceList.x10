package x10.array;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import x10.util.Iterator_Halfspace;
import x10.util.ArrayList_Halfspace;


public class HalfspaceList(int rank) extends ArrayList_Halfspace {

    public HalfspaceList(:self.rank==rank)(final int rank) {
        this.rank = rank;
    }


    //
    // a simple mechanism of somewhat dubious utility to allow
    // semi-symbolic specification of halfspaces. For example
    // X0-Y1 >= n is specified as addHalfspace(X(0)-Y(1), GE, n)
    //
    // XXX coefficients must be -1,0,+1; can allow larger coefficients
    // by increasing # bits per coeff
    //

    private static final int ZERO = 0xAAAAAAA;

    public static final int GE = 0;
    public static final int LE = 1;

    public static final int X(int axis) {
        return 0x1<<2*axis;
    }

    public void add(int coeff, int op, int k) {
        coeff += ZERO;
        int [] as = new int[rank+1];
        for (int i=0; i<rank; i++) {
            int a = (coeff&3) - 2;
            as[i] = op==LE? a : - a;
            coeff = coeff >> 2;
        }
        as[rank] = op==LE? -k : k;
        add(new Halfspace(as));
    }


    //
    // Eliminate redundant parallel halfspaces. Since halfspaces
    // with equal coefficients are sorted in increasing order of
    // the constant (which is the least significant part of the
    // key) taking the last of a set of parallel halfspaces
    // captures the strongest halfspace.
    //
    HalfspaceList simplifyParallel() {
        sort();
        Iterator_Halfspace it = iterator();
        if (!it.hasNext())
            return this;
        HalfspaceList result = new HalfspaceList(rank);
        Halfspace last = it.next();
        while (it.hasNext()) {
            Halfspace next = it.next();
            if (!next.isParallel(last))
                result.add(last);
            last = next;
        }
        result.add(last);
        return result;
    }


    //
    // Determine whether a halfspace is redundant by reductio ad
    // absurdum: assume the negation of H, and if the result is the
    // empty set then H is implied by the other halfspaces.
    //
    // This is guaranteed to remove redundant halfspaces, but it may
    // be expensive.
    //

    boolean isSimplified = false;

    HalfspaceList simplifyAll() {
        sort(); // not needed but helps w/ debugging to keep in sorted order
        if (isSimplified)
            return this;
        HalfspaceList result = new HalfspaceList(rank);
        boolean [] removed = new boolean[size()];
        for (int i=0; i<size(); i++) {
            Halfspace h = get(i);
            HalfspaceList trial = new HalfspaceList(rank);
            for (int j=0; j<size(); j++)
                if (!removed[j])
                    trial.add(i==j? h.complement() : get(j));
            if (!trial.isEmpty())
                result.add(h);
            else
                removed[i] = true;
        }
        result.isSimplified = true;
        return result;
    }


    //
    // Apply Fourier-Motzkin Elimination to eliminate variable k:
    //
    // Copy each halfspace whose kth coefficient is already 0
    //
    // For each pair of halfspaces such that the kth coefficient is of
    // opposite sign, construct a new halfspace by adding the two
    // original halfspaces with appropriate positive multipliers to
    // obtain a halfspace with a kth coefficent of 0.
    //
    // The result is a set of halfspaces that describe the polyhedron
    // that is the projection of the polyhedron described by the
    // original halfspaces onto a rank-1 dimensional subspace obtained
    // by eliminating axis k
    //
    HalfspaceList FME(int k, boolean simplifyDegenerate) {
        HalfspaceList result = new HalfspaceList(rank);
        for (int i=0; i<size(); i++) {
            Halfspace ih = get(i);
            int ia = ih.as[k];
            if (ia==0) {
                result.add(ih);
            } else {
                for (int j=i+1; j<size(); j++) {
                    Halfspace jh = get(j);
                    int ja = jh.as[k];
                    int [] as = new int[rank+1];
                    if (ia>0 && ja<0) {
                        for (int l=0; l<=rank; l++)
                            as[l] = ia*jh.as[l] - ja*ih.as[l];
                    } else if (ia<0 && ja>0) {
                        for (int l=0; l<=rank; l++)
                            as[l] = ja*ih.as[l] - ia*jh.as[l];
                    }
                    int lim = simplifyDegenerate? rank : rank+1;
                    boolean degenerate = true;
                    for (int l=0; l<lim; l++)
                        if (as[l]!=0)
                            degenerate = false;
                    if (!degenerate) {
                        Halfspace h = new Halfspace(as);
                        result.add(h);
                    }
                }
            }
        }
        result = result.simplifyParallel();
        return result;
    }


    //
    // Support for constructing rectangular regions: determining
    // whether or not a cl is rectangular, and computing min/max along
    // each axis if it is.
    //
    // XXX cache these for efficiency during region construction
    // XXX assume halfspaces have been sorted and simplified - check/enforce
    // XXX rectMin/Max only work if isRect is true - check/enforce
    // XXX cache rectMin/rectMax/isZeroBased for performance

    boolean isRect() {
        Iterator_Halfspace it = iterator();
        while (it.hasNext()) {
            Halfspace h = it.next();
            if (!h.isRect())
                return false;
        }
        return true;
    }

    int rectMin(int axis) {
        Iterator_Halfspace it = iterator();
        while (it.hasNext()) {
            Halfspace h = it.next();
            int a = h.as[axis];
            if (a < 0)
                return -h.as[rank()] / a;
        }
        String msg = "axis " + axis + " has no minimum";
        throw new UnboundedRegionException(msg);
    }
    
    int rectMax(int axis) {
        Iterator_Halfspace it = iterator();
        while (it.hasNext()) {
            Halfspace h = it.next();
            int a = h.as[axis];
            if (a > 0)
                return -h.as[rank()] / a;
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
        try {
            for (int i=0; i<rank; i++)
                if (rectMin(i)!=0)
                    return false;
        } catch (UnboundedRegionException e) {
            return false;
        }
        return true;
    }

    boolean isBounded() {
        try {
            for (int i=0; i<rank; i++) {
                rectMin(i);
                rectMax(i);
            }
        } catch (UnboundedRegionException e) {
            return false;
        }
        return true;
    }



    //
    // A set of halfspaces is empty iff, after eliminating all
    // variables with FME, we are left with a contradiction, i.e. a
    // halfspace k<=0 where k>0.
    //
    boolean isEmpty() {

        // eliminate all variables
        HalfspaceList hl = this;
        for (int i=0; i<rank; i++)
            hl = hl.FME(i, false);
    
        // look for contradictions
        for (int i=0; i<hl.size(); i++) {
            Halfspace h = hl.get(i);
            if (h.as[rank]>0)
                return true;
        }
        return false;
    }


    //
    //
    //

    public void printInfo(PrintStream ps, String label) {
        ps.printf("%s\n", label);
        Iterator_Halfspace it = iterator();
        while (it.hasNext()) {
            ps.printf("    ");
            ((Halfspace)it.next()).printInfo(ps);
        }
    }

    public String toString() {
        String s = "(";
        Iterator_Halfspace it = iterator();
        boolean first = true;
        while (it.hasNext()) {
            Halfspace h = it.next();
            if (!first) s += " && ";
            s += h.toString();
            first = false;
        }
        s += ")";
        return s;
    }
}            


