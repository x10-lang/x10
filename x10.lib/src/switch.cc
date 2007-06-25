/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: switch.cc,v 1.10 2007-06-25 15:47:47 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for X10Lib's Switch interface. */

#include <x10/switch.h>
#include <x10/xassert.h>

namespace x10lib {

/* Switch initialization */
void
Switch::init(int val, x10_wait_type_t wt)
{
	extern lapi_handle_t __x10_hndl;
	extern int __x10_inited;

	if (!__x10_inited) return;

	cntrp = new lapi_cntr_t;
	assert(cntrp != NULL);
	(void)LAPI_Setcntr(__x10_hndl, cntrp, val);
	type = wt;
}

/* Switch constructor (1) */
Switch::Switch(int val)
{
	init(val, X10_BOTH_WAIT);
}

/* Switch constructor (2) */
Switch::Switch(int val, x10_wait_type_t wt)
{
	init(val, wt);
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

/* Get switch waiting type. */
x10_wait_type_t Switch::get_type(void)
{
	return (type);
}

/* Change switch waiting type. */
x10_wait_type_t Switch::change_type(x10_wait_type_t wt)
{
	x10_wait_type_t old_wt;

	old_wt = type;
	type = wt;
	return (old_wt);
}

/* Reset switch before waiting once again. */
void Switch::reset(void)
{
	extern lapi_handle_t __x10_hndl;

	(void)LAPI_Setcntr(__x10_hndl, cntrp, -1);
}

/* Perform next (wait) operation on switch. */
void Switch::next(void)
{
	extern lapi_handle_t __x10_hndl;

	(void)LAPI_Waitcntr(__x10_hndl, cntrp, 0, NULL);
}

/* Allocate switch object and return reference to it. */
x10_switch_t AllocSwitch(x10_wait_type_t wt)
{
	x10_switch_t sw = new Switch(-1, wt);
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
x10_switch_t x10_alloc_switch(x10_wait_type_t wt)
{
	return x10lib::AllocSwitch(wt);
}

/* Deallocate switch object. */
void x10_free_switch(x10_switch_t sw)
{
	return x10lib::FreeSwitch(sw);
}

/* Change switch waiting type. */
x10_wait_type_t x10_change_wait_type(x10_switch_t sw,
					x10_wait_type_t wt)
{
	return sw->change_type(wt);
}

/* Reset switch before waiting once again. */
void x10_reset_switch(x10_switch_t sw)
{
	return sw->reset();
}

/* Perform wait on switch. */
extern "C"
void x10_wait_on_switch(x10_switch_t sw)
{
	return sw->next();
}
