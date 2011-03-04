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
#include "Closure.h"
#include "Sys.h"
#include "Runnable.h"
#include <assert.h>

using namespace std;
using namespace x10lib_cws;

	
// ActiveWorkerCount::ActiveWorkerCount()
// {
// 	updater = 0;
// }
/*We assume we are being passed a new object that we control. 
 * So we delete b in the desctructor.*/
ActiveWorkerCount::ActiveWorkerCount(Runnable *b)
 : barrierAction(b) {
 	updater = 0;
}
ActiveWorkerCount::~ActiveWorkerCount() {
	delete barrierAction;	
}

void ActiveWorkerCount::checkIn() 
{
	atomic_add(&updater,-1);

	if (updater == 0) {
		if(barrierAction)
			barrierAction->run();
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

/*
void ActiveWorkerCount::barrierAction(Closure &*c)
{
	if (c != NULL && c->requiresGlobalQuiescence()) 
		c->completed();
	c = NULL;
	MEM_BARRIER(); // let everyone see it -- TODO RAJ
}
*/	
