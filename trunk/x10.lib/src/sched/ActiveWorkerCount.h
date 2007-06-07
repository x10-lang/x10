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

namespace x10lib_cws {
	
class ActiveWorkerCount {
	private:
		volatile int updater;
		void (ActiveWorkerCount::*barrierAction)(Closure *cl);
		Closure *closure;
	protected:
	public:
		ActiveWorkerCount();
		ActiveWorkerCount(void (*b)(Closure *d), Closure *c);
		void CheckIn();
		void CheckOut();
		int getNumberCheckedOut();
		
};
}
#endif
