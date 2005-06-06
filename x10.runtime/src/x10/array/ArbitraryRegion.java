/*
 * Created on Jan 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package x10.array;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Set;

import x10.lang.point;
import x10.lang.region;

/**
 * @author Christoph von Praun
 */
public class ArbitraryRegion extends region {

    private final SortedSet points_; 
    
    private ArbitraryRegion(int rank) {
        super(rank);
        rankCache_ = new ArbitraryRegion[rank];
        points_ = new TreeSet();
    }
    
    public ArbitraryRegion(region[] dims) {
        super(dims.length);
        int sz = dims[0].size();
        int sz_total = sz;
        for (int i = 1; i < dims.length; ++ i) {
            assert dims[i].size() == sz;
            assert dims[i].rank == 1;;
        }
        rankCache_ = new ArbitraryRegion[dims.length];
        points_ = new TreeSet();
        permutations_(points_, new int[]{}, dims);
    }
    
    /* create all points in the region that is spawned by the dimensions in var */
    private void permutations_(Set hs, int[] fix, region[] var) {
        if (var.length == 0) {
            point new_point = point.factory.point(ArbitraryRegion.this, fix);
            hs.add(new_point);
        } else {
            region[] var_new = new region[var.length - 1];
            System.arraycopy(var, 1, var_new, 0, var.length - 1);
            region cur_reg = var[0];
            int[] fix_new = new int[fix.length + 1];
            System.arraycopy(fix, 0, fix_new, 0, fix.length);
            for (Iterator it = cur_reg.iterator(); it.hasNext(); ) {
                point tmp = (point) it.next();
                int tmp_i = tmp.get(0);
                fix_new[fix.length] = tmp_i;
                permutations_(hs, fix_new, var_new);
            }
        }
    }
    
    public ArbitraryRegion(int rank, Set points) {
        super(rank);
        assert (points != null);
        
        for (Iterator it = points.iterator(); it.hasNext(); ) {
            java.lang.Object o = it.next();
            assert o instanceof point;
            point p = (point) o;
            assert p.rank == rank;
        }
        rankCache_ = new ArbitraryRegion[rank];
        points_ = new TreeSet();
        points_.addAll(points);
    }
    
    
    /* modifies this region - this method should be only called during the initialization 
     * or creation of an arbitrary region - hence it is private. 
     */
    private void add_(point p) {
        assert p.rank == rank;
        points_.add(p);
    }
    
    /* (non-Javadoc)
     * @see x10.lang.region#size()
     */
    public int size() {
        return points_.size();
    }

    private final ArbitraryRegion[] rankCache_;
    /**
     * @returns a one-dimenasional region (formerly called range)
     */
    public region rank(int index) {
        assert index >= 0;
        assert index < rank;
        ArbitraryRegion ret;
        
        synchronized (this) {
            ret = rankCache_[index];
        }
        if (ret == null) {
            ret = new ArbitraryRegion(1);
            for (Iterator it = iterator(); it.hasNext(); ) {
                point p = (point) it.next();
                point p_onedim = point.factory.point(ret, new int[] {p.get(index)});
                ret.add_(p_onedim);
            }
            // add it to rankCache_ only after it is initialized 
            // race on rankCache possible_!
            synchronized (this) {
                rankCache_[index] = ret;
            }
            
        }
        return ret;
    }

    public boolean isConvex() {
        boolean ret = true;
        if (rank == 1) {
            ret = (high() - low() + 1 == size());
        } else {
            int conv_size = 1;
            for (int i = 0; ret && i < rank; ++i) {
                region r = rank(i);
                ret &= r.isConvex();
                conv_size *= r.size();
            }
            ret &= size() == conv_size;
        }
        return ret;
    }

    /* 
     * method only menaingful for regions of one dimension (aka ranges)
     */
    public int low() throws ArrayIndexOutOfBoundsException {
        assert rank == 1;
        if (points_.isEmpty())
            throw new ArrayIndexOutOfBoundsException();
        
        point p = (point) points_.first(); 
        return p.get(0);
    }

    /* 
     * method only meaningful for regions of one dimension (aka ranges)
     */
    public int high() throws ArrayIndexOutOfBoundsException {
        assert rank == 1;
        if (points_.isEmpty())
            throw new ArrayIndexOutOfBoundsException();
        
        point p = (point) points_.last(); 
        return p.get(0);
    }

    /* (non-Javadoc)
     * @see x10.lang.region#union(x10.lang.region)
     */
    public region union(region r) {
        assert r != null;
        assert r.rank == rank;
        return union(this, r);
    }
    
    protected static region union(region r1, region r2) {
        assert r1.rank == r2.rank;
        
        ArbitraryRegion ret = new ArbitraryRegion(r1.rank);
        for (Iterator it = r1.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            ret.add_(p);
        }
        for (Iterator it = r2.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            ret.add_(p);
        }
        return ret;
    }

    /* 
     */
    public region intersection(region r) {
        assert r != null;
        assert r.rank == rank;
        return intersection(this, r);
    }
    
    protected static region intersection(region r1, region r2) {
        assert r1.rank == r2.rank;
        
        ArbitraryRegion ret = new ArbitraryRegion(r1.rank);
        for (Iterator it = r1.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            if (r2.contains(p))
                ret.add_(p);
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see x10.lang.region#difference(x10.lang.region)
     */
    public region difference(region r) {
        assert r != null;
        assert r.rank == rank;
        return difference(this, r);
    }
    
    protected static region difference(region r1, region r2) {
        ArbitraryRegion ret = new ArbitraryRegion(r1.rank);
        for (Iterator it = r1.iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            if (! r2.contains(p))
                ret.add_(p);
        }
        return ret;
    }

    public region convexHull() {
        region ret;
        if (rank == 1) {
            ret = new ContiguousRange(low(), high());
        } else {
            int[] mins = new int[rank];
            int[] maxs = new int[rank];
            for (int i = 0; i < rank; ++i) {
                region r = rank(i);
                mins[i] = r.low();
                maxs[i] = r.high();
            }
            
            region[] dims = new region[rank];
            for (int i = 0; i < rank; ++i) {
                dims[i] = new ContiguousRange(mins[i], maxs[i]);
            }
            ret = new MultiDimRegion(dims);
        } 
        return ret;
    }

    /* (non-Javadoc)
     * @see x10.lang.region#contains(x10.lang.point)
     */
    public boolean contains(point p) {
        assert p != null;
        assert p.rank == rank;
        
        return points_.contains(p);
    }

    /* (non-Javadoc)
     * @see x10.lang.region#contains(int[])
     */
    public boolean contains(int[] p) {
        assert p != null;
        assert p.length == rank;
        
        boolean ret;
        if (p.length == rank) {
            point pp = point.factory.point(ArbitraryRegion.this, p);
        	ret = contains(pp);
        } else
            ret = false;
        return ret;
    }

    /* (non-Javadoc)
     * @see x10.lang.region#disjoint(x10.lang.region)
     */
    public boolean disjoint(region r) {
        assert r != null;
        assert r.rank == rank;
        return intersection(r).size() == 0;
    }

    /* (non-Javadoc)
     * @see x10.lang.region#ordinal(x10.lang.point)
     */
    public int ordinal(point p) throws ArrayIndexOutOfBoundsException, ArrayIndexOutOfBoundsException {
        assert p != null;
        assert p.rank == rank;
        
        int ret = 0;
        if (size() == 0)
            throw new ArrayIndexOutOfBoundsException();
        if (!contains(p))
            throw new ArrayIndexOutOfBoundsException();
        else {
            for (Iterator it = iterator(); it.hasNext(); ) {
    		    point q = (point) it.next();
    		    if (q.equals(p))
    		        break;
    		    else
    		        ret++;
            }
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see x10.lang.region#coord(int)
     */
    public point coord(int ord) throws ArrayIndexOutOfBoundsException {
        assert ord >= 0;
        
        point ret;
        if (ord >= size())
            throw new ArrayIndexOutOfBoundsException();
        else {
            int ctr = 0;
            Iterator it = iterator();
            while (ctr++ < ord) it.next();
            ret = (point) it.next();
        }
        return ret;
    }

    /* (non-Javadoc)
     * @see x10.lang.region#iterator()
     */
    public Iterator iterator() {
        return points_.iterator();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (Iterator it = iterator(); it.hasNext(); ) {
		    sb.append(it.next().toString());
			if (it.hasNext())
				sb.append(",");
		}
		sb.append("}");
		return sb.toString();
    }





}
