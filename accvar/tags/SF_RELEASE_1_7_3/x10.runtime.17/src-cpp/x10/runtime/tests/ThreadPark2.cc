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

class MyRunnable1 : public Runnable {
	void run() {
		Thread &th = Thread::currentThread();

		cout << "[" << th.getId() << "] parking...." << endl;
		Thread::park();
		cout << "[" << th.getId() << "] someone unparked me" << endl;
	}
};

class MyRunnable2 : public Runnable {
private:
	Thread *otherThread;
public:
	MyRunnable2(Thread &th) {
		otherThread = &th;
	}
	~MyRunnable2() {}
	void run() {
		Thread &th = Thread::currentThread();

		sleep(2);
		cout << "[" << th.getId() << "] unparking thread ("
					<< otherThread->getId() << ")...." << endl;
		Thread::unpark(*otherThread);
	}
};


int main(int argc, char *argv[])
{
	MyRunnable1 runner1;
	Thread thread1(runner1);
	MyRunnable2 runner2(thread1);
	Thread thread2(runner2);

	thread1.start();
	thread2.start();
	thread2.join();
	thread1.join();
	return 0;
}
