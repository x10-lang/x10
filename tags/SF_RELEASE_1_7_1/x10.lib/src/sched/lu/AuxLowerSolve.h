/** 
 * A C++ cws version of the Cilk LU code. cws stuff for aux_lower_solve routine
 * @author Sriram Krishnamoorthy
 */

#ifndef __AUX_LOWER_SOLVE_H__
#define __AUX_LOWER_SOLVE_H__

#include "base.h"

class AuxLowerSolveFrame;
class AuxLowerSolveC;

class AuxLowerSolveFrame : public Frame {
 public:
  matrix_t Ma, Mb, L;
  int nb;

  volatile int PC;

  AuxLowerSolveFrame(matrix_t _Ma, matrix_t _Mb, matrix_t _L, int _nb);
  virtual void setOutletOn(Closure *c) { /*no-op*/}
  virtual Closure *makeClosure();
  virtual AuxLowerSolveFrame *copy();

 private:
  AuxLowerSolveFrame(const AuxLowerSolveFrame &f);
};

class AuxLowerSolveC : public Closure {
 private:
  friend class AuxLowerSolveFrame;

 private:
  static const int ENTRY=0, LABEL_1=1, LABEL_2=2, 
    LABEL_3=3, LABEL_4=4, LABEL_5=5, LABEL_6=6, LABEL_7=7,
    LABEL_8=8, LABEL_9=9;

 public:
  virtual void setResultInt(int x) { /*no-op*/ }
  virtual int resultInt() { return 0; /*dummy*/ }

  //sequential code for verification
  static void seq_aux_lower_solve(matrix_t Ma, matrix_t Mb, matrix_t L, int nb);

  //fast mode
  static void aux_lower_solve(Worker *w, matrix_t Ma, matrix_t Mb, matrix_t L, int nb);
  
  AuxLowerSolveC(Frame *frame) : Closure(frame) {}
  ~AuxLowerSolveC() { delete frame; }

  virtual void compute(Worker *w, Frame *frame);
};


#endif  /*__AUX_LOWER_SOLVE_H__*/

