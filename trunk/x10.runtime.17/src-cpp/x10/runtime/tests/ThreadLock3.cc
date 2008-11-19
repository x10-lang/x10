/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

#include <Lock.h>
#include <iostream>
#include <cstdlib>

#define PrHCnt(i, op) \
do { \
	cout << "hold count [" << i << " " #op "(s)]: " \
				<< lock.getHoldCount() << endl; \
} while(0)

using namespace std;
using namespace xrx_runtime;

int main(int argc, char *argv[])
{
	Lock lock;
	int nlocks = 5;


	/*
	if (argc != 2) {
		cerr << "Usage: " << argv[0] << " nlocks" << endl;
		exit(1);
	}
	*/
	if (argc == 2) {
		nlocks = std::atoi(argv[1]);
		if (nlocks <= 0) {
			cerr << argv[0] << ": number of locks should be positive!!" << endl;
			exit(1);
		}
	}
	int i = 0;

	PrHCnt(i, lock);
	/* apply locks recursively */
	for (i = 1; i <= nlocks; i++) {
		lock.lock();
		PrHCnt(i, lock);
		sleep(1);
	}
	/* release applied locks */
	for (i = 1; i <= nlocks; i++) {
		lock.unlock();
		PrHCnt(i, unlock);
		sleep(1);
	}
	return 0;
}
