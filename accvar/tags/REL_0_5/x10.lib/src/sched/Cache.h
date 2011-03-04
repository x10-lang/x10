/*
============================================================================
 Name        : Cache.h
 Author      : Rajkishore Barik
 Version     :
 Copyright   : IBM Corporation 2007
 Description : Exe source file
============================================================================
*/
#ifndef x10lib_Cache_h
#define x10lib_Cache_h
#include <limits.h>
#include <vector>

using namespace std;

namespace x10lib_cws {

class Worker;
class Frame;
class Pool;
	
const int MAXIMUM_CAPACITY = 1 << 30;
const int INITIAL_CAPACITY = 1 << 13;
const int EXCEPTION_INFINITY = INT_MAX;

	
class Cache {	
	private:
		volatile int head, tail, exception; 
		void growAndPushFrame (Frame *);

	protected:
/* 		unsigned int lastException;  */
		Worker *owner;
		vector<Frame *> *stack; /* using vector as resizing would be lot easier */
		
		void incrementExceptionPointer ();
		void decrementExceptionPointer ();
	
	public:
		Cache (Worker *w);
		~Cache (); 
				
		Frame *childFrame () ;
		Frame *topFrame () ;
		bool headAheadOfTail () ;
		bool headGeqTail () ;
		bool exceptionOutstanding () ;
		void incHead ();
		int gethead () ;
		int gettail () ;
		int getexception() ;
		void resetExceptionPointer (Worker *w);
		void signalImmediateException ();
		bool empty () ;
		bool atTopOfStack () ;
		void pushFrame (Frame *);
		Frame *currentFrame()  ;
		void reset ();
		void popFrame ();
		bool interrupted() ;
		void pushIntUpdatingInPlace(Pool *pool, int tid, int x);

		bool dekker(Worker *thief);
	
};
}
#endif
