/** 
 * A C++ cws version of the Cilk LU code. cws stuff for schur routine.
 * @author Sriram Krishnamoorthy
 */

#ifndef __SCHUR_H__
#define __SCHUR_H__

#include "base.h"

class SchurFrame;
class SchurC; 

/* class anon_Outlet1: public virtual Outlet { */
/* } */

class SchurFrame : public Frame {
/*  private: */
/*   friend class anon_Outlet1; */

 public:
  matrix_t M, V, W;
  const int nb;

  volatile int PC;

  SchurFrame(matrix_t _M, matrix_t _V, matrix_t _W, int _nb);
  virtual void setOutletOn(Closure *c) { /*no-op*/}
  virtual Closure *makeClosure();
  virtual SchurFrame *copy();

 private:
  SchurFrame(const SchurFrame &f);
};

class SchurC : public Closure {
 private:
  friend class SchurFrame;
/*   friend class anon_Outlet1; */

 private:
  static const int ENTRY=0, LABEL_1=1, LABEL_2=2, 
    LABEL_3=3, LABEL_4=4, LABEL_5=5, LABEL_6=6, LABEL_7=7,
    LABEL_8=8, LABEL_9=9;

 public:
  //sequential code for verification
  static void seq_schur(matrix_t M, matrix_t V, matrix_t W, int nb);

  //fast mode
  static void schur(Worker *w, matrix_t M, matrix_t V, matrix_t W, int nb);
  
  SchurC(Frame *frame) : Closure(frame) {}
  ~SchurC() { delete frame; }

  virtual void compute(Worker *w, Frame *frame);

 protected:
  /*reporting completion*/ 
};

#endif /*__SCHUR_H__*/
