/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: switch.cc,v 1.12 2007-06-26 16:05:57 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for X10Lib's Switch interface. */

#include <x10/switch.h>
#include <x10/xassert.h>

namespace x10lib {

/* Switch constructor (1) */
Switch::Switch(int val)
{
	extern lapi_handle_t __x10_hndl;
	extern int __x10_inited;

	if (!__x10_inited) return;

	cntrp = new lapi_cntr_t;
	assert(cntrp != NULL);
	(void)LAPI_Setcntr(__x10_hndl, cntrp, val);
}

/* Switch destructor */
Switch::~Switch(void)
{
	delete cntrp;
}
	
/* Get switch internal handle. */
lapi_cntr_t *Switch::get_handle(void)
{
	return (cntrp);
}

/* Decrement switch before waiting once again. */
void Switch::decrement(void)
{
	extern lapi_handle_t __x10_hndl;
	int val;

	(void)LAPI_Getcntr(__x10_hndl, cntrp, &val);
	val -= 1;
	(void)LAPI_Setcntr(__x10_hndl, cntrp, val);
}

/* Perform next (wait) operation on switch. */
void Switch::next(void)
{
	extern lapi_handle_t __x10_hndl;

	(void)LAPI_Waitcntr(__x10_hndl, cntrp, 0, NULL);
}

/* Allocate switch object and return reference to it. */
x10_switch_t AllocSwitch()
{
	x10_switch_t sw = new Switch(0);
	assert(sw != NULL);
	return sw;
}

/* Deallocate switch object. */
void FreeSwitch(x10_switch_t sw)
{
	if (sw)
		delete sw;
}

} /* closing brace for namespace x10lib */

/* Allocate switch object and return reference to it. */
x10_switch_t x10_alloc_switch()
{
	return x10lib::AllocSwitch();
}

/* Deallocate switch object. */
void x10_free_switch(x10_switch_t sw)
{
	return x10lib::FreeSwitch(sw);
}

/* Decrement switch before waiting once again. */
void x10_decrement_switch(x10_switch_t sw)
{
	return sw->decrement();
}

/* Perform wait on switch. */
extern "C"
void x10_next_on_switch(x10_switch_t sw)
{
	return sw->next();
}

/* return the underlying lapi_cntr_t */
lapi_cntr_t* x10_switch_get_handle (x10_switch_t sw)
{
        return sw->get_handle();
}
