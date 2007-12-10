/*
 * (c) Copyright IBM Corporation 2007
 *
 * $Id: gen_array_copy_switch.cc,v 1.2 2007-12-10 13:15:45 srkodali Exp $
 * This file is part of X10 Runtime System.
 */

#include <x10/types.h>
#include <iostream>
#include <lapi.h>

using namespace std;

extern "C"
lapi_vec_t* genArrayCopySwitch (int handler, void * args)
{
   cout << "genArrayCopySwitch should be overriddern \n";
   exit(-1);
}
