import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Random;

/**
 * A class to encapsulate the creation of an input set of 
 * Points for use in the KMeans program.
 * The Factory will either read the points from a file
 * or use a random number generator to create the points.
 */
public class PointsFactory {
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
     * Create numPoints random points each of dimension numDimensions.
     */
    public static float[][] readPointsFromFile(String fileName, int numPoints, int numDimensions) {
        int i = 0;
        int j = 0;
        float[][] points = new float[numPoints][numDimensions];
        try {
            DataInputStream data = new DataInputStream(new FileInputStream(new File(fileName)));
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
        }
        
        return points;
    }
     

}
