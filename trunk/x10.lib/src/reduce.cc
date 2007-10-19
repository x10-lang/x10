/*
 * (c) Copyright IBM Corporation 2007
 * $Id: reduce.cc,v 1.3 2007-10-19 16:04:29 ganeshvb Exp $ 
 * This file is part of X10 Runtime System.
 */

#include <x10/x10lib.h>
#include <iostream>
#include <math.h>

#define LOG2(x)  (int) round (log ((double) x) / log (2.0))

#define X10_MAX_REDUCE_OPS_INFLIGHT 4

#define X10_MAX_REDUCE_OBJECT_SIZE 4 * sizeof(double)

namespace x10lib {

lapi_cntr_t   reduce_cntr;
void**        reduce_cntr_list;
void*         scratch;
void**        reduce_list;
void*         inbuf[X10_MAX_REDUCE_OPS_INFLIGHT];
int           reduceCount = 0;

}

void reduceInit()
{
  x10lib::reduce_cntr_list = (void**) malloc (sizeof(void*)*x10lib::__x10_num_places); 
  
  LAPI_Address_init (x10lib::__x10_hndl, (void*)&x10lib::reduce_cntr, x10lib::reduce_cntr_list);
  
  x10lib::reduce_list = (void**) malloc (sizeof(void*) * x10lib::__x10_num_places);  
  
  x10lib::scratch = new char[X10_MAX_REDUCE_OBJECT_SIZE * X10_MAX_REDUCE_OPS_INFLIGHT * LOG2(x10lib::__x10_num_places)];
  
  LAPI_Address_init (x10lib::__x10_hndl, (void*)x10lib::scratch, x10lib::reduce_list);  
}

void reduceFinalize()
{
  delete [] (char*) x10lib::scratch;
  delete [] x10lib::reduce_cntr_list;
  delete [] x10lib::reduce_list;
}

