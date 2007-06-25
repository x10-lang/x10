/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: finish.h,v 1.7 2007-06-25 16:07:36 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_FINISH_H
#define __X10_FINISH_H

#include <x10/err.h>
#include <x10/types.h>

namespace x10lib {

// Exception class
class Exception {
public:
	Exception () {}
	virtual size_t size() = 0;
	virtual void print() = 0;
};

class MultiException {
public:
	MultiException (Exception **e, int n)
		: exceptions_(e), total_(n) {}
	void print() {
		for (int i = 0; i < total_; i++)
			exceptions_[i]->print();
	}
	int size() const { return total_; }
	Exception **const exceptions() const {return exceptions_;}

	~MultiException () {
		for (int i = 0; i < total_; i++)
			delete exceptions_[i];
		delete [] exceptions_;
	}

private:
	int total_;
	Exception **exceptions_;
};

x10_err_t finishEnd (Exception *a);
int finishStart(int cs);

} /* closing brace for namespace x10lib */

#endif /* __X10_FINISH_H */
