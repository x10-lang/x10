import java.io.Serializable;

import com.ibm.apgas.Pool;
import com.ibm.apgas.Task;

public class NQueens {
    public static int[] expectedSolutions = new int[] { 0, 1, 0, 0, 2, 10, 4, 40, 92, 352, 724, 2680, 14200, 73712, 365596, 2279184, 14772512 };
    
    public static int numSolutions = 0;
    
    public static void main(String[] args) {
        final int n = args.length > 0 ? Integer.parseInt(args[0]) : 8;
        Pool p = new Pool(new Task() {
            public void body() {
                solveNQueens(n);
              }
        });
        p.start();
    }

    static void solveNQueens(final int n) {
        long start = System.nanoTime();
        Pool.runFinish(new Task(){
            public void body() {
                int P = Pool.numPlaces();
                int baseSize = n / P;
                int extra = n - (baseSize*P);
                for (int i=0; i<P; i++) {
                    final int start = i*baseSize + (i<extra ? i : extra);
                    final int end = start+baseSize+(i<extra ? 0 : -1);
                    Pool.runAsync(i, new Task() {
                        public void body() {
                            new Board(n).search(start, end);
                            if (Pool.here() != 0) reportResult();
                        }

                        void reportResult() {
                            final int tmp = numSolutions;
                            Pool.runAsync(0, new Task(){
                                public void body() {
                                    addSolutions(tmp);
                                }});
                        }
                    });
                }
            }});
        long stop = System.nanoTime();
        boolean correct = numSolutions == expectedSolutions[n];
        double time = ((double)(stop - start)) / 1e9;
        System.out.println("NQueens "+n+"(P = "+Pool.numPlaces()+") has "+numSolutions+" solutions "+
                           (correct ? "(ok)." : "(wrong).")+" Time "+time+" seconds");
    }
    
    static synchronized void addSolutions(int n) {
        numSolutions += n;
    }
    
    static class Board implements Serializable {
        int n;
        int[] q;
        
        Board(int n) {
            this.n = n;
            q = new int[0];
        }
        
        Board(int n, int[] old, int newQ) {
            this.n = n;
            int[] tmp = new int[old.length+1];
            System.arraycopy(old, 0, tmp, 0, old.length);
            tmp[old.length] = newQ;
            q = tmp;
        }
        
        boolean safe(int j) {
            int n = q.length;
            for (int k=0; k<n; k++) {
                if (j == q[k] || Math.abs(n-k) == Math.abs(j-q[k])) return false;
            }
            return true;
        }
        
        void search(int low, int high) {
            for (int k=low; k<=high; k++) {
                if (safe(k)) {
                    new Board(n, q, k).search();
                }
            }
        }
        
        void search() {
            if (q.length == n) {
                addSolutions(1);
            } else {
                search(0, n-1);
            }
        }
    }
    
}
