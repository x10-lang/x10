/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: switch.h,v 1.3 2007-06-25 16:07:36 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's Switch interface. **/

#ifndef __X10_SWITCH_H
#define __X10_SWITCH_H

#include <lapi.h>

/* x10 waiting categories */
typedef enum {
	X10_BOTH_WAIT = 0, /* Wait on both send and receive completion */
	X10_SEND_WAIT, /* Wait on send completion */
	X10_RECV_WAIT, /* Wait on receive completion */
} x10_wait_type_t;

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

class Switch {
public:
	Switch(int val = -1);
	Switch(int val, x10_wait_type_t wt);
	~Switch();
	void init(int val, x10_wait_type_t wt);
	lapi_cntr_t *get_handle(void);
	x10_wait_type_t get_type(void);
	x10_wait_type_t change_type(x10_wait_type_t);
	void reset(void);
	void next(void);
private:
	x10_wait_type_t type;
	lapi_cntr_t *cntrp;
};

} /* closing brace for namespace x10lib */

typedef x10lib::Switch *x10_switch_t;

namespace x10lib {

/* Allocate switch object and return reference to it. */
x10_switch_t AllocSwitch(x10_wait_type_t wt);

/* Deallocate switch object. */
void FreeSwitch(x10_switch_t sw);

} /* closing brace for namespace x10lib */
#endif

/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

/* Allocate switch object and return reference to it. */
x10_switch_t x10_alloc_switch(x10_wait_type_t wt);

/* Deallocate switch object. */
void x10_free_switch(x10_switch_t sw);

/* Change switch waiting type. */
x10_wait_type_t x10_change_wait_type(x10_switch_t sw,
					x10_wait_type_t wt);

/* Reset switch before waiting once again. */
void x10_reset_switch(x10_switch_t sw);

/* Perform wait on switch. */
void x10_wait_on_switch(x10_switch_t sw);

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

#endif /* __X10_SWITCH_H */
