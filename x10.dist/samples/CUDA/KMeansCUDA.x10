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

import x10.util.Team;
import x10.util.Pair;
import x10.util.HashMap;

import x10.util.OptionsParser;
import x10.util.Option;
import x10.util.CUDAUtilities;

import x10.compiler.Unroll;
import x10.compiler.CUDADirectParams;
import x10.compiler.CUDA;
import x10.compiler.Native;
import x10.compiler.NoInline;


public class KMeansCUDA {

    public static def printClusters (clusters:Rail[Float], dims:Long) {
        for (var d:Long=0 ; d<dims ; ++d) { 
            for (var k:Long=0 ; k<clusters.size/dims ; ++k) { 
                if (k>0) Console.OUT.print(" ");
                Console.OUT.printf("%.2f",clusters(k*dims+d));
            }
            Console.OUT.println();
        }
    }

    private static def round_up (x:Long, n:Long) = (x-1) - ((x-1)%n) + n;

    public static def main (args : Rail[String]) {
        val opts = new OptionsParser(args, [
            Option("q","quiet","just print time taken"),
            Option("v","verbose","print out each iteration")
        ], [
            Option("p","points","location of data file"),
            Option("i","iterations","quit after this many iterations"),
            Option("c","clusters","number of clusters to find"),
            Option("n","num","quantity of points")]);
        val fname = opts("-p", "points.dat");
        val num_clusters=opts("-c",8l);
        val num_global_points=opts("-n", 100000l);
        val iterations=opts("-i",500l);
        val verbose = opts("-v");
        val quiet = opts("-q");
        val dim = 4l; // must be compiletime constant

        val MEM_ALIGN = 32; // FOR CUDA

        if (!quiet)
            Console.OUT.println("points: "+num_global_points+" clusters: "+num_clusters+" dim: "+dim);

        // file is dimension-major
        val file = new File(fname);
        val fr = file.openRead();
        val init_points = (Long) => Float.fromIntBits(Marshal.INT.read(fr).reverseBytes());
        val num_file_points = (file.size() / 4l / dim);
        val file_points = new Rail[Float](num_file_points*dim, init_points);

        if (!quiet) {
            if (Place.NUM_ACCELS==0) {
                Console.OUT.println("Running without using GPUs.  Running the kernel on the CPU.");
                Console.OUT.println("If that's not what you want, set X10RT_ACCELS=ALL to use all GPUs at each place.");
                Console.OUT.println("For more information, see the X10/CUDA documentation.");
            } else {
                Console.OUT.println("Running using "+Place.NUM_ACCELS+" GPUs.");
            }
        }

        val team = Place.NUM_ACCELS==0 ? Team.WORLD : Team(new Rail[Place](Place.NUM_ACCELS as Long, (i:Long) => Place(Place.MAX_PLACES+i as Int).parent()));

        finish {

            for (h in Place.places()) {

                val workers = Place.NUM_ACCELS==0 ? [h as Place] : h.children();

                for (gpu in workers) async at (h) {

                    val role = gpu==h ? h.id as Long : gpu.id as Long - Place.MAX_PLACES;

                    team.barrier(role as Int);


                    // carve out local portion of points (point-major)
                    val num_local_points = num_global_points / team.size();
                    val offset = role * num_local_points;

                    for (p in 0l..(team.size()-1l)) {
                        if (p==role && !quiet) {
                            Console.OUT.println("GPU known as "+gpu+" gets role "+role+" offset "+offset+" len "+num_local_points);
                        }
                        team.barrier(role as Int);
                    }
                    val num_local_points_stride = round_up(num_local_points,MEM_ALIGN);
                    val init = (i:Long) => {
                        val d=i/num_local_points_stride;
                        val p=i%num_local_points_stride;
                        return p<num_local_points ? file_points(((p+offset)%num_file_points)*dim + d) : 0f;
                    };

                    // these are pretty big so allocate up front
                    val host_points = new Rail[Float]((num_local_points_stride*dim), init);

                    val gpu_points = CUDAUtilities.makeRemoteRail(gpu, num_local_points_stride*dim, host_points);
                    val host_nearest = new Rail[Long](num_local_points, 0l);
                    val gpu_nearest = CUDAUtilities.makeRemoteRail[Long](gpu, num_local_points, 0l);

                    val host_clusters  = new Rail[Float](num_clusters*dim, (i:Long)=>file_points(i));
                    val host_cluster_counts = new Rail[Int](num_clusters, 0);

                    val toplevel_start_time = System.currentTimeMillis();

                    val clusters_copy = new Rail[Float](num_clusters*dim);

                    var k_time:Long = 0;
                    var c_time:Long = 0;
                    var d_time:Long = 0;
                    var r_time:Long = 0;

                    main_loop: for (var iter:Int=0 ; iter<iterations ; iter++) {

                        Rail.copy(host_clusters, 0l, clusters_copy, 0l, num_clusters*dim);

                        var start_time : Long = System.currentTimeMillis();
                        // classify kernel
                        finish async at (gpu) /*@CUDA @CUDADirectParams*/ {
                            val blocks = CUDAUtilities.autoBlocks();
                            val threads = CUDAUtilities.autoThreads();
                            finish for (block in 0l..(blocks-1l)) async {
                                val clustercache = new Rail[Float](clusters_copy);
                                clocked finish for (thread in 0l..(threads-1l)) clocked async {
                                    val tid = block * threads + thread;
                                    val tids = blocks * threads;
                                    for (var p:Long=tid ; p<num_local_points ; p+=tids) {
                                        var closest:Long = -1;
                                        var closest_dist:Float = Float.MAX_VALUE;
                                        /*@Unroll(20)*/ for (k in 0l..(num_clusters-1l)) { 
                                            // Pythagoras (in dim dimensions)
                                            var dist : Float = 0;
                                            for (d in 0l..(dim-1l)) { 
                                                val tmp = gpu_points()(p+d*num_local_points_stride) 
                                                          - @NoInline clustercache(k*dim+d);
                                                dist += tmp * tmp;
                                            }
                                            // record closest cluster seen so far
                                            if (dist < closest_dist) {
                                                closest_dist = dist;
                                                closest = k;
                                            }
                                        }
                                        gpu_nearest()(p) = closest;
                                    }
                                }
                            }
                        }
                        k_time += System.currentTimeMillis() - start_time;
                        //if (verbose) Console.OUT.println("kernel: "+(System.currentTimeMillis() - start_time));

                        // bring gpu results onto host
                        start_time = System.currentTimeMillis();
                        finish Rail.asyncCopy(gpu_nearest, 0l, host_nearest, 0l, num_local_points);
                        d_time += System.currentTimeMillis() - start_time;
                        //if (verbose) Console.OUT.println("dma: "+(System.currentTimeMillis() - start_time));
                        
                        // compute new clusters
                        start_time = System.currentTimeMillis();
                        host_clusters.clear();
                        host_cluster_counts.clear();
                        for (var p:Long=0 ; p<num_local_points ; p++) {
                            val closest = host_nearest(p);
                            for (var d:Long=0 ; d<dim ; ++d)
                                host_clusters(closest*dim+d) += host_points(p+d*num_local_points_stride);
                            host_cluster_counts(closest)++;
                        }
                        c_time += System.currentTimeMillis() - start_time;
                        //if (verbose) Console.OUT.println("reaverage: "+(System.currentTimeMillis() - start_time));

                        start_time = System.currentTimeMillis();
                        team.allreduce(role as Int, host_clusters, 0, host_clusters, 0, host_clusters.size as Int, Team.ADD);
                        team.allreduce(role as Int, host_cluster_counts, 0, host_cluster_counts, 0, host_cluster_counts.size as Int, Team.ADD);
                        r_time += System.currentTimeMillis() - start_time;

                        for (var k:Long=0 ; k<num_clusters ; ++k) { 
                            if (host_cluster_counts(k) <= 0) Console.ERR.println("host_cluster_counts("+k+") = "+host_cluster_counts(k));
                            for (var d:Long=0 ; d<dim ; ++d) host_clusters(k*dim+d) /= host_cluster_counts(k);
                        }

                        if (offset==0l && verbose) {
                            Console.OUT.println("Iteration: "+iter);
                            printClusters(host_clusters,dim);
                        }


                        /*
                        // TEST FOR CONVERGENCE
                        for (var j:Int=0 ; j<num_clusters*dim ; ++j) {
                            if (true||Math.abs(clusters_copy(j)-host_clusters(j))>0.0001) continue main_loop;
                        }

                        break;
                        */

                    } // main_loop

                    if (offset==0l) {
                        val toplevel_stop_time = System.currentTimeMillis();
                        if (!quiet) Console.OUT.print(""+num_global_points+" "+num_clusters+" "+dim+" ");
                        Console.OUT.println((toplevel_stop_time-toplevel_start_time)/1E3);
                        Console.OUT.println("kernel: "+k_time/1E3);
                        Console.OUT.println("dma: "+d_time/1E3);
                        Console.OUT.println("cpu: "+c_time/1E3);
                        Console.OUT.println("reduce: "+r_time/1E3);
                    }

                    CUDAUtilities.deleteRemoteRail(gpu_points);
                    CUDAUtilities.deleteRemoteRail(gpu_nearest);

                } // gpus

            } // hosts

        } // finish

    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
