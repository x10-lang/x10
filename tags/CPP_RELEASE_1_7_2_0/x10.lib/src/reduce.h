/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: reduce.h,v 1.15 2008-06-04 12:15:17 ipeshansky Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's interface for user-defined reductions **/

#ifndef __X10_REDUCE_H
#define __X10_REDUCE_H

#include <lapi.h>

#include <iostream>
#include <math.h>

#include <x10/err.h>
#include <x10/xmacros.h>

#define LOG2(x)  (int) round (log ((double) x) / log (2.0))

#define X10_MAX_REDUCE_OPS_INFLIGHT 4

#define X10_MAX_REDUCE_OBJECT_SIZE 4 * sizeof(double)


/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {
  extern int __x10_num_places;
  extern int __x10_my_place;

  extern void ReduceCounterInit();
  extern void ReduceCounterWait(int depth);
  extern void ReduceTransferData(int low, size_t size, int depth, void *values);
  extern void ReduceDataEnqueue(void *var, size_t size);
  extern void *ReduceDataRetrieve(int index);
  extern int ReduceDataGetCount();
  extern void ReduceDataInitCount();
  extern void ReduceTempStorageInit(size_t size);
  extern void *ReduceTempStorageGet(size_t size, int i, int j);
 
 /*
  * Recursively reduce the sum.
  * Note that this is not technically legal for a non-commutative
  * operation like FP add, but this works for our checksums.
  */
 
 template <typename T, void F (T&, const T&)>
   static x10_err_t
   CommutativeReduce(T *values, int total, int low, int high, int depth)
   {
     X10_DEBUG(1, "Entry");
     int src = low + ((high - low) / 2);
     int i;
     
	if (depth > 0) {
	  CommutativeReduce<T,F> (values, total, low, src, (depth - 1));
	  CommutativeReduce<T,F> (values, total, src, high, (depth - 1));
	}
	
	if (__x10_my_place == src && __x10_num_places > 1) {
	  ReduceCounterWait(depth);
	  
	  for (i = 0; i < depth; i++) {
	    for (int j = 0; j < total; j++)
	      F(values[j],
	        *((T*) x10lib::ReduceTempStorageGet(sizeof(T), i, j)));
	  }
	  
	  x10lib::ReduceTransferData(low, sizeof(T), depth, values);
	}
	X10_DEBUG(1, "Exit");
	return X10_OK;
   }
 
 /*
 * Perform a reduction operation over the variable var.
 * This operation just queues the reduction operations.
 * The reduction itself is performed by FinishReduceAll.
 * T - reduction type
 * var - reduction variable
 */
template <typename T>
  void Reduce(T *var)
  {
    X10_DEBUG(1, "Entry");
	x10lib::ReduceDataEnqueue(var, sizeof(T));
	X10_DEBUG(1, "Exit");
}
 
/*
 * Finish-up all the queued reductions.
 * T - reduction type
 * F - reduction funcion (should be commutative)
 */
 template <typename T, void F (T&, const T&)>
   void FinishReduceAll ()
   {
     X10_DEBUG(1, "Entry");
	int i;
	int reduceCount = x10lib::ReduceDataGetCount();
	
	T *values = new T [reduceCount];
	
	//Accumulate all of the sum arrays onto the root process. 
	for (int i = 0; i < reduceCount; i++)
	  values[i] = *((T*) ReduceDataRetrieve(i));
	
	//Set the counter to zero. 
	x10lib::ReduceCounterInit();
	
	//Zero out the reduce. 
	x10lib::ReduceTempStorageInit(sizeof(T));

	x10lib::SyncGlobal();
	
	//Call commutative reduce.
	CommutativeReduce<T,F> (values, reduceCount, 0, __x10_num_places,
		LOG2(__x10_num_places) - 1);
	
	if (__x10_my_place == 0) {
	  ReduceCounterWait(LOG2(__x10_num_places));
	  for (i = 0; i < LOG2(__x10_num_places); i++) {
	    for (int j = 0; j < reduceCount; j++) {
	      F(*((T*) x10lib::ReduceDataRetrieve(j)),
	        *((T*) x10lib::ReduceTempStorageGet(sizeof(T), i, j)));
	    }
	  }
	}
	delete [] values;
	ReduceDataInitCount();
	X10_DEBUG(1, "Exit");
   }
 
} /* closing brace for namespace x10lib */
#endif

#endif /* __X10_REDUCE_H */
