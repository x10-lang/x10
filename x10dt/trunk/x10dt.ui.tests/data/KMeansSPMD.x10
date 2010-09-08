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
import x10.util.DistributedRail;

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
            val fname = opts("-p", "points.dat"), num_clusters=opts("-c",4),
                num_slices=opts("-s",1), num_global_points=opts("-n", 2000),
                iterations=opts("-i",50), dim=opts("-d", 4);
            val verbose = opts("-v"), quiet = opts("-q");

            if (!quiet)
                Console.OUT.println("points: "+num_global_points+" clusters: "+num_clusters+" dim: "+4);

            // file is dimension-major
            val file = new File(fname), fr = file.openRead();
            val init_points = (Int) => Float.fromIntBits(Marshal.INT.read(fr).reverseBytes());
            val num_file_points = (file.size() / dim / 4) as Int;
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
                        val host_nearest = Rail.make(num_slice_points, 0);

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
