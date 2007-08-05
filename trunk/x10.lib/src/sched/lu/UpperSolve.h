/** 
 * A C++ cws version of the Cilk LU code. cws stuff for upper_solve routine.
 * @author Sriram Krishnamoorthy
 */

#ifndef __UPPER_SOLVE_H__
#define __UPPER_SOLVE_H__

#include "base.h"

class UpperSolveFrame;
class UpperSolveC;

class UpperSolveFrame : public Frame {
 public:
  matrix_t M, U;
  int nb;

  volatile int PC;

  UpperSolveFrame(matrix_t _M, matrix_t _U, int _nb);
  virtual void setOutletOn(Closure *c) { /*no-op*/}
  virtual Closure *makeClosure();
  virtual UpperSolveFrame *copy();

 private:
  UpperSolveFrame(const UpperSolveFrame &f);
};

class UpperSolveC : public Closure {
 private:
  friend class UpperSolveFrame;

 private:
  static const int ENTRY=0, LABEL_1=1, LABEL_2=2, 
    LABEL_3=3, LABEL_4=4, LABEL_5=5, LABEL_6=6, LABEL_7=7,
    LABEL_8=8, LABEL_9=9;

 public:
  virtual void setResultInt(int x) { /*no-op*/ }
  virtual int resultInt() { return 0; /*dummy*/ }
  //sequential code for verification
  static void seq_upper_solve(matrix_t M, matrix_t U, int nb);

  //fast mode
  static void upper_solve(Worker *w, matrix_t M, matrix_t U, int nb);
  
  UpperSolveC(Frame *frame) : Closure(frame) {}
  ~UpperSolveC() { delete frame; }

  virtual void compute(Worker *w, Frame *frame);
};


#endif  /*__UPPER_SOLVE_H__*/


