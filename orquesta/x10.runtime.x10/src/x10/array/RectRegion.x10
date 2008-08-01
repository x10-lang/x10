package x10.array;

import x10.lang.Region;
import x10.lang.Point;

final class RectRegion extends PolyRegion implements Region.Scanner {

    private final int [] min;            // cached bounds for efficiency
    private final int [] max;            // cached bounds for efficiency

    private int size;


    RectRegion(ConstraintList cl) {
        
        super(cl);

        this.min = constraints.rectMin();
        this.max = constraints.rectMax();

        // XXX PolyRegion.size()???
        size = 1;
        for (int i=0; i<rank; i++)
            size *= max[i] - min[i] + 1;
    }

    static RectRegion make(int [] min, int [] max) {

        if (max.length!=min.length)
            throw U.illegal("min and max must have same length");

        ConstraintList cl = new ConstraintList(min.length);
        for (int i=0; i<min.length; i++) {
            addConstraint(cl, ZERO+X(i), GE, min[i]);
            addConstraint(cl, ZERO+X(i), LE, max[i]);
        }

        return new RectRegion(cl);
    }


    static RectRegion make(int min, int max) {
        return make(new int [] {min}, new int [] {max});
    }

    public int size() {
        return size;
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

    int [] min() {
        return min;
    }

    int [] max() {
        return max;
    }

}
