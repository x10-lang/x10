/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: xassert.h,v 1.6 2007-10-19 16:04:29 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_ASSERT_H__
#define __X10_ASSERT_H__

/* This is a wrapper around default assert which only prints
 * the message, but not causes the program to abort.
 * Additionally it also prints the process id.
 * Plus, it beeps :)
 * Use -DWARN compiler flag to enable this.
 */
#define stringize(a) #a

#ifdef __cplusplus
#ifdef WARN
#include <iostream>
using namespace std;
#define assert(cond) \
 do { \
 	if ((cond) == 0) \
    	cout << "assert " << stringize(cond) << " failed: " \
			<< x10lib::__x10_my_place << " " << __FILE__  << ", line " \
      		<< __LINE__ << "\a" << endl; \
 } while(0) 
#else
#include <cassert>
#endif

#else

#ifdef WARN
#define assert(cond) \
  do {									\
    if ((cond) == 0)							\
      printf ("assert %s failed %d %s line %d \a \n", stringize(cond), x10_here(), __FILE__, __LINE__); \
  }while(0)                                         
#else 
#include <assert.h>
#endif

#endif

#endif /* __X10_ASSERT__H__ */
