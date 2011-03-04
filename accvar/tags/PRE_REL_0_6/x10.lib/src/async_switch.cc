/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 */

/* $Id: async_switch.cc,v 1.1 2007-11-13 05:32:39 ganeshvb Exp $ */

#include "types.h"
#include <iostream>

using namespace std;

extern "C"
void 
asyncSwitch (x10_async_handler_t h, void* args,int niter)
{
   cout << "asyncSwitch should be overriddern \n";
   exit(-1);
}


