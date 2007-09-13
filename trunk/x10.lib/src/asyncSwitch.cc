/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: asyncSwitch.cc,v 1.4 2007-09-13 15:20:04 ganeshvb Exp $ */

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


