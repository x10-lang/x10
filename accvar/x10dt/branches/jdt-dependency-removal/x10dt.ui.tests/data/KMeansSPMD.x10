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
import x10.util.Team;

/**
 * An SPMD formulation of KMeans.
 * Converted to 2.1 on 9/1/2010.
 */
public class KMeansSPMD {

    public static def printClusters (clusters:Array[Float](1), dims:Int) {
        for (var d:Int=0 ; d<dims ; ++d) { 
            for (var k:Int=0 ; k<clusters.size/dims ; ++k) { 
                if (k>0)
                    Console.OUT.print(" ");
                Console.OUT.print(clusters(k*dims+d).toString());
            }
            Console.OUT.println();
        }
    }

    public static def main (args:Array[String](1)) {

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
            val init_points = (int) => Float.fromIntBits(Marshal.INT.read(fr).reverseBytes());
            val num_file_points = (file.size() / dim / 4) as Int;
            val file_points = new Array[Float](num_file_points*dim, init_points);

            //val team = Team.WORLD;
            val team = Team(new Array[Place](num_slices * Place.MAX_PLACES, (i:int) => Place.place(i/num_slices)));

            val num_slice_points = num_global_points / num_slices / Place.MAX_PLACES;

            finish {

                for ([slice] in 0..num_slices-1) {

                    for (h in Place.places()) async at(h) {

                        val role = here.id * num_slices + slice;

                        // carve out local portion of points (point-major)
                        val offset = (here.id*num_slices*num_slice_points) + slice*num_slice_points;
                        if (!quiet)
                            Console.OUT.println(h.toString()+" gets "+offset+" len "+num_slice_points);
                        val num_slice_points_stride = num_slice_points;
                        val init = (i:int) => {
                            val d=i/num_slice_points_stride, p=i%num_slice_points_stride;
                            return p<num_slice_points ? file_points(((p+offset)%num_file_points)*dim + d) : 0;
                        };

                        // these are pretty big so allocate up front
                        val host_points = new Array[Float](num_slice_points_stride*dim, init);
                        val host_nearest = new Array[Float](num_slice_points);

                        val host_clusters  = new Array[Float](0..num_clusters*dim-1, file_points);
                        val host_cluster_counts = new Array[Int](num_clusters);

                        val start_time = System.currentTimeMillis();

                        var compute_time:ULong = 0;
                        var comm_time:ULong = 0;
                        var barrier_time:ULong = 0;

                        team.barrier(role);

                        main_loop: for (var iter:Int=0 ; iter<iterations ; iter++) {

                            //if (offset==0) Console.OUT.println("Iteration: "+iter);

                            val old_clusters = new Array[Float](host_clusters.size);
	                    Array.copy(host_clusters, 0, old_clusters, 0, host_clusters.size);

                            host_clusters.fill(0);
                            host_cluster_counts.fill(0);

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

                            val comm_start = System.nanoTime();
                            team.allreduce(role, host_clusters, 0, host_clusters, 0, host_clusters.size, Team.ADD);
                            team.allreduce(role, host_cluster_counts, 0, host_cluster_counts, 0, host_cluster_counts.size, Team.ADD);
                            comm_time += System.nanoTime() - comm_start;

                            for (var k:Int=0 ; k<num_clusters ; ++k) {
                                for (var d:Int=0 ; d<dim ; ++d) host_clusters(k*dim+d) /= host_cluster_counts(k);
                            }

                            if (offset==0 && verbose) {
                                Console.OUT.println("Iteration: "+iter);
                                printClusters(host_clusters,dim);
                            }

                            // TEST FOR CONVERGENCE
                            for (var j:Int=0 ; j<num_clusters*dim ; ++j) {
                                if (true/*||Math.abs(clusters_old(j)-host_clusters(j))>0.0001*/) continue main_loop;
                            }

                            break;

                        } // main_loop

                        if (offset==0) {
                            val stop_time = System.currentTimeMillis();
                            if (!quiet) Console.OUT.print(num_global_points.toString()+" "+num_clusters+" "+dim+" ");
                            Console.OUT.println((stop_time-start_time)/1E3);
                        }
                        for (var i:Int=0 ; i<team.size() ; ++i) {
                            if (role == i) {                            
                                Console.OUT.println(role.toString()+": Computation time: "+compute_time/1E9);
                                Console.OUT.println(role.toString()+": barrier time: "+barrier_time/1E9);
                                Console.OUT.println(role.toString()+": Communication time: "+comm_time/1E9);
                            }
                            team.barrier(role);
                            if (role==0) {
                                Console.OUT.println("\nFinal results:");
                                printClusters(host_clusters,dim);
                            }

                        }

                        team.del(role);    

                    } // async


                } // slice

            } // finish

        } catch (e : Throwable) {
            e.printStackTrace(Console.ERR);
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
