/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: clock.h,v 1.2 2007-12-09 12:20:09 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** X10Lib's (DUMMY) clock interface. **/

#ifndef __X10_CLOCK_H
#define __X10_CLOCK_H

#include <x10/types.h>
#include <x10/err.h>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {
	class Clock {
		/* ... */
	};

	x10_err_t ClockNext(Clock *c, int n);
} /* closing brace for namespace x10lib */
#endif 

#endif /* __X10_CLOCK_H */
