/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: gas.h,v 1.1 2007-08-02 11:22:43 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Global address space interface. **/

#ifndef __X10_GAS_H
#define __X10_GAS_H

/* On 64-bit machine 32 + 64 = 96 bytes size */
/* Type to address locations across places uniquely. */
/*
 * !!! WARNING !!!
 * The internal implementation might be changed.
 * Rely only on the provided function macros to construct and
 * interpret the address.
 *
 */
struct __x10_gas_ref_t {
	int place; /* place id where this addr is hold */
	unsigned long addr; /* cast addr to (void *) */
};

typedef struct __x10_gas_ref_t x10_gas_ref_t;

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

	/* Construct GAS reference for the specified place
	 * and virtual address.
	 */
	x10_gas_ref_t MakeGASRef(int place, void *addr);

	/* Return the place id for the specified GAS reference. */
	int GAS2Place(x10_gas_ref_t ref);

	/* Return the associated virtual address for the specified
	 * GAS reference.
	 */
	void *GAS2Addr(x10_gas_ref_t ref);

} /* closing brace for namespace x10lib */
#endif

/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

/* Construct GAS reference for the specified place
 * and virtual address.
 */
x10_gas_ref_t x10_make_gas_ref(int place, void *addr);

/* Return the place id for the specified GAS reference. */
int x10_gas2place(x10_gas_ref_t ref);

/* Return the associated virtual address for the specified
 * GAS reference.
 */
void *x10_gas2addr(x10_gas_ref_t ref);

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

#endif /* __X10_GAS_H */
