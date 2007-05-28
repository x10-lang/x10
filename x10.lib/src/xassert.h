/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: xassert.h,v 1.1 2007-05-28 06:38:20 ganeshvb Exp $ */

#ifndef __X10_ASSERT_H__
#define __X10_ASSERT_H__

#ifdef WARN
//#include <iostream>
//using namespace std;

#define stringize(a) #a

  //  This is a wrapper around default assert which only prints the message,
  // but not causes the program to abort.
  // Additionally it also prints the process id.
  // And it does beep :)
  // Use -DWARN compiler flag to enable this
#define assert(cond) \
  do {									\
    if ((cond) == 0)							\
      cout << "assert " << stringize(cond) << " failed: " << x10lib::here () << " " << __FILE__  << ", line " \
      << __LINE__ << "\a" << endl;						\
  }while(0) 

#else

#include <cassert>

#endif

#endif /* __X10_ASSERT__H__ */
