/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 */

/* $Id: arrayCopySwitch.cc,v 1.3 2007-10-19 16:04:28 ganeshvb Exp $ */

#include "types.h"
#include <iostream>

using namespace std;

extern "C"
void*
arrayCopySwitch (int handler, void* args)
{
   cout << "arrayCopySwitch should be overriddern \n";
   exit(-1);
}


