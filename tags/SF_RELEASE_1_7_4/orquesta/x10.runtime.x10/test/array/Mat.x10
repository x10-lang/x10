import x10.lang.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import x10.lang.Object;
import x10.util.Iterator_Scanner;


class Mat {

    //
    // create a matrix covering a square nxn region
    //

    Region R;                                // region covered by whole matrix

    Mat(int n) {
        int [] min = new int [] {0,0};
        int [] max = new int [] {n-1,n-1};
        R = Region.makeRectangular(min, max);
    }


    //
    // decompose the matrix into a sparse set of blocks blocks
    // each block covers some rectangular region R of the matrix
    //

    class Block {

        Region R;                            // region covered by this block
        Array_double data;                   // data for this block
        
        Block(Region R) {
            this.R = R;
            this.data = Array_double.make(R, Array_double.NO_INIT/*null*/);
        }
        


        // compute this block times the portion of vec that overlaps the block
        Vec times(final Vec vec) {
            class Init implements Indexable_double {
                public double get(Point pt) {
                    int i = pt.get(0);
                    double sum = 0.0;
                    Iterator_Scanner it = R.projection(1).scanners();
                    while (it.hasNext()) {
                        Region.Scanner s = (Region.Scanner) it.next();
                        int min0 = s.min(0);
                        int max0 = s.max(0);
                        for (int j=min0; j<=max0; j++)
                            sum += data.get(i,j)*vec.data.get(j);
                    }
                    return sum;
                }
            }
            return new Vec (
                Array_double.make(R.projection(0), new Init(), true)
            );
        }
    }


    //
    // manage a distributed block workload
    // at each place keep a list of blocks
    // uses a greedy bin-packing algorithm:
    // each new block is distributed to the least busy place
    //

    static class Work {                      // work distributed to each place:
        List blocks = new ArrayList();       // a list of Blocks
    }

    static class XXX implements Indexable_Object {
        public Object get(Point p) {
            return new Work();
        }
    }
    Array_Object places                      // a Work object per place
        = Array_Object.make(Dist.UNIQUE, new XXX());

    int [] work = new int[place.MAX_PLACES]; // how much work at each place

    // add a 1xn block
    void addBlock(final int i, final int j, final double [] value) {

        // find the least busy place
        int best = 0;
        Iterator_Scanner it = places.region.scanners();
        while (it.hasNext()) {
            Region.Scanner s = (Region.Scanner) it.next();
            int min0 = s.min(0);
            int max0 = s.max(0);
            for (int p=0; p<=max0; p++)
                if (work[p] <= work[best])
                    best = p;
        }

        // create the block at that place and add it to the list
        finish {
            final int p = best;
            // XXX
            // original was async (places[p])
            // needs to translate to async(places.dist().get(p) i.e. plc for inx
            // not supported yet so for now assume UNIQUE
            async (place.factory.place(p)) {
                int [] min = new int [] {i, j};
                int [] max = new int [] {i, j+value.length-1};
                Region r = Region.makeRectangular(min, max);
                Block block = new Block(r);
                for (int k=0; k<value.length; k++) {
                    block.data.set(i, j+k, value[k]);
                }
                ((Work)(places.get(p))).blocks.add(block);
            }
        }

        // update how much work is distributed to that place
        work[best] += 1;
    }


    //
    // multiply matrix times vector, distributing and asyncing the work
    //

    Vec times(final Vec vec) {

        final Array_double result = Array_double.make(R.projection(0), Array_double.NO_INIT/*null*/);

        finish {
            // for (point p : places) {
            Iterator_Scanner p_it = places.region.scanners();
            while (p_it.hasNext()) {
                Region.Scanner p_s = (Region.Scanner) p_it.next();
                int p_min0 = p_s.min(0);
                int p_max0 = p_s.max(0);
                for (int pp=p_min0; pp<=p_max0; pp++) {
                    final int p = pp;
                    // XXX
                    // original was async (places[p])
                    // needs to translate to async(places.dist().get(p) i.e. plc for inx
                    // not supported yet so for now assume UNIQUE
                    async (place.factory.place(p)) {
                        Iterator block_it = ((Work)(places.get(p))).blocks.iterator(); 
                        while (block_it.hasNext()) {
                            final Block block = (Block) block_it.next();
                            async {
                                final Vec v = block.times(vec);
                                async (this) {
                                    // for (point [i] : v.data) {
                                    Iterator_Scanner i_it = v.data.region.scanners();
                                    while (i_it.hasNext()) {
                                        Region.Scanner i_s = (Region.Scanner) i_it.next();
                                        int i_min0 = i_s.min(0);
                                        int i_max0 = i_s.max(0);
                                        for (int i=i_min0; i<=i_max0; i++) {
                                            result.set(i, result.get(i) + v.data.get(i));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return new Vec(result);
    }

}

