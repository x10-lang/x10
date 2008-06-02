/*
 * (c) Copyright IBM Corporation 2007
 * $Id: reduce.cc,v 1.11 2008-06-02 16:07:50 ipeshansky Exp $ 
 * This file is part of X10 Runtime System.
 */

#include <iostream>
#include <math.h>

#include <lapi.h>

#include <x10/types.h>
#include <x10/x10lib.h>
#include "__x10lib.h__"
#include <x10/reduce.h>
#include <x10/rmc.h>

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

//using namespace x10lib;

/* Initialize Reduction */
void ReduceInit()
{
	X10_DEBUG(1, "Entry");

	x10lib::reduce_cntr_list = (void **)malloc(sizeof(void *) * x10lib::__x10_num_places);
	LAPI_Address_init(x10lib::__x10_hndl, (void *)&x10lib::reduce_cntr,
				x10lib::reduce_cntr_list);
	x10lib::reduce_list = (void **)malloc(sizeof(void *) * x10lib::__x10_num_places);
	x10lib::scratch = (x10lib::__x10_num_places == 1) ? NULL :
		new char[X10_MAX_REDUCE_OBJECT_SIZE *
				X10_MAX_REDUCE_OPS_INFLIGHT *
				LOG2(x10lib::__x10_num_places)];
	LAPI_Address_init(x10lib::__x10_hndl, (void *)x10lib::scratch, x10lib::reduce_list);

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

/* Initialize Reduce Counter */
void ReduceCounterInit() {
	//Set the counter to zero. 
	x10lib::LAPIStyleSetcntr(&x10lib::reduce_cntr, 0);
}

/* Wait on a Reduce Counter */
void ReduceCounterWait(int depth) {
	x10lib::LAPIStyleWaitcntr(&x10lib::reduce_cntr, depth, NULL);
}

/* Transfer Data On Reduce Counters */
void ReduceTransferData(int low, size_t size, int depth, void *values) {
	x10lib::LAPIStylePut(low, x10lib::reduceCount * size,
			(char *)x10lib::reduce_list[low] + x10lib::reduceCount * depth * size,
			values, (lapi_cntr_t *)x10lib::reduce_cntr_list[low],
			NULL, NULL);
}

/* Enqueue Data For Reduction */
void ReduceDataEnqueue(void *var, size_t size) {
	assert(size <= X10_MAX_REDUCE_OBJECT_SIZE);
	assert(x10lib::reduceCount < X10_MAX_REDUCE_OPS_INFLIGHT);
	x10lib::inbuf[x10lib::reduceCount] = var;
	x10lib::reduceCount++;
}

/* Retrieve Enqueued Reduction Data */
void *ReduceDataRetrieve(int index) {
	assert(index < x10lib::reduceCount);
	return x10lib::inbuf[index];
}

/* Get Count of Enqueued Reduction Data */
int ReduceDataGetCount() {
	return x10lib::reduceCount;
}

/* Initialize Count of Enqueued Reduction Data */
int ReduceDataInitCount() {
	x10lib::reduceCount = 0;
}

/* Initialize Reduction Temporary Storage */
void ReduceTempStorageInit(size_t size) {
	//Zero out the reduce. 
	memset(x10lib::scratch, 0, x10lib::reduceCount * size * LOG2(x10lib::__x10_num_places));
}

/* Get Reduction Temporary Storage Location */
void *ReduceTempStorageGet(size_t size, int i, int j) {
	return ((char*) x10lib::scratch) + size * (x10lib::reduceCount * i + j);
}

