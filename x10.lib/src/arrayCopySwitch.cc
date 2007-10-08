/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 * Author : Ganesh Bikshandi
 */

/* $Id: arrayCopySwitch.cc,v 1.2 2007-10-08 05:19:35 ganeshvb Exp $ */

#include "types.h"
#include <iostream>

using namespace std;

extern "C"
void*
arrayCopySwitch (void* args)
{
   cout << "arrayCopySwitch should be overriddern \n";
   exit(-1);
}


