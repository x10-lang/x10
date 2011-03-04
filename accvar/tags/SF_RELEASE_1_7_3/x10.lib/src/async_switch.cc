/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: async_switch.cc,v 1.4 2007-12-10 12:12:04 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/types.h>
#include <iostream>
#include <stdlib.h>
#include "__x10lib.h__"

using namespace std;

extern "C"
void AsyncSwitch(x10_async_handler_t h, void *args, int niter)
{
	cout << "!!AsyncSwitch should be overridden!!\n";
	exit(-1);
}
