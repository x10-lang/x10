/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: switch.cc,v 1.4 2007-06-16 16:20:36 ganeshvb Exp $ */

#include "types.h"
#include <iostream>

using namespace std;

extern "C"
int 
asyncSwitch (async_handler_t h, void* args, size_t size, int niter)
{
   cout << "asyncSwitch should be overriddern \n";
   exit(-1);
}
