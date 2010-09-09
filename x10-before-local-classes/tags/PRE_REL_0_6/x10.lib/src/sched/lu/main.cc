/** 
 * A C++ cws version of the Cilk LU code. main routine.
 * @author Sriram Krishnamoorthy
 */

#include "base.h"
#include "Lu.h"
#include <stdio.h>
#include <string.h>
#include <cstdlib>

// #define TEST

const char *usage = "%s <nprocs> <matrix-size> <block-size>\n";

class anon_Job1 : public Job {
private:
  int result;
  matrix_t M;
  const int nb;
public:
  anon_Job1(Pool *g, matrix_t _M, int _nb) : Job(g), M(_M), nb(_nb) {}
  virtual int spawnTask(Worker *ws) {  
    //fprintf(stderr, "invoked spawn task M=%p\n", M);
    LuC::lu(ws, M, nb); 
    //fprintf(stderr, "done with lu call\n");
    return 0; 
  }

  virtual void setResultInt(int x) { /*no-op*/ }
  virtual int resultInt() { return 0; /*dummy*/ }
  virtual ~anon_Job1() {}
protected:
  
};


int main(int argc, char *argv[]) {
  matrix_t M, Mone, Mtwo;
  int nb, nprocs;
  
  if(argc != 4) {
    printf(usage, argv[0]);
    exit(0);
    return 0;
  }

  nprocs = atoi(argv[1]);
  gMatrixSize = atoi(argv[2]);
  gBlockSize = atoi(argv[3]);
  gBlockStride = gBlockSize * gBlockSize;

//   printf("nprocs=%d M=%d B=%d\n", nprocs, gMatrixSize, gBlockSize);  

  assert((gMatrixSize&(gMatrixSize-1)) == 0); /*check it is a power-of-2*/
  assert((gBlockSize&(gBlockSize-1)) == 0); /*check it is a power-of-2*/  

  nb = gMatrixSize/gBlockSize;

  M = (double *)malloc(gMatrixSize * gMatrixSize * sizeof(double));
  assert(M != NULL);
 
  init_matrix(M, nb);

#ifdef TEST
  Mone = new double[gMatrixSize * gMatrixSize];
  assert(Mone != NULL);
  memcpy(Mone, M, gMatrixSize * gMatrixSize * sizeof(double));

  Mtwo = new double[gMatrixSize * gMatrixSize];
  assert(Mtwo != NULL);
  memcpy(Mtwo, M, gMatrixSize * gMatrixSize * sizeof(double));
#endif

  //printf("M=%p\n", M);
  //print_matrix(M, nb);

  Pool *g = new Pool(nprocs);
  assert(g != NULL);

  int sc = g->getStealCount();
  int sa = g->getStealAttempts();
  long long s=nanoTime();
  {
    anon_Job1 job(g, M, nb);
    g->submit(&job);
    while(!job.isJobDone()) {}
  }
  long long t = nanoTime();

  cout<<"nprocs="<<nprocs
      <<" N="<<gMatrixSize
      <<" B="<<gBlockSize
      <<" time="<<(t-s)/1000000<<"ms"
      <<" steals="<< (g->getStealCount()-sc)
      <<" stealAttempts=" << (g->getStealAttempts()-sa)
      << endl;

  //print_matrix(M, nb);
  //fflush(stdin);
  //LuC::seq_lu(Mone, nb);
  //print_matrix(Mone, nb);

#ifdef TEST
  if(!test_result(M, Mtwo, nb)) {
    printf("Test failed\n");
  }
#endif

  g->shutdown();
  delete g;
}

