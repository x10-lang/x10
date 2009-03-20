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

class MyRunnable : public Runnable {
	void run() { cout << "Doing Sample Run..." << endl; }
};

int main(int argc, char *argv[])
{
	Thread thread1;
	MyRunnable mytask;
	Thread thread2(mytask);

	cout << "===== Thread's Default Constructor =====" << endl;
	cout << "[thread1] name: " << thread1.getName()
				<< " id: " << thread1.getId() << endl;
	cout << "===== Thread's Runnable Constructor =====" << endl;
	cout << "[thread2] name: " << thread2.getName()
				<< " id: " << thread2.getId() << endl;
	return 0;
}
