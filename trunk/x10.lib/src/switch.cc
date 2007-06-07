/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: switch.cc,v 1.3 2007-06-07 10:47:11 ganeshvb Exp $ */

#include "types.h"
#include <iostream>

using namespace std;

extern "C"
int 
asyncSwitch (async_handler_t h, void* args, size_t size)
{
   cout << "asyncSwitch should be overriddern \n";
   exit(-1);
}
