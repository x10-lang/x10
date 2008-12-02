/** 
 * A C++ cws version of the Cilk LU code. cws stuff for lower_solve routine
 * @author Sriram Krishnamoorthy
 */

#ifndef __LOWER_SOLVE_H__
#define __LOWER_SOLVE_H__

#include "base.h"

class LowerSolveFrame;
class LowerSolveC;

class LowerSolveFrame : public Frame {
 public:
  matrix_t M, L;
  int nb;

  volatile int PC;

  LowerSolveFrame(matrix_t _M, matrix_t _L, int _nb);
  virtual void setOutletOn(Closure *c) { /*no-op*/}
  virtual Closure *makeClosure();
  virtual LowerSolveFrame *copy();

 private:
  LowerSolveFrame(const LowerSolveFrame &f);
};

class LowerSolveC : public Closure {
 private:
  friend class LowerSolveFrame;

 private:
  static const int ENTRY=0, LABEL_1=1, LABEL_2=2, 
    LABEL_3=3, LABEL_4=4, LABEL_5=5, LABEL_6=6, LABEL_7=7,
    LABEL_8=8, LABEL_9=9;

 public:
  virtual void setResultInt(int x) { /*no-op*/ }
  virtual int resultInt() { return 0; /*dummy*/ }
  //sequential code for verification
  static void seq_lower_solve(matrix_t M, matrix_t L, int nb);

  //fast mode
  static void lower_solve(Worker *w, matrix_t M, matrix_t L, int nb);
  
  LowerSolveC(Frame *frame) : Closure(frame) {}
  ~LowerSolveC() { delete frame; }

  virtual void compute(Worker *w, Frame *frame);
};


#endif  /*__LOWER_SOLVE_H__*/

