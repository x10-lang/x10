//  USE -qnomaf -qnomaf -qnomaf
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
  bool LUFactorize();                           // the whole matrix
  bool LUFactorize(unsigned int diag_index);    // just this one column
  void LyeqPb();
  void Uxeqy();
  bool check();
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
   unsigned int N = 3;
   bool debug = false;
   for (unsigned int i = 1; i < argc; ++i) {
      if (argv[i][0] == '-') {
         if (strcmp(argv[i],"-debug") == 0) debug = true;
      } else {
         sscanf(argv[i],"%d", &N);
      }
   }
   Solver *solver = new Solver(N, debug);
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

Solver::Solver(unsigned int N, bool debug) : N_(N), debug_(debug), nonSingular_(true) {
   A_ = new double *[N_];
   for (unsigned int i = 0; i < N_; ++i) {
      // This is where we distribute A over multiple LAPI places
      // each column on a separate place
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
         A_[col][row] = value + row - col;  // LAPI ?
         bv += value;
      }
      b_[col] = bv;
      if (clearX) x_[col] = 0;
      y_[col] = 0;
   }
}

void Solver::print() {
   for (unsigned int row = 0; row < N_; ++row) {
      for (unsigned int col = 0; col < N_; ++col) {
         double v = A_[col][row];       // LAPI ?
         printf("%.10f ", v);
      }
      printf(": %.10f: %.10f: %.10f\n", b_[row], y_[row], x_[row]);
   }
   printf("-----------------------------------\n");
}

bool Solver::LUFactorize() {
   for (unsigned int i = 0; i < N_; ++i) {
      y_[i] = b_[i];
   }
   for (unsigned int diag_index = 0; diag_index < N_; ++diag_index) {
     // LAPI?  This loop needs to happen on the node handling column "diag_index"
     if (!LUFactorize(diag_index)) {
       nonSingular_ = false;
       return false;
     }
   }
   return true;
}

bool Solver::LUFactorize(unsigned int diag_index) {
  double largest_val = A_[diag_index][diag_index];
  double swp = largest_val;
  unsigned int largest_row = diag_index;
  for (unsigned int row = diag_index; row < N_; ++row) {
    if (fabs(A_[diag_index][row]) > fabs(largest_val)) {
      largest_val = A_[diag_index][row];
      largest_row = row;
    }
  }
  // we now need to swap rows "diag_index" and "largest_row"
  // in the entire matrix A and also in the vector b
  // we'll do our own column first
  if (largest_row != diag_index) {
    A_[diag_index][largest_row] = swp;
    A_[diag_index][diag_index] = largest_val;
    // This part should be done at other places.... LAPI?
    for (unsigned int col = 0; col < diag_index; ++col) {
      // and also we'll swap the row in all the columns
      // to the left
      double swpb = A_[col][diag_index];
      A_[col][diag_index] = A_[col][largest_row];
      A_[col][largest_row] = swpb;
    }
  }
  
  //  if (fabs(A_[diag_index][diag_index]) < 1e-12) {
  if (fabs(A_[diag_index][diag_index]) == 0) {
    // Singular!
    return false;
  } else {
    // now for each row, compute the multipliers
    double multipliers[N_ - diag_index - 1];
    for (unsigned int row = diag_index+1; row < N_; ++row) {
      A_[diag_index][row] /= A_[diag_index][diag_index];
      multipliers[row - diag_index - 1] = A_[diag_index][row];
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
      for (unsigned int row = diag_index+1; row < N_; ++row) {
        double m = multipliers[row - diag_index - 1];
        y_[row] = y_[row] - m * y_[diag_index];
      }
    }
    // This part should be done at other "places" ... LAPI?
    for (unsigned int col = diag_index+1; col < N_; ++col) {
      if (largest_row != diag_index) {
        // first swap the rows in this column
        double swpb = A_[col][diag_index];
        A_[col][diag_index] = A_[col][largest_row];
        A_[col][largest_row] = swpb;
      }
      for (int row = diag_index+1; row < N_; ++row) {
        double m = multipliers[row - diag_index - 1];
        A_[col][row] = A_[col][row] - m * A_[col][diag_index];
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
    for (unsigned int diag_index = 0; diag_index < N_; ++diag_index) {
      // LAPI??
      // We're going to compute yi at the place holding
      // column i.  Once we have it, we'll multiply all
      // of the multipliers (below the main diagonal) by
      // yi.
      double sm = b_[diag_index];
      for(unsigned int col = 0; col < diag_index; ++col) {
        // here is where we would have to multiply by yi
        // if we hadn't done so earlier
        sm -= A_[col][diag_index];
      }
      double yi = sm;
      y_[diag_index] = yi;      // LAPI?
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
      double sm = y_[diag_index]; // don't forget to send this value to node!
      for (unsigned int col = diag_index+1; col < N_; ++col) {
        // here is where we would have to multiply by xi
        // if we hadn't done so earlier
        sm -= A_[col][diag_index];
      }
      double xi = sm/A_[diag_index][diag_index];
      x_[diag_index] = xi;        // don't forget to send this value back
      for (unsigned int row = 0; row < diag_index; ++row) {
        A_[diag_index][row] *= xi;
      }
    }
  }
}

bool Solver::check() {
  bool rc = true;
  double total_err = 0;
  for (unsigned int row = 0; row < N_; ++row) {
    double s = 0;
    for (unsigned int col = 0; col < N_; ++col) {
      double Av = A_[col][row];  // LAPI?
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
