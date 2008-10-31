/** 
 * A C++ cws version of the Cilk LU code. cws stuff for lower_solve routine
 * @author Sriram Krishnamoorthy
 */

#include "base.h"
#include "LowerSolve.h"
#include "AuxLowerSolve.h"

/*----------LowerSolveFrame definitions-----------------------*/

LowerSolveFrame::LowerSolveFrame(matrix_t _M, matrix_t _L, int _nb) 
  : M(_M), L(_L), nb(_nb) {}

/*virtual*/ Closure *
LowerSolveFrame::makeClosure() { return new LowerSolveC(this); }

/*virtual*/ LowerSolveFrame *
LowerSolveFrame::copy() { return new LowerSolveFrame(*this); }


LowerSolveFrame::LowerSolveFrame(const LowerSolveFrame &f)
  : M(f.M), L(f.L), nb(f.nb) {}


/*--------------------LowerSolveC definitions---------------------*/

/*static*/ void 
LowerSolveC::seq_lower_solve(matrix_t M, matrix_t L, int nb) {
  matrix_t M00, M01, M10, M11;
  int hnb;
  
  /* Check base case. */
  if (nb == 1) {
    block_lower_solve(M, L);
    return;
  }
  /* Break matrices into 4 pieces. */
  hnb = nb / 2;
  M00 = MATRIX(M, 0, 0);
  M01 = MATRIX(M, 0, hnb);
  M10 = MATRIX(M, hnb, 0);
  M11 = MATRIX(M, hnb, hnb);
  
  /* Solve with recursive calls. */
  AuxLowerSolveC::seq_aux_lower_solve(M00, M10, L, hnb);
  AuxLowerSolveC::seq_aux_lower_solve(M01, M11, L, hnb);
}

//fast mode
/*static*/ void 
LowerSolveC::lower_solve(Worker *w, matrix_t M, matrix_t L, int nb) {
  matrix_t M00, M01, M10, M11;
  int hnb;
  
  /* Check base case. */
  if (nb == 1) {
    block_lower_solve(M, L);
    return;
  }
  /* Break matrices into 4 pieces. */
  hnb = nb / 2;
  M00 = MATRIX(M, 0, 0);
  M01 = MATRIX(M, 0, hnb);
  M10 = MATRIX(M, hnb, 0);
  M11 = MATRIX(M, hnb, hnb);

  LowerSolveFrame *frame = new LowerSolveFrame(M, L, nb);
  assert(frame != NULL);
  w->pushFrame(frame);
  
  frame->PC = LABEL_1;
  AuxLowerSolveC::aux_lower_solve(w, M00, M10, L, hnb);
  if(w->abortOnSteal()) { return; }

  frame->PC = LABEL_2;
  AuxLowerSolveC::aux_lower_solve(w, M01, M11, L, hnb);
  if(w->abortOnSteal()) { return; }

  w->popFrame();
  if(!w->cache->interrupted()) { delete frame; }
}

//slow mode 
/*virtual*/ void 
LowerSolveC::compute(Worker *w, Frame *frame) {
  LowerSolveFrame *f = dynamic_cast<LowerSolveFrame *>(frame);
  assert(f != NULL);

  matrix_t M, L;
  int nb;

  M = f->M;
  L = f->L;
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
  ENTRY:
    if (nb == 1) {
      block_lower_solve(M, L);
      setupReturn(w);
      return;
    }
    f->PC = LABEL_1;
    AuxLowerSolveC::aux_lower_solve(w, M00, M10, L, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_1:
    f->PC = LABEL_2;
    AuxLowerSolveC::aux_lower_solve(w, M01, M11, L, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_2:
    f->PC = LABEL_3;
    if(sync(w)) { return; /*suspended*/ }

  case LABEL_3:
    setupReturn(w);
    break;
  default:
    fprintf(stderr, "Lowersolve::compute(). PC=%d\n", f->PC);
    assert(0);
  }
}


