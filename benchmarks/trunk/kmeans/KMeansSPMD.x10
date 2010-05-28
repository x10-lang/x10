/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */


import x10.io.Console;
import x10.io.File;
import x10.io.Marshal;
import x10.io.IOException;
import x10.util.Random;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.Pair;
import x10.util.HashMap;

final class DistributedRail[T] implements Settable[Int,T], Iterable[T] {
    global val data : PlaceLocalHandle[Rail[T]];
    global val firstPlace : Place;
    global val localRails 
    = PlaceLocalHandle.make[HashMap[Activity, Rail[T]]](Dist.makeUnique(), 
            ()=>new HashMap[Activity, Rail[T]]());
    global val original : ValRail[T];
    global val original_len : Int;

    global val done = PlaceLocalHandle.make[Cell[Boolean]](Dist.makeUnique(), ()=>new Cell[Boolean](false));

    public def this (len:Int, init:ValRail[T]) {
        val vr = ValRail.make(len, init);
        data = PlaceLocalHandle.make[Rail[T]](Dist.makeUnique(), ()=>Rail.make(len,vr));
        firstPlace = here;
        original = vr;
        original_len = len;
    }

    public def this (len:Int, init:(Int)=>T) {
        val vr = ValRail.make(len, init);
        data = PlaceLocalHandle.make[Rail[T]](Dist.makeUnique(), ()=>Rail.make(len,vr));
        firstPlace = here;
        original = vr;
        original_len = len;
    }

    public static safe operator[S] (x:DistributedRail[S]) = x() as ValRail[S];

    public global safe def apply () {
        val a = Runtime.activity();
        val r:Rail[T] = localRails().getOrElse(a, null);
        if (r==null) {
            val r_ = Rail.make[T](original_len, original);
            localRails().put(a, r_);
            return r_;
        }
        return r;
    }

    public global safe def get() = data();

    public global safe def drop() { localRails().remove(Runtime.activity()); }

    public global safe def apply (i:Int) = this()(i);

    public global safe def set (v:T, i:Int) {
        val t = this();
        at (t) 
          t(i) = v;
        return v;
    }

    public global safe def iterator () {
        val t = this();
        return at (t)
         t.iterator();
    }

    private global def reduceLocal (op:(T,T)=>T) {
        val master = data();
        var first:Boolean = true;
        for (e in localRails().entries()) {
            val r = at(e) e.getValue();
            if (first) {
                at (r) 
                  finish r.copyTo(0, master, 0, r.length);
                first = false;
            } else {
                
                for (var i:Int=0 ; i<master.length ; ++i) {
                    val i0=i;
                    master(i) = op(master(i), at (r) r(i0));
                }
            }
        }
    }

    private global def reduceGlobal (op:(T,T)=>T) {
        if (firstPlace!=here) {
            val local_ = data();
            {
                val local = local_ as ValRail[T];
                val data_ = data;
                at (firstPlace) {
                    val master = data_();
                    atomic for (var i:Int=0 ; i<master.length ; ++i) {
                        master(i) = op(master(i), local(i));
                    }
                }
            }
            next; // every place has transmitted contents to master
            val handle = data; // avoid 'this' being serialised
            finish local_.copyFrom(0, firstPlace, ()=>Pair[Rail[T],Int](handle(),0), local_.length);
        } else {
            next;
        }
    }

    private global def bcastLocal (op:(T,T)=>T) {
        val master = data();
        for (e in localRails().entries()) {
            
            val r = at (e) e.getValue();
            finish at (r) r.copyFrom(0, master, 0, r.length);
            
        }
    }

    // op must be commutative
    public global def collectiveReduce (op:(T,T)=>T) {
        var i_won:Boolean = false;
        atomic {
            if (!done().value) {
                i_won = true;
                done().value = true;
            }
        }
        next; // activity rails populated at this place
        if (i_won) {
            // single thread per place mode
            reduceLocal(op);
            next; // every place has local rail populated
            reduceGlobal(op); // there's one 'next' in here too
            bcastLocal(op);
            done().value = false;
        } else {
            next;
            next;
        }
        next; // every place has finished reading from place 0
    }

}

public class KMeansSPMD {

    public static def printClusters (clusters:Rail[Float]!, dims:Int) {
        for (var d:Int=0 ; d<dims ; ++d) { 
            for (var k:Int=0 ; k<clusters.length/dims ; ++k) { 
                if (k>0)
                    Console.OUT.print(" ");
                Console.OUT.print(clusters(k*dims+d).toString());
            }
            Console.OUT.println();
        }
    }

    public static def main (args : Rail[String]!) {

        try {

            val opts = new OptionsParser(args, [
                Option("q","quiet","just print time taken"),
                Option("v","verbose","print out each iteration")
            ], [
                Option("p","points","location of data file"),
                Option("i","iterations","quit after this many iterations"),
                Option("c","clusters","number of clusters to find"),
                Option("d","dim","number of dimensions"),
                Option("s","slices","factor by which to oversubscribe computational resources"),
                Option("n","num","quantity of points")]);
            val fname = opts("-p", "points.dat"), num_clusters=opts("-c",8),
                num_slices=opts("-s",1), num_global_points=opts("-n", 100000),
                iterations=opts("-i",50), dim=opts("-d", 4);
            val verbose = opts("-v"), quiet = opts("-q");

            if (!quiet)
                Console.OUT.println("points: "+num_global_points+" clusters: "+num_clusters+" dim: "+4);

            // file is dimension-major
            val file = new File(fname), fr = file.openRead();
            val init_points = (Int) => Float.fromIntBits(Marshal.INT.read(fr).reverseBytes());
            val num_file_points = file.size() / dim / 4 as Int;
            val file_points = ValRail.make(num_file_points*dim, init_points);

            var results : Rail[Float]!;

            // clusters are dimension-major
            val clusters       = new DistributedRail[Float](num_clusters*dim, file_points);
            val cluster_counts = new DistributedRail[Int](num_clusters, (Int)=>0);


            finish async {

                val clk = Clock.make();

                val num_slice_points = num_global_points / num_slices / Place.MAX_PLACES;

                for ((slice) in 0..num_slices-1) {

                    for (h in Place.places) async (h) clocked(clk) {

                        // carve out local portion of points (point-major)
                        val offset = (here.id*num_slices*num_slice_points) + slice*num_slice_points;
                        if (!quiet)
                            Console.OUT.println(h+" gets "+offset+" len "+num_slice_points);
                        val num_slice_points_stride = num_slice_points;
                        val init = (i:Int) => {
                            val d=i/num_slice_points_stride, p=i%num_slice_points_stride;
                            return p<num_slice_points ? file_points(((p+offset)%num_file_points)*dim + d) : 0;
                        };

                        // these are pretty big so allocate up front
                        val host_points = Rail.make(num_slice_points_stride*dim, init);
                        val host_nearest = Rail.make(num_slice_points, (Int)=>0);

                        val host_clusters : Rail[Float]! = clusters();
                        val host_cluster_counts : Rail[Int]! = cluster_counts();

                        val start_time = System.currentTimeMillis();

                        var compute_time:ULong = 0;
                        var comm_time:ULong = 0;
                        var next_time:ULong = 0;

                        next; // ensure everyone is ready before we start timing

                        main_loop: for (var iter:Int=0 ; iter<iterations ; iter++) {

                            Console.OUT.println("Iteration: "+iter);

                            val old_clusters = clusters() as ValRail[Float];

                            host_clusters.reset(0);
                            host_cluster_counts.reset(0);

                            val compute_start = System.nanoTime();
                            for (var p:Int=0 ; p<num_slice_points ; ++p) {
                                var closest:Int = -1;
                                var closest_dist:Float = Float.MAX_VALUE;
                                for (var k:Int=0 ; k<num_clusters ; ++k) { 
                                    var dist : Float = 0;
                                    for (var d:Int=0 ; d<dim ; ++d) { 
                                        val tmp = host_points(p+d*num_slice_points_stride) - old_clusters(k*dim+d);
                                        dist += tmp * tmp;
                                    }
                                    if (dist < closest_dist) {
                                        closest_dist = dist;
                                        closest = k;
                                    }
                                }
                                for (var d:Int=0 ; d<dim ; ++d) { 
                                    host_clusters(closest*dim+d) += host_points(p+d*num_slice_points_stride);
                                }
                                host_cluster_counts(closest)++;
                            }
                            compute_time += System.nanoTime() - compute_start;

/*
                            val next_start = System.nanoTime();
                            next;
                            next_time += System.nanoTime() - next_start;
*/

                            val comm_start = System.nanoTime();
                            clusters.collectiveReduce(Float.+);
                            cluster_counts.collectiveReduce(Int.+);
                            comm_time += System.nanoTime() - comm_start;

                            for (var k:Int=0 ; k<num_clusters ; ++k) {
                                for (var d:Int=0 ; d<dim ; ++d) host_clusters(k*dim+d) /= host_cluster_counts(k);
                            }

                            if (offset==0 && verbose) {
                                Console.OUT.println("Iteration: "+iter);
                                printClusters(clusters() as Rail[Float]!,dim);
                            }

                            // TEST FOR CONVERGENCE
                            for (var j:Int=0 ; j<num_clusters*dim ; ++j) {
                                if (true/*||Math.abs(clusters_old(j)-clusters(j))>0.0001*/) continue main_loop;
                            }

                            break;

                        } // main_loop

                        if (offset==0) {
                            val stop_time = System.currentTimeMillis();
                            if (!quiet) Console.OUT.print(num_global_points+" "+num_clusters+" "+dim+" ");
                            Console.OUT.println((stop_time-start_time)/1E3);
                        }
                        Console.OUT.println("Computation time: "+compute_time/1E9);
                        Console.OUT.println("'next' time: "+next_time/1E9);
                        Console.OUT.println("Communication time: "+comm_time/1E9);

                        

                    } // async

                } // slice

            } // finish

        } catch (e : Throwable) {
            e.printStackTrace(Console.ERR);
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
