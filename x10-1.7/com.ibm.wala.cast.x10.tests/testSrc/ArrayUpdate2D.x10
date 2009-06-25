public class ArrayUpdate2D {
    public static void main(String[] args) {
	new ArrayUpdate2D().run();
    }
    public void run() {
	final int N= 5;
	int[.] a= createArray(N);
	System.out.println("Before update:");
	print(a, N);
	int[.] iters= new int[[1:5]] (point p) { return p[0]; };
	for(point p[i]: iters.region) {
	    update(a, N);
	    System.out.println("After update #" + i + ":");
	    print(a, N);
	}
    }
    public void print(int[.] a, int N) {
	for(point p[i]: [0:N]) {
	    if (i == 0)
		System.out.print("a[] = ");
	    else
		System.out.print("      ");
	    for(point p1[j]: [0:N]) {
		System.out.print(a[i,j] + " ");
	    }
	    System.out.println();
	}
    }
    private int[.] createArray(int N) {
	region(:self.rank==2) r= [0:N, 0:N];
	int[.] result= new int[r] (point p) { return 0; };
	region(:self.rank==2) interior= [1:N-1,1:N-1];
	region border= ((region (:self.rank==2)) result.region) - ((region (:self.rank==2)) interior);
	for(point p: border) {
	    result[p]= 1;
	}
	return result;
    }
    public int[.] copyOf(int[.] a) {
	int[.] res= new int[a.region];
	for(point p: a.region) {
	    res[p]= a[p];
	}
	return res;
    }
    public void copyTo(int[.] a, int[.] b) {
	for(point p: a.region) {
	    b[p]= a[p];
	}
    }
    private int[.] createKernel() {
	final int[.] kernel= new int[[0:2,0:2]];
	kernel[0,0]= 1; kernel[0,1]= 2; kernel[0,2]= 1;
	kernel[1,0]= 2; kernel[1,1]= 4; kernel[1,2]= 2;
	kernel[2,0]= 1; kernel[2,1]= 2; kernel[2,2]= 1;
	return kernel;
    }
    public void update(int[.] a, int N) {
	region(:self.rank==2) interior= [1:N-1,1:N-1];
	final int[.] kernel= createKernel();
	int[.] res= copyOf(a);
	int result= 0;
        for(point p[pi,pj]: interior) {
            int sum= 0;
            for(point pk[ki,kj]: kernel) {
        	point ap= [pi+ki-1, pj+kj-1];
        	sum += a[ap] * kernel[pk];
            }
            res[p]= sum;
        }
        copyTo(res, a);
    }
}
