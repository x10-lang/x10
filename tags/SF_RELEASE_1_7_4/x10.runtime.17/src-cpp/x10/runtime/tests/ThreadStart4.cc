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

class HelloRunnable : public Runnable {
	void run() { cout << "Hello, X10 World!" << endl; }
};

int main(int argc, char *argv[])
{
	HelloRunnable helloTask;
	Thread helloThread(helloTask);

	helloThread.start();
	try {
		helloThread.start();
	} catch(IllegalThreadStateException& e) {
		cerr << "This thread was already started!!" << endl;
	}
	helloThread.join();
	return 0;
}
