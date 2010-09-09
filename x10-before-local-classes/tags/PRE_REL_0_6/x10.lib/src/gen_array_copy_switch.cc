/*
 * (c) Copyright IBM Corporation 2007
 *
 * This file is part of X10 Runtime System.
 */

/* $Id: gen_array_copy_switch.cc,v 1.1 2007-11-13 05:32:39 ganeshvb Exp $ */

#include "types.h"
#include <iostream>
#include "lapi.h"

using namespace std;

extern "C"
lapi_vec_t* genArrayCopySwitch (int handler, void * args)
{
   cout << "genArrayCopySwitch should be overriddern \n";
   exit(-1);
}


