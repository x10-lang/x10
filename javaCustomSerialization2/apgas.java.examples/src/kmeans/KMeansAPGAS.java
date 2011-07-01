package kmeans;

import java.util.Arrays;

import com.ibm.apgas.Pool;
import com.ibm.apgas.Task;

/**
 *
 */
public class KMeansAPGAS {    
    // Statics used to hold place-local data needed by asyncs
    private static float[] points;
    private static int[] clusterCounts;
    private static int[] closestCluster;
    
    private static float[] incomingClusterPoints;
    private static int[] incomingClusterCounts;
    
    private static int myK;
    private static int numPoints;
    private static int numDimensions;
    
    // per iteration timing data
    private static int iteration;
    private static long[] kernelNanos;
    private static long[] kernel2Nanos;
    private static long[] allToAllNanos;
    private static long[] localReduceNanos;
        
    /**
     * Initialize data structures needed for the kMeans computation
     */
    public static void initialize(int _myK, int numIterations, int _numPoints, int _numDimensions, float[] _points) {
        points = _points;
        myK = _myK;
        numPoints = _numPoints;
        numDimensions = _numDimensions;
        
        clusterCounts = new int[myK];
        closestCluster = new int[numPoints];
        
        kernelNanos = new long[numIterations];
        kernel2Nanos = new long[numIterations];
        allToAllNanos = new long[numIterations];
        localReduceNanos = new long[numIterations];
    }

    private static native void nativeInit(int myK, int numPoints, int numDimensions, float[] points);
    
    /**
     * Given the current cluster, compute a new cluster
     */
    public static void computeNewLocalClusters(float[] clusterPoints) {
        kernelNanos[iteration] = - System.nanoTime();

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

        // This really should be an all-to-all collective using Teams, but instead we
        // will do a point-to-point send to place 0, which will receive the 
        // data, accumulate it, reduce it, then scatter it back as the argument
        // to an async to start the next iteration.
        final float[] outgoingClusterPoints = clusterPoints;
        final int[] outgoingClusterCounts = clusterCounts;
        Pool.runAsync(0, new Task(){
            public void body() {
                synchronized(incomingClusterPoints) {
                    for (int i=0; i<outgoingClusterPoints.length; i++) {
                        incomingClusterPoints[i] += outgoingClusterPoints[i];
                    }
                }
                synchronized(incomingClusterCounts) {
                    for (int i=0; i<outgoingClusterCounts.length; i++) {
                        incomingClusterCounts[i] += outgoingClusterCounts[i];
                    }
                }
            }});
        now = System.nanoTime();
        allToAllNanos[iteration] += now;
        
        iteration += 1;
    }

    private static double toMillis(long nanoTime) {
        return ((double)nanoTime)/1e6;
    }
    
    public static void main (final String[] args) {
        Pool p = new Pool(new Task() {
            public void body() {
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

                final KMeansDataSet data = KMeansDataSet.readPointsFromFile(fileName);
                final float[] currentCluster = new float[K*data.numDimensions];
                System.arraycopy(data.points, 0, currentCluster, 0, currentCluster.length);

                // Only place zero needs these arrays (incoming buffers for "collective")
                incomingClusterCounts = new int[K];
                incomingClusterPoints = new float[K*data.numDimensions];

                // Initialize all places, evenly splitting data.points between them
                final int pointsPerPlace = data.numPoints/Pool.numPlaces();
                final int capturedK = K;
                final int capturedIterations = iterations;
                Pool.runFinish(new Task(){
                    public void body() {
                        for (int i = 0; i<Pool.numPlaces(); i++) {
                            final int start = i * pointsPerPlace;
                            final int stop = Math.min(start+pointsPerPlace-1, data.points.length);
                            final int numPoints = stop-start+1;
                            final float[] points = new float[numPoints*data.numDimensions];
                            System.arraycopy(data.points, start, points, 0, points.length);
                            System.out.println("Sending points "+start+"..."+stop+" to place "+i);
                            Pool.runAsync(i, new Task(){
                                public void body() {
                                    initialize(capturedK, capturedIterations, numPoints, data.numDimensions, points);
                                }});
                        }
                    }});

                // Do the requested number of iterations.
                long start = System.nanoTime();
                for (int iter = 0; iter < iterations; iter++) {
                    Pool.atEach(new Task(){
                        public void body() {
                            computeNewLocalClusters(currentCluster);
                        }});

                    // local reduction to get the new clusters
                    // Adjust cluster coordinates by dividing each point value
                    // by the number of points in the cluster
                    localReduceNanos[iter] = -System.nanoTime();
                    for (int k=0; k<K; k++) {
                        float tmp = (float)incomingClusterCounts[k];
                        for (int dim=0; dim<numDimensions; dim++) {
                            incomingClusterPoints[k*numDimensions+dim] /= tmp;
                        }
                    }
                    System.arraycopy(incomingClusterPoints, 0, currentCluster, 0, currentCluster.length);
                    Arrays.fill(incomingClusterCounts, 0);
                    Arrays.fill(incomingClusterPoints, 0.0f);
                    localReduceNanos[iter] += System.nanoTime();
                }
                long stop = System.nanoTime();
                
                // All done. Print the results
                for (int k=0; k<K; k++) {
                    for (int j=0; j<data.numDimensions; j++) {
                        if (j>0) System.out.print(" ");
                        System.out.print(currentCluster[k*data.numDimensions+j]);
                    }
                    System.out.println();
                }
                System.out.println();

                long totalKernelNanos = 0;
                long totalKernel2Nanos = 0;
                long totalAllToAllNanos = 0;
                long totalLocalReduceNanos = 0;
                System.out.println("Per iteration phase timings (kernel1, kernel2, collective, localReduce)");
                for (int i=0; i<iterations; i++) {
                    System.out.printf("%3.5f %3.5f %3.5f %3.5f\n",toMillis(kernelNanos[i]), toMillis(kernel2Nanos[i]), toMillis(allToAllNanos[i]), toMillis(localReduceNanos[i]));
                    totalKernelNanos += kernelNanos[i];
                    totalKernel2Nanos += kernel2Nanos[i];
                    totalAllToAllNanos += allToAllNanos[i];
                    totalLocalReduceNanos += localReduceNanos[i];
                }
                System.out.println("-------------------------------------------------------");
                System.out.printf("%3.5f %3.5f %3.5f %3.5f\n", toMillis(totalKernelNanos), toMillis(totalKernel2Nanos), toMillis(totalAllToAllNanos), toMillis(totalLocalReduceNanos));
                System.out.println("Total time (seconds)"+((double)(stop-start)/1e9));
            }
        });
        p.start();
    }
}

