/** 
 * A C++ cws version of the Cilk LU code. cws stuff for aux_lower_solve routine
 * @author Sriram Krishnamoorthy
 */

#include "base.h"
#include "LowerSolve.h"
#include "AuxLowerSolve.h"
#include "Schur.h"

/*----------AuxLowerSolveFrame definitions-----------------------*/

AuxLowerSolveFrame::AuxLowerSolveFrame(matrix_t _Ma, matrix_t _Mb, matrix_t _L, int _nb) 
  : Ma(_Ma), Mb(_Mb), L(_L), nb(_nb) {}

/*virtual*/ Closure *
AuxLowerSolveFrame::makeClosure() { return new AuxLowerSolveC(this); }

/*virtual*/ AuxLowerSolveFrame *
AuxLowerSolveFrame::copy() { return new AuxLowerSolveFrame(*this); }


AuxLowerSolveFrame::AuxLowerSolveFrame(const AuxLowerSolveFrame &f)
  : Ma(f.Ma), Mb(f.Mb), L(f.L), nb(f.nb) {}


/*--------------------AuxLowerSolveC definitions---------------------*/

/*static*/ void 
AuxLowerSolveC::seq_aux_lower_solve(matrix_t Ma, matrix_t Mb, matrix_t L, int nb) {
  matrix_t L00, L01, L10, L11;

  /* Break L matrix into 4 pieces. */
  L00 = MATRIX(L, 0, 0);
  L01 = MATRIX(L, 0, nb);
  L10 = MATRIX(L, nb, 0);
  L11 = MATRIX(L, nb, nb);

  /* Solve with recursive calls. */
  LowerSolveC::seq_lower_solve(Ma, L00, nb);
  SchurC::seq_schur(Mb, L10, Ma, nb);
  LowerSolveC::seq_lower_solve(Mb, L11, nb);
}

//fast mode
/*static*/ void 
AuxLowerSolveC::aux_lower_solve(Worker *w, matrix_t Ma, matrix_t Mb, matrix_t L, int nb) {
  matrix_t L00, L01, L10, L11;

  /* Break L matrix into 4 pieces. */
  L00 = MATRIX(L, 0, 0);
  L01 = MATRIX(L, 0, nb);
  L10 = MATRIX(L, nb, 0);
  L11 = MATRIX(L, nb, nb);

  AuxLowerSolveFrame *frame = new AuxLowerSolveFrame(Ma, Mb, L, nb);
  assert(frame != NULL);
  w->pushFrame(frame);
  
  frame->PC = LABEL_1;
  LowerSolveC::lower_solve(w, Ma, L00, nb);
  if(w->abortOnSteal()) { return; }

  frame->PC = LABEL_3;
  SchurC::schur(w, Mb, L10, Ma, nb);
  if(w->abortOnSteal()) { return; }

  frame->PC = LABEL_5;
  LowerSolveC::lower_solve(w, Mb, L11, nb);
  if(w->abortOnSteal()) { return; }

  w->popFrame();
  if(!w->cache->interrupted()) { delete frame; }
}

//slow mode 
/*virtual*/ void 
AuxLowerSolveC::compute(Worker *w, Frame *frame) {
  AuxLowerSolveFrame *f = dynamic_cast<AuxLowerSolveFrame *>(frame);
  assert(f != NULL);

  matrix_t Ma, Mb, L;
  int nb;

  Ma = f->Ma;
  Mb = f->Mb;
  L = f->L;
  nb = f->nb;

  matrix_t L00, L01, L10, L11;

  /* Break L matrix into 4 pieces. */
  L00 = MATRIX(L, 0, 0);
  L01 = MATRIX(L, 0, nb);
  L10 = MATRIX(L, nb, 0);
  L11 = MATRIX(L, nb, nb);

  switch(f->PC) {
  case ENTRY:
    f->PC = LABEL_1;
    LowerSolveC::lower_solve(w, Ma, L00, nb);
    if(w->abortOnSteal()) { return; }

  case LABEL_1:
    f->PC = LABEL_2;
    if(sync(w)) { return; /*suspended*/ }

  case LABEL_2:
    f->PC = LABEL_3;
    SchurC::schur(w, Mb, L10, Ma, nb);
    if(w->abortOnSteal()) { return; }

  case LABEL_3:
    f->PC = LABEL_4;
    if(sync(w)) { return; /*suspended*/ }
    
  case LABEL_4:
    f->PC = LABEL_5;
    LowerSolveC::lower_solve(w, Mb, L11, nb);
    if(w->abortOnSteal()) { return; }

  case LABEL_5:
    f->PC = LABEL_6;
    if(sync(w)) { return; /*suspended*/ }

  case LABEL_6:
    setupReturn(w);
    break;
  default:
    assert(0);
  }
}


