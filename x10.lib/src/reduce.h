/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: reduce.h,v 1.10 2007-12-10 13:38:14 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's interface for user-defined reductions **/

#ifndef __X10_REDUCE_H
#define __X10_REDUCE_H

#include <x10/types.h>
#include <x10/xmacros.h>
#include <x10/err.h>
#include <lapi.h>
#include <iostream>
#include <math.h>

#define LOG2(x)  (int) round (log ((double) x) / log (2.0))

#define X10_MAX_REDUCE_OPS_INFLIGHT 4

#define X10_MAX_REDUCE_OBJECT_SIZE 4 * sizeof(double)

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {


/*
 * Recursively reduce the sum.
 * Note that this is not technically legal for a non-commutative
 * operation like FP add, but this works for our checksums.
 */

template <typename T, void F (T&, const T&)>
static x10_err_t
CommutativeReduce(T *values, int low, int high, int depth)
{
	X10_DEBUG(1, "Entry");
	int src = low + ((high - low) / 2);
	int i;

	if (depth > 0) {
		CommutativeReduce<T,F> (values, low, src, (depth - 1));
		CommutativeReduce<T,F> (vlaues, src, high, (depth - 1));
	}

	if (__x10_my_place == src && __x10_num_places > 1) {
		LAPIStyleWaitcntr(&reduce_cntr, depth, NULL);

		for (i = 0; i < depth; i++) {
			for (int j = 0; j < reduceCount; j++)
				F(values[j],((T*)scratch)[reduceCount * i + j]);
		}

		LAPIStylePut(low, reduceCount * sizeof(T),
			(char *)reduce_list[low] + reduceCount * depth * sizeof(T),
			values, (lapi_cntr_t *)reduce_cntr_list[low],
			NULL, NULL);
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
	assert(sizeof(T) <= X10_MAX_REDUCE_OBJECT_SIZE);
	assert(reduceCount < X10_MAX_REDUCE_OPS_INFLIGHT);
	inbuf[reduceCount] = var;
	reduceCount++;
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

	T *values = new T [reduceCount];

	/* Accumulate all of the sum arrays onto the root process. */
	for (int i = 0; i < reduceCount; i++)
		values[i] = *((T*) inbuf[i]);

	/* Set the counter to zero. */
	LAPIStyleSetcntr(&reduce_cntr, 0);

	/* Zero out the reduce. */
	memset(scratch, 0, reduceCount * sizeof(T) *
			LOG2(__x10_num_places));

	SyncGlobal();

	/* Call commutative reduce. */
	CommutativeReduce<T,F> (values, 0, __x10_num_places,
		LOG2(__x10_num_places) - 1);

	if (__x10_my_place == 0) {
		LAPIStyleWaitcntr(&reduce_cntr, LOG2(__x10_num_places), NULL);
		for (i = 0; i < LOG2(__x10_num_places); i++) {
			for (int j = 0; j < reduceCount; j++) {
				F(*((T*)(inbuf[j])), ((T*)scratch)[reduceCount * i + j]);
			}
		}
	}
	delete [] values;
	reduceCount = 0;
	X10_DEBUG(1, "Exit");
}

} /* closing brace for namespace x10lib */
#endif

#endif /* __X10_REDUCE_H */
