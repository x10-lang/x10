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

import x10.lang.PointOutOfRegionError;
import x10.lang.point;
import x10.lang.region;

/**
 * @author Christoph von Praun
 */
public class ArbitraryRegion extends region {

    private final SortedSet points_; 
    
    private ArbitraryRegion(int rank) {
        super(rank);
        points_ = new TreeSet();
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

    /* returns a one-dimenasional region (formerly called range)
     */
    public region rank(int index) {
        ArbitraryRegion ret = new ArbitraryRegion(1);
        for (Iterator it = iterator(); it.hasNext(); ) {
            point p = (point) it.next();
            point p_onedim = point.factory.point(ArbitraryRegion.this, new int[] {p.valueAt(index)});
            ret.add_(p_onedim);
        }
        return ret;
    }

    /* 
     * TODO cvp->vj
     */
    public boolean isConvex() {
        throw new Error("TODO");
    }

    /* 
     * method only menaingful for regions of one dimension (aka ranges)
     */
    public int low() throws EmptyRegionError {
        assert rank == 1;
        if (points_.isEmpty())
            throw new EmptyRegionError();
        
        point p = (point) points_.first(); 
        return p.valueAt(0);
    }

    /* 
     * method only menaingful for regions of one dimension (aka ranges)
     */
    public int high() throws EmptyRegionError {
        assert rank == 1;
        if (points_.isEmpty())
            throw new EmptyRegionError();
        
        point p = (point) points_.last(); 
        return p.valueAt(0);
    }

    /* (non-Javadoc)
     * @see x10.lang.region#union(x10.lang.region)
     */
    public region union(region r) {
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

    /* 
     * TODO cvp->vj
     */
    public region convexHull() {
        throw new Error("TODO");
    }

    /* (non-Javadoc)
     * @see x10.lang.region#contains(x10.lang.point)
     */
    public boolean contains(point p) {
        assert p != null;
        return points_.contains(p);
    }

    /* (non-Javadoc)
     * @see x10.lang.region#contains(int[])
     */
    public boolean contains(int[] p) {
        assert p != null;
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
        return intersection(r).size() == 0;
    }

    /* (non-Javadoc)
     * @see x10.lang.region#ordinal(x10.lang.point)
     */
    public int ordinal(point p) throws EmptyRegionError, PointOutOfRegionError {
        int ret = 0;
        if (size() == 0)
            throw new EmptyRegionError();
        if (!contains(p))
            throw new PointOutOfRegionError();
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
    public point coord(int ord) throws PointOutOfRegionError {
        point ret;
        if (ord >= size())
            throw new PointOutOfRegionError();
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
