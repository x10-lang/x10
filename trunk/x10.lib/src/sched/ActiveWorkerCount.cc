/*
============================================================================
 Name        : ActiveWorkerCount.cc
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/

#include "ActiveWorkerCount.h"
#include "Sys.h"

using namespace x10lib_cws;

	
ActiveWorkerCount::ActiveWorkerCount()
{
	updater = 0;
}
ActiveWorkerCount::ActiveWorkerCount(Closure *c)
{
 	updater = 0;
	closure = c;
}
void ActiveWorkerCount::checkIn() 
{
	atomic_add(&updater,-1);
	if (updater == 0) {
		barrierAction(closure);
	}
}
void ActiveWorkerCount::checkOut() 
{
	atomic_add(&updater,1);
}
int ActiveWorkerCount::getNumberCheckedOut()
{
	return updater; 
}
void ActiveWorkerCount::barrierAction(Closure *c)
{
	if (c != NULL && c->requiresGlobalQuiescence()) 
			c->completed();
	c = NULL;
	MEM_BARRIER(); // let everyone see it -- TODO RAJ
}
	
