/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: Lock.h,v 1.6 2007-12-14 13:39:35 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_XWS_LOCK_H
#define __X10_XWS_LOCK_H

#include <pthread.h>

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib_xws {
	
class SpinLock {
	private:
		volatile int spin_lock_var;
/* 		pthread_mutex_t posix_lock_var; */

	public:
		SpinLock ();
		~SpinLock ();
		void spin_lock_init();
		void spin_lock_wait();
		int spin_lock_try();
		void spin_lock_signal();
		
};

class PosixLock {
private:

public:
	PosixLock();
	~PosixLock(); 
	pthread_mutex_t posix_lock_var;
	void lock_init_posix();
	int lock_try_posix();
	void lock_wait_posix();
	void lock_signal_posix();
};

} /* closing brace for namespace x10lib_xws */
#endif

#endif /* __X10_XWS_LOCK_H */
