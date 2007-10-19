/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 */

/* $Id: asyncSwitch.cc,v 1.5 2007-10-19 16:04:29 ganeshvb Exp $ */

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


