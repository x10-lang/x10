/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: reduce.cc,v 1.2 2007-10-16 03:05:38 ipeshansky Exp $ */

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

void reduceInit()
{
  reduce_cntr_list = (void**) malloc (sizeof(void*)*x10lib::__x10_num_places); 
  
  LAPI_Address_init (x10lib::__x10_hndl, (void*)&reduce_cntr, reduce_cntr_list);
  
  reduce_list = (void**) malloc (sizeof(void*) * x10lib::__x10_num_places);  
  
  scratch = new char[X10_MAX_REDUCE_OBJECT_SIZE * X10_MAX_REDUCE_OPS_INFLIGHT * LOG2(x10lib::__x10_num_places)];
  
  LAPI_Address_init (x10lib::__x10_hndl, (void*)scratch, reduce_list);  
}

void reduceFinalize()
{
  delete [] (char*) scratch;
  delete [] reduce_cntr_list;
  delete [] reduce_list;
}

} // namespace x10lib
