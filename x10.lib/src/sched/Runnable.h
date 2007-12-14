/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Runnable.h,v 1.2 2007-12-14 13:39:35 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_XWS_RUNNABLE_H
#define __X10_XWS_RUNNABLE_H

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib_xws {

class Runnable {
public:
	virtual void run()=0;
	virtual ~Runnable() {}
};

} /* closing brace for namespace x10lib_xws */
#endif

#endif  /* __X10_XWS_RUNNABLE_H */
