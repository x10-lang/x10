/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: StealAbort.h,v 1.5 2007-12-26 07:57:34 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_XWS_STEAL_ABORT_H
#define __X10_XWS_STEAL_ABORT_H

#include <x10/xws/Exception.h>

/* C++ Lang Interface */
#ifdef __cpluscplus
namespace x10lib_xws {

	class StealAbort : public virtual Exception {
	};

} /* closing brace for namespace x10lib_xws */
#endif

#endif /* __X10_XWS_STEAL_ABORT_H */   
