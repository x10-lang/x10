import x10.io.Console;

import x10.io.File;
import x10.io.Marshal;
import x10.io.IOException;

import x10.util.DistributedRail;

import x10.util.OptionsParser;
import x10.util.Option;

import x10.compiler.CUDA;

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
            val opts = new OptionsParser(args, null, [
                Option("p","points","location of data file"),
                Option("i","iterations","quit after this many iterations"),
                Option("c","clusters","number of clusters to find"),
                Option("n","num","quantity of points")]);
            val fname = opts("-p", "points.dat"), num_clusters=opts("-c",8),
                num_global_points=opts("-n", 100000), iterations=opts("-i",500);
            val MEM_ALIGN = 32; // FOR CUDA

            Console.OUT.println("points: "+num_global_points+" clusters: "+num_clusters+" dim: "+4);

            // file is dimension-major
            val fr = (new File(fname)).openRead();
            val init_points = (Int) => Float.fromIntBits(Marshal.INT.read(fr).reverseBytes());
            val global_points = ValRail.make(num_global_points*4, init_points);

            // SPMD style for algorithm
            val clk = Clock.make();
            val dist = Dist.makeUnique();

            // clusters are dimension-major
            val clusters       = new DistributedRail[Float](clk, dist, num_clusters*4, global_points);
            val cluster_counts = new DistributedRail[Int](clk, dist, num_clusters, (Int)=>0);

            val start_time = System.currentTimeMillis();

            finish {

                for (h in Place.places) for (gpu in h.children()) async (h) clocked(clk) {

                    // carve out local portion of points (point-major)
                    val num_local_points = num_global_points / Place.NUM_ACCELS;
                    val offset = (gpu.id - Place.MAX_PLACES) * num_local_points;
                    val num_local_points_stride = round_up(num_local_points,MEM_ALIGN);
                    val init = (i:Int) => {
                        val d=i/num_local_points_stride, p=i%num_local_points_stride + offset;
                        return p < num_local_points ? global_points(p*4+d) : 0;
                    };

                    // these are pretty big so allocate up front
                    val host_points = Rail.make(num_local_points_stride*4, init);
                    val gpu_points = Rail.makeRemote(gpu, num_local_points_stride*4, host_points);
                    val host_nearest = Rail.make(num_local_points, (Int)=>0 as Int);
                    val gpu_nearest = Rail.makeRemote(gpu, num_local_points, (Int)=>0 as Int);

                    main_loop: for (var iter:Int=0 ; iter<iterations ; iter++) {

                        if (h==Place.FIRST_PLACE) Console.OUT.println("Iteration: "+iter);
        
                        val clusters_copy = clusters as ValRail[Float];

                        val kernel_start_time = System.currentTimeMillis();
                        // classify kernel
                        val blocks = 8, threads = 64;
                        at (gpu) @CUDA {
                            for ((block) in 0..blocks-1) {
                                val clustercache = Rail.make[Float](num_clusters*4, clusters_copy);
                                for ((thread) in 0..threads-1) async {
                                    val tid = block * threads + thread;
                                    val tids = blocks * threads;
                                    for (var p:Int=tid ; p<num_local_points ; p+=tids) {
                                        var closest:Int = -1;
                                        var closest_dist:Float = Float.MAX_VALUE;
                                        //@unroll(20)
                                        for ((k) in 0..num_clusters-1) { 
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
                        
                        // compute new clusters and counters
                        val cluster_counts_ = cluster_counts();
                        val clusters_ = clusters();
                        cluster_counts_.reset(0);
                        clusters_.reset(0);
                        for (var p:Int=0 ; p<num_local_points ; p++) {
                            val closest = host_nearest(p);
                            //Console.ERR.println(p+" = "+closest);
/*
                            assert closest >= 0 : "closest is "+closest +
                                                  " at point "+p+" of "+num_local_points;
                            assert closest < num_clusters : "closest is "+closest +
                                                        " but num_clusters is "+num_clusters+
                                                        " at point "+p+" of "+num_local_points;
*/
                            for (var d:Int=0 ; d<4 ; ++d) { 
                                clusters_(closest*4+d) += host_points(p+d*num_local_points_stride);
                            }
                            cluster_counts_(closest)++;
                        }

                        clusters.collectiveReduce(Float.+);
                        cluster_counts.collectiveReduce(Int.+);

                        for (var k:Int=0 ; k<num_clusters ; ++k) { 
                            for (var d:Int=0 ; d<4 ; ++d) clusters(k*4+d) /= cluster_counts(k);
                        }

                        // TEST FOR CONVERGENCE
                        for (var j:Int=0 ; j<num_clusters*4 ; ++j) {
                            if (Math.abs(clusters_copy(j)-clusters(j))>0.0001) continue main_loop;
                        }

                        break;

                    } // main_loop

                } // gpus

                clk.drop();

            } // finish

            val stop_time = System.currentTimeMillis();

            printClusters(clusters(),4);

            Console.OUT.println("Time taken: "+(stop_time-start_time)/1E3);

        } catch (e : Throwable) {
            e.printStackTrace(Console.ERR);
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
