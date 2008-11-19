/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

#include <Thread.h>
#include <iostream>
#include <string>

using namespace std;
using namespace xrx_runtime;

int parktime = 1000000;

class MyRunnable : public Runnable {
	void run() {
		Thread &th = Thread::currentThread();
		Long nanos = ::parktime;

		sleep(1);
		cout << "[" << th.getId() << "] parking for "
					<< nanos << " nano seconds...." << endl;
		Thread::parkNanos(nanos);
		cout << "[" << th.getId() << "] unparked due to timeout" << endl;
	}
};

int main(int argc, char *argv[])
{
	MyRunnable runner;
	Thread thread(runner);

	thread.start();
	thread.join();
	return 0;
}
