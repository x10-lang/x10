import java.io.Serializable;



/**
 * A class that represents the sum of 'count' number of vectors.
 */
class SumVector implements Serializable {
    private static final long serialVersionUID = -6202104451669002837L;
    
    private float[] summedVector;
    private int count;
    
    SumVector(int dim, float[] points, int pt) {
        summedVector = new float[dim];
        System.arraycopy(points, pt*dim, summedVector, 0, dim);
        count = 0;
    }

    SumVector(int dim) {
        summedVector = new float[dim];
        count = 0;
    }

    public void makeZero() {
        java.util.Arrays.fill(summedVector, 0.0f);
        count = 0;
    }
    
    public void addIn(float[] a) {
        for (int i=0; i<a.length; i++) {
            summedVector[i] += a[i];
        }
        count++;
    }
 
    public void addIn(int nd, float[] points, int point) {
        int offset = nd * point;
        for (int i=0; i<nd; i++) {
            summedVector[i] += points[offset + i];
        }
        count++;
    }

    public void addIn(SumVector other) {
        for (int i=0; i<summedVector.length; i++) {
            summedVector[i] += other.summedVector[i];
        }
        count += other.count;
    }
    
    public void divide(int f) {
        for (int i=0; i<summedVector.length; i++) {
            summedVector[i] /= f;
        }
    }
    
    public float distance(float[] a) {
        float dist =0.0F;
        for (int i=0; i<summedVector.length; i++) {
            float tmp = summedVector[i] - a[i];
            dist += tmp*tmp;
        }
        return dist;
    }
    
    public float distance(int nd, float[] points, int point) {
        float dist =0.0F;
        int offset = nd * point;
        for (int i=0; i<summedVector.length; i++) {
            float tmp = summedVector[i] - points[offset+i];
            dist += tmp*tmp;
        }
        return dist;
    }

    public float distance(SumVector vec) {
        return distance(vec.summedVector);
    }
    
    public void print() {
        System.out.println();
        for (int i=0; i<summedVector.length; i++) {
            if (i>0) System.out.print(" ");
            System.out.print(summedVector[i]);
        }
    }
    
    public void normalize() { 
        divide(count);
    }
    
    public int count() { 
        return count;
    }
    
    public float getSumElement(int i) { return summedVector[i]; }
}