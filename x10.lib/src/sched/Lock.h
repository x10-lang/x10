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
	
SpinLock {
	private:
		volatile int spin_lock_var = 0; 
		pthread_mutex_t posix_lock_var;
	public:
		SpinLock ();
		~SpinLock ();
		inline void spin_lock_init();
		inline void spin_lock_wait();
		inline int spin_lock_try();
		inline void spin_lock_signal();
		
};

PosixLock {
public:
	pthread_mutex_t posix_lock_var;
	inline void lock_init_posix();
	inline void lock_try_posix();
	inline void lock_wait_posix();
	inline void lock_signal_posix();
};
}
#endif
