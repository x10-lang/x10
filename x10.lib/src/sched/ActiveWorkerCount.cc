/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: ActiveWorkerCount.cc,v 1.7 2007-12-14 13:39:34 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/* An active worker count keeps track of the number of workers
 * that are active.
 * An action is triggered when the counter transitions to zero.
 */
 
#include "ActiveWorkerCount.h"
#include "Closure.h"
#include "Sys.h"
#include "Runnable.h"
#include <assert.h>

using namespace std;
using namespace x10lib_xws;

	
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
