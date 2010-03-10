

/**
 * A class that represents the sum of 'count' number of vectors.
 */
class SumVector {
    private float[] summedVector;
    private int count;
    
    SumVector(float[] initialValue) {
        summedVector = new float[initialValue.length];
        System.arraycopy(initialValue, 0, summedVector, 0, initialValue.length);
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