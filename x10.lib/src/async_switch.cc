/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: async_switch.cc,v 1.2 2007-12-09 11:16:45 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/types.h>
#include <iostream>

extern "C"
void AsyncSwitch(x10_async_handler_t h, void *args, int niter)
{
	cout << "!!AsyncSwitch should be overridden!!\n";
	exit(-1);
}
