/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: switch.cc,v 1.5 2007-06-18 11:29:55 ganeshvb Exp $ */

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
