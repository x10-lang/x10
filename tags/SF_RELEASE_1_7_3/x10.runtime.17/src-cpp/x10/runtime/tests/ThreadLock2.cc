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

#define PrHCnt(id, op) \
do { \
	cout << "Thread " << id << " hold count [after " #op "]: " \
			<< lock.getHoldCount() << endl; \
} while(0)

class MyRunnable : public Runnable {
private:
	static Lock lock;
	static int objcnt;
	int objid;
public:
	MyRunnable() {objcnt++; objid = objcnt;}
	~MyRunnable() {}
	void run() {
			lock.lock();
			PrHCnt(objid, 1st lock);
			sleep(1);
			lock.lock();
			PrHCnt(objid, 2nd lock);
			sleep(1);
			lock.unlock();
			PrHCnt(objid, 1st unlock);
			sleep(1);
			lock.unlock();
			PrHCnt(objid, 2nd unlock);
	}
};

// initialize static variables
Lock MyRunnable::lock;
int MyRunnable::objcnt = 0;

int main(int argc, char *argv[])
{
	MyRunnable runner1, runner2, runner3;

	Thread thread1(runner1), thread2(runner2), thread3(runner3);

	thread2.start();
	thread1.start();
	thread3.start();
	thread1.join();
	thread3.join();
	thread2.join();
	return 0;
}
