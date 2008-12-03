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
		Thread &cth = Thread::currentThread();

		cout << "calling thread name: " << cth.getName()
				<< " id: " << cth.getId() << endl;
	}
};

int main(int argc, char *argv[])
{
	MyRunnable runner1, runner2;
	Thread thread1(runner1), thread2(runner2);

	cout << "[thread1] name: " << thread1.getName()
				<< " id: " << thread1.getId() << endl;
	thread1.start();
	thread1.join();
	cout << "[thread2] name: " << thread2.getName()
				<< " id: " << thread2.getId() << endl;
	thread2.start();
	thread2.join();
	return 0;
}
