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

import x10.compiler.Unroll;
import x10.compiler.CUDA;
import x10.compiler.CUDAUtilities;
import x10.compiler.Native;


public class KMeansCUDA {

    public static def printClusters (clusters:Rail[Float], dims:Int) {
        for (var d:Int=0 ; d<dims ; ++d) { 
            for (var k:Int=0 ; k<clusters.length/dims ; ++k) { 
                if (k>0) Console.OUT.print(" ");
                Console.OUT.printf("%.2f",clusters(k*dims+d));
            }
            Console.OUT.println();
        }
    }

    private static def round_up (x:UInt, n:UInt) = (x-1) - ((x-1)%n) + n;

    public static def main (args : Array[String](1)) {
        try {
            val opts = new OptionsParser(args, [
                Option("q","quiet","just print time taken"),
                Option("v","verbose","print out each iteration")
            ], [
                Option("p","points","location of data file"),
                Option("i","iterations","quit after this many iterations"),
                Option("c","clusters","number of clusters to find"),
                Option("s","slices","factor by which to oversubscribe computational resources"),
                Option("n","num","quantity of points")]);
            // The casts can go on resolution of XTENLANG-1413
            val fname = opts("-p", "points.dat"), num_clusters=opts("-c",8) as UInt,
                num_slices=opts("-s",4) as UInt, num_global_points=opts("-n", 100000) as UInt,
                iterations=opts("-i",500) as UInt;
            val verbose = opts("-v"), quiet = opts("-q");

            val MEM_ALIGN = 32; // FOR CUDA

            if (!quiet)
                Console.OUT.println("points: "+num_global_points+" clusters: "+num_clusters+" dim: "+4);

            // file is dimension-major
            val file = new File(fname), fr = file.openRead();
            val init_points = (Int) => Float.fromIntBits(Marshal.INT.read(fr).reverseBytes());
            val num_file_points = (file.size() / 4 / 4) as Int;
            val file_points = ValRail.make(num_file_points*4, init_points);

            //val team = Team.WORLD;
            val team = Team(Rail.make[Place](num_slices * Place.MAX_PLACES, (i:Int) => Place.places(i/num_slices)));

            finish {

                val num_slice_points = num_global_points / num_slices;

                for ([slice] in 0..num_slices-1) {

                    for (h in Place.places) for (gpu in h.children()) async at (h) {

                        val role = here.id * num_slices + slice;

                        // carve out local portion of points (point-major)
                        val num_local_points = num_slice_points / Place.NUM_ACCELS;
                        val offset = slice*num_slice_points + (gpu.id - Place.MAX_PLACES) * num_local_points;
                        if (!quiet)
                            Console.OUT.println(gpu+" gets "+offset+" len "+num_local_points);
                        val num_local_points_stride = round_up(num_local_points,MEM_ALIGN);
                        val init = (i:Int) => {
                            val d=i/num_local_points_stride, p=i%num_local_points_stride;
                            return p<num_local_points ? file_points(((p+offset)%num_file_points)*4 + d) : 0;
                        };

                        // these are pretty big so allocate up front
                        val host_points = Rail.make((num_local_points_stride*4) as Int, init);
                        val gpu_points = Rail.makeRemote(gpu, (num_local_points_stride*4) as Int, host_points);
                        val host_nearest = Rail.make[Int](num_local_points as Int, (Int)=>0 as Int);
                        val gpu_nearest = Rail.makeRemote[Int](gpu, num_local_points as Int, (Int)=>0 as Int);

                        val host_clusters  = Rail.make[Float](num_clusters*4, file_points);
                        val host_cluster_counts = Rail.make[Int](num_clusters, (Int)=>0);

                        val start_time = System.currentTimeMillis();

                        main_loop: for (var iter:UInt=0 ; iter<iterations ; iter++) {

                            val clusters_copy = host_clusters as ValRail[Float];

                            var k_start_time : Long = System.currentTimeMillis();
                            // classify kernel
                            finish async at (gpu) @CUDA {
                                val blocks = CUDAUtilities.autoBlocks(),
                                    threads = CUDAUtilities.autoThreads();
                                for ([block] in 0..blocks-1) {
                                    val clustercache = Rail.make[Float](num_clusters*4, clusters_copy);
                                    for ([thread] in 0..threads-1) async {
                                        val tid = block * threads + thread;
                                        val tids = blocks * threads;
                                        for (var p:UInt=tid ; p<num_local_points ; p+=tids) {
                                            var closest:Int = -1;
                                            var closest_dist:Float = Float.MAX_VALUE;
                                            @Unroll(20) for ([k] in 0..num_clusters-1) { 
                                                // Pythagoras (in d dimensions)
                                                var dist : Float = 0;
                                                for ([d] in 0..3) { 
                                                    val tmp = gpu_points(p+d*num_local_points_stride)
                                                              - clustercache(k*4+d);
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
                            Console.OUT.println("kernel: "+(System.currentTimeMillis() - k_start_time));

                            // bring gpu results onto host
                            k_start_time = System.currentTimeMillis();
                            finish host_nearest.copyFrom(0, gpu_nearest, 0, num_local_points as Int);
                            Console.OUT.println("dma: "+(System.currentTimeMillis() - k_start_time));
                            
                            // compute new clusters
                            host_clusters.reset(0);
                            host_cluster_counts.reset(0);

                            k_start_time = System.currentTimeMillis();
                            for (var p:UInt=0 ; p<num_local_points ; p++) {
                                val closest = host_nearest(p);
                                for (var d:UInt=0 ; d<4u ; ++d)
                                    host_clusters(closest*4+d) += host_points(p+d*num_local_points_stride);
                                host_cluster_counts(closest)++;
                            }
                            Console.OUT.println("reaverage: "+(System.currentTimeMillis() - k_start_time));

                            team.allreduce(role, host_clusters, 0, host_clusters, 0, host_clusters.length, Team.ADD);
                            team.allreduce(role, host_cluster_counts, 0, host_cluster_counts, 0, host_cluster_counts.length, Team.ADD);

                            for (var k:UInt=0 ; k<num_clusters ; ++k) { 
                                for (var d:UInt=0 ; d<4u ; ++d) host_clusters(k*4u+d) /= host_cluster_counts(k);
                            }

                            if (offset==0u && verbose) {
                                Console.OUT.println("Iteration: "+iter);
                                printClusters(host_clusters,4u);
                            }


                            /*
                            // TEST FOR CONVERGENCE
                            for (var j:UInt=0 ; j<num_clusters*4 ; ++j) {
                                if (true||Math.abs(clusters_copy(j)-host_clusters(j))>0.0001) continue main_loop;
                            }

                            break;
                            */

                        } // main_loop

                        if (offset==0u) {
                            val stop_time = System.currentTimeMillis();
                            if (!quiet) Console.OUT.print(num_global_points+" "+num_clusters+" 4 ");
                            Console.OUT.println((stop_time-start_time)/1E3);
                        }

                    } // gpus

                } // slice

            } // finish

        } catch (e : Throwable) {
            e.printStackTrace(Console.ERR);
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
