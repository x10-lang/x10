package x10.lang;

import x10.array.U;

public class Point(int rank) implements /*Indexable_int,*/ ArithmeticOps_Point, ComparisonOps_Point {


    //
    // basics
    //

    public int get(int i) {
        return coords[i];
    }

    public int [] coords() {
        return coords;
    }


    //
    // factories
    //

    public static Point make(int [] coords) {
        return new Point(coords);
    }

    public static Point makeConstant(int rank, int c) {
        throw U.unsupported("Point.makeConstant");
    }

    public static Point makeZero(int rank) {
        throw U.unsupported("Point.makeZero");
    }


    //
    // from ArithmeticOps_Point
    //

    public Point $plus() {
        return this;
    }

    public Point $minus() {
        int [] cs = new int[rank];
        for (int i=0; i<rank; i++)
            cs[i] = -this.coords[i];
        return Point.make(cs);
    }

    public Point $plus(Point that) {
        chkRank(that);
        int [] cs = new int[rank];
        for (int i=0; i<rank; i++)
            cs[i] = this.coords[i]+that.coords[i];
        return Point.make(cs);
    }

    public Point $minus(Point that) {
        chkRank(that);
        int [] cs = new int[rank];
        for (int i=0; i<rank; i++)
            cs[i] = this.coords[i]-that.coords[i];
        return Point.make(cs);
    }

    public Point $times(Point that) {
        chkRank(that);
        int [] cs = new int[rank];
        for (int i=0; i<rank; i++)
            cs[i] = this.coords[i]*that.coords[i];
        return Point.make(cs);
    }

    public Point $over(Point that) {
        chkRank(that);
        int [] cs = new int[rank];
        for (int i=0; i<rank; i++)
            cs[i] = this.coords[i]/that.coords[i];
        return Point.make(cs);
    }

    //
    // from ComparisonOps_Point
    //

    public boolean $eq(Point that) {
        chkRank(that);
        for (int i=0; i<rank; i++)
            if (!(this.coords[i]==that.coords[i]))
                return false;
        return true;
    }

    public boolean $lt(Point that) {
        chkRank(that);
        for (int i=0; i<rank; i++)
            if (!(this.coords[i]<that.coords[i]))
                return false;
        return true;
    }

    public boolean $gt(Point that) {
        chkRank(that);
        for (int i=0; i<rank; i++)
            if (!(this.coords[i]>that.coords[i]))
                return false;
        return true;
    }

    public boolean $le(Point that) {
        chkRank(that);
        for (int i=0; i<rank; i++)
            if (!(this.coords[i]<=that.coords[i]))
                return false;
        return true;
    }

    public boolean $ge(Point that) {
        chkRank(that);
        for (int i=0; i<rank; i++)
            if (!(this.coords[i]>=that.coords[i]))
                return false;
        return true;
    }

    public boolean $ne(Point that) {
        chkRank(that);
        for (int i=0; i<rank; i++)
            if (!(this.coords[i]!=that.coords[i]))
                return false;
        return true;
    }


    //
    //
    //

    public String toString() {
        String s = "(";
        if (coords.length>0)
            s += coords[0];
        for (int i=1; i<coords.length; i++)
            s += "," + coords[i];
        s += ")";
        return s;
    }


    //
    //
    //

    private int [] coords;

    private Point(int [] coords) {
        this.coords = coords;
        this.rank = coords.length;
    }

    private void chkRank(Point that) {
        if (this.rank!=that.rank)
            throw new RankMismatchException(this.rank, that.rank);
    }



}
