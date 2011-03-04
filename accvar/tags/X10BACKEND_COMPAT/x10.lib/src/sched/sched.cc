/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: sched.cc,v 1.1.1.1 2007-04-25 09:57:46 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

/** Sample Implementation file for X10Lib scheduler **/

#include <x10/x10lib.h>
#include <iostream>

using namespace std;

int
sched_sample()
{
	/* Code for sched_sample */
#ifdef DEBUG
	cout << "sched_sample()" << endl;
#endif
	return X10_OK;
}
