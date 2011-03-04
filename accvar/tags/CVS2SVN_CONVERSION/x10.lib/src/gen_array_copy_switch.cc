/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: gen_array_copy_switch.cc,v 1.3 2008-01-09 05:36:43 ganeshvb Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/types.h>
#include <iostream>
#include <lapi.h>

using namespace std;

extern "C"
lapi_vec_t* GenArrayCopySwitch (int handler, void * args)
{
   cout << "genArrayCopySwitch should be overriddern \n";
   exit(-1);
}
