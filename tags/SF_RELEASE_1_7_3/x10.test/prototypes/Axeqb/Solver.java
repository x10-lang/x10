class Solver {
    
    public static void main(String[] args) {
        int N = 32;
        boolean debug = false;
        boolean computeResidual = true;
        int count = 1;  // 262144
        for (int i = 0; i < args.length; ++i) {
            if (args[i].charAt(0) == '-') {
                if (args[i].equals("-debug")) {
                    debug = true;
                }
                if (args[i].equals("-nr")) {
                    computeResidual = false;
                }
                if (args[i].equals("-count")) {
                    ++i;
                    count = java.lang.Integer.parseInt(args[i]);
                }
            } else {
                N = java.lang.Integer.parseInt(args[i]);
            }
        }
        long accumTime = 0;
        
        // Force Math.abs to be a resolved method!
        double dddd = Math.abs((double) args.length - N);
        
        Solver solver = new Solver(N, debug);
        double residual = 0;
        boolean nonSingular;
        for (int x = 0; x < count; ++x) {
            solver.setup();
            if (debug) solver.print();
            //long start_time0 = System.currentTimeMillis();
            long start_time0 = System.nanoTime();
            nonSingular = solver.LUFactorize();
            if (debug) solver.print();
            if (!Solver.computeYDirectly) {
                solver.LyeqPb();
                if (debug) solver.print();
            }
            solver.Uxeqy();
            long finish_time0 = System.nanoTime();
            //        long finish_time0 = System.currentTimeMillis();
            long start_time1 = 0;
            long finish_time1 = 0;
            if (debug) solver.print();
            solver.setup();
            if (computeResidual && nonSingular) {
                start_time1 = System.nanoTime();
                residual = solver.computeResidual();
                finish_time1 = System.nanoTime();
            }
            if (!nonSingular) {
                System.out.println("A is singular");
            }
            accumTime += (finish_time0-start_time0) + (finish_time1-start_time1);
        }
        System.out.println("residual = " + residual);
        String kindoftime = "microseconds";
        System.out.println("Number of " + kindoftime + ": " + (accumTime/1000));
    }

    void setup() {
        int init = 1325;
        for (int col = 0; col < N; ++col) {
            double bv = 0;
            for (int row = 0; row < N; ++row) {
                init = 3125 * init % 65535;
                final double value = (init - 32768.0)/16384.0;
                A[col][row] = value + row - col;
                bv += value;
            }
            b[col] = bv;
            if (Solver.computeYDirectly) {
                y[col] = b[col];
            }
        }
    }

    Solver(int N_, boolean debug_) {
        N = N_;
        A = new double[N][N];
        b = new double[N];
        y = new double[N];
        x = new double[N];
        debug = debug_;
        nonSingular = true;
    }
    final int N;
    final double[][] A;      // A is an NxN matrix
    final double[] b;        // b is a column vector of length N
    final double[] y;
    final double[] x;
    final boolean debug;
    boolean nonSingular;
    static final boolean computeYDirectly = true;

    boolean LUFactorize() {
        
        double largest_val = Math.abs(A[0][0]);
        int largest_row = 0;
        for (int row = 1; row < N; ++row) {
            if (Math.abs(A[0][row]) > largest_val) {
                largest_val = Math.abs(A[0][row]);
                largest_row = row;
            }
        }
        if (largest_val < 1e-32) {
            nonSingular = false;
            return false;
        }
        
        for (int diag_index = 0; diag_index < N; ++diag_index) {
            
            if (largest_row != diag_index) {
                double swp = A[diag_index][diag_index];
                A[diag_index][diag_index] = A[diag_index][largest_row];
                A[diag_index][largest_row] = swp;
                double[] d = Solver.computeYDirectly ? y : b;
                // while we're here, we'll swap rows of b (or y)
                double swpb = d[diag_index];
                d[diag_index] = d[largest_row];
                d[largest_row] = swpb;
            }
        
            if (Math.abs(A[diag_index][diag_index]) < 1e-32) {
                // This matrix is Singular...Gaussian elimination will not work
                nonSingular = false;
                return false;
            } else {
                // now for each row, compute the multipliers
                for (int row = diag_index+1; row < N; ++row) {
                    A[diag_index][row] /= A[diag_index][diag_index];
                }
                
                if (largest_row != diag_index) {
                    // first we swap all the rows in columns to the left or
                    // right of out current column
                    for (int col = 0; col < N; ++col) {
                        if (col != diag_index) {
                            double swpb = A[col][diag_index];
                            A[col][diag_index] = A[col][largest_row];
                            A[col][largest_row] = swpb;
                        }
                    }
                }

                // then we subtract the scaled row for all columns to the
                // right (+ the y vector if we want to compute it directly.)
                if (Solver.computeYDirectly) {
                    // we should common this ...
                    double y_diag_index = y[diag_index];
                    for (int row = diag_index+1; row < N; ++row) {
                        double m = A[diag_index][row];
                        y[row] = y[row] - m * y_diag_index; //y[diag_index];
                    }
                }
                largest_val = 0;
                largest_row = diag_index+1;
                if (diag_index + 1 < N) {
                    for (int row = diag_index+1; row < N; ++row) {
                        double m = A[diag_index][row];
                        double A_col_diag_index = A[diag_index+1][diag_index];
                        A[diag_index+1][row] = A[diag_index+1][row] - m * A_col_diag_index;//A[col][diag_index];
                        if (Math.abs(A[diag_index+1][row]) > largest_val) {
                            largest_val = Math.abs(A[diag_index+1][row]);
                            largest_row = row;
                        }
                    }
                }
                for (int col = diag_index + 2; col < N; ++col) {
                    // we should common this ...
                    double A_col_diag_index = A[col][diag_index];
                    for (int row = diag_index+1; row < N; ++row) {
                        double m = A[diag_index][row];
                        A[col][row] = A[col][row] - m * A_col_diag_index;//A[col][diag_index];
                    }
                }
            }
        }
        return true;
    }

    void LyeqPb() {
        if (!Solver.computeYDirectly && nonSingular) {
            // We have L (which is I + the multipliers below the diagonal)
            // and Pb, so we can proceed to compute y
            for (int diag_index = 0; diag_index < N; ++diag_index) {
                // We're going to compute yi.  Once we have it, we'll multiply
                // all of the multipliers in column i (below the main diagonal)
                // by yi.
                double yi = b[diag_index];
                for(int col = 0; col < diag_index; ++col) {
                    // here is where we would have to multiply by yi
                    // if we hadn't done so earlier
                    yi -= A[col][diag_index];
                }
                y[diag_index] = yi;
                for (int row = diag_index+1; row < N; ++row) {
                    A[diag_index][row] *= yi;
                }
            }
        }
    }
    
    void Uxeqy() {
        // We have U (which is the main diagonal and above) and
        // y which was computed in LyeqPb.  Now compute x
        if (nonSingular) {
            for (int diag_index = N-1; diag_index >= 0; --diag_index) {
                // We're going to compute xi.  Once we have it, we'll multiply
                // all of the values in column i by xi.
                double xi = y[diag_index];
                for (int col = diag_index+1; col < N; ++col) {
                    // here is where we would have to multiply by xi
                    // if we hadn't done so earlier
                    xi -= A[col][diag_index];
                }
                xi /= A[diag_index][diag_index];
                x[diag_index] = xi;
                for (int row = 0; row < diag_index; ++row) {
                    A[diag_index][row] *= xi;
                }
            }
        }
    }
    
    double computeResidual() {
        double rc = 0;
        for (int row = 0; row < N; ++row) {
            double s = 0;
            for (int col = 0; col < N; ++col) {
                double Av = A[col][row];
                double xv = x[col];
                s += (Av * xv);
            }
            double bv = b[row];
//            if (debug || Math.abs(s - bv) > 0.00000000001) {
//                System.err.println("s[" + row + "] = " + s +
//                                   " b[" + row + "] = " + bv);
//                if (Math.abs(s - bv) > 0.00000000001) rc = false;
//            }
            rc += Math.abs(s - bv);
        }
        return rc;
    }
    
    void print() {
        for (int row = 0; row < N; ++row) {
            for (int col = 0; col < N; ++col) {
                double v = A[col][row];
                System.out.print(v + " ");
            }
            double bv = b[row];
            System.out.print(": " + bv);
            double yv = y[row];
            System.out.print(": " + yv);
            double xv = x[row];
            System.out.println(": " + xv);
        }
        System.out.println("--------------------");
    }
}
