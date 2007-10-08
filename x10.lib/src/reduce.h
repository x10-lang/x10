/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: reduce.h,v 1.1 2007-10-08 15:10:25 ganeshvb Exp $ */

#ifndef __X10_REDUCE_H
#define __X10_REDUCE_H


#include <lapi.h>
#include <iostream>

#define LOG2(x)  (int) round (log (x) / log (2))

#define X10_MAX_REDUCE_OPS_INFLIGHT 4

#define X10_MAX_REDUCE_OBJECT_SIZE 4 * sizeof(double)

extern lapi_cntr_t   reduce_cntr;
extern void**        reduce_cntr_list;
extern void*         scratch;
extern void**        reduce_list;
extern void*         inbuf[X10_MAX_REDUCE_OPS_INFLIGHT];
extern int           reduceCount;

template <typename T, void F (T&, const T&)>
static x10_err_t commutative_reduce (T *values, int low, int high, int depth)
     /**************************************************************************
      *
      *   Recursively reduce the sum.  Note that this is not technically
      *   legal for a non-commutative operation like FP add, but this
      *   works for our checksums.
      *
      *************************************************************************/
{
  int src  = low + ((high - low) / 2);
  int i;
  
  if (depth > 0)
    {
      commutative_reduce<T, F> (values, low, src, depth - 1);
      commutative_reduce<T, F> (values, src, high, depth - 1);
    }
  
  if (x10lib::__x10_my_place == src)
    {    
      LAPI_Waitcntr (x10lib::__x10_hndl, &reduce_cntr, depth, NULL);
      
      for (i = 0; i < depth; i++) 
	{
	  for (int j = 0; j < reduceCount; j++) 
	    F (values[j],((T*) scratch)[reduceCount * i + j]);
	} 
      
      LRC (LAPI_Put (x10lib::__x10_hndl, low, reduceCount * sizeof(T),
		     (char*) reduce_list[low] + reduceCount * depth * sizeof(T), 
		     values, (lapi_cntr_t*) reduce_cntr_list[low], NULL, NULL)); 
    }
}

namespace x10lib {
  
  template <typename T>
    void reduce (T* sum)
    {
      assert (sizeof (T) <= X10_MAX_REDUCE_OBJECT_SIZE);
      
      assert (reduceCount < X10_MAX_REDUCE_OPS_INFLIGHT);
      
      inbuf[reduceCount] = sum;
      reduceCount++; 
    }
  
  
  template <typename T, void F (T&, const T&)>
    void finishReduceAll ()
    /**************************************************************************
     *
     *   Accumulate all of the 'sum' arrays onto the root process.
     *
     *************************************************************************/
    {
      int i;
      
      T* values = new T [reduceCount];
      
      for (int i = 0 ; i < reduceCount; i++)
	values[i] = *((T*) inbuf[i]);
      
      /* Set the counter to zero. */
      LAPI_Setcntr (x10lib::__x10_hndl, &reduce_cntr, 0);
      
      /* Zero out the reduce. */
      bzero (scratch, reduceCount * sizeof(T) * LOG2(x10lib::__x10_num_places));
      
      LAPI_Gfence (x10lib::__x10_hndl);
      
      /* Call commutative reduce. */
      commutative_reduce<T, F> (values, 0, x10lib::__x10_num_places, LOG2(x10lib::__x10_num_places) - 1);
      
      if (x10lib::__x10_my_place == 0)
      {
	LAPI_Waitcntr (x10lib::__x10_hndl, &reduce_cntr, LOG2(x10lib::__x10_num_places), NULL);
	for (i = 0; i < LOG2(x10lib::__x10_num_places); i++)
	  {
	    for (int j = 0; j < reduceCount; j++) {	      
	      F ( *((T*) (inbuf[j])), ((T*) scratch)[reduceCount * i + j]);
	    }
	  }
      }
      
      delete [] values;
      reduceCount = 0;
    }
  
}

#endif

// Local Variables:
// mode: C++
// End:
