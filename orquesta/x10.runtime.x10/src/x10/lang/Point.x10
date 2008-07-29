package x10.lang;

import x10.array.U;

public class Point implements /*Indexable_int,*/ Arithmetic_Point {


    //
    // properties
    //

    public final int rank;


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
        throw U.unsupported();
    }

    public static Point makeZero(int rank) {
        throw U.unsupported();
    }


    //
    // ops from Arithmetic_Point
    //

    public Point add(Point x) {
        throw U.unsupported();
    }

    public Point sub(Point x) {
        throw U.unsupported();
    }

    public Point mul(Point x) {
        throw U.unsupported();
    }

    public Point div(Point x) {
        throw U.unsupported();
    }

    public Point cosub(Point x) {
        throw U.unsupported();
    }

    public Point codiv(Point x) {
        throw U.unsupported();
    }

    public Point neginv() {
        throw U.unsupported();
    }

    public Point mulinv() {
        throw U.unsupported();
    }

    public boolean eq(Point x) {
        throw U.unsupported();
    }

    public boolean lt(Point x) {
        throw U.unsupported();
    }

    public boolean gt(Point x) {
        throw U.unsupported();
    }

    public boolean le(Point x) {
        throw U.unsupported();
    }

    public boolean ge(Point x) {
        throw U.unsupported();
    }

    public boolean ne(Point x) {
        throw U.unsupported();
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

}
