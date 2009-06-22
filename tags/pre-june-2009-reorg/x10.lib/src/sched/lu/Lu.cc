/** 
 * A C++ cws version of the Cilk LU code. cws stuff for lu routine
 * @author Sriram Krishnamoorthy
 */

#include "Lu.h"
#include "Schur.h"
#include "LowerSolve.h"
#include "UpperSolve.h"

/*------------LuFrame definitions------------------------*/

LuFrame::LuFrame(matrix_t _M, int _nb) 
  : M(_M), nb(_nb) { }

/*virtual*/ Closure*
LuFrame::makeClosure() {
  return new LuC(this);
}

/*virtual*/ LuFrame*
LuFrame::copy() { 
  return new LuFrame(*this);
}

LuFrame::LuFrame(const LuFrame &f) 
  : M(f.M), nb(f.nb) {}


/*----------------LuC definitions------------------------*/

/*static*/ void
LuC::seq_lu(matrix_t M, int nb) {
  matrix_t M00, M01, M10, M11;
  int hnb;

  fprintf(stderr, "entered seq_lu\n");

  /* Check base case. */
  if (nb == 1) {
    block_lu(M);
    return;
  }
  /* Break matrix into 4 pieces. */
  hnb = nb / 2;
  M00 = MATRIX(M, 0, 0);
  M01 = MATRIX(M, 0, hnb);
  M10 = MATRIX(M, hnb, 0);
  M11 = MATRIX(M, hnb, hnb);

  /* Decompose upper left. */
  seq_lu(M00, hnb);

  /* Solve for upper right and lower left. */
  LowerSolveC::seq_lower_solve(M01, M00, hnb);
  UpperSolveC::seq_upper_solve(M10, M00, hnb);

  /* Compute Schur complement of lower right. */
  SchurC::seq_schur(M11, M10, M01, hnb);

  /* Decompose lower right. */
  seq_lu(M11, hnb);
}

//fast mode
/*static*/ void
LuC::lu(Worker *w, matrix_t M, int nb) {
  matrix_t M00, M01, M10, M11;
  int hnb;
  
  //fprintf(stderr, "Entered LuC::lu\n");

  /* Check base case. */
  if (nb == 1) {
    block_lu(M);
    //fprintf(stderr, "returning from LuC::lu\n");
    return;
  }
  /* Break matrix into 4 pieces. */
  hnb = nb / 2;
  M00 = MATRIX(M, 0, 0);
  M01 = MATRIX(M, 0, hnb);
  M10 = MATRIX(M, hnb, 0);
  M11 = MATRIX(M, hnb, hnb);

  LuFrame *f = new LuFrame(M, nb);
  assert(f != NULL);
  w->pushFrame(f);

  f->PC = LABEL_1;
  /* Decompose upper left. */
  lu(w, M00, hnb);
  if(w->abortOnSteal()) { return; }

  f->PC = LABEL_3;
  /* Solve for upper right and lower left. */
  LowerSolveC::lower_solve(w, M01, M00, hnb);
  if(w->abortOnSteal()) { return; }

  f->PC = LABEL_4;
  UpperSolveC::upper_solve(w, M10, M00, hnb);
  if(w->abortOnSteal()) { return; }

  f->PC = LABEL_6;
  /* Compute Schur complement of lower right. */
  SchurC::schur(w, M11, M10, M01, hnb);
  if(w->abortOnSteal()) { return; }

  f->PC = LABEL_8;
  /* Decompose lower right. */
  lu(w, M11, hnb);
  if(w->abortOnSteal()) { return; }

  w->popFrame();

  if(!w->cache->interrupted()) { delete f; }
}

//slow mode
/*virtual*/ void
LuC::compute(Worker *w, Frame *frame) {
  LuFrame *f = dynamic_cast<LuFrame *>(frame);
  assert(f != NULL);
  matrix_t M;
  int nb;

  M = f->M;
  nb = f->nb;

  matrix_t M00, M01, M10, M11;
  int hnb;

  /* Break matrix into 4 pieces. */
  hnb = nb / 2;
  M00 = MATRIX(M, 0, 0);
  M01 = MATRIX(M, 0, hnb);
  M10 = MATRIX(M, hnb, 0);
  M11 = MATRIX(M, hnb, hnb);

  switch(f->PC) {
  case ENTRY:
    /* Check base case. */
    if (nb == 1) {
      block_lu(M);
      setupReturn(w);
      return;
    }
    f->PC = LABEL_1;
    /* Decompose upper left. */
    lu(w, M00, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_1:
    f->PC = LABEL_2;
    if(sync(w)) { return; /*suspended*/ }

  case LABEL_2:
    f->PC = LABEL_3;
    /* Solve for upper right and lower left. */
    LowerSolveC::lower_solve(w, M01, M00, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_3:
    f->PC = LABEL_4;
    UpperSolveC::upper_solve(w, M10, M00, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_4:
    f->PC = LABEL_5;
    if(sync(w)) { return; /*suspended*/ }

  case LABEL_5:
    f->PC = LABEL_6;
    /* Compute Schur complement of lower right. */
    SchurC::schur(w, M11, M10, M01, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_6:
    f->PC = LABEL_7;
    if(sync(w)) { return; /*suspended*/ }

  case LABEL_7:
    f->PC = LABEL_8;
    /* Decompose lower right. */
    lu(w, M11, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_8:
    f->PC = LABEL_9;
    if(sync(w)) { return; /*suspended*/ }

  case LABEL_9:
    setupReturn(w);
    break;
  default:
    assert(0);
  }
}


