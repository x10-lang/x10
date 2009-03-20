/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

#include <Thread.h>
#include <iostream>
		
using namespace std;
using namespace xrx_runtime;

String quotes[] = {
	"Mares eat oats",
	"Does eat oats",
	"Little lambs eat ivy",
	"A kid will eat ivy too",
};


int main(int argc, char *argv[])
{
	int nquotes = sizeof(quotes)/sizeof(String);
	for (int i = 0; i < nquotes; i++) {
		Thread::sleep(4000);
		cout << quotes[i] << endl;
	}
	return 0;
}
