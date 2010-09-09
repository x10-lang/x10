/*
 * (c) Copyright IBM Corporation 2008
 *
 * $Id$
 * This file is part of XRX/C++ native layer implementation.
 */

#include <Thread.h>
#include <Runnable.h>
#include <iostream>
		
using namespace std;
using namespace xrx_runtime;

class MyRunnable : public Runnable {
public:
	static String quotes[];
	static int nquotes;
	void run() {
		try {
			for (int i = 0; i < nquotes; i++) {
				Thread::sleep(4000);
				cout << "[" << i << "]: " << quotes[i] << endl;
			}
		} catch (InterruptedException &ie) {
			cout << "I wasn't done!" << endl;
		}
	}
};

String MyRunnable::quotes[] = {
	"Mares eat oats",
	"Does eat oats",
	"Little lambs eat ivy",
	"A kid will eat ivy too"
};
int MyRunnable::nquotes = 4;

int main(int argc, char *argv[])
{
	long patience = 1000 * 10;
	MyRunnable runner;
	Thread th(runner);
	long curTime, startTime;

	cout << "Starting MessageLoop thread" << endl;
	time(&startTime);
	startTime *= 1000;
	th.start();
	cout << "Waiting for MessageLoop thread to finish" << endl;
	while (th.isAlive()) {
		cout << "Still waiting..." << endl;
		sleep(1);
		time(&curTime);
		curTime *= 1000;
		if (((curTime - startTime) > patience) && th.isAlive()) {
			cout << "Tired of waiting!" << endl;
			th.interrupt();
			th.join();
		}
	}
	cout << "Finally!" << endl;
	return 0;
}
