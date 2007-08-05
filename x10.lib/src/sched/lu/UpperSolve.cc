/** 
 * A C++ cws version of the Cilk LU code. cws stuff for upper_solve routine.
 * @author Sriram Krishnamoorthy
 */

#include "base.h"
#include "UpperSolve.h"
#include "AuxUpperSolve.h"

/*----------UpperSolveFrame definitions-----------------------*/

UpperSolveFrame::UpperSolveFrame(matrix_t _M, matrix_t _U, int _nb) 
  : M(_M), U(_U), nb(_nb) {}

/*virtual*/ Closure *
UpperSolveFrame::makeClosure() { return new UpperSolveC(this); }

/*virtual*/ UpperSolveFrame *
UpperSolveFrame::copy() { return new UpperSolveFrame(*this); }


UpperSolveFrame::UpperSolveFrame(const UpperSolveFrame &f)
  : M(f.M), U(f.U), nb(f.nb) {}


/*--------------------UpperSolveC definitions---------------------*/

/*static*/ void 
UpperSolveC::seq_upper_solve(matrix_t M, matrix_t U, int nb) {
  matrix_t M00, M01, M10, M11;
  int hnb;

  /* Check base case. */
  if (nb == 1) {
    block_upper_solve(M, U);
    return;
  }
  /* Break matrices into 4 pieces. */
  hnb = nb / 2;
  M00 = MATRIX(M, 0, 0);
  M01 = MATRIX(M, 0, hnb);
  M10 = MATRIX(M, hnb, 0);
  M11 = MATRIX(M, hnb, hnb);

  /* Solve with recursive calls. */
  AuxUpperSolveC::seq_aux_upper_solve(M00, M01, U, hnb);
  AuxUpperSolveC::seq_aux_upper_solve(M10, M11, U, hnb);
}

//fast mode
/*static*/ void 
UpperSolveC::upper_solve(Worker *w, matrix_t M, matrix_t U, int nb) {
  matrix_t M00, M01, M10, M11;
  int hnb;

  /* Break matrices into 4 pieces. */
  hnb = nb / 2;
  M00 = MATRIX(M, 0, 0);
  M01 = MATRIX(M, 0, hnb);
  M10 = MATRIX(M, hnb, 0);
  M11 = MATRIX(M, hnb, hnb);

  /* Check base case. */
  if (nb == 1) {
    block_upper_solve(M, U);
    return;
  }

  UpperSolveFrame *frame = new UpperSolveFrame(M, U, nb);
  assert(frame != NULL);
  w->pushFrame(frame);

  frame->PC = LABEL_1;
  AuxUpperSolveC::aux_upper_solve(w, M00, M01, U, hnb);
  if(w->abortOnSteal()) { return; }

  frame->PC = LABEL_2;
  AuxUpperSolveC::aux_upper_solve(w, M10, M11, U, hnb);
  if(w->abortOnSteal()) { return; }

  w->popFrame();
  if(!w->cache->interrupted()) {
    delete frame;
  }
}

//slow mode 
/*virtual*/ void 
UpperSolveC::compute(Worker *w, Frame *frame) {
  UpperSolveFrame *f = dynamic_cast<UpperSolveFrame *> (frame);
  matrix_t M, U;
  int nb;

  M = f->M;
  U = f->U;
  nb = f->nb;

  matrix_t M00, M01, M10, M11;
  int hnb;  

  /* Break matrices into 4 pieces. */
  hnb = nb / 2;
  M00 = MATRIX(M, 0, 0);
  M01 = MATRIX(M, 0, hnb);
  M10 = MATRIX(M, hnb, 0);
  M11 = MATRIX(M, hnb, hnb);

  switch(f->PC) {
  case ENTRY:
    if (nb == 1) {
      block_upper_solve(M, U);
      setupReturn(w);
      return;
    }
    f->PC = LABEL_1;
    AuxUpperSolveC::aux_upper_solve(w, M00, M01, U, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_1:
    f->PC = LABEL_2;
    AuxUpperSolveC::aux_upper_solve(w, M10, M11, U, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_2:
    f->PC = LABEL_3;
    if(sync(w)) { return; /*suspended*/ }

  case LABEL_3:
    setupReturn(w);
    break;
  default:
    assert(0);
  }
}


