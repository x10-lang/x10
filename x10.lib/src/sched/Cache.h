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
		vector<Frame *> stack; /* using vector as resizing would be lot easier */
		
		void incrementExceptionPointer ();
		void decrementExceptionPointer ();
	
	public:
		Cache (Worker *w);
		~Cache (); 
				
		Frame *childFrame () const;
		Frame *topFrame () const;
		bool headAheadOfTail () const;
		bool headGeqTail () const;
		bool exceptionOutstanding () const;
		void incHead ();
		int gethead () const;
		int gettail () const;
		int getexception() const;
		void resetExceptionPointer (Worker *w);
		void signalImmediateException ();
		bool empty () const;
		bool atTopOfStack () const;
		void pushFrame (Frame *);
		Frame *currentFrame() const ;
		void reset ();
		void popFrame ();
		bool interrupted() const;
		void pushIntUpdatingInPlace(Pool *pool, int tid, int x);

		bool dekker(Worker *thief);
	
};
}
#endif
