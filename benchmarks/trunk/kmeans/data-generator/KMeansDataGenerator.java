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
        boolean metaData = false;
        while (argIndex < args.length) {
            String arg = args[argIndex++];
            if (arg.equals("-p")) {
                numPoints = Integer.parseInt(args[argIndex++]);   
            } else if (arg.equals("-d")) {
                numDimensions = Integer.parseInt(args[argIndex++]);
            } else if (arg.equals("-s")) {
                seed = Integer.parseInt(args[argIndex++]);
            } else if (arg.equals("-m")) {
                metaData = true;
            } else {
                fileName = arg;
            }
        }
        
        System.out.printf("Generating %d points of %d dimensions with seed %d into %s\n", numPoints, numDimensions, seed, fileName);
        PointsFactory.generateRandomPointsToFile(fileName, numPoints, numDimensions, seed, metaData);
    }

}
