/** 
 * A C++ cws version of the Cilk LU code. cws stuff for aux_upper_solve routine
 * @author Sriram Krishnamoorthy
 */

#include "base.h"
#include "UpperSolve.h"
#include "AuxUpperSolve.h"
#include "Schur.h"

/*----------AuxUpperSolveFrame definitions-----------------------*/

AuxUpperSolveFrame::AuxUpperSolveFrame(matrix_t _Ma, matrix_t _Mb, matrix_t _U, int _nb) 
  : Ma(_Ma), Mb(_Mb), U(_U), nb(_nb) {}

/*virtual*/ Closure *
AuxUpperSolveFrame::makeClosure() { return new AuxUpperSolveC(this); }

/*virtual*/ AuxUpperSolveFrame *
AuxUpperSolveFrame::copy() { return new AuxUpperSolveFrame(*this); }


AuxUpperSolveFrame::AuxUpperSolveFrame(const AuxUpperSolveFrame &f)
  : Ma(f.Ma), Mb(f.Mb), U(f.U), nb(f.nb) {}


/*--------------------AuxUpperSolveC definitions---------------------*/

/*static*/ void 
AuxUpperSolveC::seq_aux_upper_solve(matrix_t Ma, matrix_t Mb, matrix_t U, int nb) {
  matrix_t U00, U01, U10, U11;

  /* Break U matrix into 4 pieces. */
  U00 = MATRIX(U, 0, 0);
  U01 = MATRIX(U, 0, nb);
  U10 = MATRIX(U, nb, 0);
  U11 = MATRIX(U, nb, nb);

  /* Solve with recursive calls. */
  UpperSolveC::seq_upper_solve(Ma, U00, nb);
  SchurC::seq_schur(Mb, Ma, U01, nb);
  UpperSolveC::seq_upper_solve(Mb, U11, nb);
}

//fast mode
/*static*/ void 
AuxUpperSolveC::aux_upper_solve(Worker *w, matrix_t Ma, matrix_t Mb, matrix_t U, int nb) {
  matrix_t U00, U01, U10, U11;

  /* Break U matrix into 4 pieces. */
  U00 = MATRIX(U, 0, 0);
  U01 = MATRIX(U, 0, nb);
  U10 = MATRIX(U, nb, 0);
  U11 = MATRIX(U, nb, nb);

  AuxUpperSolveFrame *frame = new AuxUpperSolveFrame(Ma, Mb, U, nb);
  assert(frame != NULL);
  w->pushFrame(frame);

  frame->PC = LABEL_1;
  UpperSolveC::upper_solve(w, Ma, U00, nb);
  if(w->abortOnSteal()) { return; }

  frame->PC = LABEL_3;
  SchurC::schur(w, Mb, Ma, U01, nb);
  if(w->abortOnSteal()) { return; }

  frame->PC = LABEL_5;
  UpperSolveC::upper_solve(w, Mb, U11, nb);
  if(w->abortOnSteal()) { return; }

  w->popFrame();
  if(!w->cache->interrupted()) { delete frame; }
}

//slow mode 
/*virtual*/ void 
AuxUpperSolveC::compute(Worker *w, Frame *frame) {
  AuxUpperSolveFrame *f = dynamic_cast<AuxUpperSolveFrame *>(frame);
  assert(f != NULL);

  matrix_t Ma, Mb, U;
  int nb;

  Ma = f->Ma;
  Mb = f->Mb;
  U = f->U;
  nb = f->nb;

  matrix_t U00, U01, U10, U11;

  /* Break U matrix into 4 pieces. */
  U00 = MATRIX(U, 0, 0);
  U01 = MATRIX(U, 0, nb);
  U10 = MATRIX(U, nb, 0);
  U11 = MATRIX(U, nb, nb);

  switch(f->PC) {
  case ENTRY:
    f->PC = LABEL_1;
    UpperSolveC::upper_solve(w, Ma, U00, nb);
    if(w->abortOnSteal()) { return; }

  case LABEL_1:
    f->PC = LABEL_2;
    if(sync(w)) { return; /*suspended*/ }

  case LABEL_2:
    f->PC = LABEL_3;
    SchurC::schur(w, Mb, Ma, U01, nb);
    if(w->abortOnSteal()) { return; }

  case LABEL_3:
    f->PC = LABEL_4;
    if(sync(w)) { return; /*suspended*/ }

  case LABEL_4:
    f->PC = LABEL_5;
    UpperSolveC::upper_solve(w, Mb, U11, nb);
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
