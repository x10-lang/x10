/*
 * LU priority with partial pivoting.
 * 
 * @author Sriram Krishnamoorthy
 */
 
#include <iostream>
#include <cstdlib>
#include <strings.h>
#include "Sys.h"
#include "Lock.h"

#include <stdlib.h>
#include <assert.h>
#include <pthread.h>

using namespace std;
//using x10lib_cws::atomic_add;
using x10lib_cws::PosixLock;

/*---------------Timing routines---------------------*/

typedef long long nano_time_t;

nano_time_t nanoTime() {
  struct timespec ts;
  // clock_gettime is POSIX!
  ::clock_gettime(CLOCK_REALTIME, &ts);
  return (nano_time_t)(ts.tv_sec * 1000000000LL + ts.tv_nsec);
}


/*----------------Profiling variables-----------------*/

class Profiler {
protected:
  PosixLock plock;

  void lock() { plock.lock_wait_posix(); }
  void unlock() { plock.lock_signal_posix(); }
public:
  volatile nano_time_t runT; /*Time spent in Worker::run()*/
  volatile nano_time_t dgemmT; /*Time spent in DGEMM*/
  volatile nano_time_t luT; /*Time spent in Block::LU()*/
  volatile nano_time_t bsT; /*Time in Block::backSolve()*/
  volatile nano_time_t lwrT; /*Time in Block::lower()*/
  volatile nano_time_t getT; /*Time in get() or getLocal() */
  volatile nano_time_t permuteT; /*Time in permute()*/
  volatile int nStep; /*#calls to Block::step()*/
  volatile int nStepLU; /*#calls to stepLU*/
  
  Profiler() {
    runT=dgemmT=luT=bsT=lwrT=permuteT=0;
    nStep = nStepLU = 0;
    //MEM_BARRIER();
  }
  inline void log_run(nano_time_t t) { runT += t; }
  inline void log_dgemm(nano_time_t t) { dgemmT += t; }
  inline void log_lu(nano_time_t t) { luT += t; }
  inline void log_bsolve(nano_time_t t) { bsT += t; }
  inline void log_lower(nano_time_t t) { lwrT += t; }
  inline void log_permute(nano_time_t t) { permuteT += t; }
  inline void log_step() { nStep += 1; }
  inline void log_stepLU() { nStepLU += 1; }

  void report(Profiler &pf) {
    pf.lock();
    pf.runT += runT;
    pf.dgemmT += dgemmT;
    pf.luT += luT;
    pf.bsT += bsT;
    pf.lwrT += lwrT;
    pf.permuteT += permuteT;
    pf.nStep += nStep;
    pf.nStepLU += nStepLU;
    pf.unlock();
  }

  void display() {
    cout<<"Worker::run() = "<<runT/1000000.0<<" ms"
	<<"\t dgemm="<<dgemmT/1000000.0<<" ms"
	<<"\t LU="<<luT/1000000.0<<" ms"
	<<"\t bsolve="<<bsT/1000000.0<<" ms"
	<<"\t lower="<<lwrT/1000000.0<<" ms"
	<<"\t permute="<<permuteT/1000000.0<<" ms"
	<<"\t nStep="<<nStep
	<<"\t nStepLU="<<nStepLU
	<<endl;
  }
};


/*--------------numerics declarations---------------*/

/*Need to actually match the fortran integer. Assume int for now and
  pray. Google blas dgemm to understand the arguments.  */ 
// typedef long Integer;
typedef int Integer;

#if 0
#define DGEMM dgemm_
#define DTRSM dtrsm_
#define DGEMV dgemv_
#define DSCAL dscal_

#else
#define DGEMM dgemm
#define DTRSM dtrsm
#define DGEMV dgemv
#define DSCAL dscal
#endif

extern "C" {
  void DGEMM(char *TRANSA, char *TRANSB, 
	     Integer *M, Integer *N, Integer *K, 
	     double *ALPHA, double* A, Integer *LDA,
	     double *B, Integer *LDB, 
	     double *BETA, double *C, Integer *LDC);

  void DTRSM(char *SIDE, char *UPLO, char *TRANSA,
	     char *DIAG, Integer *M, Integer *N, double *ALPHA,
	     double *A, Integer *LDA, double *B, Integer *LDB);

  void DGEMV(char *TRANS, Integer *M, Integer *N,
	     double *ALPHA, 
	     double *A, Integer *LDA,
	     double *X, Integer *INCX, 
	     double *BETA, double *Y, Integer *INCY);

  void DSCAL(Integer *N, double *DA, double *DX, Integer *INCX);
}



/*---------------support routines--------------------*/

static double format(double v, int precision){
  int scale=1;
  for(int i=0; i<precision; i++)
    scale *= 10;
  return ((int)(v*scale))*1.0/scale;
}
static  int max(int a, int b) {
  return a > b ? a : b;
}
static  double max(double a, double b) {
  return a > b ? a : b;
}
static double fabs(double v){
  return  v > 0 ? v : -v;
  }
static  int min(int a, int b) {
  return a > b ? b : a;
}
static double flops(int n) {
  return ((4.0 *  n - 3.0) *  n - 1.0) * n / 6.0;
}

/*---------2-D block-cyclic array of blocks-------------*/

class Block;

class TwoDBlockCyclicArray {
public:
  Block **A; /*array of block pointers. Note that we are using a 1-d
	       array of all the blocks. The processor dimension is
	       merged with the per-process data*/ 
  const int px,py, nx,ny,B;
  const int N;

public:
  TwoDBlockCyclicArray(int spx, int spy, int snx, int sny,int sB); 
  TwoDBlockCyclicArray(const TwoDBlockCyclicArray &arr); 

  ~TwoDBlockCyclicArray();
    
  void init() {}
  int pord(int i, int j) const {
    assert(j>=0 && j<py);
    assert(i>=0 && i<px);
    return i*py+j;
  }
  int lord(int i, int j) const {
    assert(j>=0 && j<ny);
    assert(i>=0 && i<nx);
    return i*ny+j;
  }
  Block *get(int i, int j) {
    return A[pord(i % px, j%py)*nx*ny + lord(i/px,j/py)];
  }

  Block *getLocal(int pi, int pj, int i, int j) {
    return A[pord(pi,pj)*nx*ny + lord(i,j)];
  }
  void set(int i, int j, Block *v) {
    A[pord(i % px, j%py)*nx*ny + lord(i/px,j/py)] = v;
  }

  TwoDBlockCyclicArray *copy() {
    return new TwoDBlockCyclicArray(*this);
  }

	void applyLowerPivots(volatile int *pivots);

	void applyPivots(volatile int *pivots);
	    
  void display(const char *msg);
};


/*--------Dense 2-D block and operations on it-------------*/

/**
 * A B*B array of doubles, whose top left coordinate is i,j).
 * get/set operate on the local coordinate system, i.e.
 * (i,j) is treated as (0,0).
 * @author VijaySaraswat
 *
 * The data within the blocks is stored in column-major order to
 * reduce the hassle in actually using fortran blas routine
 *
 */
class Block {
public:
  double *A;
  TwoDBlockCyclicArray * M; //Array of which this block is part
    
  volatile bool ready;
  // counts the number of phases left for this
  // block to finish its processing;
private:
  const int maxCount;
  volatile int count; //In PLU, other threads read count
  int readyBelowCount; //# blocks in this column that are "ready" for this block to be processed

  int Ip1, Ip2, I1, I2; /*mulsub contributor along i, in sync with count*/
  int Jp1, Jp2, J1, J2; /*mulsub contributor along j, in sync with count*/

  void initIBuddy() { Ip1 = I%M->px; Ip2 = 0; I1 = I/M->px; I2=0; }
  void initJBuddy() { Jp1 = 0; Jp2 = J%M->py; J1 = 0; J2=J/M->py; }

  inline void incIBuddy() { Ip2++; if(Ip2>=M->py) { Ip2=0; I2++; }  }
  inline void incJBuddy() { Jp1++; if(Jp1>=M->px) { Jp1=0; J1++; } }

	//The column with the block in which LU is being done
	//start with an invalid value
	volatile int LU_col;

	volatile double maxColV; //maximum value in Column LU_col
	volatile int maxRow; //Row with that value			

	void computeMax(int col) {
		computeMax(col, 0);
	}

	void computeMax(int col, int start_row) {
		assert(B > 0);
		assert(col>=0 && col<B);
		assert(start_row>=0 && start_row<B);
		maxColV = get(start_row,col);
		maxRow = I*B+start_row;
		for(int i=start_row; i<B; i++) {
			if(fabs(get(i,col)) > fabs(maxColV)) {
				maxColV = get(i,col);
				maxRow = I*B+i;
			}
		}				
	}
	
	int colMaxCount; //#maxes ready for this column
	//stepping through to perform panel factorization
	bool stepLU(volatile int *pivots, Profiler &prof);

  void checkReadyAbove() {
  }

public:
  const int I,J, B;

  Block(int sI, int sJ, int sB, TwoDBlockCyclicArray *const sM)
    : I(sI), J(sJ), B(sB), M(sM), maxCount(min(sI,sJ)), ready(false), count(0) {
    A = new double[B*B];
    assert( A != NULL);
    readyBelowCount = I;
    LU_col = -1;
    colMaxCount = 0;
    
    initIBuddy(); initJBuddy();
  }

  Block(const Block &b) 
    : I(b.I), J(b.J), B(b.B), M(b.M), maxCount(b.maxCount), ready(b.ready), count(0)  {
    assert(maxCount == min(I,J));
    A = new double[B*B];
    assert(A != NULL);
    //also copy the data
    for(int i=0; i<B*B; i++)
      A[i] = b.A[i];
    readyBelowCount = I;
    LU_col = b.LU_col;
    colMaxCount = b.colMaxCount;
  }
  ~Block() { delete [] A; }

  Block *copy() {
    return new Block(*this);
  }
  /*printing in row-major order for laymen like me*/
  void display() {
    //cout<<"I="<<I<<" J="<<J<<endl;
    for(int i=0; i<B; i++) {
      for(int j=0; j<B; j++) {
	cout<<format(A[i+j*B],6) << " ";
      }
      //cout<<endl;;
    }
  }
  void init() {
    for (int i=0; i < B*B; i++)
      A[i] = format(rand()*10.0/RAND_MAX, 4);
    if (I==J) {
      for (int i=0; i < B; i++) 
	A[i+i*B] = format(rand()*20.0/RAND_MAX + 10.0, 4);
    }
  }
  bool step(volatile int *pivots, Profiler &prof);

  int  ord(int i, int j) {
    return i+j*B;
  }
  double  get(int i, int j) {
    return A[ord(i,j)];
  }
  void  set(int i, int j, double v) {
    A[ord(i,j)] = v;
  }
  void  negAdd(int i, int j, double v) {
    A[ord(i,j)] -= v;
  }
  void  posAdd(int i, int j, double v) {
    A[ord(i,j)] += v;
  }


  //permute, for the columns in this block, 
  //row1 in this block with row2 (in potentially some other block)*/
  //not timed: for use outside profiler
  void permute(int row1, int row2) {
    assert (row1 != row2); //why was this called then?
    assert (row1>=I*B && row1<(I+1)*B); //should be a row in this block
    Block *b = M->get(row2/B, J); //the other block

#if 0    
    for(int j=0; j<B; j++){
      double v1 = get(row1%B, j);
      double v2 = b->get(row2%B, j);
      set(row1%B, j, v2);
      b->set(row1%B, j, v1);
    }
#else
    int base1 = row1%B, base2 = row2%B;
    for(int j=0; j<B; j++) {
      double v1 = this->A[j*B+base1];
      this->A[j*B+base1] = b->A[j*B+base2];
      b->A[j*B+base2] = v1;
    }
#endif
  }

  void permute(int row1, int row2, Profiler& prof) {
    nano_time_t s = nanoTime();
    permute(row1, row2);
    nano_time_t t = nanoTime();
    prof.log_permute(t-s);
  }


  void lower(Block *diag, int col, Profiler &prof) {
    nano_time_t s = nanoTime();
#if 0
    for(int i=0; i<B; i++) {
      double r = 0.0;
      for(int k=0; k<col; k++)
	r += get(i,k)*diag->get(k,col);
      negAdd(i,col,r);
      set(i,col,get(i,col)/diag->get(col,col));				
    }
#else
      /*DGEMV+DSCAL: compute 
	this(0..B-1,col) -= this(0..B-1,0..col-1)*diag(0..col-1,col)
	this(0..B-1,col) /= diag(col,col)*/

      {      
	char TRANSA = 'N';
	Integer M = B;
	Integer N = col;
	double ALPHA = -1.0;
	double *mA = this->A;
	Integer LDA = B;
	double *mX = diag->A + (B*col);
	Integer INCX = 1;
	double BETA = 1.0;
	double *mY = this->A+ (B*col);
	Integer INCY = 1;
	
	DGEMV(&TRANSA, &M, &N, &ALPHA, mA, &LDA,
	      mX, &INCX, &BETA, mY, &INCY);
      }

      {	
	Integer N = B;
	double DA = 1.0/(diag->get(col,col));
	double *DX = this->A + (B*col);
	Integer INCX = 1;
	DSCAL(&N, &DA, DX, &INCX);
      }
#endif
    nano_time_t t = nanoTime();
    prof.log_lower(t-s);
  }
    
  void lower(Block *diag, Profiler &prof) {
    nano_time_t s = nanoTime();
#if 0
    for(int i=0; i<B; i++)
      for(int j=0; j<B; j++) {
	double r = 0.0;
	for(int k=0; k<j; k++)
	  r += get(i,k)*diag->get(k,j);
	negAdd(i,j,r);
	set(i,j,get(i,j)/diag->get(j,j));
      }
#else
    /*DTRSM: solve diag*X = 1.0*this; this is overwritten with X*/
    char SIDE = 'R'; //diag is to right of X
    char UPLO = 'U'; //diag is upper-triangular
    char TRANSA = 'N'; //No transpose of diag
    char DIAG = 'N'; //Nonunit-diagonal for diag
    Integer M = B; //#rows of diag
    Integer N = B; //#cols of diag
    double ALPHA = 1.0; //scale on right-hand size
    double *mA = diag->A;
    Integer LDA = B;
    double *mB = this->A;
    Integer LDB = B;

    DTRSM(&SIDE, &UPLO, &TRANSA, &DIAG, &M, &N, &ALPHA,
	  mA, &LDA, mB, &LDB);
#endif
    nano_time_t t = nanoTime();
    prof.log_lower(t-s);
  }

  void backSolve(Block *diag, volatile int *pivots, Profiler &prof) {
    nano_time_t s = nanoTime();

	for(int i=I*B; i<(I+1)*B; i++) {
	  if(pivots[i] != i)
	    permute(i, pivots[i], prof);
	}
	
#if 0
    for (int i = 0; i < B; i++) {
      for (int j = 0; j < B; j++) {
	double r = 0.0;
	for (int k = 0; k < i; k++) {
	  r += diag->get(i, k) * get(k, j);
	}
	negAdd(i, j, r);
      }
    }
#else
    /*DTRSM: solve diag*X = 1.0*this; this is overwritten with X*/
    char SIDE = 'L'; //diag is to left of X
    char UPLO = 'L'; //diag is lower-triangular
    char TRANSA = 'N'; //No transpose of diag
    char DIAG = 'U'; //Unit-diagonal for diag
    Integer M = B; //#rows of diag
    Integer N = B; //#cols of diag
    double ALPHA = 1.0; //scale on right-hand size
    double *mA = diag->A;
    Integer LDA = B;
    double *mB = this->A;
    Integer LDB = B;

    DTRSM(&SIDE, &UPLO, &TRANSA, &DIAG, &M, &N, &ALPHA,
	  mA, &LDA, mB, &LDB);

#endif
    nano_time_t t = nanoTime();
    prof.log_bsolve(t-s);
  }

  void mulsub(Block *left, Block *upper, Profiler &prof) {
    nano_time_t s = nanoTime();

#if 0
    for(int i=0; i<B; i++)
      for(int j=0; j<B; j++) {
	double r=0;
	for(int k=0; k<B; k++)
	  r += left->get(i, k) * upper->get(k, j);
	negAdd(i,j,r);
      }
#else
    double *mA = left->A;
    double *mB = upper->A;
    double *mC = A;

    char transa = 'N', transb = 'N';
    Integer m=B, n=B, k=B;
    double alpha = -1.0;
    Integer lda = B, ldb = B, ldc = B;
    double beta = 1.0;

    DGEMM(&transa, &transb, &m, &n, &k, &alpha, mA, &lda,
	  mB, &ldb, &beta, mC, &ldc);
#endif
    nano_time_t t = nanoTime();
    prof.log_dgemm(t-s);
  }

  void LU(int col, Profiler &prof) {
    nano_time_t s = nanoTime();
    for (int i = 0; i < B; i++) {
      double r = 0.0;
      for(int k=0; k<min(i,col); k++)
	r += get(i,k) * get(k,col);
      negAdd(i,col, r);
      if(i>col) set(i,col, get(i,col)/get(col,col));
    }				
    nano_time_t t = nanoTime();
    prof.log_lu(t-s);
  }


  void LU(Profiler &prof) {
    nano_time_t s = nanoTime();
    for (int k = 0; k < B; k++)
      for (int i = k + 1; i < B; i++) {
	set(i,k, get(i,k)/get(k,k));
	double a = get(i,k);
	for(int j=k+1; j<B; j++)
	  negAdd(i,j, a*get(k,j));
      }
    nano_time_t t = nanoTime();
    prof.log_lu(t-s);
  }
};

/*---------Definitions after necessary forward declarations-------------*/

TwoDBlockCyclicArray::TwoDBlockCyclicArray(int spx, int spy, int snx, int sny,int sB) 
  : px(spx), py(spy), nx(snx), ny(sny), B(sB), N(spx*snx*sB) {

  assert(px*nx==py*ny);
  A = new Block* [px*py*nx*ny];
  assert(A != NULL);

  int ctr=0;
  for(int pi=0; pi<px; pi++) {
    for(int pj=0; pj<py; pj++) {
      for(int i=0; i<nx; i++) {
	for(int j=0; j<ny; j++) {
	  A[ctr] = new Block(i*px+pi, j*py+pj, B, this);
	  assert(A[ctr] != NULL);
	  A[ctr]->init();
	  ++ctr;
	}
      }
    }
  }
}

TwoDBlockCyclicArray::TwoDBlockCyclicArray(const TwoDBlockCyclicArray &arr) 
  : px(arr.px), py(arr.py), nx(arr.nx), ny(arr.ny), B(arr.B), N(arr.N)  {

  assert(px*nx==py*ny);
  A = new Block *[px*py*nx*ny];
  assert(A != NULL);
  int ctr=0;
  for(int pi=0; pi<px; pi++) {
    for(int pj=0; pj<py; pj++) {
      for(int i=0; i<nx; i++) {
	for(int j=0; j<ny; j++) {
	  A[ctr] = arr.A[ctr]->copy();
	  assert( A[ctr] != NULL );
	  A[ctr]->M = this;
	  ++ctr;
	}
      }
    }
  }
}

TwoDBlockCyclicArray::~TwoDBlockCyclicArray() {
  for(int i=0; i<px*py*nx*ny; i++) {
    delete A[i];
  }
  delete [] A;
}

void 
TwoDBlockCyclicArray::applyLowerPivots(volatile int *pivots) {
  for(int i=0; i<px*nx; i++) {
    for(int j=0; j<i;  j++) {
      for(int r=i*B; r<(i+1)*B; r++) {
	assert(pivots[r]>=r);
	assert(pivots[r]<px*nx*B);
	if(r != pivots[r]) {
	  get(i,j)->permute(r, pivots[r]);
	}
      }
    }
  }
}

void 
TwoDBlockCyclicArray::applyPivots(volatile int *pivots) {
  for(int i=0; i<px*nx; i++) {
    for(int j=0; j<py*ny;  j++) {
      for(int r=i*B; r<(i+1)*B; r++) {
	assert(pivots[r]>=r);
	assert(pivots[r]<px*nx*B);
	if(r != pivots[r]) {
	  get(i,j)->permute(r, pivots[r]);
	}
      }
    }
  }				
}

void 
TwoDBlockCyclicArray::display(const char *msg) {
  cout<<msg<<endl;;
  cout<<"px="<<px<<" py="<<py<<" nx="<<nx<<" ny="<<ny<<" B="<<B<<endl;;
  
  for(int I=0; I<px*nx; I++) {
    for(int J=0; J<py*ny; J++) {
      get(I,J)->display();
    }
    //cout<<endl;
  }
  cout<<endl;
}


/*------------Definition after necessary forward-declarations---------------*/

/*Try to step through the next ready operation in given priority
  order. An operation is LU, backSolve, lower, or mulSub on a block. */
bool 
Block::step(volatile int *pivots, Profiler &prof) {
  prof.log_step();

  if(ready) return false;

  //cerr<<"I="<<I<<" J="<<J<<endl;
  if (count == maxCount) {
    //cerr<<"Done all mulsubs"<<endl;
    //cerr<<"Diagonal"<<endl;
    if(I<J && M->get(I,I)->ready) {
      if(readyBelowCount==0) readyBelowCount=I;
      for(;readyBelowCount<M->px*M->nx && 
	    (M->get(readyBelowCount, J)->count==I);
	  readyBelowCount++);
      if(readyBelowCount==M->px*M->nx) {
	backSolve(M->get(I,I), pivots, prof);
	checkReadyAbove();
	ready = true;
	//MEM_BARRIER();
	//cerr<<"I="<<I<<" J="<<J<<endl;
	return true;
      }
      return false;
    }
    else if (I >=J) {
      //cerr<<"Calling stepLU"<<endl;
      bool rval = stepLU(pivots, prof);
      //cerr<<"Done calling stepLU"<<endl;
      return rval;
    }
    else
      return false;
  }
  //cerr<<"Rest"<<endl;
  Block *IBuddy = M->get(I, count), *JBuddy = M->get(count,J);
  if (IBuddy->ready && JBuddy->ready) {
    mulsub(IBuddy, JBuddy, prof);
    count++;
    //cerr<<"Done mulsub"<<endl;
    return true;
  }
  return false;
}

//stepping through to perform panel factorization
bool
Block::stepLU(volatile int *pivots, Profiler &prof) {
  prof.log_stepLU();
  assert (I >= J);
  assert (count == maxCount);
  assert (ready == false);
  assert (LU_col < B);
	
  if(LU_col==-1 && (I==J)) { 
    if(readyBelowCount==0) readyBelowCount=I;
    for(;readyBelowCount<M->px*M->nx && 
	  (M->get(readyBelowCount, J)->count==J);
	readyBelowCount++);
		
    if(readyBelowCount < M->px*M->nx) {
      return false;
    }
  }

  if(I == J) {
    if(LU_col>=0) {
      if(colMaxCount==0) colMaxCount = I+1;
      for(;colMaxCount<M->px*M->nx &&
	    (M->get(colMaxCount,J)->LU_col==LU_col);
	  colMaxCount++) {
	if(fabs(M->get(colMaxCount, J)->maxColV) > fabs(maxColV)) {
	  maxColV = M->get(colMaxCount, J)->maxColV;
	  maxRow = M->get(colMaxCount, J)->maxRow;
	}
      }
      if(colMaxCount < M->px*M->nx)
	return false;
      pivots[I*B+LU_col] = maxRow;
      //MEM_BARRIER(); //maybe not
      if(I*B+LU_col != pivots[I*B+LU_col])
	permute(I*B+LU_col, pivots[I*B+LU_col], prof);
      LU(LU_col, prof);
      if(LU_col==B-1)  {
	checkReadyAbove();
	ready=true;
	//cerr<<"I="<<I<<" J="<<J<<endl;
      }
      //MEM_BARRIER();
    }
    LU_col = (LU_col==-1? 0 : LU_col+1);
    if(LU_col<=B-1)	{
      computeMax(LU_col, LU_col);
      colMaxCount=0;
    }
  }
  else {
    if(LU_col>=0) {
      Block *diag = M->get(J,J);
      if(!(diag->LU_col > LU_col) && !diag->ready) 
	return false;
      lower(diag, LU_col, prof);
      if(LU_col==B-1) {
	checkReadyAbove();
	ready = true;
	//cerr<<"I="<<I<<" J="<<J<<endl;
      } 
    }
    if(LU_col+1 <= B-1) computeMax((LU_col==-1?0:LU_col+1));
    MEM_BARRIER(); //A store barrier?
    LU_col = (LU_col==-1 ? 0 : LU_col+1);
  }

  return true;
}


/*------------Worker-----------------------*/

extern "C" {
  static void *start_thread(void *arg);
}

/*a thread base class using pthreads*/
class Thread {
protected:
  pthread_t thread_id;
  bool joined; /*multiple joins on same thread are undefined in pthreads*/
public:
  Thread() {
    joined = true; //no actual thread yet.
  }

  virtual void run()=0;

  void join() {
    assert(!joined);
    int res = pthread_join(thread_id, NULL);
    if(res != 0) {
      cerr<<"Could not join thread "<<thread_id<<endl;
    }
    joined = true;
  }
  void start() {
    pthread_attr_t attr;
    pthread_attr_init(&attr); 
    pthread_attr_setscope(&attr, PTHREAD_SCOPE_SYSTEM); 
	 
    int res = pthread_create(&thread_id, 
			     &attr,
			     start_thread,
			     (void *) this);
    if (res) {cout << "could not create thread"; abort(); }
    joined = false;
  }

  virtual ~Thread() {
    if(!joined) {
      cerr<<"Thread object deleted before being joined! Aborting"<<endl;
      abort();
    }
  }
};

extern "C" {
  static void *start_thread(void *arg) {
    Thread *th = static_cast<Thread *>(arg);
    th->run();
    return NULL;
  }
}

class Worker : public Thread {
private:
  Profiler &gProf;
public:
  const int pi, pj;
  TwoDBlockCyclicArray * const M;
  volatile int *pivots;

  Worker(int spi, int spj, TwoDBlockCyclicArray *sM, 
	 volatile int *s_pivots, Profiler &_gProf) 
    : pi(spi), pj(spj), M(sM), gProf(_gProf), pivots(s_pivots) { }

  void run() {
    const int nx = M->nx;
    const int ny = M->ny;
    Profiler prof;

    assert(nx>=1);
    assert(ny>=1);

    nano_time_t s = nanoTime();

    Block *lastBlock = M->getLocal(pi, pj, nx-1, ny-1);
    int starty = 0;
    int readyCount=0;

    while(/*!lastBlock->ready*/ starty<ny) {
      MEM_BARRIER();

//       if(M->getLocal(pi, pj, nx-1, starty)->ready) {
// 	for(int i=0; i<nx; i++) {
// 	  assert(M->getLocal(pi, pj, i, starty)->ready);
// 	}
// 	starty += 1;
//       }
      assert(ny-1>=starty);
      readyCount=0;

#if 0
      for(int j=starty; j<ny; j++) {
	bool doneForNow = false;
	for(int i=0; i<nx; i++) {
	  Block *block = M->getLocal(pi, pj, i,j);
	  //cerr<<"Calling step I="<<i<<" J="<<j<<endl;
	  bool rval = block->step(pivots, prof);
	  //cerr<<"Done calling step"<<endl;
	  //if(rval) { doneForNow=true; break; }
	  if(rval) readyCount += 1;
	}
	if(doneForNow) break;	
      }
#else
      for(int j=starty; j<min(starty+3,ny); j++) {
	bool doneForNow = false;
	for(int i=0; i<nx; i++) {
	  Block *block = M->getLocal(pi, pj, i,j);
	  bool rval = block->step(pivots, prof);
	  if(rval) { doneForNow = true; }
	  if(j==starty && block->ready) readyCount += 1;
	}
	//if(doneForNow) break;
      }
#endif
      if(readyCount == nx) starty+=1;
    }

    nano_time_t t = nanoTime();
    prof.log_run(t-s);
    prof.report(gProf);
  }
};


/*----------------Wrapper class for non-pivoting LU------------------*/

class PLU {
public: 
  TwoDBlockCyclicArray *M;

  const int nx,ny,px,py,B;
	volatile int *pivots;

public:
  /**
     Iterative version, with pivoting.
  */
  PLU(int spx, int spy, int snx, int sny, int sB) 
    :  nx(snx), ny(sny), px(spx), py(spy), B(sB) {
    M = new TwoDBlockCyclicArray(px,py,nx,ny,B);
	pivots = new volatile int [nx*px*B];
	assert(pivots != NULL);
	for(int i=0; i<nx*px*B; i++) pivots[i] = i;
    assert( M != NULL);
  }

  /*Returns time between thread creation and termination (in
    nanoseconds). This removes the thread creation overhead*/ 
  long long plu(Profiler &gProf) {
    Thread **workers = new Thread* [px*py];
    assert(workers != NULL);

    for (int i=0; i < px; i++)
      for(int j=0; j<py; j++)
	workers[i*py+j] = new Worker(i, j, M, pivots, gProf);
  
    for (int i=0; i < px*py; i++) workers[i]->start();
    long long s = nanoTime();
    // run, run, score many runs.
    for (int i=0; i < px*py; i++) {
	workers[i]->join();
	delete workers[i];
    }
    long long t = nanoTime();
    delete [] workers;
    return (t-s);
  }
  
  bool verify(TwoDBlockCyclicArray *Input) {
    int k;
    /* Initialize test. */
    double max_diff = 0.0;

    /* Find maximum difference between any element of LU and M. */
    for (int i = 0; i < nx * px * B; i++)
      for (int j = 0; j < ny * py * B; j++) {
	const int I = i / B;
	const int J = j / B;
	double v = 0.0;
	for (k = 0; k < i && k <= j; k++) {
	  const int K = k / B;
	  v += M->get(I,K)->get(i%B, k%B) * M->get(K,J)->get(k%B, j%B);
	}
	if (k == i && k <= j) {
	  const int K = k / B;
	  v += M->get(K,J)->get(k%B, j%B);
	}
	double diff = fabs(Input->get(I,J)->get(i%B, j%B) - v);
	max_diff = max(diff, max_diff);
      }

    /* Check maximum difference against threshold. */
    if (max_diff > 0.0000001)
      return false;
    else
      return true;
  }  
};



int main(int argc, char *argv[]) {
  if (argc != 5) {
    cout<<"Usage: LU N b px py"<<endl;
    return 0;
  }
  const int N = atoi(argv[1]);
  const int B = atoi(argv[2]);
  const int px= atoi(argv[3]);
  const int py= atoi(argv[4]);
  const int nx = N / (px*B), ny = N/(py*B);
  assert (N % (px*B) == 0 && N % (py*B) == 0);
  Profiler gProf;

  PLU *plu = new PLU(px,py,nx,ny,B);

//   TwoDBlockCyclicArray *A = plu->M->copy();

//   LU *seqlu = new LU(1,1,1,1,N);
//   for(int i=0; i<N; i++) {
//     for(int j=0; j<N; j++) {
//       int I = i/B, J = j/B;
//       seqlu->M->get(0,0)->set(i,j,lu->M->get(I,J)->get(i%B,j%B));
//     }
//   }

//    plu->M->display("Original array");
//   seqlu->M->display("Seq array");
  
  long long s = nanoTime();
  long long tt = plu->plu(gProf);
  long long t = nanoTime();

//   seqlu->lu();
//   lu->M->display(string("After LU"));
//   seqlu->M->display(string("Seq after LU"));
  
//   plu->M->applyLowerPivots(plu->pivots);
//   plu->M->display("After back-propogating pivots");
//   A->applyPivots(plu->pivots);
//   A->display("Original array after pivoting");
  
//   bool correct = plu->verify(A);

//   cout<<"Pivots: ";
//   for(int i=0; i<px*nx*B; i++) {
//     cout<<plu->pivots[i]<<" ";
//   }
//   cout<<endl;

  delete plu;
//   delete A;


//   cout<<"N="<<N<<" px="<<px<<" py="<<py<<" B="<<B
//     <<(correct?" ok":" fail")<<" time="<<(t-s)/1000000<<"ms"
//       <<" Rate="<< format(flops(N)/(t-s)*1000, 3)<<"MFLOPS"
//       <<endl;
  cout<<"N="<<N<<" px="<<px<<" py="<<py<<" B="<<B
//     	<<(correct?" ok":" fail")
      <<" rt time="<<(t-s)/1000000<<"ms"
      <<" rt Rate="<< format(flops(N)/(t-s)*1000, 3)<<"MFLOPS"
      <<" comp time="<<tt/1000000<<"ms"
      <<" comp Rate="<< format(flops(N)/(tt)*1000, 3)<<"MFLOPS"
      <<endl;

  gProf.display();
}
