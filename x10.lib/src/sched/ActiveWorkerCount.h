/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: ActiveWorkerCount.h,v 1.6 2007-12-14 13:39:34 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/* Active worker count interface file. */

#ifndef __X10_XWS_ACTIVE_WORKER_COUNT_H
#define __X10_XWS_ACTIVE_WORKER_COUNT_H

#include <cstdlib>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib_xws {

class Closure;
class Runnable;

class ActiveWorkerCount {
	private:
		volatile int updater;
//		void barrierAction(Closure &*cl);
//		Closure &*closure;
		Runnable *barrierAction;

	protected:
	public:
/* 		ActiveWorkerCount(); */
		ActiveWorkerCount(Runnable *barrierAction=NULL);
 		~ActiveWorkerCount();

		virtual void checkIn();
		virtual void checkOut();
		virtual int getNumberCheckedOut();
};

} /* closing brace for namespace x10lib_xws */
#endif

#endif /* __X10_XWS_ACTIVE_WORKER_COUNT_H */
