/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: asyncSwitch.cc,v 1.1 2007-06-19 17:14:38 ganeshvb Exp $ */

#include "types.h"
#include <iostream>

using namespace std;

extern "C"
int 
asyncSwitch (async_handler_t h, void* args,int niter)
{
   cout << "asyncSwitch should be overriddern \n";
   exit(-1);
}
