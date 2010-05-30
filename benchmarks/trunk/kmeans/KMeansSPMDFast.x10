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


import x10.compiler.Native;
import x10.io.Console;
import x10.io.File;
import x10.io.Marshal;
import x10.io.IOException;
import x10.util.Random;
import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.Pair;
import x10.util.HashMap;
import x10.compiler.Native;
import x10.compiler.NativeCPPInclude;

@NativeCPPInclude("pgas_collectives.h")

final class DistributedRail[T] implements Iterable[T] {
    global val data : PlaceLocalHandle[Rail[T]];

    public def this (init:ValRail[T]) {
        data = PlaceLocalHandle.make[Rail[T]](Dist.makeUnique(), ()=>init as Rail[T]);
    }

    public def this (len:Int, init:(Int)=>T) {
        this(ValRail.make(len, init));
    }

    public static safe operator[S] (x:DistributedRail[S]) = x() as ValRail[S];

    public global safe def apply () = data();

    public global safe def apply (i:Int) = this()(i);

    public global safe def iterator () = data().iterator();

    // op must be commutative
    public global def collectiveReduceSumInt () {
        val local = data();
        val len = local.length;
        @Native("c++",
        "void *r = __pgasrt_tspcoll_iallreduce(0, local->raw(), local->raw(), PGASRT_OP_ADD, PGASRT_DT_int, len);" +
        "while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();") {}
    }

    public global def collectiveReduceSumFloat () {
        val local = data();
        val len = local.length;
        @Native("c++",
        "void *r = __pgasrt_tspcoll_iallreduce(0, local->raw(), local->raw(), PGASRT_OP_ADD, PGASRT_DT_flt, len);" +
        "while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();") {}
    }

    public global def barrier() {
        @Native("c++", 
            "void *r = __pgasrt_tspcoll_ibarrier(0);" +
            "while (!__pgasrt_tspcoll_isdone(r)) x10rt_probe();") {}
    }
}

final class MyRail[T] {
    def this() {}

    @Native("c++", "closest")
    const closest = new MyRail[Int]();

    @Native("c++", "closest_dist")
    const closest_dist = new MyRail[Float]();

    @Native("c++", "dist")
    const dist = new MyRail[Float]();

    @Native("c++", "tmp")
    const tmp = new MyRail[Float]();

    @Native("c++", "#1 #4[#5]")
    native static def declare[U](MyRail[U], size:Int):Void;

    @Native("c++", "#4[#5]")
    native static def get[U](MyRail[U], index:Int):U;

    @Native("c++", "#4[#5] = #6")
    native static def set[U](MyRail[U], index:Int, value:U):Void;

    @Native("c++", "#4[#5] += #6")
    native static def incr[U](MyRail[U], index:Int, value:U):Void;
}


public class KMeansSPMDFast {

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
    
    @Native("c++", "(#4)->_data[(#5)] = (#6)")
    public native static def set[T](rail:Rail[T], index:Int, value:T):Void;
    
    @Native("c++", "(#4)->_data[(#5)] += (#6)")
    public native static def incr[T](rail:Rail[T], index:Int, value:T):Void;
    
    @Native("c++", "(#4)->_data[(#5)]")
    public native static def get[T](rail:Rail[T], index:Int):T;

    @Native("c++", "(#4)->_data[(#5)]")
    public native static def get[T](rail:ValRail[T], index:Int):T;

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
                Option("n","num","quantity of points")]);
            val fname = opts("-p", "points.dat"), num_clusters=opts("-c",8),
                num_global_points=opts("-n", 100000),
                iterations=opts("-i",50), dim=opts("-d", 4);
            val verbose = opts("-v"), quiet = opts("-q");

            if (!quiet)
                Runtime.println("points: "+num_global_points+" clusters: "+num_clusters+" dim: "+dim);

            // file is dimension-major
            val file = new File(fname), fr = file.openRead();
            val init_points = (Int) => Float.fromIntBits(Marshal.INT.read(fr).reverseBytes());
            val num_file_points = file.size() / dim / 4 as Int;
            val file_points = ValRail.make(num_file_points*dim, init_points);

            var results : Rail[Float]!;

            // clusters are dimension-major
            val clusters       = new DistributedRail[Float](num_clusters*dim, file_points);
            val cluster_counts = new DistributedRail[Int](num_clusters, (Int)=>0);


            finish {

                val num_slice_points = num_global_points / Place.MAX_PLACES;

                    for (h in Place.places) async (h) {

                        // carve out local portion of points (point-major)
                        val offset = here.id*num_slice_points;
                        if (!quiet && offset==0)
                            Runtime.println(h+" gets "+offset+" len "+num_slice_points);
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
                        var barr_time:ULong = 0;

                        // ensure everyone is ready before we start timing
                        clusters.barrier();

                        main_loop: for (var iter:Int=0 ; iter<iterations ; iter++) {

                            if (!quiet && offset==0)
                                Runtime.println("Iteration: "+iter);

                            val old_clusters = clusters() as ValRail[Float];

                            host_clusters.reset(0);
                            host_cluster_counts.reset(0);

                            val compute_start = System.nanoTime();
                            for (var p:Int=0 ; p<num_slice_points ; p += 8) {
                                MyRail.declare(MyRail.closest, 8);
                                MyRail.declare(MyRail.closest_dist, 8);
                                for (var w:Int=0; w<8; w++) MyRail.set(MyRail.closest, w, -1);
                                for (var w:Int=0; w<8; w++) MyRail.set(MyRail.closest_dist, w, 1e37f);
                                for (var k:Int=0 ; k<num_clusters ; ++k) {
                                    MyRail.declare(MyRail.dist, 8);
                                    MyRail.declare(MyRail.tmp, 8);
                                    for (var w:Int=0; w<8; w++) MyRail.set(MyRail.dist, w, 0f);
                                    for (var d:Int=0 ; d<dim ; ++d) {
                                        for (var w:Int=0; w<8; w++) {
                                            val tmp = get(host_points, p+w+d*num_slice_points_stride) - get(old_clusters, k*dim+d);
                                            MyRail.incr(MyRail.dist, w, tmp * tmp);
                                        }
                                    }
                                    for (var w:Int=0; w<8; w++) {
                                        if (MyRail.get(MyRail.dist, w) < MyRail.get(MyRail.closest_dist, w)) {
                                            MyRail.set(MyRail.closest_dist, w, MyRail.get(MyRail.dist, w));
                                            MyRail.set(MyRail.closest, w, k);
                                        }
                                    }
                                }
                                for (var d:Int=0 ; d<dim ; ++d) {
                                    for (var w:Int=0; w<8; w++) {
                                        val index = MyRail.get(MyRail.closest, w)*dim+d;
                                        incr(host_clusters, index, get(host_points, p+w+d*num_slice_points_stride));
                                    }
                                }
                                for (var w:Int=0; w<8; w++)
                                    incr(host_cluster_counts, MyRail.get(MyRail.closest, w), 1);
                            }
                            compute_time += System.nanoTime() - compute_start;

                            //val barr_start = System.nanoTime();
                            //clusters.barrier();
                            //barr_time += System.nanoTime() - barr_start;

                            val comm_start = System.nanoTime();
                            clusters.collectiveReduceSumFloat();
                            cluster_counts.collectiveReduceSumInt();
                            comm_time += System.nanoTime() - comm_start;

                            for (var k:Int=0 ; k<num_clusters ; ++k) {
                                for (var d:Int=0 ; d<dim ; ++d) host_clusters(k*dim+d) /= host_cluster_counts(k);
                            }

                            if (offset==0 && verbose) {
                                //printClusters(clusters() as Rail[Float]!,dim);
                            }

                            // TEST FOR CONVERGENCE
                            for (var j:Int=0 ; j<num_clusters*dim ; ++j) {
                                if (true/*||Math.abs(clusters_old(j)-clusters(j))>0.0001*/) continue main_loop;
                            }

                            break;

                        } // main_loop

                        if (offset==0) {
                            val stop_time = System.currentTimeMillis();
                            //if (!quiet) Runtime.println(num_global_points+" "+num_clusters+" "+dim+" ");
                            Runtime.println(""+(stop_time-start_time)/1E3);
                        }
                        Runtime.println("Computation time: "+compute_time/1E9);
                        Runtime.println("Communication time: "+comm_time/1E9);
                        //Runtime.println("Barrier time: "+barr_time/1E9);

                        

                    } // async

            } // finish

        } catch (e : Throwable) {
            e.printStackTrace(Console.ERR);
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
