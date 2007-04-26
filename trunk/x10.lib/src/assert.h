/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: assert.h,v 1.3 2007-04-26 05:34:14 ganeshvb Exp $ */

#ifndef __X10_ASSERT_H__
#define __X10_ASSERT_H__

#ifdef WARN
#include <iostream>
using namespace std;

#define stringize(a) #a

#define assert(cond) \

  //  This is a wrapper around default assert which only prints the message,
  // but not causes the program to abort.
  do {									\
    if ((cond) == 0)							\
      cout << "assert " << stringize(cond) << " failed: " << __FILE__  << ", line " \
      << __LINE__ <<  endl;						\
  }while(0) 

#else

#include <cassert>

#endif

#endif /* __X10_ASSERT__H__ */
