/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Frame.h,v 1.14 2007-12-14 13:39:35 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_XWS_FRAME_H
#define __X10_XWS_FRAME_H

#include "Sys.h"
#include "Executable.h"

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib_xws {

class Closure;

class Frame : public Executable {
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

 protected:
  Frame(const Frame &f)  {
# if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
	  incCons(); 
#endif
  }
public:
  Frame() { 
#if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
    incCons(); 
#endif
}
  virtual inline ~Frame() { 
#if defined(MEM_DEBUG) && (MEM_DEBUG!=0)
    incDestruct(); 
#endif
  }
  
  virtual Closure *makeClosure();
  virtual void setOutletOn(Closure *c);
  virtual void setInt(int x);
  virtual Frame *copy() = 0; //should be implemented by sub-classes

  virtual Frame *copy(Worker *w) { return copy(); /*default implementation*/ }
  virtual void compute(Worker *ws);
  virtual Executable *execute(Worker *w);
};

} /* closing brace for namespace x10lib_xws */
#endif

#endif /* __X10_XWS_FRAME_H */
