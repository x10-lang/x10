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

class HelloThread : public Thread {
	void run() { cout << "Hello, X10 World!" << endl; }
};

int main(int argc, char *argv[])
{
	HelloThread helloThread;

	helloThread.start();
	return 0;
}
