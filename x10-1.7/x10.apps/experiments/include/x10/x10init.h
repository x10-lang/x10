/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: x10init.h,v 1.1 2007-08-02 11:22:45 srkodali Exp $
 * This file is part of X10 Runtime System.
 */


#ifndef __X10_X10INIT_H
#define __X10_X10INIT_H

#ifdef __cplusplus
#ifdef STATICINIT
class __lapi__init__ {
  
  static int count;
  
 public:
  
  __lapi__init__() {
    
    if (count++ == 0) {
      
      x10lib::Init(NULL, 0);
      
      std::cerr << "Initialized" << std::endl;
      
    }    
  }
  
  ~__lapi__init__() {
    
    if (--count == 0) {      
      x10lib::Finalize();
      
      std::cerr << "Finalized" << std::endl;      
    }    
  }  
};

int __lapi__init__::count = 0;
static __lapi__init__ __lapi__init__counter;
#endif
#endif

#endif /* __X10_X10INIT_H */
