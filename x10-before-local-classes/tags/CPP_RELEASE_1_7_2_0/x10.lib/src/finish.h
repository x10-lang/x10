/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: finish.h,v 1.13 2008-01-19 18:20:06 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#ifndef __X10_FINISH_H
#define __X10_FINISH_H

#include <x10/types.h>

/* exception buffer size */
#define X10_EX_BUFFER_SIZE 1024

/* C++ Lang Interface */
#ifdef __cplusplus
namespace x10lib {

class Exception {
	public:
		Exception() {}

		virtual size_t size() = 0;
		virtual void print() = 0;
		virtual x10_place_t proc() = 0;
};

class MultiException {
	private:
		Exception **exceptions_;
		int total_;

	public:
		MultiException(Exception **e, int n) :
			exceptions_(e), total_(n) {}

		~MultiException() {
			/* x10lib takes care of garbage collection/managing
			 * each of the exceptions_ objections;
			 * so, just delete the exceptions_ array only.
			 */
			// delete [] exceptions_;
		}

		void print() {
			for (int i = 0; i < total_; i++)
				exceptions_[i]->print();
		}

		int size() const {return total_;}

		Exception* operator [] (int i) {
			return exceptions_[i];
		}

		Exception** const exceptions() const {
			return exceptions_;
		}
};

void FinishEnd(Exception *a);
int FinishStart(int cs);

} /* closing brace for namespace x10lib */
#endif

#endif /* __X10_FINISH_H */
