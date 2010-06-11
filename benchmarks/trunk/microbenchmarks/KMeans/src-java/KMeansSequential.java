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

/**
 * A KMeans object o can compute K means of a given set of 
 * points of dimension o.myDim.
 * <p> 
 * This class implements a sequential program, that is readily parallelizable.
 * 
 * A translation of the x10.dist/samples/KMeans.x10 program to Java
 */
public class KMeansSequential {
    
    static SumVector[] redCluster;
    static SumVector[] blackCluster;
    
    /**
     * Compute myK means for the given set of points of dimension myDim.
     */
    static void computeMeans(KMeansDataSet data, int myK, int numIterations, float EPS) {
        float[] points = data.points;
        int numDimensions = data.numDimensions;
        int numPoints = data.numPoints;
        redCluster = new SumVector[myK];
        for (int i=0; i<myK; i++) {
            redCluster[i] = new SumVector(numDimensions, points, i);
        }
        blackCluster = new SumVector[myK];
        for (int i=0; i<myK; i++) {
            blackCluster[i] = new SumVector(numDimensions);
        }
        
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
    
    public static void main (String[] args) {
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
        computeMeans(data, K, iterations, EPS);
        
        SumVector[] result =  redCluster;
        for (int k=0; k<K; k++) {
            result[k].print();
        }
        System.out.println();
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
