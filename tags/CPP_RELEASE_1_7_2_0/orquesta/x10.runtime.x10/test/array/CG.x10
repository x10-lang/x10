public class CG extends TestArray {

    abstract class Data {
	double [] A;   // the data
	int [] cols;   // the column index for each item in A
	int [] rows;   // the location in A for the start of each success row
	double [] b;   // solve for Ax=b
    }


    // a 16x16 tri-diagonal matrix
    Data data1 = new Data() {{
	A = new double[] {2, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1,  2, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1, 1, 2, 1,  1, 2,  1,  1,  2,  1,  1,  2,  1,  1,  2,  1,  1,  2,  1,  1,  2     };
	cols = new int[] {0, 1, 0, 1, 2, 1, 2, 3, 2, 3, 4, 3,  4, 5, 4, 5, 6, 5, 6, 7, 6, 7, 8, 7, 8, 9, 8, 9, 10, 9, 10, 11, 10, 11, 12, 11, 12, 13, 12, 13, 14, 13, 14, 15, 14, 15    };
	rows = new int[] {0,    2,       5,       8,       11,       14,      17,      20,      23,      26,       29,        32,         35,         38,     41,         44,         46};
	b = new double[] {6, 7, 0, 1, 2, 3, 5, 3, 2, 1, 3, 5, 8, 14, 15, 16};
    }};


    Mat makeIt(Data data) {
	int n = data.rows.length-1;
	Mat mat = new Mat(n);
	for (int r=0; r<n; r++) {
	    int cFirst = data.cols[data.rows[r]];
	    int cLast = data.cols[data.rows[r+1]-1];
	    double [] value = new double[cLast-cFirst+1];
	    for (int j=0; j<value.length; j++)
		value[j] = data.A[data.rows[r]+j];
	    mat.addBlock(r, cFirst, value);
	}
	return mat;
    }


    public void run() {

	Mat A = makeIt(data1);
	Vec b = new Vec(data1.b);
	Vec x0 = new Vec(new double[16]);

        Vec x = x0;
        Vec r = b.minus(A.times(x0));
        Vec p = r;
                           
        for (int i=0; i<20; i++) {

	    pr(i + " r=" + r.dot(r));

            // hard stuff
            Vec q = A.times(p);

            // easy stuff
            double a = r.dot(r) / q.dot(p);
            x = x.plus(p.times(a));
            Vec r1 = r;
            r = r.minus(q.times(a));
            double beta = r.dot(r) / r1.dot(r1);
            p = r.plus(p.times(beta));

	    Vec rr = A.times(x).minus(b);
	    pr(i + " Ax-b " + rr.dot(rr));
        }

        // answer
        pr("x " + x);
    }

}
