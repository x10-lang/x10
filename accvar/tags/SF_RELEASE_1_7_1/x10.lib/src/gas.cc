/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: gas.cc,v 1.6 2007-12-10 12:12:04 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/* Implementation file for Global address space. */

#include <x10/xassert.h>
#include <x10/gas.h>
#include <lapi.h>
#include "__x10lib.h__"

namespace x10lib {

/* Construct GAS reference for the specified place and
 * virtual address.
 */
x10_gas_ref_t MakeGASRef(int place, void *addr)
{
	x10_gas_ref_t ref;

	/* sanity check; is place valid? */
	if (!__x10_inited || place < 0 || place > (__x10_num_places - 1)) {
		ref.place = -1;
		ref.addr = (unsigned long)NULL;
	} else {
		/* local reference */
		if (place == __x10_my_place) {
			ref.place = place;
			ref.addr = (unsigned long)addr;
		/* global reference */
		} else {
			lapi_long_t *add_tab = new lapi_long_t [__x10_num_places];
			assert(add_tab != NULL);

			/* exchange virtual addresses */
			(void)LAPI_Address_init64(__x10_hndl, (lapi_long_t)addr, add_tab);
			(void)LAPI_Gfence(__x10_hndl);
			ref.place = place;
			ref.addr = (unsigned long)add_tab[place];
			delete [] add_tab;
		}
	}
	return ref;
}

/* Return the place id for the specified GAS reference. */
int GAS2Place(x10_gas_ref_t ref)
{
	return (ref.place);
}

/* Return the associated virtual address for the specified
 * GAS reference.
 */
void *GAS2Addr(x10_gas_ref_t ref)
{
	return ((void *)ref.addr);
}

} /* closing brace for namespace x10lib */

/* Construct GAS reference for the specified place and
 * virtual address.
 */
extern "C"
x10_gas_ref_t x10_make_gas_ref(int place, void *addr)
{
	return x10lib::MakeGASRef(place, addr);
}

/* Return the place id for the specified GAS reference. */
extern "C"
int x10_gas2place(x10_gas_ref_t ref)
{
	return x10lib::GAS2Place(ref);
}

/* Return the associated virtual address for the specified
 * GAS reference.
 */
extern "C"
void *x10_gas2addr(x10_gas_ref_t ref)
{
	return x10lib::GAS2Addr(ref);
}
