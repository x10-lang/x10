/*
============================================================================
 Name        : Lock.cpp
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/

#include "Lock.h"
#include "Sys.h"

using namespace x10lib_cws;

/*-------------SpinLock----------------------*/

SpinLock::SpinLock() {  spin_lock_var = 0; }
SpinLock::~SpinLock() {}


inline void SpinLock::spin_lock_init()
{
	spin_lock_var = 0; 
}
inline void SpinLock::spin_lock_wait()
{
	if (atomic_exchange(&(spin_lock_var),1) == 0) return;
	WRITE_BARRIER();
	do {
		  while (spin_lock_var != 0);
	} while (atomic_exchange(&(spin_lock_var),1) != 0);
	READ_BARRIER();
}

inline int SpinLock::spin_lock_try()
{
	WRITE_BARRIER();
	if (atomic_exchange(&(spin_lock_var),1) == 0) 
	{
		READ_BARRIER();
		return 1;
	}
	return 0;
}
inline void SpinLock::spin_lock_signal()
{
	WRITE_BARRIER();
	spin_lock_var = 0;
}

/*--------------PosixLock--------------------------*/

PosixLock::PosixLock() {  pthread_mutex_init(&posix_lock_var, NULL);}
PosixLock::~PosixLock() { pthread_mutex_destroy(&posix_lock_var);}

void PosixLock::lock_init_posix()
{
	pthread_mutex_init(&posix_lock_var, NULL); 
}

void PosixLock::lock_wait_posix()
{
	pthread_mutex_lock(&posix_lock_var);
}

void PosixLock::lock_signal_posix()
{
  pthread_mutex_unlock(&posix_lock_var);
}

int PosixLock::lock_try_posix()
{
  return (pthread_mutex_trylock(&posix_lock_var) == 0) ? 1 : 0; 
}


