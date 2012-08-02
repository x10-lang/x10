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

    public static def printClusters (clusters:Array[Float]{rank==1}, dims:Int) {
        for (var d:Int=0 ; d<dims ; ++d) { 
            for (var k:Int=0 ; k<clusters.size/dims ; ++k) { 
                if (k>0) Console.OUT.print(" ");
                Console.OUT.printf("%.2f",clusters(k*dims+d));
            }
            Console.OUT.println();
        }
    }

    private static def round_up (x:Int, n:Int) = (x-1) - ((x-1)%n) + n;

    public static def main (args : Array[String](1)) {
        val opts = new OptionsParser(args, [
            Option("q","quiet","just print time taken"),
            Option("v","verbose","print out each iteration")
        ], [
            Option("p","points","location of data file"),
            Option("i","iterations","quit after this many iterations"),
            Option("c","clusters","number of clusters to find"),
            Option("n","num","quantity of points")]);
        val fname = opts("-p", "points.dat");
        val num_clusters=opts("-c",8);
        val num_global_points=opts("-n", 100000);
        val iterations=opts("-i",500);
        val verbose = opts("-v");
        val quiet = opts("-q");
        val dim = 4; // must be compiletime constant

        val MEM_ALIGN = 32; // FOR CUDA

        if (!quiet)
            Console.OUT.println("points: "+num_global_points+" clusters: "+num_clusters+" dim: "+dim);

        // file is dimension-major
        val file = new File(fname);
        val fr = file.openRead();
        val init_points = (Int) => Float.fromIntBits(Marshal.INT.read(fr).reverseBytes());
        val num_file_points = (file.size() / 4 / dim) as Int;
        val file_points = new Array[Float](num_file_points*dim, init_points);

        if (!quiet) {
            if (Place.NUM_ACCELS==0) {
                Console.OUT.println("Running without using GPUs.  Running the kernel on the CPU.");
                Console.OUT.println("If that's not what you want, set X10RT_ACCELS=ALL to use all GPUs at each place.");
                Console.OUT.println("For more information, see the X10/CUDA documentation.");
            } else {
                Console.OUT.println("Running using "+Place.NUM_ACCELS+" GPUs.");
            }
        }

        val team = Place.NUM_ACCELS==0 ? Team.WORLD : Team(new Array[Place](Place.NUM_ACCELS, (i:Int) => Place(Place.MAX_PLACES+i).parent()));

        finish {

            for (h in Place.places()) {

                val workers = Place.NUM_ACCELS==0 ? [h as Place] : h.children();

                for (gpu in workers.values()) async at (h) {

                    val role = gpu==h ? h.id : gpu.id - Place.MAX_PLACES;

                    team.barrier(role);


                    // carve out local portion of points (point-major)
                    val num_local_points = num_global_points / team.size();
                    val offset = role * num_local_points;

                    for (p in 0..(team.size()-1)) {
                        if (p==role && !quiet) {
                            Console.OUT.println("GPU known as "+gpu+" gets role "+role+" offset "+offset+" len "+num_local_points);
                        }
                        team.barrier(role);
                    }
                    val num_local_points_stride = round_up(num_local_points,MEM_ALIGN);
                    val init = (i:Int) => {
                        val d=i/num_local_points_stride;
                        val p=i%num_local_points_stride;
                        return p<num_local_points ? file_points(((p+offset)%num_file_points)*dim + d) : 0f;
                    };

                    // these are pretty big so allocate up front
                    val host_points = new Array[Float]((num_local_points_stride*dim), init);

                    val gpu_points = CUDAUtilities.makeRemoteArray(gpu, num_local_points_stride*dim, host_points);
                    val host_nearest = new Array[Int](num_local_points, 0);
                    val gpu_nearest = CUDAUtilities.makeRemoteArray[Int](gpu, num_local_points, 0);

                    val host_clusters  = new Array[Float](num_clusters*dim, (i:Int)=>file_points(i));
                    val host_cluster_counts = new Array[Int](num_clusters, 0);

                    val toplevel_start_time = System.currentTimeMillis();

                    val clusters_copy = new Array[Float](num_clusters*dim);

                    var k_time:Long = 0;
                    var c_time:Long = 0;
                    var d_time:Long = 0;
                    var r_time:Long = 0;

                    main_loop: for (var iter:Int=0 ; iter<iterations ; iter++) {

                        Array.copy(host_clusters, 0, clusters_copy, 0, num_clusters*dim);

                        var start_time : Long = System.currentTimeMillis();
                        // classify kernel
                        finish async at (gpu) @CUDA @CUDADirectParams {
                            val blocks = CUDAUtilities.autoBlocks();
                            val threads = CUDAUtilities.autoThreads();
                            finish for (block in 0..(blocks-1)) async {
                                val clustercache = new Array[Float](clusters_copy);
                                clocked finish for (thread in 0..(threads-1)) clocked async {
                                    val tid = block * threads + thread;
                                    val tids = blocks * threads;
                                    for (var p:Int=tid ; p<num_local_points ; p+=tids) {
                                        var closest:Int = -1;
                                        var closest_dist:Float = Float.MAX_VALUE;
                                        @Unroll(20) for (k in 0..(num_clusters-1)) { 
                                            // Pythagoras (in dim dimensions)
                                            var dist : Float = 0;
                                            for (d in 0..(dim-1)) { 
                                                val tmp = gpu_points(p+d*num_local_points_stride) 
                                                          - @NoInline clustercache(k*dim+d);
                                                dist += tmp * tmp;
                                            }
                                            // record closest cluster seen so far
                                            if (dist < closest_dist) {
                                                closest_dist = dist;
                                                closest = k;
                                            }
                                        }
                                        gpu_nearest(p) = closest;
                                    }
                                }
                            }
                        }
                        k_time += System.currentTimeMillis() - start_time;
                        //if (verbose) Console.OUT.println("kernel: "+(System.currentTimeMillis() - start_time));

                        // bring gpu results onto host
                        start_time = System.currentTimeMillis();
                        finish Array.asyncCopy(gpu_nearest, 0, host_nearest, 0, num_local_points);
                        d_time += System.currentTimeMillis() - start_time;
                        //if (verbose) Console.OUT.println("dma: "+(System.currentTimeMillis() - start_time));
                        
                        // compute new clusters
                        host_clusters.fill(0);
                        host_cluster_counts.fill(0);

                        val host_nearest_raw = host_nearest.raw();
                        val host_clusters_raw = host_clusters.raw();
                        val host_points_raw = host_points.raw();
                        val host_cluster_counts_raw = host_cluster_counts.raw();
                        start_time = System.currentTimeMillis();
                        for (var p:Int=0 ; p<num_local_points ; p++) {
                            val closest = host_nearest_raw(p);
                            for (var d:Int=0 ; d<dim ; ++d)
                                host_clusters_raw(closest*dim+d) += host_points_raw(p+d*num_local_points_stride);
                            host_cluster_counts_raw(closest)++;
                        }
                        c_time += System.currentTimeMillis() - start_time;
                        //if (verbose) Console.OUT.println("reaverage: "+(System.currentTimeMillis() - start_time));

                        start_time = System.currentTimeMillis();
                        team.allreduce(role, host_clusters, 0, host_clusters, 0, host_clusters.size, Team.ADD);
                        team.allreduce(role, host_cluster_counts, 0, host_cluster_counts, 0, host_cluster_counts.size, Team.ADD);
                        r_time += System.currentTimeMillis() - start_time;

                        for (var k:Int=0 ; k<num_clusters ; ++k) { 
                            if (host_cluster_counts(k) <= 0) Console.ERR.println("host_cluster_counts("+k+") = "+host_cluster_counts(k));
                            for (var d:Int=0 ; d<dim ; ++d) host_clusters(k*dim+d) /= host_cluster_counts(k);
                        }

                        if (offset==0 && verbose) {
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

                    if (offset==0) {
                        val toplevel_stop_time = System.currentTimeMillis();
                        if (!quiet) Console.OUT.print(""+num_global_points+" "+num_clusters+" "+dim+" ");
                        Console.OUT.println((toplevel_stop_time-toplevel_start_time)/1E3);
                        Console.OUT.println("kernel: "+k_time/1E3);
                        Console.OUT.println("dma: "+d_time/1E3);
                        Console.OUT.println("cpu: "+c_time/1E3);
                        Console.OUT.println("reduce: "+r_time/1E3);
                    }

                    CUDAUtilities.deleteRemoteArray(gpu_points);
                    CUDAUtilities.deleteRemoteArray(gpu_nearest);

                } // gpus

            } // hosts

        } // finish

    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
