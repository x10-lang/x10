/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: progress.h,v 1.1 2007-08-02 11:22:44 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Progress operations interface. **/

#ifndef __X10_PROGRESS_H
#define __X10_PROGRESS_H

/* x10 messaging state */
typedef enum {
	X10_NO_PROGRESS, /* looped so many times without progress */
	X10_SEND_COMPLETE, /* send operation completed */
	X10_RECV_COMPLETE, /* receive operation completed */
	X10_BOTH_COMPLETE, /* both send and receive ops completed */
	X10_SHM_COMPLETE, /* shared memory completion */
	X10_UNKNOWN_STATE, /* invalid (or) unknown state */
} x10_msg_state_t;

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

	/* Make progress in polling mode once.
	 * [Non-blocking operation]
	 */
	void Probe(void);

	/* Make progress in polling mode the specified number
	 * of times.
	 * [Blocking operation]
	 */
	void Poll(unsigned int cnt, x10_msg_state_t *state);

} /* closing brace for namespace x10lib */
#endif


/* C Lang Interface */
#ifdef __cplusplus
extern "C" {
#endif

/* Make progress in polling mode once. */
void x10_probe(void);

/* Make progress in polling mode the specified number of times. */
void x10_poll(unsigned int cnt, x10_msg_state_t *state);

#ifdef __cplusplus
} /* closing brace for extern "C" */
#endif

#endif /* __X10_PROGRESS_H */
