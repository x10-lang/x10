/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: arrayCopySwitch.cc,v 1.1 2007-09-25 06:57:15 ganeshvb Exp $ */

#include "types.h"
#include <iostream>

using namespace std;

extern "C"
void*
arrayCopySwitch (x10_async_handler_t h, void* args)
{
   cout << "arrayCopySwitch should be overriddern \n";
   exit(-1);
}


