// xlC -O3 -qhot -qlist -qnomaf -qstrict -g -o Solver Solver.cpp
// xlC -O3 -qhot -g -o SolverX Solver.cpp
// Unfortunately, SolverX doesn't seem to generate non-singular matrices
// above around 1701x1701.  (Obviously, the matricies are the same for
// both Solver and SolverX, but some intermediate rounding steps are
// producing a lot more zeros for SolverX.)

#include <stdio.h>
#include <string.h>
#include <time.h>
#include <math.h>

class Solver {
public:
    Solver(unsigned int, bool);
    ~Solver();
    void setup(bool clearX);
    void print();
    bool LUFactorize();
    void LyeqPb();
    void Uxeqy();
    double computeResidual();
private:
    double** A_;
    double* b_;
    double* y_;
    double* x_;
    unsigned int N_;
    bool debug_;
    bool nonSingular_;
    static bool computeYDirectly;
};

bool Solver::computeYDirectly = true;

int main(int argc, char *argv[]) {
    unsigned int N = 32;
    bool debug = false;
    bool computeResidual = true;
    unsigned long count = 1;    // 262144
    for (unsigned int i = 1; i < argc; ++i) {
        if (argv[i][0] == '-') {
            if (strcmp(argv[i],"-debug") == 0) debug = true;
            if (strcmp(argv[i],"-nr") == 0) computeResidual = false;
            if (strcmp(argv[i],"-count") == 0) {
                ++i;
                sscanf(argv[i],"%lu", &count);
            }
        } else {
            sscanf(argv[i],"%d", &N);
        }
    }
    unsigned long accumTime = 0;
    double residual = 0;
    Solver *solver = new Solver(N, debug);
    for (unsigned long x = 0; x < count; ++x) {
        solver->setup(true);
        if (debug) solver->print();
        clock_t start_time0 = clock();
        bool nonSingular = solver->LUFactorize();
        if (debug) solver->print();
        solver->LyeqPb();
        solver->Uxeqy();
        clock_t finish_time0 = clock();
        clock_t start_time1 = 0;
        clock_t finish_time1 = 0;
        if (debug) solver->print();
        solver->setup(false);
        if (computeResidual && nonSingular) {
            start_time1 = clock();
            residual = solver->computeResidual();
            finish_time1 = clock();
        }
        if (!nonSingular) {
            fprintf(stderr, "A is singular\n");
        }
        accumTime += (finish_time0-start_time0)+(finish_time1-start_time1);
    }
    printf("residual = %e\n", residual);
    printf("Number of microseconds: %lu\n", accumTime);
    delete solver;
    return 0;
}

Solver::Solver(unsigned int N, bool debug) : N_(N), debug_(debug), nonSingular_(true) {
    A_ = new double *[N_];
    for (unsigned int i = 0; i < N_; ++i) {
        A_[i] = new double[N_];
    }
    b_ = new double[N_];
    y_ = new double[N_];
    x_ = new double[N_];
}

Solver::~Solver() {
    for (unsigned int i = 0; i < N_; ++i) {
        delete A_[i];
    }
    delete A_;
    delete b_;
    delete y_;
    delete x_;
}

void Solver::setup(bool clearX) {
    int init = 1325;
    for (unsigned int col = 0; col < N_; ++col) {
        double bv = 0;
        for (unsigned int row = 0; row < N_; ++row) {
            init = 3125 * init % 65535;
            double value = (init - 32768.0)/16384.0;
            A_[col][row] = value + row - col;
            bv += value;
        }
        b_[col] = bv;
        if (clearX) x_[col] = 0;
        if (Solver::computeYDirectly) {
            y_[col] = b_[col];
        } else {
            y_[col] = 0;
        }
    }

    #if 0
    // Perform equilibration to improve scaling
    double rowmax;
    for (unsigned int row = 0; row < N_; ++row) {
        rowmax = fabs(b_[row]);
        for (unsigned int col = 0; col < N_; ++col) {
            if (fabs(A_[col][row]) > rowmax) {
                rowmax = fabs(A_[col][row]);
            }
        }
        for (unsigned int col = 0; col < N_; ++col) {
            A_[col][row] /= rowmax;
        }
        b_[row] /= rowmax;
    }
    #endif
}

void Solver::print() {
    for (unsigned int row = 0; row < N_; ++row) {
        printf("row %d: ", row);
        for (unsigned int col = 0; col < N_; ++col) {
            double v = A_[col][row];       // LAPI ?
            printf("%.2e ", v);
        }
        printf(": %.3e: %.3e: %.3e\n", b_[row], y_[row], x_[row]);
    }
    printf("-----------------------------------\n");
}

bool Solver::LUFactorize() {

    double largest_val = fabs(A_[0][0]);
    int largest_row = 0;
    for (int row = 1; row < N_; ++row) {
        if (fabs(A_[0][row]) > largest_val) {
            largest_val = fabs(A_[0][row]);
            largest_row = row;
        }
    }
    if (largest_val < 1e-32) {
    //if (largest_val == 0) {
        nonSingular_ = false;
        return false;
    }
        
    for (unsigned int diag_index = 0; diag_index < N_; ++diag_index) {
        // we now need to swap rows "diag_index" and "largest_row"
        // in the entire matrix A and also in the vector b or y
        if (largest_row != diag_index) {
            double swp = A_[diag_index][diag_index];
            A_[diag_index][diag_index] = A_[diag_index][largest_row];
            A_[diag_index][largest_row] = swp;
            double *d = Solver::computeYDirectly ? y_ : b_;
            double swpb = d[diag_index];
            d[diag_index] = d[largest_row];
            d[largest_row] = swpb;
        }

        if (fabs(A_[diag_index][diag_index]) < 1e-32) {
        //if (fabs(A_[diag_index][diag_index]) == 0) {
            // This matrix is Singular...Gaussian elimination will not work
            nonSingular_ = false;
            return false;
        } else {
            
            
            // now for each row, compute the multipliers
            for (unsigned int row = diag_index+1; row < N_; ++row) {
                A_[diag_index][row] /= A_[diag_index][diag_index];
            }
            
            if (largest_row != diag_index) {
                // first we swap all the rows in columns to the left or
                // right of out current column
                // This is where the time is spent !!  (SolverX 1701)
                //MOD    976 12.41    ./SolverX
                // SYM    951 12.09     .Solver::LUFactorize()
                //  OFF    469  5.96      0x7a0
                //  OFF    280  3.56      0x7b0
                //  OFF     68  0.86      0x794
                for (unsigned int col = 0; col < N_; ++col) {
                    if (col != diag_index) {
                        double swpb = A_[col][diag_index];
                        A_[col][diag_index] = A_[col][largest_row];
                        A_[col][largest_row] = swpb;
                    }
                }
            }
            
            // finally, all the columns to the right have
            // to do row swapping and subtract the scaled
            // row.
            // If we want to compute y as part of this major
            // loop, we do the same thing as we do for all
            // other columns and we do it first
            if (Solver::computeYDirectly) {
                for (unsigned int row = diag_index+1; row < N_; ++row) {
                    double m = A_[diag_index][row];
                    y_[row] = y_[row] - m * y_[diag_index];
                }
            }
            largest_val = 0;
            largest_row = diag_index+1;
            #if 1       // This version is much faster in both C++ and Java!
            if (diag_index + 1 < N_) {
                for (int row = diag_index+1; row < N_; ++row) {
                    double m = A_[diag_index][row];
                    A_[diag_index+1][row] = A_[diag_index+1][row] - m * A_[diag_index+1][diag_index];
                    if (fabs(A_[diag_index+1][row]) > largest_val) {
                        largest_val = fabs(A_[diag_index+1][row]);
                        largest_row = row;
                    }
                }
            }
            for (unsigned int col = diag_index+1; col < N_; ++col) {
                for (int row = diag_index+1; row < N_; ++row) {
                    double m = A_[diag_index][row];
                    A_[col][row] = A_[col][row] - m * A_[col][diag_index];
                }
            }
            #else
            for (int row = diag_index+1; row < N_; ++row) {
                double m = A_[diag_index][row];
                for (unsigned int col = diag_index+1; col < N_; ++col) {
                    A_[col][row] = A_[col][row] - m * A_[col][diag_index];
                }
                if (fabs(A_[diag_index+1][row]) > largest_val) {
                    largest_val = fabs(A_[diag_index+1][row]);
                    largest_row = row;
                }
            }
            #endif
        }
    }
    return true;
}

void Solver::LyeqPb() {
    if (Solver::computeYDirectly || nonSingular_) {
        return;
    } else {
        // We have L (which is I + the multipliers below the diagonal)
        // and Pb, so we can proceed to compute y
        for (unsigned int diag_index = 0; diag_index < N_; ++diag_index) {
            // LAPI??
            // We're going to compute yi at the place holding
            // column i.  Once we have it, we'll multiply all
            // of the multipliers (below the main diagonal) by
            // yi.
            double yi = b_[diag_index];
            for(unsigned int col = 0; col < diag_index; ++col) {
                // here is where we would have to multiply by yi
                // if we hadn't done so earlier
                yi -= A_[col][diag_index];
            }
            y_[diag_index] = yi;
            for (unsigned int row = diag_index+1; row < N_; ++row) {
                A_[diag_index][row] *= yi;
            }
        }
    }
}

void Solver::Uxeqy() {
    // We have U (which is the main diagonal and above) and
    // y which was computed in LyeqPb or directly in LUFactorize.  Now compute x
    if (nonSingular_) {
        for (int diag_index = N_-1; diag_index >= 0; --diag_index) {
            // LAPI?  do this on the node containing column i
            // We're going to compute xi at the place holding
            // column i.  Once we have it, we'll multiply all
            // of the values in that column by xi.
            double xi = y_[diag_index];
            for (unsigned int col = diag_index+1; col < N_; ++col) {
                // here is where we would have to multiply by xi
                // if we hadn't done so earlier
                xi -= A_[col][diag_index];
            }
            xi /= A_[diag_index][diag_index];
            x_[diag_index] = xi;
            for (unsigned int row = 0; row < diag_index; ++row) {
                A_[diag_index][row] *= xi;
            }
        }
    }
}

double Solver::computeResidual() {
    double rc = 0;
    for (unsigned int row = 0; row < N_; ++row) {
        double s = 0;
        for (unsigned int col = 0; col < N_; ++col) {
            double Av = A_[col][row];
            double xv = x_[col];
            s += (Av * xv);
        }
        double bv = b_[row];
        rc += fabs(s - bv);
        //if (debug_ || fabs(s - bv) > 0.000000001) {
        //    printf("s[%d] = %e b[%d] = %e\n", row, s, row, bv);
        //    if (fabs(s - bv) > 0.000000001) rc = false;
        //}
    }
    return rc;
}
