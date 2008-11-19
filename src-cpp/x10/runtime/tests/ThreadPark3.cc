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

int parktime = 5000000;
int waittime = 1000000;

class MyRunnable1 : public Runnable {
	void run() {
		Thread &th = Thread::currentThread();
		Long nanos = ::parktime;

		sleep(1);
		cout << "[" << th.getId() << "] parking for "
					<< nanos << " nano seconds...." << endl;
		Thread::parkNanos(nanos);
		if (::parktime <= ::waittime) {
			cout << "[" << th.getId() << "] unparked due to timeout" << endl;
		} else {
			cout << "[" << th.getId() << "] someone unparked me" << endl;
		}
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
		struct timespec tval;

		sleep(1);
		cout << "[" << th.getId() << "] waiting for "
					<< ::waittime << " nano seconds to unpark thread ("
					<<  otherThread->getId() << ")...." << endl;
		tval.tv_sec = 0;
		tval.tv_nsec = ::waittime;
		nanosleep(&tval, NULL);
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
