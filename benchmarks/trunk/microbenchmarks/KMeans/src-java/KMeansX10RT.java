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

import java.util.Arrays;

import x10.x10rt.ActiveMessage;
import x10.x10rt.MessageRegistry;
import x10.x10rt.Place;
import x10.x10rt.X10RT;

/**
 * A KMeans object o can compute K means of a given set of 
 * points of dimension o.myDim.
 * <p> 
 * 
 * A translation of the x10.dist/samples/KMeansSPMD.x10 program to Java+X10RT
 */
public class KMeansX10RT {
    
    private static boolean USE_NATIVE = false;
    
    // Statics used to hold place-local data needed by AMs
    private static float[] result;
    private static float[] cachedClusters;
    private static int[] cachedClusterCounts;
    private static volatile int reductionCount;  // used to implement "await" in collective
    
    // per iteration timing data
    private static long[] kernelNanos;
    private static long[] kernel2Nanos;
    private static long[] allToAllNanos;
    private static long[] localReduceNanos;
    
    // AM handles
    private static ActiveMessage computeMeansAM;
    private static ActiveMessage accumulatePointsAM;
    private static ActiveMessage accumulateCountsAM;
    
    /**
     * Compute myK means for the given set of points of dimension myDim.
     */
    public static void computeMeans(int myK, int numIterations, int numPoints, int numDimensions, float[] initialCluster, float[] points) {
        assert numDimensions * myK == initialCluster.length;
        
        float[] clusters = new float[myK*numDimensions];
        System.arraycopy(initialCluster, 0, clusters, 0, clusters.length);
        int[] clusterCounts = new int[myK];
        int[] closestCluster = new int[numPoints];
        
        cachedClusters = clusters;
        cachedClusterCounts = clusterCounts;
        
        kernelNanos = new long[numIterations];
        kernel2Nanos = new long[numIterations];
        allToAllNanos = new long[numIterations];
        localReduceNanos = new long[numIterations];
        
        if (USE_NATIVE) {
            System.loadLibrary("KMeansX10RT");
            nativeInit(myK, numPoints, numDimensions, points);
        }
        
        for (int iteration = 0; iteration < numIterations; iteration++) {
            doAnIteration(iteration, myK, numPoints, numDimensions, points, clusters, clusterCounts, closestCluster);
        }
        
        // Workaround AM not being able to return a value by stashing computed clusters in a static field
        // so that main can read it again in place 0.
        result = clusters;
    }

    private static native void nativeInit(int myK, int numPoints, int numDimensions, float[] points);
    
    // A singled iteration of the algorithm.
    // Pulled out into a separate method so that the JIT has a chance to optimize it.
    // Compensating for weaknesses in hot-loop-transfer in some JVMs. 
    private static void doAnIteration(int iteration, int myK, int numPoints, int numDimensions, float[] points,
                                      float[] clusterPoints, int[] clusterCounts, int[] closestCluster) {
        kernelNanos[iteration] = - System.nanoTime();
        reductionCount = 2*(X10RT.numPlaces()-1);

        if (USE_NATIVE) {
            computeClosestClusterNative(myK, numPoints, numDimensions, points, clusterPoints, closestCluster);           
        } else {
            computeClosestCluster(myK, numPoints, numDimensions, points, clusterPoints, closestCluster);
        }
        
        long now = System.nanoTime();
        kernelNanos[iteration] += now;
        kernel2Nanos[iteration] = -now;
        
        // Now that we know the closest cluster for each point, compute the new cluster centers
        Arrays.fill(clusterCounts, 0);
        Arrays.fill(clusterPoints, 0.0f);
        for (int pointNumber=0; pointNumber<numPoints; pointNumber++) {
            int closest = closestCluster[pointNumber];
            for (int dim=0; dim<numDimensions; dim++) {
                clusterPoints[closest*numDimensions + dim] += points[pointNumber*numDimensions + dim];
            }
            clusterCounts[closest]++;
        }
        
        now = System.nanoTime();
        kernel2Nanos[iteration] += now;
        allToAllNanos[iteration] = -now;

        // All-to-All collective accumulation operations.
        // Accumulate the redClusterPoints and redClusterCounts into every place.
        X10RT.barrier();
        for (int recv = 0; recv<X10RT.numPlaces(); recv++) {
            Place p = X10RT.getPlace(recv);
            if (p != X10RT.here()) {
                accumulatePointsAM.send(p, clusterPoints);
                accumulateCountsAM.send(p, clusterCounts);
            }
        }
        while (reductionCount > 0) X10RT.probe(); // Can't use a barrier, because we have to await the completion of the reduction.
        
        now = System.nanoTime();
        allToAllNanos[iteration] += now;
        localReduceNanos[iteration] = -now;
        
        // Local reduction: adjust cluster coordinates by dividing each point value
        // by the number of points in the cluster
        for (int k=0; k<myK; k++) {
            float tmp = (float)clusterCounts[k];
            for (int dim=0; dim<numDimensions; dim++) {
                clusterPoints[k*numDimensions+dim] /= tmp;
            }
        }
        
        localReduceNanos[iteration] += System.nanoTime();
    }

    // For all points assigned to me, compute the closest current cluster
    private static void computeClosestCluster(int myK, int numPoints, int numDimensions, float[] points,
                                              float[] clusterPoints, int[] closestCluster) {
        for (int pointNumber = 0; pointNumber<numPoints; pointNumber++) {
            int closest = -1;
            float closestDist = Float.MAX_VALUE;
            for (int k=0; k<myK; k++) {
                float dist = 0;
                for (int dim=0; dim<numDimensions; dim++) {
                    float tmp = clusterPoints[k*numDimensions + dim] - points[pointNumber*numDimensions + dim];
                    dist += tmp*tmp;
                }
                if (dist < closestDist) {
                    closestDist = dist;
                    closest = k;
                }
            }
            closestCluster[pointNumber] = closest;
        }
    }
    
    // For all points assigned to me, compute the closest current cluster
    private static native void computeClosestClusterNative(int myK, int numPoints, int numDimensions, float[] points,
                                                           float[] clusterPoints, int[] closestCluster);
 

    public static synchronized void accumulatePoints(float[] other) {
        float[] clusters = cachedClusters;
        for (int i=0; i<clusters.length; i++) {
            clusters[i] += other[i];
        }
        reductionCount--;
    }
    
    public static synchronized void accumulateCounts(int[] other) {
        int[] clusterCounts = cachedClusterCounts;
        for (int i=0; i<clusterCounts.length; i++) {
            clusterCounts[i] += other[i];
        }
        reductionCount--;
    }

    private static double toMillis(long nanoTime) {
        return ((double)nanoTime)/1e6;
    }
    
    public static void main (String[] args) {
        Class<?> floatArray = new float[0].getClass();
        Class<?> intArray = new int[0].getClass();
        computeMeansAM = MessageRegistry.register(KMeansX10RT.class, "computeMeans", 
                                                  Integer.TYPE, Integer.TYPE,
                                                  Integer.TYPE, Integer.TYPE, floatArray, floatArray);
        accumulatePointsAM = MessageRegistry.register(KMeansX10RT.class, "accumulatePoints", floatArray);
        accumulateCountsAM = MessageRegistry.register(KMeansX10RT.class, "accumulateCounts", intArray);

        X10RT.barrier();
        
        if (X10RT.here() == X10RT.getPlace(0)) {
            String fileName = "points.dat";
            int K = 4;
            int iterations = 50;
            int argIndex = 0;

            while (argIndex < args.length) {
                String arg = args[argIndex++];
                if (arg.equals("-k")) {
                    K = Integer.parseInt(args[argIndex++]);   
                } else if (arg.equals("-i")) {
                    iterations = Integer.parseInt(args[argIndex++]);
                } else if (arg.equals("-n")) {
                    USE_NATIVE = true;
                } else {
                    fileName = arg;
                }
            }

            KMeansDataSet data = KMeansDataSet.readPointsFromFile(fileName);
            float[] initialCluster = new float[K*data.numDimensions];
            System.arraycopy(data.points, 0, initialCluster, 0, initialCluster.length);
            
            // Send messages to other places to initiate their computation, evenly splitting data.points.
            int pointsPerPlace = data.numPoints/X10RT.numPlaces();
            for (int i = 1; i<X10RT.numPlaces(); i++) {
                int start = i * pointsPerPlace;
                int stop = Math.min(start+pointsPerPlace, data.points.length);
                int numPoints = stop-start+1;
                float[] points = new float[numPoints*data.numDimensions];
                System.arraycopy(data.points, start, points, 0, points.length);
                System.out.println("Sending points "+start+"..."+stop+" to place "+i);
                computeMeansAM.send(X10RT.getPlace(i), K, iterations, numPoints, data.numDimensions, initialCluster, points);
            }
            
            X10RT.barrier(); // matched in else clause
            
            // Now, initiate the computation in my place.
            float[] points = new float[pointsPerPlace*data.numDimensions];
            System.arraycopy(data.points, 0, points, 0, points.length);
            System.out.println("Computing points "+0+"..."+(pointsPerPlace-1)+" at place 0");
            computeMeans(K, iterations, pointsPerPlace, data.numDimensions, initialCluster, points);
        
            // All done. Print the results
            for (int k=0; k<K; k++) {
                for (int j=0; j<data.numDimensions; j++) {
                    if (j>0) System.out.print(" ");
                    System.out.print(result[k*data.numDimensions+j]);
                }
                System.out.println();
            }
            System.out.println();
            
            long totalKernelNanos = 0;
            long totalKernel2Nanos = 0;
            long totalAllToAllNanos = 0;
            long totalLocalReduceNanos = 0;
            System.out.println("Per iteration phase timings (kernel, allToAll, localReuce)");
            for (int i=0; i<iterations; i++) {
                System.out.printf("%3.5f %3.5f %3.5f %3.5f\n",toMillis(kernelNanos[i]), toMillis(kernel2Nanos[i]), toMillis(allToAllNanos[i]), toMillis(localReduceNanos[i]));
                totalKernelNanos += kernelNanos[i];
                totalKernel2Nanos += kernel2Nanos[i];
                totalAllToAllNanos += allToAllNanos[i];
                totalLocalReduceNanos += localReduceNanos[i];
            }
            System.out.println("-------------------------------------------------------");
            System.out.printf("%3.5f %3.5f %3.5f %3.5f\n", toMillis(totalKernelNanos), toMillis(totalKernel2Nanos), toMillis(totalAllToAllNanos), toMillis(totalLocalReduceNanos));
        } else {
            X10RT.barrier();  // matched the barrier in place 0 in "then clause" above
        }
        
        X10RT.barrier(); // make sure no one exits until we are all done.
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
