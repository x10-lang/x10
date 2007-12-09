/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 */

/* $Id: array_copy_switch.cc,v 1.1 2007-12-09 10:31:19 srkodali Exp $ */

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


