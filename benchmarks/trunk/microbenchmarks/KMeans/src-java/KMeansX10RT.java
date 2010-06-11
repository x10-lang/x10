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
    
    private static float[] redClusterPoints;
    private static int[] redClusterCounts;
    private static float[] blackClusterPoints;
    
    private static ActiveMessage computeMeansAM;
    private static ActiveMessage accumulatePointsAM;
    private static ActiveMessage accumulateCountsAM;
    
    /**
     * Compute myK means for the given set of points of dimension myDim.
     */
    public static void computeMeans(int myK, int numIterations, int numPoints, int numDimensions, float[] initialCluster, float[] points) {
        assert numDimensions * myK == initialCluster.length;
        
        redClusterPoints = new float[myK*numDimensions];
        System.arraycopy(initialCluster, 0, redClusterPoints, 0, redClusterPoints.length);
        blackClusterPoints = new float[myK*numDimensions];
        redClusterCounts = new int[myK];

        X10RT.barrier();
        
        for (int iteration = 1; iteration <= numIterations; iteration++) {
            float[] swapTmp = redClusterPoints;
            redClusterPoints = blackClusterPoints;
            blackClusterPoints = swapTmp;
 
            // For all points assigned to me, compute the closest current cluster
            // and add the point into that cluster for the next iteration.
            for (int pointNumber = 0; pointNumber<numPoints; pointNumber++) {
                int closest = -1;
                float closestDist = Float.MAX_VALUE;
                for (int k=0; k<myK; k++) {
                    float dist = 0;
                    for (int dim=0; dim<numDimensions; dim++) {
                        float tmp = blackClusterPoints[k*numDimensions + dim] - points[pointNumber*numDimensions + dim];
                        dist += tmp*tmp;
                    }
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = k;
                    }
                }
                for (int dim=0; dim<numDimensions; dim++) {
                    redClusterPoints[closest*numDimensions +dim] += points[pointNumber*numDimensions + dim];
                }
                redClusterCounts[closest]++;
           }
 
            // Collective accumulation operations.
            // Accumulate the redClusterPoints and redClusterCounts into every place.
            X10RT.barrier();
            for (int recv = 0; recv<X10RT.numPlaces(); recv++) {
                Place p = X10RT.getPlace(recv);
                if (p != X10RT.here()) {
                    accumulatePointsAM.send(p, redClusterPoints);
                    accumulateCountsAM.send(p, redClusterCounts);
                }
            }
            X10RT.barrier();
            
            // Local reduction: adjust cluster coordinates by dividing each point value
            // by the number of points in the cluster
            for (int k=0; k<myK; k++) {
                float tmp = (float)redClusterCounts[k];
                for (int dim=0; dim<numDimensions; dim++) {
                    redClusterPoints[k*numDimensions+dim] /= tmp;
                }
            }
            
            // Reset for the next iteration
            Arrays.fill(redClusterCounts, 0);
            Arrays.fill(blackClusterPoints, 0.0f);
        } 
    }
    
    public static synchronized void accumulatePoints(float[] other) {
        for (int i=0; i<redClusterPoints.length; i++) {
            redClusterPoints[i] += other[i];
        }
    }
    
    public static synchronized void accumulateCounts(int[] other) {
        for (int i=0; i<redClusterCounts.length; i++) {
            redClusterCounts[i] += other[i];
        }
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
            
            // Now, initiate the computation in my place.
            float[] points = new float[pointsPerPlace*data.numDimensions];
            System.arraycopy(data.points, 0, points, 0, points.length);
            System.out.println("Computing points "+0+"..."+(pointsPerPlace-1)+" at place 0");
            computeMeans(K, iterations, pointsPerPlace, data.numDimensions, initialCluster, points);
        
            // All done. Print the results
            for (int k=0; k<K; k++) {
                for (int j=0; j<data.numDimensions; j++) {
                    if (j>0) System.out.print(" ");
                    System.out.print(redClusterPoints[k*data.numDimensions+j]);
                }
                System.out.println();
            }
            System.out.println();
        }
        
        X10RT.barrier();
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
