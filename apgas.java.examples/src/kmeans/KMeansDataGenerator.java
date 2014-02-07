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

package kmeans;

/**
 * Driver routine to generate data sets for the KMeans problem
 */
public class KMeansDataGenerator {

    public static void main(String[] args) {
        int numPoints = 1000;
        int numDimensions = 4;
        int seed = 2112;
        int argIndex = 0;
        String fileName = "points.dat";
        while (argIndex < args.length) {
            String arg = args[argIndex++];
            if (arg.equals("-p")) {
                numPoints = Integer.parseInt(args[argIndex++]);   
            } else if (arg.equals("-d")) {
                numDimensions = Integer.parseInt(args[argIndex++]);
            } else if (arg.equals("-s")) {
                seed = Integer.parseInt(args[argIndex++]);
            } else {
                fileName = arg;
            }
        }
        
        System.out.printf("Generating %d points of %d dimensions with seed %d into %s\n", numPoints, numDimensions, seed, fileName);
        KMeansDataSet.generateRandomPointsToFile(fileName, numPoints, numDimensions, seed);
    }

}
