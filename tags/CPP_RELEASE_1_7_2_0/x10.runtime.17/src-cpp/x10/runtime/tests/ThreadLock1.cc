/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

#include <Thread.h>
#include <Lock.h>
#include <Runnable.h>
#include <iostream>
#include <string>

using namespace std;
using namespace xrx_runtime;

class Incrementer : public Runnable {
private:
	static int valcnt;
	const static int maxval;
	static int objcnt;
	int id;
	static Lock lock;
public:
	// constructor
	Incrementer() { objcnt++; id = objcnt; }

	// destructor
	~Incrementer() { }

	// run method
	void run() {
		while (valcnt < maxval) {
			lock.lock();
			if (valcnt < maxval) {
				valcnt++;
			}
			cout << "Thread " << id << " = " << valcnt << endl;
			lock.unlock();
			sleep(1);
		}
	}
};

// initialize static variables
int Incrementer::valcnt = 0;
const int Incrementer::maxval = 10;
int Incrementer::objcnt = 0;
Lock Incrementer::lock;

int main(int argc, char *argv[])
{
	Incrementer one, two;
	Thread first(one), second(two);

	first.start();
	second.start();
	first.join();
	second.join();
	return 0;
}
