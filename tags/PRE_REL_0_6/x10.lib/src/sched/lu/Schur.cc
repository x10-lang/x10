/** 
 * A C++ cws version of the Cilk LU code. cws stuff for schur routine.
 * @author Sriram Krishnamoorthy
 */

#include "Schur.h"

/*------------SchurFrame definitions------------------------*/

SchurFrame::SchurFrame(matrix_t _M, matrix_t _V, matrix_t _W, int _nb) 
  : M(_M), V(_V), W(_W), nb(_nb) { }

// /*virtual*/ void 
// SchurFrame::setOutletOn(Closure *c) {
//   //No outlet business. We just report completion
//   assert(0);
// }

/*virtual*/ Closure*
SchurFrame::makeClosure() {
  return new SchurC(this);
}

/*virtual*/ SchurFrame*
SchurFrame::copy() { 
  return new SchurFrame(*this);
}

SchurFrame::SchurFrame(const SchurFrame &f) 
  : M(f.M), V(f.V), W(f.W), nb(f.nb) {}

/*----------------SchurC definitions------------------------*/

/*static*/ void
SchurC::seq_schur(matrix_t M, matrix_t V, matrix_t W, int nb) {
  matrix_t M00, M01, M10, M11;
  matrix_t V00, V01, V10, V11;
  matrix_t W00, W01, W10, W11;
  int hnb;

  /* Check base case. */
  if (nb == 1) {
    block_schur(M, V, W);
    return;
  }
  /* Break matrices into 4 pieces. */
  hnb = nb / 2;
  M00 = MATRIX(M, 0, 0);
  M01 = MATRIX(M, 0, hnb);
  M10 = MATRIX(M, hnb, 0);
  M11 = MATRIX(M, hnb, hnb);
  V00 = MATRIX(V, 0, 0);
  V01 = MATRIX(V, 0, hnb);
  V10 = MATRIX(V, hnb, 0);
  V11 = MATRIX(V, hnb, hnb);
  W00 = MATRIX(W, 0, 0);
  W01 = MATRIX(W, 0, hnb);
  W10 = MATRIX(W, hnb, 0);
  W11 = MATRIX(W, hnb, hnb);
  
  /* Form Schur complement with recursive calls. */
  seq_schur(M00, V00, W00, hnb);
  seq_schur(M01, V00, W01, hnb);
  seq_schur(M10, V10, W00, hnb);
  seq_schur(M11, V10, W01, hnb);
  
  seq_schur(M00, V01, W10, hnb);
  seq_schur(M01, V01, W11, hnb);
  seq_schur(M10, V11, W10, hnb);
  seq_schur(M11, V11, W11, hnb);
}

//fast mode
/*static*/ void
SchurC::schur(Worker *w,
	      matrix_t M, matrix_t V, matrix_t W, int nb) {
  matrix_t M00, M01, M10, M11;
  matrix_t V00, V01, V10, V11;
  matrix_t W00, W01, W10, W11;
  int hnb;

  /* Break matrices into 4 pieces. */
  hnb = nb / 2;
  M00 = MATRIX(M, 0, 0);
  M01 = MATRIX(M, 0, hnb);
  M10 = MATRIX(M, hnb, 0);
  M11 = MATRIX(M, hnb, hnb);
  V00 = MATRIX(V, 0, 0);
  V01 = MATRIX(V, 0, hnb);
  V10 = MATRIX(V, hnb, 0);
  V11 = MATRIX(V, hnb, hnb);
  W00 = MATRIX(W, 0, 0);
  W01 = MATRIX(W, 0, hnb);
  W10 = MATRIX(W, hnb, 0);
  W11 = MATRIX(W, hnb, hnb);
  
  if (nb == 1) {
    block_schur(M, V, W);
    return;
  }

  SchurFrame *frame = new SchurFrame(M, V, W, nb);
  assert(frame != NULL);
  frame->PC = LABEL_1;
  w->pushFrame(frame);

  schur(w, M00, V00, W00, hnb);
  //If frame stolen, just return
  if(w->abortOnSteal()) { return; }

  frame->PC = LABEL_2;
  schur(w, M01, V00, W01, hnb);
  if(w->abortOnSteal()) { return; }
  
  frame->PC = LABEL_3;
  schur(w, M10, V10, W00, hnb);
  if(w->abortOnSteal()) { return; }

  frame->PC = LABEL_4;
  schur(w, M11, V10, W01, hnb);
  if(w->abortOnSteal()) { return; }
  
  frame->PC = LABEL_5;
  schur(w, M00, V01, W10, hnb);
  if(w->abortOnSteal()) { return; }

  frame->PC = LABEL_6;
  schur(w, M01, V01, W11, hnb);
  if(w->abortOnSteal()) { return; }

  frame->PC = LABEL_7;
  schur(w, M10, V11, W10, hnb);
  if(w->abortOnSteal()) { return; }

  frame->PC = LABEL_8;
  schur(w, M11, V11, W11, hnb);
  if(w->abortOnSteal()) { return; }

  w->popFrame();
  if(!w->cache->interrupted()) {
    delete frame;
  }
}

//slow mode
/*virtual*/ void
SchurC::compute(Worker *w, Frame *f) {
  SchurFrame *frame = dynamic_cast<SchurFrame *>(f);
  assert(frame != NULL);

  matrix_t M, V, W;
  int nb;

  M = frame->M;
  V = frame->V;
  W = frame->W;
  nb = frame->nb;

  matrix_t M00, M01, M10, M11;
  matrix_t V00, V01, V10, V11;
  matrix_t W00, W01, W10, W11;
  int hnb;

  /* Break matrices into 4 pieces. */
  hnb = nb / 2;
  M00 = MATRIX(M, 0, 0);
  M01 = MATRIX(M, 0, hnb);
  M10 = MATRIX(M, hnb, 0);
  M11 = MATRIX(M, hnb, hnb);
  V00 = MATRIX(V, 0, 0);
  V01 = MATRIX(V, 0, hnb);
  V10 = MATRIX(V, hnb, 0);
  V11 = MATRIX(V, hnb, hnb);
  W00 = MATRIX(W, 0, 0);
  W01 = MATRIX(W, 0, hnb);
  W10 = MATRIX(W, hnb, 0);
  W11 = MATRIX(W, hnb, hnb);

  switch(frame->PC) {
  case ENTRY:
    if (nb == 1) {
      block_schur(M, V, W);
      setupReturn(w);
      return;
    }

    frame->PC = LABEL_1;
    schur(w, M00, V00, W00, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_1:
    frame->PC = LABEL_2;  
    schur(w, M01, V00, W01, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_2:
      frame->PC = LABEL_3;
      schur(w, M10, V10, W00, hnb);
      if(w->abortOnSteal()) { return; }

  case LABEL_3:
    frame->PC = LABEL_4;
    schur(w, M11, V10, W01, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_4:
    frame->PC = LABEL_5;
    schur(w, M00, V01, W10, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_5:
    frame->PC = LABEL_6;
    schur(w, M01, V01, W11, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_6:
    frame->PC = LABEL_7;
    schur(w, M10, V11, W10, hnb);
    if(w->abortOnSteal()) { return; }
    
  case LABEL_7:
    frame->PC = LABEL_8;
    schur(w, M11, V11, W11, hnb);
    if(w->abortOnSteal()) { return; }

  case LABEL_8:
    frame->PC = LABEL_9;
    if(sync(w)) {
      return; //suspended
    }

  case LABEL_9:
    setupReturn(w);
    break;
  default:
    assert(0);
  }
}

