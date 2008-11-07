/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

#include <Thread.h>
#include <Runnable.h>
#include <iostream>
#include <string>

using namespace std;
using namespace xrx_runtime;

class MyRunnable1 : public Runnable {
	void run() { cout << "XRX -- X10 Runtime In X10" << endl; }
};

class MyRunnable2 : public Runnable {
	void run() { cout << "Now Uses C++ Native Layer" << endl; }
};

int main(int argc, char *argv[])
{
	MyRunnable1 mytask1;
	MyRunnable2 mytask2;

	Thread mythread1(mytask1);
	Thread mythread2(mytask2);

	cout << "===== Thread's Runnable Constructor(s) =====" << endl;
	cout << "thread name: " << mythread2.getName() << " thread id: "
			<< mythread2.getId() << endl;
	mythread2.start();
	cout << "thread name: " << mythread1.getName() << " thread id: "
			<< mythread1.getId() << endl;
	mythread1.start();
	return 0;
}
