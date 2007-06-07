/*
============================================================================
 Name        : ActiveWorkerCount.cpp
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/

#include "ActiveWorkerCount.h"
using namespace x10lib_cws;

	
ActiveWorkerCount::ActiveWorkerCount()
{
	updater = 0;
	barrierAction = NULL;
}
ActiveWorkerCount::ActiveWorkerCount(void (*b)(Closure *d), Closure *c)
{
	barrierAction = b;
	closure = c;
}
void ActiveWorkerCount::CheckIn() 
{
	atomic_add(&updater,-1);
	if (updater == 0) {
		(*b)(c);
	}
}
void ActiveWorkerCount::CheckOut() 
{
	atomic_add(&updater,1);
}
int ActiveWorkerCount::getNumberCheckedOut()
{
	return updater; 
}
	
