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
#include <pthread.h>

namespace x10lib_cws {
	
const int MAXIMUM_CAPACITY = 1 << 30;
const int INITIAL_CAPACITY = 1 << 13;
const int EXCEPTION_INFINITY = MAX_INT;

	
class Cache {	
	private:
		volatile int head, tail, exception; // need barrier for these
		void growAndPushFrame (Frame *);
		
	protected:
		unsigned int lastException; 
		Worker *owner;
		vector<Frame *> stack; /* using vector as resizing would be lot easier */
		
		void pushFrame (Frame *);
		void pushIntUpdatingInPlace(int x);
		void resetExceptionPointer ();
		void incrementExceptionPointer ();
		void decrementExceptionPointer ();
		void signalImmediateException ();
		bool atTopOfStack () const;
		Frame *childFrame () const;
		Frame *topFrame () const;
		void popFrame ();
		bool empty () const;
		void reset ();
		bool interrupted() const;
	
	public:
		Cache (Worker *w);
		~Cache ();
		
		bool headAheadOfTail () const;
		bool headGeqTail () const;
		bool exceptionOutstanding () const;
		void incHead ();
		int head () const;
		int tail () const;
		int exception() const;

		bool dekker(Worker *thief);
	
};
}
#endif
