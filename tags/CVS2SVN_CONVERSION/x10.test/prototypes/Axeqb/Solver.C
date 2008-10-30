// LU factorization -- optimized C++ version
#include <stdio.h>
#include <string.h>
#include <time.h>
#include <math.h>
#define A_acc(row,col) A_[col][row]

class Solver {
public:
  Solver(int,bool);
  ~Solver();
  void setup(bool clearX);
  void print();
  bool LUFactorize();                           // the whole matrix
  bool LUFactorize(int diag_index);    // just this one column
  void LyeqPb();
  void Uxeqy();
  bool check();
private:
  bool debug_;
  bool nonSingular_;
  static bool computeYDirectly;
  double *b_;
  double *y_;
  double *x_;
  double **A_;
  int N_;
};

bool Solver::computeYDirectly = true;

int main(int argc, char *argv[]) {
   int N = 3;
   bool debug = false;
   int n=0;
   for (int i = 1; i < argc; ++i) {
      if (argv[i][0] == '-') {
         if (strcmp(argv[i],"-debug") == 0) debug = true;
	 else {printf("Unknown option %s\n", argv[i]); exit(1); }
      } else if (n==0) {
	 n++;
	 sscanf(argv[i],"%d", &N);
      } else {
	 printf("Extraneous argument %s\n", argv[i]);
	 exit(1);
      }
   }
   Solver *solver = new Solver(N,debug);
   solver->setup(true);
   if (debug) solver->print();
   clock_t start_time = clock();
   bool nonSingular = solver->LUFactorize();
   if (debug) solver->print();
   solver->LyeqPb();
   solver->Uxeqy();
   clock_t finish_time = clock();
   if (debug) solver->print();
   if (!nonSingular) {
      fprintf(stderr, "A is singular\n");
   }
   solver->setup(false);
   if (debug || nonSingular) {
     if (!solver->check() && nonSingular) fprintf(stderr,"FAILED");
   }
   printf("Time difference is %ld\n", finish_time-start_time);
   delete solver;
   return 0;
}

Solver::Solver(int N,bool debug) : N_(N), debug_(debug), nonSingular_(true) {
   A_ = new double* [N_];
   for(int k=0; k< N_; ++k) A_[k]= new double[N_];
   b_ = new double[N_];
   y_ = new double[N_];
   x_ = new double[N_];

}

Solver::~Solver() {
   for(int k=0; k< N_; ++k) delete A_[k];
   delete A_;
   delete b_;
   delete y_;
   delete x_;
}

void Solver::setup(bool clearX) {
   int init = 1325;
   // not parallelizable across columns, 
   // unless lookahead seeds are created for each column
   // not parallelizable within a column
   for (int col = 0; col < N_; ++col) {
      double bv = 0;
      for (int row = 0; row < N_; ++row) {
         init = (3125 * init) & 0xffff;
         double value = (double)(init - 32768)/16384.0;
         A_acc(row,col) = value + row - col;  // LAPI ?
         bv += value;
      }
      b_[col] = bv;
      if (clearX) x_[col] = 0;
      y_[col] = 0;
   }
}

void Solver::print() {
   // not parallelizable
   for (int row = 0; row < N_; ++row) {
      for (int col = 0; col < N_; ++col) {
         double v = A_acc(row,col);       // LAPI ?
         printf("%.10f ", v);
      }
      printf(": %.10f: %.10f: %.10f\n", b_[row], y_[row], x_[row]);
   }
   printf("-----------------------------------\n");
}

bool Solver::LUFactorize() {
   for (int i = 0; i < N_; ++i) {
      y_[i] = b_[i];
   }
   for (int diag_index = 0; diag_index < N_; ++diag_index) {
     // LAPI?  This loop needs to happen on the node handling column "diag_index"
     if (!LUFactorize(diag_index)) {
       nonSingular_ = false;
       return false;
     }
   }
   return true;
}

bool Solver::LUFactorize(int diag_index) {
  double largest_val = A_acc(diag_index,diag_index);
  double swp = largest_val;
  int largest_row = diag_index;
  for (int row = diag_index; row < N_; ++row) {
    if (fabs(A_acc(row,diag_index)) > fabs(largest_val)) {
      largest_val = A_acc(row,diag_index);
      largest_row = row;
    }
  }
  // we now need to swap rows "diag_index" and "largest_row"
  // in the entire matrix A and also in the vector b
  // we'll do our own column first
  if (largest_row != diag_index) {
    A_acc(largest_row,diag_index) = swp;
    A_acc(diag_index,diag_index) = largest_val;
    // This part should be done at other places.... LAPI?
    for (int col = 0; col < diag_index; ++col) {
      // and also we'll swap the row in all the columns
      // to the left
      double swpb = A_acc(diag_index,col);
      A_acc(diag_index,col) = A_acc(largest_row,col);
      A_acc(largest_row,col) = swpb;
    }
  }
  
  //  if (fabs(A_acc(diag_index,diag_index)) < 1e-12) {
  if (fabs(A_acc(diag_index,diag_index)) == 0) {
    // Singular!
    return false;
  } else {
    // now for each row, compute the multipliers
    double *multipliers=(double *)alloca((N_ - diag_index - 1)*sizeof(double));
    double t=A_acc(diag_index,diag_index);
    for (int row = diag_index+1; row < N_; ++row) {
      A_acc(row,diag_index) /= t;
      multipliers[row - diag_index - 1] = A_acc(row,diag_index);
    }
    // finally, all the columns to the right have
    // to do row swapping and subtract the scaled
    // row.
    // If we want to compute y as part of this major
    // loop, we do the same thing as we do for all
    // other columns and we do it first
    if (Solver::computeYDirectly) {
      // This part should be run at "place 0" since that's where y_ is
      if (largest_row != diag_index) {
        double swpb = y_[diag_index];
        y_[diag_index] = y_[largest_row];
        y_[largest_row] = swpb;
      }
      double t=y_[diag_index];
      for (int row = diag_index+1; row < N_; ++row) {
        double m = multipliers[row - diag_index - 1];
        y_[row] -= m * t;
      }
    }
    // This part should be done at other "places" ... LAPI?
    for (int col = diag_index+1; col < N_; ++col) {
      if (largest_row != diag_index) {
        // first swap the rows in this column
        double swpb = A_acc(diag_index,col);
        A_acc(diag_index,col) = A_acc(largest_row,col);
        A_acc(largest_row,col) = swpb;
      }
      double t=A_acc(diag_index,col);
      for (int row = diag_index+1; row < N_; ++row) {
        double m = multipliers[row - diag_index - 1];
        A_acc(row,col) -= m * t;
      }
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
    for (int diag_index = 0; diag_index < N_; ++diag_index) {
      // LAPI??
      // We're going to compute yi at the place holding
      // column i.  Once we have it, we'll multiply all
      // of the multipliers (below the main diagonal) by
      // yi.
      double sm = b_[diag_index];
      for(int col = 0; col < diag_index; ++col) {
        // here is where we would have to multiply by yi
        // if we hadn't done so earlier
        sm -= A_acc(diag_index,col);
      }
      double yi = sm;
      y_[diag_index] = yi;      // LAPI?
      for (int row = diag_index+1; row < N_; ++row) {
        A_acc(row,diag_index) *= yi;
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
      double sm = y_[diag_index]; // don't forget to send this value to node!
      for (int col = diag_index+1; col < N_; ++col) {
        // here is where we would have to multiply by xi
        // if we hadn't done so earlier
        sm -= A_acc(diag_index,col);
      }
      double xi = sm/A_acc(diag_index,diag_index);
      x_[diag_index] = xi;        // don't forget to send this value back
      for (int row = 0; row < diag_index; ++row) {
        A_acc(row,diag_index) *= xi;
      }
    }
  }
}

bool Solver::check() {
  bool rc = true;
  double total_err = 0;
  for (int row = 0; row < N_; ++row) {
    double s = 0;
    for (int col = 0; col < N_; ++col) {
      double Av = A_acc(row,col);  // LAPI?
      double xv = x_[col];
      s += (Av * xv);
    }
    double bv = b_[row];
    total_err += fabs(s - bv);
    //    if (debug_ || fabs(s - bv) > 0.000000001) {
    //      printf("s[%d] = %e b[%d] = %e\n", row, s, row, bv);
    //      if (fabs(s - bv) > 0.000000001) rc = false;
    //    }
  }
  printf("total error = %e\n", total_err);
  return rc;
}
