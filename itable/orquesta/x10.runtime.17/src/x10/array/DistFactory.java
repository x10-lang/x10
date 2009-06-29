package x10.array;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import x10.runtime.Place;
import x10.runtime.Runtime;

public class DistFactory {

    public static final Dist UNIQUE = makeUnique(Runtime.placeSet());

    public static
    /*(region R)*/ Dist/*(R)*/ makeBlock(final Region base, final Region[] R) {
            final Dist/*(R)*/ result = makeBlock/*(R)*/(base, R, Runtime.placeSet());
            //assert result.region.equals(R);
            return result;
    }

    /**
     * Returns the cyclic distribution over the given region, and over
     * all places.
     */
    public static /*(region R)*/ Dist/*(R)*/ makeCyclic(final Region R) {
            /*final*/ Dist result = makeCyclic/*(R)*/(R, Runtime.placeSet());
            assert result.region.equals(R);
            return result;
    }

    /**
     * Create a Distribution where the elements in the Region_c are
     * distributed over all Places in p in a cyclic manner,
     * that is the next Point in the Region_c is at the next Place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public static Dist makeCyclic(Region r, Set<Place> qs) {
        assert r.rank > 0;
        final int dim_to_split = r.rank - 1;
        int sz = r.rank(dim_to_split).size();
        //if (sz % qs.size() != 0)
        //  throw new Error("DistributionFactory::cyclic - only supported if least significant dimension has a size that is a multiple of the number of places to cycle over");
        //else
        return makeBlockCyclic(r, 1, qs);
    }

    public static Dist makeBlockCyclic(Region r, /*nat*/int p) {
        return makeBlockCyclic(r, p, Runtime.placeSet());
    }

    /**
     * Create a Distribution where the elements in the Region_c are
     * distributed over all Places in p in a cyclic manner,
     * that is the next Point in the Region_c is at the next Place
     * for a cyclic ordering of the given places.
     * @param r
     * @return
     */
    public static Dist makeBlockCyclic(Region r, /*nat*/int n, Set<Place> qs) {
        Dist ret;
        assert n > 0;
        assert r.rank > 0;

        final boolean dim_splittable = r instanceof ContiguousRange || r instanceof MultiDimRegion;
        final int dim_to_split = r.rank - 1;
        int sz = r.rank(dim_to_split).size();
        int qsize = qs.size();
        assert (qsize > 0);

        //if (sz % (n * qsize) != 0)
        //  throw new Error("DistributionFactory::blockCyclic - only supported if least significant dimension has a size that is a multiple of the number of places to cycle over");
        Place[] q = qs.toArray(new Place[qs.size()]);
        // actually qs should be a sorted set of places - here we sort the in global order 
        assert (q[0] instanceof Comparable);

        Arrays.sort(q);
        if (qsize == 1) {
            Place p = (Place) q[0];
            ret = new Distribution_c.Constant(r, p);
        }
        else if (sz % (n * qsize) == 0 && dim_splittable) {
            // partitioning done along dimension dim_to_split
            int chunks = (sz % n == 0 || sz < n) ? (sz / n) : ((sz / n) + 1);
            assert chunks > 0;
            Distribution_c[] dists = new Distribution_c[chunks];
            Region[] sub = r.partition(chunks, dim_to_split);
            for (int i = 0; i < chunks; i++) {
                dists[i] = new Distribution_c.Constant(sub[i], (Place) q[i % q.length]);
            }
            ret = new Distribution_c.Combined(r, dists, q.length == 1 ? (Place) q[0] : null);

        }
        else if (sz == n && dim_to_split > 0 && qsize == r.rank(dim_to_split - 1).size()) {
            // blocking entire rows
            // currently only support it if virtual mapping function is a simple offset ie
            // number of rows must equal number of places

            int adjustment = 0;
            // FIXME n should be == num of places, but if x10c config differs from x10
            // then it won't be
            int adjustmentOffset[] = new int[Runtime.places().length];
            int chunks = r.rank(dim_to_split - 1).size();
            ;
            Distribution_c[] dists = new Distribution_c[chunks];
            Region[] sub = r.partition(chunks, dim_to_split - 1);//split along rows
            for (int i = 0; i < chunks; ++i) {
                int placeId = ((Place) q[i % chunks]).id;
                adjustmentOffset[placeId] = adjustment;

                //System.err.println("set adjustment to:"+adjustment);
                adjustment += sub[i].size();
                dists[i] = new Distribution_c.Constant(sub[i], (Place) q[i % chunks]);
            }
            ret = new Distribution_c.Combined(r, dists, q.length == 1 ? (Place) q[0] : null);

        }
        else {
            ret = blockCyclicHelper_(r, n, q);
        }

        return ret;
    }

    private static Distribution_c.Arbitrary blockCyclicHelper_(Region r, int bf, Place[] places) {
        assert bf > 0;
        HashMap<Point, Place> hm = new HashMap<Point, Place>();
        int offset = 0;
        for (Iterator<Point> it = r.iterator(); it.hasNext();) {
            Point p = (Point) it.next();
            hm.put(p, places[(offset / bf) % places.length]);
            offset++;
        }
        Distribution_c.Arbitrary ret = new Distribution_c.Arbitrary(r, hm, places.length == 1 ? (Place) places[0] : null);

        return ret;
    }

    public static Dist makeRandom(Region r) {
        return makeCyclic(r);
    }
    
    public static Dist makeLocal(Region R) {
        return makeConstant(R, Runtime.here());
    }
    
    /**
     * Return the distribution that maps the region 0..k-1 to here. 
     *
     * @param k -- the upper bound of the 1-dimensional region. k >= 0
     * @return the given distribution
     */
    public static Dist/*(:rank=1)*/ makeLocal(int k) {
            return makeLocal(RegionFactory.makeRect(0, (k-1)));
    }

    /**
     * Create a Distribution where the given Region is distributed
     * into blocks the specified places
     * @param r Region
     * @param q The set of Places
     * @return
     */
    public static Dist makeBlock(Region r, Set<Place> q) {
        return makeBlock(r, q.size(), q);
    }

    /**
     * Create a Distribution where the given tiled region r is distributed
     * into blocks over the given q places. 
     * For now we require the programmer to provide the base region, base,
     * which is intended to be split into r as well. It is the 
     * programmer's responsibility to ensure that r partitions base.
     * @param r
     * @return
     */
    public static Dist makeBlock(Region base, Region[] r, Set<Place> q) {
        return makeBlock(base, r, q.size(), q);
    }

    public static Dist makeBlock(Region base, Region[] r, int n, Set<Place> qs) {
        assert (n > 0);
        if (r.length != n)
            throw new Error("Not implemented yet.");
        Distribution_c[] dists = new Distribution_c[n];
        Object[] q = qs.toArray();

        for (int i = 0; i < n; i++) {
            dists[i] = new Distribution_c.Constant(r[i], (Place) q[i]);
        }
        return new Distribution_c.Combined(base, dists, null);
    }

    /**
     * Create a Distribution where the given Region is distributed
     * into blocks over the first n Places.
     * @param r
     * @return
     */
    public static Dist makeBlock(Region r, int n, Set<Place> qs) {
        assert n <= qs.size();
        assert n > 0;

        final boolean dim_splittable = r instanceof ContiguousRange || r instanceof MultiDimRegion;
        final int dim_to_split = 0; //r.rank - 1;
        Dist ret = null;
        int sz = r.rank(dim_to_split).size();

        //if (sz < n)
        // throw new Error("DistributionFactory::block - blocking only supported along the most significant dimension and blocking factor must be lower than or equal to the size of that dimension.");

        Place[] q = qs.toArray(new Place[qs.size()]);
        //      actually qs should be a sorted set of places - here we sort the in global order 
        assert (q[0] instanceof Comparable);

        Arrays.sort(q);
        if (n == 1) {
            Place p = (Place) q[0];
            ret = new Distribution_c.Constant(r, p);
        }
        else if (sz >= n && sz % n == 0 && dim_splittable) {
            // partition along dimension dim_to_split           
            Region sub[] = r.partition(n, dim_to_split);

            Distribution_c[] dists = new Distribution_c[n];
            int adjustment = 0;
            // FIXME n should be == num of places, but if x10c config differs from x10
            // then it won't be
            int adjustmentOffset[] = new int[Runtime.places().length];
            for (int i = 0; i < n; i++) {
                int placeId = ((Place) q[i]).id;
                adjustmentOffset[placeId] = adjustment;

                //System.err.println("set adjustment to:"+adjustment);
                adjustment += sub[i].size();
                dists[i] = new Distribution_c.Constant(sub[i], (Place) q[i]);
            }
            ret = new Distribution_c.Combined(r, dists, n == 1 ? (Place) q[0] : null);

        }
        else {

            ret = blockHelper_(r, n, q);
        }

        return ret;
    }

    private static Distribution_c.Arbitrary blockHelper_(Region r, int nb, Place[] places) {
        assert nb > 0 && nb <= places.length;
        int total_points = r.size();
        int p = total_points / nb;
        int q = total_points % nb;

        int adjustment = 0;
        int adjustmentOffset[] = new int[Runtime.places().length];

        HashMap hm = new HashMap();
        int offsWithinPlace = 0;
        int blockNum = 0;
        for (Iterator it = r.iterator(); it.hasNext();) {
            Point pt = (Point) it.next();
            hm.put(pt, places[blockNum]);
            offsWithinPlace++;
            ++adjustment;
            if (offsWithinPlace == (p + ((blockNum < q) ? 1 : 0))) {
                offsWithinPlace = 0; // start next block
                blockNum++;
                if (blockNum < places.length)
                    adjustmentOffset[blockNum] = adjustment;
            }
        }
        Distribution_c.Arbitrary ret = new Distribution_c.Arbitrary(r, hm, places.length == 1 ? (Place) places[0] : null);

        return ret;
    }

    /**
     * Create a Distribution where the points of the Region are
     * distributed randomly over all available Places.
     * @param r
     * @return
     */
    public static Dist makeArbitrary(Region r) {
        Dist ret;
        int blocksize = r.size() / x10.runtime.Runtime.places().length;
        if (blocksize == 0)
            ret = makeConstant(r, x10.runtime.Runtime.places()[0]);
        else
            ret = makeBlockCyclic(r, blocksize, x10.runtime.Runtime.placeSet());
        return ret;
    }

    /**
     * Create a Distribution where all points in the given
     * Region are mapped to the same Place.
     * @param r
     * @param p specifically use the given Place for all points
     * @return
     */
    public static Dist makeConstant(Region r, Place p) {
        // initialized to zero
        int adjustmentOffset[] = new int[Runtime.places().length];

        Dist newDist = new Distribution_c.Constant(r, p);
        return newDist;
    }

    /**
     * Create a Distribution where the points in the
     * Region_c 1...p.length are mapped to the respective
     * places.
     * @param p the list of places (implicitly defines the Region_c)
     * @return
     */
    public static Dist makeUnique(Set<Place> p) {
        Object[] places = p.toArray();
        Place[] ps = new Place[places.length];
        for (int i = 0; i < places.length; i++)
            ps[i] = (Place) places[i];

        Dist newDist = new Distribution_c.Unique(ps);
        return newDist;
    }

    public static Dist makeUnique(Region R) {
        Dist newDist = new KDimUnique(R);
        return newDist;
    }

}
