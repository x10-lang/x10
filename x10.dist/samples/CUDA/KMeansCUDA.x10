/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
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

class KernelWorker {
    private val gpu:Place;
    private val gpuIndex:Long;
    private val gpuNum:Long;
    private val kernelOffset:Long;
    private val kernelNumPoints:Long;
    private val kernelNumPointsStride:Long;
    private val hostClusters:Rail[Float];
    private val numClusters:Int;

    private var kernelTime:Long;
    private var dmaTime:Long;

    private val gpuPoints:GlobalRail[Float]{home==gpu};
    private val gpuClusters:GlobalRail[Float]{home==gpu};
    private val gpuNearest:GlobalRail[Int]{home==gpu};
    private val hostNearest:Rail[Int];
    
    private static def round_up (x:Long, n:Long) = (x-1) - ((x-1)%n) + n;

    def this(quiet:Boolean, gpu:Place, gpu_index:Long, gpu_num:Long, global_points:Rail[Float], global_num_points:Long, host_offset:Long, host_num_points:Long, host_clusters:Rail[Float], num_clusters:Int, host_nearest:Rail[Int]) {
        this.gpu = gpu;
        this.gpuIndex = gpu_index;
        this.gpuNum = gpu_num;
        this.hostClusters = host_clusters;
        this.numClusters = num_clusters;
        this.hostNearest = host_nearest;

        kernelNumPoints = host_num_points / gpuNum;
        kernelOffset = gpuIndex * kernelNumPoints + host_offset;

        if (!quiet) {
            Console.OUT.println("Worker at "+gpu+" gets points "+kernelOffset+".."+(kernelOffset + kernelNumPoints - 1));
        }
        
        val MEM_ALIGN = 32n; // FOR CUDA
        kernelNumPointsStride = round_up(kernelNumPoints, MEM_ALIGN);

        val dim = 4;

        // these are pretty big so allocate up front

        gpuPoints = CUDAUtilities.makeGlobalRail[Float](this.gpu, kernelNumPointsStride*dim);
        // DMA them from global_points
        finish for (d in 0..(dim-1)) {
            Rail.asyncCopy(global_points, d*global_num_points + kernelOffset, gpuPoints, d*kernelNumPointsStride, kernelNumPoints);
        }
        this.gpuClusters = CUDAUtilities.makeGlobalRail[Float](this.gpu, num_clusters*dim);
        this.gpuNearest = CUDAUtilities.makeGlobalRail[Int](this.gpu, kernelNumPoints);
    }

    def doWork() {

        val kernel_num_points = this.kernelNumPoints;
        val kernel_num_points_stride = this.kernelNumPointsStride;
        val gpu_points = this.gpuPoints;
        val gpu_nearest = this.gpuNearest;
        val the_host_clusters = this.hostClusters;
        val the_num_clusters = this.numClusters;
        val the_dim = 4;

        val kernel_start_time : Long = System.currentTimeMillis();
        // classify kernel
        finish async at (gpu) @CUDA @CUDADirectParams {
            val blocks = CUDAUtilities.autoBlocks();
            val threads = CUDAUtilities.autoThreads();
            finish for (block in 0n..(blocks-1n)) async {
                val clustercache = new Rail[Float](the_host_clusters);
                clocked finish for (thread in 0n..(threads-1n)) clocked async {
                    val tid = block * threads + thread;
                    val tids = blocks * threads;
                    for (var p:Long=tid; p<kernel_num_points; p+=tids) {
                        var closest:Int = -1n;
                        var closest_dist:Float = Float.MAX_VALUE;
                        @Unroll(20) for (k in 0n..(the_num_clusters-1n)) { 
                            // Pythagoras (in dim dimensions)
                            var dist:Float = 0.0f;
                            for (d in 0..(the_dim-1)) { 
                                val tmp = gpu_points(p+d*kernel_num_points_stride) 
                                          - clustercache(k*the_dim+d);
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
        kernelTime = System.currentTimeMillis() - kernel_start_time;
        //if (verbose) Console.OUT.println("kernel: "+this.kernelTime);

        // bring gpu results onto host
        val dma_start_time = System.currentTimeMillis();
        finish Rail.asyncCopy(gpuNearest, 0, hostNearest, gpuIndex * kernelNumPoints, kernelNumPoints);
        dmaTime = System.currentTimeMillis() - dma_start_time;
        //if (verbose) Console.OUT.println("dma: "+(System.currentTimeMillis() - start_time));
    }

    def delete() {
        CUDAUtilities.deleteGlobalRail(gpuPoints);
        CUDAUtilities.deleteGlobalRail(gpuNearest);
    }

    def getKernelTime() = kernelTime;
    def getDMATime() = dmaTime;
}

public class KMeansCUDA {

    public static def printClusters (clusters:Rail[Float], dims:Long) {
        for (var d:Long=0; d<dims; ++d) { 
            for (var k:Long=0; k<clusters.size/dims; ++k) { 
                if (k>0) Console.OUT.print(" ");
                Console.OUT.printf("%.2f",clusters(k*dims+d));
            }
            Console.OUT.println();
        }
    }


    public static def main (args:Rail[String]) {
        val topo = PlaceTopology.getTopology();
        val opts = new OptionsParser(args, [
            Option("q","quiet","just print time taken"),
            Option("v","verbose","print out each iteration")
        ], [
            Option("p","points","location of data file"),
            Option("i","iterations","quit after this many iterations"),
            Option("c","clusters","number of clusters to find"),
            Option("n","num","quantity of points"),
            Option("w","work","number of points to process per activity (cpu part)")
        ]);
        val fname = opts("-p", "points.dat");
        val num_clusters=opts("-c",8n);
        val global_num_points=opts("-n", 100000);
        val iterations=opts("-i", topo.numChildrenPlaces() == 0 ? 50 : 500);
        val verbose = opts("-v");
        val quiet = opts("-q");
        val work = opts("-w", 100000n);
        val dim = 4; // must be compiletime constant


        if (!quiet)
            Console.OUT.println("points: "+global_num_points+" clusters: "+num_clusters+" dim: "+dim);

        // file is dimension-major
        val file = new File(fname);
        val fr = file.openRead();
        val init_points = (Long) => Float.fromIntBits(Marshal.INT.read(fr).reverseBytes());
        val file_num_points = (file.size() / 4l / dim);
        val file_points = new Rail[Float](file_num_points*dim, init_points);
        val init_global_points = (i:Long) => { val p = (i%global_num_points)%file_num_points, d=i/global_num_points; return file_points(p*dim + d); };
        val global_points = new Rail[Float](global_num_points*dim, init_global_points);

        if (!quiet) {
            if (topo.numChildrenPlaces() == 0) {
                Console.OUT.println("Running without using GPUs.  Running the kernel on the CPU.");
                Console.OUT.println("If that's not what you want, set X10RT_ACCELS=ALL to use all GPUs at each place.");
                Console.OUT.println("For more information, see the X10/CUDA documentation.");
            } else {
                Console.OUT.println("Running using "+topo.numChildrenPlaces()+" GPUs.");
            }
        }

        finish for (h in Place.places()) at (h) async {

            // carve out local portion of points (point-major)
            val host_num_points = global_num_points / Place.numPlaces();
            val host_offset = h.id * host_num_points;



            val host_nearest = new Rail[Int](host_num_points);
            val host_clusters  = new Rail[Float](num_clusters*dim, (i:Long)=>{ val p = i/dim, d=i%dim ; return global_points(p+host_offset + d*global_num_points); } );
            val host_cluster_counts = new Rail[Int](num_clusters as Long);

            val numGPUs = topo.numChildren(here);
            val kernel_workers:Rail[KernelWorker];
            if (numGPUs == 0) {
                kernel_workers = [new KernelWorker(quiet, here,0,1, global_points, global_num_points, host_offset, host_num_points, host_clusters, num_clusters, host_nearest) as KernelWorker];
            } else {
                kernel_workers = new Rail[KernelWorker](numGPUs, (i:Long)=>new KernelWorker(quiet, topo.getChild(here, i), i, numGPUs, global_points, global_num_points, host_offset, host_num_points, host_clusters, num_clusters, host_nearest));
            }

            var k_time:Long = 0;
            var c_time:Long = 0;
            var d_time:Long = 0;
            var r_time:Long = 0;

            val toplevel_start_time = System.currentTimeMillis();

            main_loop: for (var iter:Int=0n; iter<iterations; iter++) {

                finish for (kernel_worker in kernel_workers) async {
                    kernel_worker.doWork();
                    k_time += kernel_worker.getKernelTime();
                    d_time += kernel_worker.getDMATime();
                }

                // compute new clusters
                val cpu_start_time = System.currentTimeMillis();
                host_clusters.clear();
                host_cluster_counts.clear();
                finish for (var p_start:Long=0; p_start<host_num_points; p_start+=work) {
                    val p_start_ = p_start;
                    async {
                        for (i in 0..(work-1)) {
                            val p = p_start_ + i;
                            if (p >= host_num_points) break;
                            val closest = host_nearest(p);
                            //Console.ERR.println("closest = "+closest);
                            for (var d:Long=0; d<dim; ++d)
                                host_clusters(closest*dim+d) += global_points(global_num_points*d + p + host_offset);
                            host_cluster_counts(closest)++;
                        }
                    }
                }
                c_time += System.currentTimeMillis() - cpu_start_time;
                //if (verbose) Console.OUT.println("reaverage: "+(System.currentTimeMillis() - start_time));

                val reduce_start_time = System.currentTimeMillis();
                Team.WORLD.allreduce(host_clusters, 0, host_clusters, 0, host_clusters.size, Team.ADD);
                Team.WORLD.allreduce(host_cluster_counts, 0, host_cluster_counts, 0, host_cluster_counts.size, Team.ADD);
                r_time += System.currentTimeMillis() - reduce_start_time;

                for (var k:Long=0; k<num_clusters; ++k) { 
                    if (here.id==0) {
                        if (host_cluster_counts(k) <= 0) Console.ERR.println("host_cluster_counts("+k+") = "+host_cluster_counts(k));
                    }
                    for (var d:Long=0; d<dim; ++d) host_clusters(k*dim+d) /= host_cluster_counts(k);
                }

                if (here.id==0) {
                    Console.OUT.println("Iteration: "+iter);
                    if (verbose) printClusters(host_clusters,dim);
                }


                /*
                // TEST FOR CONVERGENCE
                for (var j:Int=0n; j<num_clusters*dim ; ++j) {
                    if (Math.abs(clusters_copy(j)-host_clusters(j))>0.0001) continue main_loop;
                }

                break;
                */

            } // main_loop

            val toplevel_stop_time = System.currentTimeMillis();

            // times
            if (!quiet) Console.OUT.print(""+global_num_points+" "+num_clusters+" "+dim+" ");
            Console.OUT.println((toplevel_stop_time-toplevel_start_time)/1E3);
            Console.OUT.println("kernel: "+k_time/1E3);
            Console.OUT.println("dma: "+d_time/1E3);
            Console.OUT.println("cpu: "+c_time/1E3);
            Console.OUT.println("reduce: "+r_time/1E3);

            // results
            if (!quiet && here.id==0) printClusters(host_clusters,dim);

            finish for (kernel_worker in kernel_workers) async kernel_worker.delete();

        } // finish

    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
