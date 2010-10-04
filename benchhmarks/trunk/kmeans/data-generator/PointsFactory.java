import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * A class to encapsulate the creation of an input set of 
 * Points for use in the KMeans program and the 
 * reading/writing of a set of points to data files.
 */
public class PointsFactory {
    private static final int cookie = 0x2badfdc0;
    private static final int version = 1;
    
    
    /**
     * Create numPoints random points each of dimension numDimensions.
     */
    public static float[][] generateRandomPoints(int numPoints, int numDimensions) {
        Random rnd = new Random(0);
        float[][] points = new float[numPoints][numDimensions];
        for (int i=0; i<numPoints; i++) {
            for (int j=0; j<numDimensions; j++) {
                points[i][j] = rnd.nextFloat();
            }
        }
        return points;
    }
    
    
    /**
     * Generate a set of random points and write them to a data file
     * @param fileName the name of the file to create
     * @param numPoints the number of points to write to the file
     * @param numDimensions the number of dimensions each point should have
     * @param seed, a random number seed to generate the points.
     * @return <code>true</code> on success, <code>false</code> on failure
     */
    public static boolean generateRandomPointsToFile(String fileName, int numPoints, int numDimensions, int seed, boolean includeMetaData) {
        try {
            Random rand = new Random(seed);
            File outputFile = new File(fileName);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile));
            if (includeMetaData) {
                out.writeInt(cookie);
                out.writeInt(version);
                out.writeInt(numPoints);
                out.writeInt(numDimensions);
            }
            int numFloats = numPoints * numDimensions;
            for (int i=0; i<numFloats; i++) {
                if (includeMetaData) {
                    out.writeFloat(rand.nextFloat());
                } else {
                    // legacy compatability with original kmeans data format.
                    out.writeInt(Integer.reverseBytes(Float.floatToIntBits(rand.nextFloat())));
                }
            }
            out.flush();
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file for writing "+fileName);
            return false;
        } catch (IOException e) {
            System.out.println("Error writing data to "+fileName);
            e.printStackTrace();
            return false;
        }
                
        return true;
    }

    
    /**
     * Write a set of points to a data file
     * @param fileName the name of the file to create
     * @param points the points to write
     * @return <code>true</code> on success, <code>false</code> on failure
     */
    public static boolean writePointsToFile(String fileName, float[][]points, boolean includeMetaData) {
        int numPoints = points.length;
        int i = -1;
        int j = -1;
        if (numPoints == 0) return false;
        int numDimensions = points[0].length;
        try {
            File outputFile = new File(fileName);
            DataOutputStream out = new DataOutputStream(new FileOutputStream(outputFile));
            if (includeMetaData) {
                out.writeInt(cookie);
                out.writeInt(version);
                out.writeInt(numPoints);
                out.writeInt(numDimensions);
            }
            for (i=0; i<numPoints; i++) {
                float[] point = points[i];
                if (point == null || point.length != numDimensions) {
                    outputFile.delete();
                    System.err.printf("Point %d is invalid; expected %d dimension points, found %d\n", i, numDimensions, point == null ? 0 : point.length);
                    return false;
                }
                for (j=0; j<numDimensions; j++) {
                    out.writeFloat(point[j]);
                }
            }
        
        } catch (FileNotFoundException e) {
            System.out.println("Unable to open file for writing "+fileName);
            return false;
        } catch (IOException e) {
            System.out.println("Error writing data to "+fileName+" ("+i+", "+j+")");
            e.printStackTrace();
            return false;
        }
                
        return true;
    }
    
    /**
     * Create numPoints random points each of dimension numDimensions.
     */
    public static float[][] readPointsFromFile(String fileName) {
        int i = 0;
        int j = 0;
        int numDimensions = 0;
        int numPoints = 0;
        float[][] points = null;
        
        try {
            DataInputStream data = new DataInputStream(new FileInputStream(new File(fileName)));
            int fc = data.readInt();
            if (fc != cookie) {
                System.err.printf("Invalid cookie.  Found %d but expected %d\n", fc, cookie);
            }
            int fv = data.readInt();
            if (fv != version) {
                System.err.printf("Invalid version.  Found %d but expected %d\n", fc, cookie);
            }            
            numPoints = data.readInt();
            numDimensions = data.readInt();
            points = new float[numPoints][numDimensions];
            System.out.printf("Reading %d %d-dimensional points from %s", numPoints, numDimensions, fileName);
            for (i=0; i<numPoints; i++) {
                for (j=0; j<numDimensions; j++) {
                    points[i][j] = data.readFloat();
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Unable to open file "+fileName);
            System.exit(-1);
        } catch (IOException e) {
            System.err.printf("File did not contain enough data for %d %d-dimenstional points\n", numPoints, numDimensions);
            System.err.printf("Only found %d floats; expected to find %d\n", i*numDimensions+j, numPoints*numDimensions);
            e.printStackTrace();
            System.exit(-1);
        }
        
        return points;
    }
}
