/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 */

/* $Id: reduce.h,v 1.7 2007-10-26 07:33:04 ganeshvb Exp $ */

/* Implementation file for user-defined reductions */

#ifndef __X10_REDUCE_H
#define __X10_REDUCE_H

#include <x10/xmacros.h>
#include <lapi.h>
#include <iostream>
#include <math.h>

#define LOG2(x)  (int) round (log ((double) x) / log (2.0))

#define X10_MAX_REDUCE_OPS_INFLIGHT 4

#define X10_MAX_REDUCE_OBJECT_SIZE 4 * sizeof(double)

namespace x10lib {

extern lapi_cntr_t   reduce_cntr;
extern void**        reduce_cntr_list;
extern void*         scratch;
extern void**        reduce_list;
extern void*         inbuf[X10_MAX_REDUCE_OPS_INFLIGHT];
extern int           reduceCount;

 /**************************************************************************
  *
  *   Recursively reduce the sum.  Note that this is not technically
  *   legal for a non-commutative operation like FP add, but this
  *   works for our checksums.
  *
  *************************************************************************/
template <typename T, void F (T&, const T&)>
static x10_err_t 
commutative_reduce (T *values, int low, int high, int depth)
{

  X10_DEBUG (1, "Entry");
  int src  = low + ((high - low) / 2);
  int i;
  
  if (depth > 0)
    {
      commutative_reduce<T, F> (values, low, src, depth - 1);
      commutative_reduce<T, F> (values, src, high, depth - 1);
    }
  
  if (x10lib::__x10_my_place == src && __x10_num_places > 1)
    {    
      // LAPI_Waitcntr (x10lib::__x10_hndl, &reduce_cntr, depth, NULL);
      
      x10lib::LAPIStyleWaitcntr (&reduce_cntr, depth, NULL);

      for (i = 0; i < depth; i++) 
	{
	  for (int j = 0; j < reduceCount; j++) 
	    F (values[j],((T*) scratch)[reduceCount * i + j]);
	} 
      
      x10lib::LAPIStylePut (low, reduceCount * sizeof (T), (char*) reduce_list[low] + reduceCount * depth * sizeof(T),
	   values, (lapi_cntr_t*) reduce_cntr_list[low], NULL, NULL);
      
      //LRC (LAPI_Put (x10lib::__x10_hndl, low, reduceCount * sizeof(T),
      //	     (char*) reduce_list[low] + reduceCount * depth * sizeof(T), 
      //	     values, (lapi_cntr_t*) reduce_cntr_list[low], NULL, NULL)); 
    }
  X10_DEBUG (1, "Exit");
  return X10_OK;
}

  /**
   * Perform a reduction operation over the variable var 
   * This operation just queues the reduction operations
   * The reduction itself is performed by finishReduceAll.

   * T -- reduction type
   * var -- reduction variable
   */

  template <typename T>
    void reduce (T* var)
    {
      X10_DEBUG (1, "Entry");

      assert (sizeof (T) <= X10_MAX_REDUCE_OBJECT_SIZE);
      
      assert (reduceCount < X10_MAX_REDUCE_OPS_INFLIGHT);
      
      inbuf[reduceCount] = var;
      reduceCount++; 

      X10_DEBUG (1, "Exit");
    }
  

  /**
   * Finish up all the queued reductions
   * T -- reduction type
   * F -- reduction function (should be commutative)
   */

  template <typename T, void F (T&, const T&)>
    void finishReduceAll ()
    /**************************************************************************
     *
     *   Accumulate all of the 'sum' arrays onto the root process.
     *
     *************************************************************************/
    {
      X10_DEBUG (1, "Entry");
      int i;
      
      T* values = new T [reduceCount];
      
      for (int i = 0 ; i < reduceCount; i++)
	values[i] = *((T*) inbuf[i]);
      
      /* Set the counter to zero. */
      //LAPI_Setcntr (x10lib::__x10_hndl, &reduce_cntr, 0);
      LAPIStyleSetcntr (&reduce_cntr, 0);
      /* Zero out the reduce. */
      memset (scratch, 0, reduceCount * sizeof(T) * LOG2(x10lib::__x10_num_places));
      
      //LAPI_Gfence (x10lib::__x10_hndl);
      SyncGlobal ();

      /* Call commutative reduce. */
      commutative_reduce<T, F> (values, 0, x10lib::__x10_num_places, LOG2(x10lib::__x10_num_places) - 1);
      
      if (x10lib::__x10_my_place == 0)
      {
        x10lib::LAPIStyleWaitcntr (&reduce_cntr, LOG2(x10lib::__x10_num_places), NULL);
	//LAPI_Waitcntr (x10lib::__x10_hndl, &reduce_cntr, LOG2(x10lib::__x10_num_places), NULL);
	for (i = 0; i < LOG2(x10lib::__x10_num_places); i++)
	  {
	    for (int j = 0; j < reduceCount; j++) {	      
	      F ( *((T*) (inbuf[j])), ((T*) scratch)[reduceCount * i + j]);
	    }
	  }
      }
      
      delete [] values;
      reduceCount = 0;
      X10_DEBUG (1, "Exit");
    }
  
}

#endif

// Local Variables:
// mode: C++
// End:
