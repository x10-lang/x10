/*
============================================================================
 Name        : Frame.h
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
#ifndef x10lib_Frame_h
#define x10lib_Frame_h

#include "Sys.h"

namespace x10lib_cws {

class Closure;

class Frame {
 protected:
  Frame(const Frame &f) { incCons; }
public:
  Frame() { incCons(); }
  virtual ~Frame() { incDestruct(); }
  
  virtual Closure *makeClosure();
  virtual void setOutletOn(Closure *c);
  virtual void setInt(int x);
  virtual Frame *copy() = 0; //should be implemented by sub-classes

 public:
#if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
  static volatile int nCons, nDestruct;
#endif
  inline static void incCons() {
# if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
    atomic_add(&nCons, 1);
# endif
  }
  inline static void incDestruct() {
# if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
    atomic_add(&nDestruct, 1);
# endif
  }
};

}
#endif
