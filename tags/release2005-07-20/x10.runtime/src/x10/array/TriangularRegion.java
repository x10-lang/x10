package x10.array;

import java.util.Iterator;

import x10.lang.point;
import x10.lang.region;

/**
 * @author Christoph von Praun
 */
public class TriangularRegion extends region {

    private final boolean isLower_;
    private final region[] dims_;
    private final int size_;
    /**
     * @param rank
     */
    public TriangularRegion(region[] dims, boolean is_lower) {
        super(2); // rank must be == 2
        // all regions must be one dimensional and have the same size > 0
        assert (dims != null && dims.length == rank);
        int size = dims[0].size();
        assert (size > 0);
        for (int i = 0; i < dims.length; ++i) {
            assert (dims[i] instanceof ContiguousRange);
            assert (dims[i].size() == size);
        }
        size_ = gauss_(size);
        isLower_ = is_lower;
        dims_ = dims;
    }

    public int size() {
        return size_;
    }
    
    private int gauss_(int n) {
        int ret = (n <= 0) ? 0 : ((n * (n + 1)) / 2);
        // System.out.println("gauss_(" + n +")=" + ret);
        return ret;
    }
    
    private int inv_gauss_(int x) {
        assert (x >= 0);
        int ret = (int) ((Math.sqrt(8 * x + 1.0) - 1.0) / 2.0);
        // System.out.println("inv_gauss_(" + x +")=" + ret);
        return ret;
    }
    
    public region rank(int index) {
        assert index >= 0;
        assert index < rank;
        return dims_[index];
    }

    public boolean isConvex() {
        return true;
    }

    public int low() {
        throw new UnsupportedOperationException("TriangularRegion::low");
    }

    public int high() {
        throw new UnsupportedOperationException("TriangularRegion::high");
    }

    public region union(region r) {
        assert r != null;
        assert r.rank == rank;    
        region ret = ArbitraryRegion.union(this, r);
        if (ret.size() == 0) 
            new EmptyRegion(rank);
        return ret;
    }

    public region intersection(region r) {
        assert r != null;
        assert r.rank == rank;    
        region ret = ArbitraryRegion.intersection(this, r);
        if (ret.size() == 0) 
            new EmptyRegion(rank);
        return ret;
    }

    public region difference(region r) {
        assert r != null;
        assert r.rank == rank;    
        region ret = ArbitraryRegion.difference(this, r);
        if (ret.size() == 0) 
            new EmptyRegion(rank);
        return ret;
    }

    public region convexHull() {
        return this;
    }

    public boolean contains(point p) {
        assert (p != null);
        boolean ret;
        if (p.rank == 2) 
            ret = contains_(p.get(0), p.get(1));
        else
            ret = false;
        return ret;
            
    }

    public boolean contains(int[] p) {
        assert (p != null);
        boolean ret;
        if (p.length == 2)
            ret = contains_(p[0], p[1]);
        else
            ret = false;
        return ret;
    }

    private boolean contains_(int a, int b) {
        int a_normal = a - dims_[0].low();
        int b_normal = b - dims_[1].low();
        boolean ul_criterion = (isLower_) ? (a_normal >= b_normal) : (a_normal <= b_normal);
        boolean size_criterion = 
            dims_[0].contains(new int[] {a}) && 
            dims_[1].contains(new int[] {b});
        return ul_criterion && size_criterion;
    }
    
    public boolean disjoint(region r) {
        assert r != null;
        assert r.rank == rank;
        return intersection(r).size() == 0;
    }

    public int ordinal(point p) throws ArrayIndexOutOfBoundsException {
        assert p != null;
        assert p.rank == rank;        
        int ret;
        if (size() == 0)
            throw new ArrayIndexOutOfBoundsException();
        if (!contains(p))
            throw new ArrayIndexOutOfBoundsException();
        else
            ret =  ordinal_(p.get(0), p.get(1));
        return ret;
    }
    
    private int ordinal_(int a, int b) {
        int a_normal, b_normal;
        if (!isLower_) {
            a_normal = dims_[0].high() - a;
            b_normal = dims_[1].high() - b;
        } else {
            a_normal = a - dims_[0].low();
            b_normal = b - dims_[1].low();
        }
        return gauss_(a_normal) + b_normal;
    }
    
    public point coord(int ord) throws ArrayIndexOutOfBoundsException {
        assert ord >= 0;        
        point ret;
        int x_normal, y_normal;
        int[] tmp;
        if (ord >= size())
            throw new ArrayIndexOutOfBoundsException();
        else {
            x_normal = inv_gauss_(ord);
            y_normal = ord - gauss_(x_normal);
            if (isLower_)
                tmp = new int[] {x_normal + dims_[0].low(), y_normal + dims_[1].low()};                
            else              
                tmp = new int[] {dims_[0].high() - x_normal, dims_[1].high() - y_normal}; 
            ret = point.factory.point(this, tmp);
        }
        return ret;
    }

    private class TriangularRegionIterator_ implements Iterator {
        private int nextOrd_ = 0;
        
        public boolean hasNext() {
            return nextOrd_ < size_;
        }
        
        public void remove() {
            throw new UnsupportedOperationException("TriangularRegionIterator_::remove - not implemented");
        }
        
        public java.lang.Object next() {
            assert hasNext();
            return coord(nextOrd_++);
        }
    }
    
    public Iterator iterator() {
        return new TriangularRegionIterator_();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(isLower_ ? "upper" : "lower");
        sb.append("-triangular(");
        sb.append(dims_[0].toString());
        sb.append(", ");
        sb.append(dims_[1].toString());
        sb.append(")");
        return sb.toString();
    }

}
