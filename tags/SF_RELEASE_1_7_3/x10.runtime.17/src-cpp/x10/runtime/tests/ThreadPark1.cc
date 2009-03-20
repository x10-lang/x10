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

class MyRunnable : public Runnable {
	void run() {
		Thread &th = Thread::currentThread();

		cout << "[" << th.getId() << "] parking...." << endl;
		Thread::park();
		cout << "[" << th.getId() << "] someone unparked me" << endl;
	}
};

int main(int argc, char *argv[])
{
	MyRunnable runner;
	Thread thread(runner);

	thread.start();
	sleep(2);
	cout << __func__ << ": unparking thread ("
				<< thread.getId() << ")..." << endl;
	Thread::unpark(thread);
	thread.join();
	return 0;
}
