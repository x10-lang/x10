/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: progress.cc,v 1.2 2007-12-10 12:12:05 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Implementation file for Progress operations interface. **/

#include <x10/progress.h>
#include <lapi.h>
#include "__x10lib.h__"

namespace x10lib {

/* Make progress in polling mode once. */
void Probe(void)
{
	if (!__x10_inited) return;

	(void)LAPI_Probe(__x10_hndl);
}

/* Make progress in polling mode 'cnt' times. */
void Poll(unsigned int cnt, x10_msg_state_t *state)
{
	lapi_msg_info_t msg_info;
	x10_msg_state_t __state;

	if (!__x10_inited) {
		if (state)
			*state = X10_UNKNOWN_STATE;
		return;
	}

	(void)LAPI_Msgpoll(__x10_hndl, cnt, &msg_info);
	/* map LAPI status to that of X10Lib */
	if (msg_info.status & LAPI_DISP_CNTR) {
		__state = X10_NO_PROGRESS;
	} else if (msg_info.status & LAPI_SEND_COMPLETE) {
		__state = X10_SEND_COMPLETE;
	} else if (msg_info.status & LAPI_RECV_COMPLETE) {
		__state = X10_RECV_COMPLETE;
	} else if (msg_info.status & LAPI_BOTH_COMPLETE) {
		__state = X10_BOTH_COMPLETE;
	} else if (msg_info.status & LAPI_POLLING_NET) {
		__state = X10_SHM_COMPLETE;
	} else {
		__state = X10_UNKNOWN_STATE;
	}

	if (state)
		*state = __state;
	return;
}

} /* closing brace for namespace x10lib */


/* Make progress in polling mode once. */
extern "C"
void x10_probe(void)
{
	return x10lib::Probe();
}

/* Make progress in polling mode the specified number of times. */
extern "C"
void x10_poll(unsigned int cnt, x10_msg_state_t *status)
{
	return x10lib::Poll(cnt, status);
}
