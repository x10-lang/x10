/** 
 * A C++ cws version of the Cilk LU code. cws stuff for lu routine
 * @author Sriram Krishnamoorthy
 */

#ifndef __Lu_h__
#define __Lu_h__

#include "base.h"

class LuFrame;
class LuC; 

class LuFrame : public Frame {
 public:
  matrix_t M;
  const int nb;

  volatile int PC;

  LuFrame(matrix_t _M, int _nb);
  virtual void setOutletOn(Closure *c) { /*no-op*/ }

  virtual Closure *makeClosure();
  virtual LuFrame *copy();

 private:
  LuFrame(const LuFrame &f);
};

class LuC : public Closure {
 private:
  friend class LuFrame;

 private:
  static const int ENTRY=0, LABEL_1=1, LABEL_2=2, 
    LABEL_3=3, LABEL_4=4, LABEL_5=5, LABEL_6=6, LABEL_7=7,
    LABEL_8=8, LABEL_9=9;

 public:
  virtual void setResultInt(int x) { /*no-op*/ }
  virtual int resultInt() { return 0; /*dummy*/ }
  //sequential code for verification
  static void seq_lu(matrix_t M, int nb);

  //fast mode
  static void lu(Worker *w, matrix_t M, int nb);
  
  LuC(Frame *frame) : Closure(frame) {}
  ~LuC() { delete frame; }

  //slow mode
  virtual void compute(Worker *w, Frame *frame);
};


#endif /*__Lu_h__*/
