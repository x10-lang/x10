/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: closure.h,v 1.4 2008-06-02 16:07:34 ipeshansky Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_CLOSURE_H
#define __X10_CLOSURE_H

#include <x10/types.h>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

	/* A type representing the closure for asyncs.
	 * handler --> unique handle for the array
	 * Every closure type argument should have this as its base class.
	 */
	struct Closure
	{
		Closure () : len(0), handler(0) {}
		Closure (int _len, x10_async_handler_t _handler) :
			len(_len), handler(_handler) {}

		size_t len;
		x10_async_handler_t handler;
	};

} /* closing brace for namespace x10lib */

typedef x10lib::Closure* x10_closure_t;

#else /* C Language Interface */

struct Closure;
typedef struct Closure* x10_closure_t;

#endif /* __cplusplus */

#endif /* __X10_CLOSURE_H */
