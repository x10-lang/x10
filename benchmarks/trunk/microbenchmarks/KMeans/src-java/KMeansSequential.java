import java.util.Random;

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
    
    public static final int DIM=2;
    public static final int K=4;
    public static final int POINTS=2000;
    public static final int ITERATIONS=50;
    public static final float EPS=0.01f;
    
    final int myDim;
    
    KMeansSequential(int md) {
      this.myDim = md;
    }
    
    /**
     * Compute myK means for the given set of points of dimension myDim.
     */
    SumVector[] computeMeans(int myK, final float[][] points) {
        SumVector[] redCluster = new SumVector[myK];
        for (int i=0; i<myK; i++) {
            redCluster[i] = new SumVector(points[i]);
        }
        SumVector[] blackCluster = new SumVector[myK];
        for (int i=0; i<myK; i++) {
            blackCluster[i] = new SumVector(myDim);
        }
        
        for (int i = 1; i <= ITERATIONS; i++) {
            SumVector[] tmp = redCluster;
            redCluster = blackCluster;
            blackCluster = tmp;
            for (int p= 0; p<POINTS; p++) {
                int closest = -1;
                float closestDist = Float.MAX_VALUE;
                float[] point = points[p];
                for (int k=0; k<myK; k++) { // compute closest mean in cluster.
                    float dist = blackCluster[k].distance(point);
                    if (dist < closestDist) {
                        closestDist = dist;
                        closest = k;
                    }
                }
                redCluster[closest].addIn(point);
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
        return redCluster;  
    }
    
    public static void main (String[] args) {
        float[][] points = PointsFactory.generateRandomPoints(POINTS, DIM);
        SumVector[] result =  new KMeansSequential(DIM).computeMeans(K, points);
        for (int k=0; k<K; k++) {
            result[k].print();
        }
    }
}

// vim: shiftwidth=4:tabstop=4:expandtab
