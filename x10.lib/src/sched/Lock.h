/*
============================================================================
 Name        : Lock.h
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
#ifndef x10lib_Lock_h
#define x10lib_Lock_h
#include <pthread.h>

namespace x10lib_cws {
	
class SpinLock {
	private:
		volatile int spin_lock_var;
/* 		pthread_mutex_t posix_lock_var; */
		~SpinLock (); //cannot inherit. for now
	public:
		SpinLock ();
		void spin_lock_init();
		void spin_lock_wait();
		int spin_lock_try();
		void spin_lock_signal();
		
};

class PosixLock {
private:
	~PosixLock(); //cannot inherit.

public:
	PosixLock();
	pthread_mutex_t posix_lock_var;
	void lock_init_posix();
	int lock_try_posix();
	void lock_wait_posix();
	void lock_signal_posix();
};
}
#endif
