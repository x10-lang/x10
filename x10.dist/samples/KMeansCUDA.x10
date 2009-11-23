import x10.io.Console;

import x10.io.File;
import x10.io.Marshal;
import x10.io.IOException;

import x10.util.DistributedRail;

import x10.util.OptionsParser;
import x10.util.Option;



import x10.compiler.Unroll;
import x10.compiler.CUDA;
import x10.compiler.CUDAUtilities;


public class KMeansCUDA {

    public static def printClusters (clusters:Rail[Float]!, dims:Int) {
        for (var d:Int=0 ; d<dims ; ++d) { 
            for (var k:Int=0 ; k<clusters.length/dims ; ++k) { 
                if (k>0) Console.OUT.print(" ");
                Console.OUT.printf("%.2f",clusters(k*dims+d) as Box[Float]);
            }
            Console.OUT.println();
        }
    }

    private static def round_up (x:Int, n:Int) = (x-1) - ((x-1)%n) + n;

    public static def main (args : Rail[String]!) {
        try {
            val opts = new OptionsParser(args, [
                Option("q","quiet","just print time taken"),
                Option("v","verbose","print out each iteration")
            ], [
                Option("p","points","location of data file"),
                Option("i","iterations","quit after this many iterations"),
                Option("c","clusters","number of clusters to find"),
                Option("n","num","quantity of points")]);
            val fname = opts("-p", "points.dat"), num_clusters=opts("-c",8),
                num_global_points=opts("-n", 100000), iterations=opts("-i",500);
            val verbose = opts("-v"), quiet = opts("-q");
            val MEM_ALIGN = 32; // FOR CUDA

            if (!quiet)
                Console.OUT.println("points: "+num_global_points+" clusters: "+num_clusters+" dim: "+4);

            // file is dimension-major
            val file = new File(fname), fr = file.openRead();
            val init_points = (Int) => Float.fromIntBits(Marshal.INT.read(fr).reverseBytes());
            val num_file_points = file.size() / 4 / 4 as Int;
            val file_points = ValRail.make(num_file_points*4, init_points);

            var results : Rail[Float]!;

            // clusters are dimension-major
            val clusters       = new DistributedRail[Float](num_clusters*4, file_points);
            val cluster_counts = new DistributedRail[Int](num_clusters, (Int)=>0);

            finish async {

                // SPMD style for algorithm
                val clk = Clock.make();

                for (h in Place.places) for (gpu in h.children()) async (h) clocked(clk) {

                    // carve out local portion of points (point-major)
                    val num_local_points = num_global_points / Place.NUM_ACCELS;
                    val offset = (gpu.id - Place.MAX_PLACES) * num_local_points;
                    if (!quiet)
                        Console.OUT.println(gpu+" gets "+offset+" len "+num_local_points);
                    val num_local_points_stride = round_up(num_local_points,MEM_ALIGN);
                    val init = (i:Int) => {
                        val d=i/num_local_points_stride, p=i%num_local_points_stride;
                        return p<num_local_points ? file_points(((p+offset)%num_file_points)*4 + d) : 0;
                    };

                    // these are pretty big so allocate up front
                    val host_points = Rail.make(num_local_points_stride*4, init);
                    val gpu_points = Rail.makeRemote(gpu, num_local_points_stride*4, host_points);
                    val host_nearest = Rail.make(num_local_points, (Int)=>0 as Int);
                    val gpu_nearest = Rail.makeRemote(gpu, num_local_points, (Int)=>0 as Int);

                    next;

                    val start_time = System.currentTimeMillis();

                    main_loop: for (var iter:Int=0 ; iter<iterations ; iter++) {

                        val clusters_copy = clusters as ValRail[Float];

                        // classify kernel
                        finish async (gpu) @CUDA {
                            val blocks = CUDAUtilities.autoBlocks(),
                                threads = CUDAUtilities.autoThreads();
                            for ((block) in 0..blocks-1) {
                                val clustercache = Rail.make[Float](num_clusters*4, clusters_copy);
                                for ((thread) in 0..threads-1) async {
                                    val tid = block * threads + thread;
                                    val tids = blocks * threads;
                                    for (var p:Int=tid ; p<num_local_points ; p+=tids) {
                                        var closest:Int = -1;
                                        var closest_dist:Float = Float.MAX_VALUE;
                                        @Unroll(20) for ((k) in 0..num_clusters-1) { 
                                            // Pythagoras (in d dimensions)
                                            var dist : Float = 0;
                                            for ((d) in 0..3) { 
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

                        // bring gpu results onto host
                        finish host_nearest.copyFrom(0, gpu_nearest, 0, num_local_points);
                        
                        // compute new clusters

                        // hoist from loop for performance reasons
                        val host_clusters : Rail[Float]! = clusters();
                        val host_cluster_counts : Rail[Int]! = cluster_counts();

                        host_clusters.reset(0);
                        host_cluster_counts.reset(0);

                        for (var p:Int=0 ; p<num_local_points ; p++) {
                            val closest = host_nearest(p);
                            for (var d:Int=0 ; d<4 ; ++d)
                                host_clusters(closest*4+d) += host_points(p+d*num_local_points_stride);
                            host_cluster_counts(closest)++;
                        }

                        clusters.collectiveReduce(Float.+);
                        cluster_counts.collectiveReduce(Int.+);

                        for (var k:Int=0 ; k<num_clusters ; ++k) { 
                            for (var d:Int=0 ; d<4 ; ++d) host_clusters(k*4+d) /= host_cluster_counts(k);
                        }

                        if (offset==0 && verbose) {
                            Console.OUT.println("Iteration: "+iter);
                            printClusters(clusters(),4);
                        }

                        // TEST FOR CONVERGENCE
                        for (var j:Int=0 ; j<num_clusters*4 ; ++j) {
                            if (true||Math.abs(clusters_copy(j)-clusters(j))>0.0001) continue main_loop;
                        }

                        break;

                    } // main_loop

                    if (offset==0) {
                        val stop_time = System.currentTimeMillis();
                        if (!quiet) Console.OUT.print(num_global_points+" "+num_clusters+" 4 ");
                        Console.OUT.println((stop_time-start_time)/1E3);
                    }

                } // gpus

            } // finish


            if (!quiet) {
                for (var k:Int=0 ; k<num_clusters ; ++k) { 
                    for (var d:Int=0 ; d<4 ; ++d) clusters.get()(k*4+d) /= cluster_counts.get()(k);
                }
                printClusters(clusters.get(),4);
            }


        } catch (e : Throwable) {
            e.printStackTrace(Console.ERR);
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
