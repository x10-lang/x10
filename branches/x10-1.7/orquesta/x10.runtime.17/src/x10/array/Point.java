/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Language.
 *
 */
package x10.array;

import x10.core.Rail;
import x10.core.ValRail;
import x10.core.Value;
import x10.types.Type;
import x10.types.Types;

public class Point extends Value implements x10.core.Indexable<Integer,java.lang.Integer>, Comparable<Point>
{
    public final int rank;
    final int[] val;
    final int hash_;

    public final int rank() {return rank; }

    public static Point makeFromVarRail(Rail<Integer> rail) {
        // make a copy of the array
        int[] val = rail.getIntArray();
        int[] copy = new int[val.length];
        System.arraycopy(val, 0, copy, 0, copy.length);
        return new Point(copy);
    }

    public static Point makeFromValRail(ValRail<Integer> rail) {
        // do not copy the array--the type system ensures we won't write to
        // it
        return new Point(rail.getIntArray());
    }

    public static Point makeFromVarArgs(int... elements) {
        return new Point(elements);
    }

    public static Point makeFromJavaArray(int[] elements) {
        return new Point(elements);
    }

    public static Point makeConstant(int rank, int c) {
        int[] a = new int[rank];
        for (int i = 0; i < rank; i++) {
            a[i] = c;
        }
        Point p = new Point(a);
        return p;
    }

    public static Point makeZero(int rank) {
        Point p = new Point(new int[rank]);
        return p;
    }


    /**
     * Is this indexable value-equals to the other indexable?
     * 
     * @param other
     * @return true if these objects are value-equals
     */
    public boolean equals(Object other) {
        if (! (other instanceof Point))
            return false;
        Point op = (Point) other;
        if ((op.hash_ == hash_) && (op.val.length == val.length)) {
            for (int i = val.length-1; i >= 0; i--)
                if (val[i] != op.val[i])
                    return false;
            return true;
        } else
            return false;
    }

    public int[] val() { return val; }

    public Point(int[] val) {
        this.rank = val.length;
        this.val = val;
        this.hash_ = hash(val);
    }

    private int hash(int[] val) {
        // compute hash
        int b = 378551;
        int a = 63689;
        int hash_tmp = 0;
        for (int i = 0; i < val.length; i++) {
            hash_tmp = hash_tmp * a + val[i];
            a = a * b;
        }
        return hash_tmp;
    }

    public Point(int rank) {
        this.rank = rank;
        this.val = new int[rank];
        this.hash_ = hash(this.val);
    }


    /**
     * Return the value of this AbstractPoint on the i'th dimension.
     */
    public int get(int i) {
        return val[i];
    }
    
    /**
     * Return the value of this AbstractPoint on the i'th dimension.
     */
    public Integer apply(Integer i) {
        return val[i];
    }

    public int hashCode() {
        return hash_;
    }

    /* lexicographical ordering */
    public int compareTo(Point o) {
        Point tmp = o;
        if (tmp.rank != rank)
            throw new RankMismatchException(tmp, rank);

        int res = 0;
        // row major ordering (C conventions)
        for (int i = 0; res == 0 && i < rank; ++i) {
            int t1 = val[i], t2 = tmp.val[i];
            if (t1 < t2)
                res = -1;
            else if (t1 > t2)
                res = 1;
        }
        return res;
    }

    public boolean gt(Point p) {
        return compareTo(p) == 1;
    }
    public boolean lt(Point p) {
        return compareTo(p) == -1;
    }
    public boolean ge(Point p) {
        return compareTo(p) >= 0;
    }
    public boolean le(Point p) {
        return compareTo(p) <= 0;
    }

    public Point neg() {
        int array[] = new int[val.length];
        for (int i = 0; i < val.length; i++)
            array[i] = -val[i];
        return new Point(array);
    }

    public Point add(Point p) {
        if (val.length != p.rank)
            throw new RankMismatchException(p, rank);
        int array[] = new int[val.length];
        for (int i = 0; i < val.length; i++)
            array[i] = val[i] + p.get(i);
        return new Point(array);
    }

    public Point sub(Point p) {
        if (val.length != p.rank)
            throw new RankMismatchException(p, rank);
        int array[] = new int[val.length];
        for (int i = 0; i < val.length; i++)
            array[i] = val[i] - p.get(i);
        return new Point(array);
    }

    public Point mul(Point p) {
        if (val.length != p.rank)
            throw new RankMismatchException(p, rank);
        int array[] = new int[val.length];
        for (int i = 0; i < val.length; i++)
            array[i] = val[i] * p.get(i);
        return new Point(array);
    }

    public Point div(Point p) {
        if (val.length != p.rank)
            throw new RankMismatchException(p, rank);
        int array[] = new int[val.length];
        // The loop below may also throw an ArithmeticException
        for (int i = 0; i < val.length; i++)
            array[i] = val[i] / p.get(i);
        return new Point(array);
    }

    public Point mod(Point p) {
        if (val.length != p.rank)
            throw new RankMismatchException(p, rank);
        int array[] = new int[val.length];
        // The loop below may also throw an ArithmeticException
        for (int i = 0; i < val.length; i++)
            array[i] = val[i] % p.get(i);
        return new Point(array);
    }

    public Point add(int c) {
        int array[] = new int[val.length];
        for (int i = 0; i < val.length; i++)
            array[i] = val[i] + c;
        return new Point(array);
    }

    public Point mul(int c) {
        int array[] = new int[val.length];
        for (int i = 0; i < val.length; i++)
            array[i] = val[i] * c;
        return new Point(array);
    }

    public Point div(int c) {
        int array[] = new int[val.length];
        // The loop below may also throw an ArithmeticException
        for (int i = 0; i < val.length; i++)
            array[i] = val[i] / c;
        return new Point(array);
    }

    public Point invsub(int c) {
        int array[] = new int[val.length];
        for (int i = 0; i < val.length; i++)
            array[i] = c - val[i];
        return new Point(array);
    }

    public Point invdiv(int c) {
        int array[] = new int[val.length];
        // The loop below may also throw an ArithmeticException
        for (int i = 0; i < val.length; i++)
            array[i] = c / val[i];
        return new Point(array);
    }

    public Point invmod(int c) {
        int array[] = new int[val.length];
        // The loop below may also throw an ArithmeticException
        for (int i = 0; i < val.length; i++)
            array[i] = c % val[i];
        return new Point(array);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < val.length; ++i) {
            sb.append(val[i]);
            if (i < val.length -1)
                sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
    
    //
    // Runtime type information
    //
    
    static public class RTT extends x10.types.RuntimeType<Point> {
        public static final RTT it = new RTT();
        
        public RTT() {
            super(Point.class);
        }

        public boolean instanceof$(java.lang.Object o) {
            if (!(o instanceof Point))
                return false;
            return true;
        }
    }

    public Type<?> rtt_x10$lang$Fun_0_1_Z1() { return Types.INT; }
    public Type<?> rtt_x10$lang$Fun_0_1_U()  { return Types.INT; }

}



