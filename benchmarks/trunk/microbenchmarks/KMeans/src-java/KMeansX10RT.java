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

import x10.x10rt.ActiveMessage;
import x10.x10rt.MessageRegistry;
import x10.x10rt.Place;
import x10.x10rt.X10RT;

/**
 * A KMeans object o can compute K means of a given set of 
 * points of dimension o.myDim.
 * <p> 
 * This class implements a sequential program, that is readily parallelizable.
 * 
 * A translation of the x10.dist/samples/KMeans.x10 program to Java
 */
public class KMeansX10RT {
    
    static SumVector[] redCluster;
    static SumVector[] blackCluster;
    
    static ActiveMessage computeMeansAM;
    static ActiveMessage accumulateResultAM;
    
    /**
     * Compute myK means for the given set of points of dimension myDim.
     */
    public static void computeMeans(int myK, int numIterations, float EPS, int numPoints, int numDimensions, float[] initialCluster, float[] points) {
        assert numDimensions * myK == initialCluster.length;
        redCluster = new SumVector[myK];
        for (int i=0; i<myK; i++) {
            redCluster[i] = new SumVector(numDimensions, initialCluster, i);
        }
        blackCluster = new SumVector[myK];
        for (int i=0; i<myK; i++) {
            blackCluster[i] = new SumVector(numDimensions);
        }

        X10RT.barrier();
        
        for (int i = 1; i <= numIterations; i++) {
            SumVector[] tmp = redCluster;
            redCluster = blackCluster;
            blackCluster = tmp;
            for (int p= 0; p<numPoints; p++) {
                int closest = -1;
                float closestDist = Float.MAX_VALUE;
                for (int k=0; k<myK; k++) { // compute closest mean in cluster.
                    float dist = blackCluster[k].distance(numDimensions, points, p);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = k;
                    }
                }
                redCluster[closest].addIn(numDimensions, points, p);
            }
 
            X10RT.barrier();
            for (int recv = 0; recv<X10RT.numPlaces(); recv++) {
                Place p = X10RT.getPlace(recv);
                if (p != X10RT.here()) {
                    accumulateResultAM.send(p, (Object)redCluster);
                }
            }
            X10RT.barrier();
            
            for (int k=0; k<myK; k++) {
                redCluster[k].normalize();
            }
            
            boolean converged = true;
            for (int k=0; k<myK; k++) {
                if (redCluster[k].distance(blackCluster[k]) > EPS) {
                    converged=false;
                    break;
                }
            }
            if (converged) 
                break;
            for (int k=0; k<myK; k++) {
                blackCluster[k].makeZero();
            }
        } 
    }
    
    public static synchronized void accumulateResult(SumVector[] otherCluster) {
        for (int i=0; i<redCluster.length; i++) {
            redCluster[i].addIn(otherCluster[i]);
        }
    }
    
    public static void main (String[] args) {
        Class<?> floatArray = new float[0].getClass();
        computeMeansAM = MessageRegistry.register(KMeansX10RT.class, "computeMeans", 
                                                Integer.TYPE, Integer.TYPE, Float.TYPE,
                                                Integer.TYPE, Integer.TYPE, floatArray, floatArray);
        accumulateResultAM = MessageRegistry.register(KMeansX10RT.class, "accumulateResult", new SumVector[0].getClass());
                                                
        X10RT.barrier();
        
        if (X10RT.here() == X10RT.getPlace(0)) {
            String fileName = "points.dat";
            int K = 4;
            int iterations = 50;
            float EPS = 0.1f;
            int argIndex = 0;

            while (argIndex < args.length) {
                String arg = args[argIndex++];
                if (arg.equals("-k")) {
                    K = Integer.parseInt(args[argIndex++]);   
                } else if (arg.equals("-i")) {
                    iterations = Integer.parseInt(args[argIndex++]);
                } else if (arg.equals("-e")) {
                    EPS = Float.parseFloat(args[argIndex++]);
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
                computeMeansAM.send(X10RT.getPlace(i), K, iterations, EPS, numPoints, data.numDimensions, initialCluster, points);
            }
            float[] points = new float[pointsPerPlace*data.numDimensions];
            System.arraycopy(data.points, 0, points, 0, points.length);
            System.out.println("Computing points "+0+"..."+(pointsPerPlace-1)+" at place 0");
            computeMeans(K, iterations, EPS, pointsPerPlace, data.numDimensions, initialCluster, points);
        
            SumVector[] result =  redCluster;
            for (int k=0; k<K; k++) {
                result[k].print();
            }
            System.out.println();
        }
        
        X10RT.barrier();
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
