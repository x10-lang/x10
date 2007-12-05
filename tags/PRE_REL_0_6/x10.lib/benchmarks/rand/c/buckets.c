/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: buckets.c,v 1.2 2007-05-03 11:40:31 srkodali Exp $
 * This file is part of X10 Runtime System.
 *
 * Original version of this code using LAPI C/C++ is due to
 * Hanhong Xue <hanhong@us.ibm.com>
 */

/** Functins for bucket sorting **/
#include <x10/x10lib.h>
#include "gups.h"

/* bucket vars & stat counters */
element bucket[MAX_TASKS];
element *head[AGG_LIMIT + 1];
long nsends = 0;
long nelements = 0;
long max_bucket_size;
long search_count = 0;

/* initialize buckets for all tasks */
void
init_buckets(void)
{
	int i;
	
	for (i = 0; i < AGG_LIMIT; i++)
		head[i] = NULL;
	max_bucket_size = 0;
	head[0] = &bucket[0];
	for (i = 0; i < num_tasks; i++) {
		bucket[i].dest = i;
		bucket[i].count = 0;
		bucket[i].before = &bucket[i - 1];
		bucket[i].after = &bucket[i + 1];
	}
	bucket[i].before = NULL;
	bucket[num_tasks - 1].after = NULL;
}

/* push an element (at the front) */
void
push(element *e)
{
	e->before = NULL;
	e->after = head[e->count];
	head[e->count] = e;
	if (e->after)
		e->after->before = e;
}

/* deque the specified element */
void
deque(element *e)
{
	if (e->before)
		e->before->after = e->after;
	else
		head[e->count] = e->after;
	if (e->after)
		e->after->before = e->before;
}

/* add location to the destination bucket */
void
add_to_bucket(int dest, uint64_t ran)
{
	element *e = &bucket[dest];
	deque(e);
	e->update[e->count] = ran;
	e->count++;
	push(e);
	
	if (e->count > max_bucket_size)
		max_bucket_size = e->count;
	nelements++;
	if (nelements >= AGG_LIMIT) {
		x10_gas_ref_t gas_ref;
		element *e = head[max_bucket_size];
		
		nsends++;
		gas_ref = MakeGasRef(dest, 0);
		X10RC(x10_xfer(e->update, gas_ref, \
				e->count * sizeof(uint64_t), (void *)1));
		deque(e);
		e->count = 0;
		push(e);
		nelements -= max_bucket_size;
		
		while (head[max_bucket_size] == NULL) {
			search_count++;
			max_bucket_size--;
		}
	}
}

/* flush all buckets */
void
flush_buckets(void)
{
	int dest;
	
	for (dest = 0; dest < num_tasks; dest++) {
		x10_gas_ref_t gas_ref;
		element *e = &bucket[dest];
		
		if (e->count > 0) {
			nsends++;
			gas_ref = MakeGasRef(dest, 0);
			X10RC(x10_xfer(e->update, gas_ref,
					e->count * sizeof(uint64_t), (void *)1));
		}
	}
}
