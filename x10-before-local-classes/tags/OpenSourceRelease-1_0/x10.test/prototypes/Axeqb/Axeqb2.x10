class Axeqb2 {
    public static void main(String[] args) {
        int N = 3;
        boolean debug = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].charAt(0) == '-') {
                if (args[i].equals("-debug")) {
                    debug = true;
                }
            } else {
                N = java.lang.Integer.parseInt(args[i]);
            }
        }
        Axeqb2 solver = new Axeqb2(N, debug);
        solver.setup();
        if (debug) solver.print();
        long start_time = System.currentTimeMillis();
        try {
            solver.LUFactorize();
        } catch (Error e) {
            System.err.println(e);
        }
        if (debug) solver.print();
        if (!Axeqb2.computeYDirectly) solver.LyeqPb();
        if (debug) {
            solver.print();
            solver.checkLy();
        }
        solver.Uxeqy();
        long finish_time = System.currentTimeMillis();
        if (debug) solver.print();
        solver.setup();
        if (debug) solver.print();
        if (!solver.check()) System.err.println("FAILED");
        System.out.println("Number of milliseconds: " + (finish_time-start_time));
    }

    void setup() {
        finish {
           int init = 1325;
           for (int xcol = 0; xcol < N; ++xcol) {
              final int col = xcol;
              double bv = 0;
              for (int xrow = 0; xrow < N; ++xrow) {
                 final int row = xrow;
                 init = 3125 * init % 65535;
                 final double value = (init - 32768.0)/16384.0;
                 async(A.distribution[col,0]) A[col,row] = value;
                 bv += value;
              }
              final double fbv = bv;
              async(b.distribution) b[col] = fbv;
           }
        }
    }

    Axeqb2(int N_, boolean debug_) {
        N = N_;
        A = new double[dist.factory.blockCyclic([0:N-1,0:N-1], N)];
        b = new double[[0:N-1]->place.FIRST_PLACE];
        y = new double[[0:N-1]->place.FIRST_PLACE];
        x = new double[[0:N-1]->place.FIRST_PLACE];
        debug = debug_;
    }
    final int N;
    final double[.] A;        // A is an NxN matrix
    final double[.] b;        // b is a column vector of length N
    final double[.] y;
    final double[.] x;
    final boolean debug;
    static final boolean computeYDirectly = true;

    // We really want to be able to allocate A using a column distrbution
    // something like:
    //    A = new double[dist.factory.column([0:N-1,0:N-1]);
    // however, that is currently not available.  Throughout this code,
    // we use A[col, row].  When column distributions are available,
    // we will change that to A[row, col]

    // Each element of a given column of A resides in the same place
    // In fact, we would like the elements of the column to be in
    // contiguous storage since that should help with caching.

    void LUFactorize() {
        if (Axeqb2.computeYDirectly) {
            finish async(place.FIRST_PLACE) {
                //foreach(point[i] : [0 : N-1]) {
                for (int i = 0; i < N; ++i) {
                    y[i] = b[i];
                }
            }
        }
        for (int colx = 0; colx < N; ++colx) {
            final int diag_index = colx;
            finish async(A.distribution[diag_index,0]) {
                // this really should be finish async(A.distribution[0,diag_index])

                // here we would like array sub-sections and
                // a max position reduction operator
                // (assuming a column distribution...)
                // int largest_row = A[diag_index:N-1, diag_index].maxPos();
                // double largest_val = A[diag_index:N-1, diag_index].maxVal();
                double largest_val = A[diag_index, diag_index];
                double swp = largest_val;
                int largest_row = diag_index;
                for (int row = diag_index; row < N; ++row) {
                    if (Math.abs(A[diag_index, row]) > Math.abs(largest_val)) {
                        largest_val = A[diag_index, row];
                        largest_row = row;
                    }
                }
                final int swp_row = largest_row;

                // we now need to swap rows "diag_index" and "largest_row"
                // in the entire matrix A and also in the vector b
                // we'll do our own column first
                if (largest_row != diag_index) {
                    A[diag_index, largest_row] = swp;
                    A[diag_index, diag_index] = largest_val;
                    if (!Axeqb2.computeYDirectly) {
                        // while we're here, we'll swap rows of b
                        async(place.FIRST_PLACE) {
                            double swpb = b[diag_index];
                            b[diag_index] = b[swp_row];
                            b[swp_row] = swpb;
                        }
                    }
                    // and also we'll swap the row in all the columns
                    // to the left
                    //foreach (point[xcol] : [0 : diag_index-1]) {
                    for (int xxcol = 0; xxcol < diag_index; ++xxcol) {
                        final int xcol = xxcol;
                        async(A.distribution[xcol, 0]) {
                            // really, async(A.distribution[0, xcol])
                            double swpb = A[xcol, diag_index];
                            A[xcol, diag_index] = A[xcol, swp_row];
                            A[xcol, swp_row] = swpb;
                        }
                    }
                }

                if (A[diag_index, diag_index] == 0) {
                    throw new Error("A is singular");
                } else {
                    // now for each row, compute the multipliers
                    // if (use value array of multipliers) {
                    // all the multiplies in this column
                    final double[] multipliers = new double[N - diag_index - 1];
                    // }
                    
                    //finish foreach(point[xrow] : [diag_index+1 : N-1]) {
                    finish for (int xrow = diag_index+1; xrow < N; ++xrow) {
                        A[diag_index, xrow] /= A[diag_index, diag_index];
                        // use a value array of multipliers...
                        // if (use value array of multipliers) {
                        multipliers[xrow - diag_index - 1] = A[diag_index, xrow];
                        // }
                    }
                    // finally, all the columns to the right have
                    // to do row swapping and subtract the scaled
                    // row.
                    // If we want to compute y as part of this major
                    // loop, we do the same thing as we do for all
                    // other columns and we do it first
                    if (Axeqb2.computeYDirectly) {
                        // don't really want a finish here
                        // would prefer a future that is forced
                        // after the following loop
                        finish async(place.FIRST_PLACE) {
                            if (swp_row != diag_index) {
                                double swpb = y[diag_index];
                                y[diag_index] = y[swp_row];
                                y[swp_row] = swpb;
                            }
                            //foreach(point[xrow] : [diag_index+1 : N-1]) {
                            for (int xrow = diag_index+1; xrow < N; ++xrow) {
                                // if (use value array of multipliers) {
                                //   we can use the value array we built
                                double m = multipliers[xrow - diag_index - 1];
                                // } else {
                                //   we need to get the multiplier from the
                                //   other place
                                // double m = future(A.distribution[diag_index,0]) { A[diag_index, xrow] }.force();
                                // }
                                y[xrow] = y[xrow] - m * y[diag_index];
                            }
                        }
                    }
                    //foreach (point[xcol] : [diag_index+1 : N-1]) {
                    for (int xxcol = diag_index+1; xxcol < N; ++xxcol) {
                        final int xcol = xxcol;
                        async(A.distribution[xcol,0]) {
                            // really async(A.distribution[0,xcol])
                            if (swp_row != diag_index) {
                                // first swap the rows in this column
                                double swpb = A[xcol, diag_index];
                                A[xcol, diag_index] = A[xcol, swp_row];
                                A[xcol, swp_row] = swpb;
                            }
                            //foreach(point[xrow] : [diag_index+1 : N-1]) {
                            for (int xrow = diag_index+1; xrow < N; ++xrow) {
                                // if (use value array of multipliers) {
                                //   we can use the value array we built
                                double m = multipliers[xrow - diag_index - 1];
                                // } else {
                                //   we need to get the multiplier from the
                                //   other place
                                // double m = future(A.distribution[diag_index,0]) { A[diag_index, xrow] }.force();
                                // }
                                A[xcol, xrow] = A[xcol, xrow] - m * A[xcol, diag_index];
                            }
                        }
                    }
                }
            }
        }
    }

    void LyeqPb() {
        // We have L (which is I + the multipliers below the diagonal)
        // and Pb, so we can proceed to compute y
        finish async(place.FIRST_PLACE) {
            for (int i = 0; i < N; ++i) {
                final int diag_index = i;
                finish async(A.distribution[diag_index, 0]) {
                    // async(A.distribution[0, diag_index])

                    // We're going to compute yi at the place holding
                    // column i.  Once we have it, we'll multiply all
                    // of the multipliers (below the main diagonal) by
                    // yi.  This could potentially be vectorized which
                    // is one reason we do it.  The other is to avoid
                    // having to re-fetch the yi value in subsequent steps.
                    final double[.] sm = new double[[0:diag_index]->here];
                    sm[0] = future(b.distribution[diag_index]) { -(b[diag_index]) }.force();
                    //finish foreach(point[xcol] : [0 : diag_index-1]) {
                    finish for (int xxcol = 0; xxcol < diag_index; ++xxcol) {
                        final int xcol = xxcol;
                        // here is where we would have to multiply by yi
                        // if we hadn't done so earlier
                        sm[xcol+1] = future(A.distribution[xcol, diag_index]) { A[xcol, diag_index] }.force();
                    }
                    final double yi = -(sm.sum());
                    async(place.FIRST_PLACE) {
                        y[diag_index] = yi;
                    }
                    //foreach(point[xrow] : [diag_index+1 : N-1]) {
                    for (int xrow = diag_index+1; xrow < N; ++xrow) {
                        A[diag_index, xrow] *= yi;
                    }
                }
            }
        }
    }

    void Uxeqy() {
        // We have U (which is the main diagonal and above) and
        // y which was computed in LyeqPb.  Now compute x
        finish async(place.FIRST_PLACE) {
            for (int i = N-1; i >= 0; --i) {
                final int diag_index = i;
                finish async(A.distribution[diag_index, 0]) {
                    // async(A.distribution[0, diag_index])

                    // We're going to compute xi at the place holding
                    // column i.  Once we have it, we'll multiply all
                    // of the values in that column by xi.
                    // This could potentially be vectorized which
                    // is one reason we do it.  The other is to avoid
                    // having to re-fetch the xi value in subsequent steps.
                    final double[.] sm = new double[[diag_index:N-1]->here];
                    sm[diag_index] = future(b.distribution[diag_index]) { -(y[diag_index]) }.force();
                    //finish foreach(point[xcol] : [diag_index+1 : N-1]) {
                    finish for (int xxcol = diag_index+1; xxcol < N; ++xxcol) {
                        final int xcol = xxcol;
                        // here is where we would have to multiply by xi
                        // if we hadn't done so earlier
                        sm[xcol] = future(A.distribution[xcol, diag_index]) { A[xcol, diag_index] }.force();
                    }
                    
                    final double xi = -(sm.sum())/A[diag_index, diag_index];
                    async(place.FIRST_PLACE) {
                        x[diag_index] = xi;
                    }
                    //foreach(point[xrow] : [0 : diag_index-1]) {
                    for (int xrow = 0; xrow < diag_index; ++xrow) {
                        A[diag_index, xrow] *= xi;
                    }
                }
            }
        }
    }

    boolean check() {
        boolean rc = true;
        for (int xrow = 0; xrow < N; ++xrow) {
            final int row = xrow;
            double s = 0;
            for (int xcol = 0; xcol < N; ++xcol) {
                final int col = xcol;
                double Av = future(A.distribution[col, row]) { A[col, row] }.force();
                double xv = future(x.distribution[col]) { x[col] }.force();
                s += (Av * xv);
            }
            double bv = future(b.distribution[row]) { b[row] }.force();
            if (debug || Math.abs(s - bv) > 0.00000000001) {
                System.err.println("s[" + xrow + "] = " + s +
                                   " b[" + xrow + "] = " + bv);
                if (Math.abs(s - bv) > 0.00000000001) rc = false;
            }
        }
        return rc;
    }
    
    void checkLy() {
        for (int xrow = 0; xrow < N; ++xrow) {
            final int row = xrow;
            double s = 0;
            for (int xcol = 0; xcol < N; ++xcol) {
                final int col = xcol;
                double Av;
                if (col < row) {
                    Av = future(A.distribution[col, row]) { A[col, row] }.force();
                } else if (col == row) {
                    Av = 1;
                } else {
                    Av = 0;
                }
                double yv = future(y.distribution[col]) { y[col] }.force();
                s += (Av * yv);
            }
            double bv = future(b.distribution[row]) { b[row] }.force();
            System.out.println("Ly[" + xrow + "] = " + s + "(bogus due to scaled Av values) " +
                               " Pb[" + xrow + "] = " + bv);
        }
    }
    
    void print() {
        for (int xrow = 0; xrow < N; ++xrow) {
            final int row = xrow;
            for (int xcol = 0; xcol < N; ++xcol) {
                final int col = xcol;
                double v = future(A.distribution[col, row]) { A[col, row] }.force();
                System.out.print(v + " ");
            }
            double bv = future(b.distribution[row]) { b[row] }.force();
            System.out.print(": " + bv);
            double yv = future(y.distribution[row]) { y[row] }.force();
            System.out.print(": " + yv);
            double xv = future(x.distribution[row]) { x[row] }.force();
            System.out.println(": " + xv);
        }
        System.out.println("--------------------");
    }
}
