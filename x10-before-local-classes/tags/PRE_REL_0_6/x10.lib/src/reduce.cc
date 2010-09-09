/*
 * (c) Copyright IBM Corporation 2007
 * $Id: reduce.cc,v 1.5 2007-10-26 07:33:04 ganeshvb Exp $ 
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

using namespace x10lib;

void reduceInit()
{
  X10_DEBUG (1, "Entry");

  x10lib::reduce_cntr_list = (void**) malloc (sizeof(void*)*x10lib::__x10_num_places); 
  
  LAPI_Address_init (x10lib::__x10_hndl, (void*)&x10lib::reduce_cntr, x10lib::reduce_cntr_list);
  
  x10lib::reduce_list = (void**) malloc (sizeof(void*) * x10lib::__x10_num_places);  
  
  x10lib::scratch = x10lib::__x10_num_places == 1 ? NULL : new char[X10_MAX_REDUCE_OBJECT_SIZE * X10_MAX_REDUCE_OPS_INFLIGHT * LOG2(x10lib::__x10_num_places)];
  
  LAPI_Address_init (x10lib::__x10_hndl, (void*)x10lib::scratch, x10lib::reduce_list);  

  X10_DEBUG (1, "Exit");
}

void reduceFinalize()
{
  if (x10lib::scratch) 
    delete [] (char*) x10lib::scratch;
  delete [] x10lib::reduce_cntr_list;
  delete [] x10lib::reduce_list;
}

