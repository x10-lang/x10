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

int main(int argc, char *argv[])
{
	Thread thread1, thread2;

	cout << "===== Thread's Default Constructor =====" << endl;
	cout << "[thread1] name: " << thread1.getName()
				<< " id: " << thread1.getId() << endl;
	cout << "[thread2] name: " << thread2.getName()
				<< " id: " << thread2.getId() << endl;
	return 0;
}
