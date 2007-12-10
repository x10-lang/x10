/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: async_switch.cc,v 1.3 2007-12-10 09:29:57 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/types.h>
#include <iostream>
#include <stdlib.h>
#include "x10libP.h"

using namespace std;

extern "C"
void AsyncSwitch(x10_async_handler_t h, void *args, int niter)
{
	cout << "!!AsyncSwitch should be overridden!!\n";
	exit(-1);
}
