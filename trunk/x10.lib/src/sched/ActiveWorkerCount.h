/*
============================================================================
 Name        : ActiveWorkerCount.h
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/

#ifndef x10lib_ActiveWorkerCount_h
#define x10lib_ActiveWorkerCount_h

#include <cstdlib>

namespace x10lib_cws {

class Closure;


class ActiveWorkerCount {
	private:
		volatile int updater;
		void barrierAction(Closure *cl);
		Closure *closure;
	protected:
	public:
/* 		ActiveWorkerCount(); */
		ActiveWorkerCount(Closure *c=NULL);
		void checkIn();
		void checkOut();
		int getNumberCheckedOut();
};

}
#endif
