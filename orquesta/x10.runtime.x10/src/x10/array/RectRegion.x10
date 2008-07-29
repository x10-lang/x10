package x10.array;

import java.util.Iterator;

import x10.lang.Region;
import x10.lang.Point;

class RectRegion extends PolyRegion implements Region.Scanner, Layout {

    private int [] min;
    private int [] max;
    private int [] delta;

    private boolean zeroBased;
    private int size;

    RectRegion(int [] min, int [] max) {

        super(min.length);

        if (max.length!=min.length)
            throw U.illegal("min and max must have same length");

        this.min = min;
        this.max = max;

        zeroBased = true;
        for (int i=0; i<min.length; i++)
            if (min[i]!=0)
                zeroBased = false;

        size = 1;
        delta = new int[min.length];
        for (int i=0; i<min.length; i++) {
            delta[i] = max[i] - min[i] + 1;
            size *= delta[i];
        }

        // add constraints
        for (int i=0; i<rank; i++) {
            addConstraint(ZERO+X(i), GE, min[i]);
            addConstraint(ZERO+X(i), LE, max[i]);
        }
        endConstraints();

    }

    RectRegion(int min, int max) {
        this(new int [] {min}, new int [] {max});
    }


    //
    //
    //

    public boolean isRect() {
        return true;
    }

    public boolean isZeroBased() {
        return zeroBased;
    }

    public int size() {
        return size;
    }


    //
    // RectRegion implements a rectangular layout
    //
    // ((i0-min0)*(delta1)+(i1-min1))*(delta2)+(i2-min2);
    //

    public Point coord(int offset) {
        int [] cs = new int[rank];
        for (int i=rank-1; i>0; i--) {
            cs[i] = offset%delta[i] + min[i];
            offset /= delta[i];
        }
        cs[0] = offset + min[0];
        return Point.make(cs);
    }

    public int offset(Point coord) {
        assert coord.rank==rank;
        int [] cs = coord.coords();
        int offset = cs[0] - min[0];
        for (int i=1; i<rank; i++) {
            offset = offset*delta[i] + cs[i] - min[i];
        }
        return offset;
    }

    public int offset(int i0) {
        int offset = i0 - min[0];
        return offset;
    }

    public int offset(int i0, int i1) {
        int offset = i0 - min[0];
        offset = offset*delta[1] + i1 - min[1];
        return offset;
    }

    public int offset(int i0, int i1, int i2) {
        int offset = i0 - min[0];
        offset = offset*delta[1] + i1 - min[1];
        offset = offset*delta[2] + i2 - min[2];
        return offset;
    }



    //
    // scanner
    //

    protected Region.Scanner scanner() {
        return this;
    }

    public void set(int axis, int position) {
        // no-op
    }

    public int min(int axis) {
        return min[axis];
    }

    public int max(int axis) {
        return max[axis];
    }


    //
    // region operations
    //

    public Region boundingBox() {
        return this;
    }

}
