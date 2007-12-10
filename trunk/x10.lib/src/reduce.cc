/*
 * (c) Copyright IBM Corporation 2007
 * $Id: reduce.cc,v 1.6 2007-12-10 10:22:44 srkodali Exp $ 
 * This file is part of X10 Runtime System.
 */

#include <x10/types.h>
#include <x10/reduce.h>
#include <iostream>
#include <lapi.h>
#include <math.h>
#include "x10libP.h"

namespace x10lib {

/* reduction variables */
lapi_cntr_t reduce_cntr;
void **reduce_cntr_list;
void *scratch;
void **reduce_list;
void *inbuf[X10_MAX_REDUCE_OPS_INFLIGHT];
int reduceCount = 0;

} /* closing brace for namespace x10lib */

/* The following two methods should be available
 * during x10lib's initialization; mark them as external.
 */

using namespace x10lib;

/* Initialize Reduction */
void ReduceInit()
{
	X10_DEBUG(1, "Entry");

	x10lib::reduce_cntr_list = (void **)malloc(sizeof(void *) * __x10_num_places);
	LAPI_Address_init(__x10_hndl, (void *)&x10lib::reduce_cntr,
				x10lib::reduce_cntr_list);
	x10lib::reduce_list = (void **)malloc(sizeof(void *) * __x10_num_places);
	x10lib::scratch = (__x10_num_places == 1) ? NULL :
		new char[X10_MAX_REDUCE_OBJECT_SIZE *
				X10_MAX_REDUCE_OPS_INFLIGHT *
				LOG2(__x10_num_places)];
	LAPI_Address_init(__x10_hndl, (void *)x10lib::scratch, x10lib::reduce_list);

	X10_DEBUG(1, "Exit");
}

/* Finialize Reduction */
void ReduceFinalize()
{
	if (x10lib::scratch)
		delete [] (char *)x10lib::scratch;
	delete [] x10lib::reduce_cntr_list;
	delete [] x10lib::reduce_list;
}
